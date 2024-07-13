package io.ocepgen.modules.exam.autogenpaper;

import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能组卷服务类
 * @author Roxy
 */
@Slf4j
public class GenExamPaperService {
    private GenExamPaperConfig config;
    private GenExamPaperRuleVo rule;
    // 题库中题型匹配的题目
    private List<QbQuestionDTO> questions;
    // 按题目类型 对 题目进行分组
    Map<Integer, List<QbQuestionDTO>> questionGroup;
    // 每个题型 实际 分配的期望题数
    Map<QuestionTypeEnum, Integer> realAllocateQuestionCountMap;

    public GenExamPaperService(GenExamPaperRuleVo rule, List<QbQuestionDTO> questions) {
        this.rule = rule;
        this.questions = questions;
        this.config = GenExamPaperConfig.getConfig(rule.getExpectedQuestionCount(), questions.size());
    }

    public GenExamPaperService(GenExamPaperRuleVo rule, ExamGenerateParamsDTO config, List<QbQuestionDTO> questions) {
        this.rule = rule;
        this.questions = questions;
        this.config = GenExamPaperConfig.customConfig(config);
    }

    // 返回最优试卷个体
    public ExamPaper generate() {
        // 按题目类型 对 题目进行分组
        questionGroup = questions.stream()
                .collect(Collectors.groupingBy(QbQuestionDTO::getType));
        // 初始化种群
        Population population = new Population(config.getPopulationSize(), questions, rule);
        // 保存 初始种群中 分配的题型数量分布
        realAllocateQuestionCountMap = population.getRealAllocateQuestionCountMap();
        // 迭代次数
        int iteration = 0;
        while (iteration++ < config.getMaxIteratorNum()
                && population.getBestPaper().getFitness() >= config.getExpectedFitness()) { //若还未达到最大迭代次数，或最优适应度仍大于期望适应度
            population = evolve(population);
            log.warn("当前迭代次数: {} , 当前最优适应度: {}", iteration, population.getBestPaper().getFitness());
        }
        // 在达到最大迭代次数或达到期望适应度后 返回最优个体
        return population.getBestPaper();
    }

    // 种群进化
    private Population evolve(Population population) {
        //int elitismId = 0;
        // 新的种群
        Population newPopulation = new Population(config.getPopulationSize());
        if (config.isElitism()) { //如果开启了精英主义，每次种群进化时留下最优个体
            ExamPaper paper = population.getBestPaper();
            //elitismId = paper.getId(); //保留elitismId，用于之后防止重复个体进入子种群
            paper.setId(1); //设置id
            newPopulation.addPaper(paper);
            log.warn("当前精英适应度: {}", population.getBestPaper().getFitness());
        }
        // 进行交叉
        for (int index = newPopulation.getCurrentLastPaperIndex(); index < config.getPopulationSize(); index++) {

            // 选择双亲
            ExamPaper father = selectParent(population);
            ExamPaper mother = selectParent(population);
            ExamPaper child = new ExamPaper(); //要new 一个，不然是引用传递，会影响后续的id
            while(father == mother) { //确保双亲不同
                mother = selectParent(population);
            }
            if(Math.random() < config.getCrossoverRate()){ //如果满足交叉概率
                // 交叉
                child = crossover(father, mother);
            } else {// 不交叉，直接复制更优秀的双亲之一
                // 从双亲中选择适应度更优秀的个体
                if(father.getFitness() < mother.getFitness()){
                    child.setQuestions(father.getQuestions());
                } else {
                    child.setQuestions(mother.getQuestions());
                }
            }

            // 设置子代个体id
            child.setId(index+1);
            // 加入新的种群
            newPopulation.addPaper(child);
        }

        // 变异操作，精英不参与变异
        for (int index = config.isElitism() ? 1 : 0; index < config.getPopulationSize(); index++) {
            ExamPaper paper = newPopulation.getPaper(index);
            mutate(paper);

            // 计算适应度
            paper.setFitness(rule, realAllocateQuestionCountMap);
            // 将变异后的个体添加进种群，不需要此操作，因为是引用传递，在种群中的个体已经被修改
            //newPopulation.setPaper(index, paper);
        }
        // 返回进化后的种群
        return newPopulation;
    }


    // 锦标赛选择法，从当前种群中随机选取几个个体，挑选最优的个体 。 其他算法：如果是轮盘赌算法：染色体i被选择的概率 = 染色体i的适应度 / 所有染色体的适应度之和
    private ExamPaper selectParent(Population population) {
        return population.getPopulation(config.getTournamentNum()).getBestPaper();
    }

    // 交叉操作
    private ExamPaper crossover(ExamPaper father, ExamPaper mother) {
        ExamPaper paper = new ExamPaper();
        // 随机计算父亲中留下的基因（题目）数量
        int remainNumber = (int) (Math.random() * father.getQuestionSize());
        for (int i = 0; i < remainNumber; i++) {
            paper.addQuestion(father.getQuestion(i));
        }
        // 交叉
        for (int i = remainNumber; i < mother.getQuestionSize(); i++) {

            // 维护一个不重复题目集合，将父亲的 被留下的题目（基因）添加进该集合
            Set<QbQuestionDTO> fatherQuestions = new HashSet<>(father.getQuestions().subList(0, remainNumber));
            // 当前母亲的题目
            QbQuestionDTO question = mother.getQuestion(i);
            // 如果父亲的被留下的题目（基因） 中存在当前的题目，则获取一个不重复的随机题目
            if (fatherQuestions.contains(question)) {
                do { //获取随机题目
                    question = getRandomQuestion();
                } while (!fatherQuestions.add(question)); //插入不成功代表题目重复，继续循环
            }
            paper.addQuestion(question);
        }
        return paper;
    }

    // 变异操作：在新染色体上随机选择若干个基因，然后随机修改基因的值，从而给现有的染色体引入了新的基因，突破了当前搜索的限制，更有利于算法寻找到全局最优解。
    private void mutate(ExamPaper paper) {
        // 计算当前试卷个体的各种题型所占的题目数量
        Map<Integer, Integer> questionTypeCount = new HashMap<>();
        for (QbQuestionDTO question : paper.getQuestions()) {
            questionTypeCount.put(question.getType(), questionTypeCount.getOrDefault(question.getType(), 0) + 1);
        }
        List<QbQuestionDTO> currentQuestions = paper.getQuestions();
        int questionSize = paper.getQuestionSize();
        for (int i = 0; i < paper.getQuestionSize(); i++) {
            //如果满足变异概率，首先如果试卷的题目数量=题库中匹配的题目数量，那就不用变异了，直接返回。不过这样的话，其实可以直接不用遗传算法生成，直接返回就行了，这个有空再优化
            if (questionSize < questions.size()
                    && Math.random() < config.getMutationRate()   // 满足变异概率，并且题库中该题目类型的数量 大于 当前试卷个体中该题目类型的数量，这样是防止变异时将题型改变
                    && questionGroup.get(currentQuestions.get(i).getType()).size() > questionTypeCount.get(currentQuestions.get(i).getType())) {
                // 维护一个不重复题目集合，就可以在 O(1) 的时间内检查题目是否已经存在于试卷中，getRandomQuestion()会花费O(n)的时间
                Set<QbQuestionDTO> uniqueQuestions = new HashSet<>(currentQuestions);
                QbQuestionDTO question;
                do {
                    question = getRandomQuestion();
                    // 还要保证 获取到的随机题目跟要被替换的题目是 同一题型
                } while (!currentQuestions.get(i).getType().equals(question.getType()) && !uniqueQuestions.add(question)); //插入不成功代表题目重复，继续循环
                paper.updateQuestionOnIndex(question, i);
            }
        }
    }

    private QbQuestionDTO getRandomQuestion() {
        int randomIndex = (int) (Math.random() * questions.size());
        return questions.get(randomIndex);
    }


}

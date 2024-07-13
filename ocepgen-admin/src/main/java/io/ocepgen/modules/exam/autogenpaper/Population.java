package io.ocepgen.modules.exam.autogenpaper;

import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 种群类
 * 种群：由一个个试卷个体（染色体）组成，试卷（染色体）又由一个个题目（基因）组成
 * @author Roxy
 */
@Slf4j
@Data
public class Population {
    // 种群个体
    private ExamPaper[] papers;
    // 当前种群中实际最靠后的试卷个体索引
    private int currentLastPaperIndex = 0;
    // 每个题型 实际 分配的期望题数
    Map<QuestionTypeEnum, Integer> realAllocateQuestionCountMap;


    public Population(int populationSize, List<QbQuestionDTO> questions, GenExamPaperRuleVo rule) {
        // 初始化种群
        papers = new ExamPaper[populationSize];
        // 按题目类型 对 题目进行分组
        Map<Integer, List<QbQuestionDTO>> questionGroup = questions.stream()
                .collect(Collectors.groupingBy(QbQuestionDTO::getType));

        // 获取用户设定的题型分布
        Map<QuestionTypeEnum, Float> typeWeight = new HashMap<>(rule.getQuestionTypeWeightMap());
        // 如果用户没有设置分布，那么按照实际题型占比进行题目分配
        if (rule.getQuestionTypeWeightMap().isEmpty()) {
            for (Integer key : questionGroup.keySet()) {
                typeWeight.put(QuestionTypeEnum.getByValue(key)
                        , (float) questionGroup.get(key).size() / questions.size());
            }
        }
        // 如果用户设置了题型分布，按用户设定进行题目分配
        // 剩余需要分配的题数
        int remainingQuestionCount = rule.getExpectedQuestionCount();
        // 每个题型 实际 分配的题数
        realAllocateQuestionCountMap = new HashMap<>();
        // 拥有充足题量的题型
        Set<QuestionTypeEnum> abundantTypeSet = new HashSet<>(typeWeight.keySet());

        for (QuestionTypeEnum type : typeWeight.keySet()) {
            float currentTypeWeight = typeWeight.get(type);
            //当前题型的预期题数 = 题型权重 * 期望题数
            int expectedQuestionCount = (int) (currentTypeWeight * rule.getExpectedQuestionCount());
            log.warn("当前题型{}预期题数{}", type.label(), expectedQuestionCount);
            // 题库中 当前题型的实际数量
            int realQuestionCount = 0;
            if (questionGroup.get(type.value()) != null) {
                realQuestionCount = questionGroup.get(type.value()).size();
            }
            // 如果题库中 当前题型题目数量不够分配
            if (realQuestionCount < expectedQuestionCount) {
                // 将题型从 题型数量充足的题型集合 剔除
                abundantTypeSet.remove(type);
                // 填充当前题型的 实际分配题数
                realAllocateQuestionCountMap.put(type, realQuestionCount);
                // 剩余需要分配的题数减少
                remainingQuestionCount -= realQuestionCount;
            } else {

                realAllocateQuestionCountMap.put(type, expectedQuestionCount);
                // 剩余需要分配的题数减少
                remainingQuestionCount -= expectedQuestionCount;
            }
        }
        // 平均分配剩余的题目数量 到 题型数量充足的题型集合中
        Iterator<QuestionTypeEnum> iterator; //iterator.next()不会遍历已经访问过的元素，这里需要循环遍历
        // 题型数量充足的题型枚举
        QuestionTypeEnum key;
        // 当前题型在题库中的数量
        int questionInBankCount;
        // 当前题型已经被分配的数量
        int realAllocateQuestionCount;
        // 一次一次分配剩余的题目数量，实现平均分配
        while (remainingQuestionCount > 0) {
            // 重置iterator
            iterator = abundantTypeSet.iterator();
            // 开始遍历map
            while (iterator.hasNext()) {
                key = iterator.next(); //获取当前题型枚举
                // 题型在题库中的数量
                questionInBankCount = questionGroup.get(key.value()).size();

                realAllocateQuestionCount = realAllocateQuestionCountMap.get(key);
                // 如果已经分配的数量 小于 题库中的数量，说明还有剩，才进行分配
                if (realAllocateQuestionCount < questionInBankCount) {
                    // 需要分配的题目数量-1
                    remainingQuestionCount--;
                    // 当前题型已经分配的数量+1
                    realAllocateQuestionCountMap.put(key, realAllocateQuestionCount + 1);
                } else { // 如果不小于，说明当前题型数量已经不充足
                    //在充足题型Map剔除当前题型
                    iterator.remove(); // 使用迭代器的 remove() 方法安全地移除当前元素 ，在这里直接用abundantTypeSet.remove(key)的话会出现多线程不安全问题
                    continue;
                }
                if (remainingQuestionCount == 0) { //如果已经分配完，直接退出循环
                    break;
                }
            }
        }
        // 对每个试卷个体进行实际分配题目
        for (int i = 0; i < populationSize; i++) {
            ExamPaper paper = new ExamPaper();
            paper.setId(i + 1);
            // 进行题目添加
            realAllocateQuestionCountMap.forEach((type, count) -> {
                if (count > 0) {
                    // 对分组后的题目进行乱序，以让不同试卷获得不同的题目
                    Collections.shuffle(questionGroup.get(type.value()));
                    paper.getQuestions().addAll(questionGroup.get(type.value()).subList(0, count));
                    //log.warn("种群初始化中，试卷个体ID：{}, 当前试卷题型：{}, 分配的题目数量：{}", paper.getId(), type, count);
                }
            });
            // 计算当前试卷的适应度
            paper.setFitness(rule, realAllocateQuestionCountMap);
            // 将试卷添加进种群
            papers[i] = paper;
            log.warn("种群初始化中，试卷个体ID：{}, 试卷个体题目数量：{}, 试卷个体平均难度：{}，试卷个体适应度: {}" ,
                    paper.getId(), paper.getQuestionSize(), paper.getDifficulty(), paper.getFitness());
        }
        log.info("初始化种群完毕，共：{} 个试卷个体", papers.length);
    }

    public Population(int populationSize) {
        papers = new ExamPaper[populationSize];
    }

    public Population(ExamPaper[] papers) {
        this.papers = papers;
        currentLastPaperIndex = papers.length - 1;
    }

    // 获取子代种群
    public Population getPopulation(int paperCount) {
        // 防止paperCount大于种群大小
        paperCount = Math.min(paperCount, papers.length);

        // 将当前种群中的个体乱序
        List<ExamPaper> paperList = Arrays.asList(papers);
        Collections.shuffle(paperList);

        // 创建子代种群
        paperList = paperList.subList(0, paperCount);
        ExamPaper[] childPapers = paperList.toArray(new ExamPaper[paperCount]);
        return new Population(childPapers);
    }

    // 获取当前种群中最优秀的个体
    public ExamPaper getBestPaper() {
        return Arrays.stream(papers).min(Comparator.comparing(ExamPaper::getFitness)).orElse(null);
    }

    // 根据索引获取种群个体
    public ExamPaper getPaper(int index) {
        return papers[index];
    }

    // 往种群中添加个体
    public void addPaper(ExamPaper paper) {
        if (currentLastPaperIndex < papers.length) {
            papers[currentLastPaperIndex] = paper;
            currentLastPaperIndex++;
        }
    }

    // 根据索引往群众中修改个体
    public void setPaper(int index, ExamPaper paper) {
        if (index < papers.length) {
            papers[index] = paper;
        }
    }
}

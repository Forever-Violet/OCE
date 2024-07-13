package io.ocepgen.modules.exam.autogenpaper;

import io.ocepgen.modules.exam.autogenpaper.vo.GenExamPaperRuleVo;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import lombok.Data;
import java.util.*;


/**
 * 试卷个体类
 * @author Roxy
 */
@Data
public class ExamPaper {

    private Integer id;
    /**
     * 试卷总分
     */
    private double score = 0;
    /**
     * 试卷难度 （平均）
     */
    private double difficulty = 0;
    /**
     * 适应度
     */
    private double fitness = 0;
    /**
     * 题目列表
     */
    private List<QbQuestionDTO> questions = new LinkedList<>(); //因为增删改操作多于读操作，所以使用链表list

    // 获取试卷平均难度
    public double getDifficulty() {
        if (difficulty == 0) {
            difficulty = questions.stream().mapToDouble(QbQuestionDTO::getDifficulty).sum() / questions.size();
        }
        return difficulty;
    }

    // 获取试卷总分
    public double getScore() {
        if (score == 0) {
            score = questions.stream().mapToDouble(QbQuestionDTO::getScore).sum();
        }
        return score;
    }

    // 计算并设置适应度 ，这里适应度越小越好，最好是0  。适应度函数
    public void setFitness(GenExamPaperRuleVo rule, Map<QuestionTypeEnum, Integer> realAllocateQuestionCountMap) {
        if (fitness != 0) {
            return;
        }
        fitness = Math.abs(getDifficulty() - rule.getExpectedDifficulty());
        // 遍历当前试卷个体的题目，将题目类型及其数量进行分组
        Map<Integer, Integer> currentQuestionTypeCountMap = new HashMap<>();
        for (QbQuestionDTO question : questions) {
            currentQuestionTypeCountMap.put(question.getType()
                    , currentQuestionTypeCountMap.getOrDefault(question.getType(), 0) + 1);
        }
        // 如果题目类型分配不均衡，则增加适应度。具体计算方法为 : 适应度 = 适应度 + |(当前题目类型数量 - 实际期望分配题目类型数量)| / 试卷题目总数 * 0.1
        // 当前题型数量分配 与 初始分配值 差距越大，适应度就越差
        // 题目总数越多，单个题型数量的偏差对整体影响可能越小，使用试卷总题目数的10%作为缩放因子
        for (QuestionTypeEnum type : QuestionTypeEnum.values()) {
            fitness += (double) (Math.abs(currentQuestionTypeCountMap.getOrDefault(type.value(), 0)
                    - realAllocateQuestionCountMap.getOrDefault(type, 0))) / (questions.size() * 0.1);
        }
    }


    /**
     * 在指定索引修改题目 用于变异操作
     */
    public void updateQuestionOnIndex(QbQuestionDTO question, int index) {
        questions.set(index, question);
        resetPaperParams();
    }

    /**
     * 添加题目
     */
    public void addQuestion(QbQuestionDTO question) {
        questions.add(question);
        resetPaperParams();
    }

    /**
     * 检查该题目是否存在于题目列表中，用于查重
     */
    public boolean containsQuestion(QbQuestionDTO question) {
        return questions.stream().anyMatch(q -> q==null || Objects.equals(question.getId(), q.getId()));
    }
    /**
     * 根据索引获取题目
     */
    public QbQuestionDTO getQuestion(int index) {
        return questions.get(index);
    }

    /**
     * 获取题目总数量
     */
    public int getQuestionSize() {
        return questions.size();
    }


    private void resetPaperParams() {
        score = 0;

        difficulty = 0;

        fitness = 0;
    }

}

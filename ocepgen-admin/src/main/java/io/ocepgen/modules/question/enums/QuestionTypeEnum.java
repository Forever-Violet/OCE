package io.ocepgen.modules.question.enums;

import lombok.Getter;

/**
 * 题目适用范围枚举
 * @author Roxy
 */
public enum QuestionTypeEnum {

    /**
     * 单选题
     */
    SINGLE_CHOICE(0, "单选题"),
    /**
     * 多选题
     */
    MULTIPLE_CHOICE(1, "多选题"),
    /**
     * 判断题
     */
    JUDGMENTAL(2,"判断题"),
    /**
     * 填空题
     */
    COMPLETION(3, "填空题"),

    /**
     * 主观题
     */
    SUBJECTIVE(4, "主观题");

    private final int value;

    private final String label;

    QuestionTypeEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int value() {
        return this.value;
    }

    public String label() {
        return this.label;
    }

    public static QuestionTypeEnum getByValue(Integer value) {
        return QuestionTypeEnum.values()[value];
    }

}

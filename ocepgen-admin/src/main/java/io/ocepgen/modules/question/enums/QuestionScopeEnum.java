package io.ocepgen.modules.question.enums;

/**
 * 题目适用范围枚举
 * @author Roxy
 */
public enum QuestionScopeEnum {

    /**
     * 公共题库
     */
    PUBLIC(0),
    /**
     * 按钮
     */
    PRIVATE(1);

    private int value;

    QuestionScopeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}

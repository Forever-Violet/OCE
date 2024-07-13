package io.ocepgen.modules.exam.enums;

/**
 * 试卷适用范围枚举
 * @author Roxy
 */
public enum ExamPaperScopeEnum {

    /**
     * 公共试卷
     */
    PUBLIC(0),
    /**
     * 私有
     */
    PRIVATE(1);

    private int value;

    ExamPaperScopeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}

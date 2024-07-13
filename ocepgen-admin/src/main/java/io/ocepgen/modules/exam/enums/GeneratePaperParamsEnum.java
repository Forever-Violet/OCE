package io.ocepgen.modules.exam.enums;

/**
 * 组卷配置适用范围枚举
 * @author Roxy
 */
public enum GeneratePaperParamsEnum {

    /**
     * 公共
     */
    PUBLIC(0),
    /**
     * 私有
     */
    PRIVATE(1);

    private int value;

    GeneratePaperParamsEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}

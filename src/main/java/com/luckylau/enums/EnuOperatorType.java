package com.luckylau.enums;

import com.luckylau.base.BaseEnum;

public enum EnuOperatorType implements BaseEnum<String, String> {
    /**
     * eq，=
     */
    EQ("eq", "="),
    NE("ne", "!="),
    GT("gt", ">"),
    LT("lt", "<"),
    GE("ge", ">="),
    LE("le", "<="),
    IN("in", "in"),
    NOTIN("notin", "not in"),
    LIKE("like", "like"),
    LLIKE("llike", "like"),
    RLIKE("rlike", "like"),
    ISNULL("isnull", "is null"),
    ISNOTNULL("isnotnull", "is not null");

    private String type;
    private String value;

    EnuOperatorType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        return value;
    }
}
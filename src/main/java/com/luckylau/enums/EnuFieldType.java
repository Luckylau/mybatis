package com.luckylau.enums;

import com.luckylau.base.BaseEnum;

public enum EnuFieldType implements BaseEnum<String, Class<?>> {
    /**
     *
     */
    STRING("string", String.class),
    FLOAT("float", Float.class),
    INT("int", Integer.class),
    LONG("long", Long.class);

    private String type;
    private Class<?> value;

    EnuFieldType(String type, Class<?> value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Class<?> getValue() {
        return value;
    }
}
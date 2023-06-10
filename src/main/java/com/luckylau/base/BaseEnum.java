package com.luckylau.base;

public interface BaseEnum<K, V> {

    /**
     * @return 返回type
     */
    K getType();

    /**
     * @return 返回value
     */
    V getValue();

}
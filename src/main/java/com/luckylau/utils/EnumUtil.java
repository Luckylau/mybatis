package com.luckylau.utils;

import com.luckylau.base.BaseEnum;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
public class EnumUtil {

    public static Map<Class<? extends BaseEnum>, BaseEnum[]> enumMap = new WeakHashMap<>();

    public static <T, V> boolean valid(Class<? extends BaseEnum> enumClass, T type) {
        Class<? extends BaseEnum<T, V>> enumClazz = (Class<? extends BaseEnum<T, V>>) enumClass;
        return getEnum(enumClazz, type) != null;
    }

    public static <T, V> BaseEnum<T, V> getEnum(Class<? extends BaseEnum<T, V>> enumClass, T type) {
        Assert.notNull(type, "传入枚举类型不能为空");
        for (BaseEnum<T, V> baseEnum : getEnumConsts(enumClass)) {
            if (baseEnum.getType().equals(type)) {
                return baseEnum;
            }
        }
        return null;
    }

    public static <T, V> BaseEnum[] getEnumConsts(Class<? extends BaseEnum<T, V>> enumClass) {
        validEnumClass(enumClass);
        if (enumMap.get(enumClass) == null) {
            synchronized (enumMap) {
                if (enumMap.get(enumClass) == null) {
                    enumMap.put(enumClass, enumClass.getEnumConstants());
                }
            }
        }
        return enumMap.get(enumClass);
    }

    private static <T, V> void validEnumClass(Class<? extends BaseEnum<T, V>> enumClass) {
        if (enumClass == null || !enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getClass().getName() + "不是枚举类型");
        }
    }

}

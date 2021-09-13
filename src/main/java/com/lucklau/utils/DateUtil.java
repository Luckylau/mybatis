package com.lucklau.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
public class DateUtil {
    public static String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN_YYYY_MM_DD_HH_MM_SS;
        }
        return new SimpleDateFormat(pattern);
    }
}

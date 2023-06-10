package com.luckylau.utils;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
public class CommonUtil {

    public static <T extends Throwable> void throwAs(Exception e) throws T {
        throw (T) e;
    }
}

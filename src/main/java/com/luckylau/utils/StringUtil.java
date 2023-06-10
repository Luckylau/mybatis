package com.luckylau.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.luckylau.utils.ClassUtil.castType;
import static org.springframework.objenesis.ObjenesisHelper.newInstance;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
public class StringUtil {
    public static final String DEFAULT_STRING_SEPARATOR = ",";
    public static final String DEFAULT_HIDE_REPLACE_CHAR = "*";

    public static <T> List<T> splitString2Collection(String value, Class<T> clazz) {
        return splitString2Collection(value, clazz, ArrayList.class, null);
    }

    public static String camelToUnderscore(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            return null;
        }
        int distance = 'a' - 'A';
        StringBuilder sb = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append((char) (c + distance));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String lowerFirstLetter(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static String parseTemplate(String openToken, String closeToken, String text, Map<String, Object> context) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        int start = text.indexOf(openToken, 0);
        if (start == -1) {
            return text;
        }
        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    builder.append(context.get(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    public static String wrap(String value) {
        return wrap(value, "`");
    }

    public static String wrap(String value, String wrapper) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(wrapper)) {
            return value;
        }
        return wrapper + value + wrapper;
    }

    public static <V, T extends Collection<V>> T splitString2Collection(String value, Class<V> valClazz, Class<T> colleclazz, String separator) {
        if (colleclazz == null || colleclazz.isInterface()) {
            throw new IllegalArgumentException("集合类[" + colleclazz + "]必须是可实现的类");
        }
        if (StringUtils.isEmpty(value) || valClazz == null) {
            return null;
        }
        if (StringUtils.isEmpty(separator)) {
            separator = DEFAULT_STRING_SEPARATOR;
        }
        T t = newInstance(colleclazz);
        for (String val : value.split(separator)) {
            t.add(castType(val, valClazz));
        }
        return t;
    }
}


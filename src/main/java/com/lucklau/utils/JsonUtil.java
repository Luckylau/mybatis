package com.lucklau.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author luckylau
 * @Date 2019/6/13
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            CommonUtil.throwAs(e);
        }
        return null;
    }
}

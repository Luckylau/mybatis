package com.luckylau.utils;

import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

public class RequestUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) getRequestAttributes()).getResponse();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

}
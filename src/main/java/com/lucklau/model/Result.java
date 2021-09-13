package com.lucklau.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * luckylau  2018/5/21 下午5:16
 */
@Data
public class Result {
    public static final int SUCC_CODE = 200;
    public static final int FAIL_CODE = 400;
    public static final int FAIL_ARG_ERR = 400;
    public static final int FAIL_SESSION_TIME_OUT = 401;
    public static final int FAIL_ACCESS_DENIED = 403;

    public static final Result SUCCESS = new Result(SUCC_CODE, "SUCCESS");

    private int code;
    private String msg;
    private Object data;

    public Result() {
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(List data) {
        Result result = new Result(SUCC_CODE, "SUCCESS");
        HashMap<String, Object> rs = new HashMap<String, Object>();
        rs.put("list", data);
        result.setData(rs);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result(SUCC_CODE, "SUCCESS");
        result.setData(data);
        return result;
    }

    public static Result fail(String msg) {
        return fail(FAIL_CODE, msg);
    }

    public static Result fail(int code, String msg) {
        return new Result(code, msg);
    }

    public static boolean isSuccess(Result result) {
        if (result == null) {
            return false;
        }
        int code = result.getCode();
        return code == 200 || code == 201 || code == 204;

    }

    public static Result notFound() {
        return new Result(404, "not found");
    }


    public static Result deleted() {
        return new Result(404, "deleted");
    }
}

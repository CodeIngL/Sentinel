package com.taobao.csp.sentinel.dashboard.view;

/**
 * @author leyou
 */
public class Result<R> {
    private int code;
    private String msg;
    private R data;

    public static <R> Result<R> ofSuccess(R data) {
        Result<R> result = new Result<>();
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <R> Result<R> ofSuccessMsg(String msg) {
        Result<R> result = new Result<>();
        result.setMsg(msg);
        return result;
    }

    public static <R> Result<R> ofFail(int code, String msg) {
        Result<R> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <R> Result<R> ofThrowable(int code, Throwable throwable) {
        Result<R> result = new Result<>();
        result.setCode(code);
        result.setMsg(throwable.getClass().getName() + ", " + throwable.getMessage());
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}

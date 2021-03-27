package com.luna.common.exception;

public class BaseException extends RuntimeException {
    private int    code;
    private String message;

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static boolean isBaseException(Throwable t) {
        return t instanceof BaseException;
    }
}

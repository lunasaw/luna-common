package com.luna.common.exception;

/**
 * @author luna
 */
public class BaseException extends RuntimeException {
    /** 错误吗 */
    private final int    code;
    /** 错误消息 */
    private final String message;

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

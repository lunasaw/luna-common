package com.luna.common.exception;

import com.luna.common.dto.constant.ResultCode;

/**
 * @author luna
 */
public class BaseException extends RuntimeException {
    /** 错误码 {@link  ResultCode} */
    private int    code;
    /** 错误消息 */
    private String message;

    public BaseException(int status, String msg, Throwable throwable) {
        super(throwable);
        this.code = status;
        this.message = msg;
    }

    public BaseException(String msg, Throwable throwable) {
        super(throwable);
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(int status, Throwable throwable) {
        super(throwable);
        this.code = status;
    }

    public static boolean isBaseException(Throwable t) {
        return t instanceof BaseException;
    }

    public BaseException(String message) {
        super(message);
        this.code = ResultCode.PARAMETER_INVALID;
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public BaseException() {}
}

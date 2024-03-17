package com.luna.common.exception;

import com.luna.common.dto.constant.ResultCode;

/**
 * @author luna
 */
public class BaseException extends RuntimeException {
    public static final BaseException SYSTEM_ERROR            = new BaseException(100000, "系统异常");
    public static final BaseException UNKNOWN                 = new BaseException(600000, "系统错误");
    public static final BaseException PARAMETER_ERROR         = new BaseException(600001, "参数错误");
    public static final BaseException MISSING                 = new BaseException(600002, "信息不存在");
    public static final BaseException PARAMETER_OVERFLOW      = new BaseException(600003, "参数个数超限");
    public static final BaseException REPEAT_OPERATION        = new BaseException(600004, "重复操作(幂等校验失败)");
    public static final BaseException UN_SUPPORT_ENCRYPT_TYPE = new BaseException(600005, "不支持该数据加密类型");

    /** 错误码 {@link ResultCode} */
    private int                       code;
    /** 错误消息 */
    private String                    message;

    public BaseException(BaseException baseException, String... extendMessage) {
        this(baseException.getCode(),
            String.format(baseException.getMessage(), extendMessage));
    }

    public BaseException(int status, String msg, Throwable throwable) {
        super(throwable);
        this.code = status;
        this.message = msg;
    }

    public BaseException(String msg, Throwable throwable) {
        super(throwable);
        this.message = msg;
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(int status, Throwable throwable) {
        super(throwable);
        this.code = status;
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

    public static boolean isBaseException(Throwable t) {
        return t instanceof BaseException;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

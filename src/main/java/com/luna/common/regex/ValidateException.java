package com.luna.common.regex;

import com.luna.common.exception.BaseException;
import com.luna.common.text.StringTools;

/**
 * 验证异常
 *
 * @author xiaoleilu
 */
public class ValidateException extends BaseException {
    private static final long serialVersionUID = 6057602589533840889L;

    public ValidateException() {}

    public ValidateException(String msg) {
        super(msg);
    }

    public ValidateException(String messageTemplate, Object... params) {
        super(StringTools.format(messageTemplate, params));
    }

    public ValidateException(Throwable throwable) {
        super(throwable);
    }

    public ValidateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ValidateException(int status, String msg) {
        super(status, msg);
    }

    public ValidateException(int status, Throwable throwable) {
        super(status, throwable);
    }

    public ValidateException(int status, String msg, Throwable throwable) {
        super(status, msg, throwable);
    }
}

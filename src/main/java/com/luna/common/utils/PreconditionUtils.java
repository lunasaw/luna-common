package com.luna.common.utils;

import com.luna.common.dto.constant.ResultCode;
import com.luna.common.exception.BaseException;

/**
 * @author Luna
 */
public class PreconditionUtils {

    /**
     * 校验表达式是否正确
     *
     * @param baseException 异常
     */
    public static void check(boolean expression, RuntimeException baseException) {

        if (!expression) {
            throw baseException;
        }

    }

    public static void checkExpression(boolean expression, String msg) {

        if (!expression) {
            throw new BaseException(ResultCode.PARAMETER_INVALID, msg);
        }

    }

    public static <T> T checkNotNull(T reference, String errorMessage) {

        if (reference == null) {
            throw new BaseException(ResultCode.PARAMETER_INVALID, errorMessage);
        }

        return reference;
    }

    public static <T> T checkParamNotNull(T reference) {

        if (reference == null) {
            throw new BaseException(ResultCode.PARAMETER_INVALID, ResultCode.MSG_PARAMETER_INVALID);
        }

        return reference;
    }
}
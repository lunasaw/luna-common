package com.luna.common.check;

import com.luna.common.dto.constant.ResultEnum;
import com.luna.common.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Preconditions {

    /**
     * 校验表达式是否正确
     *
     * @param expression
     * @param error FxErrorCode枚举
     */
    public static void check(boolean expression, ResultEnum error) {

        if (!expression) {
            throw new BaseException(error.getErrorCode(), error.getErrorMsg());
        }

    }

    public static void checkExpression(boolean expression, String msg) {

        if (!expression) {
            throw new BaseException(ResultEnum.PARAMETER_INVALID.getErrorCode(), msg);
        }

    }

    public static void checkExpression(boolean expression, ResultEnum resultEnum) {

        if (!expression) {
            throw new BaseException(resultEnum.getErrorCode(), resultEnum.getErrorMsg());
        }

    }

    public static <T> T checkNotNull(T reference, String errorMessage) {

        if (reference == null) {
            throw new BaseException(ResultEnum.PARAMETER_INVALID.getErrorCode(), errorMessage);
        }

        return reference;
    }

    public static <T> T checkNotNull(T reference, ResultEnum bizErrorEnum) {

        if (reference == null) {
            throw new BaseException(bizErrorEnum.getErrorCode(), bizErrorEnum.getErrorMsg());
        }

        return reference;
    }

    public static <T> T checkParamNotNull(T reference) {

        if (reference == null) {
            throw new BaseException(ResultEnum.PARAMETER_INVALID.getErrorCode(), ResultEnum.PARAMETER_INVALID.getErrorMsg());
        }

        return reference;
    }

    public static void triggerException(String message) {
        throw new BaseException(ResultEnum.PARAMETER_INVALID.getErrorCode(), message);
    }
}
package com.luna.common.check;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.luna.common.exception.BaseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * @author luna
 */
public class ValidDataUtil {

    private static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    /**
     * 校验器
     *
     * @param t 参数
     * @param <T> 参数类型
     * @return
     */
    public static <T> List<String> valid(T t) {
        if (t == null) {
            throw new BaseException("参数错误");
        }
        Validator validatorFactory = DEFAULT_VALIDATOR_FACTORY.getValidator();
        Set<ConstraintViolation<T>> errors = validatorFactory.validate(t);
        return errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

    public static <T> void validThrow(T t) {
        List<String> valid = valid(t);
        for (String s : valid) {
            throw new BaseException(s);
        }
    }
}

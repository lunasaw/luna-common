package com.luna.common.sensitive;

import java.lang.annotation.*;

/**
 * 敏感字段注解
 * 字段被注解后，使用SensitiveUtil会脱敏该字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Sensitive{
    // 字段类型
    FieldType value();
}

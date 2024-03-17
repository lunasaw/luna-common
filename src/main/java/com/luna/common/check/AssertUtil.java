package com.luna.common.check;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.luna.common.exception.BaseException;

/**
 * 校验辅助类
 *
 * @author luna
 **/
public class AssertUtil {

    /**
     * 私有构造方法
     */
    private AssertUtil() {}

    /**
     * 校验指定对象不能为null
     *
     * @param object 对象
     * @param baseException 运行时异常
     */
    public static void notNull(Object object, BaseException baseException, String... extendInfos) {
        isTrue(object != null, baseException, extendInfos);
    }

    /**
     * 校验指定集合不能为空
     *
     * @param collection 集合
     * @param baseException 运行时异常
     */
    public static void notEmpty(Collection collection, BaseException baseException,
        String... extendInfos) {
        isTrue(CollectionUtils.isNotEmpty(collection),
            baseException, extendInfos);
    }

    /**
     * 校验指定对象不能为null
     *
     * @param map map集合
     * @param baseException 运行时异常
     */
    public static void notEmpty(Map map, BaseException baseException,
        String... extendInfos) {
        isTrue(MapUtils.isNotEmpty(map), baseException, extendInfos);
    }

    /**
     * 校验字符串不能为空
     *
     * @param str 字符串
     * @param baseException 运行时异常
     */
    public static void notBlank(String str, BaseException baseException,
        String... extendInfos) {
        if (StringUtils.isBlank(str)) {
            isTrue(false, baseException, extendInfos);
        }
    }

    /**
     * 校验指定条件为true
     *
     * @param condition 条件
     * @param baseException 运行时异常
     */
    public static void isFalse(boolean condition, BaseException baseException,
        String... extendInfos) {
        if (condition) {
            fail(baseException, extendInfos);
        }
    }

    /**
     * 校验指定条件为true
     *
     * @param condition 条件
     * @param baseException 运行时异常
     */
    public static void isTrue(boolean condition, BaseException baseException,
        String... extendInfos) {
        if (!condition) {
            fail(baseException, extendInfos);
        }
    }

    /**
     * 抛出指定运行时异常
     *
     * @param baseException 运行时异常
     */
    public static void fail(BaseException baseException, String... extendInfos) {
        Assert.notNull(baseException);

        throw new BaseException(baseException, extendInfos);
    }
}

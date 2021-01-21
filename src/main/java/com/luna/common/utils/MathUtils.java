package com.luna.common.utils;

import java.math.BigDecimal;

/**
 * @author Luna
 */
public class MathUtils {
    /**
     * 次方计算
     * <p>
     * 对于任何异常都不做处理
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    public static long pow(long a, long b) {
        return new BigDecimal(a).pow((int)b).longValue();
    }
}

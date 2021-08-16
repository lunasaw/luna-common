package com.luna.common.utils;

import java.math.BigDecimal;

/**
 * @author Luna
 */
public class MathUtils {

    private static final BigDecimal HUNDRED_B = new BigDecimal("100");

    /**
     * 次方计算
     * <p>
     * 对于任何异常都不做处理
     * </p>
     *
     * @param a 底数
     * @param b 幂
     * @return
     */
    public static long pow(long a, long b) {
        return new BigDecimal(a).pow((int)b).longValue();
    }

    /**
     * 大数相减 四舍五入
     * 
     * @param b1 big-1
     * @param b2 big-2
     * @return
     */
    public static BigDecimal substract(BigDecimal b1, BigDecimal b2) {
        if (null == b1 || null == b2) {
            return null;
        }
        int scale = 2;
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 两个百分比相乘
     *
     * @param b1 percent—1
     * @param b2 percent-2
     * @return
     */
    public static BigDecimal percentMultiply(BigDecimal b1, BigDecimal b2) {
        if (null == b1 || null == b2) {
            return null;
        }
        int scale = 4;
        return b1.multiply(b2).divide(HUNDRED_B, scale, BigDecimal.ROUND_HALF_UP);
    }
}

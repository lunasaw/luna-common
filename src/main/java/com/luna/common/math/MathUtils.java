package com.luna.common.math;

import com.luna.common.constant.Constant;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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
        return substract(b1, b2, scale);
    }

    /**
     * 大数相减 四舍五入
     *
     * @param b1 big-1
     * @param b2 big-2
     * @param scale 保留小数位数
     * @return
     */
    public static BigDecimal substract(BigDecimal b1, BigDecimal b2, int scale) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.subtract(b2).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 大数相除 四舍五入
     *
     * @param b1 big-1
     * @param b2 big-2
     * @return
     */
    public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.divide(b2, Constant.NUMBER_TWO, RoundingMode.HALF_UP);
    }

    /**
     * 大数相除 四舍五入
     *
     * @param b1 big-1
     * @param b2 big-2
     * @param scale 保留小数位数
     * @return
     */
    public static BigDecimal divide(BigDecimal b1, BigDecimal b2, int scale) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.divide(b2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 大数相除
     *
     * @param b1 big-1
     * @param b2 big-2
     * @param scale 保留小数位数
     * @param roundingMode {@link RoundingMode}
     * @return
     */
    public static BigDecimal divide(BigDecimal b1, BigDecimal b2, int scale, RoundingMode roundingMode) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.divide(b2, scale, roundingMode);
    }

    /**
     * 大数相加
     *
     * @param b1 big-1
     * @param b2 big-2
     * @param scale 保留小数位数
     * @param roundingMode {@link RoundingMode}
     * @return
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2, int scale, RoundingMode roundingMode) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.add(b2, new MathContext(scale, roundingMode));
    }

    /**
     * 大数相加 四舍五入
     *
     * @param b1 big-1
     * @param b2 big-2
     * @param scale 保留小数位数
     * @return
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2, int scale) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.add(b2, new MathContext(scale, RoundingMode.HALF_UP));
    }

    /**
     * 大数相加
     *
     * @param b1 big-1
     * @param b2 big-2
     * @return
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
        if (null == b1 || null == b2) {
            return null;
        }
        return b1.add(b2);
    }

    /**
     * 舍数
     *
     * @param bigDecimal
     * @param scale
     * @param roundingMode {@link RoundingMode}
     * <p>
     * RoundingMode.CEILING：取右边最近的整数
     * RoundingMode.DOWN：去掉小数部分取整，也就是正数取左边，负数取右边，相当于向原点靠近的方向取整
     * RoundingMode.FLOOR：取左边最近的正数
     * RoundingMode.UP：向上取整
     * RoundingMode.HALF_DOWN:五舍六入，负数先取绝对值再五舍六入再负数
     * RoundingMode.HALF_UP:四舍五入，负数原理同上
     * RoundingMode.HALF_EVEN:这个比较绕，整数位若是奇数则四舍五入，若是偶数则五舍六入
     * <p/>
     * @return
     */
    public static BigDecimal roundingMode(BigDecimal bigDecimal, int scale, RoundingMode roundingMode) {
        return bigDecimal.setScale(scale, roundingMode);
    }

    /**
     * 截断0输出
     *
     * @param b1 big-1
     * @return
     */
    public static BigDecimal disZeros(BigDecimal b1) {
        return b1.stripTrailingZeros();
    }

    /**
     * 字符串输出
     *
     * @param b1 big-1
     * @return
     */
    public static String toPlainString(BigDecimal b1) {
        return b1.toPlainString();
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

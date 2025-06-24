package com.luna.common.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class MoneyUtil {

    private static final Logger LOGGER             = LoggerFactory.getLogger(MoneyUtil.class);

    private static BigDecimal   BIGDECIMAL_HUNDRED = new BigDecimal("100.00");

    public static String        ZERO_STR           = "0";

    public static String        YUAN_STR           = "¥";

    public static int           SCALE              = 2;

    /**
     * 分转str元
     * 
     * @param fen
     * @return
     */
    public static String fenToYuan(Long fen) {
        return convertLong2Str(fen);
    }

    /**
     * 分转str元
     *
     * @param fen
     * @return
     */
    public static String fenToYuanOnePoint(Long fen) {
        if (fen == null) {
            return null;
        }

        if (fen <= 0) {
            return "0.0";
        }
        Double yuan = fen / 100d;
        DecimalFormat df = new DecimalFormat("###0.0");
        return df.format(yuan);
    }

    public static double fen2Yuand(Long price) {
        if (price == null || price <= 0) {
            return 0.0;
        }
        return ((double)price) / 100;
    }

    public static String formatPrice(Double d) {
        try {
            return new DecimalFormat("0.##").format(d);
        } catch (Exception e) {
            LOGGER.error("DecimalFormat.format error! param={}", d, e);
        }
        return null;
    }

    public static String formatPrice(String d) {
        try {
            long cent = yuan2Fen(d);
            return formatPrice(fen2Yuand(cent));
        } catch (Exception e) {
            LOGGER.error("DecimalFormat.format error! param={}", d, e);
        }
        return null;
    }

    /**
     * convertLong2Str,会乘以0.01
     * 不保留0小数位
     * 1.00 返回1
     * 1.10 返回1.1
     * 
     * @param lMoney
     * @return
     */
    public static String convertLong2Str(Long lMoney) {

        if (lMoney == null) {
            return null; // "0.0";
        }
        return formatPrice(new BigDecimal(lMoney).multiply(new BigDecimal(0.01)).doubleValue());
    }

    public static String convert2Str(Long price) {
        return price == null ? "" : (new BigDecimal(price)).divide(new BigDecimal(100)).setScale(2, RoundingMode.FLOOR).toString();
    }

    public static String convert2WithDefaultStr(Long price) {
        String priceStr = convert2Str(price);
        if (StringUtils.isEmpty(priceStr)) {
            return "0";
        }
        return priceStr;
    }

    /**
     * 分转BigDecimal元
     * 
     * @param fen
     * @return
     */
    public static BigDecimal fen2Yuan(long fen) {
        BigDecimal bigDecimalFen = new BigDecimal(fen);
        return bigDecimalFen.divide(BIGDECIMAL_HUNDRED, SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取半价
     *
     * @param originPrice
     * @return
     */
    public static BigDecimal getHalf(BigDecimal originPrice) {
        return originPrice.divide(new BigDecimal("2"), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将价格转换为long型的分
     *
     * @param price
     * @return
     */
    public static long getFen(BigDecimal price) {
        return price.longValue();
    }

    /**
     * 将Float元转为long型的分 不留小数位 四舍五入
     * 
     * @param yuan
     * @return
     */
    public static Long yuan2Fen(Float yuan) {
        if (yuan == null) {
            return 0L;
        }
        return yuan2Fen(new BigDecimal(yuan));
    }

    /**
     * 将元转为long型的分 不留小数位 四舍五入
     * 
     * @param yuan
     * @return
     */
    public static long yuan2Fen(BigDecimal yuan) {
        return yuan2Fen(yuan, 0, RoundingMode.HALF_UP);
    }

    /**
     * 将元转为long型的分 不留小数位 四舍五入
     * 
     * @param yuan
     * @return
     */
    public static long yuan2Fen(String yuan) {
        return yuan2Fen(new BigDecimal(yuan), 0, RoundingMode.HALF_UP);
    }

    /**
     * 将元转为long型的分
     * 
     * @param yuan
     * @param scale
     * @param roundingMode
     * @return
     */
    public static long yuan2Fen(BigDecimal yuan, int scale, RoundingMode roundingMode) {
        return yuan.multiply(BIGDECIMAL_HUNDRED).setScale(scale, roundingMode).longValue();
    }

    /**
     * 转换成¥前缀的元
     * 
     * @param fen
     * @return
     */
    public static String convertFen2Yuan(Long fen) {
        String yuan = fenToYuan(fen);
        if (!StringUtils.isEmpty(yuan)) {
            return YUAN_STR + yuan;
        }
        return yuan;
    }

    /**
     * 这里保留2未精度
     * 入参500，出参5
     * 
     * @param entryStr
     * @return
     */
    public static double longConvert2Double(long entryStr) {
        try {
            return BigDecimal.valueOf(entryStr).divide(
                BigDecimal.valueOf(100), 2,
                RoundingMode.HALF_UP).doubleValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0l;
    }

    public static void main(String[] args) {
        System.out.println(convertFen2Yuan(101l));
        System.out.println(getFen(new BigDecimal("123.22")));
        System.out.println(getFen(new BigDecimal("123.2533")));
        System.out.println(getFen(new BigDecimal("123.6533")));
        System.out.println(getFen(new BigDecimal("123")));
        System.out.println(getFen(new BigDecimal("123.00")));
        System.out.println(yuan2Fen(new BigDecimal("123")));
        System.out.println(yuan2Fen(new BigDecimal("123.00")));
        System.out.println(convertLong2Str(2300L));
        System.out.println(convertLong2Str(100L));
        System.out.println(formatPrice(0.00d));
        System.out.println(formatPrice("0.00"));
    }
}

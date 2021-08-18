package com.luna.common.xml;

/**
 * @author chenzhangyue@weidian.com
 * 2021/8/18
 */
public interface XmlConstant {
    /**
     * 字符串常量：XML 空格转义 {@code "&nbsp;" -> " "}
     */
    String NBSP           = "&nbsp;";

    /**
     * 字符串常量：XML And 符转义 {@code "&amp;" -> "&"}
     */
    String AMP            = "&amp;";

    /**
     * 字符串常量：XML 双引号转义 {@code "&quot;" -> "\""}
     */
    String QUOTE          = "&quot;";

    /**
     * 字符串常量：XML 单引号转义 {@code "&apos" -> "'"}
     */
    String APOS           = "&apos;";

    /**
     * 字符串常量：XML 小于号转义 {@code "&lt;" -> "<"}
     */
    String LT             = "&lt;";

    /**
     * 字符串常量：XML 大于号转义 {@code "&gt;" -> ">"}
     */
    String GT             = "&gt;";

    /**
     * 在XML中无效的字符 正则
     */
    String INVALID_REGEX  = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";
    /**
     * 在XML中注释的内容 正则
     */
    String COMMENT_REGEX  = "(?s)<!--.+?-->";
    /**
     * XML格式化输出默认缩进量
     */
    int    INDENT_DEFAULT = 2;
}

package com.luna.common.utils;
import java.util.regex.Pattern;

public final class CT{
    private static final long serialVersionUID = 3070917046443378762L;

    private static final Pattern PATTERN = Pattern.compile("^\\+\\d+_\\d+$");
    private String countryCode;
    private String telephone;

    private CT() {
    }

    public static CT build(String s) {
        return new CT(s);
    }

    public static CT build(String countryCode, String telephone) {
        return new CT(countryCode, telephone);
    }

    private CT(String s)  {
        if (null == s || !PATTERN.matcher(s).find()) {
            throw new RuntimeException("区域号不合法");
        }
        String[] split = s.split("_");
        this.setCountryCode(split[0].replaceFirst("\\+", ""));
        this.setTelephone(split[1]);
    }

    private CT(String countryCode, String telephone) {
        this.setCountryCode(countryCode);
        this.setTelephone(telephone);
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        assertIsNumber(countryCode);
        this.countryCode = countryCode;
    }


    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        assertIsNumber(telephone);
        this.telephone = telephone;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return !(!(obj instanceof CT))
                && this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return "+" + getCountryCode() + "_" + getTelephone();
    }

    private void assertIsNumber(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public enum CC {
        AL("355", "A", "阿尔巴尼亚", "F"),
        DZ("213", "A", "阿尔及利亚", "F"),
        AF("93", "A", "阿富汗", "F"),
        AR("54", "A", "阿根廷", "F"),
        IE("353", "A", "爱尔兰", "F"),
        EG("20", "A", "埃及", "F"),
        ET("251", "A", "埃塞俄比亚", "F"),
        EE("372", "A", "爱沙尼亚", "F"),
        AE("971", "A", "阿拉伯联合酋长国", "F"),
        AW("297", "A", "阿鲁巴", "F"),
        OM("968", "A", "阿曼", "F"),
        AD("376", "A", "安道尔", "F"),
        AO("244", "A", "安哥拉", "F"),
        AI("1264", "A", "安圭拉", "F"),
        AG("1268", "A", "安提瓜和巴布达", "F"),
        AU("61", "A", "澳大利亚", "F"),
        AT("43", "A", "奥地利", "F"),
        AZ("994", "A", "阿塞拜疆", "F"),
        AC("247", "A", "阿森松岛", "F"),
        BB("1246", "B", "巴巴多斯", "F"),
        PG("675", "B", "巴布亚新几内亚", "F"),
        BS("1242", "B", "巴哈马", "F"),
        BY("375", "B", "白俄罗斯", "F"),
        BM("1441", "B", "百慕大", "F"),
        PK("92", "B", "巴基斯坦", "F"),
        PY("595", "B", "巴拉圭", "F"),
        PS("970", "B", "巴勒斯坦", "F"),
        BH("973", "B", "巴林", "F"),
        PA("507", "B", "巴拿马", "F"),
        BG("359", "B", "保加利亚", "F"),
        BR("55", "B", "巴西", "F"),
        MP("1670", "B", "北马里亚纳群岛", "F"),
        BJ("229", "B", "贝宁", "F"),
        BE("32", "B", "比利时", "F"),
        PE("51", "B", "秘鲁", "F"),
        IS("354", "B", "冰岛", "F"),
        BW("267", "B", "博茨瓦纳", "F"),
        PR("1", "B", "波多黎各", "F"),
        PL("48", "B", "波兰", "F"),
        BO("591", "B", "玻利维亚", "F"),
        BZ("501", "B", "伯利兹", "F"),
        BA("387", "B", "波斯尼亚和黑塞哥维那", "F"),
        BT("975", "B", "不丹", "F"),
        BF("226", "B", "布基纳法索", "F"),
        BI("257", "B", "布隆迪", "F"),
        KP("850", "C", "朝鲜", "F"),
        GQ("240", "C", "赤道几内亚", "F"),
        DK("45", "D", "丹麦", "F"),
        DE("49", "D", "德国", "F"),
        TL("670", "D", "东帝汶", "F"),
        TG("228", "D", "多哥", "F"),
        DO("1767", "D", "多米尼加", "F"),
        DM("1809", "D", "多米尼加共和国", "F"),
        EC("593", "E", "厄瓜多尔", "F"),
        ER("291", "E", "厄立特里亚", "F"),
        RU("7", "E", "俄罗斯", "F"),
        FR("33", "F", "法国", "F"),
        FO("298", "F", "法罗群岛", "F"),
        VA("39", "F", "梵蒂冈", "F"),
        PF("689", "F", "法属波利尼西亚", "F"),
        GF("594", "F", "法属圭亚那", "F"),
        FJ("679", "F", "斐济", "F"),
        PH("63", "F", "菲律宾", "F"),
        FI("358", "F", "芬兰", "F"),
        CV("238", "F", "佛得角", "F"),
        GM("220", "G", "冈比亚", "F"),
        CG("242", "G", "刚果（布）", "F"),
        CD("243", "G", "刚果（金）", "F"),
        GG("44", "G", "根西岛", "F"),
        GL("299", "G", "格陵兰", "F"),
        GD("1473", "G", "格林纳达", "F"),
        GE("995", "G", "格鲁吉亚", "F"),
        CO("57", "G", "哥伦比亚", "F"),
        CR("506", "G", "哥斯达黎加", "F"),
        GP("590", "G", "瓜德罗普岛", "F"),
        GU("1671", "G", "关岛", "F"),
        CU("53", "G", "古巴", "F"),
        GY("592", "G", "圭亚那", "F"),
        HT("509", "H", "海地", "F"),
        KR("82", "H", "韩国", "F"),
        KZ("7", "H", "哈萨克斯坦", "F"),
        ME("382", "H", "黑山共和国", "F"),
        NL("31", "H", "荷兰", "F"),
        AN("599", "H", "荷属安的列斯群岛", "F"),
        HN("504", "H", "洪都拉斯", "F"),
        GH("233", "J", "加纳", "F"),
        CA("1", "J", "加拿大", "F"),
        KH("855", "J", "柬埔寨", "F"),
        GA("241", "J", "加蓬", "F"),
        DJ("253", "J", "吉布提", "F"),
        CZ("420", "J", "捷克共和国", "F"),
        KG("996", "J", "吉尔吉斯斯坦", "F"),
        KI("686", "J", "基里巴斯", "F"),
        GN("224", "J", "几内亚", "F"),
        GW("245", "J", "几内亚比绍", "F"),
        ZW("263", "J", "津巴布韦", "F"),
        KY("1345", "K", "开曼群岛", "F"),
        CM("237", "K", "喀麦隆", "F"),
        QA("974", "K", "卡塔尔", "F"),
        CC("61", "K", "科科斯（基林）群岛", "F"),
        HR("385", "K", "克罗地亚", "F"),
        KM("269", "K", "科摩罗", "F"),
        KE("254", "K", "肯尼亚", "F"),
        CI("225", "X", "科特迪沃", "F"),
        KW("965", "K", "科威特", "F"),
        CK("682", "K", "库克群岛", "F"),
        LS("266", "L", "莱索托", "F"),
        LO("856", "L", "老挝", "F"),
        LV("371", "L", "拉脱维亚", "F"),
        LB("961", "L", "黎巴嫩", "F"),
        LR("231", "L", "利比里亚", "F"),
        LY("218", "L", "利比亚", "F"),
        LI("423", "L", "列支敦士登", "F"),
        LT("370", "L", "立陶宛", "F"),
        RE("262", "L", "留尼汪岛", "F"),
        RO("40", "L", "罗马尼亚", "F"),
        LU("352", "L", "卢森堡", "F"),
        RW("250", "L", "卢旺达", "F"),
        MG("261", "M", "马达加斯加", "F"),
        IM("44", "M", "马恩岛", "F"),
        MV("960", "M", "马尔代夫", "F"),
        MT("356", "M", "马耳他", "F"),
        MY("60", "M", "马来西亚", "F"),
        MW("265", "M", "马拉维", "F"),
        ML("223", "M", "马里", "F"),
        MU("230", "M", "毛里求斯", "F"),
        MR("222", "M", "毛里塔尼亚", "F"),
        MK("389", "M", "马其顿", "F"),
        MH("692", "M", "马绍尔群岛", "F"),
        MQ("596", "M", "马提尼克岛", "F"),
        YT("262", "M", "马约特", "F"),
        US("1", "M", "美国", "T"),
        AS("1684", "M", "美属萨摩亚", "F"),
        VI("1340", "M", "美属维京群岛", "F"),
        MN("976", "M", "蒙古", "F"),
        BD("880", "M", "孟加拉", "F"),
        MS("1664", "M", "蒙特塞拉特", "F"),
        MM("95", "M", "缅甸", "F"),
        MF("691", "M", "密克罗尼西亚", "F"),
        MD("373", "M", "摩尔多瓦", "F"),
        MA("212", "M", "摩洛哥", "F"),
        MC("377", "M", "摩纳哥", "F"),
        MZ("258", "M", "莫桑比克", "F"),
        MX("52", "M", "墨西哥", "F"),
        NA("264", "N", "纳米比亚", "F"),
        ZA("27", "N", "南非", "F"),
        AQ("672", "N", "南极洲", "F"),
        NR("674", "N", "瑙鲁", "F"),
        NP("977", "N", "尼泊尔", "F"),
        NI("505", "N", "尼加拉瓜", "F"),
        NE("227", "N", "尼日尔", "F"),
        NG("234", "N", "尼日利亚", "F"),
        NU("683", "N", "纽埃", "F"),
        NF("672", "N", "诺福克岛", "F"),
        NO("47", "N", "挪威", "F"),
        PW("680", "P", "帕劳", "F"),
        PN("64", "P", "皮特凯恩群岛", "F"),
        PT("351", "P", "葡萄牙", "F"),
        JP("81", "R", "日本", "F"),
        SE("46", "R", "瑞典", "F"),
        CH("41", "R", "瑞士", "F"),
        SV("503", "S", "萨尔瓦多", "F"),
        RS("381", "S", "塞尔维亚", "F"),
        SL("232", "S", "塞拉利昂", "F"),
        SN("221", "S", "塞内加尔", "F"),
        CY("357", "S", "塞浦路斯", "F"),
        SC("248", "S", "塞舌尔", "F"),
        WS("685", "S", "萨摩亚", "F"),
        SA("966", "S", "沙特阿拉伯", "F"),
        PM("508", "S", "圣皮埃尔和密克隆群岛", "F"),
        ST("239", "S", "圣多美和普林西比", "F"),
        SH("290", "S", "圣赫勒拿", "F"),
        KN("1869", "S", "圣基茨和尼维斯", "F"),
        LC("1758", "S", "圣卢西亚", "F"),
        SM("378", "S", "圣马力诺", "F"),
        VC("1784", "S", "圣文森特和格林纳丁斯", "F"),
        LK("94", "S", "斯里兰卡", "F"),
        SK("421", "S", "斯洛伐克", "F"),
        SI("386", "S", "斯洛文尼亚", "F"),
        SJ("47", "S", "斯瓦尔巴特和扬马延岛", "F"),
        SZ("268", "S", "斯威士兰", "F"),
        SD("249", "S", "苏丹", "F"),
        SR("597", "S", "苏里南", "F"),
        SB("677", "S", "所罗门群岛", "F"),
        SO("252", "S", "索马里", "F"),
        TH("66", "T", "泰国", "F"),
        TJ("992", "T", "塔吉克斯坦", "F"),
        TO("676", "T", "汤加", "F"),
        TZ("255", "T", "坦桑尼亚", "F"),
        TC("1649", "T", "特克斯和凯科斯群岛", "F"),
        TT("1868", "T", "特立尼达和多巴哥", "F"),
        TR("90", "T", "土耳其", "F"),
        TM("993", "T", "土库曼斯坦", "F"),
        TN("216", "T", "突尼斯", "F"),
        TK("690", "T", "托克劳", "F"),
        TV("688", "T", "图瓦卢", "F"),
        WF("681", "W", "瓦里斯和福图纳群岛", "F"),
        VU("678", "W", "瓦努阿图", "F"),
        GT("502", "W", "危地马拉", "F"),
        VE("58", "W", "委内瑞拉", "F"),
        BN("673", "W", "文莱", "F"),
        UG("256", "W", "乌干达", "F"),
        UA("380", "W", "乌克兰", "F"),
        UY("598", "W", "乌拉圭", "F"),
        UZ("998", "W", "乌兹别克斯坦", "F"),
        ES("34", "X", "西班牙", "F"),
        GR("30", "X", "希腊", "F"),
        SG("65", "X", "新加坡", "F"),
        NC("687", "X", "新喀里多尼亚", "F"),
        NZ("64", "X", "新西兰", "F"),
        HU("36", "X", "匈牙利", "F"),
        EH("212", "X", "西撒哈拉", "F"),
        SY("963", "X", "叙利亚", "F"),
        JM("1876", "Y", "牙买加", "F"),
        AM("374", "Y", "亚美尼亚", "F"),
        YE("967", "Y", "也门", "F"),
        IT("39", "Y", "意大利", "F"),
        IQ("964", "Y", "伊拉克", "F"),
        IR("98", "Y", "伊朗", "F"),
        IN("91", "Y", "印度", "F"),
        ID("62", "Y", "印度尼西亚", "F"),
        UK("44", "Y", "英国", "F"),
        VG("1284", "Y", "英属维京群岛", "F"),
        IL("972", "Y", "以色列", "F"),
        JO("962", "Y", "约旦", "F"),
        VN("84", "Y", "越南", "F"),
        ZM("260", "Z", "赞比亚", "F"),
        JE("44", "Z", "泽西岛", "F"),
        TD("235", "Z", "乍得", "F"),
        GI("350", "Z", "直布罗陀", "F"),
        CL("56", "Z", "智利", "F"),
        CF("236", "Z", "中非共和国", "F"),
        CN("86", "Z", "中国大陆", "T"),
        TW("886", "Z", "中国台湾", "F"),
        MO("853", "Z", "中国澳门特别行政区", "F"),
        HK("852", "Z", "中国香港特别行政区", "T");

        private final String c;//国家码
        private final String i;//索引
        private final String n;//国家名
        private final String s;//是否常用

        CC(String c, String i, String n, String s) {
            this.c = c;
            this.i = i;
            this.n = n;
            this.s = s;
        }

        public String getC() {
            return c;
        }

        public String getI() {
            return i;
        }

        public String getN() {
            return n;
        }

        public String getS() {
            return s;
        }
    }
}

package com.luna.common.sensitive;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脱敏工具类
 * 通过配置Config实现手机号电话,QQ微信邮箱，地址等脱敏开关
 * 当Config全部是关闭状态时，调用不做任何操作
 */
public class SensitiveUtil<T> {

    private static final Pattern        PATTERN = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17([1-3]|[5-9]))|(18[0,5-9]))\\d{8}");

    /**
     * 需要脱敏的数据
     */
    private final T                           data;

    /**
     * 脱敏开关配置
     */
    private final Map<FieldType, FieldConfig> config;

    private SensitiveUtil(T data, Map<FieldType, FieldConfig> config) {
        this.data = data;
        this.config = config;
    }

    /**
     * 调用此方法进行脱敏
     *
     * @param data 需要脱敏的数据
     * @param config 脱敏配置
     * @param <T>
     * @throws Exception
     */
    public static <T> void apply(T data, Map<FieldType, FieldConfig> config) {
        new SensitiveUtil<T>(data, config).convert();
    }

    public static <T> void apply(T data) {
        new SensitiveUtil<T>(data, new HashMap<>()).convert();
    }

    /**
     * 不使用注解的方式脱敏
     *
     * @param data
     * @param config
     * @param fieldTypeFunction
     * @param <T>
     * @throws Exception
     */
    public static <T> void parse(T data, Map<FieldType, FieldConfig> config, Function<String, FieldType> fieldTypeFunction) {
        WithoutAnnotationUtil.create(data, config, fieldTypeFunction).convert();
    }

    public static <T> void parse(T data, Map<FieldType, FieldConfig> config) {
        WithoutAnnotationUtil.create(data, config, FieldType::parseFieldType).convert();
    }

    public static <T> void parse(T data, Function<String, FieldType> fieldTypeFunction) {
        WithoutAnnotationUtil.create(data, new HashMap<>(), fieldTypeFunction).convert();
    }

    public static <T> void parse(T data) {
        WithoutAnnotationUtil.create(data, new HashMap<>(), FieldType::parseFieldType).convert();
    }

    public static String regexReplaceTelPhone(String string) {
        Matcher matcher = PATTERN.matcher(string);
        String temp = string;
        while (matcher.find()) {
            String group = matcher.group();
            temp = temp.replace(group, Convert.mobile(group));
        }
        return temp;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String data =
            "{\"addTime\":1676269173000,\"attributes\":{\"cnl\":\"xiaochengxu\",\"1ys_c_fans_order\":\"y\",\"v_a_id\":\"extWxBuye\",\"1ns_c_must_online_pay_channel\":\"y\",\"sub_cnl\":\"thirdApp\",\"1ys_c_mall_order\":\"y\",\"1ys_c_fans_biztype\":\"1\",\"c_reward_type\":\"1\",\"1ys_c_mall_new_order\":\"y\",\"source\":\"CART\",\"c_mini_program_scene\":\"0\",\"c_ip\":\"112.3.205.244\",\"c_tax_type\":\"1547154012384894978\",\"page_cnl\":\"shop_menu_cart\",\"1yl_c_createid\":\"30314065413391\",\"c_uid\":\"1743008060\",\"1ys_c_settle_biz_flag\":\"fans\",\"1ys_c_fans1_id\":\"1580171905\",\"1ys_c_fans1_type\":\"1\",\"plat\":\"android\",\"1ns_c_support_express_insurance\":\"y\"},\"buyerAddress\":\"江苏省 苏州市 相城区 元和街道 魅力花园9-510\",\"buyerId\":\"1743008060\",\"buyerInfo\":{\"city\":\"苏州\",\"province\":\"江苏\"},\"buyerName\":\"庄庄\",\"buyerTelephone\":\"18816293764\",\"encryptOrderId\":\"9zp3a9lNTgVI1SF%2F0qznOg%3D%3D\",\"expressFee\":0,\"expressType\":0,\"extend\":\",,,,,,,,,,,,,,,,,,,cnl:xiaochengxu;1ys_c_fans_order:y;v_a_id:extWxBuye;1ns_c_must_online_pay_channel:y;sub_cnl:thirdApp;1ys_c_mall_order:y;1ys_c_fans_biztype:1;c_reward_type:1;1ys_c_mall_new_order:y;source:CART;c_mini_program_scene:0;c_ip:112.3.205.244;c_tax_type:1547154012384894978;page_cnl:shop_menu_cart;1yl_c_createid:30314065413391;c_uid:1743008060;1ys_c_settle_biz_flag:fans;1ys_c_fans1_id:1580171905;1ys_c_fans1_type:1;plat:android;1ns_c_support_express_insurance:y;,,,,,,,,,,\",\"fSellerId\":0,\"flag\":2,\"flagBin\":536870920,\"flagBinL\":2216739995656,\"fxFee\":0,\"imgHead\":\"https://si.geilicdn.com/pcitem1575265323-0d780000017e8664972d0a20e2c5_800_800.jpg?w=110&h=110&cp=1\",\"isShow\":1,\"orderCommissionDTOList\":[{\"bizType\":3,\"extend\":\"{\\\"commissionType\\\":1,\\\"delayMinutes\\\":21600,\\\"rate\\\":\\\"30.0\\\",\\\"reqNo\\\":\\\"712207271183\\\",\\\"roleType\\\":\\\"5\\\",\\\"taxRate\\\":\\\"0.069\\\"}\",\"extendObject\":{\"commissionType\":1,\"delayMinutes\":21600,\"rate\":\"30.0\",\"reqNo\":\"712207271183\",\"taxRate\":\"0.069\"},\"fee\":747,\"feeType\":30,\"feeTypeDesc\":\"商城推广员佣金\",\"fundAccount\":\"334544046\",\"gmtCreate\":1676269174000,\"gmtUpdate\":1676269191000,\"id\":50334,\"orderId\":820539069129999,\"refundFee\":0,\"status\":1,\"subOrderId\":630162500969743,\"userId\":\"1580171905\"},{\"bizType\":3,\"extend\":\"{\\\"commissionType\\\":1,\\\"delayMinutes\\\":21600,\\\"rate\\\":\\\"30.0\\\",\\\"reqNo\\\":\\\"712207271183\\\",\\\"roleType\\\":\\\"5\\\",\\\"taxRate\\\":\\\"0.069\\\"}\",\"extendObject\":{\"commissionType\":1,\"delayMinutes\":21600,\"rate\":\"30.0\",\"reqNo\":\"712207271183\",\"taxRate\":\"0.069\"},\"fee\":1797,\"feeType\":30,\"feeTypeDesc\":\"商城推广员佣金\",\"fundAccount\":\"334544046\",\"gmtCreate\":1676269174000,\"gmtUpdate\":1676269191000,\"id\":50333,\"orderId\":820539069129999,\"refundFee\":0,\"status\":1,\"subOrderId\":630170103153935,\"userId\":\"1580171905\"},{\"bizType\":3,\"extend\":\"{\\\"commissionType\\\":1,\\\"delayMinutes\\\":21600,\\\"rate\\\":\\\"30.0\\\",\\\"reqNo\\\":\\\"712207271183\\\",\\\"roleType\\\":\\\"5\\\",\\\"taxRate\\\":\\\"0.069\\\"}\",\"extendObject\":{\"commissionType\":1,\"delayMinutes\":21600,\"rate\":\"30.0\",\"reqNo\":\"712207271183\",\"taxRate\":\"0.069\"},\"fee\":897,\"feeType\":30,\"feeTypeDesc\":\"商城推广员佣金\",\"fundAccount\":\"334544046\",\"gmtCreate\":1676269174000,\"gmtUpdate\":1676269191000,\"id\":50335,\"orderId\":820539069129999,\"refundFee\":0,\"status\":1,\"subOrderId\":630170103170319,\"userId\":\"1580171905\"}],\"orderFlagInfo\":[\"粉丝推广订单\"],\"orderId\":820539069129999,\"orderSource\":0,\"orderStatus\":20,\"payTime\":1676269190000,\"refundStatus\":0,\"sellerId\":1871859650,\"sellerInfoDTOList\":[{\"id\":1871859650,\"type\":101}],\"sellerInfos\":[{\"id\":1871859650,\"type\":101}],\"subOrderInfoDTOs\":[{\"addTime\":1676269173000,\"attributes\":{\"orig_price\":\"59.9\",\"merchant_code\":\"01\",\"weight_orig_price\":\"59.9\",\"1ns_c_fans1_rate\":\"30.0\",\"1ns_c_fans1_money\":\"1797\",\"1ys_c_category_id\":\"123216051\",\"1yl_sx_cp_flag\":\"0\",\"1ns_c_member_price_bonus_type\":\"1\",\"stock_id\":\"1638259693847981\"},\"buyerId\":\"1743008060\",\"cpsFee\":0,\"id\":630170103153935,\"imgHead\":\"pcitem1575265323-0d780000017e8664972d0a20e2c5_800_800.jpg\",\"itemId\":4405257645,\"itemSkuId\":0,\"itemTitle\":\"邻芝艾灸宝 艾绒暖宫护腰热敷袋温灸理疗 多档控温 家用办公可用 无烟艾灸包邮\",\"orderId\":820539069129999,\"price\":5990,\"quantity\":1,\"refundStatus\":0,\"status\":0,\"totalPrice\":5990},{\"addTime\":1676269173000,\"attributes\":{\"orig_price\":\"24.9\",\"weight_orig_price\":\"24.9\",\"1ns_c_fans1_rate\":\"30.0\",\"1ns_c_fans1_money\":\"747\",\"1ys_c_category_id\":\"2102\",\"1yl_sx_cp_flag\":\"0\",\"1ns_c_member_price_bonus_type\":\"1\",\"stock_id\":\"1678371857106823\"},\"buyerId\":\"1743008060\",\"cpsFee\":0,\"id\":630162500969743,\"imgHead\":\"pcitem901649049667-3083000001850faa612f0a813276_800_800.jpg\",\"itemId\":4441106311,\"itemSkuId\":49581748508,\"itemSkuTitle\":\"艾草足贴（包装随机发）\",\"itemTitle\":\"邻芝艾草足贴  足部护理 养生足贴 包邮（50贴/盒）\",\"orderId\":820539069129999,\"price\":2490,\"quantity\":1,\"refundStatus\":0,\"status\":0,\"totalPrice\":2490},{\"addTime\":1676269173000,\"attributes\":{\"orig_price\":\"29.9\",\"weight_orig_price\":\"29.9\",\"1ns_c_fans1_rate\":\"30.0\",\"1ns_c_fans1_money\":\"897\",\"1ys_c_category_id\":\"2102\",\"1yl_sx_cp_flag\":\"0\",\"1ns_c_member_price_bonus_type\":\"1\",\"stock_id\":\"1657608251737024\"},\"buyerId\":\"1743008060\",\"cpsFee\":0,\"id\":630170103170319,\"imgHead\":\"pcitem901649049667-1e0f000001850f63eff30a231418_800_800.jpg\",\"itemId\":4441908160,\"itemSkuId\":0,\"itemTitle\":\"邻芝艾绒肚脐贴  暖宫健体 懒人保健神器 包邮（30粒，30张脐贴）\",\"orderId\":820539069129999,\"price\":2990,\"quantity\":1,\"refundStatus\":0,\"status\":0,\"totalPrice\":2990}],\"totalFee\":0,\"totalPrice\":11470,\"unPayCloseTime\":1676441973000,\"updateTime\":1676269191000}";
        for (int i = 0; i < 1; i++) {
            regexReplaceTelPhone(data);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    /**
     * 开始做脱敏工作
     */
    public void convert() {
        try {
            convert(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void convert(T data) throws Exception {
        if (data == null) {
            return;
        }
        if (data instanceof Collection) {
            Collection collection = (Collection)data;
            if (collection != null && !collection.isEmpty()) {
                for (Object item : collection) {
                    convert(item);
                }
            }
        } else if (data instanceof Map) {
            Map map = (Map)data;
            if (map != null && !map.isEmpty()) {
                for (Object key : map.keySet()) {
                    convert(map.get(key));
                }
            }
        } else {
            convertObject(data);
        }
    }

    private <T> void convertObject(T data) throws Exception {
        if (data == null) {
            return;
        }
        Class<? extends Object> clazz = data.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            DataMethods methods = null;
            // 放过基本数据类型
            if (field.getType().isPrimitive()) {
                continue;
            }
            if (field.getType().equals(String.class)) {
                // 判断字段是否被注解了
                Sensitive fieldType = field.getAnnotation(Sensitive.class);
                if (fieldType == null) {
                    continue;
                }
                methods = DataMethods.init(data, field);
                if (methods.isNull()) {
                    continue;
                }
                String value = (String)methods.getMethod.invoke(data);
                if (value != null && !value.isEmpty() && !value.trim().equals("")) {
                    FieldType type = fieldType.value();
                    methods.setMethod.invoke(data, type.convert(value, config.get(type)));
                }
            }
            // 判断字段是否是POJO对象或者Collection或Map
            if (Collection.class.isAssignableFrom(field.getType())) {
                methods = DataMethods.init(data, field);
                if (methods.isNull()) {
                    continue;
                }
                Collection collection = (Collection)methods.getMethod.invoke(data);
                if (collection != null && !collection.isEmpty()) {
                    for (Object item : collection) {
                        convert(item);
                    }
                }
            } else if (Map.class.isAssignableFrom(field.getType())) {
                methods = DataMethods.init(data, field);
                if (methods.isNull()) {
                    continue;
                }
                Map map = (Map)methods.getMethod.invoke(data);
                if (map != null && !map.isEmpty()) {
                    for (Object key : map.keySet()) {
                        convert(map.get(key));
                    }
                }
            } else if (!field.getType().getName().startsWith("java.")) {
                methods = DataMethods.init(data, field);
                if (methods.isNull()) {
                    continue;
                }
                Object obj = methods.getMethod.invoke(data);
                if (null != obj) {
                    convertObject(obj);
                }
            }
        }
    }

    private static class DataMethods {
        Method getMethod;
        Method setMethod;

        public static <T> DataMethods init(T data, Field field) {
            DataMethods dataMethods = new DataMethods();
            for (Method method : data.getClass().getMethods()) {
                method.setAccessible(true);
                if (!method.getName().startsWith("get") && !method.getName().startsWith("set")) {
                    continue;
                }
                if (method.getName().equalsIgnoreCase("get" + field.getName()) && method.getParameterCount() == 0) {
                    dataMethods.getMethod = method;
                }
                if (method.getName().equalsIgnoreCase("set" + field.getName()) && method.getParameterCount() == 1) {
                    dataMethods.setMethod = method;
                }
            }
            return dataMethods;
        }

        public boolean isNull() {
            return getMethod == null || setMethod == null;
        }
    }

    /**
     * 脱敏工具类,不使用注解的方式实现
     *
     * @param <T>
     */
    public static class WithoutAnnotationUtil<T> {

        // 需要脱敏的数据
        private final T                           data;

        // 脱敏开关配置
        private final Map<FieldType, FieldConfig> config;

        private final Function<String, FieldType> fieldTypeFunction;

        private WithoutAnnotationUtil(T data, Map<FieldType, FieldConfig> config, Function<String, FieldType> fieldTypeFunction) {
            this.data = data;
            this.fieldTypeFunction = fieldTypeFunction;
            this.config = config;
        }

        /**
         * 生成工具类
         *
         * @param data 需要脱敏的数据
         * @param config 脱敏开关配置
         * @param <T>
         * @return
         */
        public static <T> WithoutAnnotationUtil<T> create(T data, Map<FieldType, FieldConfig> config, Function<String, FieldType> fieldTypeFunction) {
            return new WithoutAnnotationUtil<T>(data, config, fieldTypeFunction);
        }

        public void convert() {
            try {
                convert(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private <T> void convert(T data) throws Exception {
            if (data == null) {
                return;
            }
            if (data instanceof Collection) {
                Collection collection = (Collection)data;
                if (!collection.isEmpty()) {
                    for (Object item : collection) {
                        convert(item);
                    }
                }
            } else if (data instanceof Map) {
                Map map = (Map)data;
                if (!map.isEmpty()) {
                    for (Object key : map.keySet()) {
                        Object value = map.get(key);
                        if (key instanceof String && value instanceof String) {
                            FieldType fieldType = fieldTypeFunction.apply((String)key);
                            if (fieldType != null) {
                                map.put(key, fieldType.convert((String)value, config.get(fieldType)));
                            }
                        } else {
                            convert(map.get(key));
                        }
                    }
                }
            } else {
                convertObject(data);
            }
        }

        /**
         * 脱敏一个POJO对象
         *
         * @param data 需要脱敏的数据
         * @param <T>
         * @throws Exception
         */
        private <T> void convertObject(T data) throws Exception {
            Class<? extends Object> clazz = data.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                boolean isZeroParameter = method.getParameterCount() == 0;
                boolean isGetMethod = method.getName().startsWith("get");
                boolean isReturnVoid = method.getReturnType().getName().equals("void");
                boolean isPrimitiveReturn = method.getReturnType().isPrimitive();
                if (isZeroParameter && isGetMethod && !isReturnVoid && !isPrimitiveReturn) {
                    if (method.getReturnType().equals(String.class)) {
                        FieldType fieldType = fieldTypeFunction.apply(method.getName().replace("get", "").toLowerCase());
                        if (fieldType != null) {
                            String value = (String)method.invoke(data);
                            if (value != null && !value.isEmpty()) {
                                Method setMethod = clazz.getMethod(method.getName().replace("get", "set"), String.class);
                                setMethod.invoke(data, fieldType.convert(value, config.get(fieldType)));
                            }
                        }
                    } else if (Collection.class.isAssignableFrom(method.getReturnType())) {
                        Collection collection = (Collection)method.invoke(data);
                        if (collection != null && !collection.isEmpty()) {
                            for (Object item : collection) {
                                convert(item);
                            }
                        }
                    } else if (Map.class.isAssignableFrom(method.getReturnType())) {
                        Map map = (Map)method.invoke(data);
                        if (map != null && !map.isEmpty()) {
                            for (Object key : map.keySet()) {
                                Object value = map.get(key);
                                if (key instanceof String && value instanceof String) {
                                    FieldType fieldType = fieldTypeFunction.apply((String)key);
                                    if (fieldType != null) {
                                        map.put(key, fieldType.convert((String)value, config.get(fieldType)));
                                    }
                                } else {
                                    convert(map.get(key));
                                }
                                convert(map.get(key));
                            }
                        }
                    } else if (method.getReturnType().getPackage() != null && !method.getReturnType().getPackage().getName().startsWith("java.")) {
                        Object obj = method.invoke(data);
                        if (null != obj) {
                            convertObject(obj);
                        }
                    }
                }
            }
        }
    }
}

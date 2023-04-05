package com.luna.common.sensitive;


import com.alibaba.fastjson2.JSON;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenzhangyue
 * 2023/2/8
 */
public class SensitiveTest {

    public static void notAnnoParse() {
        Person person = new Person();
        person.setName("洛空是");
        person.setMobile("18017648954");
        person.setAddress("浙江省杭州市余杭区知北路128号");

        Map<String, Object> params = new HashMap<>();
        params.put("bankcard", "62226043567892341234");
        params.put("idcard", "14010156783451234");
        person.setParams(params);
        // 脱敏

        SensitiveUtil.parse(person);
        System.out.println(JSON.toJSONString(person));
    }

    public static void immutablePojoApply() {
        // 这里有一个叫张三的人
        Person zhangsan = new Person();
        zhangsan.setName("张三");
        zhangsan.setMobile("18019295001");
        zhangsan.setPassword("PA23235454");
        zhangsan.setAddress("上海市松江区佘山镇");
        // 这里有一个叫李四的人
        Person lisi = new Person();
        lisi.setName("李四");
        lisi.setMobile("18018732893");
        lisi.setPassword("HAHAHAHAHA");
        lisi.setAddress("上海市嘉定区南翔镇");
        // 李四是张三的朋友
        zhangsan.setFriends(Arrays.asList(lisi));
        // 对张三进行脱敏
        // 脱敏配置
        Map<FieldType, FieldConfig> config = new HashMap<>();
        config.put(FieldType.CHINESE_NAME, new FieldConfig(false));
        config.put(FieldType.MOBILE, new FieldConfig(true));
        config.put(FieldType.ADDRESS, new FieldConfig(value -> "<地址被隐藏>"));
        // 执行脱敏方法
        SensitiveUtil.apply(zhangsan, config);
        System.out.println(JSON.toJSONString(zhangsan));
    }

    public static void main(String[] args) {
        immutablePojoApply();
    }

    public static class Person {

        @Sensitive(FieldType.CHINESE_NAME)
        private String name;
        @Sensitive(FieldType.MOBILE)
        private String mobile;
        @Sensitive(FieldType.ADDRESS)
        private String address;
        @Sensitive(FieldType.PASSWORD)
        private String password;

        private List<Person> friends;
        private Map<String, Object> params;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<Person> getFriends() {
            return friends;
        }

        public void setFriends(List<Person> friends) {
            this.friends = friends;
        }

    }
}

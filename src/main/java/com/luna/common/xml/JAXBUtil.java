package com.luna.common.xml;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import com.luna.common.file.FileTools;
import com.luna.common.io.IoUtil;
import com.luna.common.text.CharsetUtil;
import com.luna.common.text.StringTools;

/**
 * JAXB（Java Architecture for XML Binding），根据XML Schema产生Java对象，即实现xml和Bean互转。
 * <p>
 * 相关介绍：
 * <ul>
 * <li>https://www.cnblogs.com/yanghaolie/p/11110991.html</li>
 * <li>https://my.oschina.net/u/4266515/blog/3330113</li>
 * </ul>
 *
 * @author dazer
 * @see XmlUtil
 * @since 5.7.3
 */
public class JAXBUtil {

    /**
     * JavaBean转换成xml
     * <p>
     * bean上面用的常用注解
     *
     * @param bean Bean对象
     * @return 输出的XML字符串
     * @see XmlRootElement {@code @XmlRootElement(name = "school")}
     * @see XmlElement {@code @XmlElement(name = "school_name", required = true)}
     * @see XmlElementWrapper {@code @XmlElementWrapper(name="schools")}
     * @see XmlTransient JAXB "有两个名为 "**" 的属性,类的两个属性具有相同名称 "**""解决方案
     */
    public static String beanToXml(Object bean) {
        return beanToXml(bean, CharsetUtil.CHARSET_UTF_8, true);
    }

    /**
     * JavaBean转换成xml
     *
     * @param bean Bean对象
     * @param charset 编码 eg: utf-8
     * @param format 是否格式化输出eg: true
     * @return 输出的XML字符串
     */
    public static String beanToXml(Object bean, Charset charset, boolean format) {
        StringWriter writer;
        try {
            JAXBContext context = JAXBContext.newInstance(bean.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            writer = new StringWriter();
            marshaller.marshal(bean, writer);
        } catch (Exception e) {
            throw new RuntimeException("convertToXml 错误：" + e.getMessage(), e);
        }
        return writer.toString();
    }

    /**
     * xml转换成JavaBean
     *
     * @param <T> Bean类型
     * @param xml XML字符串
     * @param c Bean类型
     * @return bean
     */
    public static <T> T xmlToBean(String xml, Class<T> c) {
        return xmlToBean(StringTools.getReader(xml), c);
    }

    /**
     * XML文件转Bean
     *
     * @param file 文件
     * @param charset 编码
     * @param c Bean类
     * @param <T> Bean类型
     * @return Bean
     */
    public static <T> T xmlToBean(File file, Charset charset, Class<T> c) {
        return xmlToBean(FileTools.getReader(file, charset), c);
    }

    /**
     * 从{@link Reader}中读取XML字符串，并转换为Bean
     *
     * @param reader {@link Reader}
     * @param c Bean类
     * @param <T> Bean类型
     * @return Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(Reader reader, Class<T> c) {
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T)unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("convertToJava2 错误：" + e.getMessage(), e);
        } finally {
            IoUtil.close(reader);
        }
    }
}

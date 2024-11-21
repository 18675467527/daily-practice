package com.mk.webfluxtest.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiuAY
 * @createDate 2023/10/8 11:30
 */
public class JackJsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JackJsonUtil.class);

    private static ObjectMapper objectMapper;

    private static ObjectMapper objectMapper2;

    /**
     * 设置JSON对于的序列化方式（默认为忽略为null的属性）
     *
     * @param include JsonInclude.Include.ALWAYS：序列化所有属性，与属性的值无关。Jackson默认的序列化方式。
     *                JsonInclude.Include.NON_NULL：仅序列化非null的属性。
     *                JsonInclude.Include.NON_ABSENT：这个选项拥有 NON_NULL 的功能，主要作用于java.util.concurrent.atomic.AtomicReference和java.util.Optional等类型。如果AtomicReference/Optional对象的value属性为null，则对象不会被序列化
     *                JsonInclude.Include.NON_EMPTY：这个选项拥有 NON_ABSENT 选项的功能，同时还会判断属性值是否为空，如果为空，则属性不会被序列化。
     *                JsonInclude.Include.NON_DEFAULT：这个选项拥有 NON_EMPTY 选项的功能，同时还会判断属性值是否是默认值，如果是默认值，则属性不会被序列化。
     *                JsonInclude.Include.CUSTOM：自定义序列化规则，通过自定义的过滤器控制Jackson的序列化行为。
     *                JsonInclude.Include.USE_DEFAULTS：如果一个属性被配置了多种序列化方式，那么向上找上一级配置的序列化方式。
     */
    static {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            // 处理序列化过程中不去除小数位0
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            JsonNodeFactory jsonNodeFactory = JsonNodeFactory.withExactBigDecimals(true);
            objectMapper.setNodeFactory(jsonNodeFactory);
            // 忽略json多余的属性
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 忽略value为null的属性
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        if (objectMapper2 == null) {
            objectMapper2 = new ObjectMapper();
            // 处理序列化过程中不去除小数位0
            objectMapper2.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            JsonNodeFactory jsonNodeFactory = JsonNodeFactory.withExactBigDecimals(true);
            objectMapper2.setNodeFactory(jsonNodeFactory);
            // 忽略json多余的属性
            objectMapper2.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 不忽略value为null的属性
            objectMapper2.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        }
    }

    /**
     * 字符串转换为对象
     *
     * @param str json字符串
     * @return 对象
     */
    public static <T> T jsonToObject(String str, Class<T> cla) {
        try {
            return objectMapper.readValue(str, cla);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为集合
     *
     * @param str json字符串
     * @return 集合
     */
    public static <T> List<T> jsonToList(String str, Class<T> cla) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, cla);
            return objectMapper.readValue(str, javaType);
        } catch (JsonProcessingException e) {
            log.error("字符串反序列化为集合解析错误：", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为JsonNode
     *
     * @param str json字符串
     * @return JsonNode
     */
    public static JsonNode toJson(String str) {
        try {
            return objectMapper.readTree(str);
        } catch (JsonProcessingException e) {
            log.error("字符串反序列化为JSON解析错误：", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列化为字符串，忽略空值
     *
     * @param obj 序列化对象
     * @return 字符串
     */
    public static String jsonToString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化为String错误：", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列化为字符串，不忽略空值
     *
     * @param obj 序列化对象
     * @return 字符串
     */
    public static String jsonToStringAlways(Object obj) {
        try {
            return objectMapper2.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化为String错误：", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否为对象
     *
     * @param str json字符串
     * @return boolean
     */
    public static boolean isObj(String str) {
        if (!StringUtil.isNullOrEmpty(str)) {
            str = str.trim();
        }
        return str.startsWith("{") && str.endsWith("}");
    }

    /**
     * 判断是否为集合
     *
     * @param str json字符串
     * @return boolean
     */
    public static boolean isArray(String str) {
        if (!StringUtil.isNullOrEmpty(str)) {
            str = str.trim();
        }
        return str.startsWith("[") && str.endsWith("]");
    }

    public static ObjectNode newsObjectNode() {
        return objectMapper.createObjectNode();
    }


    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", new ArrayList<>());
        map.put("pageSize", null);
        map.put("total", 0);
        /*RequestInfo<Object> objectRequestInfo = new RequestInfo<>();
        objectRequestInfo.setRequestParam(map);
        objectRequestInfo.setCallTime(new Date());
     *//*   List list = new ArrayList();
        list.add(map);
        PageInfo<Object> objectPageInfo = new PageInfo<>(list);*//*
        String s = JackJsonUtil.jsonToStringAlways(objectRequestInfo);
        RequestInfo requestInfo = JackJsonUtil.jsonToObject(s, RequestInfo.class);
        System.out.println(requestInfo.getRequestParam());*/
       /* JsonNode jsonNode = JackJsonUtil.toJson(s);
        System.out.println(null != jsonNode.get("list") && jsonNode.get("list").isArray() && jsonNode.get("total") != null );*/


    }
}

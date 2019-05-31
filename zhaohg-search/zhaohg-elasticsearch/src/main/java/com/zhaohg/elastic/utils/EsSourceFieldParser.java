package com.zhaohg.elastic.utils;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: elastic
 * @description: ES中 source 提取出 字段内容并转换类型 工具类
 * @author: zhaohg
 * @create: 2018-08-22 01:42
 **/
public class EsSourceFieldParser {
    public static Gson gson = new Gson();

    /**
     * object 类型转为 List
     */
    public static List<String> matToList(Map<String, Object> source, String fieldName) {
        Object value = source.get(fieldName);
        if (value == null) {
            return null;
        }
        return (ArrayList) value;
    }

    /**
     * object 类型转为 Date
     */
    public static Date mapToDate(Map<String, Object> source, String fieldName, SimpleDateFormat dateFormater) {
        Object value = source.get(fieldName);
        if (value != null) {
            try {
                return dateFormater.parse(String.valueOf(value));
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * object 类型转为 Integer
     */
    public static Integer mapToInteger(Map<String, Object> source, String fieldName) {
        Object value = source.get(fieldName);
        if (value != null) {
            try {
                return (Integer) value;
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * object 类型转为 String
     */
    public static String mapToString(Map<String, Object> source, String fieldName) {
        Object value = source.get(fieldName);
        return value == null ? null : String.valueOf(value);
    }

    public static Object map(Map<String, Object> source, String fieldName) {
        // key 不存在返回 null
        return source.get(fieldName);
    }

}

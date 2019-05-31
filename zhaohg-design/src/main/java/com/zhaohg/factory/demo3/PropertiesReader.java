package com.zhaohg.factory.demo3;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * properties文件的读取工具
 * @author Administrator
 */
class PropertiesReader {


    public Map<String, String> getProperties() {

        Properties props = new Properties();
        Map<String, String> map = new HashMap<>();
        try {

            InputStream in = this.getClass().getResourceAsStream("type.properties");
            props.load(in);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                map.put(key, property);
//				System.out.println(key + "  " + property);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}


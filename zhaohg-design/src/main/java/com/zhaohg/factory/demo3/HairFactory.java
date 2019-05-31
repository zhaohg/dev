package com.zhaohg.factory.demo3;

import java.util.Map;

/**
 * 发型工厂
 * @author Administrator
 */
public class HairFactory {

    /**
     * 根据类型来创建对象
     * @param key
     * @return
     */
    public HairInterface getHair(String key) {
        if ("left".equals(key)) {
            return new LeftHair();
        } else if ("right".equals(key)) {
            return new RightHair();
        }
        return null;
    }

    /**
     * 根据类的名称来生产对象
     * @param className
     * @return
     */
    public HairInterface getHairByClass(String className) {

        try {
            HairInterface hair = (HairInterface) Class.forName(className).newInstance();
            return hair;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据类的名称来生产对象
     * @param key
     * @return
     */
    public HairInterface getHairByClassKey(String key) {

        try {
            Map<String, String> map = new PropertiesReader().getProperties();

            HairInterface hair = (HairInterface) Class.forName(map.get(key)).newInstance();
            return hair;
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

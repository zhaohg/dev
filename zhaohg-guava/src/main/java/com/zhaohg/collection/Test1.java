package com.zhaohg.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: guava
 * @description:
 * @author: zhaohg
 * @create: 2018-08-30 17:07
 **/
public class Test1 {
    public static void main(String[] args) {
        guavaCollection();
        guavaImmutable();

        ImmutableList<String> immutableList = ImmutableList.of("1", "2", "3", "4");
        System.out.println(immutableList);
    }

    /**
     * 普通Collection的创建
     */
    public static void guavaCollection() {
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        Map<String, String> map = Maps.newHashMap();
    }

    /**
     * 不变Collection的创建
     */
    public static void guavaImmutable() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");
        ImmutableSet<String> set = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> map = ImmutableMap.of("k1", "v1", "k2", "v2");
    }
}

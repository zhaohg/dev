package com.zhaohg.leetcode.easy;

/**
 * Function:
 * @author zhaohg
 * Date: 07/12/2018 17:00
 * @since JDK 1.8
 */
public class ListNode {
    /**
     * 当前值
     */
    int currentVal;
    
    /**
     * 下一个节点
     */
    ListNode next;
    
    ListNode(int val) {
        currentVal = val;
    }
    
    @Override
    public String toString() {
        return "ListNode{" +
                "currentVal=" + currentVal +
                ", next=" + next +
                '}';
    }
}

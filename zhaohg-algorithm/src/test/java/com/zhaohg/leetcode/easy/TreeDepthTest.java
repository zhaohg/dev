package com.zhaohg.leetcode.easy;

import org.junit.Assert;
import org.junit.Test;

public class TreeDepthTest {
    @Test
    public void depth() throws Exception {

        TreeDepth.BinaryNode node = new TreeDepth.BinaryNode("1", null, null);
        TreeDepth.BinaryNode left2 = new TreeDepth.BinaryNode("2", null, null);
        TreeDepth.BinaryNode left3 = new TreeDepth.BinaryNode("3", null, null);
        TreeDepth.BinaryNode left4 = new TreeDepth.BinaryNode("4", null, null);
        TreeDepth.BinaryNode left5 = new TreeDepth.BinaryNode("5", null, null);
        TreeDepth.BinaryNode left6 = new TreeDepth.BinaryNode("6", null, null);

        node.setLeft(left2);
        node.setRight(left3);
        left2.setLeft(left4);
        left4.setLeft(left6);
        left3.setLeft(left5);

        TreeDepth treeDepth = new TreeDepth();
        int depth = treeDepth.depth(node);
        Assert.assertEquals(4, depth);

    }

}
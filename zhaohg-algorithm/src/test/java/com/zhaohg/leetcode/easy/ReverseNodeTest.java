package com.zhaohg.leetcode.easy;

import org.junit.Test;

public class ReverseNodeTest {
    @Test
    public void reverseNode1() throws Exception {
        ReverseNode.Node<String> node4 = new ReverseNode.Node<>("4", null);
        ReverseNode.Node<String> node3 = new ReverseNode.Node<>("3", node4);
        ReverseNode.Node<String> node2 = new ReverseNode.Node<>("2", node3);
        ReverseNode.Node<String> node1 = new ReverseNode.Node("1", node2);

        ReverseNode reverseNode = new ReverseNode();
        reverseNode.reverseNode1(node1);
    }

    @Test
    public void reverseNode12() throws Exception {

        ReverseNode.Node<String> node1 = new ReverseNode.Node("1", null);

        ReverseNode reverseNode = new ReverseNode();
        reverseNode.reverseNode1(node1);
    }

    @Test
    public void reverseNode13() throws Exception {

        ReverseNode.Node<String> node1 = null;

        ReverseNode reverseNode = new ReverseNode();
        reverseNode.reverseNode1(node1);
    }


    /**
     * 头插法
     * @throws Exception
     */
    @Test
    public void reverseHead21() throws Exception {
        ReverseNode.Node<String> node4 = new ReverseNode.Node<>("4", null);
        ReverseNode.Node<String> node3 = new ReverseNode.Node<>("3", node4);
        ReverseNode.Node<String> node2 = new ReverseNode.Node<>("2", node3);
        ReverseNode.Node<String> node1 = new ReverseNode.Node("1", node2);

        ReverseNode reverseNode = new ReverseNode();
        reverseNode.reverseNode(node1);

    }

    /**
     * 头插法
     * @throws Exception
     */
    @Test
    public void reverseHead22() throws Exception {
        ReverseNode reverseNode = new ReverseNode();
        reverseNode.reverseNode(null);

    }


    @Test
    public void recNodeTest31() {
        ReverseNode.Node<String> node4 = new ReverseNode.Node<>("4", null);
        ReverseNode.Node<String> node3 = new ReverseNode.Node<>("3", node4);
        ReverseNode.Node<String> node2 = new ReverseNode.Node<>("2", node3);
        ReverseNode.Node<String> node1 = new ReverseNode.Node("1", node2);

        ReverseNode reverseNode = new ReverseNode();
        reverseNode.recNode(node1);
    }

    @Test
    public void recNodeTest32() {
        ReverseNode reverseNode = new ReverseNode();
        reverseNode.recNode(null);
    }

}
package com.zhaohg;

/**
 * Created by zhaohg on 2017/8/21.
 * 单向链表的实现
 */
public class NodeList<E> {

    /**
     * 首元素的引用
     **/
    private Node<E> head;
    /**
     * 尾元素的引用
     **/
    private Node<E> last;
    /**
     * 其他元素的引用
     **/
    private Node<E> other;
    /**
     * list的元素个数
     **/
    private int     size = 0;

    /**
     * 默认构造
     */
    public NodeList() {
        head = null;
        last = head;
    }

    /**
     * 构造一个有参数的list
     * @param data 初始化参数
     */
    public NodeList(E data) {
        Node<E> d = new Node<>(data, null);
        head = d;
        last = head;
        size++;
    }

    public static void main(String[] args) {
        NodeList<String> nodeList = new NodeList<>("first");
        nodeList.add("a");
        nodeList.add("b");
        nodeList.add("c");
        nodeList.print();

        nodeList.add(2, "addNew");
        nodeList.print();

        nodeList.set("c", "C");
        nodeList.print();

        nodeList.addAfter("addNew", "addNew2");
        nodeList.print();

        System.out.println(nodeList.get(2));

        nodeList.remove("addNew2");
        nodeList.print();
        nodeList.remove("first");
        nodeList.print();

        System.out.println(nodeList.contains("a"));
        System.out.println(nodeList.contains("A"));

        nodeList.clear();
        nodeList.print();

    }

    /**
     * 增加一个数据到list中
     * @param data 新增的数据
     */
    public void add(E data) {
        Node<E> d = new Node<>(data, null);
        if (isEmpty()) {
            head = d;
            last = head;
        } else {
            last.next = d;
            last = d;
        }
        size++;
    }

    /**
     * 在index处添加元素data,其他元素后移
     * @param index 元素索引,从0开始.
     * @param data 新增元素
     */
    public void add(int index, E data) {
        checkIndex(index);

        other = head;
        if (index == 0) {   //如果加在第一个,就把head的引用设为新元素对象
            Node<E> d = new Node<>(data, other);
            head = d;
        } else {
            for (int i = 0; i < index; i++) {
                other = other.next;
            }
            Node<E> d = new Node<>(data, other.next);
            other.next = d;
        }
        size++;
    }

    /**
     * 替换原有的值为newValue
     * @param oldValue 旧值
     * @param newValue 新值
     * @return 如果替换成功返回true, 否则返回false
     */
    public boolean set(E oldValue, E newValue) {
        other = head;
        while (other != null) {
            if (other.data.equals(oldValue)) {
                other.data = newValue;
                return true;
            }
            other = other.next;
        }
        return false;
    }

    /**
     * 在指定的值后面插入一个新值
     * @param specidiedData 指定的值
     * @param newData 新值
     * @return 如果插入成功返回true, 否则返回false
     */
    public boolean addAfter(E specidiedData, E newData) {
        other = head;
        while (other != null) {
            if (other.data.equals(specidiedData)) {
                Node<E> d = new Node<>(newData, other.next);
                other.next = d;
                size++;
                return true;
            }
            other = other.next;
        }
        return false;
    }

    /**
     * 根据索引, 获得此处的数据
     * @param index 索引
     * @return 获得的数据
     */
    public E get(int index) {
        checkIndex(index);
        other = head;
        for (int i = 0; i < index; i++) {
            other = other.next;
        }
        return other.data;
    }

    /**
     * 删除list中存在的data
     * @param data 要删除的数据
     * @return 如果删除成功返回true, 否则返回false
     */
    public boolean remove(E data) {
        other = head;
        Node<E> pre = other;
        for (int i = 0; i < size; i++) {
            if (other.data.equals(data)) {
                if (i == 0) {
                    head = other.next;
                    return true;
                }
                pre.next = other.next;
                return true;
            }
            pre = other;
            other = other.next;
        }
        return false;
    }

    /**
     * 是否包含传入的元素
     * @param data 传入的数据
     * @return 如果存在返回true, 否则false
     */
    public boolean contains(E data) {
        other = head;
        for (int i = 0; i < size; i++) {
            if (other.data.equals(data)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmpty() {
        return size == 0 || head == null;
    }

    /**
     * 清空链表
     */
    public void clear() {
        head = null;
        size = 0;
    }

    public void checkIndex(int index) {
        if (index < 0 || index > size - 1)
            throw new IndexOutOfBoundsException("传入的索引无效: " + index);
    }

    /**
     * 打印全部链表数据
     */
    public void print() {
        if (head == null || size == 0)
            System.out.println("链表为空");
        else {
            other = head;
            while (other != null) {
                System.out.print(other.data + " ");
                other = other.next;
            }
            System.out.println();
        }
    }

    private static class Node<E> {
        E       data;
        Node<E> next;

        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

}

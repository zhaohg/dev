package com.zhaohg;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Created by zhaohg on 2017/8/21.
 * <p>
 * 链式结构的另一种实现就是双向链表。
 * 双向链表维护两个引用：一个指向链表的第一个结点，另一个指向最后一个结点。链表中的每个结点都存储两个引用：一个指向下一个结点，另一个指向前一个结点。
 * 下面是我仿着LinkedList写的双向链表。自己写完后发现很多地方原作者写的真是精妙，把增加元素、移除元素抽离出来，
 * 以及写法的精简、效率的考虑（利用索引构造结点处判断index是否小于size>>1）
 */

public class LinkedList<E> implements Serializable {

    private static final long    serialVersionUID = -5635851059340344485L;
    /**
     * 第一个结点的引用
     **/
    transient            Node<E> first;
    /**
     * 最后一个结点的引用
     **/
    transient            Node<E> last;
    /**
     * list中元素的数目
     **/
    transient            int     size             = 0;
    /**
     * 操作次数
     **/
    transient            int     modCount         = 0;

    public LinkedList() {
    }

    /**
     * 把传入的元素变为第一个元素
     * @param e
     */
    private void linkFirst(E e) {
        Node<E> f = first;
        Node<E> newNode = new Node<>(null, e, f);
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        first = newNode;
        size++;
        modCount++;
    }

    /**
     * 在最后面插入元素
     * @param e
     */
    private void linkLast(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<>(l, e, null);
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        last = newNode;
        size++;
        modCount++;
    }

    /**
     * 在指定结点之前插入元素
     * @param e
     * @param succ 指定结点
     */
    private void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 取消第一个结点的引用
     */
    private E unlinkFirst(Node<E> f) {
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null;
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 取消最后一个结点的引用
     * @param l
     * @return 最后一个结点的值
     */
    private E unlinkLast(Node<E> l) {
        final E element = l.item;
        final Node<E> pred = l.prev;
        l.item = null;
        l.prev = null;
        last = pred;
        if (pred == null)
            first = null;
        else
            pred.next = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 取消结点x的引用
     * @param x
     * @return 取消的结点元素
     */
    E unlink(Node<E> x) {
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> pred = x.prev;

        // 如果是第一个
        if (pred == null)
            first = next;
        else {
            pred.next = next;
            x.prev = null;
        }

        //如果是最后一个
        if (next == null)
            last = pred;
        else {
            next.prev = pred;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 取得第一个元素
     * @return 第一个元素
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * 取得最后一个元素
     * @return 最后一个元素
     */
    public E getLast() {
        final Node<E> l = last;
        if (last == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * 删除第一个元素
     * @return 第一个被删除的元素
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * 删除最后一个元素
     * @return 最后一个被删除的元素
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    /**
     * 增加一个元素到list的第一个位置
     * @param e
     */
    public void addFirst(E e) {
        linkFirst(e);
    }

    /**
     * 增加一个元素到list的结尾
     * @param e
     */
    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * 增加一个元素(默认增加在结尾)
     * @param e
     * @return true
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * 删除list中存在传入的对象
     * @param o
     * @return 如果改变了list, 返回true, 否则false
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 清空list,所有引用对象置为null
     */
    public void clear() {
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.prev = null;
            x.next = null;
            x = next;
        }
        first = null;
        last = null;
        size = 0;
        modCount++;
    }

    /**
     * 获取索引处的元素值
     * @param index 索引
     * @return 元素值
     */
    public E get(int index) {
        checkIndex(index);
        return node(index).item;
    }

    /**
     * 替换索引处的值为element
     * @param index 索引
     * @param element 新值
     * @return 旧值
     */
    public E set(int index, E element) {
        checkIndex(index);
        Node<E> oldNode = node(index);
        E oldValue = oldNode.item;
        oldNode.item = element;
        return oldValue;
    }

    /**
     * 在指定索引的地方插入元素element,原来的元素以及之后的元素后移
     * @param index 插入元素的索引
     * @param element 插入的元素
     */
    public void add(int index, E element) {
        checkIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    /**
     * 移除索引处的元素
     * @param index 索引
     * @return 删除的元素
     */
    public E remove(int index) {
        checkIndex(index);
        return unlink(node(index));
    }

    /**
     * 获取对象在list中的索引
     * @param o 要查找的对象
     * @return 如果找到了对象, 返回对应的索引值, 否则返回-1
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * 是否包含元素
     * @param o
     * @return 如果包含返回true
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * 返回list中元素的大小
     * @return 元素的大小
     */
    public int getSize() {
        return size;
    }

    /**
     * 根据索引得到结点
     * @param index 索引
     * @return 结点
     */
    Node<E> node(int index) {

        // 如果是大小的一半之前,正序查找,否则倒序查找,提高效率
        if (index < (size >> 1)) {
            Node<E> f = first;
            for (int i = 0; i < index; i++)
                f = f.next;
            return f;
        } else {
            Node<E> l = last;
            for (int i = size - 1; i > index; i--)
                l = l.prev;
            return l;
        }
    }

    /**
     * 检查索引是否正确,不正确抛出 IndexOutOfBoundsException 异常
     * @param index
     */
    private void checkIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 越界的错误信息
     * @param index 越界的错误索引
     * @return 越界的msg
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    /**
     * 检查索引是否没有溢出
     * @param index 索引
     * @return 如果索引正确返回true
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * 结点类
     * @param <E>
     */
    private static class Node<E> {
        E       item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }
}
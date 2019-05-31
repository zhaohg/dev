package com.zhaohg.tree;

import sun.jvm.hotspot.utilities.RBNode;
import sun.jvm.hotspot.utilities.RBTree;

import java.util.Comparator;
import java.util.Random;

/**
 * Created by zhaohg on 2017/2/8.
 */
public class Test {

    public static void main(String[] args) {
//        List list = Collections.synchronizedList(new ArrayList<>());

        short treeSize = 10;
        short maxVal = treeSize;
        System.err.println("Building tree...");
        RBTree tree = new RBTree(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i1.intValue() < i2.intValue() ? -1 : (i1.intValue() == i2.intValue() ? 0 : 1);
            }
        });
        Random rand = new Random(System.currentTimeMillis());

        int i;
        for (i = 0; i < treeSize; ++i) {
            Integer xParent = new Integer(rand.nextInt(maxVal) + 1);

            try {
                tree.insertNode(new RBNode(xParent));
                if (i > 0 && i % 10 == 0) {
                    System.err.print(i + "...");
                    System.err.flush();
                }
            } catch (Exception var11) {
                var11.printStackTrace();
                System.err.println("While inserting value " + xParent);
                tree.printOn(System.err);
                System.exit(1);
            }
        }

        System.err.println();
        System.err.println("Churning tree...");

        for (i = 0; i < treeSize; ++i) {
            System.err.println("Iteration " + i + ":");
            tree.printOn(System.err);
            RBNode var12 = null;
            RBNode x = tree.getRoot();

            int depth;
            for (depth = 0; x != null; ++depth) {
                var12 = x;
                if (rand.nextBoolean()) {
                    x = x.getLeft();
                } else {
                    x = x.getRight();
                }
            }

            int height = rand.nextInt(depth);
            if (height >= depth) {
                throw new RuntimeException("bug in java.util.Random");
            }

            while (height > 0) {
                var12 = var12.getParent();
                --height;
            }

//            System.err.println("(Removing value " + tree.getNodeValue(var12) + ")");
            tree.deleteNode(var12);
            Integer newVal = rand.nextInt(maxVal) + 1;
            System.err.println("(Inserting value " + newVal + ")");
            tree.insertNode(new RBNode(newVal));
        }

        System.err.println("All tests passed.");
    }
}

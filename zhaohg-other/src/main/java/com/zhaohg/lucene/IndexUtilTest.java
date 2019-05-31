package com.zhaohg.lucene;

import org.junit.Test;

public class IndexUtilTest {

    @Test
    public void create() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.create(true);
    }

    @Test
    public void query() {
        IndexUtil indexUtil = new IndexUtil();
        String querystr = "java";
        indexUtil.query(querystr);
    }

    @Test
    public void delete01() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.delete01();
    }

//	@NoteTest
//	public void undelete(){
//		IndexUtil indexUtil = new IndexUtil();
//		indexUtil.undelete();
//	}

    @Test
    public void delete02() {
        IndexUtil indexUtil = new IndexUtil();
//		indexUtil.delete02();
    }

    @Test
    public void recover() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.recover();
    }

    @Test
    public void forceMergeDeletes() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.forceMergeDeletes();
    }


    @Test
    public void merge() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.merge();
    }

    @Test
    public void update() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.update();
    }

    @Test
    public void search01() {
        IndexUtil indexUtil = new IndexUtil();
        indexUtil.search01();
    }

    @Test
    public void search02() {
        IndexUtil indexUtil = new IndexUtil();
        String querystr = "like";
        indexUtil.search02(querystr);
    }
}

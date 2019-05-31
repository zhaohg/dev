package com.zhaohg.lucene.demo2;

import java.util.List;

public class QueryResult {
    private int  count;
    private List list;
    
    public QueryResult() {
        super();
    }
    
    public QueryResult(int count, List list) {
        super();
        this.count = count;
        this.list = list;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public List getList() {
        return list;
    }
    
    public void setList(List list) {
        this.list = list;
    }
}
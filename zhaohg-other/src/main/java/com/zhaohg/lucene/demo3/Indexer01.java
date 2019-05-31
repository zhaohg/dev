package com.zhaohg.lucene.demo3;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class Indexer01 {
    
    private Integer ids[]   = {1, 2, 3};
    private String  citys[] = {"青岛", "南京", "上海"};
    private String  descs[] = {
            "青岛是一个漂亮的城市。",
            "南京是一个文化的城市。",
            "上海是一个繁华的城市。"
    };
    
    private Directory dir;
    
    public static void main(String[] args) throws Exception {
        
        new Indexer01().index("/Users/zhaohg/dic/demo3");
        System.out.println("Success Indexer");
    }
    
    /**
     * 实例化indexerWriter
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() throws Exception {
        
        //中文分词器
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        
        IndexWriter writer = new IndexWriter(dir, iwc);
        
        return writer;
    }
    
    /**
     * 获取indexDir
     * @param indexDir
     * @throws Exception
     */
    private void index(String indexDir) throws Exception {
        
        dir = FSDirectory.open(Paths.get(indexDir));
        
        IndexWriter writer = getWriter();
        
        for (int i = 0; i < ids.length; i++) {
            
            Document doc = new Document();
            
            doc.add(new TextField("id", Integer.toString(ids[i]), Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.YES));
            
            writer.addDocument(doc);
        }
        
        writer.close();
    }
    
}

package com.zhaohg.lucene.demo3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;


/**
 * 文档加权
 * 1、加权操作：给要迅速查找的东西加上权，可提升查找速度！
 */

public class AddDocumentReg {
    
    //写测试数据，这些数据是写到索引文档里去的。
    private String ids[]      = {"1", "2", "3", "4"}; //标示文档
    private String author[]   = {"Jack", "Mary", "Jerry", "Machech"};
    private String title[]    = {"java of china", "Apple of china", "Androw of apple the USA", "People of Apple java"}; //
    private String contents[] = {
            "java  of China!the world the why what",
            "why a dertity compante is my hometown!",
            "Jdekia ssde hhh is a beautiful city!",
            "Jdekia ssde hhh is a beautiful java!"
    };
    
    private Directory dir;
    
    /**
     * 获取IndexWriter实例
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() throws Exception {
        
        //实例化分析器
        Analyzer analyzer = new StandardAnalyzer();
        
        //实例化IndexWriterConfig
        IndexWriterConfig con = new IndexWriterConfig(analyzer);
        
        //实例化IndexWriter
        IndexWriter writer = new IndexWriter(dir, con);
        
        return writer;
    }
    
    /**
     * 生成索引（对应图一）
     * @throws Exception
     */
    @Test
    public void index() throws Exception {
        
        dir = FSDirectory.open(Paths.get("/Users/zhaohg/dic/demo3"));
        
        IndexWriter writer = getWriter();
        
        for (int i = 0; i < ids.length; i++) {
            
            Document doc = new Document();
            
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("author", author[i], Field.Store.YES));
            // 加权操作
            TextField field = new TextField("title", title[i], Field.Store.YES);
            
            if ("Mary".equals(author[i])) {
                
                //设权 默认为1
                field.setBoost(1.5f);
            }
            
            doc.add(field);
            doc.add(new StringField("contents", contents[i], Field.Store.NO));
            
            // 添加文档
            writer.addDocument(doc);
        }
        
        //关闭writer
        writer.close();
    }
    
    /**
     * 查询
     * @throws Exception
     */
    @Test
    public void search() throws Exception {
        
        //得到读取索引文件的路径
        dir = FSDirectory.open(Paths.get("/Users/zhaohg/dic/demo3"));
        
        //通过dir得到的路径下的所有的文件
        IndexReader reader = DirectoryReader.open(dir);
        
        //建立索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);
        
        //查找的范围
        String searchField = "title";
        
        //查找的字段
        String q = "apple";
        
        //运用term来查找
        Term t = new Term(searchField, q);
        
        //通过term得到query对象
        Query query = new TermQuery(t);
        
        //获得查询的hits
        TopDocs hits = searcher.search(query, 10);
        
        //显示结果
        System.out.println("匹配 '" + q + "'，总共查询到" + hits.totalHits + "个文档");
        
        //循环得到文档，得到文档就可以得到数据
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            
            Document doc = searcher.doc(scoreDoc.doc);
            
            System.out.println(doc.get("author"));
        }
        
        //关闭reader
        reader.close();
    }
}

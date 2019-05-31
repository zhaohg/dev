package com.zhaohg.lucene.demo3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * 对被索引的文章进行crud
 * @author SZQ
 */
public class IndexDocument {
    
    //写测试数据，这些数据是写到索引文档里去的。
    private String ids[]     = {"1", "2", "3"}; //标示文档
    private String citys[]   = {"BeiJing", "HeBei", "ShanXi"};
    private String cityDes[] = {
            
            "BeiJing is the captial of China!",
            "HeBei is my hometown!",
            "ShanXi is a beautiful city!"
    };
    
    private Directory dir;
    
    //每次启动的时候都会执行这个方法，写索引的东西都写在setUp方法里
    @Before
    public void setUp() throws Exception {
        
        //得到读取索引文件的路径
        dir = FSDirectory.open(Paths.get("/Users/zhaohg/dic/demo3"));
        
        //获取IndexWriter实例
        IndexWriter writer = getWriter();
        
        for (int i = 0; i < ids.length; i++) {
            
            Document doc = new Document();
            
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", cityDes[i], Field.Store.NO));
            
            // 添加文档
            writer.addDocument(doc);
        }
        
        writer.close();
    }
    
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
     * 测试写了几个文档（对应图片一）
     * @throws Exception
     */
    @Test
    public void testIndexWriter() throws Exception {
        
        //获取IndexWriter实例
        IndexWriter writer = getWriter();
        
        System.out.println("写入了" + writer.numDocs() + "个文档");
        
        //关闭writer
        writer.close();
    }
    
    /**
     * 测试读取文档（对应图片二）
     * @throws Exception
     */
    @Test
    public void testIndexReader() throws Exception {
        
        //根据路径得到索引读取
        IndexReader reader = DirectoryReader.open(dir);
        
        //公共是多少文件，也就是最大文档数
        System.out.println("最大文档数：" + reader.maxDoc());
        
        //读取的实际文档数
        System.out.println("实际文档数：" + reader.numDocs());
        
        //关闭reader
        reader.close();
    }
    
    /**
     * 测试删除 在合并前（对应图片三）
     * @throws Exception
     */
    @Test
    public void testDeleteBeforeMerge() throws Exception {
        
        //获取IndexWriter实例
        IndexWriter writer = getWriter();
        
        //统计删除前的文档数
        System.out.println("删除前：" + writer.numDocs());
        
        //Term：第一个参数是删除的条件，第二个是删除的条件值
        writer.deleteDocuments(new Term("id", "1"));
        
        //提交writer（如果不提交，就不能删除）
        writer.commit();
        
        //显示删除在合并前的最大文档数量
        System.out.println("writer.maxDoc()：" + writer.maxDoc());
        
        //显示删除在合并前的实际数量
        System.out.println("writer.numDocs()：" + writer.numDocs());
        
        //关闭writer
        writer.close();
    }
    
    /**
     * 测试删除 在合并后（对应图片四）
     * @throws Exception
     */
    @Test
    public void testDeleteAfterMerge() throws Exception {
        
        //获取IndexWriter实例
        IndexWriter writer = getWriter();
        
        //删除前的文档数
        System.out.println("删除前：" + writer.numDocs());
        
        //Term：第一个参数是删除的条件，第二个是删除的条件值
        writer.deleteDocuments(new Term("id", "1"));
        
        // 强制删除
        writer.forceMergeDeletes();
        
        //提交writer
        writer.commit();
        
        //显示删除在合并后的最大文档数量
        System.out.println("writer.maxDoc()：" + writer.maxDoc());
        
        //显示删除在合并后的实际数量
        System.out.println("writer.numDocs()：" + writer.numDocs());
        
        //关闭writer
        writer.close();
    }
    
    /**
     * 测试更新（对应图片五）
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        
        //获取IndexWriter实例
        IndexWriter writer = getWriter();
        
        //实例化文档
        Document doc = new Document();
        
        //向文档里添加值
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("city", "qingdao", Field.Store.YES));
        doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
        
        //更新文档
        /**
         * 第一个参数是根据id为1的更新文档，，
         * 第二个是更改的内容
         *
         * 过程：先把查到的文档删掉，再添加。这就是更新。但是原来的数据还在；
         */
        writer.updateDocument(new Term("id", "1"), doc);
        
        //关闭writer
        writer.close();
    }
    
}

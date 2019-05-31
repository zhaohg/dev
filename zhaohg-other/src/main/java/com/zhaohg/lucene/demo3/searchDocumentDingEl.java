package com.zhaohg.lucene.demo3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * 对索引文档进行特定查询、解析表达式查询
 * @author SZQ
 */
public class searchDocumentDingEl {


    private Directory     dir;
    private IndexReader   reader;
    private IndexSearcher searcher;

    @Before
    public void setUp() throws Exception {

        //得到索引所在目录的路径
        dir = FSDirectory.open(Paths.get("/Users/zhaohg/dic/demo3"));

        //通过dir得到的路径下的所有的文件
        reader = DirectoryReader.open(dir);

        //建立索引查询器
        searcher = new IndexSearcher(reader);

    }

    @After
    public void tearDown() throws Exception {

        reader.close();
    }

    /**
     * 对特定项搜索：对索引文档有的分词进行查询（对应图一）
     * @throws Exception
     */
    @Test
    public void testTermQuery() throws Exception {

        //定义要查询的索引
        String searchField = "contents";

        //根据contents要查询的对象
        String q = "solr";

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

            System.out.println(doc.get("fullPath"));
        }
    }


    /**
     * 解析查询表达式（对应图二）
     * @throws Exception
     */
    @Test
    public void testQueryParser() throws Exception {

        String searchField = "contents";
        String q = "Rob* AND separab*";//再解析查询表达式的时候，中间的那个and一定要大写，否则查不出来

        //实例化分析器
        Analyzer analyzer = new StandardAnalyzer();

        //建立查询解析器
        /**
         * 第一个参数是要查询的字段；
         * 第二个参数是分析器Analyzer
         * */
        QueryParser parser = new QueryParser(searchField, analyzer);

        //根据传进来的p查找
        Query query = parser.parse(q);

        //开始查询
        /**
         * 第一个参数是通过传过来的参数来查找得到的query；
         * 第二个参数是要出查询的行数
         * */
        TopDocs hits = searcher.search(query, 100);

        //遍历topDocs
        /**
         * ScoreDoc:便利得到的文档
         * scoreDocs:代表TopDocs 的文件数组
         * @throws Exception
         * */
        System.out.println("匹配 " + q + "查询到" + hits.totalHits + "个记录");

        for (ScoreDoc scoreDoc : hits.scoreDocs) {

            Document doc = searcher.doc(scoreDoc.doc);

            System.out.println(doc.get("fullPath"));
        }

    }
}
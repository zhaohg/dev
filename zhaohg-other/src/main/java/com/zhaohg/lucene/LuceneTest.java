package com.zhaohg.lucene;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.StringReader;
import java.nio.file.Paths;

public class LuceneTest {

    private static final String   INDEXPATH = "/Users/zhaohg/dic/demo2";
    private static       Analyzer analyzer  = new ComplexAnalyzer();

    //new StandardAnalyzer()可以高亮
    public static void main(String[] args) {
        try {
            indexCreate();
            search();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexCreate() throws Exception {
        // 建立索引对象
        Directory directory = FSDirectory.open(Paths.get(INDEXPATH));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(directory, config);
        String title = "昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
        Document doc = new Document();
        doc.add(new Field("title", title, TextField.TYPE_STORED));
        writer.addDocument(doc);
        writer.close();
        directory.close();
    }

    public static void search() throws Exception {

        // 索引目录
        Directory dir = FSDirectory.open(Paths.get(INDEXPATH));
        // 根据索引目录创建读索引对象
        IndexReader reader = DirectoryReader.open(dir);
        // 搜索对象创建
        IndexSearcher searcher = new IndexSearcher(reader);
        // 创建查询解析对象
        QueryParser parser = new MultiFieldQueryParser(new String[]{"title"}, analyzer);
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        String word = "中西 本市 气温";
        BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
        String[] fields = {"title", "content"};
        Query multiQuery = MultiFieldQueryParser.parse(word, fields, clauses, analyzer);//parser.parse(query);
        // 根据域和目标搜索文本创建查询器
        System.out.println("搜索关键词: " + multiQuery.toString(word));
        // 对结果进行相似度打分排序
        ScoreDoc scoreDoc = new ScoreDoc(1, 1f);
        TopScoreDocCollector collector = TopScoreDocCollector.create(5 * 10, scoreDoc);
        searcher.search(multiQuery, collector);

        // 获取结果
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        int numTotalHits = collector.getTotalHits();
        System.out.println("一共匹配" + numTotalHits + "个网页");
        // 设置高亮显示格式
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'><strong>", "</strong></font>");
        /* 语法高亮显示设置 */
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(multiQuery));
        highlighter.setTextFragmenter(new SimpleFragmenter(100));

        // 显示搜索结果
        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String title = doc.get("title");
            TokenStream titleTokenStream = analyzer.tokenStream(title, new StringReader(title));
            String highLightTitle = highlighter.getBestFragment(titleTokenStream, title);
            System.out.println((i + 1) + "." + title);
            System.out.println(highLightTitle);
        }
    }
}

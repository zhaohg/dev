package com.zhaohg.lucene.demo3;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.StringReader;
import java.nio.file.Paths;

/**
 * 通过索引字段来读取文档
 */
public class ReaderByIndexer02Test {

    public static void search(String indexDir, String par) throws Exception {

        //得到读取索引文件的路径
        Directory dir = FSDirectory.open(Paths.get(indexDir));

        //通过dir得到的路径下的所有的文件
        IndexReader reader = DirectoryReader.open(dir);

        //建立索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);

        //中文分词器
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();

        //建立查询解析器
        /**
         * 第一个参数是要查询的字段；
         * 第二个参数是分析器Analyzer
         * */
        QueryParser parser = new QueryParser("desc", analyzer);

        //根据传进来的par查找
        Query query = parser.parse(par);

        //计算索引开始时间
        long start = System.currentTimeMillis();

        //开始查询
        /**
         * 第一个参数是通过传过来的参数来查找得到的query；
         * 第二个参数是要出查询的行数
         * */
        TopDocs topDocs = searcher.search(query, 10);

        //索引结束时间
        long end = System.currentTimeMillis();

        System.out.println("匹配" + par + ",总共花费了" + (end - start) + "毫秒,共查到" + topDocs.totalHits + "条记录。");


        //高亮显示start

        //算分
        QueryScorer scorer = new QueryScorer(query);

        //显示得分高的片段
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);

        //设置标签内部关键字的颜色
        //第一个参数：标签的前半部分；第二个参数：标签的后半部分。
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");

        //第一个参数是对查到的结果进行实例化；第二个是片段得分（显示得分高的片段，即摘要）
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);

        //设置片段
        highlighter.setTextFragmenter(fragmenter);

        //高亮显示end

        //遍历topDocs
        /**
         * ScoreDoc:是代表一个结果的相关度得分与文档编号等信息的对象。
         * scoreDocs:代表文件的数组
         * @throws Exception
         * */
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            //获取文档
            Document document = searcher.doc(scoreDoc.doc);

            //输出全路径
            System.out.println(document.get("city"));
            System.out.println(document.get("desc"));

            String desc = document.get("desc");
            if (desc != null) {

                //把全部得分高的摘要给显示出来
                //第一个参数是对哪个参数进行设置；第二个是以流的方式读入
                TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));

                //获取最高的片段
                System.out.println(highlighter.getBestFragment(tokenStream, desc));
            }
        }

        reader.close();
    }


    //开始测试
    public static void main(String[] args) {

        //索引指定的路径
        String indexDir = "/Users/zhaohg/dic/demo3";

        //查询的字段
        String par = "南京文化";

        try {

            search(indexDir, par);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

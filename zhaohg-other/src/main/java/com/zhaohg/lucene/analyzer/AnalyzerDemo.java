package com.zhaohg.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 选取了六个实现类，并分别输出它们对英文、中文、特殊符号及邮箱等的切分效果。
 */
public class AnalyzerDemo {
    private static final String[]   examples  = {"The quick brown 1234 fox jumped over the lazy dog!", "XY&Z 15.6 Corporation - xyz@example.com",
            "北京市北京大学"};
    private static final Analyzer[] ANALYZERS = new Analyzer[]{
            new WhitespaceAnalyzer(), new SimpleAnalyzer(), new StopAnalyzer(),
            new StandardAnalyzer(),
            new CJKAnalyzer(), new SmartChineseAnalyzer()
    };

    @Test
    public void testAnalyzer() throws IOException {
        for (int i = 0; i < ANALYZERS.length; i++) {
            String simpleName = ANALYZERS[i].getClass().getSimpleName();
            for (int j = 0; j < examples.length; j++) {
                TokenStream contents = ANALYZERS[i].tokenStream("contents", examples[j]);
                //TokenStream contents = ANALYZERS[i].tokenStream("contents", new StringReader(examples[j]));
                OffsetAttribute offsetAttribute = contents.addAttribute(OffsetAttribute.class);
                TypeAttribute typeAttribute = contents.addAttribute(TypeAttribute.class);
                contents.reset();
                System.out.println(simpleName + " analyzing : " + examples[j]);
                while (contents.incrementToken()) {
                    String s1 = offsetAttribute.toString();
                    int i1 = offsetAttribute.startOffset();//起始偏移量
                    int i2 = offsetAttribute.endOffset();//结束偏移量
                    System.out.print(s1 + "[" + i1 + "," + i2 + ":" + typeAttribute.type() + "]" + " ");
                }
                contents.end();
                contents.close();
                System.out.println();
            }
        }
    }

    @Test
    public void testTokenStream() throws IOException {
        Analyzer analyzer = new WhitespaceAnalyzer();
        String inputText = "This is a test text for token!";
        TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(inputText));
        //保存token字符串
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //在调用incrementToken()开始消费token之前需要重置stream到一个干净的状态
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            //打印分词结果
            System.out.print("[" + charTermAttribute + "]");
        }
    }

    @Test
    public void testAttribute() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        String input = "This is a test text for attribute! Just add-some word.";
        TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(input));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);
        PayloadAttribute payloadAttribute = tokenStream.addAttribute(PayloadAttribute.class);
        payloadAttribute.setPayload(new BytesRef("Just"));
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print("[" + charTermAttribute + " increment:" + positionIncrementAttribute.getPositionIncrement() +
                    " start:" + offsetAttribute
                    .startOffset() + " end:" +
                    offsetAttribute
                            .endOffset() + " type:" + typeAttribute.type() + " payload:" + payloadAttribute.getPayload() +
                    "]\n");
        }
        //在调用incrementToken()结束迭代之后，调用end()和close()方法;
        // end()可以唤醒当前TokenStream的处理器去做一些收尾工作;
        // close()可以关闭TokenStream和Analyzer去释放在分析过程中使用的资源;
        tokenStream.end();
        tokenStream.close();
    }

    /**
     * Analyzer之PerFieldAnalyzerWrapper
     * PerFieldAnalyzerWrapper的doc注释中提供了详细的说明，该类提供处理不同的Field使用不同的Analyzer的技术方案。
     * PerFieldAnalyzerWrapper可以像其它的Analyzer一样使用，包括索引和查询分析。
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testPerFieldAnalyzerWrapper() throws IOException, ParseException {
        Map<String, Analyzer> fields = new HashMap<>();
        fields.put("partnum", new KeywordAnalyzer());
        //对于其他的域，默认使用SimpleAnalyzer分析器，对于指定的域partnum使用KeywordAnalyzer
        PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(new SimpleAnalyzer(), fields);
        Directory directory = new RAMDirectory();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(perFieldAnalyzerWrapper);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        document.add(new Field("partnum", "Q36", fieldType));
        document.add(new Field("description", "Illidium Space Modulator", fieldType));
        indexWriter.addDocument(document);
        indexWriter.close();
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        //直接使用TermQuery是可以检索到的
        TopDocs search = indexSearcher.search(new TermQuery(new Term("partnum", "Q36")), 10);
        Assert.assertEquals(1, search.totalHits);
        //如果使用QueryParser，那么必须要使用PerFieldAnalyzerWrapper，否则如下所示，是检索不到的
        Query description = new QueryParser("description", new SimpleAnalyzer()).parse("partnum:Q36 AND SPACE");
        search = indexSearcher.search(description, 10);
        Assert.assertEquals(0, search.totalHits);
        System.out.println("SimpleAnalyzer :" + description.toString());//+partnum:q +description:space，原因是SimpleAnalyzer会剥离非字母字符并将字母小写化
        //使用PerFieldAnalyzerWrapper可以检索到
        //partnum:Q36 AND SPACE表示在partnum中出现Q36，在description中出现SPACE
        description = new QueryParser("description", perFieldAnalyzerWrapper).parse("partnum:Q36 AND SPACE");
        search = indexSearcher.search(description, 10);
        Assert.assertEquals(1, search.totalHits);
        System.out.println("(SimpleAnalyzer,KeywordAnalyzer) :" + description.toString());//+partnum:Q36 +description:space
    }

}
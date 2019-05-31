package com.zhaohg.lucene.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class SpanQueryDemo {
    private RAMDirectory      directory;
    private IndexSearcher     indexSearcher;
    private IndexReader       indexReader;
    private SpanTermQuery     quick;
    private SpanTermQuery     brown;
    private SpanTermQuery     red;
    private SpanTermQuery     fox;
    private SpanTermQuery     lazy;
    private SpanTermQuery     sleepy;
    private SpanTermQuery     dog;
    private SpanTermQuery     cat;
    private Analyzer          analyzer;
    private IndexWriter       indexWriter;
    private IndexWriterConfig indexWriterConfig;

    @Before
    public void setUp() throws IOException {
        directory = new RAMDirectory();
        analyzer = new WhitespaceAnalyzer();
        indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        //TextField使用Store.YES时索引文档、频率、位置信息
        document.add(new TextField("f", "What's amazing, the quick brown fox jumps over the lazy dog", Field.Store.YES));
        indexWriter.addDocument(document);
        document = new Document();
        document.add(new TextField("f", "Wow! the quick red fox jumps over the sleepy cat", Field.Store.YES));
        indexWriter.addDocument(document);
        indexWriter.commit();
        indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        indexReader = indexSearcher.getIndexReader();
        quick = new SpanTermQuery(new Term("f", "quick"));
        brown = new SpanTermQuery(new Term("f", "brown"));
        red = new SpanTermQuery(new Term("f", "red"));
        fox = new SpanTermQuery(new Term("f", "fox"));
        lazy = new SpanTermQuery(new Term("f", "lazy"));
        dog = new SpanTermQuery(new Term("f", "dog"));
        sleepy = new SpanTermQuery(new Term("f", "sleepy"));
        cat = new SpanTermQuery(new Term("f", "cat"));
    }

    @After
    public void setDown() {
        if (indexWriter != null && indexWriter.isOpen()) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private void assertOnlyBrownFox(Query query) throws IOException {
        TopDocs search = indexSearcher.search(query, 10);
        Assert.assertEquals(1, search.totalHits);
        Assert.assertEquals("wrong doc", 0, search.scoreDocs[0].doc);
    }

    private void assertBothFoxes(Query query) throws IOException {
        TopDocs search = indexSearcher.search(query, 10);
        Assert.assertEquals(2, search.totalHits);
    }

    private void assertNoMatches(Query query) throws IOException {
        TopDocs search = indexSearcher.search(query, 10);
        Assert.assertEquals(0, search.totalHits);
    }

    /**
     * 输出跨度查询的结果
     * @param query
     * @throws IOException
     */
    private void dumpSpans(SpanQuery query) throws IOException {
        SpanWeight weight = query.createWeight(indexSearcher, true);
        Spans spans = weight.getSpans(indexReader.getContext().leaves().get(0), SpanWeight.Postings.POSITIONS);
        System.out.println(query);
        TopDocs search = indexSearcher.search(query, 10);
        float[] scores = new float[2];
        for (ScoreDoc sd : search.scoreDocs) {
            scores[sd.doc] = sd.score;
        }
        int numSpans = 0;
        //处理所有跨度
        while (spans.nextDoc() != Spans.NO_MORE_DOCS) {
            while (spans.nextStartPosition() != Spans.NO_MORE_POSITIONS) {
                numSpans++;
                int id = spans.docID();
                //检索文档
                Document doc = indexReader.document(id);
                //重新分析文本
                TokenStream stream = analyzer.tokenStream("contents", new StringReader(doc.get("f")));
                stream.reset();
                OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
                int i = 0;
                StringBuilder sb = new StringBuilder();
                sb.append("  ");
                //处理所有语汇单元
                while (stream.incrementToken()) {
                    if (i == spans.startPosition()) {
                        sb.append("<");
                    }
                    sb.append(offsetAttribute.toString());
                    if (i + 1 == spans.endPosition()) {
                        sb.append(">");
                    }
                    sb.append(" ");
                    i++;
                }
                sb.append("(").append(scores[id]).append(")");
                System.out.println(sb.toString());
                stream.close();
            }
            if (numSpans == 0) {
                System.out.println(" No Spans");
            }
        }
    }

    @Test
    public void testSpanTermQuery() throws IOException {
        assertOnlyBrownFox(brown);
        dumpSpans(brown);
        dumpSpans(new SpanTermQuery(new Term("f", "the")));
        dumpSpans(new SpanTermQuery(new Term("f", "fox")));
    }

    /**
     * SpanFirstQuery可以对出现在域中前面某位置的跨度进行查询
     */
    @Test
    public void testSpanFirstQuery() throws IOException {
        SpanFirstQuery spanFirstQuery = new SpanFirstQuery(brown, 2);
        assertNoMatches(spanFirstQuery);
        dumpSpans(spanFirstQuery);
        //设置从前面开始10个跨度查找brown，一个切分结果是一个跨度，使用的是空格分词器
        spanFirstQuery = new SpanFirstQuery(brown, 5);
        dumpSpans(spanFirstQuery);
        assertOnlyBrownFox(spanFirstQuery);
    }

    @Test
    public void testSpanNearQuery() throws IOException {
        SpanQuery[] queries = new SpanQuery[]{quick, brown, dog};
        SpanNearQuery spanNearQuery = new SpanNearQuery(queries, 0, true);
        assertNoMatches(spanNearQuery);
        dumpSpans(spanNearQuery);
        spanNearQuery = new SpanNearQuery(queries, 4, true);
        assertNoMatches(spanNearQuery);
        dumpSpans(spanNearQuery);
        spanNearQuery = new SpanNearQuery(queries, 5, true);
        assertOnlyBrownFox(spanNearQuery);
        dumpSpans(spanNearQuery);
        //将inOrder设置为false，那么就不会考虑顺序，只要两者中间不超过三个就可以检索到
        spanNearQuery = new SpanNearQuery(new SpanQuery[]{lazy, fox}, 3, false);
        assertOnlyBrownFox(spanNearQuery);
        dumpSpans(spanNearQuery);
        //在考虑顺序的情况下，是检索不到的
        spanNearQuery = new SpanNearQuery(new SpanQuery[]{lazy, fox}, 3, true);
        assertNoMatches(spanNearQuery);
        dumpSpans(spanNearQuery);
        PhraseQuery phraseQuery = new PhraseQuery.Builder().add(new Term("f", "lazy")).add(new Term("f", "fox")).setSlop(4).build();
        assertNoMatches(phraseQuery);
        PhraseQuery phraseQuery1 = new PhraseQuery.Builder().setSlop(5).add(phraseQuery.getTerms()[0]).add(phraseQuery.getTerms()[1]).build();
        assertOnlyBrownFox(phraseQuery1);
    }

    @Test
    public void testSpanNotQuery() throws IOException {
        //设置slop为1
        SpanNearQuery quickFox = new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
        assertBothFoxes(quickFox);
        dumpSpans(quickFox);
        //第一个参数表示要包含的跨度对象，第二个参数则表示要排除的跨度对象
        SpanNotQuery quickFoxDog = new SpanNotQuery(quickFox, dog);
        assertBothFoxes(quickFoxDog);
        dumpSpans(quickFoxDog);
        //下面把red排除掉，那么就只能查到一条记录
        SpanNotQuery noQuickRedFox = new SpanNotQuery(quickFox, red);
        assertOnlyBrownFox(noQuickRedFox);
        dumpSpans(noQuickRedFox);
    }

    @Test
    public void testSpanOrQuery() throws IOException {
        SpanNearQuery quickFox = new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
        SpanNearQuery lazyDog = new SpanNearQuery(new SpanQuery[]{lazy, dog}, 0, true);
        SpanNearQuery sleepyCat = new SpanNearQuery(new SpanQuery[]{sleepy, cat}, 0, true);
        SpanNearQuery quickFoxNearLazyDog = new SpanNearQuery(new SpanQuery[]{quickFox, lazyDog}, 3, true);
        assertOnlyBrownFox(quickFoxNearLazyDog);
        dumpSpans(quickFoxNearLazyDog);
        SpanNearQuery quickFoxNearSleepyCat = new SpanNearQuery(new SpanQuery[]{quickFox, sleepyCat}, 3, true);
        dumpSpans(quickFoxNearSleepyCat);
        SpanOrQuery or = new SpanOrQuery(new SpanQuery[]{quickFoxNearLazyDog, quickFoxNearSleepyCat});
        assertBothFoxes(or);
        dumpSpans(or);
    }

    /**
     * 测试安全过滤
     * @throws IOException
     */
    @Test
    public void testSecurityFilter() throws IOException {
        Document document = new Document();
        document.add(new StringField("owner", "eric", Field.Store.YES));
        document.add(new TextField("keywords", "A B of eric", Field.Store.YES));
        indexWriter.addDocument(document);
        document = new Document();
        document.add(new TextField("owner", "jobs", Field.Store.YES));
        document.add(new TextField("keywords", "A B of jobs", Field.Store.YES));
        indexWriter.addDocument(document);
        document.add(new TextField("owner", "jack", Field.Store.YES));
        document.add(new TextField("keywords", "A B of jack", Field.Store.YES));
        indexWriter.addDocument(document);
        indexWriter.commit();
        TermQuery termQuery = new TermQuery(new Term("owner", "eric"));
        TermQuery termQuery1 = new TermQuery(new Term("keywords", "A"));
        //把FILTER看做是Like即可，也就是说owner必须是eric的才允许检索到
        Query query = new BooleanQuery.Builder().add(termQuery1, BooleanClause.Occur.MUST).add(termQuery,
                BooleanClause.Occur.FILTER).build();
        System.out.println(query);
        indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        TopDocs search = indexSearcher.search(query, 10);
        Assert.assertEquals(1, search.totalHits);
        //如果不加安全过滤的话，那么应该检索到三条记录
        query = new BooleanQuery.Builder().add(termQuery1, BooleanClause.Occur.MUST).build();
        System.out.println(query);
        search = indexSearcher.search(query, 10);
        Assert.assertEquals(3, search.totalHits);
        String keywords = indexSearcher.doc(search.scoreDocs[0].doc).get("keywords");
        System.out.println("使用Filter查询：" + keywords);
        //使用BooleanQuery可以实现同样的功能
        BooleanQuery booleanQuery = new BooleanQuery.Builder().add(termQuery, BooleanClause.Occur.MUST).add(termQuery1, BooleanClause.Occur.MUST).build();
        search = indexSearcher.search(booleanQuery, 10);
        Assert.assertEquals(1, search.totalHits);
        System.out.println("使用BooleanQuery查询：" + indexSearcher.doc(search.scoreDocs[0].doc).get("keywords"));
    }
}
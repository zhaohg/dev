package com.zhaohg.lucene.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Description:测试有多个索引文件的情况下，如何进行搜索并合并搜索结果
 */
public class MultiReaderDemo {
    Directory         aDirectory         = new RAMDirectory();
    Directory         bDirectory         = new RAMDirectory();
    IndexWriter       aIndexWriter;
    IndexWriter       bIndexWriter;
    private Analyzer analyzer = new WhitespaceAnalyzer();
    IndexWriterConfig aIndexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriterConfig bIndexWriterConfig = new IndexWriterConfig(analyzer);

    @Before
    public void setUp() throws IOException {
        String[] animals = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        aIndexWriter = new IndexWriter(aDirectory, aIndexWriterConfig);
        bIndexWriter = new IndexWriter(bDirectory, bIndexWriterConfig);
        for (int i = 0; i < animals.length; i++) {
            Document document = new Document();
            String animal = animals[i];
            document.add(new StringField("animal", animal, Field.Store.YES));
            if (animal.charAt(0) < 'n') {
                aIndexWriter.addDocument(document);
            } else {
                bIndexWriter.addDocument(document);
            }
        }
        aIndexWriter.commit();
        bIndexWriter.commit();
    }

    @After
    public void setDown() {
        try {
            if (aIndexWriter != null && aIndexWriter.isOpen()) {
                aIndexWriter.close();
            }
            if (bIndexWriter != null && bIndexWriter.isOpen()) {
                bIndexWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultiReader() throws IOException {
        IndexReader aIndexReader = DirectoryReader.open(aDirectory);
        IndexReader bIndexReader = DirectoryReader.open(bDirectory);
        MultiReader multiReader = new MultiReader(aIndexReader, bIndexReader);
        IndexSearcher indexSearcher = new IndexSearcher(multiReader);
        TopDocs animal = indexSearcher.search(new TermRangeQuery("animal", new BytesRef("h"), new BytesRef("q"), true, true), 10);
        Assert.assertEquals(10, animal.totalHits);
        ScoreDoc[] scoreDocs = animal.scoreDocs;
        for (ScoreDoc sd : scoreDocs) {
            System.out.println(indexSearcher.doc(sd.doc));
        }
    }
}
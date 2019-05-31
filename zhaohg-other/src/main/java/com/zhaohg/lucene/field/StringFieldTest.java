package com.zhaohg.lucene.field;

import com.zhaohg.lucene.Index.IndexUtil;
import com.zhaohg.lucene.search.SearchUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.IOException;

public class StringFieldTest {
    /**
     * 保存一个StringField
     */
    @Test
    public void testIndexStringFieldStored() {
        Document document = new Document();
        document.add(new StringField("stringValue", "12445", Field.Store.YES));
        document.add(new SortedDocValuesField("stringValue", new BytesRef("12445".getBytes())));

        Document document1 = new Document();
        document1.add(new StringField("stringValue", "23456", Field.Store.YES));
        document1.add(new SortedDocValuesField("stringValue", new BytesRef("23456".getBytes())));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("stringFieldPath", false);
            writer.addDocument(document);
            writer.addDocument(document1);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.commit();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试StringField排序
     */
    @Test
    public void testStringFieldSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("stringFieldPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("stringValue", SortField.Type.STRING, true);
            Sort sort = new Sort(sortField);
            //查询所有结果
            Query query = new MatchAllDocsQuery();
            TopFieldDocs docs = searcher.search(query, 2, sort);
            ScoreDoc[] scores = docs.scoreDocs;
            //遍历结果
            for (ScoreDoc scoreDoc : scores) {
                //System.out.println(searcher.doc(scoreDoc.doc));;
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc);
                //System.out.println(doc.getField("binaryValue").numericValue());
            }
            //searcher.search(query, results);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

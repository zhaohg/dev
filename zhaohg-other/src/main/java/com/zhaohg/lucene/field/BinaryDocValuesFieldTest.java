package com.zhaohg.lucene.field;

import com.zhaohg.lucene.Index.IndexUtil;
import com.zhaohg.lucene.search.SearchUtil;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
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

public class BinaryDocValuesFieldTest {
    /**
     * 保存一个BinaryDocValuesField
     */
    @Test
    public void testIndexLongFieldStored() {
        Document document = new Document();
        document.add(new BinaryDocValuesField("binaryValue", new BytesRef("1234".getBytes())));

        Document document1 = new Document();
        document1.add(new BinaryDocValuesField("binaryValue", new BytesRef("2345".getBytes())));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("binaryValueFieldPath", false);
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
     * 测试BinaryDocValuesField排序
     */
    @Test
    public void testBinaryDocValuesFieldSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("binaryValueFieldPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("binaryValue", SortField.Type.STRING_VAL, true);
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
            e.printStackTrace();
        }
    }
}

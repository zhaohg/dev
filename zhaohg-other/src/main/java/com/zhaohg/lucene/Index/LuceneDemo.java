package com.zhaohg.lucene.Index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * 如果仅仅做测试用，还可以将索引文件存储在内存之中，此时需要使用RAMDirectory
 */
public class LuceneDemo {
    private Directory   directory;
    private String[]    ids      = {"1", "2"};
    private String[]    unIndex  = {"Netherlands", "Italy"};
    private String[]    unStored = {"Amsterdam has lots of bridges", "Venice has lots of canals"};
    private String[]    text     = {"Amsterdam", "Venice"};
    private IndexWriter indexWriter;

    private IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());

    @Test
    public void createIndex() throws IOException {
        directory = new RAMDirectory();
        //指定将索引创建信息打印到控制台
        indexWriterConfig.setInfoStream(System.out);
        indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexWriterConfig = (IndexWriterConfig) indexWriter.getConfig();
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        fieldType.setStored(true);//存储
        fieldType.setTokenized(true);//分词
        for (int i = 0; i < ids.length; i++) {
            Document document = new Document();
            document.add(new Field("id", ids[i], fieldType));
            document.add(new Field("country", unIndex[i], fieldType));
            document.add(new Field("contents", unStored[i], fieldType));
            document.add(new Field("city", text[i], fieldType));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
    }

    @Test
    public void testDelete() throws IOException {
        RAMDirectory ramDirectory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(ramDirectory, new IndexWriterConfig(new StandardAnalyzer()));
        Document document = new Document();
        document.add(new IntPoint("ID", 1));
        indexWriter.addDocument(document);
        indexWriter.commit();
        //无法删除ID为1的
        indexWriter.deleteDocuments(new Term("ID", "1"));
        indexWriter.commit();
        DirectoryReader open = DirectoryReader.open(ramDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(open);
        Query query = IntPoint.newExactQuery("ID", 1);
        TopDocs search = indexSearcher.search(query, 10);
        //命中，1，说明并未删除
        System.out.println(search.totalHits);

        //使用Query删除
        indexWriter.deleteDocuments(query);
        indexWriter.commit();
        indexSearcher = new IndexSearcher(DirectoryReader.openIfChanged(open));
        search = indexSearcher.search(query, 10);
        //未命中，0，说明已经删除
        System.out.println(search.totalHits);
    }
}
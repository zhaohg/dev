package com.zhaohg.lucene.Index;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexUtil {

    /**
     * 创建索引写入器
     * @param indexPath
     * @param create
     * @return
     * @throws IOException
     */
    public static IndexWriter getIndexWriter(String indexPath, boolean create) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath, new String[0]));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        if (create) {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }
}

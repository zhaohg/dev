package com.zhaohg.lucene.search;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

public class SearchUtil {
    /**
     * 获取索引查看器
     * @param filePath
     * @param service
     * @return
     * @throws IOException
     */
    public static IndexSearcher getIndexSearcher(String filePath, ExecutorService service) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(filePath, new String[0])));
        IndexSearcher searcher = new IndexSearcher(reader, service);
        return searcher;
    }

}

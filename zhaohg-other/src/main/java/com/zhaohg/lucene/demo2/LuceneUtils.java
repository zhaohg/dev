package com.zhaohg.lucene.demo2;

import ch.qos.logback.classic.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class LuceneUtils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LuceneUtils.class);
    
    private static Directory directory;
    private static Analyzer  analyzer;
    
    static {
        try {
            directory = FSDirectory.open(Paths.get("/Users/zhaohg/dic/demo1"));
            // analyzer = new StandardAnalyzer();
            analyzer = new SmartChineseAnalyzer();
        } catch (Exception e) {
            logger.error("LuceneUtils error!", e);
        }
    }
    
    public static Directory getDirectory() {
        return directory;
    }
    
    public static Analyzer getAnalyzer() {
        return analyzer;
    }
    
    public static void closeIndexWriter(IndexWriter indexWriter) {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (Exception e2) {
                logger.error("indexWriter.close error", e2);
            }
        }
    }
    
}
package com.zhaohg;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class SearchFiles {
    public static void main(String[] args) throws Exception {
        String indexPath = "D:/lucene_test/index"; // 建立索引文件的目录
        String field = "contents";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        QueryParser parser = new QueryParser(field, analyzer);
        System.out.println("Enter query:");
        // 从Console读取要查询的语句
        String line = in.readLine();
        if (line == null || line.length() == -1) {
            return;
        }
        line = line.trim();
        if (line.length() == 0) {
            return;
        }
        
        Query query = parser.parse(line);
        System.out.println("Searching for:" + query.toString(field));
        doPagingSearch(searcher, query);
        in.close();
        reader.close();
    }
    
    private static void doPagingSearch(IndexSearcher searcher, Query query) throws IOException {
        // TopDocs保存搜索结果
        TopDocs results = searcher.search(query, 10);
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = Math.toIntExact(results.totalHits);
        System.out.println(numTotalHits + " total matching documents");
        for (ScoreDoc hit : hits) {
            Document document = searcher.doc(hit.doc);
            System.out.println("文档:" + document.get("path"));
            System.out.println("相关度:" + hit.score);
            System.out.println("================================");
        }
        
    }
}

package com.zhaohg;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexFiles {
    public static void main(String[] args) {
        String indexPath = "D:/lucene_test/index"; // 建立索引文件的目录
        String docsPath = "D:/lucene_test/docs"; // 读取文本文件的目录

        Path docDir = Paths.get(docsPath);

        IndexWriter writer = null;
        try {
            // 存储索引数据的目录
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            // 创建分析器
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            writer = new IndexWriter(dir, iwc);
            indexDocs(writer, docDir);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        indexDoc(writer, file);
                    } catch (IOException ignore) {
                        // 不索引那些不能读取的文件,忽略该异常
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path);
        }
    }

    private static void indexDoc(IndexWriter writer, Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // 创建一个新的空文档
            Document doc = new Document();
            // 添加字段
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);
            Field contentsField = new TextField("contents",
                    new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            doc.add(contentsField);
            System.out.println("adding " + file);
            // 写文档
            writer.addDocument(doc);
        }
    }
}
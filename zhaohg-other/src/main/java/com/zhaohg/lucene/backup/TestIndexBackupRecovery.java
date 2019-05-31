package com.zhaohg.lucene.backup;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import static org.apache.lucene.document.Field.Store.YES;

/**
 * 测试Lucene索引热备
 */
public class TestIndexBackupRecovery {

    public static final Logger logger = LoggerFactory.getLogger(TestIndexBackupRecovery.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        String f = "D:/index_test";
        String d = "D:/index_back";
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setIndexDeletionPolicy(new SnapshotDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy()));
        IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(f)), indexWriterConfig);
        Document document = new Document();
        document.add(new StringField("ID", "111", YES));
        document.add(new IntPoint("age", 111));
        document.add(new StoredField("age", 111));
        writer.addDocument(document);
        writer.commit();
        document = new Document();
        document.add(new StringField("ID", "222", Field.Store.YES));
        document.add(new IntPoint("age", 333));
        document.add(new StoredField("age", 333));
        writer.addDocument(document);
        document.add(new StringField("ID", "333", Field.Store.YES));
        document.add(new IntPoint("age", 555));
        document.add(new StoredField("age", 555));
        for (int i = 0; i < 1000; i++) {
            document = new Document();
            document.add(new StringField("ID", "333", YES));
            document.add(new IntPoint("age", 1000000 + i));
            document.add(new StoredField("age", 1000000 + i));
            document.add(new StringField("desc", "ABCDEFG" + i, YES));
            writer.addDocument(document);
        }
        writer.deleteDocuments(new TermQuery(new Term("ID", "333")));
        writer.commit();
        backupIndex(writer, f, d);
        writer.close();
    }

    public static void backupIndex(IndexWriter indexWriter, String indexDir, String
            backupIndexDir) throws IOException {
        IndexWriterConfig config = (IndexWriterConfig) indexWriter.getConfig();
        SnapshotDeletionPolicy snapshotDeletionPolicy = (SnapshotDeletionPolicy) config.getIndexDeletionPolicy();
        IndexCommit snapshot = snapshotDeletionPolicy.snapshot();
        //设置索引提交点，默认是null，会打开最后一次提交的索引点
        config.setIndexCommit(snapshot);
        Collection<String> fileNames = snapshot.getFileNames();
        File[] dest = new File(backupIndexDir).listFiles();
        String sourceFileName;
        String destFileName;
        if (dest != null && dest.length > 0) {
            //先删除备份文件中的在此次快照中已经不存在的文件
            for (File file : dest) {
                boolean flag = true;
                //包括文件扩展名
                destFileName = file.getName();
                for (String fileName : fileNames) {
                    sourceFileName = fileName;
                    if (sourceFileName.equals(destFileName)) {
                        flag = false;
                        break;//跳出内层for循环
                    }
                }
                if (flag) {
                    file.delete();//删除
                }
            }
            //然后开始备份快照中新生成的文件
            for (String fileName : fileNames) {
                boolean flag = true;
                sourceFileName = fileName;
                for (File file : dest) {
                    destFileName = file.getName();
                    //备份中已经存在无需复制，因为Lucene索引是一次写入的，所以只要文件名相同不要要hash检查就可以认为它们的数据是一样的
                    if (destFileName.equals(sourceFileName)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    File from = new File(indexDir + File.separator + sourceFileName);//源文件
                    File to = new File(backupIndexDir + File.separator + sourceFileName);//目的文件
                    FileUtils.copyFile(from, to);
                }
            }
        } else {
            //备份不存在，直接创建
            for (String fileName : fileNames) {
                File from = new File(indexDir + File.separator + fileName);//源文件
                File to = new File(backupIndexDir + File.separator + fileName);//目的文件
                FileUtils.copyFile(from, to);
            }
        }
        snapshotDeletionPolicy.release(snapshot);
        //删除已经不再被引用的索引提交记录
        indexWriter.deleteUnusedFiles();
    }

    /**
     * @param source 索引源
     * @param dest 索引目标
     * @param indexWriterConfig 配置相关
     */
    public static void recoveryIndex(String source, String dest, IndexWriterConfig indexWriterConfig) {
        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(FSDirectory.open(Paths.get(dest)), indexWriterConfig);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            //说明IndexWriter正常打开了，无需恢复
            if (indexWriter != null && indexWriter.isOpen()) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            } else {
                try {
                    //说明IndexWriter已经无法打开，使用备份恢复索引
                    //此处简单操作，先清空损坏的索引文件目录，如果索引特别大，可以比对每个文件，不必全部删除  try {
                    FileUtils.deleteDirectory(new File(dest));
                    FileUtils.copyDirectory(new File(source), new File(dest));
                } catch (IOException e) {
                    logger.error("", e);
                    //使用备份恢复出错，那么就使用最后一招修复索引
                    logger.info("Check index {} now!", dest);
                    try {
                        IndexUtils.checkIndex(dest);//修复索引
                    } catch (IOException | InterruptedException e1) {
                        logger.error("Check index error!", e1);
                    }
                }
            }
        }
    }


}
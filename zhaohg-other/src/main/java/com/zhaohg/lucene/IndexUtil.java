package com.zhaohg.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IndexUtil {
    
    // mmseg4j索引字典存储路径
    private static final String      DICT_PATH = "/Users/zhaohg/dic";
    private static       IndexReader reader    = null;
    private int[]    ids      = {1, 2, 3, 4, 5, 6};
    private String[] names    = {"zhangsan", "lisi", "wangwu", "zhaoer", "wangmazi", "laomao"};
    private String[] emails   = {"zhangsan@163.com", "lisi@yffsc.com", "wangwu@suhu.com", "zhaoer@qq.com", "wangmazi@sina.com", "laomao@taobao.com"};
    private String[] summarys = {"欢迎来开到java的世界 like", "9月10日工作报告", "10月活动策划案", "机票信息", "新闻审核过滤规则", "双11活动策划"};
    private String[] centents = {"hello world! like", "你也不看,我也懒得写 like", "各种项详细like", "like双人巴厘10天游", "谁知道都有什么规则like,不懂", "活动前准备like详细,活动装互动环节,收尾工作,结束分析总结ç"};
    private int[]    attachs  = {3, 5, 1, 4, 6, 2};
    private Date[]   dates    = {};
    private Map<String, Float> scores = new HashMap<>();
    private              Directory   directory = null;
    
    public IndexUtil() {
        try {
            setDate();
            setPut();
            
            directory = FSDirectory.open(Paths.get(DICT_PATH));////存硬盘中
            //directory = new RAMDirectory();
            
            reader = DirectoryReader.open(directory);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建索引写入器
     * @param create
     * @return
     * @throws IOException
     */
    public static IndexWriter getIndexWriter(Directory directory, boolean create) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(getAnalyzer());
        if (create) {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }
        IndexWriter writer = new IndexWriter(directory, iwc);
        return writer;
    }
    
    //分词器
    public static Analyzer getAnalyzer() {
        Analyzer analyzer = null;
        analyzer = new StandardAnalyzer();//
        
        return analyzer;
    }
    
    public void setDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dates = new Date[ids.length];
            dates[0] = sdf.parse("2012-08-17");
            dates[1] = sdf.parse("2011-02-17");
            dates[2] = sdf.parse("2013-03-17");
            dates[3] = sdf.parse("2014-04-17");
            dates[4] = sdf.parse("2016-05-17");
            dates[5] = sdf.parse("2015-07-17");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setPut() {
        scores.put("yffsc.com", 2.0f);
        scores.put("qq.com", 1.5f);
    }
    
    public IndexSearcher getSearcher() {
        try {
            if (reader == null) {
                reader = DirectoryReader.open(directory);
            }//else {
//                IndexReader tr = IndexReader.openIfChanged().o
            //}
            return new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //创建索引
    public void create(boolean create) {
        try {
            IndexWriter writer = getIndexWriter(directory, true);
//            writer.deleteAll();//通过删除索引然后再建立索引来更新
            
            for (int i = 0; i < ids.length; i++) {
                addDoc(writer, i);
            }
            //writer.commit();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //创建文档,并为文档添加域
    private void addDoc(IndexWriter writer, int i) throws IOException {
        //域存储选项和索引选项Field.STORED  Field.Index
        Document document = new Document();
        document.add(new StoredField("id", ids[i]));
        document.add(new NumericDocValuesField("id", ids[i]));
        document.add(new Field("name", names[i], TextField.TYPE_STORED));
//        document.add(new Field("email", emails[i], TextField.TYPE_STORED));
        document.add(new Field("summary", summarys[i], TextField.TYPE_STORED));
        document.add(new Field("centent", centents[i], TextField.TYPE_STORED));
        document.add(new StoredField("attach", attachs[i]));   //存储数字
        document.add(new StoredField("date", dates[i].getTime()));//存储日期
        document.add(new NumericDocValuesField("attach", attachs[i]));
        document.add(new NumericDocValuesField("date", dates[i].getTime()));
        
        Field field = new Field("email", emails[i], TextField.TYPE_STORED);
        
        String et = emails[i].substring(emails[i].lastIndexOf("@") + 1);
        System.out.println(et);
        if (scores.containsKey(et)) {
            field.setBoost(scores.get(et));
            
        } else {
            field.setBoost(1.1f);
        }
        document.add(field);
        
        writer.addDocument(document);//将文档写入索引
    }
    
    //查询
    public void query(String querystr) {
        try {
            
            IndexReader reader = DirectoryReader.open(directory);
            System.out.println("存储的文档数 = " + reader.numDocs());
            System.out.println("文档的总数 = " + reader.maxDoc());
            System.out.println("删除总数 = " + reader.numDeletedDocs());
            
            IndexSearcher searcher = new IndexSearcher(reader);
            //创建queryParser确定要搜索的文件内容,第一个参数标识搜索的域
            QueryParser queryParser = new QueryParser("summary", getAnalyzer());
            //创建query表示搜索域content中包含queryStr的文档
//            Query query = queryParser.parse(querystr);
            Query query = new MatchAllDocsQuery();//查询所有
            
            //排序,文档一定要有document.add(new NumericDocValuesField("id", ids[i]));
            SortField idValue = new SortField("id", SortField.Type.INT, true);
            
            int page = 10;
            //搜索返回TopDocs对象
            TopDocs topDocs = searcher.search(query, page, new Sort(new SortField[]{idValue}));
            System.out.println("topDocs.getMaxScore() = " + topDocs.getMaxScore());
            System.out.println("topDocs.totalHits = " + topDocs.totalHits);
            //获取ScoreDoc对象
            ScoreDoc[] hits = topDocs.scoreDocs;
            
            for (ScoreDoc sd : hits) {
                Document document = searcher.doc(sd.doc);
                System.out.println("---score----" + sd.score);
                System.out.println(document + "\r\n"
                        + "id = " + document.get("id") + "\r\n"
                        + "name = " + document.get("name") + "-----" + document.getField("name").boost() + "\r\n"
                        + "email = " + document.get("email") + "-----" + document.getField("email").boost() + "\r\n"
                        + "summary = " + document.get("summary") + "-----" + document.getField("summary").boost() + "\r\n"
                        + "centent = " + document.get("centent") + "-----" + document.getField("summary").boost() + "\r\n"
                        + "attach = " + document.get("attach") + "-----" + document.getField("summary").boost() + "\r\n"
                        + "date = " + document.get("date") + "-----" + document.getField("summary").boost() + "\r\n");
                
                float score = sd.score;
                
                
            }


//            TopScoreDocCollector collector = TopScoreDocCollector.create(page);
//            searcher.search(query, collector);
//            ScoreDoc[] hits = collector.topDocs().scoreDocs;
//
//            for(int i=0;i<hits.length;++i) {
//                int docId = hits[i].doc;
//                Document document = searcher.doc(docId);
//                System.out.println("id = " +document.get("id") +"\r\n"
//                        + "name = " +document.get("name") +"\r\n"
//                        + "email = " +document.get("email") +"\r\n"
//                        + "summary = " +document.get("summary") +"\r\n"
//                        + "centent = " +document.get("centent") +"\r\n"
//                        + "attach = " +document.get("attach") +"\r\n" );
//            }
            
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //删除索引
    public void delete01() {
        
        try {
            IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
            IndexWriter writer = new IndexWriter(directory, config);

//            writer.deleteAll();
//
            QueryParser queryParser = new QueryParser("summary", getAnalyzer());
            Query query = queryParser.parse("java");
            Term term = new Term("id", "1");
            
            //参数是一个选项，可以是一个query，也可以是一个term  term就是一个精确查找的值
            //此时删除的文档并未完全删除，而是存储在回收站中，可以恢复的
            writer.deleteDocuments(query);
            writer.deleteDocuments(term);
            //writer.deleteAll();//彻底删除
            
            writer.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

//    public void undelete(){
//        try {
//            IndexReader reader = DirectoryReader.open(directory);
//            reader.undelete();//3.5
//            reader.close();
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//    }
    
    //恢复删除的索引
    public void recover() {//TODO
        try {
            IndexReader reader = DirectoryReader.open(directory);
            System.out.println("存储的文档数 = " + reader.numDocs());
            System.out.println("文档的总数 = " + reader.maxDoc());
            
            System.out.println("删除总数 = " + reader.numDeletedDocs());
            reader.hasDeletions();
            System.out.println("存储的文档数 = " + reader.numDocs());
            System.out.println("文档的总数 = " + reader.maxDoc());
            
            System.out.println("删除总数 = " + reader.numDeletedDocs());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //清空回收站，强制优化 尽量少用
    public void forceMergeDeletes() {
        try {
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(getAnalyzer()));
            writer.forceMergeDeletes();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void merge() {
        try {
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(getAnalyzer()));
            //将所以合并为2段,被删除的数据会被清空
            //3.5版本以后不建议使用,因为消耗大,lucene或自动处理
            writer.forceMerge(2);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //更新索引
    public void update() {
        //lucene本身不支持更新
        try {
            IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(directory, config);
            
            Document document = new Document();
            document.add(new Field("id", Integer.toString(1), TextField.TYPE_STORED));
            document.add(new Field("name", "zhao", TextField.TYPE_STORED));
            document.add(new Field("email", "373973619@qq.com", TextField.TYPE_STORED));
            document.add(new Field("summary", "更新后的摘要java", TextField.TYPE_STORED));
            document.add(new Field("centent", "更新后的内容", TextField.TYPE_STORED));
            document.add(new NumericDocValuesField("attach", 8));//存储数字
            document.add(new NumericDocValuesField("date", new Date().getTime()));//存储日期
            
            writer.updateDocument(new Term("id", "1"), document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //查询
    public void search01() {
        try {
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TermQuery termQuery = new TermQuery(new Term("content", "like"));
            int page = 10;
            TopDocs topDocs = searcher.search(termQuery, page);
            ScoreDoc[] hits = topDocs.scoreDocs;
            
            for (ScoreDoc sd : hits) {
                Document document = searcher.doc(sd.doc);
                System.out.println(sd.doc + "----" + sd.score + "---" + "id = " + document.get("id") + "\r\n"
                        + "name = " + document.get("name") + "-----" + document.getField("name").boost() + "\r\n"
                        + "email = " + document.get("email") + "-----" + document.getField("email").boost() + "\r\n"
                        + "summary = " + document.get("summary") + "-----" + document.getField("summary").boost() + "\r\n"
                        + "centent = " + document.get("centent") + "-----" + document.getField("centent").boost() + "\r\n"
                        + "attach = " + document.get("attach") + "\r\n"
                        + "date = " + document.get("date") + "\r\n");
                
                float score = sd.score;
                System.out.println(score);
                
            }
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //查询
    public void search02(String querystr) {
        try {
            IndexSearcher searcher = getSearcher();
            TermQuery termQuery = new TermQuery(new Term("content", querystr));
            int page = 10;
            TopDocs topDocs = searcher.search(termQuery, page);
            ScoreDoc[] hits = topDocs.scoreDocs;
            
            for (ScoreDoc sd : hits) {
                Document document = searcher.doc(sd.doc);
                System.out.println(sd.doc + "----" + sd.score + "---" + "id = " + document.get("id") + "\r\n"
                        + "name = " + document.get("name") + "-----" + document.getField("name").boost() + "\r\n"
                        + "email = " + document.get("email") + "-----" + document.getField("email").boost() + "\r\n"
                        + "summary = " + document.get("summary") + "-----" + document.getField("summary").boost() + "\r\n"
                        + "centent = " + document.get("centent") + "-----" + document.getField("centent").boost() + "\r\n"
                        + "attach = " + document.get("attach") + "\r\n"
                        + "date = " + document.get("date") + "\r\n");
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}

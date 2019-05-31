package com.zhaohg.lucene.field;

import com.zhaohg.lucene.Index.IndexUtil;
import com.zhaohg.lucene.search.SearchUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * IntPoint,FloatPoint,LongPoint,DoublePoint,HalfFloatPoint使用方法类似
 */
public class PointTest {
    /**
     * 保存一个IntPoint
     */
    @Test
    public void testIndexIntPointStored() {
        Document document = new Document();
        /**
         * 对float型字段索引，只索引不存储，
         * 提供了一些静态工厂方法用于创建一般的查询，提供了不同于文本的数值类型存储方式，使用KD-trees索引
         */
        document.add(new IntPoint("intValue", 30));
        /**要排序必须加同名的field，且类型为NumericDocValuesField*/
        document.add(new NumericDocValuesField("intValue", 30));
        /**存储值，用于评分、排序和值检索，如果需要存储值，还需要添加一个单独的StoredField实例*/
        document.add(new StoredField("intValue", 30));

        Document document1 = new Document();
        document1.add(new IntPoint("intValue", 40));
        document1.add(new NumericDocValuesField("intValue", 40));
        document1.add(new StoredField("intValue", 40));
        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("intPointPath", false);
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
     * 测试IntPoint排序
     */
    @Test
    public void testIntPointSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("intPointPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("intValue", SortField.Type.INT, true);//第三个参数为true，表示从大到小
            Sort sort = new Sort(sortField);
            //查询所有结果
            TopFieldDocs docs = searcher.search(new MatchAllDocsQuery(), 2, sort);
            ScoreDoc[] scores = docs.scoreDocs;
            //遍历结果
            for (ScoreDoc scoreDoc : scores) {
                System.out.println(searcher.doc(scoreDoc.doc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存一个longField
     */
    @Test
    public void testIndexLongPointStored() {
        Document document = new Document();
        /**
         * 对long型字段索引，只索引不存储，
         * 提供了一些静态工厂方法用于创建一般的查询，提供了不同于文本的数值类型存储方式，使用KD-trees索引
         */
        document.add(new LongPoint("longValue", 50L));
        /**要排序必须加同名的field，且类型为 LongDocValuesField*/
        document.add(new NumericDocValuesField("longValue", 50L));
        /** 存储值，用于评分、排序和值检索，如果需要存储值，还需要添加一个单独的StoredField实例*/
        document.add(new StoredField("longValue", 50L));

        Document document1 = new Document();
        document1.add(new LongPoint("longValue", 80L));
        document1.add(new NumericDocValuesField("longValue", 80L));
        document1.add(new StoredField("longValue", 80L));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("longPointPath", false);
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
     * 测试longField排序
     */
    @Test
    public void testLongPointSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("longPointPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("longValue", SortField.Type.LONG, true);
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
            }
            //searcher.search(query, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存一个floatField
     */
    @Test
    public void testIndexFloatPointStored() {
        Document document = new Document();
        /**
         * 对float型字段索引，只索引不存储，
         * 提供了一些静态工厂方法用于创建一般的查询，提供了不同于文本的数值类型存储方式，使用KD-trees索引
         */
        document.add(new FloatPoint("floatValue", 9.1f));
        /**要排序必须加同名的field，且类型为 FloatDocValuesField*/
        document.add(new FloatDocValuesField("floatValue", 9.1f));
        /** 存储值，用于评分、排序和值检索，如果需要存储值，还需要添加一个单独的StoredField实例*/
        document.add(new StoredField("floatValue", 9.1f));

        Document document1 = new Document();
        document1.add(new FloatPoint("floatValue", 80.1f));
        document1.add(new FloatDocValuesField("floatValue", 80.1f));
        document1.add(new StoredField("floatValue", 80.1f));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("floatPointPath", false);
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
     * 测试intField排序
     */
    @Test
    public void testFloatPointSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("floatPointPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("floatValue", SortField.Type.FLOAT, true);
            Sort sort = new Sort(sortField);
            //查询所有结果
            TopFieldDocs docs = searcher.search(new MatchAllDocsQuery(), 2, sort);
            ScoreDoc[] scores = docs.scoreDocs;
            //遍历结果
            for (ScoreDoc scoreDoc : scores) {
                //System.out.println(searcher.doc(scoreDoc.doc));;
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存一个doublePoint
     */
    @Test
    public void testIndexDoublePointStored() {
        Document document = new Document();
        /**
         * 对float型字段索引，只索引不存储，
         * 提供了一些静态工厂方法用于创建一般的查询，提供了不同于文本的数值类型存储方式，使用KD-trees索引
         */
        document.add(new DoublePoint("doubleValue", 9.11));
        /**要排序必须加同名的field，且类型为 FloatDocValuesField*/
        document.add(new DoubleDocValuesField("doubleValue", 9.11));
        /** 存储值，用于评分、排序和值检索，如果需要存储值，还需要添加一个单独的StoredField实例*/
        document.add(new StoredField("doubleValue", 9.11));

        Document document1 = new Document();
        document1.add(new DoublePoint("doubleValue", 80.12));
        document1.add(new DoubleDocValuesField("doubleValue", 80.12));
        document1.add(new StoredField("doubleValue", 80.12));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("doublePointPath", false);
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
     * 测试doublePoint排序
     */
    @Test
    public void testDoublePointSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("doublePointPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("doubleValue", SortField.Type.DOUBLE, true);
            Sort sort = new Sort(sortField);
            //查询所有结果
            TopFieldDocs docs = searcher.search(new MatchAllDocsQuery(), 2, sort);
            ScoreDoc[] scores = docs.scoreDocs;
            //遍历结果
            for (ScoreDoc scoreDoc : scores) {
                //System.out.println(searcher.doc(scoreDoc.doc));;
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //HalfFloatPoint//

    /**
     * 保存一个HalfFloatPoint
     */
    @Test
    public void testIndexHalfFloatPointStored() {
        Document document = new Document();
        /**
         * 对float型字段索引，只索引不存储，
         * 提供了一些静态工厂方法用于创建一般的查询，提供了不同于文本的数值类型存储方式，使用KD-trees索引
         */
        document.add(new HalfFloatPoint("halfFloatValue", 1));
        /**要排序必须加同名的field，且类型为 FloatDocValuesField*/
        document.add(new NumericDocValuesField("halfFloatValue", 1));
        /** 存储值，用于评分、排序和值检索，如果需要存储值，还需要添加一个单独的StoredField实例*/
        document.add(new StoredField("halfFloatValue", 1));

        Document document1 = new Document();
        document1.add(new HalfFloatPoint("halfFloatValue", 8));
        document1.add(new DoubleDocValuesField("halfFloatValue", 8));
        document1.add(new StoredField("halfFloatValue", 8));

        IndexWriter writer = null;
        try {
            writer = IndexUtil.getIndexWriter("halfFloatPointPath", false);
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
     * 测试HalfFloatPoint排序
     */
    @Test
    public void testHalfFloatPointSort() {
        try {
            IndexSearcher searcher = SearchUtil.getIndexSearcher("halfFloatPointPath", null);
            //构建排序字段
            SortField[] sortField = new SortField[1];
            sortField[0] = new SortField("halfFloatValue", SortField.Type.FLOAT, true);
            Sort sort = new Sort(sortField);
            //查询所有结果
            TopFieldDocs docs = searcher.search(new MatchAllDocsQuery(), 2, sort);
            ScoreDoc[] scores = docs.scoreDocs;
            //遍历结果
            for (ScoreDoc scoreDoc : scores) {
                //System.out.println(searcher.doc(scoreDoc.doc));;
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * IntPoint查询
     * XXXPoint类中提供了一些常用的静态工厂查询方法，可以直接用来构建查询语句。
     * @throws IOException
     */
    @Test
    public void testIntPointQuery() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        Document document = new Document();
        Field intPoint = new IntPoint("age", 11);
        document.add(intPoint);
        intPoint = new StoredField("age", 11);
        document.add(intPoint);
        indexWriter.addDocument(document);
        Field intPoint1 = new IntPoint("age", 22);
        document = new Document();
        document.add(intPoint1);
        intPoint1 = new StoredField("age", 22);
        document.add(intPoint1);
        indexWriter.addDocument(document);
        indexWriter.close();
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        //精确查询
        Query query = IntPoint.newExactQuery("age", 11);
        ScoreDoc[] scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("精确查询：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，不包含边界
        query = IntPoint.newRangeQuery("age", Math.addExact(11, 1), Math.addExact(22, -1));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("不包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，包含边界
        query = IntPoint.newRangeQuery("age", 11, 22);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，左包含，右不包含
        query = IntPoint.newRangeQuery("age", 11, Math.addExact(22, -1));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("左包含右不包含：" + indexSearcher.doc(scoreDoc.doc));
        }
        //集合查询
        query = IntPoint.newSetQuery("age", 11, 22, 33);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("集合查询：" + indexSearcher.doc(scoreDoc.doc));
        }
    }

    @Test
    public void testLongPointQuery() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        Document document = new Document();
        Field longPoint = new LongPoint("age", 11);
        document.add(longPoint);
        longPoint = new StoredField("age", 11);
        document.add(longPoint);
        indexWriter.addDocument(document);
        longPoint = new LongPoint("age", 22);
        document = new Document();
        document.add(longPoint);
        longPoint = new StoredField("age", 22);
        document.add(longPoint);
        indexWriter.addDocument(document);
        indexWriter.close();
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        //精确查询
        Query query = LongPoint.newExactQuery("age", 11);
        ScoreDoc[] scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("精确查询：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，不包含边界
        query = LongPoint.newRangeQuery("age", Math.addExact(11, 1), Math.addExact(22, -1));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("不包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，包含边界
        query = LongPoint.newRangeQuery("age", 11, 22);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，左包含，右不包含
        query = LongPoint.newRangeQuery("age", 11, Math.addExact(22, -1));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("左包含右不包含：" + indexSearcher.doc(scoreDoc.doc));
        }
        //集合查询
        query = LongPoint.newSetQuery("age", 11, 22, 33);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("集合查询：" + indexSearcher.doc(scoreDoc.doc));
        }
    }

    @Test
    public void testFloatPointQuery() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        Document document = new Document();
        Field floatPoint = new FloatPoint("age", 11.1f);
        document.add(floatPoint);
        floatPoint = new StoredField("age", 11.1f);
        document.add(floatPoint);
        indexWriter.addDocument(document);
        floatPoint = new FloatPoint("age", 22.2f);
        document = new Document();
        document.add(floatPoint);
        floatPoint = new StoredField("age", 22.2f);
        document.add(floatPoint);
        indexWriter.addDocument(document);
        indexWriter.close();
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        //精确查询
        Query query = FloatPoint.newExactQuery("age", 11.1f);
        ScoreDoc[] scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("精确查询：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，不包含边界
        query = FloatPoint.newRangeQuery("age", Math.nextUp(11.1f), Math.nextDown(22.2f));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("不包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，包含边界
        query = FloatPoint.newRangeQuery("age", 11.1f, 22.2f);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，左包含，右不包含
        query = FloatPoint.newRangeQuery("age", 11.1f, Math.nextDown(22.2f));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("左包含右不包含：" + indexSearcher.doc(scoreDoc.doc));
        }
        //集合查询
        query = FloatPoint.newSetQuery("age", 11.1f, 22.2f, 33.3f);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("集合查询：" + indexSearcher.doc(scoreDoc.doc));
        }
    }

    @Test
    public void testDoublePointQuery() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        Document document = new Document();
        Field doublePoint = new DoublePoint("age", 11.1);
        document.add(doublePoint);
        doublePoint = new StoredField("age", 11.1);
        document.add(doublePoint);
        indexWriter.addDocument(document);
        doublePoint = new DoublePoint("age", 22.2);
        document = new Document();
        document.add(doublePoint);
        doublePoint = new StoredField("age", 22.2);
        document.add(doublePoint);
        indexWriter.addDocument(document);
        indexWriter.close();
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        //精确查询
        Query query = DoublePoint.newExactQuery("age", 11.1);
        ScoreDoc[] scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("精确查询：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，不包含边界
        query = DoublePoint.newRangeQuery("age", Math.nextUp(11.1), Math.nextDown(22.2));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("不包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，包含边界
        query = DoublePoint.newRangeQuery("age", 11.1, 22.2);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("包含边界：" + indexSearcher.doc(scoreDoc.doc));
        }
        //范围查询，左包含，右不包含
        query = DoublePoint.newRangeQuery("age", 11.1, Math.nextDown(22.2));
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("左包含右不包含：" + indexSearcher.doc(scoreDoc.doc));
        }
        //集合查询
        query = DoublePoint.newSetQuery("age", 11.1, 22.2, 33.3);
        scoreDocs = indexSearcher.search(query, 10).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("集合查询：" + indexSearcher.doc(scoreDoc.doc));
        }
    }
}

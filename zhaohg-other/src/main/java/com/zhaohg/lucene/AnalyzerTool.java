package com.zhaohg.lucene;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ckb.SoraniAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 环境：Lucene 4.1版本/IKAnalyzer 2012 FF版本/mmseg4j 1.9版本
 * 1.给定输入文本，获取中文拆分词结果；
 * 2.给定输入文本，对该文本按一定规则进行权重打分；
 * 如：文本中包含指定关键词的频率越高，分值越高。
 * @author Administrator
 */
public class AnalyzerTool {
    private static final String     MMSEG4J_DICT_PATH = "/Users/zhaohg/dic/demo3";
    private static       Dictionary dictionary        = Dictionary.getInstance(MMSEG4J_DICT_PATH);

    // 负面关键词信息，如果文本中包含这些词，那么该文本的打分值将变高。
    private static List<String> lstNegativeWord;

    static {
        lstNegativeWord = new ArrayList<String>();

        // 下列词语必须存在于词典中：或者是分词器自带的词典，或者是自定义词典；
        // 否则计算权重结果不准，因为有关键词没有被分词器拆分出来。
        lstNegativeWord.add("不雅");
        lstNegativeWord.add("被免");
        lstNegativeWord.add("偷拍");
    }

    /**
     * 测试各种解析器对同样文本的解析结果
     * @param content
     * @throws Exception
     */
    public static void testAnalyzer(String content) throws Exception {
        Analyzer analyzer = null;

        analyzer = new SoraniAnalyzer(); // 等于new IKAnalyzer(false);
        System.out.println("new SoraniAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

        analyzer = new WhitespaceAnalyzer();//空格分词器
        System.out.println("new WhitespaceAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));


        analyzer = new StandardAnalyzer();//标准分词
        System.out.println("new StandardAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
        System.out.println("默认解析器打分结果：" + getBoost(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

//        HMMChineseTokenizer
        analyzer = new org.apache.lucene.analysis.core.SimpleAnalyzer();//简单分词器
//        System.out.println("new SimpleAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
        System.out.println("默认解析器打分结果：" + getBoost(analyzer, content));

        analyzer = new CJKAnalyzer();//二分法分词器
        System.out.println("new CKJAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

        analyzer = new KeywordAnalyzer();//关键词分词器
        System.out.println("new KeywordAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

        analyzer = new StopAnalyzer();//被忽略词分词器
        System.out.println("new StopAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

        analyzer = new MaxWordAnalyzer(dictionary);
        System.out.println("new MaxWordAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));

        analyzer = new ComplexAnalyzer(dictionary);
        System.out.println("new ComplexAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));

        analyzer = new IKAnalyzer();
        System.out.println("new IKAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));

        analyzer = new MMSegAnalyzer(dictionary);
        System.out.println("new MMSegAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
        analyzer = new SmartChineseAnalyzer();
        System.out.println("new SmartChineseAnalyzer()解析输出：" + getAnalyzedStr(analyzer, content));
//        System.out.println("默认解析器打分结果：" + getBoost(analyzer,content));

    }

    /**
     * 取得权重结果，规则：在输入字符串中查找关键词，关键词出现频率越多，权重越高
     * @param str
     * @return
     * @throws Exception
     */
    public static float getBoost(Analyzer analyzer, String str) throws Exception {
        float result = 1.0F;

        // 默认解析器，可以更改为其它解析器
//        Analyzer analyzer = new ComplexAnalyzer(dictionary);
        List<String> list = getAnalyzedStr(analyzer, str);
        for (String word : lstNegativeWord) {
            if (list.contains(word)) {
                result += 10F; // 每出现一种负面关键词（不管出现几次），分值加10
            }
        }
        return result;
    }

    /**
     * 调用分词器解析输入内容，将每个分词加入到List，然后返回此List
     * @param content
     * @return
     * @throws Exception
     */
    public static List<String> getAnalyzedStr(Analyzer analyzer, String content) throws Exception {
        StringReader stringReader = new StringReader(content);
        TokenStream stream = analyzer.tokenStream(content, stringReader);
        stream.reset();
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);

        List<String> result = new ArrayList<String>();
        System.out.println("分词技术" + analyzer.getClass());
        while (stream.incrementToken()) {
            result.add(term.toString());
            System.out.print(term.toString() + "|");
        }
        System.out.println();
        return result;
    }

    public static void main(String[] args) throws Exception {
        // 注意：亭湖新区/亭湖这两个词必须存在于mmseg4j两个用户自定义词典中
        String content = "亭湖新区因 不雅 难过分 视频 被免官员 国企老总名单公布";
        System.out.println("原文：" + content);
        testAnalyzer(content);

    }
}
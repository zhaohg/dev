package com.zhaohg.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Analyzer实现扩展停用词
 * 继承自Analyzer并覆写createComponents(String)方法
 * 维护自己的停用词词典
 * 重写TokenStreamComponents，选择合适的过滤策略
 */
class StopAnalyzerExtend extends Analyzer {
    private CharArraySet stopWordSet;//停止词词典

    public StopAnalyzerExtend() {
        super();
        setStopWordSet(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    /**
     * @param stops 需要扩展的停止词
     */
    public StopAnalyzerExtend(List<String> stops) {
        this();
        /**如果直接为stopWordSet赋值的话，会报如下异常，这是因为在StopAnalyzer中有ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
         * ENGLISH_STOP_WORDS_SET 被设置为不可更改的set集合
         * Exception in thread "main" java.lang.UnsupportedOperationException
         * at org.apache.lucene.analysis.util.CharArrayMap$UnmodifiableCharArrayMap.put(CharArrayMap.java:592)
         * at org.apache.lucene.analysis.util.CharArraySet.add(CharArraySet.java:105)
         * at java.util.AbstractCollection.addAll(AbstractCollection.java:344)
         * at MyAnalyzer.<init>(AnalyzerDemo.java:146)
         * at MyAnalyzer.main(AnalyzerDemo.java:162)
         */
        //stopWordSet = getStopWordSet();
        stopWordSet = CharArraySet.copy(getStopWordSet());
        stopWordSet.addAll(StopFilter.makeStopSet(stops));
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> strings = new ArrayList<String>() {{
            add("小鬼子");
            add("美国佬");
        }};
        Analyzer analyzer = new StopAnalyzerExtend(strings);
        String content = "小鬼子 and 美国佬 are playing together!";
        TokenStream tokenStream = analyzer.tokenStream("myfield", content);
        tokenStream.reset();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            // 已经过滤掉自定义停用词
            // 输出：playing   together
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
    }

    public CharArraySet getStopWordSet() {
        return this.stopWordSet;
    }

    public void setStopWordSet(CharArraySet stopWordSet) {
        this.stopWordSet = stopWordSet;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new LowerCaseTokenizer();
        return new TokenStreamComponents(source, new StopFilter(source, stopWordSet));
    }
}
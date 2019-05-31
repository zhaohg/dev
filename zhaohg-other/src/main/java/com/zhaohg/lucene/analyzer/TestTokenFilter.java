package com.zhaohg.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TokenFilter主要用于TokenStream的过滤操作，用来处理Tokenizer或者上一个TokenFilter处理后的结果，
 * 如果是对现有分词器进行扩展或修改，推荐使用自定义TokenFilter方式。
 * 自定义TokenFilter需要实现incrementToken()抽象函数，并且该方法需要声明为final的，
 * 在此函数中对过滤Term的CharTermAttribute和PositionIncrementAttribute等属性进行操作,实现过滤功能
 */
public class TestTokenFilter {
    @Test
    public void test() throws IOException {
        String text = "Hi, Dr Wang, Mr Liu asks if you stay with Mrs Liu yesterday!";
        Analyzer analyzer = new WhitespaceAnalyzer();
        CourtesyTitleFilter filter = new CourtesyTitleFilter(analyzer.tokenStream("text", text));
        CharTermAttribute charTermAttribute = filter.addAttribute(CharTermAttribute.class);
        filter.reset();
        while (filter.incrementToken()) {
            System.out.print(charTermAttribute + " ");
        }
    }
}

/**
 * 自定义词扩展过滤器
 */
class CourtesyTitleFilter extends TokenFilter {
    Map<String, String> courtesyTitleMap = new HashMap<>();
    private CharTermAttribute termAttribute;

    /**
     * Construct a token stream filtering the given input.
     * @param input
     */
    protected CourtesyTitleFilter(TokenStream input) {
        super(input);
        termAttribute = addAttribute(CharTermAttribute.class);
        courtesyTitleMap.put("Dr", "doctor");
        courtesyTitleMap.put("Mr", "mister");
        courtesyTitleMap.put("Mrs", "miss");
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        String small = termAttribute.toString();
        if (courtesyTitleMap.containsKey(small)) {
            termAttribute.setEmpty().append(courtesyTitleMap.get(small));
        }
        return true;
    }
}
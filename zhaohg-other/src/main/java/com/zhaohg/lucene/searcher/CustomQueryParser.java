package com.zhaohg.lucene.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Description:禁用模糊查询和通配符查询，同样的如果希望禁用其它类型查询，只需要覆写对应的getXXXQuery方法即可
 */
public class CustomQueryParser extends QueryParser {
    public CustomQueryParser(String f, Analyzer a) {
        super(f, a);
    }

    public static void main(String args[]) throws ParseException {
        CustomQueryParser customQueryParser = new CustomQueryParser("filed", new WhitespaceAnalyzer());
        customQueryParser.parse("a?t");
        customQueryParser.parse("junit~");
    }

    @Override
    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        throw new ParseException("Fuzzy queries not allowed!");
    }

    @Override
    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        throw new ParseException("Wildcard queries not allowed!");
    }
}
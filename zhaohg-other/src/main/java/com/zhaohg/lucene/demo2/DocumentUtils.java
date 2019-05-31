package com.zhaohg.lucene.demo2;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class DocumentUtils {
    public static Document article2Document(Article article) {
        Document doc = new Document();
        doc.add(new Field("id", article.getId().toString(), TextField.TYPE_STORED));
        doc.add(new Field("title", article.getTitle(), TextField.TYPE_STORED));
        doc.add(new Field("content", article.getContent(), TextField.TYPE_STORED));
        return doc;
    }
    
    public static Article document2Ariticle(Document doc) {
        Article article = new Article();
        article.setId(Integer.parseInt(doc.get("id")));
        article.setTitle(doc.get("title"));
        article.setContent(doc.get("content"));
        return article;
    }
}
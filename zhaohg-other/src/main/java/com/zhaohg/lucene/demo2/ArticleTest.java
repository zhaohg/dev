package com.zhaohg.lucene.demo2;


import org.junit.Test;

import java.util.List;

public class ArticleTest {

    @Test
    public void testCreate() {
        IndexDao index = new IndexDao();
        Article article = new Article();
        article.setId(1);
        article.setTitle("标题阿法法师方式打发打发dfadfadf方式");
        article.setContent("方式等会拉风红色的合法身份是发挥双方还爱护肤哈分方式哈哈佛奥呵呵后返回labflsdflablf方式as");
        index.save(article);
        index.update(article);

        QueryResult result = index.search("方式", 0, 10);

        System.out.println(result.getCount());

        List<Article> list = result.getList();
        System.out.println(list.get(0).getContent());

        index.delete("1");
    }


}

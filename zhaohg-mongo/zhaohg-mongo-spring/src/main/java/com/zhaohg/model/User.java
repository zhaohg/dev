package com.zhaohg.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by com.zhaohg on 2017/7/21.
 */

/**
 * @Id - 文档的唯一标识，在mongodb中为ObjectId，它是唯一的，通过时间戳+机器标识+进程ID+自增计数器（确保同一秒内产生的Id不会冲突）构成。
 * @Document - 把一个java类声明为mongodb的文档，可以通过collection参数指定这个类对应的文档。
 * @DBRef - 声明类似于关系数据库的关联关系。ps：暂不支持级联的保存功能，当你在本实例中修改了DERef对象里面的值时，
 * 单独保存本实例并不能保存DERef引用的对象，它要另外保存，如下面例子的Person和Account。
 * @Field - 给映射存储到 mongodb 的字段取别名
 * @Indexed - 声明该字段需要索引，建索引可以大大的提高查询效率。
 * @CompoundIndex - 复合索引的声明，建复合索引可以有效地提高多字段的查询效率。
 * @GeoSpatialIndexed - 声明该字段为地理信息的索引。
 * @Transient - 映射忽略的字段，使该字段不会保存到mongodb。
 * @PersistenceConstructor - 声明构造函数，作用是把从数据库取出的数据实例化为对象。该构造函数传入的值为从DBObject中取出的数据。
 * 注：查询表达式必须遵循指定的索引的顺序
 */
@Document(collection = "user")
@CompoundIndexes({
        @CompoundIndex(name = "name_age", def = "{'name': 1, 'age': -1}")
})
public class User {

    @Id
    private Long    id;
    @Indexed(unique = true)
    private String  mobile;
    @Indexed
    private String  name;
    private int     age;
    @Transient
    private Integer total;
    private String  email;
    private int     gender;
    private Date    createTime;

    @DBRef
    private Account            account;
    @DBRef
    private List<MessageBatch> messageBatches;

//    @PersistenceConstructor
//    public User(String mobile, String name, String email, int gender) {
//        this.mobile = mobile;
//        this.name = name;
//        this.email = email;
//        this.gender = gender;
//    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<MessageBatch> getMessageBatches() {
        return messageBatches;
    }

    public void setMessageBatches(List<MessageBatch> messageBatches) {
        this.messageBatches = messageBatches;
    }
}

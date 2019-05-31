import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by zhaohg on 2018/9/25.
 */
public class Examples {
    
    public static void main(String[] args) {
        //1. 连接到本地mongodb实例 localhost
        MongoClient client = new MongoClient();

//        MongoClient client = new MongoClient(
//                new MongoClientURI("mongodb://host1:27017,host2:27017,host3:27017/?replicaSet=myReplicaSet&authSource=db1&ssl=true")
//        );
//        MongoClient client = new MongoClient(Arrays.asList(
//                new ServerAddress("host1", 27017),
//                new ServerAddress("host2", 27017),
//                new ServerAddress("host3", 27017)
//            )
//        );

//        MongoCredential credential = MongoCredential.createCredential(user, database, password);
//        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();
//        MongoClient client = new MongoClient(new ServerAddress("host1", 27017), Arrays.asList(credential), options);
        
        // 访问 Database（demo3）
        MongoDatabase database = client.getDatabase("demo3");
        // 访问集合 （example1）
        MongoCollection<Document> collection = database.getCollection("example1");
        
        // 2. 插入数据
        List<Document> documents = asList(
                new Document("name", "Sun Bakery Trattoria").append("stars", 4)
                        .append("categories", asList("Pizza", "Pasta", "Italian", "Coffee", "Sandwiches"))
                        .append("location", asList(-73.92502, 40.8279556)),
                new Document("name", "Blue Bagels Grill").append("stars", 3)
                        .append("categories", asList("Bagels", "Cookies", "Sandwiches"))
                        .append("location", asList(-73.92502, 40.8279556)),
                new Document("name", "Hot Bakery Cafe").append("stars", 4)
                        .append("categories", asList("Bakery", "Cafe", "Coffee", "Dessert"))
                        .append("location", asList(-73.92502, 40.8279556)),
                new Document("name", "XYZ Coffee Bar").append("stars", 5)
                        .append("categories", asList("Coffee", "Cafe", "Bakery", "Chocolates"))
                        .append("location", asList(-73.92502, 40.8279556)),
                new Document("name", "456 Cookies Shop").append("stars", 4)
                        .append("categories", asList("Bakery", "Cookies", "Cake", "Coffee"))
                        .append("location", asList(-73.92502, 40.8279556)));
        
        collection.insertMany(documents);
        
        
        //  3. 查询数据
        FindIterable<Document> results = collection.find(new Document("stars", new Document("$gte", 2)
                .append("$lt", 5)).append("categories", "Bakery")).sort(Sorts.ascending("name"));
        System.out.println(results.into(new ArrayList<>()).size());
        
        
        // 4. 创建索引  ascending升序
        collection.createIndex(Indexes.ascending("name"));
        //Compound Indexes
//        collection.createIndex(Indexes.compoundIndex(Indexes.descending("stars"), Indexes.ascending("name")));
//        //Text Indexes
//        collection.createIndex(Indexes.text("name"));
//        //Hashed Index
//        collection.createIndex(Indexes.hashed("_id"));
//        //Geospatial Indexes
//        collection.createIndex(Indexes.geo2dsphere("contact.location"));
//
//        //
//        IndexOptions indexOptions = new IndexOptions().unique(true);
//        collection.createIndex(Indexes.ascending("name", "stars"), indexOptions);
        
        
        // 5. close
        client.close();
    }
}

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaohg on 2018/8/31.
 */
public class Examples2 {
    
    public static void main(String[] args) {
        
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("demo3");
        // Pass BasicDBObject.class as the second argument
        MongoCollection<BasicDBObject> collection = database.getCollection("example2", BasicDBObject.class);
        
        // insert a document
        BasicDBObject document = new BasicDBObject("x", 1);
        collection.insertOne(document);
        
        // replace a document
        document.append("x", 2).append("y", 3);
        collection.replaceOne(Filters.eq("_id", document.get("_id")), document);
        
        // find documents
        List<BasicDBObject> objects = collection.find().into(new ArrayList<>());
        
        
    }
}

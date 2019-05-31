import com.mongodb.BasicDBObject;
import com.zhaohg.dao.mongodb.MongodbOrderDAO;
import com.zhaohg.statistic.monodb.MongodbCollections;
import com.zhaohg.statistic.monodb.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaohg on 2018/7/20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:application-*.xml"})
public class MongoTest {
    
    @Autowired
    private MongodbOrderDAO mongodbOrderDAO;
    
    
    @Test
    public void testMongodb() {
        
        Order ordertest = new Order();
        ordertest.setId(1L);
        ordertest.setName("name");
        ordertest.setTime(new Date());
        
        mongodbOrderDAO.insert(ordertest, MongodbCollections.ORDER_DATA);
        
        long id = 1;
        BasicDBObject query = new BasicDBObject("id", 1L);//new BasicDBObject("$lt", id)
        int count = mongodbOrderDAO.countOrder(query);
        if (count == 0) {
            return;
        }
        
        List<Order> list = new ArrayList<>(count);
        int pages = count / 50 + 1;
        for (int page = 0; page < pages; page++) {
            list.addAll(mongodbOrderDAO.queryOrderList(query, page * 50, 50));
        }
        
        
        Order order = mongodbOrderDAO.getOrder("name", 1);
//        System.out.println("------"+ordermessage.getTime());
    }
    
}

import keys.MyKeys;
import org.junit.Test;

/**
 * Created by zhaohg on 2018/3/11.
 */
public class UserKafkaTest {

    @Test
    public void test() {
        UserKafkaProducer producerThread = new UserKafkaProducer(MyKeys.topic);
        UserKafkaConsumer consumerThread = new UserKafkaConsumer(MyKeys.topic);

        producerThread.start();
        consumerThread.start();
    }

}

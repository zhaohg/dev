import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by zhaohg on 2018/3/10.
 */
public class UserKafkaProducer extends Thread {
    
    private static final String BROKER_LIST = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"; //broker的地址和端口
    
    private final KafkaProducer<Integer, String> producer;
    private final String                         topic;
    private final Properties                     props = new Properties();
    
    public UserKafkaProducer(String topic) {
        props.put("metadata.broker.list", BROKER_LIST);
        props.put("bootstrap.servers", BROKER_LIST);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        this.topic = topic;
    }
    
    @Override
    public void run() {
        int messageNo = 1;
        while (true) {
            String messageStr = "Message_" + messageNo;
            System.out.println("Send:" + messageStr);
            producer.send(new ProducerRecord<>(topic, messageStr));
            messageNo++;
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package eventaggregator.kafka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eventaggregator.config.AppConfig;

 public class KafkaProducer {
    private static kafka.javaapi.producer.Producer<String, String> producer;
    private static final String topic= "get_social_events";
    private final static Logger logger = Logger.getLogger(KafkaProducer.class);
    
    public void initialize(String kafkaHostName) {
    	Properties properties = new Properties();
		properties.put("metadata.broker.list", kafkaHostName);
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id","camus");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        producer = new Producer<String, String>(producerConfig);
    }
    public void publishMesssage( Integer publishFrquencyInSeconds) {            
          
    	// Start a thread pool which reads events from file every second and publishes to kafka
          ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
          ses.scheduleAtFixedRate(new Runnable() {
              @Override
              public void run() {
                  
            	BufferedReader in;
				try {
					in = new BufferedReader(new FileReader("events.txt"));
					String eventString;
	                  while ((eventString = in.readLine()) != null) {
	                	  long unixTimestamp = Instant.now().getEpochSecond();
	                      JSONObject eventJson = new JSONObject(eventString);
	                      // add ts field with current time
	                      eventJson.put("ts", unixTimestamp);
	                      System.out.println(eventJson);
	                      KeyedMessage<String, String> keyedMsg =
	                              new KeyedMessage<String, String>(topic, eventJson.toString());
	                      producer.send(keyedMsg); 
	                  }
	                in.close();
	                 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					logger.debug("\nERROR: Error in reading events.txt file");
					logger.debug(e.getStackTrace());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					logger.debug("\nERROR: Error in parsing json");
					logger.debug(e1.getStackTrace());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.debug("\nERROR: Error in reading events.txt file");
					logger.debug(e1.getStackTrace());
				}
				catch(Exception e)
				{
					logger.debug("\nERROR: Error occured while publishing messages to kafka");
					logger.debug(e.getStackTrace());
				}
            	  
              }
          }, 0, publishFrquencyInSeconds, TimeUnit.SECONDS);  
      return;
    }

    public static void main(String[] args) throws Exception {
    	  AppConfig appConfig = new AppConfig();
    	  appConfig.init();
          KafkaProducer kafkaProducer = new KafkaProducer();
          // Initialize producer
          System.out.println(appConfig.getKafkaHostName());
          kafkaProducer.initialize(appConfig.getKafkaHostName());            
          // Publish events to Kafka
          kafkaProducer.publishMesssage(appConfig.getPublishFrquencyInSeconds());
         
    }
}
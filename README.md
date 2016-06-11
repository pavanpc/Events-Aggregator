# Real time  events processor using kafka, spark streaming,elasticsearch and dropwizard

## Usage
<b> Prerequisite</b> 
   1. Java 8
   2. Python >2.7
   3. pip ( follow this if pip is not installed -  https://pip.pypa.io/en/stable/installing/)
   4. elasticsearch python client- run sudo pip install elasticsearch

Follow below steps.

1. Open a terminal window.

2. git clone https://github.com/pavanpc/Events-Aggregator.git

3. cd Events-Aggregator

4. chmod 755 setup.sh

5. ./setup.sh

6. Now set up spark. Click here to download spark pre built version- http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
                     
     <b> OR Do following to download from spark homepage </b>
   
   Go to http://spark.apache.org/docs/latest/building-spark.html
   
   Download spark->  version 1.6.0 , package type - Pre-built for Hadoop 2.6, download spark-1.6.0-bin-hadoop2.6.tgz

7. Copy the downloaded file   spark-1.6.0-bin-hadoop2.6.tgz to Events-Aggregator directory

8. tar xzf spark-1.6.0-bin-hadoop2.6.tgz

9. Run below command to start spark steaming program.
       
        spark-1.6.0-bin-hadoop2.6/bin/spark-submit --jars dependencies/spark-streaming-kafka-assembly_2.10-1.6.1.jar pyspark-streaming-event-processor/event_aggregator.py

10. Open a new terminal window to start mock kafka producer to publish events to kafka.

11. Move to same directory as the cloned git hub project(step 2).

11. chmod 755 kafka_publisher.sh

12. ./kafka_publisher.sh

The above step will start publishing events and go back to terminal where spark job is running to see the output.Once the spark  program exits, you can see events in events_data folder in the same directory.

To test the applicarion , use below mentioned Count/ Search API from browser.


## Design Details

###Data collection using the interface
1. I am assuming the events are sent by sdk to backend using a <b>Restful POST API asynchronously</b> so that the app side will be less loaded.

2. The api inturn publishes  events to kafka.

3. I have written a Kafka producer java app to mock this behavior. It pubslishes events to kafka using <b>SheduledExecutor Thread poool(10) servie </b> every second. The publish frequency is configurbale. This can be set in AppConfig.properties file. The code is available here- https://github.com/pavanpc/Events-Aggregator/tree/master/java-kafka-producer-mock
#### Why Kafka? 
   1. The events should be processed in realtime.Assuming huge number of events, I decided to go with kafka.
   
   2. Also the backend will not be heavy as it just needs to publish events to kafka.
   3. Its Realtime, Scalable to handle large data volume, Low latency and Fault tolerant.
   
   
###Real time event processing
1. I am using Spark streaming to process events.
2. The streaming program reads data from kafka and aggrgates results using a micro batch of interval 10 seconds. I have set it to 10 seconds just to test application behavior quickly.This should be configured based on production loads.
3. The program reads events, aggregates them using micro batches and stores(events)  in <b>elasticsearch</b>.
4. At the same time it stores events in local file system in the following format.

   <b> events_data/year=2016/month=6/day=10/hour=1 </b>
5. The data is stored using partitions like year, month , day and hour. This will be very helpful in processing data in future as explained later in the section below.
   
   #### Output of Spark streaming 
      <b> I. Aggregated events data in elasticsearch</b>

      i. The events are aggregated by event type and count will be stored for every spark microbatch window. Below is the docuement format in elasticsearch index (events_aggregation). <b>If count queries are more</b> we can use this data instead of doing aggregation on raw events.

              id                      window_start_time    event_type     count
          ex: 1465583533session_start 1465583533           link_clicked   50
   
      <b> II. Raw events  in elasticsearch</b>
      
      i. The raw events  with id field(eventy_type+ts) are inserted into elasticsearch. This data can be used to any analysis on events. Later this should be purged based on data size.

              id                     ts           event_type     params.key1    params.keyN
          ex: session_end1465484030  1465484030   session_end    android        valueN
   
       <b> III. Raw events  in local file system (or S3)</b>
         
      i. The raw events will be stored in local file system with partitions.

#### Why Spark Streaming?
  1. Spark stremaing works really well with a lot of events. It gives very good abstractions(RDD) for data transformation(map,reduce,filter,aggregation etc).Also its easy to change the soure(kafka) and destination(local file, s3,HDFS etc) with less modifications.
  
  2. I have used pyspark because , we can find good documentation and help for python and scala compared to java.Also its easy to find python programmers compared to scala.So I thought it would be good to go with python considering future code maintenance.
#### Why Elasticsearch?
  1. Elasticsearch provides very good SLA for reading data and aggregations. With my experience its<b> ~<20 ms</b> for normal search and aggregation queries for around <b>100 million records with 3 nodes(AWS medium size machine) cluster</b>. 
  2. It uses Inverted indexes(lucene) for storing data and hence its faster.
  3. Also, elasticsaerch works very well for use cases with <b>Write Onnce and Read Many</b> patterns(similar to given use case).
  4. Also, we can build <b>realtime dashboards using Kibana </b>on top of it. This will be very helpful for Business guys to do analysis in real time.
  5. The data in elasticsearch can be <b>purged for better perfomrance. We can store data for past 1 or 2 months and set TTL for purging historical  data</b>. In the mean time we can store data(stored in files/s3 ) in HDFS for later analysis. 
  
###Ability to request the aggregated data by time range
1. I have created a <b>RESTful api service using java dropwizard </b>.Using these api's we can run aggegations/ search queries on events.
2. The api uses data stored in elasticsearch.
3. I have used <b>DAO design pattern</b> to implements api's.The code is available here    https://github.com/pavanpc/Events-Aggregator/tree/master/java-dropwizard-api
4. Below are the api endpoints for the same.
   
   a. <b>Count API :</b>

       The API returns the count of events of given event_type between start_time and end_time intervals.
      <pre>
      **Request**
       **API : http://localhost:8080/getsocial/events/count/?event_type=session_end&start_time=1465484000&end_time=1465489089**
       **Method : GET**
       **QueryParam:**
           **event_type(Required)** : The event_type for which count is requied
           **start_time(Required)** : The start_time is Unix Timestamp in seconds
           **end_time(Required)**   : The end_time is Unix Timestamp in seconds
      **Response**
           **status** : Success/Error
           **result**: The result key will hold the actual count
           **timestamp**: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in logging/debgging) 
      **Example:**
           query: http:localhost:8080/getsocial/events/count/?event_type=session_end&start_time=1465484000&end_time=1465489089
           response:
                 {"result":185,"status":"SUCCESS","timeStamp":1465552391117}
      </pre>
      
   b. <b>Search API :</b>
       
       i. The api returns all the records for given event_type between start_time and end_time.
       
       ii. If event_type is not specfied, it returns all event_type records between start_time and end_time.
      <pre>
      **Request**
       **API : http://localhost:8080/getsocial/events/getallevents/?event_type=session_end&start_time=1465484000&end_time=1465489089**
       **Method : GET**
       **QueryParam:**
           **event_type(Optional)** : The event_type for which records need to be fetched.If not specified returns all records of all event types
           **start_time(Required)** : The start_time is Unix Timestamp in seconds
           **end_time(Required)**   : The end_time is Unix Timestamp in seconds
      **Response**
           **status** : Success/Error
           **result**: The result key will hold array of jsons(search result)
           **timestamp**: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in logging/debgging) 
      **Example:**
           query: http:localhost:8080/getsocial/events/count/?event_type=session_end&start_time=1465484000&end_time=1465489089
           response:
                 {"result":[
                             {"event_type":"session_end","id":"session_end1465484132","params":{"Location service           status":"Location service
                               On", "os_type":"android"},"ts":1465484132},
                            {"event_type":"session_end","id":"session_end1465484030","params":{"Location service status":"Location service On",  "os_type":"ios"},"ts":1465484030}
                 ],
                 "status":"SUCCESS",
                 "timeStamp":1465552391117}
                 
      </pre>
   
   
###Future Data processing and its storage
  
  1. The raw events can be stored in <b>S3</b>.Its very easy when data is stored in s3. 
  2. The data can be processed later using Hadoop/Map reduce jobs. Also we can store it as an external table in HIVE.
  3. We can do some useful analysis of this data using Map reduce jobs(hadoop/spark jobs) like client usage statistics, we can do user segmentations(if we also get client id along with events). These data can be sent as reports(simple csv would work) to clients so that clients can get better insights into their business. 
  4. With my past experience, storing data using <b>partitions like year,month.day,hour (above spark code uses this approach to store data in files just like s3)</b> helps in data processing. For example <b>map reduce / Hive jobs</b>. Partitions helps in reducing search space.
  
##Technology Used
  1. Python 2.7.6
  2. kafka 0.9.0
  3. pyspark -1.6
  4. elasticsearch - 2.2.0
  5. java 8
  6. java kafka client
  7. dropwizard - 0.9.2
  8. eleasticsearch java transport client -2.2.0
  9. java gson,jackson
  
## Another approach

1. If we have access to AWS stack , we can use Kinesis, Spark streaming and Dynamodb. Its very reliable and scalable. The flow will be as shown below.

      <b> Kinesis ---->  Spark Streaming(Aggregation) ----> DynanoDB </b> 
      

## Enhancements
   1. We can dockerize the whole application.
   2. We can use Mesos for efficient cluster uitilization.
   3. If  the SLA for data availability is less than ~10 ms , we should go with in memory NoSQL  DB's like Aerospike. It is    very fast and reliable.
 


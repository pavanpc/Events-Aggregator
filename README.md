# Real time  events processor using kafka, Streaming spark,elasticsearch and dropwizard

## Usage

Follow below steps.

1. Open a terminal window.

2. git clone https://github.com/pavanpc/Events-Aggregator.git

3. cd Events-Aggregator

4. chmod 755 setup.sh

5. ./setup.sh


## Design Details

###Data collection using the interface
1. I am assuming the events are sent by sdk to backend using a <b>Restful POST API asynchronously</b> so that the app side will be less loaded.

2. The api inturn publishes  events to kafka.

3. I have written a Kafka producer java app to mock this behavior. It pubslishes events to kafka using <b>SheduledExecutor Thread poool(10) servie </b> every second. The publish frequency is configurbale. This can be set in AppConfig.properties file.
#### Why Kafka? 
   1. The events should be processed in realtime.Assuming huge number of events, I decided to go with kafka.
   
   2. Also the backend will not be heavy as it just needs to publish events to kafka.
   
   
###Real time event processing
1. I am using Spark streaming to process events.
2. The streaming program reads data from kafka and aggrgates results using a micro batch of interval 10 seconds. I have set it to 10 seconds just to test application behavior quickly.This should be configured based on production loads.
3. The program reads events, aggregates them using micro batches and stores(events)  in <b>elasticsearch</b>.
4. At the same time it stores events in local file system in the following format.

   <b> events_data/year=2016/month=6/day=10/hour=1 </b>
5. The data is stored using partitions like year, month , day and hour. This will be very helpful in processing data in future as explained later in the section below.

#### Why Streaming spark?
  1. Spark stremaing works really well with a lot of events. It gives very good abstractions for data transformation(map,reduce,filter,aggregation etc).Also its easy to change the soure(kafka) and destination(local file, s3,HDFS etc) just with less modifications.
  
  2. I have used pyspark because , we can find good documentation and help for python and scala compared to java.Also its easy to find python programmers compared to scala.So I thought it would be good to go with python considering future code maintenance.
#### Why Elasticsearch?
  1. Elasticsearch provides very good SLA for reading data and aggregations. With my experience its<b> ~<20 ms</b> for normal search and aggregation queries for around <b>100 million records with 3 nodes(AWS medium size machine) cluster</b>. 
  2. It uses Inverted indexes(lucene) for storing data and hence its faster.
  3. Also, we can build <b>realtime dashboards using Kibana </b>on top of it. This will be very helpful for Business guys to do analysis in real time.
  4. The data in elasticsearch can be <b>purged for better perfomrance. We can store data for past 1 or 2 months and set TTL for purging historical  data</b>. In the mean time we can store data(stored in files/s3 ) in HDFS for later analysis. 
  
###Ability to request the aggregated data by time range
1. I have created a RESTful api service using java dropwizard.Using these api's we can run aggegations/ search queries on events.
2. The api uses data stored in elasticsearch.
3. Below are the api endpoints for the same.
   
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
           **event_type(Optional)** : The event_type for which records need to be fetched.If not specified returns all recods of all event types
           **start_time(Required)** : The start_time is Unix Timestamp in seconds
           **end_time(Required)**   : The end_time is Unix Timestamp in seconds
      **Response**
           **status** : Success/Error
           **result**: The result key will hold array of jsons(search result)
           **timestamp**: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in logging/debgging) 
      **Example:**
           query: http:localhost:8080/getsocial/events/count/?event_type=session_end&start_time=1465484000&end_time=1465489089
           response:
                 {"result":[{"event_type":"session_end","id":"session_end1465484132","params":{"Location service        status":"Location service
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
  



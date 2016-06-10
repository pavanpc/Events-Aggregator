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
#### Why Kakfka? 
   1. The events should be processed in realtime and assuming huge number of events being sent. I decided to go with kafka.
   
   2. Also the backend will not be heavy as it just needs to publish events to kafka.
   
   
###Real time event processing
1. I am using Spark streaming to process events.
2. The stremaing program reads data from kafka and aggrgates results using a micro batch of interval 10 seconds. I have set it to 10 seconds just to test application behavior quickly.
3. The program reads data and stores it in <b>elasticsearch</b>.
4. At the same time it stores events in local file system in the following format.

      </b> events_data/year=2016/month=6/day=10/hour=1 </b>
5. The data is stored using partitions like year, month , daya and hout. This will be very helpful in processing data in future as explained later in the section below.

#### Why Streaming spark?
  1. Spark stremaing works really well with a lot of events. It gives very good abstractions for data transformation(map,reduce,filter,aggregation etc).Also its easy to change the soure(kafka) and destination(local file, s3,HDFS etc) just with less modifications.
  
  2. I have used pyspark because , we can find good documentation and help for python and scala compared to java.Also its easy to find python programmers compared to scala.So I thought it would be good to go with python considering future code maintenance.
#### Why Elasticsearch?
  1. Elasticsearch provides very good SLA for reading data and aggregations. With my experience its<b> ~<20 ms</b? for normal search and aggregation queries for around <b>100 million records with 3 nodes(AWS medium size machine) cluster</b>. 
  2. It uses Inverted indexes(lucene) for storing data and hence its faster.
  3. Also, we can build realtime dashboards using Kibana on top of it. This will be very helpful for Business guys to do analysis in real time.
  4. The data in elasticsearch can be purged for better perfomrance. We can store data for past 1 or 2 months and set TTL for purging past data. In the mean time we can store data(stored in files/s3 ) in HDFS for later analysis. 
  
###Ability to request the aggregated data by time range
1. I have created a RESTful api service using java dropwizard.Using these api's we can run aggegations/ search queries on events.
2. The api uses data stored in elasticsearch.
3. Below are the api endpoints for the same.
   
   a. <b>Count API :</b>
      
      <pre>
      **Request**
          
          *** API :</b> http://localhost:8080/getsocial/events/count/?event_type=session_end&start_time=1465484000&end_time=1465489089 ***
         ***Method : GET ***
         ***QueryParam: ***
                   ***event_type(Required)*** : The event_type for which count is requied
                   ***start_time(Required)*** : The start_time is Unix Timestamp in seconds
                   ***end_time(Required)***   : The end_time is Unix Timestamp in seconds
      </pre>

  
  



# Real time  events processor using kafka, Streaming spark,elasticsearch and dropwizard

## Usage

Follow below steps.

1. Open a terminal window.

2. git clone https://github.com/pavanpc/Events-Aggregator.git

3. cd Events-Aggregator

4. chmod 755 setup.sh

5. ./setup.sh


## Design Details

#Data collection using the interface
1. I am assuming the events are sent by sdk to backend using a Restful POST API asynchronously so that the app side will be less loaded. 

2. The api inturn publish the data to kafka.

3. I have written a Kafka producer java app to mock this behavior. It pubslishes events to kafka using <b>SheduledExecutor Thread poool(10) servie </b> every second. The publish frequency is configurbale. This can be set in AppConfig.properties file.
### Why Kakfka? 
   1. The events should be processed in realtime and assuming huge number of events being sent. I decided to go with kafka.
   
   2. Also the backend will not be heavy as it just needs to publish events to kafka.


./dependencies/elasticsearch/elasticsearch-2.2.0/bin/elasticsearch -d
echo "Staring elasticsearch......."
sleep 10s
curl localhost:9200
curl -XPUT 'http://localhost:9200/events/events/session_start1465484047'  -d '{
        "event_type" : "session_start",
        "ts" : 1465484047,
        "params" : {
          "Location service status" : "Location service On",
          "os_type": "andrioid"
        },
        "id" : "session_start1465484047"
      }'
echo "Successfully started elasticsearch !!!!"

echo "Staring kafka......."
./dependencies/kafka_2.11-0.8.2.0/bin/zookeeper-server-start.sh dependencies/kafka_2.11-0.8.2.0/config/zookeeper.properties  > application.log  2>&1
sleep 5s
echo "Started kafka zookeper...."
./dependencies/kafka_2.11-0.8.2.0/bin/kafka-server-start.sh  dependencies/kafka_2.11-0.8.2.0/config/server.properties  >  application.log  2>&1
sleep 5s
echo "Started kafka server...."
./dependencies/kafka_2.11-0.8.2.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic get_social_events 

sleep 5s

#./dependencies/kafka_2.11-0.8.2.0/bin/kafka-topics.sh --zookeeper localhost:2181 —-list

echo "Successfully started kafka !!!!"

echo "Staring dropwizard service....."

cd dependencies/dropwizard/

java -jar getSocialEvents-1.0-SNAPSHOT.jar server configuration.yml  >  application.log  2>&1

cd ../..
echo "Successfully started dropwizard service  !!!!"
echo "Check application.log for  above services(elasticsearch, kafka) start up/logging details "

#./dependencies/elasticsearch/elasticsearch-2.2.0/bin/elasticsearch -d
#echo "Staring elasticsearch......."
#sleep 10s
#curl localhost:9200
#curl -XPUT 'http://localhost:9200/events/events/session_start1465484047'  -d '{
    #    "event_type" : "session_start",
     #   "ts" : 1465484047,
     #   "platform" : "Android",
     #   "params" : {
     #     "Location service status" : "Location service On"
     #   },
     #   "session_uuid" : "4c71b97e79fbbd5599945903e77065cef8a93cf8",
     #   "id" : "session_start1465484047"
     # }'
echo "Successfully started elasticsearch !!!!"

echo "Staring kafka......."
./dependencies/kafka_2.11-0.8.2.0/bin/zookeeper-server-start.sh dependencies/kafka_2.11-0.8.2.0/config/zookeeper.properties &
sleep 5s
echo "Started kafka zookeper...."
./dependencies/kafka_2.11-0.8.2.0/bin/kafka-server-start.sh  dependencies/kafka_2.11-0.8.2.0/config/server.properties &
sleep 5s
echo "Started kafka server...."
./dependencies/kafka_2.11-0.8.2.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic get_social_events 

sleep 5s

./dependencies/kafka_2.11-0.8.2.0/bin/kafka-topics.sh --zookeeper localhost:2181 —-list

echo "Successfully started kafka !!!!"


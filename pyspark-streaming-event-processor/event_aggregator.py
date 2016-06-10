from __future__ import print_function
import sys
from pyspark.streaming import StreamingContext
from pyspark import SparkContext,SparkConf
from pyspark.streaming.kafka import KafkaUtils
import json

import os
import sys
import json
from datetime import datetime
from elasticsearch import Elasticsearch
from pyspark import SparkContext
es = Elasticsearch(['localhost'])

BATCH_INTERVAL = 15  # How frequently to update (seconds)
def get_json_from_string(jsonString):
    '''
            Methos to convert the twitter feed event read from kafka to json
            Args:
                jsonString: even data as string
            Returns:
                json: converted json 
        '''
    try:
        json_object = json.loads(jsonString.encode('utf-8'))
    except ValueError:
        return False
    return json_object

def add_events_to_elasticsearch(eventJson):
    '''
            The method adds the event json to elasticsearch
            Args:
                eventJson: event as json
            Returns:
                modified eventJson (with id field added)
    '''
    from elasticsearch import Elasticsearch
    es = Elasticsearch(['localhost'])
    # Add id field
    id= eventJson['event_type']+ str(eventJson['ts'])
    eventJson['id']= id
    try:
        es.index(index="events", doc_type="events", id=id, body=eventJson)
    except Exception as e:
        print("Exception in es")
        print(e)
    return eventJson

def save_data_to_file_with_partitions(rdd):
    '''
            The method saves a given rdd to local file system
            Args:
                rdd: dstream rdd to be saved
            
    '''
    today = datetime.today()
    year=today.year
    month=today.month
    hour= today.hour
    minute=today.minute
    day=today.day
    directory= "./events_data/year="+str(year)+"/month="+str(month)+"/day="+str(day)+"/hour="+str(hour)+"/"
    if rdd.count() >0:
        if not os.path.exists(directory):
            os.makedirs(directory)
        final_single_rdd.saveAsTextFiles(directory+str(minute)+".txt")


if __name__ == "__main__":
    '''
           Main method which does following
           1. Reads data from kafka topic - 'get_social_events' in a BATCH_INTERVAL
           2. Converts events to json.
           3. Pushes events to elasticsearch
           3. Also stores the events for every bacth in files in current directory(Ex: events_data/year=2016/month=6/day=10/hour=1)
           TODO: Persist the model to HDFS system for later use.
        '''

    # Intialized Spark config
    conf = SparkConf().setAppName("Kafka-Spark-Event-Aggregator")
    sc = SparkContext(conf=conf)
    #sc.setLogLevel("ERROR")
    
    stream=StreamingContext(sc,5) #
    kafka_topic={'get_social_events':1}
    # Read the stream into dstreams
    # Note : this is the loclahost mode
    kafkaStream = KafkaUtils.createStream(stream, 'localhost:2181', "name", kafka_topic) 
    #print(kafkaStream.pprint())
    processed_events= kafkaStream.map(lambda event : get_json_from_string(event[1]) ).map(lambda eventJson: add_events_to_elasticsearch(eventJson))
    final_single_rdd= processed_events.repartition(1)
    save_data_to_file_with_partitions(final_single_rdd)
    
    stream.start()
    stream.awaitTermination()

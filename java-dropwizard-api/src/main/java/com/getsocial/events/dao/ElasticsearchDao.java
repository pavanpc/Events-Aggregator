package com.getsocial.events.dao;

import com.getsocial.events.apiEntities.ResponseUtils;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The DAO class for elasticsearch which runs ES queries using Transport client
 *
 */
@Getter
@Setter
public class ElasticsearchDao {
    private  final String indexName="events";
    private  final String type="events";
    private Client client;
    //Initialize elasticsearch client
    public ElasticsearchDao()
    {
    	
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();

        try {
            client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
	 * This end point which returns the count of events of given event_type
	 * between start_time and end_time intervals.
	 * 
	 * @param event_type(Required) : The event_type for which count is requied
	 *        start_time(Required) : The start_time is Unix Timestamp in seconds
	 *        end_time(Required) : The end_time is Unix Timestamp in seconds
	 * 
	 * @Retunns Response 
	 * 				status : Success/Error 
	 * 				result: The result key will hold the actual count 
	 * 				timestamp: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in
	 *                   logging/debgging)
	 * 
	 */
    public Response getEventCount(String event_type, Long start_date, Long end_date){
        SearchResponse response = client.prepareSearch(indexName)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("event_type", event_type))
                .setPostFilter(QueryBuilders.rangeQuery("ts").from(start_date).to(end_date))
                .setSize(0).setExplain(true)
                .execute()
                .actionGet();
        System.out.println(response);
        return ResponseUtils.ok(response.getHits().getTotalHits());
    }

    /**
	 * This end point returns all the records for given event_type between
	 * start_time and end_time.
	 * 
	 * @param event_type(Optional) : The event_type for which records need to be
	 *        fetched.If not specified returns all recods of all event types
	 *        start_time(Required) : The start_time is Unix Timestamp in seconds
	 *        end_time(Required) : The end_time is Unix Timestamp in seconds
	 * 
	 * @Retunns 
	 * 			Response 
	 * 				status : Success/Error 
	 * 				result: The result key will hold array of jsons(search result) 
	 *          	timestamp: timestamp is Unix Timestamp in seconds. This is the time at which api was
	 *                           run(helps in logging/debgging)
	 * 
	 */
    public Response getAllEvents(String event_type, Long startTime, Long endTime) {
        SearchResponse response;
        if(StringUtils.isBlank(event_type)){
             response=    client.prepareSearch(indexName)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchAllQuery())
                    .setPostFilter(QueryBuilders.rangeQuery("ts").from(startTime).to(endTime))
                    .setSize(10000).setExplain(true)
                    .execute()
                    .actionGet();

        }
        else
        {
             response= client.prepareSearch(indexName)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery("event_type",event_type))
                    .setPostFilter(QueryBuilders.rangeQuery("ts").from(startTime).to(endTime))
                    .setSize(10000).setExplain(true)
                    .execute()
                    .actionGet();
        }
        ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : response.getHits()) {
            searchResults.add(hit.getSource());
        }

        return ResponseUtils.ok(searchResults);
    }
}

package com.getsocial.events.service;

import com.getsocial.events.dao.ElasticsearchDao;

import javax.ws.rs.core.Response;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The service class which acts as a service layer between resoures and
 *       DAO's
 *
 */
public class EventsService {

	private static ElasticsearchDao eventsDao = new ElasticsearchDao();

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
	public Response getEventCount(String event_type, Long start_time,
			Long end_time) {

		return eventsDao.getEventCount(event_type, start_time, end_time);
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
		return eventsDao.getAllEvents(event_type, startTime, endTime);
	}
}

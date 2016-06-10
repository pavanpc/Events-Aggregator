package com.getsocial.events.resources;

import com.getsocial.events.Utils.Constants;
import com.getsocial.events.apiEntities.ResponseUtils;
import com.getsocial.events.service.EventsService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.lang.invoke.MethodHandles;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       Main resource class to handle all Events related api's
 *
 */
// Base url for the resource
@Path("/getsocial/events/")
@Produces(MediaType.APPLICATION_JSON)
public class EventsResource {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static EventsService eventService = new EventsService();

    /**
	   * This end point which returns the count of events of given event_type between start_time and end_time intervals. 
	   * @param
	   * 		event_type(Required) : The event_type for which count is requied
       *		start_time(Required) : The start_time is Unix Timestamp in seconds
       *		end_time(Required)   : The end_time is Unix Timestamp in seconds
	   * 		
	   * @Retunns
	   *        Response
       *			status : Success/Error
       *			result: The result key will hold the actual count
       *			timestamp: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in logging/debgging)
	   * 
	   */
    
    @GET
    @Path("/count/")
    public Response getEventCount(@QueryParam("event_type") String event_type,
                                  @QueryParam("start_time") String start_time,
                                  @QueryParam("end_time") String end_time) {
        log.info("start time is {} end time is {} event type is {}", start_time,end_time,event_type);
        if (StringUtils.isBlank(start_time)|| StringUtils.isBlank(end_time) || StringUtils.isBlank(event_type)) {
            return ResponseUtils.fail(Constants.ERROR400, 400);
        }
        Long startTime = Long.parseLong(start_time);
        Long endTime = Long.parseLong(end_time);
        if(startTime>endTime){
            return ResponseUtils.fail(Constants.ERROR600,400);
        }
        // call event service 
        return eventService.getEventCount(event_type, startTime, endTime);
    }

    /**
	   * This end point returns all the records for given event_type between start_time and end_time.
	   * @param
	   * 		event_type(Optional) : The event_type for which records need to be fetched.If not specified returns all recods of all event types
       *		start_time(Required) : The start_time is Unix Timestamp in seconds
       *		end_time(Required)   : The end_time is Unix Timestamp in seconds
	   * 		
	   * @Retunns
	   *        Response
     *			status : Success/Error
     *			result: The result key will hold array of jsons(search result)
     *			timestamp: timestamp is Unix Timestamp in seconds. This is the time at which api was run(helps in logging/debgging)
	   * 
	   */
    @GET
    @Path("/getallevents/")
    public Response getAllEvents(@QueryParam("event_type") String event_type,
                                  @QueryParam("start_time") String start_time,
                                  @QueryParam("end_time") String end_time) {
        log.info("start time is {} end time is {} event type is {}", start_time,end_time,event_type);

        if (StringUtils.isBlank(start_time)|| StringUtils.isBlank(end_time) ) {
            return ResponseUtils.fail(Constants.ERROR400, 400);
        }
        Long startTime = Long.parseLong(start_time);
        Long endTime = Long.parseLong(end_time);
        log.info("start time is {} end time is {} event type is {}", start_time,end_time,event_type);

        if(startTime>endTime){
            return ResponseUtils.fail(Constants.ERROR600,400);
        }

        return eventService.getAllEvents(event_type, startTime, endTime);
    }


/*
 * TODO:
 * Below is the api  template which provides pagination feature if search result count is very large
    @GET
    @Path("/getallevents/")
    //if limit and from are null, returns last 24 hours events of teh given type
    public Response getAllEvents(@QueryParam("event_type") String event_type,
                                 @QueryParam("from") String from,
                                 @QueryParam("size") String size) {
        if (event_type == null) {
            return ResponseUtils.fail(Constants.ERROR400, 400);
        }
        if (from == null || size == null) {
            return null;
            //EventsService.getAllEvents(event_type, 0, 100);
        } else
            return null;
        //EventsService.getAllEvents(event_type, from, size);

    }
 */
}

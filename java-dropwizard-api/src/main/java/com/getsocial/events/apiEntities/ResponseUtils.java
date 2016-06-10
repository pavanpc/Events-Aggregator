package com.getsocial.events.apiEntities;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.getsocial.events.Utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The Util class to handle API response related tasks
 *
 */
public class ResponseUtils {

	/**
	 * The method prepares the Success response object
	 * 
	 * @param data(Required) : The data to be added in final response
	 *        
	 * 
	 * @Retunns Response object with status and result
	 * 
	 */
    public static Response ok(Object data) {
        ResponseObject responseObject = new ResponseObject(data, ResponseStatus.SUCCESS, System.currentTimeMillis(), null);
        return Response.status(Constants.HTTP_SUCCESS).type(MediaType.APPLICATION_JSON).entity((gson().toJson(responseObject))).build();
    }
    
    /**
	 * The method prepares the Failure response object
	 * 
	 * @param message : The error message to be returned
	 * 		  httpStatus : Error code
	 *        
	 * 
	 * @Retunns Response object with status and error message
	 * 
	 */
    public static Response fail(String message, int httpStatus) {
        ResponseObject responseObject = new ResponseObject(null, ResponseStatus.FAILURE, System.currentTimeMillis(), message);
        return Response.status(httpStatus).type(MediaType.APPLICATION_JSON).entity((gson().toJson(responseObject))).build();
    }
    private static Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}

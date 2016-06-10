package com.getsocial.events.application;

import com.getsocial.events.configuration.AppConfiguration;
import com.getsocial.events.resources.EventsResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       Main class/Entry point for Dropwizard application
 *
 */

public class GetSocialEventsApp extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new GetSocialEventsApp().run(args);
    }


    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        
    }

    
    /**
	   * The method starts the dropwizard service.
	   * @param
	   * 		configuration- Dropwizard configuration like port specified in configuration.yml
	   *		environment- The environment object created by dropwizard. 		
	   */
    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) {

    	// register the  EventsResource 
        environment.jersey().register(EventsResource.class);

    }

}
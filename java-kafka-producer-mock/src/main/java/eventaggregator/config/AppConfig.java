package eventaggregator.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import eventaggregator.exception.AppConfigException;


/*
 * @author Pavan PC
 * @Date 9-June-2016
 * 
 * Contains all application related configurations like 
   1. kafka hostname
 * All the config properties are read from a configuration file
 */
public class AppConfig {
	private final static Logger logger = Logger.getLogger(AppConfig.class);
	private String kafkaHostName;
	private Integer publishFrquencyInSeconds;
	
	public Integer getPublishFrquencyInSeconds() {
		return publishFrquencyInSeconds;
	}

	private static final String appConfigFileName = "AppConfig.properties";
	private Properties prop = new Properties();
	private InputStream input = null;
	
	
	public String getKafkaHostName() {
		return kafkaHostName;
	}
	public AppConfig()
	{
		this.init();
	}
	
	/*
	 * Method to initialize application config properties
	 */
	public void init() {
		try {

			input = new FileInputStream(appConfigFileName);
			// load property file
			prop.load(input);
			this.kafkaHostName = prop.getProperty("kafkaHostname");
			this.publishFrquencyInSeconds=Integer.parseInt(prop.getProperty("publishFrquencyInSeconds"));
			System.out.println(this.kafkaHostName);
			
			

		}catch (FileNotFoundException e) {
			System.out.println(e.getStackTrace());
			logger.debug("\nERROR: AppConfig.properties file not found. Please copy it to current directory");
			logger.debug(e.getMessage());
			throw new AppConfigException(
					"ERROR: AppConfig.properties file not found. Please copy it to current directory");
		}
		catch (IOException e) {
			System.out.println(e.getStackTrace());
			logger.debug("Problem in initialiazing app configurations");
			logger.debug(e.getMessage());
			throw new AppConfigException(
					"\nERROR: Error in  Reading AppConfig properties ");

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.debug("Problem in initialiazing app configurations");
					logger.debug(e.getMessage());
					throw new AppConfigException(
							"ERROR: Error in Reading AppConfig properties ");
				}
			}
		}

	}

}
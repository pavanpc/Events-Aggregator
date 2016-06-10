package com.getsocial.events.configuration;

/**
 * Created by pavan.pc on 09/06/16.
 */
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The class for App related configurations
 *       Any app related configurations can be added here
 */
@Getter
@Setter
public class AppConfiguration extends Configuration {
    @NotEmpty
    private String template;
    // default message for base url
    @NotEmpty
    private String defaultName = "Stranger";
}
package com.getsocial.events.apiEntities;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.Produces;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The class with Response Status, We can also use ENums
 *
 */
public enum ResponseStatus {
    SUCCESS,
    FAILURE;
}

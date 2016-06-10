package com.getsocial.events.apiEntities;

/**
 * Created by pavan.pc on 09/06/16.
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Pavan PC
 * @Date 10-June-2016
 * 
 *       The class represents the final response object
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseObject {


    private Object result;

    private ResponseStatus status;
    private Long timeStamp;

    private String reason;

}

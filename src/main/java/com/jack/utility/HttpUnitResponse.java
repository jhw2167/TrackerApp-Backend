package com.jack.utility;

//Spring Imports
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class HttpUnitResponse {

    private HttpStatus status;
    private Object id;  //may be string or Numeric
    private Object data;
    private String message;

    /* Constructors */

    /**
     *
     * @param data JSON formated data string
     * @param message String error message
     * */
    public HttpUnitResponse(Object data, String id, String message, HttpStatus status) {
        setData(data);
        setMessage(message);
        setId(id);
        setStatus(status);
    }

}

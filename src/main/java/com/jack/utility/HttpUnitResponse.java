package com.jack.utility;

//Spring Imports
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

//model
import com.jack.model.dto.TransactionDto;

@Data
public class HttpUnitResponse {

    private HttpStatus status;
    private Object data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object id;  //may be string or Numeric

    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    public HttpUnitResponse(Object data, HttpStatus status) {
        this(data, null, null, status);
    }

    public HttpUnitResponse(TransactionDto dto, long tid, String message, HttpStatus status) {
        this(dto, String.valueOf(tid), message, status);
    }
}

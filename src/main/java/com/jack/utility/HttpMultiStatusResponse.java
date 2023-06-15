package com.jack.utility;

//Spring Imports
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpMultiStatusResponse {

    private String message;
    private List<HttpUnitResponse> responses;
    /* Constructors */

    /**
     *
     * @param responses JSON formated data string
     * @param message String error message
     * */
    public HttpMultiStatusResponse(List<HttpUnitResponse> responses, String message) {
        setResponses(responses);
        setMessage(message);
    }
}

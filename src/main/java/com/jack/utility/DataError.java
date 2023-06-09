package com.jack.utility;

//Spring Imports
import lombok.Data;

@Data
public class DataError {

    private Object data;
    private String message;

    /* Constructors */

    /**
     *
     * @param data JSON formated data string
     * @param message String error message
     * */
    public DataError(Object data, String message) {
        setData(data);
        setMessage(message);
    }

}

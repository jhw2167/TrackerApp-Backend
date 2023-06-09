package com.jack.utility;

//Spring Imports
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessErrorMessage {

    private String successMessage;
    private Object successData;

    private String errorMessage;
    private Object errorData;

    /* Constructors */

    public SuccessErrorMessage(String successMessage, Object successData, String errorMessage, Object errorData) {
        this.successMessage = successMessage;
        this.successData = successData;
        this.errorMessage = errorMessage;
        this.errorData = errorData;
    }

    public SuccessErrorMessage(String successMessage, Object successData) {
        this.successMessage = successMessage;
        this.successData = successData;
    }
}

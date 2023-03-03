package com.jack.utility;

/* Utility class for handling all Time and Date related aspects of this project:

    - All Times within this project will be standardized to UTC time
    - All relevant incoming times will have to be converted to UTC time
    - All outgoing times will be converted to user's time zone

 */

//Java Libs
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class Time {

    //VARS


    //END VARS

    /* Dummy Unused Constructor */

    //END CONSTRUCTORS

    //NOW in UTC standard time
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    //TODAY in UTC standard time
    public static LocalDate today() {
        return LocalDate.now(ZoneOffset.UTC);
    }
}

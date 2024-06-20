package com.jack.controller;

//Spring Imports
import com.jack.TrackerSpringProperties;
import com.jack.utility.HttpUnitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

//Java Imports


/*
    * WebController serves basic web configurations and settings to frontend React website

    * Author: Jack Welsh
    * Date: 06/19/24

 */

@RestController
@RequestMapping("/server")
public class ServerController
{
        /* State Variables */
        @Autowired
        TrackerSpringProperties properties;

        /* Constructor */

       /*
        *  GET /server/config - returns the server configuration for the frontend
        *
        *  Filter application properties for only frontend related configurations and return as HttpUnitResponse
        *
        */

        @GetMapping("/config")
        public ResponseEntity<HttpUnitResponse> getServerConfig(HttpServletRequest request)
        {
            try {

                //Filter Properties
                Map<String, String> frontendProperties = properties.getAll().entrySet().stream()
                        .filter(entry -> entry.getKey().contains("frontend"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                HttpUnitResponse rsp = new HttpUnitResponse(frontendProperties, HttpStatus.OK);
                return new ResponseEntity<>(rsp, rsp.getStatus());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

}

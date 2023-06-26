package com.jack.service;

//Java Imports
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

//Spring Imports
import com.jack.utility.HttpUnitResponse;
import com.plaid.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//Plaid Imports
import com.plaid.client.request.PlaidApi;
import com.plaid.client.ApiClient;
import retrofit2.Response;

//Project
import com.jack.TrackerSpringProperties;
import org.springframework.http.HttpStatus;


@Service
public class PlaidService {

        /* State Variables */
        TrackerSpringProperties properties;

        private PlaidApi plaidClient;
        private HashMap<String, String> apiKeys = new HashMap<>();


        /* Contrsutctor */
        @Autowired
        public PlaidService(TrackerSpringProperties properties)
        {
            super();
            this.properties = properties;
            String env = properties.get("plaid.env");
            apiKeys.put("clientId", properties.get("plaid.clientId"));
            apiKeys.put("secret", properties.get("plaid."+env+"Secret"));
            ApiClient apiClient = new ApiClient(apiKeys);
            apiClient.setPlaidAdapter( env.equals("dev") ? ApiClient.Development : ApiClient.Sandbox);
            plaidClient = apiClient.createService(PlaidApi.class);
        }

        /* Services */

    //Create Link token for session
    public HttpUnitResponse generateLinkToken() throws IOException {


        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser()
                .clientUserId(properties.get("plaid.clientUserId"))
                .legalName(properties.get("plaid.legalName"))
                .phoneNumber(properties.get("plaid.phoneNumber"))
                .emailAddress(properties.get("plaid.emailAddress"));

        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .user(user)
//                .clientId(apiKeys.get("clientId"))
//                .secret(apiKeys.get("secret"))
                .clientName(properties.get("plaid.clientName"))
                .products(Arrays.asList(Products.AUTH, Products.TRANSACTIONS))
                .countryCodes(Arrays.asList(CountryCode.US))
                .language(properties.get("plaid.language"));

        Response<LinkTokenCreateResponse> response = plaidClient
                .linkTokenCreate(request)
                .execute();

       return new HttpUnitResponse(response.isSuccessful() ? response.body() : response.errorBody(),
               HttpStatus.resolve(response.code()));
    }

    //Send Plaid Token from front end to Back end API


    //Send Permanent Access Token from front end to Back end API


}

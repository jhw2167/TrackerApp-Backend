package com.jack.service;

//Java Imports
import java.util.HashMap;

//Spring Imports
import org.springframework.beans.factory.annotation.Autowired;

//Plaid Imports
import com.plaid.client.request.PlaidApi;
import com.plaid.client.ApiClient;

//Project
import com.jack.TrackerSpringProperties;

public class PlaidService {

        @Autowired
        TrackerSpringProperties properties;

        private PlaidApi plaidClient;
        private HashMap<String, String> apiKeys = new HashMap<String, String>();


        /* Contrsutctor */
        public PlaidService()
        {
            apiKeys.put("clientId", properties.get("plaid.clientId"));
            apiKeys.put("secret", properties.get("plaid.clientSecret"));
            ApiClient apiClient = new ApiClient(apiKeys);
            apiClient.setPlaidAdapter(ApiClient.Sandbox);
            plaidClient = apiClient.createService(PlaidApi.class);
        }

}

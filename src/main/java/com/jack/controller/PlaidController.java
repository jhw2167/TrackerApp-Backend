package com.jack.controller;


//Spring Imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Java Imports
import java.io.IOException;

//Plaid Imports

//Project Imports
import com.jack.service.PlaidService;
import com.jack.utility.HttpUnitResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/finances/plaid")
public class PlaidController {

	@Autowired
	private PlaidService plaidService;


	@GetMapping("/link-token")
	public ResponseEntity<HttpUnitResponse> generateLinkToken(HttpServletRequest request) {
		try {
			HttpUnitResponse rsp = plaidService.generateLinkToken();
			return new ResponseEntity<>(rsp, rsp.getStatus());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Add more controller methods for PUT, PATCH, and DELETE if needed

}

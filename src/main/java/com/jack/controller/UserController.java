package com.jack.controller;


//Spring Imports

import com.jack.model.UserAccount;
import com.jack.model.Vendor;
import com.jack.service.TransactionService;
import com.jack.service.UserAccountService;
import com.jack.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//

/*
 * User controller servs endpoint /users/*** to deliver data for user facing API calls
 * like gathering Plaid account data or matching credentials
 *
 * @author Jack Welsh 3/25/2023
 */


//Combines @Controller and @ResponseBody annotations for a restful project
@RestController
@RequestMapping("/finances/users/{userId}")
public class UserController {


	/* State Variables */
	@Autowired
	UserAccountService us;


	/* Utility Methods */
	public ResponseEntity<UserAccount> getUserDetails(@PathVariable("userId") String userId) {
		return new ResponseEntity<UserAccount>(us.getUserAccountById(userId), HttpStatus.OK);
	}
	
	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */


}
//END USER CONTROLLERS
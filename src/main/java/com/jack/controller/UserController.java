package com.jack.controller;


//Spring Imports

import com.jack.model.dto.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jack.model.UserAccount;
import com.jack.service.UserAccountService;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/finances/users")
public class UserController {


	/* State Variables */
	@Autowired
	UserAccountService us;


	/* Utility Methods */

	/* GET METHODS */
	@GetMapping("/{userId}")
	public ResponseEntity<UserAccount> getUserDetails(HttpServletRequest request,
													  @PathVariable("userId") String userId) {
		return new ResponseEntity<UserAccount>(us.getUserAccountById(userId), HttpStatus.OK);
	}

	/* POST METHODS */
	@PostMapping
	public ResponseEntity<UserAccountDto> createUser(HttpServletRequest request,
												  @RequestBody UserAccountDto user) {
		UserAccount u = us.createUserAccount(user);
		return new ResponseEntity<UserAccountDto>( new UserAccountDto(u), HttpStatus.CREATED);
	}


	/* PUT METHODS */
	
	/* PATCH METHODS */
	
	/* DELETE METHODS */


}
//END USER CONTROLLERS
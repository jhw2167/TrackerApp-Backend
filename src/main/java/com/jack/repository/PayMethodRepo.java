package com.jack.repository;


//JAVA Imports

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//Project Imports
import com.jack.model.PayMethod;


/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface PayMethodRepo extends JpaRepository<PayMethod, Long>
{
	
	//simple find all pay methods
	public List<PayMethod> findAll();

	//simple find all pay methods by userId
	public List<PayMethod> findAllByUserId(String userId);


	//finds pay method by given id
	public PayMethod findByPmId(long pmId);
	
	//find all that partially match name

	/*
	When we want to validate the PayMethod exists under this particular user
	 */
	public Optional<PayMethod> findByUserIdAndPmId(String userId, long pmId);

	/*
		Find Pay Method by this name under this user
	 */
	public Optional<PayMethod> findByUserIdAndPayMethod(String payMethod, String userId);

}

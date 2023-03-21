package com.jack.repository;


//JAVA Imports

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
	
	//finds pay method by given id
	public PayMethod findByPmId(long pmId);
	
	//find all that partially match name
	@Query(value="SELECT * FROM pay_methods pm WHERE pm.pay_method LIKE :method", nativeQuery=true)
	public List<PayMethod> findAllLikeVendorName(@Param("method") String payMethod);
	
}

package com.jack.repository;


import java.sql.Date;
import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Spring Imports
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

//Project Imports
import com.jack.model.*;


/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface VendorMapperRepo extends JpaRepository<VendorMapper, Long>
{
	
	//simple find all transactions
	public List<VendorMapper> findAll();
	
	//find all that partially match name
	@Query(value="SELECT * FROM VENDOR_MAPPER v WHERE v.local_vendor_name LIKE :name", nativeQuery=true)
	public List<VendorMapper> findAllLikeVendorName(@Param("name") String name);
	
	@Query(value="SELECT * FROM VENDOR_MAPPER v "
			+ "WHERE v.cc_id=:cc_id AND "
			+ "v.credit_card=:credit_card", nativeQuery=true)
	public Optional<VendorMapper> findVendorByID(@Param("cc_id") String cc_id, @Param("credit_card") String creditCard);
	
	
}

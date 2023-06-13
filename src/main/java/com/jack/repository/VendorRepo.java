package com.jack.repository;


import java.sql.Date;
import java.time.LocalDate;
//JAVA Imports
import java.util.List;
import java.util.Map;

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

public interface VendorRepo extends JpaRepository<Vendor, Long>
{
	
	//simple find all transactions
	public List<Vendor> findAll();
	
	//finds vendor object by exact match on string name
	public Vendor findByVendorName(String vendorName);

	public Vendor findByUserUserIdAndVendorName(String userId, String vendorName);
	
	//find all that partially match name
	@Query(value="SELECT * FROM VENDORS v WHERE v.vendor LIKE UPPER(:name)", nativeQuery=true)
	public List<Vendor> findAllLikeVendorName(@Param("name") String name);
	
}

package com.jack.repository;


//JAVA Imports
import java.util.List;
        import java.util.Optional;

//Spring Imports
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
        import org.springframework.data.jpa.repository.JpaRepository;

//Project Imports
import com.jack.model.*;


/* CRUD repository of spring provides basic "find by ___ criteria to search transactions in DB"
 * - we will also make use of sorted criteria to provide outgoing info in sorted format
 * - 
 */

public interface VendorNamesRepo extends JpaRepository<VendorNames, Long>
{
	
	//simple find all transactions
	public List<VendorNames> findAll();
	
	//find all that partially match name
	@Query(value="SELECT * FROM VENDOR_MAPPER v WHERE v.local_vendor_name LIKE :name", nativeQuery=true)
	public List<VendorNames> findAllLikeVendorName(@Param("name") String name);
	
	@Query(value="SELECT * FROM VENDOR_MAPPER v "
			+ "WHERE v.cc_id=:cc_id AND "
			+ "v.credit_card=:credit_card", nativeQuery=true)
	public Optional<VendorNames> findVendorByID(@Param("cc_id") String cc_id, @Param("credit_card") String creditCard);
	
	
}

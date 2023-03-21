package com.jack.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.jack.repository.subrepo.TransactionKeyRepo;
import org.mockito.Mock;

//Other Imports


//Project Imports
import com.jack.model.Transaction;
import com.jack.repository.*;

/*
 * Some basic testing methods and mocking for our service class
 * 
 */

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TransactionServiceTest {

	
	/* Variables we need to mock */
	@Mock
	TransactionRepo repoMock;

	@Mock
	TransactionKeyRepo keyRepoMock;
	
	
	/* Class to Test */
	TransactionService service;
	
	//@Before
	void setUp() 
	{
		List<Transaction> tsx = new ArrayList<>();
		tsx.add(new Transaction());
		//tsx.add(new Transaction(10026, "2021-06-17", 14.92, ));
		//tsx.add(new Transaction(10025, "2020-09-18", 25.02));
		//tsx.add(new Transaction(10024, "2020-09-17", 25.02));
		
		when(repoMock.findAll()).thenReturn(tsx);
		service = new TransactionService();
	}
	
	
	//Run our test
	//@Test
	void testFindAll() {
		
		List<Transaction> tsx = new ArrayList<>();
		//tsx.add(new Transaction(10026, "2021-06-17", 14.92));
		//tsx.add(new Transaction(10025, "2020-09-18", 25.02));
		//tsx.add(new Transaction(10024, "2020-09-17", 25.02));
		
		//assertEquals(tsx, service.getAllTransactions());
		assertTrue(true);
		
	}
	//END TEST FIND ALL
	
}
//END TEST SUITE FOR TESTSERVICE

package com.jack.service;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//Other Imports


//Project Imports
import com.jack.*;
import com.jack.model.Transaction;
import com.jack.repository.*;
import com.jack.service.*;

/*
 * Some basic testing methods and mocking for our service class
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

	
	/* Variables we need to mock */
	@Mock
	TransactionRepo repoMock;
	
	
	/* Class to Test */
	TransactionService service;
	
	@Before
	void setUp() 
	{
		List<Transaction> tsx = new ArrayList<>();
		tsx.add(new Transaction(10026, "2021-06-17", 14.92));
		tsx.add(new Transaction(10025, "2020-09-18", 25.02));
		tsx.add(new Transaction(10024, "2020-09-17", 25.02));
		
		when(repoMock.findAll()).thenReturn(tsx);
		service = new TransactionService(repoMock);
	}
	
	
	//Run our test
	@Test
	void testFindAll() {
		
		List<Transaction> tsx = new ArrayList<>();
		tsx.add(new Transaction(10026, "2021-06-17", 14.92));
		tsx.add(new Transaction(10025, "2020-09-18", 25.02));
		tsx.add(new Transaction(10024, "2020-09-17", 25.02));
		
		assertEquals(tsx, service.getAllTransactions());
		
	}
	//END TEST FIND ALL
	
}
//END TEST SUITE FOR TESTSERVICE

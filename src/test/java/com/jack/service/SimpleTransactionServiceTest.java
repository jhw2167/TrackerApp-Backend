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
import com.jack.model.SimpleTransaction;
import com.jack.repository.*;
import com.jack.service.*;

/*
 * Some basic testing methods and mocking for our service class
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleTransactionServiceTest {

	
	/* Variables we need to mock */
	@Mock
	SimpleTransactionRepo repoMock;
	
	
	/* Class to Test */
	SimpleTransactionService service;
	
	@Before
	void setUp() 
	{
		List<SimpleTransaction> tsx = new ArrayList<>();
		tsx.add(new SimpleTransaction(10026, "2021-06-17", 14.92));
		tsx.add(new SimpleTransaction(10025, "2020-09-18", 25.02));
		tsx.add(new SimpleTransaction(10024, "2020-09-17", 25.02));
		
		when(repoMock.findAll()).thenReturn(tsx);
		service = new SimpleTransactionService(repoMock);
	}
	
	
	//Run our test
	@Test
	void testFindAll() {
		
		List<SimpleTransaction> tsx = new ArrayList<>();
		tsx.add(new SimpleTransaction(10026, "2021-06-17", 14.92));
		tsx.add(new SimpleTransaction(10025, "2020-09-18", 25.02));
		tsx.add(new SimpleTransaction(10024, "2020-09-17", 25.02));
		
		assertEquals(tsx, service.getAllTransactions());
		
	}
	//END TEST FIND ALL
	
}
//END TEST SUITE FOR TESTSERVICE

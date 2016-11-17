package com.at4wireless.database.cdm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCDM
{	
	private static String CDM_SF = "ICSCDM_CDMServiceFramework";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCDM()
	{
		Ics ics = icsDAO.get(CDM_SF);
		assertNotNull("Retrieved ICS is null", ics);
		// Common Device Model Service Framework support is optional, so SCR expression must be empty
		assertNull("SCR expression is not empty", ics.getScrExpression());
	}
}
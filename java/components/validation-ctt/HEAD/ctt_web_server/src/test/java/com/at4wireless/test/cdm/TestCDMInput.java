package com.at4wireless.test.cdm;

import static org.junit.Assert.*;

import java.util.List;

import javax.script.ScriptException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.test.Helper;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCDMInput
{	
	private final static String HID = "ICSCDM_HidInterface";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testHid()
	{
		Ics ics = icsDAO.get(HID);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, Hid interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, Hid is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}

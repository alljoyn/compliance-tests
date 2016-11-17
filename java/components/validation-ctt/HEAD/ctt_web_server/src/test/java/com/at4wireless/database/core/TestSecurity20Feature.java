package com.at4wireless.database.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.script.ScriptException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.database.Helper;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSecurity20Feature
{	
	private final static String SECURITY_20 = "ICSCO_Security20";
	private final static String APPLICATION_INTERFACE = "ICSCO_ApplicationInterface";
	private final static String MANAGED_APPLICATION_INTERFACE = "ICSCO_ManagedApplicationInterface";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	public void testSecurity20()
	{
		Ics ics = icsDAO.get(SECURITY_20);
		assertNotNull("Retrieved ICS is null", ics);
		// Security 2.0 Feature support is optional, so SCR expression must be empty
		assertNull("SCR expression is not empty", ics.getScrExpression());
	}
	
	@Test
	@Transactional
	public void testApplicationInterface()
	{
		Ics ics = icsDAO.get(APPLICATION_INTERFACE);
		assertNotNull("Retrieved ICS is null", ics);

		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not 2", variables.size(), 2);
		
		try
		{
			// If Security 2.0 is not supported, Application interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If Security 2.0 is supported, Application interface must be included
			assertFalse("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));

		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testManagedApplicationInterface()
	{
		Ics ics = icsDAO.get(MANAGED_APPLICATION_INTERFACE);
		assertNotNull("Retrieved ICS is null", ics);

		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not 2", variables.size(), 2);
		
		try
		{
			// If Security 2.0 is not supported, ManagedApplication interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If Security 2.0 is supported, ManagedApplication interface must be included
			assertFalse("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));

		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}
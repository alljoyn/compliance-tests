package com.at4wireless.test.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import com.at4wireless.test.Helper;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAuthMechanisms
{	
	private final static String RSA_KEYX = "ICSCO_RsaKeyX";
	private final static String PIN_KEYX = "ICSCO_PinKeyX";
	private final static String SRP_KEYX = "ICSCO_SrpKeyX";
	private final static String SRP_LOGON = "ICSCO_SrpLogon";
	private final static String ECDHE_NULL = "ICSCO_EcdheNull";
	private final static String ECDHE_PSK = "ICSCO_EcdhePsk";
	private final static String ECDHE_ECDSA = "ICSCO_EcdheEcdsa";
	private final static String ECDHE_SPEKE = "ICSCO_EcdheSpeke";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	public void testRsaKeyX()
	{
		Ics ics = icsDAO.get(RSA_KEYX);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// If ALLJOYN_ECDHE_SPEKE is not enabled (not v16.04), at least one of the supported auth mechanisms must be enabled
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			// If ALLJOYN_ECDHE_SPEKE is enabled (v16.04), ALLJOYN_RSA_KEYX must be disabled
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testPinKeyX()
	{
		Ics ics = icsDAO.get(PIN_KEYX);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// If ALLJOYN_ECDHE_SPEKE is not enabled (not v16.04), at least one of the supported auth mechanisms must be enabled
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			// If ALLJOYN_ECDHE_SPEKE is enabled (v16.04), ALLJOYN_PIN_KEYX must be disabled
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testSrpKeyX()
	{
		Ics ics = icsDAO.get(SRP_KEYX);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// At least one of the supported authentication mechanisms must be enabled
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testSrpLogon()
	{
		Ics ics = icsDAO.get(SRP_LOGON);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// At least one of the supported authentication mechanisms must be enabled
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testEcdheNull()
	{
		Ics ics = icsDAO.get(ECDHE_NULL);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 9);
		
		try
		{
			// If Security 2.0 is not enabled, at least an auth mechanism must be supported
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			// If Security 2.0 is enabled, ALLJOYN_ECDHE_NULL auth mechanism is mandatory
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testEcdhePsk()
	{
		Ics ics = icsDAO.get(ECDHE_PSK);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// At least an auth mechanism must be supported
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testEcdheEcdsa()
	{
		Ics ics = icsDAO.get(ECDHE_ECDSA);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 9);
		
		try
		{
			// If Security 2.0 is not enabled, at least an auth mechanism must be supported
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			// If Security 2.0 is enabled, ALLJOYN_ECDHE_ECDSA auth mechanism is mandatory
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testEcdheSpeke()
	{
		Ics ics = icsDAO.get(ECDHE_SPEKE);
		assertNotNull("Retrieved ICS is null", ics);
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// If neither ALLJOYN_RSA_KEYX nor ALLJOYN_PIN_KEYX are not enabled (not v14.12), at least one of the supported auth mechanisms must be enabled
			assertFalse("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			// Else (v14.12), ALLJOYN_ECDHE_SPEKE must be disabled
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}

package com.at4wireless.database.cdm;

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
import com.at4wireless.database.Helper;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCDMEnvironment
{	
	private final static String CURRENT_AIR_QUALITY_LEVEL = "ICSCDM_CurrentAirQualityLevelInterface";
	private final static String CURRENT_AIR_QUALITY = "ICSCDM_CurrentAirQualityInterface";
	private final static String CURRENT_HUMIDITY = "ICSCDM_CurrentHumidityInterface";
	private final static String CURRENT_TEMPERATURE = "ICSCDM_CurrentTemperatureInterface";
	private final static String TARGET_HUMIDITY = "ICSCDM_TargetHumidityInterface";
	private final static String TARGET_TEMPERATURE_LEVEL = "ICSCDM_TargetTemperatureLevelInterface";
	private final static String TARGET_TEMPERATURE = "ICSCDM_TargetTemperatureInterface";
	private final static String WATER_LEVEL = "ICSCDM_WaterLevelInterface";
	private final static String WIND_DIRECTION = "ICSCDM_WindDirectionInterface";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCurrentAirQualityLevel()
	{
		Ics ics = icsDAO.get(CURRENT_AIR_QUALITY_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 4);
		
		try
		{
			// If CDM Service Framework is not supported, CurrentAirQualityLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1 }, ics.getScrExpression()));		
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirQualityMonitor and it does not support CurrentAirQuality interface, CurrentAirQualityLevel must be included
			assertFalse("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1 }, ics.getScrExpression()));
			// If not, CurrentAirQualityLevel is optional
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("15th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("16th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1 }, ics.getScrExpression()));

		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCurrentAirQuality()
	{
		Ics ics = icsDAO.get(CURRENT_AIR_QUALITY);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, CurrentAirQuality interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirQualityMonitor, CurrentAirQuality interface is optional
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, CurrentAirQuality interface must not be included
			assertFalse("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCurrentHumidity()
	{
		Ics ics = icsDAO.get(CURRENT_HUMIDITY);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, CurrentHumidity interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Dehumidifier, CurrentHumidity interface must be included
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, TargetHumidity interface is optional
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCurrentTemperature()
	{
		Ics ics = icsDAO.get(CURRENT_TEMPERATURE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 4);
		
		try
		{
			// If CDM Service Framework is not supported, CurrentTemperature interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirConditioner or a Thermostat, CurrentTemperature interface must be included
			assertFalse("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1 }, ics.getScrExpression()));
			// Else, CurrentTemperature interface is optional
			assertTrue("15th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("16th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTargetHumidity()
	{
		Ics ics = icsDAO.get(TARGET_HUMIDITY);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, TargetHumidity interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Dehumidifier, TargetHumidity interface must be included
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, TargetHumidity interface is optional
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTargetTemperatureLevel()
	{
		Ics ics = icsDAO.get(TARGET_TEMPERATURE_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, TargetTemperatureLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, TargetTemperatureLevel is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTargetTemperature()
	{
		Ics ics = icsDAO.get(TARGET_TEMPERATURE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 5);
		
		try
		{
			// If CDM Service Framework is not supported, TargetTemperature interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1, 1 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("15th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("16th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirConditioner, Thermostat or FoodProbe, TargetTemperature interface must be included
			assertFalse("17th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("18th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("19th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("20th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("21th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("22th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1, 1 }, ics.getScrExpression()));
			assertFalse("23th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("24th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("25th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("26th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("27th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("28th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("29th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("30th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1, 1 }, ics.getScrExpression()));
			// Else, TargetTemperature interface is optional
			assertTrue("31th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("32th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testWaterLevel()
	{
		Ics ics = icsDAO.get(WATER_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, WaterLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, WaterLevel is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testWindDirection()
	{
		Ics ics = icsDAO.get(WIND_DIRECTION);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, WindDirection interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, TargetTemperatureLevel is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}

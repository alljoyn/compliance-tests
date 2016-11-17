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
public class TestCDMOperation
{	
	private final static String AIR_RECIRCULATION_MODE = "ICSCDM_AirRecirculationModeInterface";
	private final static String ALERTS = "ICSCDM_AlertsInterface";
	private final static String AUDIO_VIDEO_INPUT = "ICSCDM_AudioVideoInputInterface";
	private final static String AUDIO_VOLUME = "ICSCDM_AudioVolumeInterface";
	private final static String BATTERY_STATUS = "ICSCDM_BatteryStatusInterface";
	private final static String CHANNEL = "ICSCDM_ChannelInterface";
	private final static String CLIMATE_CONTROL_MODE = "ICSCDM_ClimateControlModeInterface";
	private final static String CLOSED_STATUS = "ICSCDM_ClosedStatusInterface";
	private final static String CURRENT_POWER = "ICSCDM_CurrentPowerInterface";
	private final static String CYCLE_CONTROL = "ICSCDM_CycleControlInterface";
	private final static String DISH_WASHING_CYCLE_PHASE = "ICSCDM_DishWashingCyclePhaseInterface";
	private final static String ENERGY_USAGE = "ICSCDM_EnergyUsageInterface";
	private final static String FAN_SPEED_LEVEL = "ICSCDM_FanSpeedLevelInterface";
	private final static String FILTER_STATUS = "ICSCDM_FilterStatusInterface";
	private final static String HEATING_ZONE = "ICSCDM_HeatingZoneInterface";
	private final static String HVAC_FAN_MODE = "ICSCDM_HvacFanModeInterface";
	private final static String LAUNDRY_CYCLE_PHASE = "ICSCDM_LaundryCyclePhaseInterface";
	private final static String MOISTURE_OUTPUT_LEVEL = "ICSCDM_MoistureOutputLevelInterface";
	private final static String OFF_CONTROL = "ICSCDM_OffControlInterface";
	private final static String ON_CONTROL = "ICSCDM_OnControlInterface";
	private final static String ON_OFF_STATUS = "ICSCDM_OnOffStatusInterface";
	private final static String OVEN_CYCLE_PHASE = "ICSCDM_OvenCyclePhaseInterface";
	private final static String PLUG_IN_UNITS = "ICSCDM_PlugInUnitsInterface";
	private final static String RAPID_MODE_TIMED = "ICSCDM_RapidModeTimedInterface";
	private final static String RAPID_MODE = "ICSCDM_RapidModeInterface";
	private final static String REMOTE_CONTROLLABILITY = "ICSCDM_RemoteControllabilityInterface";
	private final static String REPEAT_MODE = "ICSCDM_RepeatModeInterface";
	private final static String RESOURCE_SAVING = "ICSCDM_ResourceSavingInterface";
	private final static String ROBOT_CLEANING_CYCLE_PHASE = "ICSCDM_RobotCleaningCyclePhaseInterface";
	private final static String SOIL_LEVEL = "ICSCDM_SoilLevelInterface";
	private final static String SPIN_SPEED_LEVEL = "ICSCDM_SpinSpeedLevelInterface";
	private final static String TIMER = "ICSCDM_TimerInterface";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAirRecirculationMode()
	{
		Ics ics = icsDAO.get(AIR_RECIRCULATION_MODE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, AirRecirculationMode interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, AirRecirculationMode is optional
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
	public void testAlerts()
	{
		Ics ics = icsDAO.get(ALERTS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, Alerts interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, Alerts is optional
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
	public void testAudioVideoInput()
	{
		Ics ics = icsDAO.get(AUDIO_VIDEO_INPUT);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, AudioVideoInput interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, AudioVideoInput is optional
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
	public void testAudioVolume()
	{
		Ics ics = icsDAO.get(AUDIO_VOLUME);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, AudioVolume interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, AudioVolume is optional
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
	public void testBatteryStatus()
	{
		Ics ics = icsDAO.get(BATTERY_STATUS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, BatteryStatus interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, BatteryStatus is optional
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
	public void testChannel()
	{
		Ics ics = icsDAO.get(CHANNEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, Channel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Television, Channel interface must be included
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, Channel interface is optional
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
	public void testClimateControlMode()
	{
		Ics ics = icsDAO.get(CLIMATE_CONTROL_MODE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 4);
		
		try
		{
			// If CDM Service Framework is not supported, ClimateControlMode interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirConditioner or a Thermostat, ClimateControlMode interface must be included
			assertFalse("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1 }, ics.getScrExpression()));
			// Else, ClimateControlMode interface is optional
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
	public void testClosedStatus()
	{
		Ics ics = icsDAO.get(CLOSED_STATUS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 4);
		
		try
		{
			// If CDM Service Framework is not supported, ClosedStatus interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Refrigerator or a Freezer, ClosedStatus interface must be included
			assertFalse("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1, 1 }, ics.getScrExpression()));
			// Else, ClosedStatus interface is optional
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
	public void testCurrentPower()
	{
		Ics ics = icsDAO.get(CURRENT_POWER);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, CurrentPower interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, CurrentPower is optional
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
	public void testCycleControl()
	{
		Ics ics = icsDAO.get(CYCLE_CONTROL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 8);
		
		try
		{
			// If CDM Service Framework is not supported, CycleControl interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Clothes Washer, a Clothes Dryer, a Clothes Washer-Dryer, a Dish Washer, a Robot Cleaner or an Oven,
			// CycleControl interface must be included
			assertFalse("15th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("16th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("17th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("18th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("19th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("20th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("21st evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("22nd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("23rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("24th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("25th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("26th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			// Else, CycleControl interface is optional
			assertTrue("27th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("28th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDishWashingCyclePhase()
	{
		Ics ics = icsDAO.get(DISH_WASHING_CYCLE_PHASE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, DishWashingCyclePhase interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, DishWashingCyclePhase is optional
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
	public void testEnergyUsage()
	{
		Ics ics = icsDAO.get(ENERGY_USAGE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, EnergyUsage interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, EnergyUsage is optional
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
	public void testFanSpeedLevel()
	{
		Ics ics = icsDAO.get(FAN_SPEED_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, FanSpeedLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a CookerHood, FanSpeedLevel interface must be included
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, FanSpeedLevel interface is optional
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
	public void testFilterStatus()
	{
		Ics ics = icsDAO.get(FILTER_STATUS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, FilterStatus interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, FilterStatus is optional
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
	public void testHeatingZone()
	{
		Ics ics = icsDAO.get(HEATING_ZONE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 3);
		
		try
		{
			// If CDM Service Framework is not supported, HeatingZone interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is a Cooktop, HeatingZone interface must be included
			assertFalse("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0 }, ics.getScrExpression()));
			assertTrue("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 1 }, ics.getScrExpression()));
			// Else, HeatingZone interface is optional
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
	public void testHvacFanMode()
	{
		Ics ics = icsDAO.get(HVAC_FAN_MODE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, HvacFanMode interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, HvacFanMode is optional
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
	public void testLaundryCyclePhase()
	{
		Ics ics = icsDAO.get(LAUNDRY_CYCLE_PHASE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, LaundryCyclePhase interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, LaundryCyclePhase is optional
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
	public void testMoistureOutputLevel()
	{
		Ics ics = icsDAO.get(MOISTURE_OUTPUT_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, MoistureOutputLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, MoistureOutputLevel is optional
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
	public void testOffControl()
	{
		Ics ics = icsDAO.get(OFF_CONTROL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, OffControl interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, OffControl is optional
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
	public void testOnControl()
	{
		Ics ics = icsDAO.get(ON_CONTROL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, OnControl interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, OnControl is optional
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
	public void testOnOffStatus()
	{
		Ics ics = icsDAO.get(ON_OFF_STATUS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 17);
		
		try
		{
			// If CDM Service Framework is not supported, OnOffStatus interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertFalse("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertTrue("5th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertFalse("6th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertTrue("7th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("8th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("9th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("10th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("11th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("12th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("13th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("14th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("15th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("16th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("17th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("18th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("19th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("20th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("21st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("22nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("23rd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("24th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("25th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("26th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("27th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("28th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("29th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("30th evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertTrue("31st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertFalse("32nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported...
			// If Device is an AirConditioner, a Thermostat, a Humidifier, a Dehumidifier, an Air Purifier, an Electric Fan, a Clothes Washer,
			// a Clothes Dryer, a Clothes Washer-Dryer, a Dish Washer, a Robot Cleaner, an Oven, a Cooker Hood, a Television or a Set Top Box,
			// OnOffStatus interface must be included
			assertFalse("33rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }, ics.getScrExpression()));
			assertTrue("34th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 }, ics.getScrExpression()));
			assertFalse("35th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, ics.getScrExpression()));
			assertTrue("36th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 }, ics.getScrExpression()));
			assertFalse("37th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("38th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("39th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("40th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("41st evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("42nd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("43rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("44th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("45th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("46th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("47th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("48th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("49th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("50th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("51st evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("52nd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("53rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("54th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("55th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("56th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("57th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("58th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("59th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("60th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			assertFalse("61st evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("62nd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
			// Else, OnOffStatus interface is optional
			assertTrue("63rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ics.getScrExpression()));
			assertTrue("64th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testOvenCyclePhase()
	{
		Ics ics = icsDAO.get(OVEN_CYCLE_PHASE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, OvenCyclePhase interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, OvenCyclePhase is optional
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
	public void testPlugInUnits()
	{
		Ics ics = icsDAO.get(PLUG_IN_UNITS);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, PlugInUnits interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, PlugInUnits is optional
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
	public void testRapidMode()
	{
		Ics ics = icsDAO.get(RAPID_MODE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, RapidMode interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, RapidMode is optional
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
	public void testRapidModeTimed()
	{
		Ics ics = icsDAO.get(RAPID_MODE_TIMED);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, RapidModeTimed interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, RapidModeTimed is optional
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
	public void testRemoteControllability()
	{
		Ics ics = icsDAO.get(REMOTE_CONTROLLABILITY);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, RemoteControllability interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, RemoteControllability is optional
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
	public void testRepeatMode()
	{
		Ics ics = icsDAO.get(REPEAT_MODE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, RepeatMode interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, RepeatMode is optional
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
	public void testResourceSaving()
	{
		Ics ics = icsDAO.get(RESOURCE_SAVING);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, ResourceSaving interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, ResourceSaving is optional
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
	public void testRobotCleaningCyclePhase()
	{
		Ics ics = icsDAO.get(ROBOT_CLEANING_CYCLE_PHASE);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, RobotCleaningCyclePhase interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, RobotCleaningCyclePhase is optional
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
	public void testSoilLevel()
	{
		Ics ics = icsDAO.get(SOIL_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, SoilLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, SoilLevel is optional
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
	public void testSpinSpeedLevel()
	{
		Ics ics = icsDAO.get(SPIN_SPEED_LEVEL);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, SpinSpeedLevel interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, SpinSpeedLevel is optional
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
	public void testTimer()
	{
		Ics ics = icsDAO.get(TIMER);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, Timer interface must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, Timer is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}

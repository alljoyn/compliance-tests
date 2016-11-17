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
public class TestCDMDevices
{	
	private final static String REFRIGERATOR = "ICSCDM_RefrigeratorDevice";
	private final static String FREEZER = "ICSCDM_FreezerDevice";
	private final static String AIR_CONDITIONER = "ICSCDM_AirConditionerDevice";
	private final static String THERMOSTAT = "ICSCDM_ThermostatDevice";
	private final static String HUMIDIFIER = "ICSCDM_HumidifierDevice";
	private final static String DEHUMIDIFIER = "ICSCDM_DehumidifierDevice";
	private final static String AIR_PURIFIER = "ICSCDM_AirPurifierDevice";
	private final static String ELECTRIC_FAN = "ICSCDM_ElectricFanDevice";
	private final static String AIR_QUALITY_MONITOR = "ICSCDM_AirQualityMonitorDevice";
	private final static String CLOTHES_WASHER = "ICSCDM_ClothesWasherDevice";
	private final static String CLOTHES_DRYER = "ICSCDM_ClothesDryerDevice";
	private final static String CLOTHES_WASHER_DRYER = "ICSCDM_ClothesWasherDryerDevice";
	private final static String DISH_WASHER = "ICSCDM_DishWasherDevice";
	private final static String ROBOT_CLEANER = "ICSCDM_RobotCleanerDevice";
	private final static String OVEN = "ICSCDM_OvenDevice";
	private final static String COOKER_HOOD = "ICSCDM_CookerHoodDevice";
	private final static String COOKTOP = "ICSCDM_CooktopDevice";
	private final static String FOOD_PROBE = "ICSCDM_FoodProbeDevice";
	private final static String TELEVISION = "ICSCDM_TelevisionDevice";
	private final static String SET_TOP_BOX = "ICSCDM_SetTopBoxDevice";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testRefrigerator() {
		testDeviceIcs(REFRIGERATOR);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFreezer() {
		testDeviceIcs(FREEZER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAirConditioner() {
		testDeviceIcs(AIR_CONDITIONER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testThermostat() {
		testDeviceIcs(THERMOSTAT);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testHumidifier() {
		testDeviceIcs(HUMIDIFIER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDehumidifier() {
		testDeviceIcs(DEHUMIDIFIER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAirPurifier() {
		testDeviceIcs(AIR_PURIFIER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testElectricFan() {
		testDeviceIcs(ELECTRIC_FAN);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAirQualityMonitor() {
		testDeviceIcs(AIR_QUALITY_MONITOR);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testClothesWasher() {
		testDeviceIcs(CLOTHES_WASHER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testClothesDryer() {
		testDeviceIcs(CLOTHES_DRYER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testClothesWasherDryer() {
		testDeviceIcs(CLOTHES_WASHER_DRYER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDishWasher() {
		testDeviceIcs(DISH_WASHER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testRobotCleaner() {
		testDeviceIcs(ROBOT_CLEANER);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testOven() {
		testDeviceIcs(OVEN);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCookerHood() {
		testDeviceIcs(COOKER_HOOD);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCooktop() {
		testDeviceIcs(COOKTOP);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testFoodProbe() {
		testDeviceIcs(FOOD_PROBE);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTelevision() {
		testDeviceIcs(TELEVISION);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testSetTopBox() {
		testDeviceIcs(SET_TOP_BOX);
	}
	
	private void testDeviceIcs(String deviceIcs)
	{
		Ics ics = icsDAO.get(deviceIcs);
		assertNotNull("Retrieved ICS is null", ics);
		
		List<String> variables = Helper.getVariablesFromBooleanExpression(ics.getScrExpression());
		assertEquals("The number of variables is not the expected", variables.size(), 2);
		
		try
		{
			// If CDM Service Framework is not supported, Device must not be included
			assertTrue("1st evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 0 }, ics.getScrExpression()));
			assertFalse("2nd evaluation failed", Helper.evalExpression(variables, new int[]{ 0, 1 }, ics.getScrExpression()));
			// If CDM Service Framework is supported, Device is optional
			assertTrue("3rd evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 0 }, ics.getScrExpression()));
			assertTrue("4th evaluation failed", Helper.evalExpression(variables, new int[]{ 1, 1 }, ics.getScrExpression()));
		}
		catch (ScriptException se)
		{
			fail(se.getMessage());
		}
	}
}

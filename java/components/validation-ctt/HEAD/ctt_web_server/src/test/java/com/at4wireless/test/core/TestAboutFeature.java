package com.at4wireless.test.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAboutFeature
{	
	private final static String DATE_OF_MANUFACTURE = "ICSCO_DateOfManufacture";
	private final static String HARDWARE_VERSION = "ICSCO_HardwareVersion";
	private final static String SUPPORT_URL = "ICSCO_SupportUrl";
	private final static String ICON_INTERFACE = "ICSCO_IconInterface";
	private final static String DEVICE_NAME = "ICSCO_DeviceName";
	
	@Autowired
	private IcsDAO icsDAO;
	
	@Test
	@Transactional
	public void testDateOfManufacture()
	{
		Ics ics = icsDAO.get(DATE_OF_MANUFACTURE);
		assertNotNull(ics);
		// DateOfManufacture property is optional, so SCR expression must be empty
		assertNull(ics.getScrExpression());
	}
	
	@Test
	@Transactional
	public void testHardwareVersion()
	{
		Ics ics = icsDAO.get(HARDWARE_VERSION);
		assertNotNull(ics);
		// HardwareVersion property is optional, so SCR expression must be empty
		assertNull(ics.getScrExpression());
	}
	
	@Test
	@Transactional
	public void testSupportUrl()
	{
		Ics ics = icsDAO.get(SUPPORT_URL);
		assertNotNull(ics);
		// SupportUrl property is optional, so SCR expression must be empty
		assertNull(ics.getScrExpression());
	}
	
	@Test
	@Transactional
	public void testIconInterface()
	{
		Ics ics = icsDAO.get(ICON_INTERFACE);
		assertNotNull(ics);
		// About feature Icon interface is optional, so SCR expression must be empty
		assertNull(ics.getScrExpression());
	}
	
	@Test
	@Transactional
	public void testDeviceName()
	{
		Ics ics = icsDAO.get(DEVICE_NAME);
		assertNotNull(ics);
		// DeviceName property is optional, so SCR expression must be empty
		assertNull(ics.getScrExpression());
	}
}

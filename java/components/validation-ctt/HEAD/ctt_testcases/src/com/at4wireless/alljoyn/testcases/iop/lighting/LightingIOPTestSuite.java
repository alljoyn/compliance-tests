/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.testcases.iop.lighting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.CategoryKeys;
import com.at4wireless.alljoyn.core.iop.IOPMessage;
import com.at4wireless.alljoyn.testcases.parameter.Ics;

public class LightingIOPTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(LightingIOPTestSuite.class.getSimpleName());
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;

	Boolean pass = true;
	Boolean inconc = false;
	IOPMessage message = new IOPMessage();
	Map<String, List<String>> goldenUnits;
	Boolean ICSON_OnboardingServiceFramework = false;
	String name = null;

	public LightingIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits, Ics icsList)
	{
		this.goldenUnits = goldenUnits;
		ICSON_OnboardingServiceFramework = icsList.ICSON_OnboardingServiceFramework;

		try
		{
			runTestCase(testCase);
		}
		catch(Exception e)
		{
			fail("Exception: " + e.toString());
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		showPreconditions();		
		
		if (testCase.equals("IOP_LSF_Controller-v1-01"))
		{
			IOP_LSF_Controller_v1_01();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-02"))
		{
			IOP_LSF_Lamp_v1_02();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-03"))
		{
			IOP_LSF_Lamp_v1_03();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-04"))
		{
			IOP_LSF_Lamp_v1_04();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-05"))
		{
			IOP_LSF_Lamp_v1_05();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-06"))
		{
			IOP_LSF_Lamp_v1_06();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-07"))
		{
			IOP_LSF_Lamp_v1_07();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-08"))
		{
			IOP_LSF_Lamp_v1_08();
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-09"))
		{
			IOP_LSF_Lamp_v1_09(); 
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-10"))
		{
			IOP_LSF_Lamp_v1_10(); 
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-11"))
		{
			IOP_LSF_Lamp_v1_11(); 
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-12"))
		{
			IOP_LSF_Lamp_v1_12(); 
		}
		else if (testCase.equals("IOP_LSF_Lamp-v1-13"))
		{
			IOP_LSF_Lamp_v1_13(); 
		}
		else
		{
			fail("Test Case not valid");
		}

	}

	private void IOP_LSF_Controller_v1_01()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established between the DUT and %s (e.g.: DUT Lamp name will be displayed in %s "
				+ "screen). If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1, TBAD1));


		message.showMessage("Test Procedure", "Step 5) Verify that an AllJoyn connection has been "
				+ "established between the DUT and the rest of the Golden Units and if not, establish the requested steps to "
				+ "get all Golden Units AllJoyn connected to the DUT.");

		message.showMessage("Test Procedure", "Step 6) Command all Golden Units to display current DUT lamp "
				+ "switching on/off status.");

		int resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp status correctly displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp status is not correctly displayed in all Golden Units.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 7) Command %s to switch the DUT lamp on "
				+ "(switching it off previously if the lamp was already on).", TBAD1));

		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT switched on?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT is not switched on");
			return;
		}

		String TBAD2 = "";
		
		int step = 8;
		for (int i = 1; i <= 3; i++)
		{
			String gu = "";
			
			if (i == 1)
			{
				gu = TBAD1;
			}
			else if (i == 2)
			{
				category = CategoryKeys.SEVEN_ONE;
				TBAD2 = getGoldenUnitName(category);
				gu = TBAD2;
			}
			else if(i==3)
			{
				category = CategoryKeys.SEVEN_ONE;
				gu = getGoldenUnitName(category);
			}
			
			if (gu == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Verify that DUT lamp is on and that correct DUT"
					+ " status is shown in all Golden Units.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to switch the DUT lamp off.", step, gu));
			step++;

			resp = message.showQuestion("Pass/Fail Criteria", "Is DUT switched off?");
			
			if (resp != 0) //1==NO  null==(X)
			{
				fail("DUT is not switched off");
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Verify that DUT lamp is switched off and that correct "
					+ "DUT status is shown in all Golden Units.", step));
			step++;
		}

		message.showMessage("Test Procedure", String.format("Step %d) Switch %s off.", step, TBAD1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to switch the DUT lamp on and off.", step, TBAD2));
		step++;

		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off");
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_02()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		int step = 4;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SEVEN_ONE;
			String gu = getGoldenUnitName(category);
			
			if (gu == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Verify if an AllJoyn connection has been "
					+ "established between the DUT and %s (e.g.: DUT Lamp name will be displayed in %s "
					+ "screen). If not, establish an AllJoyn connection between the DUT and %s.", step, gu, gu, gu));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Verify that an AllJoyn connection has been "
					+ "established between the DUT and the rest of the Golden Units and if not, establish the requested steps to"
					+ "get all Golden Units AllJoyn connected to the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display DUT Lamp details.", step, gu));
			step++;

			int resp = message.showQuestion("Pass/Fail Criteria", "Are DUT lamp detail properties Dimmable, "
					+ "Color, VariableColorTemp and HasEffects displayed? Are the values obtained according DUT ICS?");
			
			if (resp != 0) //1==NO  null==(X)
			{
				fail("DUT lamp detail properties Dimmable, Color, VariableColorTemp and HasEffects "
						+ "are displayed and the values obtained are not according DUT ICS");
				return;
			}

			resp = message.showQuestion("Pass/Fail Criteria", "Are the values of the next LampDetails "
					+ "(Make, Model, Type, LampType, LampBaseType, LampBeamAngle, MinVoltage, MaxVoltage, "
					+ "Wattage, IncandescentEquivalent,\n MaxLumens, MinTemperature, MaxTermperature and LampID) "				 
					+ "Interface fields displayed according to Manufacturer specs? ");
			
			if (resp != 0) //1==NO  null==(X)
			{
				fail("The values of the next LampDetails Interface fields are not displayed according to Manufacturer specs");
				return;
			}
		}
	}

	private void IOP_LSF_Lamp_v1_03()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if(ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;				
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established between the DUT and %s (e.g.: DUT Lamp name will be displayed in %s "
				+ "screen). If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1, TBAD1));
		message.showMessage("Test Procedure", "Step 5) Verify that an AllJoyn connection has been established between the DUT and the rest of the "
				+ "Golden Units and if not, establish the requested steps to get all Golden Units AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure", "Step 6) Operate all Golden Units to display current value of Lamp color.");
		message.showMessage("Test Procedure", String.format("Step 7) If DUT supports ICSL_Dimmable, command %s "
				+ "to turn DUT lamp brightness to its higher value.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 8) If DUT supports ICSL_ColorTemperature, command %s to turn DUT lamp Color "
				+ "Temperature to its higher value.", TBAD1));		
		message.showMessage("Test Procedure", String.format("Step 9) If DUT supports ICSL_Dimmable, operate %s "
				+ "to turn DUT lamp saturation to its higher value.", TBAD1));		
		message.showMessage("Test Procedure", String.format("Step 10) Operate %s to modify Color value to its higher value.", TBAD1));		

		int resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp Color as expected by its "
				+ "Color value? Is current Color value displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp Color is not as expected by its Color value or current Color value is not displayed in all Golden Units");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 11) Operate %s to modify Color value to a medium value.", TBAD1));	

		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp Color modified to a new color "
				+ "as expected by its Color value? Is current Color value displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp Color is not modified to a new color as expected by its Color value or current Color value "
					+ "is not displayed in all Golden Units");
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 12) Operate %s to modify Color value to a low value.", TBAD2));	

		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp Color modified to a new color "
				+ "as expected by its Color value? Is current Color value displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp Color is not modified to a new color"
					+ " as expected by its Color value or current Color value"
					+ " is not displayed in all Golden Units.");
			return;
		}

		message.showMessage("Test Procedure", "Step 13) Repeat this step several times with all Golden Units "
				+ "setting Color value to a different value each time.");
		
		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp Color modified each time to a "
				+ "new color as expected by its Color value? Is current Color value displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp Color is not modified each time to a new color as expected by its Color value or current "
					+ "Color value is not displayed in all Golden Units.");
			return;
		}
		
		message.showMessage("Test Procedure", "Step 14) Note current Color value.");		
		message.showMessage("Test Procedure", "Step 15) Switch DUT off and on, and perform the required "
				+ "steps to reconnect it to the AP and to the AllJoyn session.");		
		message.showMessage("Test Procedure", String.format("Step 16) Switch off %s.", TBAD2));		
		message.showMessage("Test Procedure", String.format("Step 17) Operate %s to modify Color value to a different value.", TBAD1));		
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT lamp Color have the same value it had "
				+ "before being switched off and on? Is the value properly displayed in all Golden Units (except %s that is off)?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp Color has not the same value it had before being switched off and on or the value is not "
					+ "properly displayed in all Golden Units (except %s that is off)", TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 18) Switch %s on and operate it to perform an "
				+ "AllJoyn connection if necessary.", TBAD2));		

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp Color correctly updated in %s?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp Color is not correctly updated in %s.", TBAD2));
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_04()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;				
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established between the DUT and %s (e.g.: DUT Lamp name will be displayed in %s "
				+ "screen). If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1, TBAD1));
		message.showMessage("Test Procedure", "Step 5) Verify that an AllJoyn connection has been "
				+ "established between the DUT and the rest of the Golden Units and if not, establish the requested steps to "
				+ "get all Golden Units AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure", "Step 6) Operate all Golden Units to display current Lamp saturation value.");

		int resp = message.showQuestion("Pass/Fail Criteria", "Is correct lamp saturation value displayed in "
				+ "all Golden Units (same value in all of them)?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("Correct lamp saturation value is not displayed in all Golden Units (same value in all of them).");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 7) Command %s to turn DUT lamp brightness to its higher value.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to change DUT lamp saturation to its higher value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation modified to the new "
				+ "value set by %s? Is current saturation value displayed in all Golden Units?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not modified to the new value set by %s or current saturation value is not "
					+ "displayed in all Golden Units?", TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 9) Operate %s to modify lamp saturation value to a medium value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation modified to the "
				+ "new value set by %s? Is current saturation value displayed in all Golden Units?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not modified to the new value set by %s or current saturation value "
					+ "is not displayed in all Golden Units.", TBAD1));
			return;
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 10) Operate %s to modify saturation value to a low "
				+ "value.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation modified to the "
				+ "new value set by %s? Is current saturation value displayed in all Golden Units?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not modified to the new value set by %s or current saturation value "
					+ "is not displayed in all Golden Units.", TBAD2));
			return;
		}

		message.showMessage("Test Procedure", "Step 11) Repeat step with all Golden Units setting saturation "
				+ "value to a different value each time.");

		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp saturation each time modified "
				+ "to the new value set by the corresponding Golden Unit? Is current saturation value displayed in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp saturation is not each time modified to the new value set by the corresponding Golden Unit or "
					+ "current saturation value is not displayed in all Golden Units");
			return;
		}

		message.showMessage("Test Procedure", "Step 12) Note current Lamp saturation value.");
		message.showMessage("Test Procedure", "Step 13) Switch DUT off and on, and reconnect it if "
				+ "necessary to the AP and to the AllJoyn session.");
		message.showMessage("Test Procedure", String.format("Step 14) Switch off %s.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT lamp saturation have the same value it "
				+ "had before being switched off and on? Is the value properly displayed in all Golden Units (except %s that is off)?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not modified to the new value set by %s or current saturation value "
					+ "is not displayed in all Golden Units.", TBAD2));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 15) Operate %s to modify saturation value to a "
				+ "different value.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation modified to the "
				+ "new value set by %s? Is current saturation value displayed in all Golden Units (except %s that is off)?", TBAD2, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not modified to the new value set by %s or current saturation value "
					+ "is not displayed in all Golden Units (except %s that is off)", TBAD2, TBAD1));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 16) Switch %s on and operate it to perform an "
				+ "AllJoyn connection if necessary.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation correctly updated in %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not correctly updated in %s.", TBAD1));
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_05()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established between the DUT and %s (e.g.: DUT Lamp name will be displayed in %s "
				+ "screen). If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1, TBAD1));
		message.showMessage("Test Procedure", "Step 5) Verify that an AllJoyn connection has been "
				+ "established between the DUT and the rest of the Golden Units and if not, establish the requested steps to"
				+ "get all Golden Units AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure", "Step 6) Operate all Golden Units to display current Lamp color "
				+ "temperature value.");
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Is correct lamp color temperature value "
				+ "displayed in all Golden Units (same value in all of them)?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("Correct lamp color temperature value is not displayed in all Golden Units.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 7) If DUT supports ICSL_Dimmable, command %s "
				+ "to turn DUT lamp brightness to its higher value.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to change DUT lamp color "
				+ "temperature to its higher value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp color temperature modified to "
				+ "the new value set by %s? Is current color temperature value displayed in all Golden Units?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp color temperature is not modified to the new value set by %s or current color "
					+ "temperature value is displayed in all Golden Units.", TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 9) Operate %s to modify lamp color temperature "
				+ "value to a medium value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp color temperature modified to "
				+ "the new value set by %s? Is current color temperature value displayed in all Golden Units?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp color temperature is not modified to the new value set by %s or current color "
					+ "temperature value is not displayed in all Golden Units.", TBAD1));
			return;
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 10) Operate %s to modify color temperature value "
				+ "to a low value.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp color temperature modified to "
				+ "the new value set by %s? Is current color temperature value displayed in all Golden Units?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp color temperature is not modified to the new value set by %s or current color "
					+ "temperature value is not displayed in all Golden Units.", TBAD2));
			return;
		}

		message.showMessage("Test Procedure", "Step 11) Repeat step with all Golden Units setting color "
				+ "temperature value to a different value each time.");
		
		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp color temperature each time "
				+ "modified to the new value set by the corresponding Golden UnitIs current color temperature value displayed"
				+ "in all Golden Units?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp color temperature is not each time modified to the new value set by the corresponding "
					+ "TBAD or current color temperature value is not displayed in all Golden Units.");
			return;
		}

		message.showMessage("Test Procedure", "Step 12) Note current color temperature value.");
		message.showMessage("Test Procedure", "Step 13) Switch DUT off and on, and reconnect it if "
				+ "necessary to the AP and to the AllJoyn session.");
		message.showMessage("Test Procedure", String.format("Step 14) Switch off %s.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT lamp color temperature have the same "
				+ "value it had before being switched off and on? Is the value properly displayed in all Golden Units (except "
				+ "%s that is off)?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp color temperature has not the same value it had before being switched off and on and "
					+ "the value is properly displayed in all Golden Units.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 15) Operate %s to modify color temperature value "
				+ "to a different value.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp color temperature modified to "
				+ "the new value set by %s? Is current color temperature value displayed in all Golden Units (except "
				+ "%s that is off)?", TBAD2, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp color temperature is not modified to the new value set by %s or current color "
					+ "temperature value is not displayed in all Golden Units ", TBAD2));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 16) Switch %s on and operate it to perform an "
				+ "AllJoyn connection if necessary.", TBAD1));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp color temperature correctly updated in %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp color temperature is not correctly updated in %s", TBAD1));
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_06()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established between the DUT and %s. If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1));
		message.showMessage("Test Procedure", "Step 5) Verify that an AllJoyn connection has been "
				+ "established between the DUT and the rest of the Golden Units and if not, establish the requested steps to "
				+ "get all Golden Units AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure", "Step 6) Operate all Golden Units to display current Lamp "
				+ "brightness value.");
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Is correct lamp brightness value displayed "
				+ "in all Golden Units (same value in all of them)?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("Correct lamp brightness value is not displayed in all Golden Units.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 7) Operate %s to change DUT lamp brightness to its higher value.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to modify lamp brightness value to a medium value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp brightness not modified to the new "
				+ "value set by %s? Is current brightness value displayed in all Golden Units?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp brightness is not modified to the new value set by %s or current brightness value is not "
					+ "displayed in all Golden Units.", TBAD1));
			return;
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 9) Operate %s to modify brightness value to a low value.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp brightness modified to the "
				+ "new value set by %s? Is current brightness value displayed in all Golden Units?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp brightness is not modified to the new value set by %s or current brightness value "
					+ "is not displayed in all Golden Units?", TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", "Step 10) Repeat step 9 with all Golden Units setting brightness "
				+ "value to a different value each time.");
		message.showMessage("Test Procedure", "Step 11) Note current brightness value.");
		message.showMessage("Test Procedure", "Step 12) Switch DUT off and on, and reconnect it if "
				+ "necessary to the AP and to the AllJoyn session.");
		message.showMessage("Test Procedure", String.format("Step 13) Switch off %s.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT lamp brightness have the same value"
				+ "it had before being switched off and on? Is the value properly displayed in all Golden Units (except "
				+ "%s that is off)?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp brightness has not the same value it had before being switched off and on or the"
					+ "value is not properly displayed in all Golden Units (except %s that is off).", TBAD1));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 14) Operate %s to modify brightness value to a "
				+ "different value.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp brightness modified to the "
				+ "new value set by %s? Is current brightness value displayed in all Golden Units (except %s that is off)?", TBAD2, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp brightness is not  modified to the new value set by %s or current brightness value "
					+ "is displayed not in all Golden Units (except %s that is off).", TBAD2, TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 15) Switch %s on and operate it to perform an "
				+ "AllJoyn connection if necessary.", TBAD1));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp brightness correctly updated in %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp brightness is not correctly updated in %s", TBAD1));
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_07()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", "Step 4) Verify if an AllJoyn connection has been "
				+ "established among all Golden Units. If not, establish the AllJoyn connection.");

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_TWO;
		String TBAD3 = getGoldenUnitName(category);
		
		if (TBAD3 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_TWO;
		String TBAD4 = getGoldenUnitName(category);
		
		if (TBAD4 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to switch on the lamps of %s "
				+ "and %s and to set their brightness to their maximum value.", TBAD1, TBAD3, TBAD4));
		message.showMessage("Test Procedure", "Step 6) Switch on DUT and connect it to the AP network if it is not connected yet.");
		message.showMessage("Test Procedure", "Step 7) Verify that an AllJoyn connection has been "
				+ "established between the DUT and the rest of the Golden Units and if not, establish the required steps to "
				+ "get the connection to the DUT (perform onboarding process if required).");
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to switch the DUT lamp on and if "
				+ "DUT supports ICSL_Dimmable , change DUT lamp brightness and lamp saturation to its higher values.", TBAD1));

		int resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and, If DUT "
				+ "supports ICSL_Dimmable, are DUT brightness and DUT saturation changed to its maximum values?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and, If DUT supports ICSL_Dimmable, DUT brightness and "
					+ "DUT saturation are not changed to its maximum values.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 9) If DUT supports ICSL_Dimmable, operate %s "
				+ "to switch off and on the DUT and change DUT lamp saturation to a medium value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched off and on? Is DUT "
				+ "lamp saturation changed to the value set by %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp is not switched off and on or DUT "
					+ "lamp saturation is not changed to the value set by %s.", TBAD1));
			return;
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 10) If DUT supports ICSL_Color, DUT lamp "
				+ "color is changed to the value set by %s.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Color, Is DUT lamp "
				+ "color changed to the value set by %s?", TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("If DUT supports ICSL_Color, DUT lamp color is not changed to the value set by %s.", TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 11) Operate %s to make a group ‘Group 1’ "
				+ "including DUT, %s and %s.", TBAD2, TBAD3, TBAD4));
		message.showMessage("Test Procedure", String.format("Step 12) Operate %s to make a group ‘Group 2’ "
				+ "including DUT and %s.", TBAD2, TBAD3));
		message.showMessage("Test Procedure", String.format("Step 13) Operate %s to switch off lamps of ‘Group 1’.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched off? Are %s and %s also switched off?", TBAD4));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp is not  switched off or %s and %s are not switched off.", TBAD3, TBAD4));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 14) Operate %s to switch on lamps of Group 2’.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched on? Is %s also switched on?", TBAD3));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp is not switched on or "+TBAD3+" is not "
					+ " switched on?", TBAD3));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 15) If DUT supports ICSL_ColorTemperature, operate "
				+ "%s to change Group2 lamps color temperature significantly.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_ColorTemperature, "
				+ "are DUT and %s lamps color temperature changed to the value set by %s?", TBAD3, TBAD4));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("If DUT supports ICSL_ColorTemperature, DUT and %s lamps color temperature are "
					+ "not changed to the value set by %s.", TBAD3, TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 16) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 2’ lamps saturation to its maximum value.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT "
				+ "and %s lamps saturation changed to the value set by %s?", TBAD3, TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT and %s lamps saturation are not changed to the value set by %s.", TBAD3, TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 17) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 2’ lamps saturation to a low value.", TBAD1)); 

		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT "
				+ "and %s lamps saturation changed to the value set by %s.?", TBAD3, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format(" DUT and %s lamps saturation are not changed to the value set by %s.", TBAD3, TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 18) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 1’ lamps saturation to its maximum value.", TBAD1)); 
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT, "
				+ "%s and %s lamps saturation changed to the value set by %s?", TBAD3, TBAD4, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT, %s and %s lamps saturation are not changed to the value set by $s.", TBAD3, TBAD4, TBAD1));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 19) Switch off %s and %s.", TBAD3, TBAD4)); 
		message.showMessage("Test Procedure", String.format("Step 20) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change DUT lamp saturation to a medium value.", TBAD1)); 
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation changed to the value set by %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not changed to the value set by %s.", TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 21) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change DUT lamp saturation to a high value.", TBAD2)); 
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp saturation changed to the value set by %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp saturation is not changed to the value set by %s.", TBAD1));
			return;
		}	
	}

	private void IOP_LSF_Lamp_v1_08()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if"
					+ " they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been "
				+ "established among the DUT, %s and %s. If not, establish the AllJoyn connection.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to switch the DUT lamp on and,"
				+ "if DUT supports ICSL_Dimmable, set its brightness and saturation to its maximum values.", TBAD1));

		int resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on? If DUT "
				+ "supports ICSL_Dimmable, are DUT brightness and saturation changed to its maximum values?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and or If DUT supports ICSL_Dimmable, DUT brightness and "
					+ "saturation are not changed to its maximum values.");
			return;
		}

		category = CategoryKeys.SEVEN_TWO;
		String TBAD3 = getGoldenUnitName(category);
		
		if (TBAD3 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_TWO;
		String TBAD4 = getGoldenUnitName(category);
		
		if (TBAD4 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 6) Switch on %s and %s.", TBAD3, TBAD4));
		message.showMessage("Test Procedure", String.format("Step 7) Verify that %s and %s have joined the "
				+ "AllJoyn connection and if not, establish the required steps to get the AllJoyn connection established.", TBAD3, TBAD4));
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to switch on %s and %s "
				+ "lamps and change their lamps brightness to its higher value.", TBAD1, TBAD3, TBAD4));
		message.showMessage("Test Procedure", String.format("Step 9) Operate %s to switch off and on the DUT and if "
				+ "DUT supports ICSL_Dimmable, to change DUT lamp saturation to a medium value.", TBAD1));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched off and on? After"
				+ "switching on, If DUT supports ICSL_Dimmable, is DUT lamp saturation changed to the value set by %s?", TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and or If DUT supports ICSL_Dimmable, DUT brightness and "
					+ "saturation are not changed to its maximum values.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 10) If DUT supports ICSL_Color, operate %s to "
				+ "change DUT lamp color to a different color value.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT lamp supports ICSL_Color, is DUT "
				+ "lamp color changed to the value set by %s?", TBAD2));
		
		if(resp!=0) //1==NO  null==(X)
		{
			fail(String.format("If DUT lamp supports ICSL_Color, DUT lamp color is not changed to the value set by %s.", TBAD2));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 11) Operate %s to make a group ‘Group 1’ "
				+ "including DUT, %s and %s.", TBAD2, TBAD3, TBAD4));
		message.showMessage("Test Procedure", String.format("Step 12) Operate %s to make a group ‘Group 2’ "
				+ "including DUT and %s.", TBAD2, TBAD3));
		message.showMessage("Test Procedure", String.format("Step 13) Operate %s to switch the lamps of ‘Group 1’ off.", TBAD2));
		
		resp=message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched off? Are %s and %s also switched off?", TBAD3, TBAD4));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp is not switched off or %s and %s are not also switched off.", TBAD3, TBAD4));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 14) Operate %s to switch the lamps of ‘Group 2’ off.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("Is DUT lamp switched on? Is %s also "
				+ "switched on?", TBAD3));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT lamp is not switched on or %s is not switched on.", TBAD3));
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 15) If DUT supports ICSL_ColorTemperature, operate "
				+ "%s to change Group2 lamps color temperature significantly.", TBAD2));

		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT lamp supports ICSL_ColorTemperature, are DUT and %s lamps "
				+ "color temperature changed to the value set by %s?", TBAD3, TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("DUT and %s lamps color temperature are not changed to the value set by %s.", TBAD3, TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 16) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 2’ lamps saturation to its maximum value.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT "
				+ "and %s lamps saturation changed to the value set by %s?", TBAD3, TBAD2));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("If DUT supports ICSL_Dimmable, DUT and %s lamps saturation are not changed to the"
					+ "value set by %s.", TBAD3, TBAD2));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 17) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 2’ lamps saturation to a low value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT "
				+ "and %s lamps saturation are changed to the value set by %s?", TBAD3, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("If DUT supports ICSL_Dimmable, DUT and %s lamps saturation are not changed to the value set by %s.", TBAD3, TBAD1));
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 18) If DUT supports ICSL_Dimmable, operate %s "
				+ "to change ‘Group 1’ lamps saturation to its maximum value.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", String.format("If DUT supports ICSL_Dimmable, are DUT, "
				+ "%s and %s lamps saturation changed to the value set by %s?", TBAD3, TBAD4, TBAD1));
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail(String.format("If DUT supports ICSL_Dimmable, DUT, %s and %s lamps saturation are not changed "
					+ "to the value set by %s.", TBAD3, TBAD4, TBAD1));
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_09()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", TBAD1));
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Verify if an AllJoyn connection has been established between the DUT and "
					+"%s. If not, establish an AllJoyn connection between the DUT and %s.", TBAD1, TBAD1));
		message.showMessage("Test Procedure", String.format("Step 5) Operate %s to switch DUT lamp on and off.", TBAD1));
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 6) Switch off and on %s and perform required "
				+ "actions to connect it to the AllJoyn network.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 7) Operate %s to switch DUT lamp on and off.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off.");
			return;
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 8) Switch %s on and perform required actions to "
				+ "join %s to the AllJoyn network.", TBAD2, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 9) Switch off %s.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 10) Operate %s to switch DUT lamp on and off.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off.");
			return;
		}
		
		message.showMessage("Test Procedure", "Step 11) Switch the DUT off and on and join the DUT again "
				+ "to the AllJoyn network if required.");
		message.showMessage("Test Procedure", String.format("Step 12) Operate %s to switch DUT lamp on and off.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria", "Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 13) Switch %s on and perform required actions to "
				+ "join %s to the AllJoyn network.", TBAD1, TBAD1));
		message.showMessage("Test Procedure", String.format("Step 14) Operate %s to switch DUT lamp on and off.", TBAD1));

		resp = message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp is not switched on and off.");
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_10()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		
		String category=CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s and %s.", TBAD1, TBAD2));
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}

		message.showMessage("Test Procedure", String.format("Step 4) Verify that an AllJoyn connection has been established between the DUT, "
					+"%s and %s.If not, perform the requested steps to establish the AllJoyn connection.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Operate %s to perform a DUT lamp pulse "
				+ "sequence as defined (if specified values are not supported by the DUT use DUT supported values):", TBAD1));
		message.showMessage("Test Procedure", "Step 5 a) FromState: Red color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 5 b) ToState: Green color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 5 c) period: 500 ms.");
		message.showMessage("Test Procedure", "Step 5 d) duration: 1000 ms.");
		message.showMessage("Test Procedure", "Step 5 e) numPulses: 10.");
		message.showMessage("Test Procedure", "Step 5 f) timeStamp: 100 ms.");

		int resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp display a pulse effect according "
				+ "to the parameters defined in step 5?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp doesn't display a pulse effect according to the parameters defined in step 5.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to perform a DUT lamp pulse "
				+ "sequence as defined (if specified values are not supported by the DUT use DUT supported values):", TBAD2));
		message.showMessage("Test Procedure", "Step 6 a) FromState: Red color, medium brightness.");
		message.showMessage("Test Procedure", "Step 6 b) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 6 c) period: 200 ms.");
		message.showMessage("Test Procedure", "Step 6 d) duration: 3000 ms.");
		message.showMessage("Test Procedure", "Step 5 e) numPulses: 20.");
		message.showMessage("Test Procedure"," Step 6 f) timeStamp: 2000 ms.");
		
		resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp display a pulse effect according"
				+ " to the parameters defined in step 6?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp doesn't display a pulse effect according to the parameters defined in step 6.");
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_11()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s and %s.", TBAD1, TBAD2));
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Verify that an AllJoyn connection has been established between the DUT, "
				+ "%s and %s. If not, perform the requested steps to establish the AllJoyn connection.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Operate %s to switch DUT lamp on and to set "
				+ "medium brightness and red color.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to perform a DUT lamp transition "
				+ "sequence as defined (if specified values are not supported by the DUT use DUT supported values):", TBAD1));
		message.showMessage("Test Procedure", "Step 6 a) timeStamp: 100 ms.");
		message.showMessage("Test Procedure", "Step 6 b) NewState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 6 c) Transition period: 2000 ms.");
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp display a pulse effect according "
				+ "to the parameters defined in step 6?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp doesn't display a pulse effect according to the parameters defined in step 6.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 7) Operate %s to perform a DUT lamp transition "
				+ "sequence as defined (if specified values are not supported by the devices use device supported values):", TBAD2));
		message.showMessage("Test Procedure", "Step 7 a) timeStamp: 500 ms.");
		message.showMessage("Test Procedure", "Step 7 b) NewState: Yellow color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 7 c) Transition period: 4000 ms.");
		
		resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp display a pulse effect according "
				+ "to the parameters defined in step 7?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp doesn't display a pulse effect according to the parameters defined in step 7.");
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_12()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");

		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s and %s.", TBAD1, TBAD2));

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Verify that an AllJoyn connection has been established between the DUT, "
				+ "%s and %s. If not, perform the requested steps to establish the AllJoyn.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Operate %s to switch DUT lamp on and to set "
				+ "medium brightness and yellow color.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to perform a DUT lamp transition "
				+ "sequence as defined (if specified values are not supported by the DUT use DUT supported values):", TBAD1));
		message.showMessage("Test Procedure", "Step 6 a) timeStamp: 100 ms.");
		message.showMessage("Test Procedure", "Step 6 b) NewState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 6 c) Transition period: 30000 ms.");
		message.showMessage("Test Procedure", String.format("Step 7) Ten seconds after starting step 6, operate %s "
				+ "to perform a DUT lamp pulse sequence as defined (if specified values are not supported by the devices"
				+ "use device supported values):", TBAD2));
		message.showMessage("Test Procedure", "Step 7 a) FromState: Red color, medium brightness.");
		message.showMessage("Test Procedure", "Step 7 b) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 7 c) period: 1000 ms.");
		message.showMessage("Test Procedure", "Step 7 d) duration: 1000 ms.");
		message.showMessage("Test Procedure", "Step 7 e) numPulses: 20.");
		message.showMessage("Test Procedure", "Step 7 f) timeStamp: 100 ms.");
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp start displaying the pulse effect "
				+ "specified in step 7 without waiting to complete the transition effect defined in step 6?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp doesn't start displaying the pulse effect specified in step 7 without waiting to complete the "
					+ "transition effect defined in step 6.");
			return;
		}
	}

	private void IOP_LSF_Lamp_v1_13()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		String getGoldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			getGoldenUnitOnboarding = getGoldenUnitName(category);
			if (getGoldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		category = CategoryKeys.SEVEN_ONE;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s and %s.", TBAD1, TBAD2));

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network if "
					+ "they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Verify that an AllJoyn connection has been established between the DUT, "
				+ "%s and %s. If not, perform the requested steps to establish the AllJoyn connection.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Operate %s to store following scenes.", TBAD1));
		message.showMessage("Test Procedure", "Step 5 a) Scene 1: No transition; Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 5 b) Scene 2: Transition effect.");
		message.showMessage("Test Procedure", "Step 5 c) timeStamp: 100 ms.");
		message.showMessage("Test Procedure", "Step 5 d) NewState: Purple color, low brightness.");
		message.showMessage("Test Procedure", "Step 5 e) Transition period: 2000 ms.");
		message.showMessage("Test Procedure", "Step 5 f) Scene 3: Pulse effect.");
		message.showMessage("Test Procedure", "Step 5 g) FromState: Yellow color, medium brightness.");
		message.showMessage("Test Procedure", "Step 5 h) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure", "Step 5 i) period: 1000 ms.");
		message.showMessage("Test Procedure", "Step 5 j) duration: 1000 ms.");
		message.showMessage("Test Procedure", "Step 5 k) numPulses: 20.");
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to make DUT perform scene 1.", TBAD2));
		
		int resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp change to state defined in scene 1?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp changes to state defined in scene 1.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 7) Operate %s to make DUT perform scene 2.", TBAD1));
		
		resp = message.showQuestion("Pass/Fail Criteria", "Does DUT lamp change to state defined in scene 2?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp changes to state defined in scene 2.");
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 8) Operate %s to make DUT perform scene 3.", TBAD2));
		
		resp = message.showQuestion("Pass/Fail Criteria","Does DUT lamp change to state defined in scene 3?");
		
		if (resp != 0) //1==NO  null==(X)
		{
			fail("DUT lamp changes to state defined in scene 3.");
			return;
		}
	}

	private void showPreconditions()
	{

		String msg = "Step 1) The passcode for the DUT is set to the default passcode \"000000\""
				+ "\nStep 2) The AllJoyn devices of the Test Bed used will register an AuthListener with the "
				+ "AllJoyn framework that provides the default passcode (“000000”)\n when "
				+ "authentication is requested (unless anything else is defined in a test case)."
				+ "\nStep 3) The SSID of the soft access point (Soft AP) advertised by the DUT follows the "
				+ "proper format such that it ends with the first seven digits of the deviceId."
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled.";

		message.showMessage("Preconditions", msg);
	}

	private void fail(String msg)
	{
		message.showMessage("Verdict", msg);
		logger.error(msg);
		pass = false;
	}

	private String getGoldenUnitName(final String Category)
	{
		name = null;

		final List<String> goldenUnitsList = goldenUnits.get(Category);
		
		if (goldenUnitsList != null)
		{
			//if (goldenUnitsList != null && goldenUnitsList.size() > 1)
			if (goldenUnitsList.size() > 1)
			{
				Object col[] = {"Golden Unit Name", "Category"};

				TableModel model = new DefaultTableModel(col, goldenUnitsList.size());

				final JTable tableSample = new JTable(model)
				{
					private static final long serialVersionUID = -5114222498322422255L;

					public boolean isCellEditable(int row, int column)
					{					
						return false;
					}
				};

				tableSample.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				tableSample.getTableHeader().setBackground(new Color(25, 78, 97));
				tableSample.getTableHeader().setForeground(new Color(255, 255, 255));
				tableSample.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 13));

				for (int i = 0; i < goldenUnitsList.size(); i++)
				{
					tableSample.setValueAt(goldenUnitsList.get(i), i, 0);
					tableSample.setValueAt(Category, i, 1);
				}

				JScrollPane scroll = new JScrollPane(tableSample);

				final JDialog dialog = new JDialog();
				Rectangle bounds = null ;
				Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
				bounds = new Rectangle((int) (screenDimensions.width/2) - GOLDEN_UNIT_SELECTOR_WIDTH/2, 
						(int) (screenDimensions.height/2) - GOLDEN_UNIT_SELECTOR_HEIGHT/2,
						GOLDEN_UNIT_SELECTOR_WIDTH, GOLDEN_UNIT_SELECTOR_HEIGHT);
				dialog.setBounds(bounds);
				dialog.setTitle("Select a Golden Unit");
				dialog.add(scroll, BorderLayout.CENTER);
				dialog.setResizable(false);
				JButton buttonNext = new JButton("Next");
				buttonNext.setForeground(new Color(255, 255, 255));
				buttonNext.setBackground(new Color(68, 140, 178));
				buttonNext.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						int selectedGU = tableSample.getSelectedRow();
						if (selectedGU != -1)
						{
							dialog.dispose();
							name = "GU: " + goldenUnitsList.remove(selectedGU);
							//goldenUnits.put(Category, gu);
						}		
					}
				});

				JPanel buttonPanel = new JPanel();
				GridBagLayout gridBagLayout = new GridBagLayout();
				gridBagLayout.columnWeights = new double[]{1.0};
				gridBagLayout.rowWeights = new double[]{1.0};
				buttonPanel.setLayout(gridBagLayout);
				GridBagConstraints gbc_next = new GridBagConstraints();
				gbc_next.gridx = 0;
				gbc_next.gridy = 0;
				gbc_next.anchor=GridBagConstraints.CENTER;
				buttonPanel.add(buttonNext, gbc_next);	
				dialog.add(buttonPanel, BorderLayout.SOUTH);
				dialog.setAlwaysOnTop(true); //<-- this line
				dialog.setModal(true);
				dialog.setResizable(false);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

			}
			else if (goldenUnitsList.size() == 1)
			{
				name = "GU: " + goldenUnitsList.remove(0);
			}
		}
		return name;
	}

	public String getFinalVerdict()
	{
		if (inconc)
		{
			return "INCONC";
		}
		
		if (pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
	}
}
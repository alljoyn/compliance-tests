/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.testcases.iop.config;

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

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.CategoryKeys;
import com.at4wireless.alljoyn.core.iop.IOPMessage;
import com.at4wireless.alljoyn.testcases.parameter.Ics;

public class ConfigIOPTestSuite
{
	protected static final String TAG = "ConfigIOPTestSuite";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;

	Boolean pass = true;
	Boolean inconc = false;
	Map<String, List<String>> goldenUnits;
	IOPMessage message = new IOPMessage(logger);
	Boolean ICSON_OnboardingServiceFramework = false;
	String name = null;
	
	public ConfigIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits, Ics icsList)
	{
		this.goldenUnits = goldenUnits;
		ICSON_OnboardingServiceFramework = icsList.ICSON_OnboardingServiceFramework;
		
		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			fail("Exception: "+e.toString());
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		showPreconditions();	
		
		if (testCase.equals("IOP_Config-v1-01"))
		{
			IOP_Config_v1_01();
		}
		else if (testCase.equals("IOP_Config-v1-02"))
		{
			IOP_Config_v1_02();
		}
		else if (testCase.equals("IOP_Config-v1-03"))
		{
			IOP_Config_v1_03();
		}
		else if (testCase.equals("IOP_Config-v1-04"))
		{
			IOP_Config_v1_04();
		}
		else if (testCase.equals("IOP_Config-v1-05"))
		{
			IOP_Config_v1_05();
		}
		else if (testCase.equals("IOP_Config-v1-06")){
			IOP_Config_v1_06();
		}
		else if (testCase.equals("IOP_Config-v1-08"))
		{
			IOP_Config_v1_08();
		}
		else if (testCase.equals("IOP_Config-v1-09"))
		{
			IOP_Config_v1_09();
		}
		else
		{
			fail("Test Case not valid");
		}
	}

	private void showPreconditions()
	{
		String msg = "Step 1) The passcode for the DUT is set to the default passcode \"000000\"."
				+ "\nStep 2) The AllJoyn devices of the Test Bed used will register an AuthListener with the "
				+ "AllJoyn framework that provides the default passcode (�000000�)\n when "
				+ "authentication is requested (unless anything else is defined in a test case)."
				+ "\nStep 3) The SSID of the soft access point (Soft AP) advertised by the DUT follows the "
				+ "proper format such that it ends with the first seven digits of the deviceId."
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled.";

		message.showMessage("Preconditions", msg);
	}

	private void IOP_Config_v1_01()
	{
		String testBed = "TBAD1";
		String category = CategoryKeys.ONE;
		testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;	
		}

		message.showMessage("Initial Conditions", String.format("DUT and %s are switched off.", testBed));
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed));

		message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to "
				+ "the AP network if they are not connected yet.", testBed));
		message.showMessage("Test Procedure", String.format("Step 4) Establish an AllJoyn connection"
				+ " between the DUT and %s if is not established automatically.", testBed));
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to display of DUT About "
				+ "Announcement list of object paths and service framework "
				+ "interfaces supported.", testBed));

		int response = message.showQuestion("Pass/Fail Criteria", "Verify that Config Object "
				+ "path (org.alljoyn.Config) is present in DUT About Announcement.");

		if (response != 0) //1==NO
		{
			fail("Config Object path (org.alljoyn.Config) is not present in DUT About Announcement.");
			return;
		}
	}

	private void IOP_Config_v1_02()
	{
		String testBed = "TBAD1";
		
		String category = CategoryKeys.ONE;
		testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network"
					+ " if they are not connected yet, use %s to onboard the DUT to the personal AP.", goldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network"
					+ " if they are not connected yet");
		}

		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection "
				+ "between the DUT and all the Golden Units if is not established "
				+ "automatically.");
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to display the "
				+ "values of the parameters �DeviceName� and �DefaultLanguage�"
				+ " in the DUT About Announcement.", testBed));
		
		int step = 6;
		for (int i = 1; i <= 2; i++)
		{
			testBed = "TBAD" + i;
			category=CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration data (�DeviceName� and �DefaultLanguage� "
					+ "parameters) on the Config bus object using �GetConfigurations� "
					+ "method \n(check that the values are obtained using Configuration "
					+ "Service and not About Announcement).", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", String.format("Are the values of the parameters"
					+ " �DeviceName� and �DefaultLanguage� in the DUT About Announcement"
					+ " the same than the values obtained in step %d?", step-i));

			if (response != 0) //1==NO
			{
				fail(String.format("The values of the parameters 'DeviceName' and 'DefaultLanguage' "
						+ "in the DUT About Announcement are not the same than"
						+ "the values obtained in step %d", step-i));
				return;
			}
		}
	}

	private void IOP_Config_v1_03()
	{
		String testBed = "TBAD1";
		String category = CategoryKeys.ONE;
		String TBAD_A = getGoldenUnitName(category);
		
		if (TBAD_A == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;	
		}
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on all Golden Units of the Test Bed.");
		message.showMessage("Test Procedure", "Step 2) Switch on DUT.");
		
		category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but "
							+ "ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;	
			}
		}
			
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", goldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet");				
		}
		
		int step = 4;
		for (int i = 1; i <= 2; i++)
		{
			testBed = "TBAD" + i;
			
			message.showMessage("Test Procedure",String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established "
					+ "automatically.", step));
			step++;
			
			category = CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration �DeviceName� parameter value on the "
					+ "Config bus object using GetConfigurations method.", step, testBed)); 
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to update "
					+ "�DeviceName� parameter (change DeviceName to DeviceName%d "
					+ "value).", step, testBed, i));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to get the "
					+ "value of �DeviceName� parameter using �GetConfigurations� "
					+ "method of the Config interface.", step, testBed));
			step++;

			int response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DeviceName� parameter "
					+ "value DeviceName%d?", i));

			if (response != 0) //1==NO
			{
				fail(String.format("�DeviceName� parameter value is not DeviceName%d", i));
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display the "
					+ "values of the �DeviceName� parameter in the DUT About Announcement.", step, TBAD_A));
			step++;

			response = message.showQuestion("Pass/Fail Criteria", String.format("DeviceName� parameter "
					+ "value displayed in the About Announcement is DeviceName%d?", i));

			if (response != 0) //1==NO
			{
				fail(String.format("�DeviceName� parameter value displayed in the About "
						+ "Announcement is not DeviceName%d.", i));
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Switch off the DUT.", step));
			step++;

			message.showMessage("Test Procedure", String.format("Step %d) Switch on DUT", step));
			step++;
			
			if (ICSON_OnboardingServiceFramework)
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
						+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", step, goldenUnitOnboarding));
			}
			else
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
						+ "if they are not connected yet", step));		
			}
			
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established "
					+ "automatically.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration �DeviceName� parameter value on the "
					+ "Config bus object using GetConfigurations method.", step, testBed)); 
			step++;

			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DeviceName� parameter value "
					+ "DeviceName%d?", i));

			if (response != 0) //1==NO
			{
				fail(String.format("�DeviceName� parameter value is not DeviceName%d.", i));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display "
					+ "the values of the �DeviceName� parameter response in the "
					+ "DUT About Announcement.", step, TBAD_A));
			step++;

			response=message.showQuestion("Pass/Fail Criteria", String.format("Is �DeviceName� parameter value "
					+ "displayed in the About Announcement DeviceName%d?", i));

			if (response != 0) //1==NO
			{
				fail(String.format("�DeviceName� parameter value displayed in the About "
						+ "Announcement is not DeviceName%d.", i));						
				return;
			}

			response=  message.showQuestion("Pass/Fail Criteria", String.format("�DeviceName� parameter value"
					+ " read in the Config interface is and in About Announcement"
					+ " is DeviceName%d at the end of the step?", i));

			if (response != 0) //1==NO
			{
				fail(String.format("�DeviceName� parameter value"
						+ " read in the Config interface is and in About Announcement"
						+ " is not DeviceName%d at the end of the step.", i));						
				return;
			}
		}
	}

	private void IOP_Config_v1_04()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on all Golden Units of the Test Bed.");
		message.showMessage("Test Procedure", "Step 2) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
			
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", goldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet");		
		}
		
		int step = 4;
		category = CategoryKeys.ONE;
		String TBAD_A = getGoldenUnitName(category);
		
		if (TBAD_A == null)
		{
			fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
			inconc = true;
			return;	
		}
		
		for (int i = 1; i <= 2; i++)
		{
			testBed = "TBAD" + i;
		
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established "
					+ "automatically.", step));
			step++;
			
			category = CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration �DefaultLanguage� parameter value "
					+ "on the Config bus object using GetConfigurations method.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display the "
					+ "values of the �SupportedLanguages� parameter in the DUT "
					+ "About Announcement.", step, TBAD_A));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Are supported languages"
					+ " in the About announcement according to IXITCO_SupportedLanguages?");

			if (response != 0) //1==NO
			{
				fail("Supported languages in the About announcement are not "
						+ "according to IXITCO_SupportedLanguages ");						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command  %s to update "
					+ "�DefaultLanguage� parameter (change DefaultLanguage to "
					+ "any of the DUT supported languages obtained in step %d "
					+ "which is not the current value obtained in step %d).", step, testBed, step - 1, step -2));
			
			int step7 = step;
			step++;

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to get the "
					+ "value of �DefaultLanguage� parameter using "
					+ "�GetConfigurations� method of the Config interface.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DefaultLanguage� "
					+ "parameter value the DefaultLanguage value updated in step %d?", step7));

			if (response != 0) //1==NO
			{
				fail(String.format("'DefaultLanguage' parameter value is not the DefaultLanguage "
						+ "value updated in step %d.", step7));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display the "
					+ "value of the �DefaultLanguage� parameter in the DUT About Announcement.", step, TBAD_A));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DefaultLanguage� "
					+ "parameter value displayed in the About Announcement "
					+ "the value updated in step %d?", step7));

			if (response != 0) //1==NO
			{
				fail(String.format("'DefaultLanguage' parameter value displayed in the "
						+ "About Announcement is not the DefaultLanguage "
						+ "value updated in step %d.", step7));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Switch off the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Switch on DUT", step));
			step++;
			
			if (ICSON_OnboardingServiceFramework)
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
						+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", step, goldenUnitOnboarding));
			}
			else
			{
					message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
							+ "if they are not connected yet", step));	
			}
			
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established "
					+ "automatically.", step));
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain "
					+ "the DUT configuration �DefaultLanguage� parameter value "
					+ "on the Config bus object using GetConfigurations method.");
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DefaultLanguage� "
					+ "parameter value the DefaultLanguage value updated in step %d?", step7));

			if (response != 0) //1==NO
			{
				fail(String.format("'DefaultLanguage' parameter value is not the"
						+ " DefaultLanguage value updated in step %d.", step7));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display "
					+ "the values of the �DefaultLanguage� parameter response "
					+ "in the DUT About Announcement.", step, TBAD_A));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DefaultLanguage� "
					+ "parameter value displayed in the About Announcement"
					+ " the value updated in step %d?", step7));

			if (response != 0) //1==NO
			{
				fail(String.format("'DefaultLanguage' parameter value displayed in the"
						+ " About Announcement is not the"
						+ " value updated in step %d.", step7));						
				return;
			}

			response = message.showQuestion("Pass/Fail Criteria", String.format("Is �DefaultLanguage� "
					+ "parameter value read in the Config interface and in About Announcement at the end of the step the "
					+ "value updated in the sub-step %d for the corresponding %s?", step7, testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("'DefaultLanguage' parameter  value read in the Config "
						+ "interface is and in About Announcement at the end of "
						+ "the step is not  the value updated in the sub-step %d for the"
						+ " corresponding %s.", step7, testBed));						
				return;
			}
		}
	}

	private void IOP_Config_v1_05()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		String category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;	
			}
		}
			
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", goldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP network "
					+ "if they are not connected yet");		
		}
		
		int step = 4;	
		category = CategoryKeys.ONE;
		
		for (int i = 1; i <= 2; i++)
		{
			testBed = "TBAD" + i;
			
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established automatically.", step));
			step++;
			
			category = CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration data (�DeviceName� and �DefaultLanguage� parameters) on the Config bus object "
					+ "using �GetConfigurations� method.", step, testBed));
			step++;
			message.showMessage("Test Procedure",String.format("Step %d) Modify DUT configuration "
					+ "to be different from default factory configuration. For example modify �DeviceName� to �InteropTestDeviceName� "
					+ "value.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to perform a"
					+ " �FactoryReset� in the DUT using the Config interface.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration data (�DeviceName� and "
					+ "�DefaultLanguage� parameters) on the Config \n bus "
					+ "object using �GetConfigurations� method and verify "
					+ "that the DUT configuration has been set to the factory "
					+ "default values.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Is Configuration data "
					+ "(�DeviceName� and �DefaultLanguage�) the DUT factory default configuration data?");

			if (response != 0) //1==NO
			{
				fail("Configuration data (�DeviceName� and �DefaultLanguage�) read with every "
						+ "TBAD after invoking �FactoryReset� Configuration method is not the DUT factory default configuration data");						
				return;
			}
		}
	}

	private void IOP_Config_v1_06()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
	
		String category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		int step = 3;
		for (int i = 1; i <= 2; i++)
		{
			if (ICSON_OnboardingServiceFramework)
			{ 
					message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network"
							+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", step, goldenUnitOnboarding));
			}
			else
			{
					message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP network "
							+ "if they are not connected yet");		
			}
			
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and all the Golden Units if is not established "
					+ "automatically.", step));
			step++;
			
			category = CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
				
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to modify "
					+ "the DUT DeviceName parameter with �MyTestDeviceName� "
					+ "value using the Config interface.", step, testBed));
			step++;
			message.showMessage("Test Procedure",String.format("Step %d) Command %s to call Restart "
					+ "method in the DUT.", step, testBed));
			step++;

			int response = message.showQuestion("Pass/Fail Criteria", "Is DUT restarted?");

			if(response!=0) //1==NO
			{
				fail("DUT is not restarted");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) %s will lose the "
					+ "connection. Perform the required steps to re-establish"
					+ " AllJoyn connection between the DUT and %s.", step, testBed, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain "
					+ "the DUT configuration data (�DeviceName� and �DefaultConfiguration� parameters) using the Config "
					+ "interface.", step, testBed));
			step++;

			response = message.showQuestion("Pass/Fail Criteria", "Has Value of �DeviceName� "
					+ "parameter changed to �MyTestDeviceName�?");

			if (response != 0) //1==NO
			{
				fail("Value of �DeviceName� parameter has not changed to �MyTestDeviceName�");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to modify the "
					+ "DUT �DeviceName� parameter with the original �DeviceName� value using the Config interface.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to get DUT "
					+ "�DeviceName� using the Config interface.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", "Has Value of �DeviceName�"
					+ " parameter changed to its original value?");

			if (response != 0) //1==NO
			{
				fail("Value of 'DeviceName' parameter has not changed to its original value.");						
				return;
			}
		}
	}

	private void IOP_Config_v1_08()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String goldenUnitOnboarding = null;
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if (goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
			
		category = CategoryKeys.ONE;
		
		int step = 3;
		for (int i = 1; i <= 2; i++)
		{
			if (pass)
			{
				testBed = "TBAD" + i;
				
				if (ICSON_OnboardingServiceFramework)
				{
					message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
							+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", step, goldenUnitOnboarding));
				}
				else
				{
					message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
							+ "if they are not connected yet", step));
				}
				
				step++;
				message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
						+ "between the DUT and all the Golden Units if is not established "
						+ "automatically.", step));
				step++;
				
				category = CategoryKeys.TWO;
				testBed = getGoldenUnitName(category);
				
				if (testBed == null)
				{
					fail(String.format("No %s Golden Unit.", category));
					inconc = true;
					return;
				}
				
				message.showMessage("Test Procedure", String.format("Step %d) Command %s to modify "
						+ "the DUT DeviceName parameter with �MyTestDeviceName� value using the Config interface.", step, testBed));
				step++;
				message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain the DUT "
						+ "�DeviceName� parameter value.", step, testBed));
				step++;
				
				int response = message.showQuestion("Pass/Fail Criteria", "Has value of �DeviceName� "
						+ "parameter changed to �MyTestDeviceName� value?");

				if (response != 0) //1==NO
				{
					fail("Value of �DeviceName� parameter has not changed to 'MyTestDeviceName' value");						
					return;
				}

				message.showMessage("Test Procedure", String.format("Step %d) Command %s to call the "
						+ "Config interface �ResetConfigurations� method to reset �DeviceName� parameter.", step, testBed));
				step++;

				message.showMessage("Test Procedure", String.format("Step %d) Command %s to obtain"
						+ " the DUT �DeviceName� parameter value.", step, testBed));
				step++;

				response = message.showQuestion("Pass/Fail Criteria", "Has Value of �DeviceName� "
						+ "parameter changed to its factory value?");

				if (response != 0) //1==NO
				{
					fail("Value of �DeviceName� parameter has not changed to its factory value");						
					return;
				}
			}
		}
	}

	private void IOP_Config_v1_09()
	{

		String testBed = "TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		
		String category = "Category 3 AllJoyn Device (Onboarding)";
		String goldenUnitOnboarding = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			goldenUnitOnboarding = getGoldenUnitName(category);
			if(goldenUnitOnboarding == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc=true;
				return;
			}
		}
			
		category = CategoryKeys.ONE;
		
		int step = 3;
		for (int i = 1; i <= 2; i++)
		{
			testBed = "TBAD" + i;
			
			if (ICSON_OnboardingServiceFramework)
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the Golden Units and/or DUT to the AP network "
						+ "if they are not connected yet, use %s to onboard the DUT to the personal AP.", step, goldenUnitOnboarding));
			}
			else
			{
				message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP network "
						+ "if they are not connected yet");	
			}
			step++;
			
			category = CategoryKeys.TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;	
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection "
					+ "between the DUT and %s if is not established automatically.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to modify DUT "
					+ "passcode (calling the SetPasscode method on the Config bus "
					+ "object with the newPasscode parameter) to �111111�.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Is an error message "
					+ "displayed when modifying passcode?");

			if (response != 1) //1==NO
			{
				fail("An error message has been displayed when modifying passcode.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to leave the "
					+ "session.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Clear %s key store of "
					+ "authentication keys.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to join a "
					+ "session with the DUT application after receiving an "
					+ "About Announcement  and register an AuthListener with\n"
					+ "the AllJoyn framework that provides the new passcode "
					+ "(�111111�) when authentication is requested.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s join a session "
					+ "with DUT using the passcode �111111�?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not join a session with DUT using the passcode �111111�.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to modify the "
					+ "passcode (calling the �SetPasscode� method on the Config "
					+ "bus object) with the newPasscode parameter set to default "
					+ "value �000000�.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to leave the "
					+ "session.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d)  Clear %s key store "
					+ "of authentication keys.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to join a session "
					+ "with the DUT application after receiving an About "
					+ "Announcement and register an AuthListener with \nthe "
					+ "AllJoyn framework that provides the new passcode(�000000�) "
					+ "when authentication is requested.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s join a "
					+ "session with DUT using the passcode �000000�?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not join a session with DUT using the passcode �000000�.", testBed));						
				return;
			}
		}
	}

	private void fail(String msg)
	{
		message.showMessage("Verdict",msg);
		logger.error(msg);
		pass = false;
	}
	
	private String getGoldenUnitName(String Category)
	{
		name = null;

		final List<String> goldenUnitsList = goldenUnits.get(Category);
		
		if (goldenUnitsList != null)
		{
			//if(goldenUnitsList != null && goldenUnitsList.size() > 1)
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
				gbc_next.anchor = GridBagConstraints.CENTER;
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
		
		if(pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
	}
}
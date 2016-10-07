/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.testcases.iop.onboarding;

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

public class OnboardingIOPTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(OnboardingIOPTestSuite.class.getSimpleName());
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;

	Boolean pass = true;
	Boolean inconc = false;
	IOPMessage message = new IOPMessage();
	Map<String, List<String>> goldenUnits;
	String name = null;

	public OnboardingIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits)
	{
		this.goldenUnits = goldenUnits;

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
		
		if (testCase.equals("IOP_Onboarding-v1-01"))
		{
			IOP_Onboarding_v1_01();
		}
		else if (testCase.equals("IOP_Onboarding-v1-02"))
		{
			IOP_Onboarding_v1_02();
		}
		else if (testCase.equals("IOP_Onboarding-v1-03"))
		{
			IOP_Onboarding_v1_03();
		}
		else if (testCase.equals("IOP_Onboarding-v1-04"))
		{
			IOP_Onboarding_v1_04();
		}
		else if (testCase.equals("IOP_Onboarding-v1-05"))
		{
			IOP_Onboarding_v1_05();
		}
		else if (testCase.equals("IOP_Onboarding-v1-06"))
		{
			IOP_Onboarding_v1_06();
		}
		else if (testCase.equals("IOP_Onboarding-v1-07"))
		{
			IOP_Onboarding_v1_07();
		}
		else
		{
			fail("TestCase not valid");
		}
	}

	private void IOP_Onboarding_v1_01()
	{
		String category = CategoryKeys.ONE;
		String TBAD_A = getGoldenUnitName(category);
		
		if (TBAD_A == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;	
		}
		 
		category = CategoryKeys.THREE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;				
		}
		
		message.showMessage("Initial Conditions", String.format("DUT and %s are switched off.", TBAD1));
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s and %s.", TBAD_A, TBAD1));
		message.showMessage("Test Procedure", String.format("Step 3) Connect the %s to the AP "
				+ "network if it is not connected yet.", TBAD_A));
		message.showMessage("Test Procedure", String.format("Step 4) Verify if the DUT is found in "
				+ "the personal AP. If it is not connected, command %s "
				+ "to scan for Wi-Fi networks looking for the Soft AP of the "
				+ "DUT and command %s to connect to the soft AP.", TBAD1, TBAD1));
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to display "
				+ "the DUT About Announcement list of object paths and "
				+ "service framework interfaces supported.", TBAD_A));
		
		int response = message.showQuestion("Pass/Fail Criteria", "Verify that Onboarding "
				+ "Object path (’Onboarding’) is present in DUT about announcement");

		if (response != 0) //1==NO
		{
			fail("Onboarding Object path (’Onboarding’) is not present in DUT about announcement.");						
			return;
		}
	}

	private void IOP_Onboarding_v1_02()
	{
		String category = CategoryKeys.THREE;
		String TBAD1 = getGoldenUnitName(category);
		
		if (TBAD1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		 
		String TBAD_O = getGoldenUnitName(category);
		
		if (TBAD_O == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Initial Conditions", String.format("%s is switched off. \n"
				+ "DUT has already been onboarded and connected to the personal AP.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 1) Switch on %s and %s.", TBAD_O, TBAD1));
		message.showMessage("Test Procedure", String.format("Step 2) Connect %s to the AP network "
				+ "if it is not connected yet.", TBAD1));
		message.showMessage("Test Procedure", String.format("Step 3) Establish an AllJoyn connection "
				+ "between the DUT and %s if is not established automatically. "
				+ "Command %s to onboard the DUT if required.", TBAD_O, TBAD_O));
		message.showMessage("Test Procedure", String.format("Step 4) Command %s to offboard the DUT.", TBAD1));
		
		int response = message.showQuestion("Pass/Fail Criteria", "Is DUT offboarded?");

		if (response != 0) //1==NO
		{
			fail("DUT is not offboarded.");						
			return;
		}
	}

	private void IOP_Onboarding_v1_03()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed));
		
		int step = 3;
		for (int i = 1; i < 3; i++)
		{	
			message.showMessage("Test Procedure", String.format("Step %d) Connect %s to "
					+ "the AP network if it is not connected yet.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard "
					+ "DUT (steps below may be required if they are not performed "
					+ "automatically by %s):", step, testBed, testBed)); 
			message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found command %s "
					+ "to connect to the soft AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d d) Command %s to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method on the Onboarding bus object with the SSID, "
					+ "passphrase, and authType for the personal AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Is DUT onboarded?");

			if (response != 0) //1==NO
			{
				fail("DUT is not onboarded.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to establish "
					+ "an AllJoyn connection between the DUT and %s.", step, testBed, testBed));
			step++;
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is AllJoyn Connection "
					+ "established between %s and the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("AllJoyn Connection is not established between %s and the DUT.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to "
					+ "offboard the DUT.", step, testBed));
			step++;
			
			category = CategoryKeys.THREE;
			testBed = getGoldenUnitName(category);
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
		}
	}

	private void IOP_Onboarding_v1_04()
	{
		
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;	
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s", testBed));
		
		int step = 3;
		for (int i = 1; i < 3; i++)
		{
			message.showMessage("Test Procedure", String.format("Step %d) Connect %s to "
					+ "the AP network if it is not connected yet.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard "
					+ "DUT using ”111111” passcode:", step, testBed)); 
			message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found command "
					+ "%s to connect to the soft AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (”111111”).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d d) Command %s to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method on the Onboarding bus object with the SSID, "
					+ "passphrase, and authType for the personal AP.", step, testBed));

			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an "
					+ "error during the onboarding process, after call to "
					+ "‘ConfigWiFi’ method, indicating that authentication failed?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive an error during the onboarding process, after call to "
						+ "‘ConfigWiFi’ method, indicating that authentication failed.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.", step, testBed));

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an "
					+ "error after call to ‘Connect’ method indicating that "
					+ "authentication failed?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive an error after call to ‘Connect’ method indicating that "
						+ "authentication failed.", testBed));						
				return;
			}
			
			step++;

			category = CategoryKeys.THREE;
			testBed = getGoldenUnitName(category);
	
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}	
		}
	}

	private void IOP_Onboarding_v1_05()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		testBed = getGoldenUnitName(category);
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s", testBed));
		
		int step = 3;
		for (int i = 1; i < 3; i++)
		{
			message.showMessage("Test Procedure", String.format("Step %d) Connect %s to "
					+ "the AP network if it is not connected yet.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard "
					+ "DUT with an incorrect SSID value:", step, testBed)); 
			message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found command "
					+ "%s to connect to the soft AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d d) Command %s to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method  on the Onboarding\n  bus object with an incorrect "
					+ "SSID value such as ‘IncorrectSSIDValue’, passphrase, "
					+ "and authType for the personal AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.", step, testBed));
			step++;

			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an error "
					+ "when trying to onboard the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s not receives an error when trying to onboard the DUT.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to Configure DUT "
					+ "WiFi parameters by calling the ‘ConfigWiFi’ method on the "
					+ "Onboarding bus object with the SSID, passphrase, and "
					+ "authType for the personal AP. ", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard the "
					+ "DUT calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to establish an "
					+ "AllJoyn connection between the DUT and %s.", step, testBed, testBed));	
			step++;
			response=message.showQuestion("Pass/Fail Criteria", String.format("Is AllJoyn Connection established "
					+ "between %s and the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("AllJoyn Connection is not established "
						+ "between %s and the DUT", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to offboard "
					+ "the DUT.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard "
					+ "DUT with an incorrect passphrase value:", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found "
					+ "command %s to connect to the soft AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join a "
					+ "session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d d) Command %s to Configure "
					+ "DUT WiFi parameters by calling the ‘ConfigWiFi’ method on "
					+ "the Onboarding bus object with the SSID,\n an incorrect "
					+ "passphrase such as changing all passphrases digits by 9, "
					+ "and authType for the personal AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard "
					+ "the DUT calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.", step, testBed));
			step++;

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an error when trying to "
					+ "onboard the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s not receives an error when trying to onboard the DUT.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to Configure DUT "
					+ "WiFi parameters by calling the ‘ConfigWiFi’ method on the "
					+ "Onboarding bus object with the SSID, passphrase, and authType "
					+ "for the personal AP.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard the "
					+ "DUT calling the Connect method on the DUT Onboarding bus "
					+ "object.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to establish an "
					+ "AllJoyn connection between the DUT and %s.", step, testBed, testBed));
			step++;
			response=message.showQuestion("Pass/Fail Criteria", String.format("Is AllJoyn Connection established between "
					+ "%s and the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("AllJoyn Connection is not established between %s and the DUT.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to offboard "
					+ "the DUT.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard "
					+ "DUT with an incorrect authType value:", step, testBed));



			message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan for Wi-Fi "
					+ "networks looking for the Soft AP of the DUT.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found command %s "
					+ "to connect to the soft AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join a session with "
					+ "the DUT application after receiving an About Announcement and to register an "
					+ "AuthListener with DUT passcode (default value ”000000”).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d d) Command %s to Configure DUT WiFi "
					+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
					+ "object with the SSID, passphrase, and an incorrect authType for the "
					+ "personal AP.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard the DUT calling "
					+ "the ‘Connect’ method on the DUT Onboarding bus object.", step, testBed));
			step++;

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an error when "
					+ "trying to onboard the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("AllJoyn Connection is not established between %s and the DUT.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to Configure DUT WiFi parameters "
					+ "by calling the ‘ConfigWiFi’ method on the Onboarding bus object with the "
					+ "SSID, passphrase, and authType for the personal AP.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard the DUT calling the "
					+ "‘Connect’ method on the DUT Onboarding bus object.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to establish an AllJoyn "
					+ "connection between the DUT and %s.", step, testBed, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is AllJoyn Connection established "
					+ "between %s and the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("AllJoyn Connection is not established between %s and the DUT.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to offboard the DUT.", step, testBed));
			step++;

			category = CategoryKeys.THREE;
			testBed=getGoldenUnitName(category);
			
			if(testBed==null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
		}
	}

	private void IOP_Onboarding_v1_06()
	{
		String testBed = "TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		int step=3;
		for (int i = 1; i <= 3; i++)
		{
			String category = CategoryKeys.THREE;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
		
			message.showMessage("Test Procedure", String.format("Step 2) Switch on %s", testBed));
			message.showMessage("Test Procedure", String.format("Step %d) Connect %s to "
					+ "the AP network if it is not connected yet.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) After DUT has been switched on, "
					+ "verify if it is found in the personal AP. If so, offboard the DUT.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to scan for "
					+ "Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Once the soft AP is found "
					+ "command %s to connect to the soft AP.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to join a "
					+ "session with the DUT application after receiving an About "
					+ "Announcement and to register an AuthListener with DUT "
					+ "passcode (default value ”000000”).", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to scan all the "
					+ "Wi-Fi access points in the DUT’s proximity by calling ‘GetScanInfo’ "
					+ "method.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Does DUT provide a valid list "
					+ "of scanned networks?");

			if (response != 0) //1==NO
			{
				fail("DUT not provides a valid list of scanned networks.");						
				return;
			}
			
			category = CategoryKeys.THREE;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;	
			}
		}
	}

	private void IOP_Onboarding_v1_07()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		String category = CategoryKeys.THREE;
		String testBed1 = getGoldenUnitName(category);
		
		if (testBed1 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;	
		}
		
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed1));
		
		int step = 3;

		message.showMessage("Test Procedure", String.format("Step %d) Connect %s to "
				+ "the AP network if it is not connected yet.", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) After DUT has been switched on, "
				+ "verify if it is found in the personal AP. If so, offboard the DUT.", step));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to scan for "
				+ "Wi-Fi networks looking for the Soft AP of the DUT.", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Once the soft AP is found "
				+ "command %s to connect to the soft AP.", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Operate %s to join a "
				+ "session with the DUT application after receiving an About "
				+ "Announcement and to register an AuthListener with DUT "
				+ "passcode (default value ”000000”).", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to call the "
				+ "‘SetPasscode’ method on the Config bus object with the "
				+ "‘newPasscode’ parameter set to value “123456”.", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to leave "
				+ "the session and to clear the key store.", step, testBed1));
		step++;
		
		category = CategoryKeys.THREE;
		String testBed2 = getGoldenUnitName(category);
		
		if (testBed2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step %d) Switch %s on.", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Connect %s to the AP network "
				+ "if it is not connected yet.", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Once the soft AP is found command "
				+ "%s to connect to the soft AP.", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Operate %s to join a session with "
				+ "the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (value ”123456”).", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP. ", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.", step, testBed2));
		step++;

		int response = message.showQuestion("Pass/Fail Criteria", "Is DUT onboarded properly?");

		if (response != 0) //1==NO
		{
			fail("DUT is not onboarded properly.");						
			return;
		}

		message.showMessage("Test Procedure", String.format("Step %d) Command %s to establish an AllJoyn "
				+ "connection between the DUT and %s.", step, testBed2, testBed2));
		step++;

		response = message.showQuestion("Pass/Fail Criteria", String.format("Is AllJoyn connection established "
				+ "between DUT and %s?", testBed2));

		if (response != 0) //1==NO
		{
			fail(String.format("AllJoyn connection is not established between DUT and %s.", testBed2));						
			return;
		}

		message.showMessage("Test Procedure", String.format("Step %d) Command %s to offboard the DUT.", step, testBed2));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Switch off and on %s and perform "
				+ "required actions to establish an AllJoyn connection with the DUT "
				+ "(using value ”123456” for the passcode).", step, testBed1));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to call the "
				+ "‘SetPasscode’ method on the Config bus object with the ‘newPasscode’ "
				+ "parameter set to value “000000”.", step, testBed1));
		step++;

		category = CategoryKeys.THREE;
		String testBed3 = getGoldenUnitName(category);
		
		if (testBed3 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step %d) Switch %s on.", step, testBed3));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Connect %s to the AP network "
				+ "if it is not connected yet.", step, testBed3));
		step++;
		message.showMessage("Test Procedure", String.format("Step %d) Command %s to onboard DUT using "
				+ "default passcode value ”000000”:", step, testBed3));
		message.showMessage("Test Procedure", String.format("Step %d a) Command %s to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.", step, testBed3));
		message.showMessage("Test Procedure", String.format("Step %d b) Once the soft AP is found command "
				+ "%s to connect to the soft AP.", step, testBed3));
		message.showMessage("Test Procedure", String.format("Step %d c) Operate %s to join a session "
				+ "with the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (”000000”).", step, testBed3));
		message.showMessage("Test Procedure", String.format("Step %d d) Command %s to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP.", step, testBed3));
		message.showMessage("Test Procedure", String.format("Step %d e) Command %s to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.", step, testBed3));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s onboard DUT to the personal AP?", testBed3));

		if (response != 0) //1==NO
		{
			fail(String.format("%s does not onboard DUT to the personal AP.", testBed3));						
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
		message.showMessage("Preconditions",msg);
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
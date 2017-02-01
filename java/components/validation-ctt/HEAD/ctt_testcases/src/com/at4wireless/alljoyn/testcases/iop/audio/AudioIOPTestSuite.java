/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.testcases.iop.audio;

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

public class AudioIOPTestSuite
{
	protected static final String TAG = "AudioIOPTestSuite";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;
	
 	Boolean pass = true;
 	Boolean inconc = false;
 	Map<String, List<String>> goldenUnits;
	Boolean ICSON_OnboardingServiceFramework = false;
	String name = null;
	IOPMessage message = new IOPMessage(logger);
	
	public AudioIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits, Ics icsList)
	{
		this.goldenUnits = goldenUnits;
		ICSON_OnboardingServiceFramework = icsList.ICSON_OnboardingServiceFramework;
		
		try
		{
			runTestCase(testCase);
		}
		catch(Exception e)
		{
			fail("Exception: "+e.toString());
		}
	}
	
	public void runTestCase(String testCase) throws Exception
	{
		showPreconditions();
		
		if (testCase.equals("IOP_AudioSink-v1-01"))
		{
			IOP_AudioSink_v1_01();
		}
		else if (testCase.equals("IOP_AudioSink-v1-02"))
		{
			IOP_AudioSink_v1_02();
		}
		else if (testCase.equals("IOP_AudioSink-v1-03"))
		{
			IOP_AudioSink_v1_03();
		}
		else if (testCase.equals("IOP_AudioSink-v1-04"))
		{
			IOP_AudioSink_v1_04();
		}
		else if (testCase.equals("IOP_AudioSink-v1-05"))
		{
			IOP_AudioSink_v1_05();
		}
		else if (testCase.equals("IOP_AudioSink-v1-06"))
		{
			IOP_AudioSink_v1_06();
		}
		else if (testCase.equals("IOP_AudioSink-v1-07"))
		{
			IOP_AudioSink_v1_07();
		}
		else if (testCase.equals("IOP_AudioSource-v1-01"))
		{
			IOP_AudioSource_v1_01();
		}
		else if (testCase.equals("IOP_AudioSource-v1-02"))
		{
			IOP_AudioSource_v1_02();
		}
		else if (testCase.equals("IOP_AudioSource-v1-03"))
		{
			IOP_AudioSource_v1_03();
		}
		else if (testCase.equals("IOP_AudioSource-v1-04"))
		{
			IOP_AudioSource_v1_04();
		}
		else if (testCase.equals("IOP_AudioSource-v1-05"))
		{
			IOP_AudioSource_v1_05();
		}
		else if (testCase.equals("IOP_AudioSource-v1-06"))
		{
			IOP_AudioSource_v1_06();
		}
		else if (testCase.equals("IOP_AudioSource-v1-07"))
		{
			IOP_AudioSource_v1_07();
		}
		else {
			fail("Test Case not valid");
		}
		
	}

	private void IOP_AudioSink_v1_01()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;	
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display DUT "
					+ "stream media types supported: audio/x-raw (mandatory), audio/xalac, "
					+ "image/jpeg and/or application/x-metadata.", step, testBed));
			step++;
			int response = message.showQuestion("Pass/Fail Criteria", "Are supported DUT media types "
					+ "displayed as specified in ICS?");
			
			if (response != 0) //1==NO
			{
				fail("Supported DUT media types are not displayed as specified in ICS.");						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display "
					+ "audio/x-raw parameters values.", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d a) Number of channels (Support "
					+ "for 1 and 2 channels is mandatory).", step));
			message.showMessage("Test Procedure", String.format("Step %d b) Sample formats (Support for "
					+ "�s16le� is mandatory).", step));
			message.showMessage("Test Procedure", String.format("Step %d c) Sample rate. (Support for "
					+ "44100 and 48000 sample rates is mandatory).", step));
			step++;
			response = message.showQuestion("Pass/Fail Criteria", "Are DUT Audio/x-raw parameters "
					+ "displayed according to DUT specifications?");
			
			if (response != 0) //1==NO
			{
				fail("Supported DUT media types are not displayed as specified in ICS.");						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) If DUT supports ICSAU_AudioXalac, "
					+ "command %s to display audio/x-alac parameters values.", step, testBed));
			step++;
			response = message.showQuestion("Pass/Fail Criteria","If DUT supports ICSAU_AudioXalac, "
					+ "Are DUT Audio/x-alac parameters displayed according to DUT "
					+ "specifications?");
			
			if (response != 0) //1==NO
			{
				fail("If DUT supports ICSAU_AudioXalac, DUT Audio/x-alac parameters "
						+ "are not displayed according to DUT specifications.");						
				return;
			}
		}
	}
	
	private void IOP_AudioSink_v1_02()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to "
					+ "discover nearby Audio Sinks", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s find DUT "
					+ "Audio Sink service?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("%s not finds DUT Audio Sink service.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to display "
					+ "DUT stream media types supported.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to stream "
					+ "audio (PCM data) on the DUT performing following steps:", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d a) If available at %s "
					+ "configuration for AllJoyn Audio service, select the following "
					+ "configuration: \n i. Media Type: audio/x-raw \n ii. Channels: 1 \n "
					+ "iii. Format: s16le \n iv. Rate: 44100", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Select audio stream to be "
					+ "sent (according to Preconditions 5).", step));
			message.showMessage("Test Procedure", String.format("Step %d c) Select %s Play option "
					+ "(or equivalent one to call DUT Audio Sink service connect "
					+ "method).", step, testBed));
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Did Audio stream send by %s "
					+ "sounds at DUT speaker and it is played seamlessly and at the "
					+ "right speed, for the whole duration of the stream?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("Audio stream sent by %s not sounds at DUT speaker or it is not "
						+ "played seamlessly and at the right speed, for the whole "
						+ "duration of the stream.", testBed));						
				return;
			}
			step++;
		}
	}
	
	private void IOP_AudioSink_v1_03()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No "+category+" Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;	
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between the "
				+ "DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to discover "
					+ "nearby Audio Sinks.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s find DUT "
					+ "Audio Sink service?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("%s does not find DUT Audio Sink service.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to select "
					+ "an audio stream to be played, according to Preconditions 5, and "
					+ "command %s to play the audio on the DUT \n and on all "
					+ "the category 6.2 AllJoyn devices of the Test Bed (by calling "
					+ "Audio Sink service �Connect� method).", step, testBed, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Did Audio stream send by %s "
					+ "plays on the DUT speaker synchronized with the other "
					+ "category 6.2 Golden Units?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("Audio stream sent by %s not plays on the DUT speaker synchronized "
						+ "with the other category 6.2 Golden Units.", testBed));						
				return;
			}
		}
	}

	private void IOP_AudioSink_v1_04()
	{
		
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to discover "
					+ "nearby Audio Sinks.", step, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to select an audio "
					+ "stream to be played, according to Preconditions 5, and command %s "
					+ "to play the audio on the DUT.", step, testBed, testBed));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Waits for 30 s.", step));
			step++;
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to pause the audio "
					+ "stream playback on the DUT.", testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Does ongoing audio stream pause at DUT?");
			
			if (response != 0) //1==NO
			{
				fail("Ongoing audio stream does not pause at DUT.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Waits for 30 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to resume the "
					+ "audio stream playback on the DUT.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream resume playing at DUT?");
			
			if (response != 0) //1==NO
			{
				fail("Audio stream does not resume playing at DUT.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %s) Waits for 60 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to pause the "
					+ "audio stream playback on the DUT.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream pause at DUT?");
			
			if (response != 0) //1==NO
			{
				fail("Audio stream not pauses at DUT.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Waits for 30 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to stop (flush) "
					+ "the audio stream playback on the DUT.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s display any "
					+ "indication that the Audio stream has stopped?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("%s not displays any indication that the Audio stream has stopped.", testBed));						
				return;
			}
		}
	}

	private void IOP_AudioSink_v1_05()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for(int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to discover "
					+ "nearby Audio Sinks.", step, testBed));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to select an audio "
					+ "stream to be played, according to Preconditions 5, and command %s "
					+ "to play the audio on the DUT.", step, testBed, testBed));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Waits for 60 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to stop the audio "
					+ "stream playback on the DUT.", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", "Does ongoing audio stream stops at DUT?");
			
			if (response != 0) //1==NO
			{
				fail("Ongoing audio stream not stops at DUT.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to select again "
					+ "step 6 audio stream to be played and command %s to play the "
					+ "audio on the DUT.", step, testBed, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream start playing "
					+ "at DUT from the beginning of the audio stream?");
			
			if (response != 0) //1==NO
			{
				fail("Audio stream not starts playing at DUT from the beginning of the audio stream.");						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Waits for 60 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to stop (flush) "
					+ "the audio stream playback on the DUT.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream stop at DUT?");
			
			if (response != 0) //1==NO
			{
				fail("Audio stream not stops at DUT.");						
				return;
			}
		}	
	}
	
	private void IOP_AudioSink_v1_06()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for(int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_ONE;
			testBed = getGoldenUnitName(category);

			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to discover "
					+ "nearby Audio Sinks.", step, testBed));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Operate %s to select an audio "
					+ "stream to be played, according to Preconditions 5, and command %s "
					+ "to play the audio on the DUT.", step, testBed, testBed));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Command %s to mute the DUT "
					+ "(by modifying Mute property).", step, testBed));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", String.format("Is Audio stream muted at %s?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("Audio stream is not muted at %s.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Wait for 10 s.", step));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d Command %s to unmute the DUT.", step, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Is audio stream unmuted at %s?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("Audio stream is not muted at %s.", testBed));						
				return;
			}
		}
	}

	private void IOP_AudioSink_v1_07()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		category = CategoryKeys.SIX_ONE;
		String testBed = getGoldenUnitName(category);

		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		category = CategoryKeys.SIX_ONE;
		String TBAD2 = getGoldenUnitName(category);

		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 5) Command %s and %s to discover "
				+ "nearby Audio Sinks.", testBed, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream "
				+ "to be played, according to Preconditions 5, and command %s to play "
				+ "the audio on the DUT.", testBed, testBed));
		message.showMessage("Test Procedure", String.format("Step 7) If Stream is muted, Command %s to "
				+ "unmute the stream", testBed));
		message.showMessage("Test Procedure", String.format("Step 8) Command %s to set stream to its "
				+ "higher volume value", testBed));
		
		int response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume "
				+ "increased (if it was not previously set to its maximum value)?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not increased (if it was not previously set to its maximum value).");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure", String.format("Step 10) Command %s to set stream to its "
				+ "lower volume value.", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume decreased "
		 		+ "to DUT minimum value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not decreased to DUT minimum value.");						
			return;
		}
			
		message.showMessage("Test Procedure", "Step 11) Wait for 10 s.");
		message.showMessage("Test Procedure", String.format("Step 12) Command %s to set stream to a "
				+ "medium volume value (according to its volume range).", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume increased "
		 		+ "to DUT medium value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not increased to a DUT medium value.");						
			return;
		}
			
		message.showMessage("Test Procedure", "Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure", String.format("Step 14) Command "+testBed+" to set stream to its "
				+ "lower volume value.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is Audio stream volume decreased "
				+ "to DUT minimum value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not decreased to DUT minimum value.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure", String.format("Step 16) Command %s to increase gradually volume "
				+ "value up to the maximum value.", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume gradually "
				+ "increased up to DUT maximum value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not gradually increased up to DUT "
					+ "maximum value.");						
			return;
		}
			
		message.showMessage("Test Procedure", "Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure", String.format("Step 18) Command %s to set stream to a medium "
				+ "volume value.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria","Is audio stream volume  decreased "
				+ "to a DUT medium value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not decreased to a DUT medium value.");						
			return;
		}
			
		message.showMessage("Test Procedure", "Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure", String.format("Step 20) Command %s to mute the DUT.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is Audio stream muted?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream is not muted.");						
			return;
		}
				
		message.showMessage("Test Procedure", "Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure", String.format("Step 22) Command %s to unmute the DUT.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is Audio stream volume unmuted "
				+ "and DUT volume is approximately the same than in step 18?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not unmuted or DUT volume is not approximately "
					+ "the same than in step 18.");	
			return;	
		}
	}

	private void IOP_AudioSource_v1_01()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically");
		message.showMessage("Test Procedure", "Step 5) Command DUT to display sequentially each "
				+ "Golden Unit stream media types supported: audio/x-raw (mandatory), "
				+ "audio/x-alac, image/jpeg and/or application/x-metadata.");
		
		int response = message.showQuestion("Pass/Fail Criteria", "Does DUT display Golden Units media "
				+ "types correctly?");
		
		if (response != 0) //1==NO
		{
			fail("DUT not displays Golden Units media types correctly.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 6) Command DUT to display each Golden Unit audio/x-raw "
				+ "parameters values: Number of channels, Sample formats and sample rate.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does DUT display the Golden Units parameters "
				+ "of the media types according to Golden Units specifications?");
		
		if (response != 0) //1==NO
		{
			fail("DUT not displays the Golden Units parameters of the media types according "
					+ "to Golden Units specifications");						
			return;
		}
	}

	private void IOP_AudioSource_v1_02()
	{
		String testBed = "";
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;	
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step = 5;
		for (int i = 1; i <= 3; i++)
		{
			category = CategoryKeys.SIX_TWO;
			testBed = getGoldenUnitName(category);
			
			if (testBed == null)
			{
				fail(String.format("No %s Golden Unit.", category));
				inconc = true;
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command DUT to discover "
					+ "nearby Audio Sinks.", step));
			step++;
			
			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT find %s "
					+ "Audio Sink service?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("DUT not finds %s Audio Sink service.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command DUT to display "
					+ "%s stream media types supported.", step, testBed));
			step++;
			
			message.showMessage("Test Procedure", String.format("Step %d) Command DUT to stream "
					+ "audio (PCM data) on %s performing following steps:", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d a) If DUT allows media type "
					+ "configuration of the AllJoyn Audio service, select one of "
					+ "the configurations supported by %s (preferable other "
					+ "than audio/x-raw).", step, testBed));
			message.showMessage("Test Procedure", String.format("Step %d b) Select audio stream to "
					+ "be sent, according to Preconditions 5.", step));
			message.showMessage("Test Procedure", String.format("Step %d c) Select on the DUT %s "
					+ "Play option (or equivalent one to call %s Audio Sink "
					+ "service 'Connect� method).", step, testBed, testBed));
			step++;
			
			response = message.showQuestion("Pass/Fail Criteria", String.format("Did audio stream send by DUT "
					+ "sounds at %s speaker and it is played seamlessly and at "
					+ "the right speed, for the whole duration of the stream?", testBed));
			
			if (response != 0) //1==NO
			{
				fail(String.format("Audio stream sent by DUT sounds at %s speaker and it is not "
						+ "played seamlessly and at the right speed, for the whole "
						+ "duration of the stream.", testBed));						
				return;
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Command DUT to flush the "
					+ "audio stream.", step));
			step++;
		}
	}

	private void IOP_AudioSource_v1_03()
	{
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		category = CategoryKeys.SIX_TWO;
		String testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", "Step 5) Command DUT to discover nearby Audio Sinks.");
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT find %s Audio "
				+ "Sink service?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not finds %s Audio Sink service.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream "
				+ "to be played, according to Preconditions 5, and command DUT to play \n the "
				+ "audio on all the category 6.2 AllJoyn devices of the Test Bed "
				+ "(by calling Audio Sink service �Connect� method).", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", "Did audio stream send by DUT plays "
				+ "synchronized in all category 6.2 Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream sent by DUT does not play synchronized in all category 6.2 Golden Units.");						
			return;
		}
	}

	private void IOP_AudioSource_v1_04()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure", "Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category = CategoryKeys.SIX_TWO;
		String testBed = getGoldenUnitName(category);
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream to "
				+ "be played, according to Preconditions 5, and play it on %s.", testBed, testBed));
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does audio stream play on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream does not play on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 7) Waits for 30 s.");
		message.showMessage("Test Procedure", String.format("Step 8) Operate DUT to pause the audio stream "
				+ "playback on %s.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does Ongoing audio stream pause on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Ongoing audio stream does not pause on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 9) Waits for 30 s.");
		message.showMessage("Test Procedure", String.format("Step 10) Operate DUT to resume the audio stream "
				+ "playback on %s.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does Audio stream resume playing "
				+ "on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream does not resume playing on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 11) Waits for 20 s.");
		message.showMessage("Test Procedure", String.format("Step 12) Operate DUT to stop (flush) the audio "
				+ "stream playback on %s.", testBed));
		message.showMessage("Test Procedure", "Step 13) Waits for 10 s.");
		message.showMessage("Test Procedure", "Step 14) Operate DUT to select an audio stream (long"
				+ " enough for the duration of the test) and play ii on all category 6.2 "
				+ " Golden Units in the Test Bed.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream play on all "
				+ "category 6.2 Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream does not play on all category 6.2 Golden Units.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 15) Waits for 30 s.");
		message.showMessage("Test Procedure", "Step 16) Operate DUT to pause the audio stream playback.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does Ongoing audio stream pause "
				+ "on all category 6.2 Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Ongoing audio stream does not pause on all category 6.2 Golden Units.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 17) Waits for 30 s.");
		message.showMessage("Test Procedure", "Step 18) Operate DUT to resume the audio stream "
				+ "playback on the DUT.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does Ongoing audio stream resume "
				+ "on all category 6.2 Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Ongoing audio stream does not resume on all category 6.2 Golden Units.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 19) Waits for 30 s.");
		message.showMessage("Test Procedure", "Step 20) Operate DUT to stop (flush) the audio stream playback.");
	}
	
	private void IOP_AudioSource_v1_05()
	{
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure", "Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category = CategoryKeys.SIX_TWO;
		String testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream to "
				+ "be played, according to Precondition 5, and command DUT to play the audio "
				+ "on %s.", testBed, testBed));
		message.showMessage("Test Procedure", "Step 7) Waits for 60 s.");
		message.showMessage("Test Procedure", "Step 8) Operate DUT to stop (flush) the audio stream "
				+ "playback on the DUT.");
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does Ongoing audio stream stop "
				+ "on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Ongoing audio stream not stops on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 9) Operate DUT to select again step 6 audio "
				+ "stream and play it on all category 6.2 Golden Units.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream start playing "
				+ "all category 6.2 Golden Units from the beginning of the audio stream?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream not starts playing all category 6.2 Golden Units from "
					+ "the beginning of the audio stream");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 10) Waits for 60 s.");
		message.showMessage("Test Procedure", "Step 11) Operate DUT to stop (flush) the audio "
				+ "stream playback.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Does Audio stream stops on all "
				+ "category 6.2 Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream not stop on all category 6.2 Golden Units");						
			return;
		}
	}

	private void IOP_AudioSource_v1_06()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure", "Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category = CategoryKeys.SIX_TWO;
		String testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream to be "
				+ "played, according to section 2.1, and command "
				+ "DUT to play the audio on %s.", testBed, testBed));
		message.showMessage("Test Procedure", String.format("Step 7) Command DUT to mute %s (by modifying Mute"
				+ " property).", testBed));
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Is audio stream muted on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream is not muted on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 8) Wait for 10 s.");
		message.showMessage("Test Procedure", String.format("Step 9) Command DUT to unmute the %s.", testBed));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Is audio stream unmuted on %s?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream is not unmuted on %s.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 10) Operate DUT to select an audio stream (long "
				+ "enough for the duration of the test) to be played and command DUT to play the audio on all category 6.2 "
				+ "Golden Units.");
		message.showMessage("Test Procedure", "Step 11) Command DUT to mute the audio stream on all "
				+ "category 6.2 Golden Units (by modifying Mute property).");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream muted on all category 6.2 "
		 		+ "Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream is not muted on all category 6.2 Golden Units.");						
			return;
		}
			
		message.showMessage("Test Procedure", "Step 12) Wait for 10 s.");
		message.showMessage("Test Procedure", "Step 13) Command DUT to unmute the audio stream on all "
				+ "category 6.2 Golden Units.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream unmuted on all category 6.2 "
			 	+ "Golden Units?");
		if (response != 0) //1==NO
		{
			fail("Audio stream is not unmuted on all category 6.2 Golden Units.");	
			return;
		}
	}
	
	private void IOP_AudioSource_v1_07()
	{
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		String category = CategoryKeys.THREE;
		String TBAD_O = null;
		
		if (ICSON_OnboardingServiceFramework)
		{
			TBAD_O = getGoldenUnitName(category);
			if (TBAD_O == null)
			{
				fail(String.format("No %s Golden Unit but ICSON_OnboardingServiceFramework is equals to true.", category));
				inconc = true;
				return;
			}
		}
		
		if(ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet. If required, use %s to "
					+ "onboard the DUT and/or the Golden Units to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure", "Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure", "Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category = CategoryKeys.SIX_TWO;
		String testBed = getGoldenUnitName(category);
		
		if (testBed == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 6) Operate %s to select an audio stream to be "
				+ "played, according to section 2.1, and command "
				+ "DUT to play the audio on %s.", testBed, testBed));
		message.showMessage("Test Procedure", "Step 7) If Stream is muted, Command DUT to unmute the "
				+ "stream.");
		message.showMessage("Test Procedure", "Step 8) Command DUT to set stream to its higher volume "
				+ "value.");
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Is Audio stream volume increased on "
				+ "%s (if it was not previously set to its maximum value)?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream volume is not increased on %s (if it was not previously set to its maximum "
					+ "value).", testBed));						
			return;
		}
		message.showMessage("Test Procedure", "Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure", "Step 10) Command DUT to set stream to a medium volume value.");
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Is audio stream volume decreased to a "
		 		+ "medium %s value?", testBed));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Audio stream volume is not decreased to a medium %s value.", testBed));						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 11) Wait for 10 s.");
		
		category = CategoryKeys.SIX_TWO;
		String TBAD2 = getGoldenUnitName(category);
		
		if (TBAD2 == null)
		{
			fail(String.format("No %s Golden Unit.", category));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 12) Command DUT to play the stream also on %s.", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream on on both Golden Units and "
				+ "playing at a medium volume value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream is not on on both Golden Units and playing at a medium volume value.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure", "Step 14) Command DUT to set stream to its lower volume value.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume decreased to "
				+ "Golden Units minimum value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not decreased to Golden Units minimum value.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure", "Step 16) Command DUT to increase gradually volume value "
				+ "up to the maximum value.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume gradually "
				+ "increased on both Golden Units up to its maximum "
				+ "volume value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not gradually increased on both Golden Units up to its maximum volume value.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure", "Step 18) Command DUT to set stream to a medium volume value.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream volume decreased on both "
				+ "Golden Units to a medium value?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not decreased on both Golden Units to a medium value.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure", "Step 20) Command DUT to mute the stream on both Golden Units.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is audio stream muted on both Golden Units?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream is not muted on both Golden Units.");						
			return;
		}
		
		message.showMessage("Test Procedure", "Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure", "Step 22) Command DUT to unmute the stream on both Golden Units.");
		
		response = message.showQuestion("Pass/Fail Criteria", "Is Audio stream volume unmuted on both "
				+ "Golden Units and volume is approximately the same than in step 18?");
		
		if (response != 0) //1==NO
		{
			fail("Audio stream volume is not unmuted on both Golden Units and volume is approximately the same than in step 18.");						
			return;
		}
	}

	private void showPreconditions()
	{
		String msg = "Step 1) The passcode for the DUT is set to the default passcode \"000000\""
				+ "\nStep 2) The AllJoyn devices of the Test Bed used will register an AuthListener with the "
				+ "AllJoyn framework that provides the default passcode (�000000�)\n when "
				+ "authentication is requested (unless anything else is defined in a test case)."
				+ "\nStep 3) The SSID of the soft access point (Soft AP) advertised by the DUT follows the "
				+ "proper format such that it ends with the first seven digits of the deviceId."
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled."
				+"\nStep 5) To send/receive an audio stream in Audio Service Test Procedures, American "
				+ "English Speech sample M1 from ITU T-Test Signal for Telecommunication "
				+ "Systems,\n Test Vectors Associated to Rec. ITU-T P.50 Appendix I "
				+ "(http://www.itu.int/net/itu-t/sigdb/genaudio/AudioForm-g.aspx?val=1000050)\n will "
				+ "be used. This speech file should be continually played as required by the Test "
				+ "procedure duration.";

		message.showMessage("Preconditions", msg);
	}

	private void fail(String msg)
	{
		message.showMessage("Verdict",msg);
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
/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.testcases.iop.notification;

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

public class NotificationIOPTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(NotificationIOPTestSuite.class.getSimpleName());
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;
	
	Boolean pass = true;
	Boolean inconc = false;
	IOPMessage message = new IOPMessage();
	Map<String, List<String>> goldenUnits;
	Boolean ICSON_OnboardingServiceFramework = false;
	String name = null;

	public NotificationIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits, Ics icsList)
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
		
		if (testCase.equals("IOP_Notification-v1-01"))
		{
			IOP_Notification_v1_01();
		}
		else if (testCase.equals("IOP_Notification-Consumer-v1-01"))
		{
			IOP_Notification_Consumer_v1_01();
		}
		else if (testCase.equals("IOP_Notification-Consumer-v1-02"))
		{
			IOP_Notification_Consumer_v1_02();
		}
		else if (testCase.equals("IOP_Notification-Consumer-v1-03"))
		{
			IOP_Notification_Consumer_v1_03();
		}
		else
		{
			fail("Test Case not valid");
		}
	}

	private void IOP_Notification_v1_01()
	{
		String testBed;
		String getGoldenUnitOnboarding = null;

		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");

		if ((testBed = getGoldenUnitName(CategoryKeys.FIVE_ONE)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_ONE));
			inconc = true;
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed));
		
		if (ICSON_OnboardingServiceFramework)
		{
			if ((getGoldenUnitOnboarding = getGoldenUnitName(CategoryKeys.THREE)) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
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
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet.", testBed));
		}
		
		int step = 4;
		for (int i = 1; i <= 3; i++)
		{
			if (i != 1)
			{
				if ((testBed = getGoldenUnitName(CategoryKeys.FIVE_ONE)) == null)
				{
					fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_ONE));
					inconc = true;
					return;
				}
			}
			
			message.showMessage("Test Procedure", String.format("Step %d) Establish an AllJoyn connection between the DUT and %s.", step++, testBed));
			message.showMessage("Test Procedure", String.format("Step %d) Configure %s to display Notifications received from DUT, indicating type of "
					+ "Notification.", step++, testBed));
			message.showMessage("Test Procedure", String.format("Step %d) If supported by DUT and "
					+ "feasible, handle DUT to generate a Notification of information type.", step++));

			int response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an information Notification from the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive an information Notification from the DUT.", testBed));
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) If supported by DUT and "
					+ "feasible, handle DUT to generate a Notification of warning type.", step++));

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive a warning Notification from the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive a warning Notification from the DUT.", testBed));	
				return;
			}
			message.showMessage("Test Procedure", String.format("Step %d) If supported by DUT and feasible, "
					+ "handle DUT to generate a Notification of emergency type.", step++));

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive an emergency "
					+ "Notification from the DUT?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive an emergency Notification from the DUT.", testBed));						
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) If supported by DUT (support of "
					+ "ICS ICSN_RichIconUrl) and feasible, handle DUT to generate a "
					+ "‘Notification’ message with ‘richIconUrl’ field.", step++));

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive a Notification "
					+ "where richIconUrl attribute (attrName = 0) is present and a "
					+ "valid URL is response in the iconUrl in the attrValue?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive a Notification where richIconUrl "
						+ "attribute (attrName = 0) is present and a valid URL is "
						+ "response in the iconUrl in the attrValue.", testBed));							
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) If supported by DUT (support of "
					+ "ICS ICSN_RichAudioUrl) and feasible, handle DUT to generate a "
					+ "‘Notification’ message with ‘richAudioUrl’ field.", step++));

			response = message.showQuestion("Pass/Fail Criteria", String.format("Does %s receive a "
					+ "Notification where richAudioUrl attribute (attrName = 1) is present "
					+ "and a valid URL is response in the audioUrl in the attrValue?", testBed));

			if (response != 0) //1==NO
			{
				fail(String.format("%s does not receive a Notification where richAudioUrl "
						+ "attribute (attrName = 1) is present and a valid URL is "
						+ "response in the audioUrl in the attrValue.", testBed));						
				return;
			}
		}
	}

	private void IOP_Notification_Consumer_v1_01()
	{
		String getGoldenUnitOnboarding = null;
		String TBAD1, TBAD2, TBAD3;
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");
		
		if (ICSON_OnboardingServiceFramework)
		{
			if ((getGoldenUnitOnboarding = getGoldenUnitName(CategoryKeys.THREE)) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;	
			}
		}

		if ((TBAD1 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if ((TBAD2 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if ((TBAD3 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and the Golden Units to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the DUT and Golden Units to the AP network if they are not connected yet.");
		}

		message.showMessage("Test Procedure", String.format("Step 4) Establish an AllJoyn connection among the DUT, %s and %s.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to send an information "
				+ "Notification message with TTL configured for 2 minutes.", TBAD1));
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive an information Notification message from %s?", TBAD1));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives an information Notification message from %s.", TBAD1));						
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 6) Command %s to send a warning "
				+ "Notification message with TTL configured for 15 minutes.", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive a warning Notification message from %s?", TBAD2));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives a warning Notification message from %s.", TBAD2));						
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 7) Operate %s User Interface to perform "
				+ "an action that would generate a Notification message (with TTL configured to two minutes).", TBAD3));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Is notification message received from %s?", TBAD3));
		
		if (response != 1) //1==NO
		{
			fail(String.format("Notification message is received from %s.", TBAD3));						
			return;
		}

		message.showMessage("Test Procedure", "Step 8) Wait for 3 minutes.");
		message.showMessage("Test Procedure", String.format("Step 9) Establish an AllJoyn connection between the DUT and %s.", TBAD3));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Is notification message  received from %s?", TBAD3));
		
		if (response != 1) //1==NO
		{
			fail(String.format("Notification message is received from %s.", TBAD3));						
			return;
		}

		message.showMessage("Test Procedure", "Step 10) Wait for 1 minute");
		message.showMessage("Test Procedure", "Step 11) Switch off and on the DUT.");
		message.showMessage("Test Procedure", "Step 12) Wait for 1 minute");
		message.showMessage("Test Procedure", "Step 13) Verify that the DUT is connected to the AP network.");
		message.showMessage("Test Procedure", "Step 14) Establish an AllJoyn connection between the DUT and each of the Golden Units of the Test Bed.");
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Is only notification message from %s displayed in the DUT?", TBAD2));
		
		if (response != 0) //1==NO
		{
			fail(String.format("Only notification message from %s is not displayed in the DUT.", TBAD2));						
			return;
		}
	}

	private void IOP_Notification_Consumer_v1_02()
	{
		String TBAD1, TBAD2, TBAD3, TBAD_O = null;
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		
		if (ICSON_OnboardingServiceFramework)
		{
			if ((TBAD_O = getGoldenUnitName(CategoryKeys.THREE)) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;	
			}
		}
	
		if ((TBAD1 = getGoldenUnitName(CategoryKeys.FIVE_ONE)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_ONE));
			inconc = true;
			return;
		}

		if ((TBAD2 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;

		}

		if ((TBAD3 = getGoldenUnitName(CategoryKeys.FIVE_ONE)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_ONE));
			inconc = true;
			return;
		}
		
		message.showMessage("Test Procedure", String.format("Step 1) Switch on %s and %s.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 2) Connect %s and %s to the "
				+ "AP network if they are not connected yet.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 3) Establish an AllJoyn connection "
				+ "between %s and %s.", TBAD1, TBAD2));
		message.showMessage("Test Procedure", String.format("Step 4) Command %s to send a warning "
				+ "‘Notification’ message (Notification 1) with TTL configured "
				+ "for 10 minutes.", TBAD1));
		message.showMessage("Test Procedure", "Step 5) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 6) Command %s to send an emergency "
				+ "‘Notification’ message (Notification 2) with TTL configured for "
				+ "10 minutes.", TBAD1));
		message.showMessage("Test Procedure", "Step 7) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 8) Command %s to send a warning "
				+ "Notification message (Notification 3) with TTL configured for "
				+ "10 minutes.", TBAD1));
		message.showMessage("Test Procedure", "Step 9) Switch on DUT.");
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 10) Connect the DUT to the AP network "
					+ "if it is not connected yet. If required, use %s to onboard the DUT to the personal AP", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 10) Connect the DUT to the AP network if it is not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 11) Switch on %s.", TBAD3));
		message.showMessage("Test Procedure", String.format("Step 12) Connect %s to the AP network if they are not connected yet.", TBAD3));
		message.showMessage("Test Procedure", String.format("Step 13) Establish an AllJoyn connection between the DUT and %s.", TBAD3));
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive an emergency "
				+ "Notification message (Notification 2) from %s and a warning "
				+ "Notification message (Notification 3) from %s?", TBAD1, TBAD1));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives an emergency Notification message (Notification 2) "
					+ "from %s and a warning Notification message (Notification 3) from %s.", TBAD1, TBAD1));						
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 14) Command %s to send a warning Notification"
				+ "message (Notification 4) with TTL configured for 10 minutes.", TBAD3));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive a warning Notification "
				+ "message (Notification 4) from %s?", TBAD3));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives a warning Notification message (Notification 4) from %s.", TBAD3));						
			return;
		}

		message.showMessage("Test Procedure", "Step 15) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 16) Command %s to send an emergency "
				+ "Notification message (Notification 5) with TTL configured for 10 minutes.", TBAD1));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive an emergency "
				+ "Notification message (Notification 5) from %s?", TBAD3));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives a warning Notification message (Notification 4) from %s.", TBAD3));						
			return;
		}

		message.showMessage("Test Procedure", "Step 17) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 18) Command %s to send a warning "
				+ "Notification message (Notification 6) with TTL configured for 10 minutes.", TBAD1));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive a warning "
				+ "Notification message (Notification 6) from %s? Is Notification 4 no longer displayed?", TBAD3));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives a warning Notification message (Notification 6) from %s? "
					+ "Notification 4 is no longer displayed.", TBAD3));						
			return;
		}
	}

	private void IOP_Notification_Consumer_v1_03()
	{
		String TBAD1, TBAD2, TBAD3, TBAD_O = null;
		
		message.showMessage("Initial Conditions", "DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", "Step 2) Switch on all Golden Units of the Test Bed.");

		if (ICSON_OnboardingServiceFramework)
		{
			if ((TBAD_O = getGoldenUnitName(CategoryKeys.THREE)) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;
			}
		}

		if ((TBAD1 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if ((TBAD2 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if ((TBAD3 = getGoldenUnitName(CategoryKeys.FIVE_TWO)) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.FIVE_TWO));
			inconc = true;
			return;
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and the Golden Units to the AP network if"
					+ " they are not connected yet, use %s to onboard the DUT to the personal AP.", TBAD_O));
		}
		else
		{
			message.showMessage("Test Procedure", "Step 3) Connect the DUT and the Golden Units to the AP network if"
					+ " they are not connected yet.");
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Establish an AllJoyn connection between "
				+ "the DUT %s, %s and %s.", TBAD1, TBAD2, TBAD3));
		message.showMessage("Test Procedure", String.format("Step 5) Command %s to send an emergency ‘Notification’ message.", TBAD1));
		
		int response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receive Notification "
				+ "message from %s with the correct language?", TBAD1));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives Notification message from %s with the correct language.", TBAD1));						
			return;
		}

		message.showMessage("Test Procedure", "Step 6) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 7) Command %s to send an information ‘Notification’ message.", TBAD2));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT DUT receive Notification "
				+ "message from %s with the correct language?", TBAD2));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives Notification message from %s with the correct language.", TBAD2));						
			return;
		}

		message.showMessage("Test Procedure", "Step 8) Wait for 1 minute.");
		message.showMessage("Test Procedure", String.format("Step 9) Command %s to send a warning ‘Notification’ message.", TBAD3));
		
		response = message.showQuestion("Pass/Fail Criteria", String.format("Does DUT receives Notification "
				+ "message from %s with the correct language?", TBAD3));
		
		if (response != 0) //1==NO
		{
			fail(String.format("DUT not receives Notification message from %s with the correct language.", TBAD3));						
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
						GOLDEN_UNIT_SELECTOR_WIDTH, 
						GOLDEN_UNIT_SELECTOR_HEIGHT);
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
							name = goldenUnitsList.remove(selectedGU);
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
				name = goldenUnitsList.remove(0);
			}
		}
		return name;
	}

	public String getFinalVerdict()
	{
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
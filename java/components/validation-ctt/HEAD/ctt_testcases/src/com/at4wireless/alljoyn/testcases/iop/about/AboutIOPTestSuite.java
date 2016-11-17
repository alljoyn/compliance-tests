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
package com.at4wireless.alljoyn.testcases.iop.about;

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
import java.util.ArrayList;
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

public class AboutIOPTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(AboutIOPTestSuite.class.getSimpleName());
	private static final int GOLDEN_UNIT_SELECTOR_WIDTH = 500;
	private static final int GOLDEN_UNIT_SELECTOR_HEIGHT = 200;
	
	boolean pass = true;
	boolean inconc = false;
	IOPMessage message = new IOPMessage();
	Map<String, List<String>> goldenUnits;
	Boolean ICSON_OnboardingServiceFramework = false;
	String name = null;

	public AboutIOPTestSuite(String testCase, Map<String, List<String>> goldenUnits, Ics icsList)
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
		
		if (testCase.equals("IOP_About-v1-01"))
		{
			IOP_About_v1_01();
		}
		else if (testCase.equals("IOP_About-v1-02"))
		{
			IOP_About_v1_02();
		}
		else if (testCase.equals("IOP_About-v1-03"))
		{
			IOP_About_v1_03();
		}
		else if (testCase.equals("IOP_About-v1-04"))
		{
			IOP_About_v1_04();		
		}
		else
		{
			fail("Test Case not valid");
		}
	}

	private void IOP_About_v1_01()
	{

		String testBed;
		int step = 2;
		String[] testBedAllowedCategories = {CategoryKeys.TWO, CategoryKeys.FOUR_ONE, CategoryKeys.FIVE_TWO};
		String getGoldenUnitOnboarding = null;

		int response;

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		if (ICSON_OnboardingServiceFramework)
		{
			if ((getGoldenUnitOnboarding = getGoldenUnitName(new String[] {CategoryKeys.THREE})) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;
			}
		}
				 
		for (int i = 1; i <= 3; i++)
		{
			if ((testBed = getGoldenUnitName(testBedAllowedCategories)) == null)
			{
				fail(String.format("There is no %s, %s nor %s Golden Unit.", testBedAllowedCategories[0], testBedAllowedCategories[1],
						testBedAllowedCategories[2]));
				inconc = true;
				return;
			}

			message.showMessage("Test Procedure", String.format("Step %d) Switch on %s.", step++, testBed));
			
			if (ICSON_OnboardingServiceFramework)
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the DUT and %s to the AP network if "
						+ "they are not connected yet, use %s to onboard the DUT to the personal AP.",
						step++, testBed, getGoldenUnitOnboarding));
			}
			else
			{
				message.showMessage("Test Procedure", String.format("Step %d) Connect the DUT and %s to the AP network if "
						+ "they are not connected yet.", step++, testBed));
			}

			response = message.showQuestion("Pass/Fail Criteria",
					String.format("Does the DUT appear in the list of %s Nearby devices?", testBed));
			
			if (response != 0) //1==NO  null==(X)
			{
				fail(String.format("DUT does not appear in the list of %s Nearby devices.", testBed));
				return;
			}
		}
	}

	private void IOP_About_v1_02()
	{
		String testBed;
		String getGoldenUnitOnboarding = null;
		int response;
		
		if ((testBed = getGoldenUnitName(new String[]{CategoryKeys.ONE})) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.ONE));
			inconc = true;
			return;	
		}

		if (ICSON_OnboardingServiceFramework)
		{ 
			if ((getGoldenUnitOnboarding = getGoldenUnitName(new String[]{CategoryKeys.THREE})) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;
			}
		}

		message.showMessage("Initial Conditions", String.format("DUT and %s are switched off.", testBed));
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure",String.format("Step 2) Switch on %s.", testBed));
		
		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", testBed, getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if"
					+ " they are not connected yet.", testBed));
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Use %s to display the contents of DUT About Announcement.", testBed));

		response = message.showQuestion("Pass/Fail Criteria","Does the About Announcement objects "
				+ "Description include the interfaces supported according to ICS declaration? ");

		if (response != 0) //1==NO
		{
			fail("The About Announcement objects Description does not include the supported interfaces according to ICS declaration.");
			return;
		}

		response = message.showQuestion("Pass/Fail Criteria", "Are AppId, DefaultLanguage, DeviceName, DeviceId, "
				+ "AppName, Manufacturer and ModelNumber obtained in the About Announcement?");

		if (response!=0) //1==NO
		{
			fail("Some parameters values are not obtained in the About Announcement.");
			return;
		}

		message.showMessage("Test Procedure", String.format("Step 5) Command %s to introspect the DUT "
				+ "application’s message bus and display the set of bus objects and their interfaces.", testBed));

		response = message.showQuestion("Pass/Fail Criteria", "Do the set of bus objects and their "
				+ "interfaces include at least the set of paths and interfaces displayed in step 4?");

		if (response != 0) //1==NO
		{
			fail("The set of bus objects and their interfaces do not include at least the set of paths and interfaces displayed in step 4.");
			return;
		}
	}

	private void IOP_About_v1_03()
	{
		String testBed;
		String getGoldenUnitOnboarding = null;
		int response;
		
		if ((testBed = getGoldenUnitName(new String[]{CategoryKeys.ONE})) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.ONE));
			inconc = true;
			return;
		}
		
		if (ICSON_OnboardingServiceFramework)
		{
			if ((getGoldenUnitOnboarding = getGoldenUnitName(new String[]{CategoryKeys.THREE})) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;
			}
		}
		
		message.showMessage("Initial Conditions", String.format("DUT and %s are switched off.", testBed));
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed));

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", testBed, getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet.", testBed));
		}

		message.showMessage("Test Procedure", String.format("Step 4) Operate %s to join a session with "
				+ "the DUT application after receiving an About Announcement. Note "
				+ "the values obtained in the About Announcement before joining the session.", testBed));

		message.showMessage("Test Procedure", String.format("Step 5) Command %s to get DUT available "
				+ "metadata fields by using ‘GetAboutData’ method (using default language as languageTag input parameter).", testBed));

		response = message.showQuestion("Pass/Fail Criteria", "Do DUT provide available metadata fields after invoking ’GetAboutData’ method?");

		if (response != 0) //1==NO
		{
			fail("DUT not provides available metadata fields after invoking 'GetAboutData' method.");
			return;
		}

		response = message.showQuestion("Pass/Fail Criteria", "Are the values obtained in step 5 "
				+ "the same than the values obtained in the About Announcement (step 4)?");

		if (response != 0) //1==NO
		{
			fail("The values obtained in step 5 are not the same than the values obtained in the About Announcement (step 4).");
			return;
		}

		response = message.showQuestion("Pass/Fail Criteria", "Are the values obtained in step 5 according to DUT documentation including ICS?");

		if (response != 0) //1==NO
		{
			fail("The values obtained in step 5 are not according to DUT documentation including ICS.");
			return;
		}

		message.showMessage("Test Procedure","Step 6) Repeat step 5 once for each supported "
				+ "language received in the ‘GetAboutData’, using each supported language as languageTag parameter");

		response = message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 6 for "
				+ "any language the same that the values obtained in step 5 (only differences related to language texts)?");

		if (response != 0) //1==NO
		{
			fail("The values obtained in step 6 for any language are not the same that the values obtained in step 5.");
			return;
		}
	}

	private void IOP_About_v1_04()
	{
		String testBed;
		String getGoldenUnitOnboarding = null;
		int response;
		
		if ((testBed = getGoldenUnitName(new String[]{CategoryKeys.ONE})) == null)
		{
			fail(String.format("There is no %s Golden Unit.", CategoryKeys.ONE));
			inconc = true;
			return;
		}
		
		if (ICSON_OnboardingServiceFramework)
		{	
			if ((getGoldenUnitOnboarding = getGoldenUnitName(new String[]{CategoryKeys.THREE})) == null)
			{
				fail(String.format("ICSON_OnboardingServiceFramework is set to true but there is no %s Golden Unit.", CategoryKeys.THREE));
				inconc = true;
				return;	
			}
		}
		
		message.showMessage("Initial Conditions", String.format("DUT and %s are switched off.", testBed));
		message.showMessage("Test Procedure", "Step 1) Switch on DUT.");
		message.showMessage("Test Procedure", String.format("Step 2) Switch on %s.", testBed));

		if (ICSON_OnboardingServiceFramework)
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet, use %s to onboard the DUT to the personal AP.", testBed, getGoldenUnitOnboarding));
		}
		else
		{
			message.showMessage("Test Procedure", String.format("Step 3) Connect the DUT and %s to the AP network if "
					+ "they are not connected yet.", testBed));
		}
		
		message.showMessage("Test Procedure", String.format("Step 4) Operate %s to join a session with "
				+ "the DUT application after receiving an About Announcement.", testBed));

		message.showMessage("Test Procedure", "Step 5) Verify that DeviceIcon object was received in the About Announcement.");
		message.showMessage("Test Procedure", String.format("Step 6) Command %s to get DUT DeviceIcon.", testBed));

		response = message.showQuestion("Pass/Fail Criteria","Is DeviceIcon Object received?");

		if (response != 0) //1==NO
		{
			fail("DeviceIcon Object is not received.");
			return;
		}
	}

	private String getGoldenUnitName(final String[] categories)
	{
		name = null;
		
		List<String> temporaryCategoriesList = new ArrayList<String>();
		List<String> temporaryGoldenUnitsList = new ArrayList<String>();
		
		for (int i = 0; i < categories.length; i++)
		{
			if (goldenUnits.get(categories[i]) != null)
			{
				temporaryGoldenUnitsList.addAll(goldenUnits.get(categories[i]));
				
				for (int j = 0; j < goldenUnits.get(categories[i]).size(); j++)
				{
					temporaryCategoriesList.add(categories[i]);
				}
			}
		}
		
		final List<String> goldenUnitsList = temporaryGoldenUnitsList;
		final List<String> categoriesList = temporaryCategoriesList;
		
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
					tableSample.setValueAt(categoriesList.get(i), i, 1);
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
						System.out.println(selectedGU+" : "+categoriesList.get(selectedGU));
						if (selectedGU != -1)
						{
							dialog.dispose();
							int positionInGeneralList = goldenUnits.get(categoriesList.get(selectedGU)).indexOf(goldenUnitsList.get(selectedGU));
							name = goldenUnits.get(categoriesList.get(selectedGU)).remove(positionInGeneralList);
						}		
					}});

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
				int positionInGeneralList = goldenUnits.get(categoriesList.get(0)).indexOf(goldenUnitsList.get(0));
				name = goldenUnits.get(categoriesList.get(0)).remove(positionInGeneralList);
			}
		}

		return name;
	}

	private void showPreconditions()
	{
		String msg = "Step 1) The passcode for the DUT is set to the default passcode \"000000\"."
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
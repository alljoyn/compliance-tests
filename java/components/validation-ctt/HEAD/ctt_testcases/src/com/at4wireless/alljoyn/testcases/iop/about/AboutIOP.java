/*
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.iop.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;


/**
 * The Class AboutIOP.
 */
public class AboutIOP {


	/** The pass. */
	Boolean pass=true;
	
	Boolean inconc=false;

	/** The tag. */
	protected  final String TAG = "AboutIOPTestSuite";

	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	 Map<String, List<String>> goldenUnits;
	 
	
	 
	 Boolean ICSON_OnboardingServiceFramework=false;
	 
	 String name=null;

	/**
	 * Instantiates a new about iop.
	 *
	 * @param testCase the test case
	 * @param iCSON_OnboardingServiceFramework 
	 * @param goldenUnits 
	 */
	public AboutIOP(String testCase, Map<String, List<String>> goldenUnits, boolean iCSON_OnboardingServiceFramework) {		
		this.goldenUnits=goldenUnits;
		ICSON_OnboardingServiceFramework=iCSON_OnboardingServiceFramework;
		try{
			runTestCase(testCase);
		}catch(Exception e){			
			fail("Exception: "+e.toString());			
		}
	}



	/**
	 * The main method.
	 *
	 * @param arg the arguments
	 */
	public  void main(String arg[]){

		String testCase="IOP_About-v1-03";
		try{
			runTestCase(testCase);
		}catch(Exception e){		
			fail("Exception: "+e.toString());	
			inconc=true;
		}
	}

	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
	public  void runTestCase(String testCase) throws Exception{

		showPreconditions();		
		if(testCase.equals("IOP_About-v1-01")){
			IOP_About_v1_01();
		}else if(testCase.equals("IOP_About-v1-02")){
			IOP_About_v1_02();
		}else if(testCase.equals("IOP_About-v1-03")){
			IOP_About_v1_03();
		}else if(testCase.equals("IOP_About-v1-04")){
			IOP_About_v1_04();		
		}else {
			fail("TestCase not valid");
		}

	}











	/**
	 * IOP about_v1_01.
	 */
	private  void IOP_About_v1_01() {

		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		int step=2;
		String category="Category 3 AllJoyn Device (Onboarding)";
		String getGoldenUnitOnboarding = null;
		if(ICSON_OnboardingServiceFramework){
		 getGoldenUnitOnboarding=getGoldenUnitName(category);
		 if(getGoldenUnitOnboarding==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		for(int i=1;i<=3;i++){
			if(i==1){
				category="Category 2 AllJoyn Device (Configuration)";
				testBed=getGoldenUnitName(category);
			}else if(i==2){
				category="Category 4.1 AllJoyn Device (Control Panel Controller)";
				testBed=getGoldenUnitName(category);
			}else if(i==3){
				category="Category 5.2 AllJoyn Device (Notification Producer)";
				testBed=getGoldenUnitName(category);
			}
			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
			//testBed="TBAD"+i;
			message.showMessage("Test Procedure","Step "+step+") Switch on "+testBed+".");
			step++;
			
			if(ICSON_OnboardingServiceFramework){
				message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet, use "
						+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
			}else{
			message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
					+ " they are not connected yet.");
			}
			step++;
			message.showMessage("Test Procedure","Step "+step+") Verify that "+testBed+" is able to detect the DUT (It"
					+ " appears in the list of "+testBed+" Nearby devices).");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Is DUT response in the "
					+ "list of "+testBed+" Nearby devices?");
			if(response!=0){//1==NO  null==(X)

				fail("DUT response is not in the list of "+testBed+" Nearby"
						+ " devices.");
				return;


			}
		}



	}


	/**
	 * IOP about_v1_02.
	 */
	private  void IOP_About_v1_02() {

		String testBed="TBAD1";
		String category="Category 1 AllJoyn Device (About)";
		testBed=getGoldenUnitName(category);
		if(testBed==null){

			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
			
		}
		category="Category 3 AllJoyn Device (Onboarding)";
		String getGoldenUnitOnboarding = null;
		if(ICSON_OnboardingServiceFramework){
		 getGoldenUnitOnboarding=getGoldenUnitName(category);
		 if(getGoldenUnitOnboarding==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}

		message.showMessage("Initial Conditions","DUT and "+testBed+" are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");
		

		if(ICSON_OnboardingServiceFramework){
			message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
					+ " they are not connected yet, use "
					+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
		}else{
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
				+ " they are not connected yet.");
		}
		message.showMessage("Test Procedure","Step 4) Use "+testBed+" to display the contents of DUT About Announcement.");

		int response=message.showQuestion("Pass/Fail Criteria","Do the About Announcement objects"
				+" Description include the interfaces supported"
				+" according to ICS declaration? ");

		if(response!=0){//1==NO

			fail("The About Announcement objects Description  not response the interfaces supported"
					+"according to ICS declaration.");
			return;
		}

		response=message.showQuestion("Pass/Fail Criteria","Are following parameters values "
				+ "obtained "
				+ "in the About Announcement (AppId,"
				+ " DefaultLanguage, DeviceName, DeviceId,"
				+ " AppName, Manufacturer and ModelNumber)?");

		if(response!=0){//1==NO

			fail("Some parameters values are not obtained in the About Announcement.");

			return;}

		message.showMessage("Test Procedure","Step 5) Command "+testBed + " to introspect the DUT "
				+ "application’s message bus and display the set of bus objects"
				+ " and their interfaces.");

		response=message.showQuestion("Pass/Fail Criteria","Verify that the set of bus objects and their"
				+ " interfaces include at least the set of paths and"
				+ " interfaces displayed in step 4.");

		if(response!=0){//1==NO

			fail("Verify that the set of bus objects and their interfaces not include at least "
					+ "the set paths and interfaces displayed in step 4.");
			return;}

	}


	/**
	 * IOP about_v1_03.
	 */
	private  void IOP_About_v1_03() {

		String testBed="TBAD1";
		String category="Category 1 AllJoyn Device (About)";
		testBed=getGoldenUnitName(category);
		if(testBed==null){

			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
			
		}
		category="Category 3 AllJoyn Device (Onboarding)";
		String getGoldenUnitOnboarding = null;
		if(ICSON_OnboardingServiceFramework){
		 getGoldenUnitOnboarding=getGoldenUnitName(category);
		 if(getGoldenUnitOnboarding==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		message.showMessage("Initial Conditions","DUT and "+testBed+" are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");

		if(ICSON_OnboardingServiceFramework){
			message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
					+ " they are not connected yet, use "
					+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
		}else{
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
				+ " they are not connected yet.");
		}

		message.showMessage("Test Procedure","Step 4) Operate "+testBed+" to join a session with"
				+ " the DUT application after receiving an About Announcement. Note"
				+ " the values obtained in the About Announcement before joining"
				+ " the session.");

		message.showMessage("Test Procedure","Step 5) Command "+testBed+" to get DUT available "
				+ "metadata fields by using ‘GetAboutData’ method (using default "
				+ "language as languageTag input parameter).");


		int response=message.showQuestion("Pass/Fail Criteria","Do DUT provide available metadata"
				+ " fields after invoking ’GetAboutData’ method?");

		if(response!=0){//1==NO

			fail("DUT not provides available metadata fields after invoking "
					+ "'GetAboutData' method.");
			return;}

		response=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 5 "
				+ "the same than the values obtained in the About Announcement (step 4)"
				+ " where applicable?");

		if(response!=0){//1==NO

			fail("The values obtained in step 5 are not the same than "
					+ "the values obtained in the About Announcement "
					+ "(step 4) where applicable");
			return;}

		response=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 5"
				+ " according to DUT "
				+ "documentation including ICS?");

		if(response!=0){//1==NO

			fail("The values obtained in step 5"
					+ " are not according to DUT "
					+ "documentation including ICS.");
			return;}

		message.showMessage("Test Procedure","Step 6) Repeat step 5 once for each supported "
				+ "language received in the ‘GetAboutData’, using each supported "
				+ "language as languageTag parameter");

		response=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 6 for "
				+ "any language the same that the values obtained in step 5 "
				+ "(only differences related to language texts)?");

		if(response!=0){//1==NO

			fail("The values obtained in step 6 for "
					+ "any language are not the same that the values obtained in step 5.");
			return;
		}
	}



	/**
	 * IOP about_v1_04.
	 */
	private  void IOP_About_v1_04() {

		String testBed="TBAD1";
		String category="Category 1 AllJoyn Device (About)";
		testBed=getGoldenUnitName(category);
		if(testBed==null){

			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
			
		}
		category="Category 3 AllJoyn Device (Onboarding)";
		String getGoldenUnitOnboarding = null;
		if(ICSON_OnboardingServiceFramework){
		 getGoldenUnitOnboarding=getGoldenUnitName(category);
		 if(getGoldenUnitOnboarding==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		message.showMessage("Initial Conditions","DUT and "+testBed+" are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");

		if(ICSON_OnboardingServiceFramework){
			message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
					+ " they are not connected yet, use "
					+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
		}else{
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
				+ " they are not connected yet.");
		}
		message.showMessage("Test Procedure","Step 4) Operate "+testBed+" to join a session with"
				+ " the DUT application after receiving an About Announcement.");

		message.showMessage("Test Procedure","Step 5) Verify that DeviceIcon object was "
				+ "received in the About Announcement.");
		message.showMessage("Test Procedure","Step 6) Command "+testBed+" to get DUT DeviceIcon.");

		int response=message.showQuestion("Pass/Fail Criteria","Is DeviceIcon "
				+ "Object received?");

		if(response!=0){//1==NO

			fail("DeviceIcon Object is not received.");
			return;}
	}

	
	
	private String getGoldenUnitName(final String Category) {
		name=null;

		final List<String> gu = goldenUnits.get(Category);
		if(gu!=null){
			if(gu!=null&&gu.size()>1){
				Object col[] = {"Golden Unit Name","Category"};

				TableModel model = new DefaultTableModel(col,gu.size());

				final JTable tableSample = new JTable(model){

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

				for (int i = 0; i < gu.size(); i++) {

					tableSample.setValueAt(gu.get(i),i,0);
					tableSample.setValueAt(Category,i,1);

				}




				JScrollPane scroll = new JScrollPane(tableSample);





				final JDialog dialog = new JDialog();
				Rectangle bounds = null ;
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				int width=500;
				int height=200;
				bounds = new Rectangle((int) (dim.width/2)-width/2, 
						(int) (dim.height/2)-height/2,
						width, 
						height);
				dialog.setBounds(bounds);
				dialog.setTitle("Select a Golden Unit");
				dialog.add(scroll,BorderLayout.CENTER);
				dialog.setResizable(false);
				JButton buttonNext=new JButton("Next");
				buttonNext.setForeground(new Color(255, 255, 255));
				buttonNext.setBackground(new Color(68, 140, 178));
				buttonNext.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int selectedGU = tableSample.getSelectedRow();
						if(selectedGU!=-1){
							dialog.dispose();
							name="GU: "+gu.remove(selectedGU);
							//goldenUnits.put(Category, gu);
						}		
					}});

				JPanel buttonPanel=new JPanel();
				GridBagLayout gridBagLayout = new GridBagLayout();
				gridBagLayout.columnWeights = new double[]{1.0};
				gridBagLayout.rowWeights = new double[]{1.0};
				buttonPanel.setLayout(gridBagLayout);
				GridBagConstraints gbc_next = new GridBagConstraints();
				gbc_next.gridx = 0;
				gbc_next.gridy = 0;
				gbc_next.anchor=GridBagConstraints.CENTER;
				buttonPanel.add(buttonNext,gbc_next);	
				dialog.add(buttonPanel,BorderLayout.SOUTH);
				dialog.setAlwaysOnTop(true); //<-- this line
				dialog.setModal(true);
				dialog.setResizable(false);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

			}else if(gu.size()==1){
				name="GU: "+gu.remove(0);

			}
		}

		return name;
	}

	/**
	 * Show preconditions.
	 */
	private  void showPreconditions() {

		String msg="Step 1) The passcode for the DUT is set to the default passcode \"000000\"."
				+ "\nStep 2) The AllJoyn devices of the Test Bed used will register an AuthListener with the"
				+ " AllJoyn framework that provides the default passcode (“000000”)\n when "
				+ "authentication is requested (unless anything else is defined in a test case)."
				+ "\nStep 3) The SSID of the soft access point (Soft AP) advertised by the DUT follows the"
				+ " proper format such that it ends with the first seven digits of the deviceId."
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled.";

		message.showMessage("Preconditions",msg);




	}



	/**
	 * Fail.
	 *
	 * @param msg the fail message
	 */
	private  void fail(String msg) {

		message.showMessage("Verdict",msg);
		logger.error(msg);
		pass=false;


	}



	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {

		String verdict=null;
		if(inconc){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}
}
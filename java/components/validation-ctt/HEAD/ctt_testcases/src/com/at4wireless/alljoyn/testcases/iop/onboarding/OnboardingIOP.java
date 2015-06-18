/*
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

/**
 * The Class OnboardingIOP.
 */
public class OnboardingIOP {


	
	/** The pass. */
	Boolean pass=true;
	
	Boolean inconc=false;
	
	/** The tag. */
	protected  final String TAG = "OnboardingIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	Map<String, List<String>> goldenUnits;
	 

	 
	 String name=null;

	/**
	 * Instantiates a new onboarding iop.
	 *
	 * @param testCase the test case
	 */
	public OnboardingIOP(String testCase, Map<String, List<String>> goldenUnits) {
		this.goldenUnits=goldenUnits;

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

		String testCase="IOP_Onboarding-v1-07";
		try{
			runTestCase(testCase);
		}catch(Exception e){


			fail("Exception: "+e.toString());

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
		if(testCase.equals("IOP_Onboarding-v1-01")){
			IOP_Onboarding_v1_01();
		}else if(testCase.equals("IOP_Onboarding-v1-02")){
			IOP_Onboarding_v1_02();
		}else if(testCase.equals("IOP_Onboarding-v1-03")){
			IOP_Onboarding_v1_03();
		}else if(testCase.equals("IOP_Onboarding-v1-04")){
			IOP_Onboarding_v1_04();
		}else if(testCase.equals("IOP_Onboarding-v1-05")){
			IOP_Onboarding_v1_05();
		}else if(testCase.equals("IOP_Onboarding-v1-06")){
			IOP_Onboarding_v1_06();
		}else if(testCase.equals("IOP_Onboarding-v1-07")){
			IOP_Onboarding_v1_07();

		}else {
			fail("TestCase not valid");
		}
		//tearDown();
	}



	/**
	 * IOP onboarding_v1_01.
	 */
	private  void IOP_Onboarding_v1_01() {
		String category = "Category 1 AllJoyn Device (About)";
		String TBAD_A=getGoldenUnitName(category);
		 if(TBAD_A==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		 
		category = "Category 3 AllJoyn Device (Onboarding)";
		String TBAD1=getGoldenUnitName(category);
		 if(TBAD1==null){
				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;				
			}
		

		message.showMessage("Initial Conditions","DUT and "+TBAD1+" are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on "+TBAD_A+" and "+TBAD1+".");
		message.showMessage("Test Procedure","Step 3) Connect the "+TBAD_A+" to the AP "
				+ "network if it is not connected yet.");
		message.showMessage("Test Procedure","Step 4) Verify if the DUT is found in "
				+ "the personal AP. If it is not connected, command "+TBAD1+" "
				+ "to scan for Wi-Fi networks looking for the Soft AP of the "
				+ "DUT and command "+TBAD1+" to connect to the soft AP.");
		message.showMessage("Test Procedure","Step 5) Command "+TBAD_A+" to display "
				+ "the DUT About Announcement list of object paths and "
				+ "service framework interfaces supported.");
		int response=message.showQuestion("Pass/Fail Criteria","Verify that Onboarding "
				+ "Object path (’Onboarding’) is present in DUT about announcement");

		if(response!=0){//1==NO

			fail("Onboarding "
					+ "Object path (’Onboarding’) is not present in DUT about announcement.");						
			return;}

	}




	/**
	 * IOP onboarding_v1_02.
	 */
	private  void IOP_Onboarding_v1_02() {
		String category = "Category 3 AllJoyn Device (Onboarding)";
		String TBAD1=getGoldenUnitName(category);
		 if(TBAD1==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		 
			String TBAD_O=getGoldenUnitName(category);
			 if(TBAD_O==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}

		message.showMessage("Initial Conditions",""+TBAD1+" is switched off. \n"
				+ "DUT has already been onboarded and connected to the personal AP.");
		message.showMessage("Test Procedure","Step 1) Switch on "+TBAD_O+" and "+TBAD1+".");
		message.showMessage("Test Procedure","Step 2) Connect "+TBAD1+" to the AP network "
				+ "if it is not connected yet.");
		message.showMessage("Test Procedure","Step 3) Establish an AllJoyn connection "
				+ "between the DUT and "+TBAD_O+" if is not established automatically. "
				+ "Command "+TBAD_O+" to onboard the DUT if required.");
		message.showMessage("Test Procedure","Step 4) Command "+TBAD1+" to offboard the DUT.");
		int response=message.showQuestion("Pass/Fail Criteria","Is DUT offboarded?");

		if(response!=0){//1==NO

			fail("DUT is not offboarded.");						
			return;}
	}



	/**
	 * IOP onboarding_v1_03.
	 */
	private  void IOP_Onboarding_v1_03() {
		
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		String category = "Category 3 AllJoyn Device (Onboarding)";
		 testBed=getGoldenUnitName(category);
		 if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		
		
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");
		int step=3;
		for(int i=1;i<3;i++){
			
			message.showMessage("Test Procedure","Step "+step+") Connect "+testBed+" to "
					+ "the AP network if it is not connected yet.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard "
					+ "DUT (steps below may be required if they are not performed "
					+ "automatically by "+testBed+"):"); 
			message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.");
			message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "
					+ ""+testBed+" to connect to the soft AP.");
			message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed+" to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).");
			message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed+" to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method on the Onboarding bus object with the SSID, "
					+ "passphrase, and authType for the personal AP.");
			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Is DUT onboarded?");

			if(response!=0){//1==NO

				fail("DUT is not onboarded.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to establish "
					+ "an AllJoyn connection between the DUT and "+testBed+".");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection  "
					+ "established between "+testBed+" and the DUT?");

			if(response!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and "
						+ "the DUT.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to "
					+ "offboard the DUT.");
			step++;
			
			 category = "Category 3 AllJoyn Device (Onboarding)";
			 testBed=getGoldenUnitName(category);
			 if(testBed==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
		}//for

	}



	/**
	 * IOP onboarding_v1_04.
	 */
	private  void IOP_Onboarding_v1_04() {
		
		String testBed="";
		

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		String category = "Category 3 AllJoyn Device (Onboarding)";
		 testBed=getGoldenUnitName(category);
		 if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+"");
		int step=3;
		for(int i=1;i<3;i++){
			
			message.showMessage("Test Procedure","Step "+step+") Connect "+testBed+" to "
					+ "the AP network if it is not connected yet.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard "
					+ "DUT using ”111111” passcode:"); 
			message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.");
			message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "
					+ ""+testBed+" to connect to the soft AP.");
			message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed+" to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (”111111”).");
			message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed+" to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method on the Onboarding bus object with the SSID, "
					+ "passphrase, and authType for the personal AP.");

			int response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an "
					+ "error during the onboarding process, after call to"
					+ " ‘ConfigWiFi’ method, indicating that authentication failed?");

			if(response!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "error during the onboarding process, after call to"
						+ " ‘ConfigWiFi’ method, indicating that authentication failed.");						
				return;}

			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");

			response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an "
					+ "error after call to ‘Connect’ method indicating that "
					+ "authentication failed?");

			if(response!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "error after call to ‘Connect’ method indicating that "
						+ "authentication failed.");						
				return;}
			step++;

			 category = "Category 3 AllJoyn Device (Onboarding)";
			 testBed=getGoldenUnitName(category);
			 if(testBed==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
			
			
		}//for
	}



	/**
	 * IOP onboarding_v1_05.
	 */
	private  void IOP_Onboarding_v1_05() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		
		String category = "Category 3 AllJoyn Device (Onboarding)";
		 testBed=getGoldenUnitName(category);
		 if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+"");
		int step=3;
		for(int i=1;i<3;i++){
			
			message.showMessage("Test Procedure","Step "+step+") Connect "+testBed+" to "
					+ "the AP network if it is not connected yet.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") After DUT has been "
					+ "switched on, verify if it is found in the personal AP. "
					+ "If so, offboard the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard "
					+ "DUT with an incorrect SSID value:"); 



			message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.");
			message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "
					+ ""+testBed+" to connect to the soft AP.");
			message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed+" to join "
					+ "a session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).");
			message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed+" to "
					+ "Configure DUT WiFi parameters by calling the ‘ConfigWiFi’ "
					+ "method  on the Onboarding\n  bus object with an incorrect "
					+ "SSID value such as ‘IncorrectSSIDValue’, passphrase, "
					+ "and authType for the personal AP.");


			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");
			step++;

			int response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error "
					+ "when trying to onboard the DUT?");

			if(response!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "error when trying to onboard the DUT.");						
				return;}

			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to Configure DUT "
					+ "WiFi parameters by calling the ‘ConfigWiFi’ method on the "
					+ "Onboarding bus object with the SSID, passphrase, and "
					+ "authType for the personal AP. ");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard the "
					+ "DUT calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to establish an "
					+ "AllJoyn connection between the DUT and "+testBed+".");	
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established "
					+ "between "+testBed+" and the DUT?");

			if(response!=0){//1==NO

				fail("AllJoyn Connection is not established "
						+ "between "+testBed+" and the DUT");						
				return;}

			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to offboard "
					+ "the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard "
					+ "DUT with an incorrect passphrase value:");


			message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to scan "
					+ "for Wi-Fi networks looking for the Soft AP of the DUT.");
			message.showMessage("Test Procedure", "Step "+step+" b) Once the soft AP is found "
					+ "command "+testBed+" to connect to the soft AP.");
			message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed+" to join a "
					+ "session with the DUT application after receiving an "
					+ "About Announcement and to register an AuthListener with "
					+ "DUT passcode (default value ”000000”).");
			message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed+" to Configure "
					+ "DUT WiFi parameters by calling the ‘ConfigWiFi’ method on "
					+ "the Onboarding bus object with the SSID,\n an incorrect "
					+ "passphrase such as changing all passphrases digits by 9, "
					+ "and authType for the personal AP.");
			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard "
					+ "the DUT calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");
			step++;

			response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error when trying to "
					+ "onboard the DUT?");

			if(response!=0){//1==NO

				fail(""+testBed+" not receives an error when trying to onboard the DUT.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to Configure DUT "
					+ "WiFi parameters by calling the ‘ConfigWiFi’ method on the "
					+ "Onboarding bus object with the SSID, passphrase, and authType "
					+ "for the personal AP.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard the "
					+ "DUT calling the Connect method on the DUT Onboarding bus "
					+ "object.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to establish an "
					+ "AllJoyn connection between the DUT and "+testBed+".");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established between "
					+ ""+testBed+" and the DUT?");

			if(response!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and the DUT.");						
				return;}


			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to offboard "
					+ "the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard "
					+ "DUT with an incorrect authType value:");



			message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to scan for Wi-Fi "
					+ "networks looking for the Soft AP of the DUT.");
			message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "+testBed+" "
					+ "to connect to the soft AP.");
			message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed+" to join a session with "
					+ "the DUT application after receiving an About Announcement and to register an "
					+ "AuthListener with DUT passcode (default value ”000000”).");
			message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed+" to Configure DUT WiFi "
					+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
					+ "object with the SSID, passphrase, and an incorrect authType for the "
					+ "personal AP.");
			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard the DUT calling "
					+ "the ‘Connect’ method on the DUT Onboarding bus object.");
			step++;

			response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error when "
					+ "trying to onboard the DUT?");

			if(response!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and the DUT.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to Configure DUT WiFi parameters "
					+ "by calling the ‘ConfigWiFi’ method on the Onboarding bus object with the "
					+ "SSID, passphrase, and authType for the personal AP.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to onboard the DUT calling the "
					+ "‘Connect’ method on the DUT Onboarding bus object.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to establish an AllJoyn "
					+ "connection between the DUT and "+testBed+".");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established "
					+ "between "+testBed+" and the DUT?");

			if(response!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and the DUT.");						
				return;}

			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to offboard the DUT.");
			step++;

			 category = "Category 3 AllJoyn Device (Onboarding)";
			 testBed=getGoldenUnitName(category);
			 if(testBed==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
			
			

		}//for
	}



	/**
	 * IOP onboarding_v1_06.
	 */
	private  void IOP_Onboarding_v1_06() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		
		for(int i=1;i<=3;i++){
			String category = "Category 3 AllJoyn Device (Onboarding)";
			 testBed=getGoldenUnitName(category);
			 if(testBed==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
		
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+"");
		int step=3;
		
			message.showMessage("Test Procedure","Step "+step+") Connect "+testBed+" to "
					+ "the AP network if it is not connected yet.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") After DUT has been switched on, "
					+ "verify if it is found in the personal AP. If so, offboard the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to scan for "
					+ "Wi-Fi networks looking for the Soft AP of the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Once the soft AP is found "
					+ "command "+testBed+" to connect to the soft AP.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to join a "
					+ "session with the DUT application after receiving an About "
					+ "Announcement and to register an AuthListener with DUT "
					+ "passcode (default value ”000000”).");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to scan all the "
					+ "Wi-Fi access points in the DUT’s proximity by calling ‘GetScanInfo’ "
					+ "method.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Does DUT provide a valid list "
					+ "of scanned networks?");

			if(response!=0){//1==NO

				fail("DUT not provides a valid list of scanned networks.");						
				return;}
			
			
			
				 category = "Category 3 AllJoyn Device (Onboarding)";
				 testBed=getGoldenUnitName(category);
				 if(testBed==null){

						fail("No "+category+" Golden Unit.");
						inconc=true;
						return;
						
					}
			
		}//for
	}





	/**
	 * IOP onboarding_v1_07.
	 */
	private  void IOP_Onboarding_v1_07() {


		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		
		
		 String category = "Category 3 AllJoyn Device (Onboarding)";
		 String testBed1 = getGoldenUnitName(category);
		 if(testBed1==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed1+".");
		int step=3;

		message.showMessage("Test Procedure","Step "+step+") Connect "+testBed1+" to "
				+ "the AP network if it is not connected yet.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") After DUT has been switched on, "
				+ "verify if it is found in the personal AP. If so, offboard the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command "+testBed1+" to scan for "
				+ "Wi-Fi networks looking for the Soft AP of the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Once the soft AP is found "
				+ "command "+testBed1+" to connect to the soft AP.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Operate "+testBed1+" to join a "
				+ "session with the DUT application after receiving an About "
				+ "Announcement and to register an AuthListener with DUT "
				+ "passcode (default value ”000000”).");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command "+testBed1+" to call the "
				+ "‘SetPasscode’ method on the Config bus object with the "
				+ "‘newPasscode’ parameter set to value “123456”.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command "+testBed1+" to leave "
				+ "the session and to clear the key store.");
		step++;
		
		
		
		 category = "Category 3 AllJoyn Device (Onboarding)";
		 String testBed2 = getGoldenUnitName(category);
		 if(testBed2==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}
		message.showMessage("Test Procedure","Step "+step+") Switch "+testBed2+" on.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Connect "+testBed2+" to the AP network "
				+ "if it is not connected yet.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command "+testBed2+" to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Once the soft AP is found command "
				+ ""+testBed2+" to connect to the soft AP.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Operate "+testBed2+" to join a session with "
				+ "the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (value ”123456”).");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command "+testBed2+" to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP. ");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command "+testBed2+" to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.");
		step++;

		int response=message.showQuestion("Pass/Fail Criteria","Is DUT onboarded properly?");

		if(response!=0){//1==NO

			fail("DUT is not onboarded properly.");						
			return;}


		message.showMessage("Test Procedure","Step "+step+") Command "+testBed2+" to establish an AllJoyn "
				+ "connection between the DUT and "+testBed2+".");
		step++;

		response=message.showQuestion("Pass/Fail Criteria","Is AllJoyn connection established "
				+ "between DUT and "+testBed2+"?");

		if(response!=0){//1==NO

			fail("AllJoyn connection is not established between DUT and "+testBed2+".");						
			return;}

		message.showMessage("Test Procedure","Step "+step+") Command "+testBed2+" to offboard the DUT.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Switch off and on "+testBed1+" and perform "
				+ "required actions to establish an AllJoyn connection with the DUT "
				+ "(using value ”123456” for the passcode).");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command "+testBed1+" to call the "
				+ "‘SetPasscode’ method on the Config bus object with the ‘newPasscode’ "
				+ "parameter set to value “000000”.");
		step++;



		
		category = "Category 3 AllJoyn Device (Onboarding)";
		 String testBed3 = getGoldenUnitName(category);
		 if(testBed3==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
				
			}

		message.showMessage("Test Procedure","Step "+step+") Switch "+testBed3+" on.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Connect "+testBed3+" to the AP network "
				+ "if it is not connected yet.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command "+testBed3+" to onboard DUT using "
				+ "default passcode value ”000000”:");
		message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed3+" to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.");
		message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "
				+ ""+testBed3+" to connect to the soft AP.");
		message.showMessage("Test Procedure","Step "+step+" c) Operate "+testBed3+" to join a session "
				+ "with the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (”000000”).");
		message.showMessage("Test Procedure","Step "+step+" d) Command "+testBed3+" to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP.");
		message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed3+" to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.");
		response=message.showQuestion("Pass/Fail Criteria","Does "+testBed3+" onboard DUT to the personal AP?");

		if(response!=0){//1==NO

			fail(""+testBed3+" not onboards DUT to the personal AP.");						
			return;} 
	}
	/**
	 * Show preconditions.
	 */
	private  void showPreconditions() {
		
		String msg="Step 1) The passcode for the DUT is set to the default passcode \"000000\""
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
	 * @param msg the msg
	 */
	private  void fail(String msg) {
		message.showMessage("Verdict",msg);
		logger.error(msg);
		pass=false;
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
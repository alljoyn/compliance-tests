/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.iop.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AboutIOP.
 */
public class AboutIOP {
	
	
	 /** The pass. */
 	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "AboutIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);

	/**
	 * Instantiates a new about iop.
	 *
	 * @param testCase the test case
	 */
	public AboutIOP(String testCase) {

		
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
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		int step=2;
	for(int i=1;i<=3;i++){
		
		 testBed="TBAD"+i;
		message.showMessage("Test Procedure","Step "+step+") Switch on "+testBed+".");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Verify that "+testBed+" is able to detect the DUT (It"
		+ " appears in the list of TBAD1 Nearby devices).");
		step++;
		int included=message.showQuestion("Pass/Fail Criteria","Is DUT included in the "
				+ "list of "+testBed+" Nearby devices?");
		if(included!=0){//1==NO  null==(X)
			
			fail("DUT is not included in the list of "+testBed+" Nearby"
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
		
		message.showMessage("Initial Conditions","DUT and TBAD1 are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
	
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet. (If"
						+ " ICSON_OnboardingInterface ICS value is true, use"
						+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Use "+testBed+" to display the contents of DUT About Announcement.");
			
		int included=message.showQuestion("Pass/Fail Criteria","Do the About Announcement objects"
						+" Description include the interfaces supported"
						+" according to ICS declaration? ");
						
		if(included!=0){//1==NO
			
			fail("The About Announcement objects Description  not included the interfaces supported"
				 +"according to ICS declaration.");
			return;
		}
		
	    included=message.showQuestion("Pass/Fail Criteria","Are following parameters values "
	    		+ "obtained "
	    		+ "in the About Announcement (AppId,"
	    		+ " DefaultLanguage, DeviceName, DeviceId,"
	    		+ " AppName, Manufacturer and ModelNumber)?");
				
		if(included!=0){//1==NO
	
			fail("Some parameters values are not obtained in the About Announcement.");
		
			return;}
		
		message.showMessage("Test Procedure","Step 5) Command "+testBed + " to introspect the DUT "
				+ "application’s message bus and display the set of bus objects"
				+ " and their interfaces.");
		
		included=message.showQuestion("Pass/Fail Criteria","Verify that the set of bus objects and their"
				+ " interfaces include at least the set of paths and"
				+ " interfaces displayed in step 4.");
				
		if(included!=0){//1==NO
	
			fail("Verify that the set of bus objects and their interfaces not include at least "
					+ "the set paths and interfaces displayed in step 4.");
			return;}

	}
	

	/**
	 * IOP about_v1_03.
	 */
	private  void IOP_About_v1_03() {

		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBAD1 are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
	
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet. (If"
						+ " ICSON_OnboardingInterface ICS value is true, use"
						+ " TBAD_O to onboard the DUT to the personal AP.");
		
		message.showMessage("Test Procedure","Step 4) Operate "+testBed+" to join a session with"
				+ " the DUT application after receiving an About Announcement. Note"
				+ " the values obtained in the About Announcement before joining"
				+ " the session.");
		
		message.showMessage("Test Procedure","Step 5) Command "+testBed+" to get DUT available "
				+ "metadata fields by using ‘GetAboutData’ method (using default "
				+ "language as languageTag input parameter).");
		
			
		int included=message.showQuestion("Pass/Fail Criteria","Do DUT provide available metadata"
				+ " fields after invoking ’GetAboutData’ method?");
						
		if(included!=0){//1==NO
			
			fail("DUT not provides available metadata fields after invoking "
					+ "'GetAboutData' method.");
			return;}
		
	    included=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 5 "
	    		+ "the same than the values obtained in the About Announcement (step 4)"
	    		+ " where applicable?");
				
		if(included!=0){//1==NO
	
			fail("The values obtained in step 5 are not the same than "
					+ "the values obtained in the About Announcement "
					+ "(step 4) where applicable");
			return;}
		
		included=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 5"
				+ " according to DUT "
				+ "documentation including ICS?");
		
		if(included!=0){//1==NO
			
			fail("The values obtained in step 5"
				+ " are not according to DUT "
				+ "documentation including ICS.");
			return;}
		
		message.showMessage("Test Procedure","Step 6) Repeat step 5 once for each supported "
				+ "language received in the ‘GetAboutData’, using each supported "
				+ "language as languageTag parameter");
		
		included=message.showQuestion("Pass/Fail Criteria","Are the values obtained in step 6 for "
				+ "any language the same that the values obtained in step 5 "
				+ "(only differences related to language texts)?");
				
		if(included!=0){//1==NO
	
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
		
		message.showMessage("Initial Conditions","DUT and TBAD1 are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
	
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet. (If"
						+ " ICSON_OnboardingInterface ICS value is true, use"
						+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Operate "+testBed+" to join a session with"
				+ " the DUT application after receiving an About Announcement.");
		
		message.showMessage("Test Procedure","Step 5) Verify that DeviceIcon object was "
				+ "received in the About Announcement.");
		message.showMessage("Test Procedure","Step 6) Command TBAD1 to get DUT DeviceIcon.");
		
		int included=message.showQuestion("Pass/Fail Criteria","Is DeviceIcon "
				+ "Object received?");
						
		if(included!=0){//1==NO
			
			fail("DeviceIcon Object is not received.");
			return;}
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
		//If Audio service=> Add audio preconditions
		message.showMessage("Preconditions",msg);
		
		
		
		
	}
	
	
	
/**
 * Fail.
 *
 * @param msg the msg
 */
private  void fail(String msg) {


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
		if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}
	
	
	
	
	
	

}

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
package com.at4wireless.alljoyn.testcases.iop.controlpanel;

import javax.swing.JFrame;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;


/**
 * The Class ControlPanelIOP.
 */
public class ControlPanelIOP {

	/** The frame. */
	private  JFrame frame;
	 
 	/** The pass. */
 	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "ControlPIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	/**
	 * Instantiates a new control panel iop.
	 *
	 * @param testCase the test case
	 */
	public ControlPanelIOP(String testCase) {
frame=new JFrame();
		
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
		
		String testCase="IOP_ControlPanel-v1-03";
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
		frame=new JFrame();
		showPreconditions();		
		if(testCase.equals("IOP_ControlPanel-v1-01")){
			IOP_ControlPanel_v1_01();
		}else if(testCase.equals("IOP_ControlPanel-v1-02")){
			IOP_ControlPanel_v1_02();
		}else if(testCase.equals("IOP_ControlPanel-v1-03")){
			IOP_ControlPanel_v1_03(); 

		}else {
			fail("TestCase not valid");
		}
		//tearDown();
	}



	/**
	 * IOP control panel_v1_01.
	 */
	private  void IOP_ControlPanel_v1_01() {
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If ICSON_OnboardingInterface "
				+ "ICS value is true, use TBAD_O to onboard the DUT to the personal AP.");
		int step=4;
		for(int i=1;i<=3;i++){
				testBed="TBAD"+i;
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn "
						+ "connection between the DUT and "+testBed+".");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" (using its "
						+ "virtual control panel Controller functionality) to get DUT "
						+ "Control Panel elements on a graphical interface.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Using "+testBed+" navigate through "
						+ "the different menus available.");
				step++;
				
				int included=message.showQuestion("Pass/Fail Criteria","Verify that using "+testBed+" "
						+ "virtual control panel it is possible to navigate through all "
						+ "menus specified by DUT manufacturer.");

				if(included!=0){//1==NO

					fail(""+testBed+" virtual control panel it is not possible to navigate "
							+ "through all menus sepciefied by DUT manufacturer.");						
					return;}
		}
		
	}

	


	/**
	 * IOP control panel_v1_02.
	 */
	private  void IOP_ControlPanel_v1_02() {
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
				testBed="TBAD"+i;
				message.showMessage("Test Procedure","Step "+step+") Connect "+testBed+" and/or DUT "
						+ "to the AP network if they are not connected yet. If "
						+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O to "
						+ "onboard the DUT to the personal AP.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" (using its "
						+ "virtual Control Panel Controller functionality) to get DUT "
						+ "Control Panel elements on a graphical interface.");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to navigate "
						+ "across the different DUT menu windows available in the virtual "
						+ "control panel and note the values of the parameters displayed "
						+ "in the menu windows.");
				step++;
				
				int resp=message.showQuestion("Pass/Fail Criteria","Verify that the values of "
						+ "the different parameters shown in the virtual control panel "
						+ "menu windows are the same that the real DUT device parameter "
						+ "values.");

				if(resp!=0){//1==NO

					fail("The values of the different parameters shown in the "
							+ "virtual control panel menu windows are not the same that the "
							+ "real DUT device parameter values.");						
					return;}
		}//for
		
		int resp=message.showQuestion("Pass/Fail Criteria","Verify that the values of the parameters"
				+ " obtained with the rest of the TBADs are the same"
				+ " than the results obtained with TBAD1.");

		if(resp!=0){//1==NO

			fail("The values of the parameters"
				+ " obtained with the rest of the TBADs are not the same"
				+ " than the results obtained with TBAD1.");						
			return;}
		
		
	}
	
	



	/**
	 * IOP control panel_v1_03.
	 */
	private  void IOP_ControlPanel_v1_03() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
				testBed="TBAD"+i;
				message.showMessage("Test Procedure","Step "+step+") Connect the DUT "
						+ "and "+testBed+" to the AP network if they are not connected yet. "
						+ "If ICSON_OnboardingInterface ICS value is true, use TBAD_O "
						+ "to onboard the DUT to the personal AP.");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn "
						+ "connection between the DUT and "+testBed+".");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Use "+testBed+" control "
						+ "application to display DUT Control Panel elements.");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to navigate "
						+ "through the menu windows in the virtual DUT control panel and "
						+ "perform following steps for every element:");
				message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to modify the "
						+ "element value.");
				message.showMessage("Test Procedure","Step "+step+" b) Verify that the element "
						+ "value is modified in the virtual control panel.");
				message.showMessage("Test Procedure","Step "+step+" c) Verify at DUT that the "
						+ "value of the element is modified (by means provided by DUT "
						+ "user interface).");
				step++;
				int included=message.showQuestion("Pass/Fail Criteria","For every Control Panel "
						+ "DUT element, are the parameters modified in DUT using virtual "
						+ "Control Panel interface at "+testBed+" updated in DUT and in "
						+ "the Control Panel Interface at "+testBed+".");

				if(included!=0){//1==NO

					fail("For every Control Panel "
						+ "DUT element, the parameters modified in DUT using virtual "
						+ "Control Panel interface at "+testBed+" are not updated in "
						+ "DUT and in the Control Panel Interface at "+testBed+".");						
					return;}
		}
		
		
		int included=message.showQuestion("Pass/Fail Criteria","Verify that the results obtained with the rest of the TBADs"
				+ " are the same than the results obtained with TBAD1.");

		if(included!=0){//1==NO

			fail("The results obtained with the rest of the TBADs"
				+ " are not the same than the results obtained with TBAD1.");						
			return;}
	}



	/**
	 * Show preconditions.
	 */
	private  void showPreconditions() {
		frame.setTitle("Preconditions");
		String msg="Step 1) The passcode for the DUT is set to the default passcode \"000000\""
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

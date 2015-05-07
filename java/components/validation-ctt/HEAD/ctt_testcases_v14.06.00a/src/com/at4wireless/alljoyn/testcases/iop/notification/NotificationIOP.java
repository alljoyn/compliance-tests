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
package com.at4wireless.alljoyn.testcases.iop.notification;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationIOP.
 */
public class NotificationIOP {

	/** The frame. */
	private  JFrame frame;
	 
 	/** The pass. */
 	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "NotifIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	/**
	 * Instantiates a new notification iop.
	 *
	 * @param testCase the test case
	 */
	public NotificationIOP(String testCase) {
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

		String testCase="IOP_Notification-Consumer-v1-03";
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
		if(testCase.equals("IOP_Notification-v1-01")){
			IOP_Notification_v1_01();
		}else if(testCase.equals("IOP_Notification-Consumer-v1-01")){
			IOP_Notification_Consumer_v1_01();
		}else if(testCase.equals("IOP_Notification-Consumer-v1-02")){
			IOP_Notification_Consumer_v1_02();
		}else if(testCase.equals("IOP_Notification-Consumer-v1-03")){
			IOP_Notification_Consumer_v1_03();
			
		}else {
			fail("TestCase not valid");
		}

	}







	


	/**
	 * IOP notification_v1_01.
	 */
	private  void IOP_Notification_v1_01() {
	
		String testBed="TBAD";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to "
				+ "the AP network if they are not connected yet. If "
				+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
				+ "to onboard the DUT to the personal AP.");
		int step=4;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
			message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn "
					+ "connection between the DUT and "+testBed+".");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Configure "+testBed+" to display "
					+ "Notifications received from DUT, indicating type of "
					+ "Notification.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") If supported by DUT and "
					+ "feasible, handle DUT to generate a Notification of information "
					+ "type.");
			step++;
			int included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an "
					+ "information Notification from the DUT?");

			if(included!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "information Notification from the DUT.");
				return;
			}

			message.showMessage("Test Procedure","Step "+step+") If supported by DUT and "
					+ "feasible, handle DUT to generate a Notification of warning type.");
			step++;

			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive a warning "
					+ "Notification from the DUT?");

			if(included!=0){//1==NO

				fail(""+testBed+" not receives a warning "
						+ "Notification from the DUT.");	
				return;
			}
			message.showMessage("Test Procedure","Step "+step+") If supported by DUT and feasible, "
					+ "handle DUT to generate a Notification of emergency type.");
			step++;
			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an emergency "
					+ "Notification from the DUT?");

			if(included!=0){//1==NO

				fail(""+testBed+" receives an emergency Notification from the DUT.");						
				return;
			}

			message.showMessage("Test Procedure","Step "+step+") If supported by DUT (support of "
					+ "ICS ICSN_RichIconUrl) and feasible, handle DUT to generate a "
					+ "‘Notification’ message with ‘richIconUrl’ field.");
			step++;
			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive a Notification "
					+ "where richIconUrl attribute (attrName = 0) is present and a "
					+ "valid URL is included in the iconUrl in the attrValue?");

			if(included!=0){//1==NO

				fail(""+testBed+" receives an emergency Notification from the DUT.");						
				return;
			}

			message.showMessage("Test Procedure","Step "+step+") If supported by DUT (support of "
					+ "ICS ICSN_RichAudioUrl) and feasible, handle DUT to generate a "
					+ "‘Notification’ message with ‘richAudioUrl’ field.");
			step++;
			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive a "
					+ "Notification where richAudioUrl attribute (attrName = 1) is present "
					+ "and a valid URL is included in the audioUrl in the attrValue?");

			if(included!=0){//1==NO

				fail(""+testBed+" not receives a Notification where richAudioUrl "
						+ "attribute (attrName = 1) is present and a valid URL is "
						+ "included in the audioUrl in the attrValue.");						
				return;
				}


		}//for
	}

	///////Notification Consumer Service Interoperability Test Suite


	/**
	 * IOP notification_ consumer_v1_01.
	 */
	private  void IOP_Notification_Consumer_v1_01() {
		
		

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If ICSON_OnboardingInterface ICS value is true, use TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection among the "
				+ "DUT TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 5) Command TBAD1 to send an information "
				+ "Notification message with TTL configured for 2 minutes.");
		int included=message.showQuestion("Pass/Fail Criteria","Does DUT receive an information "
				+ "Notification message from TBAD1?");
		if(included!=0){//1==NO

			fail("DUT not receives an information Notification message from TBAD1.");						
			return;}


		message.showMessage("Test Procedure","Step 6) Command TBAD2 to send a warning "
				+ "Notification message with TTL configured for 15 minutes.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT receive a warning Notification "
				+ "message from TBAD2?");
		if(included!=0){//1==NO

			fail("DUT not receives a warning Notification message from TBAD2.");						
			return;}

		message.showMessage("Test Procedure","Step 7) Operate TBAD3 User Interface to perform "
				+ "an action that would generate a Notification message (with TTL "
				+ "configured to two minutes).");
		included=message.showQuestion("Pass/Fail Criteria","Is notification message received "
				+ "from TBAD3?");
		if(included!=1){//1==NO

			fail("Notification message is received from TBAD3.");						
			return;}

		message.showMessage("Test Procedure","Step 8) Wait for 3 minutes.");
		message.showMessage("Test Procedure","Step 9) Establish an AllJoyn connection between "
				+ "the DUT and TBAD3.");
		included=message.showQuestion("Pass/Fail Criteria","Is notification message  received "
				+ "from TBAD3?");
		if(included!=1){//1==NO

			fail("Notification message is received from TBAD3.");						
			return;}

		message.showMessage("Test Procedure","Step 10) Wait for 1 minute");
		message.showMessage("Test Procedure","Step 11) Switch off and on the DUT.");
		message.showMessage("Test Procedure","Step 12) Wait for 1 minute");
		message.showMessage("Test Procedure","Step 13) Verify that the DUT is connected to the "
				+ "AP network.");
		message.showMessage("Test Procedure","Step 14) Establish an AllJoyn connection between "
				+ "the DUT and each of the TBADs of the Test Bed.");
		included=message.showQuestion("Pass/Fail Criteria","Is notification message from TBAD2 only displayed in the DUT?");
		if(included!=0){//1==NO

			fail("Only notification message from TBAD2 is not displayed in the DUT.");						
			return;}

	}




	/**
	 * IOP notification_ consumer_v1_02.
	 */
	private  void IOP_Notification_Consumer_v1_02() {
		
		

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 2) Connect TBAD1 and TBAD2 to the "
				+ "AP network if they are not connected yet.");
		message.showMessage("Test Procedure","Step 3) Establish an AllJoyn connection "
				+ "between TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 4) Command TBAD1 to send a warning "
				+ "‘Notification’ message (Notification 1) with TTL configured "
				+ "for 10 minutes.");
		message.showMessage("Test Procedure","Step 5) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 6) Command TBAD1 to send an emergency "
				+ "‘Notification’ message (Notification 2) with TTL configured for "
				+ "10 minutes.");
		message.showMessage("Test Procedure","Step 7) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 8) Command TBAD1 to send a warning "
				+ "Notification message (Notification 3) with TTL configured for "
				+ "10 minutes.");
		message.showMessage("Test Procedure","Step 9) Switch on DUT.");
		message.showMessage("Test Procedure","Step 10) Connect the DUT to the AP network "
				+ "if it is not connected yet. If required, use TBAD_O to onboard "
				+ "the DUT to the personal AP");
		message.showMessage("Test Procedure","Step 11) Switch on TBAD3.");
		message.showMessage("Test Procedure","Step 12) Connect TBAD3 to the AP network if "
				+ "they are not connected yet.");
		message.showMessage("Test Procedure","Step 13) Establish an AllJoyn connection "
				+ "between the DUT and TBAD3.");
		int included=message.showQuestion("Pass/Fail Criteria","Does DUT receive an emergency "
				+ "Notification message (Notification 2) from TBAD1 and a warning "
				+ "Notification message (Notification 3) from TBAD1?");
		if(included!=0){//1==NO

			fail("DUT not receives an emergency Notification message (Notification 2) "
					+ "from TBAD1 and a warning Notification message (Notification 3) "
					+ "from TBAD1.");						
			return;}



		message.showMessage("Test Procedure","Step 14) Command TBAD3 to send a warning Notification"
				+ "message (Notification 4) with TTL configured for 10 minutes.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT receive a warning Notification "
				+ "message (Notification 4) from TBAD3?");
		if(included!=0){//1==NO

			fail("DUT not receives a warning Notification message (Notification 4) "
					+ "from TBAD3.");						
			return;}


		message.showMessage("Test Procedure","Step 15) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 16) Command TBAD1 to send an emergency "
				+ "Notification message (Notification 5) with TTL configured for "
				+ "10 minutes.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT receive an emergency "
				+ "Notification message (Notification 5) from TBAD3?");
		if(included!=0){//1==NO

			fail("DUT not receives a warning Notification message (Notification 4) "
					+ "from TBAD3.");						
			return;}


		message.showMessage("Test Procedure","Step 17) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 18) Command TBAD1 to send a warning "
				+ "Notification message (Notification 6) with TTL configured for "
				+ "10 minutes.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT receive a warning "
				+ "Notification message (Notification 6) from TBAD3? Is Notification 4 "
				+ "no longer displayed?");
		if(included!=0){//1==NO

			fail("DUT not receives a warning Notification message (Notification 6) from TBAD3? "
					+ "Notification 4 is no longer displayed.");						
			return;}

	}



	/**
	 * IOP notification_ consumer_v1_03.
	 */
	private  void IOP_Notification_Consumer_v1_03() {
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If ICSON_OnboardingInterface "
				+ "ICS value is true, use TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT TBAD1, TBAD2 and TBAD3");
		message.showMessage("Test Procedure","Step 5) Command TBAD1 to send an emergency "
				+ "‘Notification’ message.");
		int included=message.showQuestion("Pass/Fail Criteria","Does DUT receive Notification "
				+ "message from TBAD1 with the correct language?");
		if(included!=0){//1==NO

			fail("DUT not receives Notification message from TBAD1 with the "
					+ "correct language.");						
			return;}

		message.showMessage("Test Procedure","Step 6) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 7) Command TBAD2 to send an information "
				+ "‘Notification’ message.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT DUT receive Notification "
				+ "message from TBAD2 with the correct language?");
		if(included!=0){//1==NO

			fail("DUT not receives Notification message from TBAD2 with the correct "
					+ "language.");						
			return;}
		
		
		
		message.showMessage("Test Procedure","Step 8) Wait for 1 minute.");
		message.showMessage("Test Procedure","Step 9) Command TBAD3 to send a warning "
				+ "‘Notification’ message.");
		included=message.showQuestion("Pass/Fail Criteria","Does DUT receives Notification "
				+ "message from TBAD3 with the correct language?");
		if(included!=0){//1==NO

			fail("DUT not receives Notification "
				+ "message from TBAD3 with the correct language.");						
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

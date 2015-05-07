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
package com.at4wireless.alljoyn.testcases.iop.lighting;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class LightingIOP.
 */
public class LightingIOP {

	/** The frame. */
	private  JFrame frame;
	
	/** The pass. */
	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "LightingIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);


	/**
	 * Instantiates a new lighting iop.
	 *
	 * @param testCase the test case
	 */
	public LightingIOP(String testCase) {
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

		String testCase="IOP_LSF_Lamp-v1-09";
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
		if(testCase.equals("IOP_LSF_Controller-v1-01")){
			IOP_LSF_Controller_v1_01();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-02")){
			IOP_LSF_Lamp_v1_02();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-03")){
			IOP_LSF_Lamp_v1_03();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-04")){
			IOP_LSF_Lamp_v1_04();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-05")){
			IOP_LSF_Lamp_v1_05();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-06")){
			IOP_LSF_Lamp_v1_06();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-07")){
			IOP_LSF_Lamp_v1_07();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-08")){
			IOP_LSF_Lamp_v1_08();
		}else if(testCase.equals("IOP_LSF_Lamp-v1-09")){
			IOP_LSF_Lamp_v1_09(); 
		}else if(testCase.equals("IOP_LSF_Lamp-v1-10")){
			IOP_LSF_Lamp_v1_10(); 
		}else if(testCase.equals("IOP_LSF_Lamp-v1-11")){
			IOP_LSF_Lamp_v1_11(); 
		}else if(testCase.equals("IOP_LSF_Lamp-v1-12")){
			IOP_LSF_Lamp_v1_12(); 
		}else if(testCase.equals("IOP_LSF_Lamp-v1-13")){
			IOP_LSF_Lamp_v1_13(); 
		}else {
			fail("TestCase not valid");
		}

	}



	/**
	 * IOP ls f_ lamp_v1_13.
	 */
	private  void IOP_LSF_Lamp_v1_13() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_O, TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ "they are not connected yet. (If"
				+ "ICSON_OnboardingInterface ICS value is true, use"
				+ "TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify that an AllJoyn connection has been established between the DUT,"
				+ " TBAD1 and TBAD2. If not, "
				+ "perform the requested steps to establish the AllJoyn connection.");
		message.showMessage("Test Procedure","Step 5) Operate TBAD1 to store following scenes.");
		message.showMessage("Test Procedure","Step 5 a) Scene 1: No transition; Blue color, maximum"
				+ " brightness.");
		message.showMessage("Test Procedure","Step 5 b) Scene 2: Transition effect.");
		message.showMessage("Test Procedure","Step 5 c) timeStamp: 100 ms.");
		message.showMessage("Test Procedure","Step 5 d) NewState: Purple color, low brightness.");
		message.showMessage("Test Procedure","Step 5 e) Transition period: 2000 ms.");
		message.showMessage("Test Procedure","Step 5 f) Scene 3: Pulse effect.");
		message.showMessage("Test Procedure","Step 5 g) FromState: Yellow color, medium"
				+ " brightness.");
		message.showMessage("Test Procedure","Step 5 h) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure","Step 5 i) period: 1000 ms.");
		message.showMessage("Test Procedure","Step 5 j) duration: 1000 ms.");
		message.showMessage("Test Procedure","Step 5 k) numPulses: 20.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD2 to make DUT perform scene 1.");
		int resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp change to state defined in"
				+ " scene 1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp changes to state defined in"
					+ " scene 1.");

			return;}
		message.showMessage("Test Procedure","Step 7) Operate TBAD1 to make DUT perform scene 2.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp change to state defined in"
				+ " scene 2?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp changes to state defined in"
					+ " scene 2.");

			return;}
		message.showMessage("Test Procedure","Step 8) Operate TBAD2 to make DUT perform scene 3.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp change to state defined in"
				+ " scene 3?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp changes to state defined in"
					+ " scene 3.");

			return;}

	}



	/**
	 * IOP ls f_ lamp_v1_12.
	 */
	private  void IOP_LSF_Lamp_v1_12() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_O, TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify that an AllJoyn connection has been established between the DUT,"
				+ " TBAD1 and TBAD2. If not, perform the requested steps to establish the AllJoyn.");
		message.showMessage("Test Procedure","Step 5) Operate TBAD1 to switch DUT lamp on and to set"
				+ " medium brightness and yellow color.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to perform a DUT lamp transition"
				+ " sequence as defined (if specified values are not"
				+ " supported by the DUT use DUT supported values):");
		message.showMessage("Test Procedure","Step 6 a) timeStamp: 100 ms.");
		message.showMessage("Test Procedure","Step 6 b) NewState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure","Step 6 c) Transition period: 30000 ms.");
		message.showMessage("Test Procedure","Step 7) Ten seconds after starting step 6, operate TBAD2"
				+ " to perform a DUT lamp pulse sequence as defined"
				+ " (if specified values are not supported by the devices"
				+ " use device supported values):");
		message.showMessage("Test Procedure","Step 7 a) FromState: Red color, medium brightness.");
		message.showMessage("Test Procedure","Step 7 b) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure","Step 7 c) period: 1000 ms.");
		message.showMessage("Test Procedure","Step 7 d) duration: 1000 ms.");
		message.showMessage("Test Procedure","Step 7 e) numPulses: 20.");
		message.showMessage("Test Procedure","Step 7 f) timeStamp: 100 ms.");
		int resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp start displaying the pulse effect"
				+ " specified in step 7 without waiting to complete the"
				+ " transition effect defined in step 6?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp doesn't start displaying the pulse effect"
					+ " specified in step 7 without waiting to complete the"
					+ " transition effect defined in step 6.");

			return;}
	}



	/**
	 * IOP ls f_ lamp_v1_11.
	 */
	private  void IOP_LSF_Lamp_v1_11() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_O, TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify that an AllJoyn connection has been established between the DUT,"
				+ " TBAD1 and TBAD2. If not, perform the requested steps to establish the AllJoyn connection.");
		message.showMessage("Test Procedure","Step 5) Operate TBAD1 to switch DUT lamp on and to set"
				+ " medium brightness and red color.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to perform a DUT lamp transition"
				+ " sequence as defined (if specified values are not"
				+ " supported by the DUT use DUT supported values):");
		message.showMessage("Test Procedure","Step 6 a) timeStamp: 100 ms.");
		message.showMessage("Test Procedure","Step 6 b) NewState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure","Step 6 c) Transition period: 2000 ms.");
		int resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp display a pulse effect according"
				+ " to the parameters defined in step 6?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp doesn't display a pulse effect according"
					+ " to the parameters defined in step 6.");

			return;}

		message.showMessage("Test Procedure","Step 7) Operate TBAD2 to perform a DUT lamp transition"
				+ " sequence as defined (if specified values are not"
				+ " supported by the devices use device supported"
				+ " values):");
		message.showMessage("Test Procedure","Step 7 a) timeStamp: 500 ms.");
		message.showMessage("Test Procedure","Step 7 b) NewState: Yellow color, maximum"
				+ " brightness.");
		message.showMessage("Test Procedure","Step 7 c) Transition period: 4000 ms.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp display a pulse effect according"
				+ " to the parameters defined in step 7?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp doesn't display a pulse effect according"
					+ " to the parameters defined in step 7.");

			return;}

	}



	/**
	 * IOP ls f_ lamp_v1_10.
	 */
	private  void IOP_LSF_Lamp_v1_10() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_O, TBAD1 and TBAD2.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if they are not connected yet. If ICSON_OnboardingInterface ICS value is true,\n"
				+ " use TBAD_O to onboard the DUT to the personal AP");
		message.showMessage("Test Procedure","Step 4) Verify that an AllJoyn connection has been established between the DUT, TBAD1 and TBAD2."
				+ " If not, perform the requested steps to establish the AllJoyn connection.");
		message.showMessage("Test Procedure","Step 5) Operate TBAD1 to perform a DUT lamp pulse"
				+ " sequence as defined (if specified values are not"
				+ " supported by the DUT use DUT supported values):");
		message.showMessage("Test Procedure","Step 5 a) FromState: Red color, maximum brightness.");
		message.showMessage("Test Procedure","Step 5 b) ToState: Green color, maximum brightness.");
		message.showMessage("Test Procedure","Step 5 c) period: 500 ms.");
		message.showMessage("Test Procedure","Step 5 d) duration: 1000 ms.");
		message.showMessage("Test Procedure","Step 5 e) numPulses: 10.");

		message.showMessage("Test Procedure","Step 5 f) timeStamp: 100 ms.");

		int resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp display a pulse effect according"
				+ " to the parameters defined in step 5?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp doesn't display a pulse effect according"
					+ " to the parameters defined in step 5.");

			return;}
		message.showMessage("Test Procedure","Step 6) Operate TBAD2 to perform a DUT lamp pulse"
				+ " sequence as defined (if specified values are not"
				+ " supported by the DUT use DUT supported values):");
		message.showMessage("Test Procedure","Step 6 a) FromState: Red color, medium brightness.");
		message.showMessage("Test Procedure","Step 6 b) ToState: Blue color, maximum brightness.");
		message.showMessage("Test Procedure","Step 6 c) period: 200 ms.");
		message.showMessage("Test Procedure","Step 6 d) duration: 3000 ms.");
		message.showMessage("Test Procedure","Step 5 e) numPulses: 20.");

		message.showMessage("Test Procedure","Step 6 f) timeStamp: 2000 ms.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp display a pulse effect according"
				+ " to the parameters defined in step 6?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp doesn't display a pulse effect according"
					+ " to the parameters defined in step 6.");

			return;}


	}



	/**
	 * IOP ls f_ lamp_v1_08.
	 */
	private  void IOP_LSF_Lamp_v1_08() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established among the DUT, TBAD1 and TBAD2. If"
				+ " not, establish the AllJoyn connection.");
		message.showMessage("Test Procedure","Step 5) Command TBAD1 to switch the DUT lamp on and,"
				+ " if DUT supports ICSL_Dimmable, set its brightness"
				+ " and saturation to its maximum values.");

		int resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on? If DUT"
				+ " supports ICSL_Dimmable, are DUT brightness and"
				+ " saturation changed to its maximum values?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and or If DUT"
					+ " supports ICSL_Dimmable, DUT brightness and"
					+ " saturation are not changed to its maximum values.");

			return;}
		message.showMessage("Test Procedure","Step 6) Switch on TBAD3 and TBAD4.");
		message.showMessage("Test Procedure","Step 7) Verify that TBAD3 and TBAD4 have joined the"
				+ " AllJoyn connection and if not, establish the required"
				+ " steps to get the AllJoyn connection established.");
		message.showMessage("Test Procedure","Step 8) Operate TBAD1 to switch on TBAD3 and TDAB4"
				+ " lamps and change their lamps brightness to its"
				+ " higher value.");
		message.showMessage("Test Procedure","Step 9) Operate TBAD1 to switch off and on the DUT and if"
				+ " DUT supports ICSL_Dimmable, to change DUT"
				+ " lamp saturation to a medium value.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched off and on? After"
				+ " switching on, If DUT supports ICSL_Dimmable,"
				+ " is DUT lamp saturation changed to the value set by"
				+ " TBAD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and or If DUT"
					+ " supports ICSL_Dimmable, DUT brightness and"
					+ " saturation are not changed to its maximum values.");

			return;}
		message.showMessage("Test Procedure","Step 10) If DUT supports ICSL_Color, operate TBAD2 to"
				+ " change DUT lamp color to a different color value.");

		resp=message.showQuestion("Pass/Fail Criteria","If DUT lamp supports ICSL_Color, is DUT"
				+ " lamp color changed to the value set by TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT lamp supports ICSL_Color, DUT"
					+ " lamp color is not changed to the value set by TBAD2.");

			return;}

		message.showMessage("Test Procedure","Step 11) Operate TBAD2 to make a group ‘Group 1’"
				+ "including DUT, TBAD3 and TBAD4.");
		message.showMessage("Test Procedure","Step 12) Operate TBAD2 to make a group ‘Group 2’"
				+ "	including DUT and TBAD3.");
		message.showMessage("Test Procedure","Step 13) Operate TBAD2 to switch the lamps of ‘Group 1’ off.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched off? Are TBAD3 and"
				+ " TBAD4 also switched off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched off or TBAD3 and"
					+ " TBAD4 are not also switched off.");

			return;}
		message.showMessage("Test Procedure","Step 14) Operate TBAD1 to switch the lamps of ‘Group 2’ off.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on? Is TBAD3 also"
				+ " switched on?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on or TBAD3 is not"
					+ " switched on.");

			return;}

		message.showMessage("Test Procedure","Step 15) If DUT supports ICSL_ColorTemperature, operate"
				+ " TBAD2 to change Group2 lamps color temperature"
				+ " significantly.");

		resp=message.showQuestion("Pass/Fail Criteria","If DUT lamp supports"
				+ " ICSL_ColorTemperature, are DUT and TBAD3 lamps"
				+ " color temperature changed to the value set by"
				+ " TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT and TBAD3 lamps"
					+ " color temperature are not changed to the value set by"
					+ " TBAD2.");

			return;}
		message.showMessage("Test Procedure","Step 16) If DUT supports ICSL_Dimmable, operate TBAD2"
				+ " to change ‘Group 2’ lamps saturation to its"
				+ " maximum value.");
		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT"
				+ " and TDAD3 lamps saturation changed to the"
				+ " value set by TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT supports ICSL_Dimmable, DUT"
					+ " and TDAD3 lamps saturation are not changed to the"
					+ " value set by TBAD2.");

			return;}
		message.showMessage("Test Procedure","Step 17) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to change ‘Group 2’ lamps saturation to a low value.");
		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT"
				+ " and TDAD3 lamps saturation are changed to the"
				+ " value set by TBAD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT supports ICSL_Dimmable, DUT"
					+ " and TDAD3 lamps saturation are not changed to the"
					+ " value set by TBAD1.");

			return;}
		message.showMessage("Test Procedure","Step 18) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to change ‘Group 1’ lamps saturation to its"
				+ " maximum value.");
		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT,"
				+ " TDAD3 and TDAD4 lamps saturation changed"
				+ " to the value set by TBAD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT supports ICSL_Dimmable, DUT,"
					+ " TDAD3 and TDAD4 lamps saturation are not changed"
					+ " to the value set by TBAD1.");

			return;}

	}



	/**
	 * IOP ls f_ lamp_v1_09.
	 */
	private  void IOP_LSF_Lamp_v1_09() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_O and TBAD1.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been established between the DUT and TBAD1. If not, establish an AllJoyn connection between the DUT and TBAD1.");
		message.showMessage("Test Procedure","Step 5) Operate TBAD1 to switch DUT lamp on and off.");
		int resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off.");

			return;}


		message.showMessage("Test Procedure","Step 6) Switch off and on TBAD1 and perform required"
				+ " actions to connect it to the AllJoyn network.");
		message.showMessage("Test Procedure","Step 7) Operate TBAD1 to switch DUT lamp on and off.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off.");

			return;}
		message.showMessage("Test Procedure","Step 8) Switch TBAD2 on and perform required actions to"
				+ " join TBAD2 to the AllJoyn network.");
		message.showMessage("Test Procedure","Step 9) Switch off TBAD1.");
		message.showMessage("Test Procedure","Step 10) Operate TBAD2 to switch DUT lamp on and off.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off.");

			return;}
		message.showMessage("Test Procedure","Step 11) Switch the DUT off and on and join the DUT again"
				+ " to the AllJoyn network if required.");
		message.showMessage("Test Procedure","Step 12) Operate TBAD2 to switch DUT lamp on and off.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off.");

			return;}
		message.showMessage("Test Procedure","Step 13) Switch TBAD1 on and perform required actions to"
				+ " join TBAD1 to the AllJoyn network.");
		message.showMessage("Test Procedure","Step 14) Operate TBAD1 to switch DUT lamp on and off.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off.");

			return;}




	}



	/**
	 * IOP ls f_ lamp_v1_07.
	 */
	private  void IOP_LSF_Lamp_v1_07() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established among all TBADs. If not, establish the"
				+ " AllJoyn connection.");
		message.showMessage("Test Procedure","Step 5) Command TBAD1 to switch on the lamps of TBAD3"
				+ " and TBAD4 and to set their brightness to their"
				+ " maximum value.");
		message.showMessage("Test Procedure","Step 6) Switch on DUT and connect it to the AP network if it"
				+ "is not connected yet.");
		message.showMessage("Test Procedure","Step 7) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the required steps to"
				+ " get the connection to the DUT (perform onboarding"
				+ " process if required).");
		message.showMessage("Test Procedure","Step 8) Operate TBAD1 to switch the DUT lamp on and if"
				+ " DUT supports ICSL_Dimmable , change DUT lamp"
				+ " brightness and lamp saturation to its higher values.");

		int resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and, If DUT"
				+ " supports ICSL_Dimmable, are DUT brightness and"
				+ " DUT saturation changed to its maximum values?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and, If DUT"
					+ " supports ICSL_Dimmable, DUT brightness and"
					+ " DUT saturation are not changed to its maximum values.");

			return;}
		message.showMessage("Test Procedure","Step 9) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to switch off and on the DUT and change DUT lamp"
				+ " saturation to a medium value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched off and on? Is DUT"
				+ " lamp saturation changed to the value set by"
				+ " TBAD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched off and on or DUT"
					+ " lamp saturation is not changed to the value set by"
					+ " TBAD1.");
			return;}
		message.showMessage("Test Procedure","Step 10) If DUT supports ICSL_Color, DUT lamp"
				+ " color is changed to the value set by TBAD2.");
		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Color, Is DUT lamp"
				+ "color changed to the value set by TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT supports ICSL_Color, DUT lamp"
					+ " color is not changed to the value set by TBAD2.");
			return;}
		message.showMessage("Test Procedure","Step 11) Operate TBAD2 to make a group ‘Group 1’"
				+ " including DUT, TBAD3 and TBAD4.");
		message.showMessage("Test Procedure","Step 12) Operate TBAD2 to make a group ‘Group 2’"
				+ " including DUT and TBAD3.");
		message.showMessage("Test Procedure","Step 13) Operate TBAD2 to switch off lamps of ‘Group 1’.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched off? Are TBAD3 and"
				+ " TBAD4 also switched off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not  switched off or TBAD3 and"
					+ " TBAD4 are not switched off.");
			return;}
		message.showMessage("Test Procedure","Step 14) Operate TBAD1 to switch on lamps of Group 2’.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on? Is TBAD3 also"
				+ " switched on?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on or TBAD3 is not "
					+ " switched on?");
			return;}
		message.showMessage("Test Procedure","Step 15) If DUT supports ICSL_ColorTemperature, operate"
				+ " TBAD2 to change Group2 lamps color temperature"
				+ " significantly.");

		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_ColorTemperature,"
				+ " Are DUT and TBAD3 lamps color temperature"
				+ " changed to the value set by TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("If DUT supports ICSL_ColorTemperature,"
					+ " DUT and TBAD3 lamps color temperature are"
					+ " not changed to the value set by TBAD2.");
			return;}
		message.showMessage("Test Procedure","Step 16) If DUT supports ICSL_Dimmable, operate TBAD2"
				+ " to change ‘Group 2’ lamps saturation to its"
				+ " maximum value.");

		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT"
				+ " and TDAD3 lamps saturation changed to the"
				+ " value set by TBAD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT"
					+ " and TDAD3 lamps saturation are not changed to the"
					+ " value set by TBAD2.");
			return;}
		message.showMessage("Test Procedure","Step 17) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to change ‘Group 2’ lamps saturation to a low value."); 

		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT"
				+ " and TDAD3 lamps saturation changed to the"
				+ " value set by TBAD1.?");
		if(resp!=0){//1==NO  null==(X)

			fail(" DUT"
					+ " and TDAD3 lamps saturation are not changed to the"
					+ " value set by TBAD1.");
			return;}
		message.showMessage("Test Procedure","Step 18) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to change ‘Group 1’ lamps saturation to its"
				+ " maximum value."); 
		resp=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSL_Dimmable, are DUT,"
				+ " TDAD3 and TDAD4 lamps saturation changed"
				+ " to the value set by TBAD1?");
		if(resp!=0){//1==NO  null==(X)
			fail("DUT,"
					+ " TDAD3 and TDAD4 lamps saturation are not changed"
					+ " to the value set by TBAD1.");
			return;}

		message.showMessage("Test Procedure","Step 19) Switch off TBAD3 and TBAD4."); 
		message.showMessage("Test Procedure","Step 20) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to change DUT lamp saturation to a medium value."); 
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation changed to the"
				+ " value set by TBAD1?");
		if(resp!=0){//1==NO  null==(X)
			fail("DUT lamp saturation is not changed to the"
					+ " value set by TBAD1.");
			return;}
		message.showMessage("Test Procedure","Step 21) If DUT supports ICSL_Dimmable, operate TBAD2"
				+ " to change DUT lamp saturation to a high value."); 
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation changed to the"
				+ " value set by TBAD1?");
		if(resp!=0){//1==NO  null==(X)
			fail("DUT lamp saturation is not changed to the"
					+ " value set by TBAD1.");
			return;}	

	}



	/**
	 * IOP ls f_ lamp_v1_06.
	 */
	private  void IOP_LSF_Lamp_v1_06() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established between the DUT and TBAD1. If not, establish an AllJoyn connection"
				+ " between the DUT and TBAD1.");
		message.showMessage("Test Procedure","Step 5) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the requested steps to"
				+ " get all TBADs AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure","Step 6) Operate all TBADs to display current Lamp "
				+ "brightness value.");
		int resp=message.showQuestion("Pass/Fail Criteria","Is correct lamp brightness value displayed"
				+ " in all TBADs (same value in all of them)?");
		if(resp!=0){//1==NO  null==(X)

			fail("Correct lamp brightness value is"
					+ " not displayed in all TBADs.");

			return;}
		message.showMessage("Test Procedure","Step 7) Operate TBAD1 to change DUT lamp brightness to"
				+ " its higher value.");
		message.showMessage("Test Procedure","Step 8) Operate TBAD1 to modify lamp brightness value to"
				+ " a medium value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp brightness not modified to the new"
				+ " value set by TBAD1? Is current brightness value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp brightness is not modified to the new"
					+ " value set by TBAD1 or current brightness value is not"
					+ " displayed in all TBADs.");

			return;}
		message.showMessage("Test Procedure","Step 9) Operate TBAD2 to modify brightness value to a low"
				+ " value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp brightness modified to the"
				+ " new value set by TBAD2? Is current brightness value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp brightness is not modified to the"
					+ " new value set by TBAD2 or current brightness value"
					+ " is not displayed in all TBADs?");
			return;}
		message.showMessage("Test Procedure","Step 10) Repeat step 9 with all TBADs setting brightness"
				+ " value to a different value each time.");



		message.showMessage("Test Procedure","Step 11) Note current brightness value.");
		message.showMessage("Test Procedure","Step 12) Switch DUT off and on, and reconnect it if"
				+ " necessary to the AP and to the AllJoyn session.");

		message.showMessage("Test Procedure","Step 13) Switch off TABD1.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp brightness have the same value"
				+ " it had before being switched off and on? Is the"
				+ " value properly displayed in all TABDs (except"
				+ " TABD1 that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp brightness has not the same value"
					+ " it had before being switched off and on or the"
					+ " value is not properly displayed in all TABDs (except"
					+ " TABD1 that is off).");
			return;}

		message.showMessage("Test Procedure","Step 14) Operate TBAD2 to modify brightness value to a"
				+ " different value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp brightness modified to the"
				+ " new value set by TBAD2? Is current brightness value "
				+ " displayed in all TBADs (except TABD1 that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp brightness is not  modified to the"
					+ " new value set by TBAD2 or current brightness value "
					+ "is displayed not in all TBADs (except TABD1 that is off).");
			return;}
		message.showMessage("Test Procedure","Step 15) Switch TABD1 on and operate it to perform an"
				+ " AllJoyn connection if necessary.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp brightness correctly updated"
				+ " in TABD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp brightness is not correctly updated"
					+ " in TABD1");
			return;}

	}



	/**
	 * IOP ls f_ lamp_v1_05.
	 */
	private  void IOP_LSF_Lamp_v1_05() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established between the DUT and TBAD1 (e.g.:"
				+ " DUT Lamp name will be displayed in TBAD1"
				+ " screen). If not, establish an AllJoyn connection"
				+ " between the DUT and TBAD1.");
		message.showMessage("Test Procedure","Step 5) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the requested steps to"
				+ " get all TBADs AllJoyn connected to the DUT.");
		message.showMessage("Test Procedure","Step 6) Operate all TBADs to display current Lamp color"
				+ " temperature value.");
		int resp=message.showQuestion("Pass/Fail Criteria","Is correct lamp color temperature value "
				+ " displayed in all TBADs (same value in all of them)?");
		if(resp!=0){//1==NO  null==(X)

			fail("Correct lamp color temperature value is"
					+ " not displayed in all TBADs.");

			return;}

		message.showMessage("Test Procedure","Step 7) If DUT supports ICSL_Dimmable, command TBAD1"
				+ " to turn DUT lamp brightness to its higher value.");
		message.showMessage("Test Procedure","Step 8) Operate TBAD1 to change DUT lamp color"
				+ " temperature to its higher value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature modified to"
				+ " the new value set by TBAD1? Is current color"
				+ " temperature value displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not modified to"
					+ " the new value set by TBAD1 or current color"
					+ " temperature value is displayed in all TBADs.");

			return;}
		message.showMessage("Test Procedure","Step 9) Operate TBAD1 to modify lamp color temperature"
				+ " value to a medium value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature modified to"
				+ " the new value set by TBAD1? Is current color"
				+ " temperature value displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not modified to"
					+ " the new value set by TBAD1 or current color"
					+ " temperature value is not displayed in all TBADs.");

			return;}
		message.showMessage("Test Procedure","Step 10) Operate TBAD2 to modify color temperature value"
				+ " to a low value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature modified to"
				+ " the new value set by TBAD2? Is current color"
				+ " temperature value displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not modified to"
					+ " the new value set by TBAD2 or current color"
					+ " temperature value is not displayed in all TBADs.");

			return;}


		message.showMessage("Test Procedure","Step 11) Repeat step with all TBADs setting color"
				+ " temperature value to a different value each time.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature each time"
				+ " modified to the new value set by the corresponding"
				+ " TBAD?Is current color temperature value displayed"
				+ " in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not each time"
					+ " modified to the new value set by the corresponding"
					+ " TBAD or current color temperature value is not displayed"
					+ " in all TBADs.");

			return;}

		message.showMessage("Test Procedure","Step 12) Note current color temperature value.");
		message.showMessage("Test Procedure","Step 13) Switch DUT off and on, and reconnect it if"
				+ " necessary to the AP and to the AllJoyn session.");
		message.showMessage("Test Procedure","Step 14) Switch off TABD1.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp color temperature have the same"
				+ " value it had before being switched off and on?"
				+ " Is the value properly displayed in all TABDs (except"
				+ " TABD1 that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature has not the same"
					+ " value it had before being switched off and on and"
					+ " the value is properly displayed in all TABDs.");

			return;}

		message.showMessage("Test Procedure","Step 15) Operate TBAD2 to modify color temperature value"
				+ " to a different value.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature modified to"
				+ " the new value set by TBAD2? Is current color"
				+ " temperature value displayed in all TBADs (except"
				+ " TABD1 that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not modified to"
					+ " the new value set by TBAD2 or current color"
					+ " temperature value is not displayed in all TBADs ");

			return;}


		message.showMessage("Test Procedure","Step 16) Switch TABD1 on and operate it to perform an"
				+ " AllJoyn connection if necessary.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp color temperature correctly"
				+ " updated in TABD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp color temperature is not correctly"
					+ " updated in TABD1");
			return;}

	}



	/**
	 * IOP ls f_ lamp_v1_04.
	 */
	private  void IOP_LSF_Lamp_v1_04() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");


		int i=1;

		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established between the DUT and TBAD"+i+" (e.g.:"
				+ " DUT Lamp name will be displayed in TBAD"+i+""
				+ " screen). If not, establish an AllJoyn connection"
				+ " between the DUT and TBAD"+i+".");


		message.showMessage("Test Procedure","Step 5) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the requested steps to"
				+ " get all TBADs AllJoyn connected to the DUT.");


		message.showMessage("Test Procedure","Step 6) Operate all TBADs to display current Lamp"
				+ " saturation value.");

		int resp=message.showQuestion("Pass/Fail Criteria","Is correct lamp saturation value displayed in"
				+ " all TBADs (same value in all of them)?");
		if(resp!=0){//1==NO  null==(X)

			fail("Correct lamp saturation value is not displayed in"
					+ " all TBADs (same value in all of them).");

			return;}
		message.showMessage("Test Procedure","Step 7) Command TBAD1 to turn DUT lamp brightness to"
				+ " its higher value.");
		message.showMessage("Test Procedure","Step 8) Operate TBAD1 to change DUT lamp saturation to"
				+ " its higher value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation modified to the new"
				+ " value set by TBAD1? Is current saturation value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not modified to the new"
					+ " value set by TBAD1 or current saturation value is not"
					+ " displayed in all TBADs?");

			return;}
		message.showMessage("Test Procedure","Step 9) Operate TBAD1 to modify lamp saturation value to"
				+ " a medium value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation modified to the"
				+ " new value set by TBAD1? Is current saturation value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not modified to the"
					+ " new value set by TBAD1 or current saturation value"
					+ " is not displayed in all TBADs.");

			return;	}
		message.showMessage("Test Procedure","Step 10) Operate TBAD2 to modify saturation value to a low"
				+ " value.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation modified to the"
				+ " new value set by TBAD2? Is current saturation value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not modified to the"
					+ " new value set by TBAD2 or current saturation value"
					+ " is not displayed in all TBADs.");

			return;	}

		message.showMessage("Test Procedure","Step 11) Repeat step with all TBADs setting saturation"
				+ " value to a different value each time.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation each time modified"
				+ " to the new value set by the corresponding TBAD?"
				+ " Is current saturation value displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not each time modified"
					+ " to the new value set by the corresponding TBAD or"
					+ " current saturation value is not displayed in all TBADs");

			return;}

		message.showMessage("Test Procedure","Step 12) Note current Lamp saturation value.");

		message.showMessage("Test Procedure","Step 13) Switch DUT off and on, and reconnect it if"
				+ " necessary to the AP and to the AllJoyn session.");
		message.showMessage("Test Procedure","Step 14) Switch off TABD1.");
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp saturation have the same value it "
				+ "had before being switched off and on? Is the value"
				+ " properly displayed in all TABDs (except TABD1"
				+ " that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not modified to the"
					+ " new value set by TBAD2 or current saturation value"
					+ " is not displayed in all TBADs.");

			return;}

		message.showMessage("Test Procedure","Step 15) Operate TBAD2 to modify saturation value to a"
				+ " different value.");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation modified to the"
				+ " new value set by TBAD2? Is current saturation value"
				+ " displayed in all TBADs (except TABD1 that is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not modified to the"
					+ " new value set by TBAD2 or current saturation value"
					+ " is not displayed in all TBADs (except TABD1 that is off)");

			return;}

		message.showMessage("Test Procedure","Step 16) Switch TABD2 on and operate it to perform an"
				+ " AllJoyn connection if necessary.");
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp saturation correctly updated"
				+ " in TABD1?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp saturation is not correctly updated"
					+ " in TABD1.");

			return;}




	}



	/**
	 * IOP ls f_ controller_v1_01.
	 */
	private  void IOP_LSF_Controller_v1_01() {


		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");



		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");

		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established between the DUT and TBAD1 (e.g.:"
				+ " DUT Lamp name will be displayed in TBAD1"
				+ " screen). If not, establish an AllJoyn connection"
				+ " between the DUT and TBAD1.");


		message.showMessage("Test Procedure","Step 5) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the requested steps to"
				+ " get all TBADs AllJoyn connected to the DUT.");

		message.showMessage("Test Procedure","Step 6) Command all TBADs to display current DUT lamp"
				+ " switching on/off status.");




		int resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp status "
				+ "correctly displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp status is not correctly displayed in all"
					+ " TBADs.");

			return;}

		message.showMessage("Test Procedure","Step 7) Command TBAD1 to switch the DUT lamp on"
				+ " (switching it off previously if the lamp was already on).");

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT switched on?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT is not switched on");

			return;}
		int step=8;
		for(int i=1;i<=3;i++){


			message.showMessage("Test Procedure","Step "+step+") Verify that DUT lamp is on and that correct DUT"
					+ " status is shown in all TBADs.");

			step++;
			message.showMessage("Test Procedure","Step "+step+") Command TBAD"+i+" to switch the DUT lamp off.");
			step++;

			resp=message.showQuestion("Pass/Fail Criteria","Is DUT switched off?");
			if(resp!=0){//1==NO  null==(X)

				fail("DUT is not switched off");

				return;	}

			message.showMessage("Test Procedure","Step "+step+") Verify that DUT lamp is switched off and that correct"
					+ " DUT status is shown in all TBADs.");
			step++;
		}




		message.showMessage("Test Procedure","Step "+step+") Switch TBAD1 off.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to switch the DUT lamp on and"
				+ " off.");
		step++;


		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp switched on and off?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp is not switched on and off");

			return;}
	}

	/**
	 * IOP ls f_ lamp_v1_02.
	 */
	private  void IOP_LSF_Lamp_v1_02() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");
		int step=4;
		for(int i=1;i<=3;i++){

			message.showMessage("Test Procedure","Step "+step+") Verify if an AllJoyn connection has been"
					+ " established between the DUT and TBAD"+i+" (e.g.:"
					+ " DUT Lamp name will be displayed in TBAD"+i+""
					+ " screen). If not, establish an AllJoyn connection"
					+ " between the DUT and TBAD"+i+".");
			step++;

			message.showMessage("Test Procedure","Step "+step+") Verify that an AllJoyn connection has been"
					+ " established between the DUT and the rest of the"
					+ " TBADs and if not, establish the requested steps to"
					+ " get all TBADs AllJoyn connected to the DUT.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command TABD"+i+" to display DUT Lamp details.");
			step++;

			int resp=message.showQuestion("Pass/Fail Criteria","Are DUT lamp detail properties Dimmable,"
					+ " Color, VariableColorTemp and HasEffects"
					+ " displayed? Are the values obtained"
					+ " according DUT ICS?");
			if(resp!=0){//1==NO  null==(X)

				fail("DUT lamp detail properties Dimmable,"
						+ " Color, VariableColorTemp and HasEffects"
						+ " are displayed and the values obtained are not"
						+ " according DUT ICS");

				return;
			}


			resp=message.showQuestion("Pass/Fail Criteria","Are the values of the next LampDetails"
					+ " (Make, Model, Type, LampType, LampBaseType, LampBeamAngle, MinVoltage, MaxVoltage,"
					+ " Wattage, IncandescentEquivalent,\n MaxLumens, MinTemperature, MaxTermperature and LampID)"				 
					+ " Interface fields displayed according to"
					+ " Manufacturer specs? ");
			if(resp!=0){//1==NO  null==(X)

				fail("The values of the next LampDetails"
						+ " Interface fields are not displayed according to"
						+ " Manufacturer specs");

				return;}


		}


	}



	/**
	 * IOP ls f_ lamp_v1_03.
	 */
	private  void IOP_LSF_Lamp_v1_03() {

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network if"
				+ " they are not connected yet. (If"
				+ " ICSON_OnboardingInterface ICS value is true, use"
				+ " TBAD_O to onboard the DUT to the personal AP.");


		int i=1;

		message.showMessage("Test Procedure","Step 4) Verify if an AllJoyn connection has been"
				+ " established between the DUT and TBAD"+i+" (e.g.:"
				+ " DUT Lamp name will be displayed in TBAD"+i+""
				+ " screen). If not, establish an AllJoyn connection"
				+ " between the DUT and TBAD"+i+".");


		message.showMessage("Test Procedure","Step 5) Verify that an AllJoyn connection has been"
				+ " established between the DUT and the rest of the"
				+ " TBADs and if not, establish the requested steps to"
				+ " get all TBADs AllJoyn connected to the DUT.");


		message.showMessage("Test Procedure","Step 6) Operate all TBADs to display current value of Lamp"
				+ " color.");

		message.showMessage("Test Procedure","Step 7) If DUT supports ICSL_Dimmable, command TBAD1"
				+ " to turn DUT lamp brightness to its higher value.");


		message.showMessage("Test Procedure","Step 8) If DUT supports ICSL_ColorTemperature, command TBAD1 to turn DUT lamp Color"
				+ " Temperature to its higher value.");		

		message.showMessage("Test Procedure","Step 9) If DUT supports ICSL_Dimmable, operate TBAD1"
				+ " to turn DUT lamp saturation to its higher value.");		


		message.showMessage("Test Procedure","Step 10) Operate TBAD1 to modify Color value to its higher"
				+ " value.");		

		int resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp Color as expected by its"
				+ " Color value? Is current Color value displayed in all"
				+ " TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color is not as expected by its"
					+ " Color value or current Color value is not displayed in all"
					+ " TBADs");

			return;}

		message.showMessage("Test Procedure","Step 11) Operate TBAD1 to modify Color value to a medium"
				+ " value.");	

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp Color modified to a new color"
				+ " as expected by its Color value? Is current Color value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color is not modified to a new color"
					+ " as expected by its Color value or current Color value"
					+ " is not displayed in all TBADs");
			return;}
		message.showMessage("Test Procedure","Step 12) Operate TBAD2 to modify Color value to a low"
				+ " value.");	

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp Color modified to a new color"
				+ " as expected by its Color value?Is current Color value"
				+ " displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color is not modified to a new color"
					+ " as expected by its Color value or current Color value"
					+ " is not displayed in all TBADs.");
			return;}

		message.showMessage("Test Procedure","Step 13) Repeat this step several times with all TBADs"
				+ "setting Color value to a different value each time.");		
		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp Color modified each time to a"
				+ " new color as expected by its Color value? Is current"
				+ " Color value displayed in all TBADs?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color is not modified each time to a"
					+ " new color as expected by its Color value or current"
					+ " Color value is not displayed in all TBADs.");
			return;}
		message.showMessage("Test Procedure","Step 14) Note current Color value.");		

		message.showMessage("Test Procedure","Step 15) Switch DUT off and on, and perform the required"
				+ " steps to reconnect it to the AP and to the AllJoyn"
				+ " session.");		

		message.showMessage("Test Procedure","Step 16) Switch off TABD2.");		

		message.showMessage("Test Procedure","Step 17) Operate TBAD1 to modify Color value to a different"
				+ " value.");		
		resp=message.showQuestion("Pass/Fail Criteria","Does DUT lamp Color have the same value it had"
				+ " before being switched off and on? Is the value"
				+ " properly displayed in all TABDs (except TABD2 that"
				+ "is off)?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color has not the same value it had"
					+ " before being switched off and on or the value is not"
					+ " properly displayed in all TABDs (except TABD2 that"
					+ " is off)");
			return;}
		message.showMessage("Test Procedure","Step 18) Switch TABD2 on and operate it to perform an"
				+ " AllJoyn connection if necessary.");		

		resp=message.showQuestion("Pass/Fail Criteria","Is DUT lamp Color correctly updated in"
				+ " TABD2?");
		if(resp!=0){//1==NO  null==(X)

			fail("DUT lamp Color is not correctly updated in"
					+ " TABD2.");
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

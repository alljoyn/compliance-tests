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
package com.at4wireless.alljoyn.testcases.iop.config;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigIOP.
 */
public class ConfigIOP {
	
	/** The frame. */
	private  JFrame frame;
	 
 	/** The pass. */
 	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "ConfigIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);

	/**
	 * Instantiates a new config iop.
	 *
	 * @param testCase the test case
	 */
	public ConfigIOP(String testCase) {
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
		
		String testCase="IOP_Config-v1-04";
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
		if(testCase.equals("IOP_Config-v1-01")){
			IOP_Config_v1_01();
		
		}else if(testCase.equals("IOP_Config-v1-02")){
			IOP_Config_v1_02();
		
		}else if(testCase.equals("IOP_Config-v1-03")){
			IOP_Config_v1_03();
		
		}else if(testCase.equals("IOP_Config-v1-04")){
			IOP_Config_v1_04();
		
		}else if(testCase.equals("IOP_Config-v1-05")){
			IOP_Config_v1_05();
		
		}else if(testCase.equals("IOP_Config-v1-06")){
			IOP_Config_v1_06();
		}else if(testCase.equals("IOP_Config-v1-08")){
			IOP_Config_v1_08();
		
		}else if(testCase.equals("IOP_Config-v1-09")){
			IOP_Config_v1_09();
		
		}else {
			fail("TestCase not valid");
		}
		
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
	 * IOP config_v1_01.
	 */
	private  void IOP_Config_v1_01(){
		// TODO Auto-generated method 
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBAD1 are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_1.");
		
		message.showMessage("Test Procedure","Step 3) Connect the DUT and TBAD_1 to "
				+ "the AP network if they are not connected yet.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection"
				+ " between the DUT and TBAD_1 if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command TBAD_1 to display of DUT About"
				+ " Announcement list of object paths and service framework "
				+ "interfaces supported." );
		
		int included=message.showQuestion("Pass/Fail Criteria"," Verify that Config Object "
				+ "path (org.alljoyn.Config) is present in DUT About Announcement.");
						
		if(included!=0){//1==NO
			
			fail("Config Object path (org.alljoyn.Config) is not present in DUT About"
					+ "Announcement.");
			return;}
	}
	
	
	/**
	 * IOP config_v1_02.
	 */
	private  void IOP_Config_v1_02() {
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
	
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP network"
				+ " if they are not connected yet. If"
				+ " ICSON_OnboardingInterface ICS value is true, use "
				+ "TBAD_O to onboard the DUT to the personal AP.");
		
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection "
				+ "between the DUT and all the TBADs if is not established "
				+ "automatically.");
		
		message.showMessage("Test Procedure","Step 5) Command TBAD_A to display the "
				+ "values of the parameters ‘DeviceName’ and ‘DefaultLanguage’"
				+ " in the DUT About Announcement.");
		int step=6;
		for(int i=1;i<=2;i++){
			
			 testBed="TBAD"+i;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+ " to obtain "
					+ "the DUT configuration data (‘DeviceName’ and ‘DefaultLanguage’"
					+ " parameters) on the Config bus object using ‘GetConfigurations’"
					+ " method \n(check that the values are obtained using Configuration"
					+ " Service and not About Announcement).");
			step++;
			int included=message.showQuestion("Pass/Fail Criteria","Are the values of the parameters"
					+ " ‘DeviceName’ and ‘DefaultLanguage’ in the DUT About Announcement"
					+ " the same than the values obtained in step "+step+"?");
			
			if(included!=0){//1==NO
				
				fail("The values of the parameters 'DeviceName' and 'DefaultLanguage' "
						+ "in the DUT About Announcement are not the same than"
						+ "the values obtained in step 6");
				return;}
			
			
			}
			
		
		
	

	}

	

	/**
	 * IOP config_v1_03.
	 */
	private  void IOP_Config_v1_03() {
		
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on all TBADs of the Test Bed.");
		
	
	    message.showMessage("Test Procedure","Step 2) Switch on DUT.");
	    message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT "
	 		+ "to the AP network if they are not connected yet. If "
	 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	 		+ "to onboard the DUT to the personal AP.");
	    int step=4;
	    for(int i=1;i<=2;i++){
	    	
	    		testBed="TBAD"+i;
	    		message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
	    				+ " between the DUT and all the TBADs if is not established"
	    				+ " automatically.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+ testBed +" to obtain"
	    				+ " the DUT configuration ‘DeviceName’ parameter value on the"
	    				+ " Config bus object using GetConfigurations method."); 
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+ testBed +" to update"
	    				+ " ‘DeviceName’ parameter (change DeviceName to DeviceName"+i+""
	    				+ " value).");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to get the "
	    				+ " value of ‘DeviceName’ parameter using ‘GetConfigurations’ "
	    				+ " method of the Config interface.");
	    		step++;

	    		int included=message.showQuestion("Pass/Fail Criteria","Is ‘DeviceName’ parameter "
	    				+ "value DeviceName"+i+"?");

	    		if(included!=0){//1==NO

	    			fail("‘DeviceName’ parameter value is not DeviceName"+i+"");

	    			return;}

	    		message.showMessage("Test Procedure","Step "+step+") Command TBAD_A to display the"
	    				+ " values of the ‘DeviceName’ parameter in the DUT About "
	    				+ "Announcement.");
	    		step++;
//TODO Pregunta correcta??
	    		included=message.showQuestion("Pass/Fail Criteria", "DeviceName’ parameter"
	    				+ " value displayed in the About Announcement is DeviceName"+i+"?");

	    		if(included!=0){//1==NO

	    			fail("‘DeviceName’ parameter value displayed in the About"
	    					+ "Announcement is not DeviceName"+i+".");

	    			return;}

	    		message.showMessage("Test Procedure","Step "+step+") Switch off the DUT.");
	    		step++;

	    		message.showMessage("Test Procedure","Step "+step+") Switch on DUT");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Connect the TBADs and/or DUT "
	    				+ "to the AP network if they are not connected yet. If "
	    				+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	    				+ "to onboard the DUT to the personal AP.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
	    				+ " between the DUT and all the TBADs if is not established"
	    				+ " automatically.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command"+ testBed +" to obtain"
	    				+ " the DUT configuration ‘DeviceName’ parameter value on the"
	    				+ " Config bus object using GetConfigurations method."); 
	    		step++;

	    		included=message.showQuestion("Pass/Fail Criteria","Is ‘DeviceName’ parameter value"
	    				+ " DeviceName"+i+"?");

	    		if(included!=0){//1==NO

	    			fail("‘DeviceName’ parameter value is not DeviceName"+i+".");						
	    			return;}

	    		message.showMessage("Test Procedure","Step "+step+") Command TBAD_A to display "
	    				+ "the values of the ‘DeviceName’ parameter included in the"
	    				+ " DUT About Announcement.");
	    		step++;

	    		included=message.showQuestion("Pass/Fail Criteria","Is ‘DeviceName’ parameter value"
	    				+ " displayed in the About Announcement DeviceName"+i+"?");

	    		if(included!=0){//1==NO

	    			fail("‘DeviceName’ parameter value displayed in the About"
	    					+ " Announcement is not DeviceName"+i+".");						
	    			return;}

	    		
	    		
	    		included=message.showQuestion("Pass/Fail Criteria","‘DeviceName’ parameter value"
	    				+ " read in the Config interface is and in About Announcement"
	    				+ " is DeviceName"+i+" at the end of the step?");

	    		if(included!=0){//1==NO

	    			fail("‘DeviceName’ parameter value"
	    					+ " read in the Config interface is and in About Announcement"
	    					+ " is not DeviceName"+i+" at the end of the step.");						
	    			return;}
	    	

	    }//for
	}
	
	

	/**
	 * IOP config_v1_04.
	 */
	private  void IOP_Config_v1_04() {
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on all TBADs of the Test Bed.");
		
	
	    message.showMessage("Test Procedure","Step 2) Switch on DUT.");
	    message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT "
	 		+ "to the AP network if they are not connected yet. If "
	 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	 		+ "to onboard the DUT to the personal AP.");
	    int step=4;
	    for(int i=1;i<=2;i++){
	    	
	    		testBed="TBAD"+i;
	    			message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
	    					+ " between the DUT and all the TBADs if is not established"
	    					+ " automatically.");
	    			step++;
	    			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain "
	    					+ "the DUT configuration ‘DefaultLanguage’ parameter value "
	    					+ "on the Config bus object using GetConfigurations method.");
	    			step++;
	    			message.showMessage("Test Procedure","Step "+step+") Command TBAD_A to display the"
	    					+ " values of the ‘SupportedLanguages’ parameter in the DUT"
	    					+ " About Announcement.");
	    			step++;
	    			int included=message.showQuestion("Pass/Fail Criteria","Are supported languages"
	    					+ " in the About announcement according to "
	    					+ "IXITCO_SupportedLanguages?");

		    		if(included!=0){//1==NO

		    			fail("Supported languages in the About announcement are not"
		    					+ " according to IXITCO_SupportedLanguages ");						
		    			return;}
	    			
	    			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to update"
	    					+ " ‘DefaultLanguage’ parameter (change DefaultLanguage to "
	    					+ "any of the DUT supported languages obtained in step "+(step-1)+""
	    					+ " which is not the current value obtained in step "+(step-2)+").");
	    			int step7=step;
	    			step++;
	    			
	    			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to get the"
	    					+ " value of ‘DefaultLanguage’ parameter using "
	    					+ "‘GetConfigurations’ method of the Config interface.");
	    			step++;
	    			included=message.showQuestion("Pass/Fail Criteria","Is ‘DefaultLanguage’ "
	    					+ "parameter value the DefaultLanguage value updated "
	    					+ "in step "+step7+"?");
	    			
		    		if(included!=0){//1==NO

		    			fail("'DefaultLanguage' parameter value is not the DefaultLanguage"
		    					+ " value updated in step "+step7+".");						
		    			return;}
	    			
	    			message.showMessage("Test Procedure","Step "+step+") Command TBAD_A to display the"
	    					+ " value of the ‘DefaultLanguage’ parameter in the DUT "
	    					+ "About Announcement.");
	    			step++;
	    			included=message.showQuestion("Pass/Fail Criteria","Is ‘DefaultLanguage’"
	    					+ " parameter value displayed in the About Announcement"
	    					+ " the value updated in step "+step7+"?");

		    		if(included!=0){//1==NO

		    			fail("'DefaultLanguage' parameter value displayed in the"
		    					+ "About Announcement is not the DefaultLanguage"
		    					+ "value updated in step "+step7+".");						
		    			return;}
	    			
	    			message.showMessage("Test Procedure","Step "+step+") Switch off the DUT.");
	    			step++;
	    			//TODO
	    			
	    			 message.showMessage("Test Procedure","Step "+step+") Switch on DUT");
	    			 step++;
	    			    message.showMessage("Test Procedure","Step "+step+") Connect the TBADs and/or DUT "
	    			 		+ "to the AP network if they are not connected yet. If "
	    			 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	    			 		+ "to onboard the DUT to the personal AP.");
	    			    step++;
	    			    message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
		    					+ " between the DUT and all the TBADs if is not established"
		    					+ " automatically.");
	    			    step++;
		    			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain "
		    					+ "the DUT configuration ‘DefaultLanguage’ parameter value "
		    					+ "on the Config bus object using GetConfigurations method.");
		    			step++;
	    			included=message.showQuestion("Pass/Fail Criteria","Is ‘DefaultLanguage’ "
	    					+ "parameter value the DefaultLanguage value updated"
	    					+ " in step "+step7+"?");

		    		if(included!=0){//1==NO

		    			fail("'DefaultLanguage' parameter value is not the"
		    					+ " DefaultLanguage value updated in step "+step7+".");						
		    			return;}
	    			
	    			message.showMessage("Test Procedure","Step "+step+") Command TBAD_A to display "
	    					+ "the values of the ‘DefaultLanguage’ parameter included "
	    					+ "in the DUT About Announcement.");
	    			step++;
	    			included=message.showQuestion("Pass/Fail Criteria","Is ‘DefaultLanguage’ "
	    					+ "parameter value displayed in the About Announcement"
	    					+ " the value updated in step "+step7+"?");

		    		if(included!=0){//1==NO

		    			fail("'DefaultLanguage' parameter value displayed in the"
		    					+ " About Announcement is not the"
		    					+ " value updated in step "+step7+".");						
		    			return;}
		    		
		    		included=message.showQuestion("Pass/Fail Criteria","Is ‘DefaultLanguage’ "
		    				+ "parameter value read in the Config interface and "
		    				+ "in About Announcement at the end of the step the "
		    				+ "value updated in the sub-step "+step7+" for the"
		    				+ " corresponding "+testBed+"?");

		    		if(included!=0){//1==NO

		    			fail("'DefaultLanguage' parameter  value read in the Config "
		    				+ "interface is and in About Announcement at the end of "
		    				+ "the step is not  the value updated in the sub-step "+step7+" for the"
		    				+ " corresponding "+testBed+".");						
		    			return;}
	    	
	    	
	    }//for
	}
	
	
	/**
	 * IOP config_v1_05.
	 */
	private  void IOP_Config_v1_05() {
		// TODO Auto-generated method stub
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");

	    
	    message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT "
	 		+ "to the AP network if they are not connected yet. If "
	 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	 		+ "to onboard the DUT to the personal AP.");
	    int step=4;
	    for(int i=1;i<=2;i++){
	    	
	    		testBed="TBAD"+i;
	    		message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
	    				+ " between the DUT and all the TBADs if is not established"
	    				+ " automatically.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain "
	    				+ "the DUT configuration data (‘DeviceName’ and "
	    				+ "‘DefaultLanguage’ parameters) on the Config bus object"
	    				+ " using ‘GetConfigurations’ method.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Modify DUT configuration "
	    				+ "to be different from default factory configuration. "
	    				+ "For example modify ‘DeviceName’ to ‘InteropTestDeviceName’"
	    				+ " value.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to perform a"
	    				+ " ‘FactoryReset’ in the DUT using the Config interface.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain"
	    				+ " the DUT configuration data (‘DeviceName’ and "
	    				+ "‘DefaultLanguage’ parameters) on the Config \n bus"
	    				+ " object using ‘GetConfigurations’ method and verify "
	    				+ "that the DUT configuration has been set to the factory "
	    				+ "default values.");
	    		step++;
	    		int included=message.showQuestion("Pass/Fail Criteria","Is Configuration data "
	    				+ "(‘DeviceName’ and ‘DefaultLanguage’) "
	    				+ " the the DUT factory default configuration data?");

	    		if(included!=0){//1==NO

	    			fail("Configuration data "
	    				+ "(‘DeviceName’ and ‘DefaultLanguage’) read with every "
	    				+ "TBAD after invoking ‘FactoryReset’ Configuration method"
	    				+ " is not the DUT factory default configuration data");						
	    		
	    			return;
	    	}//if
	    }//for
	}
	

	/**
	 * IOP config_v1_06.
	 */
	private  void IOP_Config_v1_06() {
		// TODO Auto-generated method stub
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
	    
	    int step=3;
	    for(int i=1;i<=2;i++){
	    	
	    		testBed="TBAD"+i;
	    	    message.showMessage("Test Procedure","Step "+step+") Connect the TBADs and/or DUT "
	    		 		+ "to the AP network if they are not connected yet. If "
	    		 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
	    		 		+ "to onboard the DUT to the personal AP.");
	    	    step++;
	    		message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
	    				+ " between the DUT and all the TBADs if is not established"
	    				+ " automatically.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to modify "
	    				+ "the DUT DeviceName parameter with ‘MyTestDeviceName’ "
	    				+ "value using the Config interface.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to call Restart"
	    				+ " method in the DUT.");
	    		step++;
	    		
	    		int included=message.showQuestion("Pass/Fail Criteria","Is DUT restarted?");

	    		if(included!=0){//1==NO

	    			fail("DUT is not restarted");						
	    			return;}
	    		message.showMessage("Test Procedure","Step "+step+") "+testBed+" will lose the "
	    				+ "connection. Perform the required steps to re-establish"
	    				+ " AllJoyn connection between the DUT and "+testBed+".");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain "
	    				+ "the DUT configuration data (‘DeviceName’ and "
	    				+ "‘DefaultConfiguration’ parameters) using the Config"
	    				+ " interface.");
	    		step++;
	    		
	    		included=message.showQuestion("Pass/Fail Criteria","Has Value of ‘DeviceName’ "
	    				+ "parameter changed to ‘MyTestDeviceName’?");

	    		if(included!=0){//1==NO

	    			fail("Value of ‘DeviceName’ "
	    				+ "parameter has not changed to ‘MyTestDeviceName’");						
	    			return;}
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to modify the"
	    				+ " DUT ‘DeviceName’ parameter with the original ‘DeviceName’ "
	    				+ "value using the Config interface.");
	    		step++;
	    		message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to get DUT "
	    				+ "‘DeviceName’ using the Config interface.");
	    		step++;
	    		included=message.showQuestion("Pass/Fail Criteria","Has Value of ‘DeviceName’"
	    				+ " parameter changed to its original value?");

	    		if(included!=0){//1==NO

	    			fail("Value of 'DeviceName' parameter has not changed to"
	    					+ " its original value.");						
	    			return;
	    		
	    	}//if
	    }//for

	}


	
	
	

	/**
	 * IOP config_v1_08.
	 */
	private  void IOP_Config_v1_08() {
		// TODO Auto-generated method stub
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		int step=3;
		for(int i=1;i<=2;i++){
			if(pass){
				testBed="TBAD"+i;
				message.showMessage("Test Procedure","Step "+step+") Connect the TBADs and/or DUT "
				 		+ "to the AP network if they are not connected yet. If "
				 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
				 		+ "to onboard the DUT to the personal AP.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
						+ " between the DUT and all the TBADs if is not established"
						+ " automatically.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to modify"
						+ " the DUT DeviceName parameter with ’MyTestDeviceName’ value"
						+ " using the Config interface.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain the DUT"
						+ " ‘DeviceName’ parameter value.");
				step++;
				int included=message.showQuestion("Pass/Fail Criteria","Has value of ‘DeviceName’"
						+ " parameter changed to ’MyTestDeviceName’ value?");

	    		if(included!=0){//1==NO

	    			fail("Value of ‘DeviceName’ parameter has not"
	    					+ "changed to 'MyTestDeviceName' value");						
	    			return;}
				//The message is repeated?
				//message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain the DUT"
					//	+ " ‘DeviceName’ parameter value.");
				//step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to call the "
						+ "Config interface ‘ResetConfigurations’ method to reset "
						+ "‘DeviceName’ parameter.");
				step++;
				
				
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to obtain"
						+ " the DUT ‘DeviceName’ parameter value.");
				step++;
				
				included=message.showQuestion("Pass/Fail Criteria","Has Value of ‘DeviceName’ "
						+ "parameter changed to its factory value?");

	    		if(included!=0){//1==NO

	    			fail("Value of ‘DeviceName’ parameter has not"
	    					+ "changed to its factory value");						
	    			return;}
			}
		}
	}

	

	/**
	 * IOP config_v1_09.
	 */
	private  void IOP_Config_v1_09() {
		// TODO Auto-generated method stub
	    String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		int step=3;
		for(int i=1;i<=2;i++){
			
				testBed="TBAD"+i;
				message.showMessage("Test Procedure","Step "+step+") Connect the TBADs and/or DUT "
				 		+ "to the AP network if they are not connected yet. If "
				 		+ "ICSON_OnboardingInterface ICS value is true, use TBAD_O "
				 		+ "to onboard the DUT to the personal AP.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn connection"
						+ " between the DUT and "+testBed+" if is not established"
						+ " automatically.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to modify DUT"
						+ " passcode (calling the SetPasscode method on the Config bus "
						+ "object with the newPasscode parameter) to “111111”.");
				step++;
				int included=message.showQuestion("Pass/Fail Criteria","Is an error message "
						+ "displayed when modifying passcode?");

	    		if(included!=1){//1==NO

	    			fail("An error message has been displayed when modifying passcode.");						
	    			return;}
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to leave the"
						+ " session.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Clear "+testBed+" key store of "
						+ "authentication keys.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to join a "
						+ "session with the DUT application after receiving an "
						+ "About Announcement  and register an AuthListener with\n"
						+ " the AllJoyn framework that provides the new passcode "
						+ "(“111111”) when authentication is requested.");
				step++;
				included=message.showQuestion("Pass/Fail Criteria","Does " +testBed+" join a session"
						+ " with DUT using the passcode “111111”?");

	    		if(included!=0){//1==NO

	    			fail(""+testBed+" not joins a session"
						+ " with DUT using the passcode “111111”.");						
	    			return;}
	    		
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to modify the "
						+ "passcode (calling the ‘SetPasscode’ method on the Config "
						+ "bus object) with the newPasscode parameter set to default"
						+ " value “000000”.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to leave the"
						+ " session.");
				step++;
				message.showMessage("Test Procedure","Step "+step+")  Clear "+testBed+" key store"
						+ " of authentication keys.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to join a session"
						+ " with the DUT application after receiving an About "
						+ "Announcement and register an AuthListener with \nthe"
						+ " AllJoyn framework that provides the new passcode(“000000”) "
						+ "when authentication is requested.");
				step++;
				included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" join a "
						+ "session with DUT using the passcode “000000”?");

	    		if(included!=0){//1==NO

	    			fail(""+testBed+" not joins a "
						+ "session with DUT using the passcode “000000”.");						
	    			return;
	
			}//if
		}//for
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

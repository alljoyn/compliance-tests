package com.at4wireless.alljoyn.testcases.iop.onboarding;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

public class OnboardingIOP {

	private  JFrame frame;
	Boolean pass=true;
	protected  final String TAG = "OnboardingIOPTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	IOPMessage message=new IOPMessage(logger);

	public OnboardingIOP(String testCase) {
		frame=new JFrame();

		try{
			runTestCase(testCase);
		}catch(Exception e){


			fail("Exception: "+e.toString());

		}
	}



	public  void main(String arg[]){

		String testCase="IOP_Onboarding-v1-07";
		try{
			runTestCase(testCase);
		}catch(Exception e){


			fail("Exception: "+e.toString());

		}
	}






	public  void runTestCase(String testCase) throws Exception{
		frame=new JFrame();
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



	private  void IOP_Onboarding_v1_01() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBAD1 are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD_A and TBAD1.");
		message.showMessage("Test Procedure","Step 3) Connect the TBAD_A to the AP "
				+ "network if it is not connected yet.");
		message.showMessage("Test Procedure","Step 4) Verify if the DUT is found in "
				+ "the personal AP. If it is not connected, command TBAD1 "
				+ "to scan for Wi-Fi networks looking for the Soft AP of the "
				+ "DUT and command TBAD1 to connect to the soft AP.");
		message.showMessage("Test Procedure","Step 5) Command TBAD_A to display "
				+ "the DUT About Announcement list of object paths and "
				+ "service framework interfaces supported.");
		int included=message.showQuestion("Pass/Fail Criteria","Verify that Onboarding "
				+ "Object path (’Onboarding’) is present in DUT about announcement?");

		if(included!=0){//1==NO

			fail("Verify that Onboarding "
					+ "Object path (’Onboarding’) is not present in DUT about announcement.");						
			return;}

	}




	private  void IOP_Onboarding_v1_02() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","TBAD1 is switched off. \n"
				+ "DUT has already been onboarded and connected to the personal AP.");
		message.showMessage("Test Procedure","Step 1) Switch on TBAD_O and TBAD1.");
		message.showMessage("Test Procedure","Step 2) Connect TBAD1 to the AP network "
				+ "if it is not connected yet.");
		message.showMessage("Test Procedure","Step 3) Establish an AllJoyn connection "
				+ "between the DUT and TBAD_O if is not established automatically. "
				+ "Command TBAD_O to onboard the DUT if required.");
		message.showMessage("Test Procedure","Step 4) Command TBAD1 to offboard the DUT.");
		int included=message.showQuestion("Pass/Fail Criteria","Is DUT offboarded?");

		if(included!=0){//1==NO

			fail("DUT is not offboarded.");						
			return;}
	}



	private  void IOP_Onboarding_v1_03() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
			int included=message.showQuestion("Pass/Fail Criteria","Is DUT onboarded?");

			if(included!=0){//1==NO

				fail("DUT is not onboarded.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to establish "
					+ "an AllJoyn connection between the DUT and "+testBed+".");
			step++;
			included=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection  "
					+ "established between "+testBed+" and the DUT?");

			if(included!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and "
						+ "the DUT.");						
				return;}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to "
					+ "offboard the DUT.");
			step++;

		}//for

	}



	private  void IOP_Onboarding_v1_04() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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

			int included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an "
					+ "error during the onboarding process, after call to"
					+ " ‘ConfigWiFi’ method, indicating that authentication failed?");

			if(included!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "error during the onboarding process, after call to"
						+ " ‘ConfigWiFi’ method, indicating that authentication failed.");						
				return;}

			message.showMessage("Test Procedure","Step "+step+" e) Command "+testBed+" to onboard the "
					+ "DUT by calling the ‘Connect’ method on the DUT Onboarding "
					+ "bus object.");

			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an "
					+ "error after call to ‘Connect’ method indicating that "
					+ "authentication failed?");

			if(included!=0){//1==NO

				fail(""+testBed+" not receives an "
						+ "error after call to ‘Connect’ method indicating that "
						+ "authentication failed.");						
				return;}
			step++;

		}//for
	}



	private  void IOP_Onboarding_v1_05() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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

			int included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error "
					+ "when trying to onboard the DUT?");

			if(included!=0){//1==NO

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
			included=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established "
					+ "between "+testBed+" and the DUT?");

			if(included!=0){//1==NO

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

			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error when trying to "
					+ "onboard the DUT?");

			if(included!=0){//1==NO

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
			included=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established between "
					+ ""+testBed+" and the DUT?");

			if(included!=0){//1==NO

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

			included=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" receive an error when "
					+ "trying to onboard the DUT?");

			if(included!=0){//1==NO

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
			included=message.showQuestion("Pass/Fail Criteria","Is AllJoyn Connection established "
					+ "between "+testBed+" and the DUT?");

			if(included!=0){//1==NO

				fail("AllJoyn Connection is not established between "+testBed+" and the DUT.");						
				return;}

			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to offboard the DUT.");
			step++;


		}//for
	}



	private  void IOP_Onboarding_v1_06() {
		// TODO Auto-generated method stub
		String testBed="TBAD1";

		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
			int included=message.showQuestion("Pass/Fail Criteria","Does DUT provide a valid list "
					+ "of scanned networks?");

			if(included!=0){//1==NO

				fail("DUT not provides a valid list of scanned networks.");						
				return;}
		}//for
	}





	private  void IOP_Onboarding_v1_07() {
		// TODO Auto-generated method stub


		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on TBAD1.");
		int step=3;

		message.showMessage("Test Procedure","Step "+step+") Connect TBAD1 to "
				+ "the AP network if it is not connected yet.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") After DUT has been switched on, "
				+ "verify if it is found in the personal AP. If so, offboard the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command TBAD1 to scan for "
				+ "Wi-Fi networks looking for the Soft AP of the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Once the soft AP is found "
				+ "command TBAD1 to connect to the soft AP.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Operate TBAD1 to join a "
				+ "session with the DUT application after receiving an About "
				+ "Announcement and to register an AuthListener with DUT "
				+ "passcode (default value ”000000”).");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command TBAD1 to call the "
				+ "‘SetPasscode’ method on the Config bus object with the "
				+ "‘newPasscode’ parameter set to value “123456”.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command TBAD1 to leave "
				+ "the session and to clear the key store.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Switch TBAD2 on.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Connect TBAD2 to the AP network "
				+ "if it is not connected yet.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.");
		step++;
		message.showMessage("Test Procedure","Step "+step+") Once the soft AP is found command "
				+ "TBAD2 to connect to the soft AP.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Operate TBAD2 to join a session with "
				+ "the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (value ”123456”).");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP. ");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.");
		step++;

		int included=message.showQuestion("Pass/Fail Criteria","Is DUT onboarded properly?");

		if(included!=0){//1==NO

			fail("DUT is not onboarded properly.");						
			return;}


		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to establish an AllJoyn "
				+ "connection between the DUT and TBAD2.");
		step++;

		included=message.showQuestion("Pass/Fail Criteria","Is AllJoyn connection established "
				+ "between DUT and TBAD2?");

		if(included!=0){//1==NO

			fail("AllJoyn connection is not established between DUT and TBAD2.");						
			return;}

		message.showMessage("Test Procedure","Step "+step+") Command TBAD2 to offboard the DUT.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Switch off and on TBAD1 and perform "
				+ "required actions to establish an AllJoyn connection with the DUT "
				+ "(using value ”123456” for the passcode).");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command TBAD1 to call the "
				+ "‘SetPasscode’ method on the Config bus object with the ‘newPasscode’ "
				+ "parameter set to value “000000”.");
		step++;




		message.showMessage("Test Procedure","Step "+step+") Switch TBAD3 on.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Connect TBAD3 to the AP network "
				+ "if it is not connected yet.");
		step++;

		message.showMessage("Test Procedure","Step "+step+") Command TBAD3 to onboard DUT using "
				+ "default passcode value ”000000”:");
		message.showMessage("Test Procedure","Step "+step+" a) Command TBAD3 to scan for Wi-Fi "
				+ "networks looking for the Soft AP of the DUT.");
		message.showMessage("Test Procedure","Step "+step+" b) Once the soft AP is found command "
				+ "TBAD3 to connect to the soft AP.");
		message.showMessage("Test Procedure","Step "+step+" c) Operate TBAD3 to join a session "
				+ "with the DUT application after receiving an About Announcement and to "
				+ "register an AuthListener with DUT passcode (”000000”).");
		message.showMessage("Test Procedure","Step "+step+" d) Command TBAD3 to Configure DUT WiFi "
				+ "parameters by calling the ‘ConfigWiFi’ method on the Onboarding bus "
				+ "object with the SSID, passphrase, and authType for the personal AP.");
		message.showMessage("Test Procedure","Step "+step+" e) Command TBAD3 to onboard the DUT "
				+ "calling the ‘Connect’ method on the DUT Onboarding bus object.");
		included=message.showQuestion("Pass/Fail Criteria","Does TBAD3 onboard DUT to the personal AP?");

		if(included!=0){//1==NO

			fail("TBAD3 not onboards DUT to the personal AP.");						
			return;}
	}




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




	private  void fail(String msg) {


		logger.error(msg);
		pass=false;


	}



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

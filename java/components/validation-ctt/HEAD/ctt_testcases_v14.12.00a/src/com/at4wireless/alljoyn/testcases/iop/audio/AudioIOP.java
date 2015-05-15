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
package com.at4wireless.alljoyn.testcases.iop.audio;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;


/**
 * The Class AudioIOP.
 */
public class AudioIOP {
	
	
	 
 	/** The pass. */
 	Boolean pass=true;
 	Boolean inconc=false;
 Map<String, List<String>> goldenUnits;
	 
	
	 
	 Boolean ICSON_OnboardingServiceFramework=false;
	 
	 String name=null;
	
	/** The tag. */
	protected  final String TAG = "AudioIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	/**
	 * Instantiates a new audio iop.
	 *
	 * @param testCase the test case
	 */
	public AudioIOP(String testCase, Map<String, List<String>> goldenUnits, boolean iCSON_OnboardingServiceFramework) {

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
		
		String testCase="IOP_AudioSource-v1-05";
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
		if(testCase.equals("IOP_AudioSink-v1-01")){
			IOP_AudioSink_v1_01();
		}else if(testCase.equals("IOP_AudioSink-v1-02")){
			IOP_AudioSink_v1_02();
		}else if(testCase.equals("IOP_AudioSink-v1-03")){
			IOP_AudioSink_v1_03();
		}else if(testCase.equals("IOP_AudioSink-v1-04")){
			IOP_AudioSink_v1_04();
		}else if(testCase.equals("IOP_AudioSink-v1-05")){
			IOP_AudioSink_v1_05();
		}else if(testCase.equals("IOP_AudioSink-v1-06")){
			IOP_AudioSink_v1_06();
		}else if(testCase.equals("IOP_AudioSink-v1-07")){
			IOP_AudioSink_v1_07();
		}else if(testCase.equals("IOP_AudioSource-v1-01")){
			IOP_AudioSource_v1_01();
		}else if(testCase.equals("IOP_AudioSource-v1-02")){
			IOP_AudioSource_v1_02();
		}else if(testCase.equals("IOP_AudioSource-v1-03")){
			IOP_AudioSource_v1_03();
		}else if(testCase.equals("IOP_AudioSource-v1-04")){
			IOP_AudioSource_v1_04();
		}else if(testCase.equals("IOP_AudioSource-v1-05")){
			IOP_AudioSource_v1_05();
		}else if(testCase.equals("IOP_AudioSource-v1-06")){
			IOP_AudioSource_v1_06();
		}else if(testCase.equals("IOP_AudioSource-v1-07")){
			IOP_AudioSource_v1_07();
		}else {
			fail("TestCase not valid");
		}
		
	}



	/**
	 * IOP audio sink_v1_01.
	 */
	private  void IOP_AudioSink_v1_01() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		
		
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){

			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}



			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to display DUT "
					+ "stream media types supported: audio/x-raw (mandatory), audio/xalac, "
					+ "image/jpeg and/or application/x-metadata.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Are supported DUT media types "
					+ "displayed as specified in ICS?");
			if(response!=0){//1==NO

				fail("Supported DUT media types are not displayed as specified in ICS.");						
				return;
			}


			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to display "
					+ "audio/x-raw parameters values.");
			message.showMessage("Test Procedure","Step "+step+" a) Number of channels (Support "
					+ "for 1 and 2 channels is mandatory).");
			message.showMessage("Test Procedure","Step "+step+" b) Sample formats (Support for "
					+ "‘s16le’ is mandatory).");
			message.showMessage("Test Procedure","Step "+step+" c) Sample rate. (Support for "
					+ "44100 and 48000 sample rates is mandatory).");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Are DUT Audio/x-raw parameters "
					+ "displayed according to DUT specifications?");
			if(response!=0){//1==NO

				fail("Supported DUT media types are not displayed as specified in ICS.");						
				return;
			}


			message.showMessage("Test Procedure","Step "+step+") If DUT supports ICSAU_AudioXalac, "
					+ "command "+testBed+" to display audio/x-alac parameters values.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","If DUT supports ICSAU_AudioXalac, "
					+ "Are DUT Audio/x-alac parameters displayed according to DUT "
					+ "specifications?");
			if(response!=0){//1==NO

				fail("If DUT supports ICSAU_AudioXalac, DUT Audio/x-alac parameters "
						+ "are not displayed according to DUT specifications.");						
				return;
			}


		}
	}
	


	/**
	 * IOP audio sink_v1_02.
	 */
	private  void IOP_AudioSink_v1_02() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to "
					+ "discover nearby Audio Sinks");
			step++;
			
			int response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" find DUT "
					+ "Audio Sink service?");
			if(response!=0){//1==NO
				fail(""+testBed+" not finds DUT Audio Sink service.");						
				return;}
			

			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to display "
					+ "DUT stream media types supported.");
			step++;
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to stream "
					+ "audio (PCM data) on the DUT performing following steps:");
			message.showMessage("Test Procedure","Step "+step+" a) If available at "+testBed+" "
					+ "configuration for AllJoyn Audio service, select the following "
					+ "configuration: \n i. Media Type: audio/x-raw \n ii. Channels: 1 \n "
					+ "iii. Format: s16le \n iv. Rate: 44100");
			message.showMessage("Test Procedure","Step "+step+" b) Select audio stream to be "
					+ "sent (according to Preconditions 5).");
			message.showMessage("Test Procedure","Step "+step+" c) Select "+testBed+" Play option "
					+ "(or equivalent one to call DUT Audio Sink service connect "
					+ "method).");
			
			response=message.showQuestion("Pass/Fail Criteria","Did Audio stream send by "+testBed+" "
					+ "sounds at DUT speaker and it is played seamlessly and at the "
					+ "right speed, for the whole duration of the stream?");
			if(response!=0){//1==NO
				fail("Audio stream sent by "+testBed+" not sounds at DUT speaker or it is not "
						+ "played seamlessly and at the right speed, for the whole "
						+ "duration of the stream.");						
				return;}
			step++;
		}
	}
	




	/**
	 * IOP audio sink_v1_03.
	 */
	private  void IOP_AudioSink_v1_03() {
	
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between the "
				+ "DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			
			
			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to discover "
					+ "nearby Audio Sinks.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" find DUT "
					+ "Audio Sink service?");
			if(response!=0){//1==NO
				fail(""+testBed+" not finds DUT Audio Sink service.");						
				return;	}
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to select "
					+ "an audio stream to be played, according to Preconditions 5, and "
					+ "command "+testBed+" to play the audio on the DUT \n and on all "
					+ "the category 6.2 AllJoyn devices of the Test Bed (by calling "
					+ "Audio Sink service ‘Connect’ method).");
			step++;
			
			response=message.showQuestion("Pass/Fail Criteria","Did Audio stream send by "+testBed+" "
					+ "plays on the DUT speaker synchronized with the other "
					+ "category 6.2 Golden Units?");
			if(response!=0){//1==NO
				fail("Audio stream sent by "+testBed+" not plays on the DUT speaker synchronized "
						+ "with the other category 6.2 Golden Units.");						
				return;}
			
		}
		
	}

	


	/**
	 * IOP audio sink_v1_04.
	 */
	private  void IOP_AudioSink_v1_04() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to discover "
					+ "nearby Audio Sinks.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to select an audio "
					+ "stream to be played, according to Preconditions 5, and command "+testBed+" "
					+ "to play the audio on the DUT.");
			step++;
					
			message.showMessage("Test Procedure","Step "+step+") Waits for 30 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to pause the audio "
					+ "stream playback on the DUT.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Does ongoing audio stream pause "
					+ "at DUT?");
			if(response!=0){//1==NO
				fail("Ongoing audio stream not pauses at DUT.");						
				return;}
			
			
			message.showMessage("Test Procedure","Step "+step+") Waits for 30 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to resume the "
					+ "audio stream playback on the DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Does Audio stream resume playing "
					+ "at DUT?");
			if(response!=0){//1==NO
				fail("Audio stream not resumes playing at DUT.");						
				return;}
			
			
			message.showMessage("Test Procedure","Step "+step+") Waits for 60 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to pause the "
					+ "audio stream playback on the DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Does Audio stream pause at DUT?");
			if(response!=0){//1==NO
				fail("Audio stream not pauses at DUT.");						
				return;}
			
			
			message.showMessage("Test Procedure","Step "+step+") Waits for 30 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to stop (flush) "
					+ "the audio stream playback on the DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Does "+testBed+" display any "
					+ "indication that the Audio stream has stopped?");
			if(response!=0){//1==NO
				fail(""+testBed+" not displays any indication that the Audio stream has stopped.");						
				return;	}
			
			
			
		}
	}

	


	/**
	 * IOP audio sink_v1_05.
	 */
	private  void IOP_AudioSink_v1_05() {
	
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to discover "
					+ "nearby Audio Sinks.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to select an audio "
					+ "stream to be played, according to Preconditions 5, and command "+testBed+" "
					+ "to play the audio on the DUT.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Waits for 60 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to stop the audio "
					+ "stream playback on the DUT.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Does ongoing audio stream stops at DUT?");
			if(response!=0){//1==NO
				fail("Ongoing audio stream not stops at DUT.");						
				return;}
			
			
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to select again "
					+ "step 6 audio stream to be played and command "+testBed+" to play the "
					+ "audio on the DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Does Audio stream start playing "
					+ "at DUT from the beginning of the audio stream?");
			if(response!=0){//1==NO
				fail("Audio stream not starts playing at DUT from the beginning of the "
						+ "audio stream.");						
				return;}
			
			
			
			message.showMessage("Test Procedure","Step "+step+") Waits for 60 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to stop (flush) "
					+ "the audio stream playback on the DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Does Audio stream stop at DUT?");
			if(response!=0){//1==NO
				fail("Audio stream not stops at DUT.");						
				return;}

		}//for
			
			
	}

	


	/**
	 * IOP audio sink_v1_06.
	 */
	private  void IOP_AudioSink_v1_06() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			category="Category 6.1 AllJoyn Device (Audio Source)";
			testBed=getGoldenUnitName(category);

			if(testBed==null){

				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;

			}
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to discover "
					+ "nearby Audio Sinks.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Operate "+testBed+" to select an audio "
					+ "stream to be played, according to Preconditions 5, and command "+testBed+" "
					+ "to play the audio on the DUT.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to mute the DUT "
					+ "(by modifying Mute property).");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Is Audio stream muted at "
					+ ""+testBed+"?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted at "+testBed+".");						
				return;}
			
			
			message.showMessage("Test Procedure","Step "+step+") Wait for 10 s.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+" Command "+testBed+" to unmute the "
					+ "DUT.");
			step++;
			response=message.showQuestion("Pass/Fail Criteria","Is audio stream unmuted "
					+ "at "+testBed+"?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted at "+testBed+".");						
				return;}
			
		}
	}
	


	/**
	 * IOP audio sink_v1_07.
	 */
	private  void IOP_AudioSink_v1_07() {
		
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or category 6.1 Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		category="Category 6.1 AllJoyn Device (Audio Source)";
		String testBed = getGoldenUnitName(category);

		if(testBed==null){

			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;

		}
		
		category="Category 6.1 AllJoyn Device (Audio Source)";
		String TBAD2 = getGoldenUnitName(category);

		if(TBAD2==null){

			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;

		}
		
		
		message.showMessage("Test Procedure","Step 5) Command "+testBed+" and "+TBAD2+" to discover "
				+ "nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream "
				+ "to be played, according to Preconditions 5, and command "+testBed+" to play "
				+ "the audio on the DUT.");
		message.showMessage("Test Procedure","Step 7) If Stream is muted, Command "+testBed+" to "
				+ "unmute the stream");
		message.showMessage("Test Procedure","Step 8) Command "+testBed+" to set stream to its "
				+ "higher volume value");
		int response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume "
				+ "increased (if it was not previously set to its maximum value)?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not increased (if it was not previously set to its maximum value).");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 10) Command "+TBAD2+" to set stream to its "
				+ "lower volume value.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased "
		 		+ "to DUT minimum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to DUT minimum value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 11) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 12) Command "+TBAD2+" to set stream to a "
				+ "medium volume value (according to its volume range).");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume increased "
		 		+ "to DUT medium value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not increased to a DUT medium value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 14) Command "+testBed+" to set stream to its "
				+ "lower volume value.");
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume decreased "
				+ "to DUT minimum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to DUT minimum value.");						
				return;}
			
		
		
		
		message.showMessage("Test Procedure","Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 16) Command "+TBAD2+" to increase gradually volume "
				+ "value up to the maximum value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume gradually "
				+ "increased up to DUT maximum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not gradually increased up to DUT "
						+ "maximum value.");						
				return;}
			
			
			
		message.showMessage("Test Procedure","Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 18) Command "+testBed+" to set stream to a medium "
				+ "volume value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume  decreased "
				+ "to a DUT medium value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to a DUT medium value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 20) Command "+testBed+" to mute the DUT.");
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream muted?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 22) Command "+testBed+" to unmute the DUT.");
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume unmuted "
				+ "and DUT volume is approximately the same than in step 18?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not unmuted or DUT volume is not approximately "
					+ "the same than in step 18.");	
			return;	
		}
		
		
		
	}
	
	/**
	 * IOP audio source_v1_01.
	 */
	private  void IOP_AudioSource_v1_01() {
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}
		
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically");
		message.showMessage("Test Procedure","Step 5) Command DUT to display sequentially each "
				+ "Golden Unit stream media types supported: audio/x-raw (mandatory), "
				+ "audio/x-alac, image/jpeg and/or application/x-metadata.");
		int response=message.showQuestion("Pass/Fail Criteria","Does DUT display Golden Units media "
				+ "types correctly?");
		if(response!=0){//1==NO
			fail("DUT not displays Golden Units media types correctly.");						
			return;}
		
		message.showMessage("Test Procedure","Step 6) Command DUT to display each Golden Unit audio/x-raw "
				+ "parameters values: Number of channels, Sample formats and "
				+ "sample rate.");
		response=message.showQuestion("Pass/Fail Criteria","Does DUT display the Golden Units parameters "
				+ "of the media types according to Golden Units specifications?");
		if(response!=0){//1==NO
			fail("DUT not displays the Golden Units parameters of the media types according "
					+ "to Golden Units specifications");						
			return;}
		
	}

	


	/**
	 * IOP audio source_v1_02.
	 */
	private  void IOP_AudioSource_v1_02() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		int step=5;
		for(int i=1;i<=3;i++){
			category="Category 6.2 AllJoyn Device (Audio Sink)";
			testBed=getGoldenUnitName(category);
			if(testBed==null){
				fail("No "+category+" Golden Unit.");
				inconc=true;
				return;
			}
			
			
			
			message.showMessage("Test Procedure","Step "+step+") Command DUT to discover "
					+ "nearby Audio Sinks.");
			step++;
			int response=message.showQuestion("Pass/Fail Criteria","Does DUT find "+testBed+" "
					+ "Audio Sink service?");
			if(response!=0){//1==NO
				fail("DUT not finds "+testBed+" Audio Sink service.");						
				return;}
			

			message.showMessage("Test Procedure","Step "+step+") Command DUT to display "
					+ ""+testBed+" stream media types supported.");
			step++;
			
			message.showMessage("Test Procedure","Step "+step+") Command DUT to stream "
					+ "audio (PCM data) on "+testBed+" performing following steps:");
			message.showMessage("Test Procedure","Step "+step+" a) If DUT allows media type "
					+ "configuration of the AllJoyn Audio service, select one of "
					+ "the configurations supported by "+testBed+" (preferable other "
					+ "than audio/x-raw).");
			message.showMessage("Test Procedure","Step "+step+" b) Select audio stream to "
					+ "be sent, according to Preconditions 5.");
			message.showMessage("Test Procedure","Step "+step+" c) Select on the DUT "+testBed+" "
					+ "Play option (or equivalent one to call "+testBed+" Audio Sink "
					+ "service 'Connect’ method).");
			step++;
			
			response=message.showQuestion("Pass/Fail Criteria","Did audio stream send by DUT "
					+ "sounds at "+testBed+" speaker and it is played seamlessly and at "
					+ "the right speed, for the whole duration of the stream?");
			if(response!=0){//1==NO
				fail("Audio stream sent by DUT sounds at "+testBed+" speaker and it is not "
						+ "played seamlessly and at the right speed, for the whole "
						+ "duration of the stream.");						
				return;}
			
			
			
			message.showMessage("Test Procedure","Step "+step+") Command DUT to flush the "
					+ "audio stream.");
			step++;
			
			
			
		}
	}

	
	


	/**
	 * IOP audio source_v1_03.
	 */
	private  void IOP_AudioSource_v1_03() {
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String testBed = getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		int response=message.showQuestion("Pass/Fail Criteria","Does DUT find "+testBed+" Audio "
				+ "Sink service?");
		if(response!=0){//1==NO
			fail("DUT not finds "+"+testBed+"+" Audio Sink service.");						
			return;}
		
		
		
		
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream "
				+ "to be played, according to Preconditions 5, and command DUT to play \n the "
				+ "audio on all the category 6.2 AllJoyn devices of the Test Bed "
				+ "(by calling Audio Sink service ‘Connect’ method).");
		
		response=message.showQuestion("Pass/Fail Criteria","Did audio stream send by DUT plays "
				+ "synchronized in all category 6.2 Golden Units?");
		if(response!=0){//1==NO
			fail("Audio stream sent by DUT not plays synchronized in all category "
					+ "6.2 Golden Units.");						
			return;}
		
	}
	
	


	/**
	 * IOP audio source_v1_04.
	 */
	private  void IOP_AudioSource_v1_04() {
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String testBed = getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream to "
				+ "be played, according to Preconditions 5, and play it on "+testBed+".");
		int response=message.showQuestion("Pass/Fail Criteria","Does audio stream play on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Audio stream not plays on "+testBed+".");						
			return;}
		
		message.showMessage("Test Procedure","Step 7) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 8) Operate DUT to pause the audio stream "
				+ "playback on "+testBed+".");
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream pause on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not pauses on "+testBed+".");						
			return;}
		
		message.showMessage("Test Procedure","Step 9) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 10) Operate DUT to resume the audio stream "
				+ "playback on "+testBed+".");
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream resume playing "
				+ "on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Audio stream not resumes playing on "+testBed+".");						
			return;}
		
		
		
		message.showMessage("Test Procedure","Step 11) Waits for 20 s.");
		message.showMessage("Test Procedure","Step 12) Operate DUT to stop (flush) the audio "
				+ "stream playback on "+testBed+".");
		message.showMessage("Test Procedure","Step 13) Waits for 10 s.");
		message.showMessage("Test Procedure","Step 14) Operate DUT to select an audio stream (long"
				+ " enough for the duration of the test) and play ii on all category 6.2 "
				+ " Golden Units in the Test Bed.");
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream play on all "
				+ "category 6.2 Golden Units?");
		if(response!=0){//1==NO
			fail("Audio stream not plays on all category 6.2 Golden Units.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 15) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 16) Operate DUT to pause the audio stream "
				+ "playback.");
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream pause "
				+ "on all category 6.2 Golden Units?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not pauses on all category 6.2 Golden Units.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 17) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 18) Operate DUT to resume the audio stream "
				+ "playback on the DUT.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream resume "
				+ "on all category 6.2 Golden Units?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not resumes on all category 6.2 Golden Units.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 19) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 20) Operate DUT to stop (flush) the audio stream playback.");

		
	}
	
	
	/**
	 * IOP audio source_v1_05.
	 */
	private  void IOP_AudioSource_v1_05() {
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String testBed = getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream to "
				+ "be played, according to Precondition 5, and command DUT to play the audio "
				+ "on "+testBed+".");
		message.showMessage("Test Procedure","Step 7) Waits for 60 s.");
		message.showMessage("Test Procedure","Step 8) Operate DUT to stop (flush) the audio stream "
				+ "playback on the DUT.");
		int response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream stop "
				+ "on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not stops on "+testBed+".");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 9) Operate DUT to select again step 6 audio "
				+ "stream and play it on all category 6.2 Golden Units.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream start playing "
				+ "all category 6.2 Golden Units from the beginning of the audio stream?");
		if(response!=0){//1==NO
			fail("Audio stream not starts playing all category 6.2 Golden Units from "
					+ "the beginning of the audio stream");						
			return;}

		
		message.showMessage("Test Procedure","Step 10) Waits for 60 s.");
		message.showMessage("Test Procedure","Step 11) Operate DUT to stop (flush) the audio "
				+ "stream playback.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream stops on all "
				+ "category 6.2 Golden Units?");
		if(response!=0){//1==NO
			fail("Audio stream not stop on all category 6.2 Golden Units");						
			return;}

	}
	
	
	/**
	 * IOP audio source_v1_06.
	 */
	private  void IOP_AudioSource_v1_06() {
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String testBed = getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream to be"
				+ " played, according to section 2.1, and command"
				+ " DUT to play the audio on "+testBed+".");
		message.showMessage("Test Procedure","Step 7) Command DUT to mute "+testBed+" (by modifying Mute"
				+ " property).");
		int response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Audio stream is not muted on "+testBed+".");						
			return;}
		message.showMessage("Test Procedure","Step 8) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 9) Command DUT to unmute the "+testBed+".");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream unmuted on "+testBed+"?");
		if(response!=0){//1==NO
			fail("Audio stream is not unmuted on "+testBed+".");						
			return;}
		
		message.showMessage("Test Procedure","Step 10) Operate DUT to select an audio stream (long"
				+ " enough for the duration of the test) to be played and"
				+ " command DUT to play the audio on all category 6.2"
				+ " Golden Units.");
		message.showMessage("Test Procedure","Step 11) Command DUT to mute the audio stream on all"
				+ " category 6.2 Golden Units (by modifying Mute property).");
		
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on all category 6.2"
		 		+ " Golden Units?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted on all category 6.2"
						+ " Golden Units.");						
				return;}
			
		message.showMessage("Test Procedure","Step 12) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 13) Command DUT to unmute the audio stream on all"
				+ " category 6.2 Golden Units.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream unmuted on all category 6.2"
			 		+ " Golden Units?");
				if(response!=0){//1==NO
					fail("Audio stream is not unmuted on all category 6.2"
							+ " Golden Units.");	
					return;	}
		
	}
	

	/**
	 * IOP audio source_v1_07.
	 */
	private  void IOP_AudioSource_v1_07() {
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		String category="Category 3 AllJoyn Device (Onboarding)";
		String TBAD_O = null;
		if(ICSON_OnboardingServiceFramework){
			TBAD_O=getGoldenUnitName(category);
		 if(TBAD_O==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}
		
		if(ICSON_OnboardingServiceFramework){
		
		message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use "+TBAD_O+" to "
				+ "onboard the DUT and/or the Golden Units to the personal AP.");
		}else{
			message.showMessage("Test Procedure","Step 3) Connect the Golden Units and/or DUT to the AP "
					+ "network if they are not connected yet.");		
		}message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the Golden Units if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String testBed = getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		
		message.showMessage("Test Procedure","Step 6) Operate "+testBed+" to select an audio stream to be"
				+ " played, according to section 2.1, and command"
				+ " DUT to play the audio on "+testBed+".");
		message.showMessage("Test Procedure","Step 7) If Stream is muted, Command DUT to unmute the"
				+ " stream.");
		message.showMessage("Test Procedure","Step 8) Command DUT to set stream to its higher volume"
				+ " value.");
		int response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume increased on"
				+ " "+testBed+" (if it was not previously set to its maximum"
				+ " value)?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not increased on"
					+ " "+testBed+" (if it was not previously set to its maximum"
					+ " value).");						
			return;}
		message.showMessage("Test Procedure","Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 10) Command DUT to set stream to a medium volume"
				+ " value.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased to a"
		 		+ " medium "+testBed+" value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased to a"
					+ " medium "+testBed+" value.");						
			return;}
		message.showMessage("Test Procedure","Step 11) Wait for 10 s.");
		
		
		category="Category 6.2 AllJoyn Device (Audio Sink)";
		String TBAD2 = getGoldenUnitName(category);
		if(TBAD2==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
		}
		message.showMessage("Test Procedure","Step 12) Command DUT to play the stream also on "+TBAD2+".");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream on on both Golden Units and"
				+ " playing at a medium volume value?");
		if(response!=0){//1==NO
			fail("Audio stream is not on on both Golden Units and"
					+ " playing at a medium volume value.");						
			return;}
		message.showMessage("Test Procedure","Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 14) Command DUT to set stream to its lower volume"
				+ " value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased to"
				+ " Golden Units minimum value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased to"
					+ " Golden Units minimum value.");						
			return;}
		message.showMessage("Test Procedure","Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 16) Command DUT to increase gradually volume value"
				+ " up to the maximum value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume gradually"
				+ " increased on both Golden Units up to its maximum"
				+ " volume value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not gradually"
					+ " increased on both Golden Units up to its maximum"
					+ " volume value.");						
			return;}
		message.showMessage("Test Procedure","Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 18) Command DUT to set stream to a medium volume"
				+ " value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased on both"
				+ " Golden Units to a medium value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased on both"
					+ " Golden Units to a medium value.");						
			return;}
		message.showMessage("Test Procedure","Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 20) Command DUT to mute the stream on both Golden Units.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on both Golden Units?");
		if(response!=0){//1==NO
			fail("Audio stream is not muted on both Golden Units.");						
			return;}
		message.showMessage("Test Procedure","Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 22) Command DUT to unmute the stream on both"
				+ " Golden Units.");
		
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume unmuted on both"
				+ " Golden Units and volume is approximately the same than"
				+ " in step 18?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not unmuted on both"
					+ " Golden Units and volume is approximately the same than"
					+ " in step 18.");						
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
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled."
				+"\nStep 5) To send/receive an audio stream in Audio Service Test Procedures, American"
				+ " English Speech sample M1 from ITU T-Test Signal for Telecommunication"
				+ " Systems,\n Test Vectors Associated to Rec. ITU-T P.50 Appendix I"
				+ " (http://www.itu.int/net/itu-t/sigdb/genaudio/AudioForm-g.aspx?val=1000050)\n will"
				+ " be used. This speech file should be continually played as required by the Test"
				+ " procedure duration.";

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

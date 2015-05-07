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

import javax.swing.JFrame;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioIOP.
 */
public class AudioIOP {
	
	/** The frame. */
	private  JFrame frame;
	 
 	/** The pass. */
 	Boolean pass=true;
	
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
	public AudioIOP(String testCase) {

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
		frame=new JFrame();
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
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
					+ "command TBAD1 to display audio/x-alac parameters values.");
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
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
	
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between the "
				+ "DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
					+ "category 6.2 TBADs?");
			if(response!=0){//1==NO
				fail("Audio stream sent by "+testBed+" not plays on the DUT speaker synchronized "
						+ "with the other category 6.2 TBADs.");						
				return;}
			
		}
		
	}

	


	/**
	 * IOP audio sink_v1_04.
	 */
	private  void IOP_AudioSink_v1_04() {
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
	
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
		
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. If required, use TBAD_O to "
				+ "onboard the DUT and/or category 6.1 TBADs to the personal AP.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command TBAD1 and TBAD2 to discover "
				+ "nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream "
				+ "to be played, according to Preconditions 5, and command TBAD1 to play "
				+ "the audio on the DUT.");
		message.showMessage("Test Procedure","Step 7) If Stream is muted, Command TBAD1 to "
				+ "unmute the stream");
		message.showMessage("Test Procedure","Step 8) Command TBAD1 to set stream to its "
				+ "higher volume value");
		int response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume "
				+ "increased (if it was not previously set to its maximum value)?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not increased (if it was not previously set to its maximum value).");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 10) Command TBAD2 to set stream to its "
				+ "lower volume value.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased "
		 		+ "to DUT minimum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to DUT minimum value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 11) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 12) Command TBAD2 to set stream to a "
				+ "medium volume value (according to its volume range).");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume increased "
		 		+ "to DUT medium value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not increased to a DUT medium value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 14) Command TBAD1 to set stream to its "
				+ "lower volume value.");
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume decreased "
				+ "to DUT minimum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to DUT minimum value.");						
				return;}
			
		
		
		
		message.showMessage("Test Procedure","Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 16) Command TBAD2 to increase gradually volume "
				+ "value up to the maximum value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume gradually "
				+ "increased up to DUT maximum value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not gradually increased up to DUT "
						+ "maximum value.");						
				return;}
			
			
			
		message.showMessage("Test Procedure","Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 18) Command TBAD1 to set stream to a medium "
				+ "volume value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume  decreased "
				+ "to a DUT medium value?");
			if(response!=0){//1==NO
				fail("Audio stream volume is not decreased to a DUT medium value.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 20) Command TBAD1 to mute the DUT.");
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream muted?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted.");						
				return;}
			
			
		message.showMessage("Test Procedure","Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 22) Command TBAD1 to unmute the DUT.");
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
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically");
		message.showMessage("Test Procedure","Step 5) Command DUT to display sequentially each "
				+ "TBAD stream media types supported: audio/x-raw (mandatory), "
				+ "audio/x-alac, image/jpeg and/or application/x-metadata.");
		int response=message.showQuestion("Pass/Fail Criteria","Does DUT display TBADs media "
				+ "types correctly?");
		if(response!=0){//1==NO
			fail("DUT not displays TBADs media types correctly.");						
			return;}
		
		message.showMessage("Test Procedure","Step 6) Command DUT to display each TBAD audio/x-raw "
				+ "parameters values: Number of channels, Sample formats and "
				+ "sample rate.");
		response=message.showQuestion("Pass/Fail Criteria","Does DUT display the TBADs parameters "
				+ "of the media types according to TBADs specifications?");
		if(response!=0){//1==NO
			fail("DUT not displays the TBADs parameters of the media types according "
					+ "to TBADs specifications");						
			return;}
		
	}

	


	/**
	 * IOP audio source_v1_02.
	 */
	private  void IOP_AudioSource_v1_02() {
		
		String testBed="TBAD1";
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		
		int step=5;
		for(int i=1;i<=3;i++){
			testBed="TBAD"+i;
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
					+ "the configurations supported by TBAD1 (preferable other "
					+ "than audio/x-raw).");
			message.showMessage("Test Procedure","Step "+step+" b) Select audio stream to "
					+ "be sent, according to Preconditions 5.");
			message.showMessage("Test Procedure","Step "+step+" c) Select on the DUT "+testBed+" "
					+ "Play option (or equivalent one to call TBAD1 Audio Sink "
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
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		int response=message.showQuestion("Pass/Fail Criteria","Does DUT find TBAD1 Audio "
				+ "Sink service?");
		if(response!=0){//1==NO
			fail("DUT not finds TBAD1 Audio Sink service.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream "
				+ "to be played, according to Preconditions 5, and command DUT to play \n the "
				+ "audio on all the category 6.2 AllJoyn devices of the Test Bed "
				+ "(by calling Audio Sink service ‘Connect’ method).");
		
		response=message.showQuestion("Pass/Fail Criteria","Did audio stream send by DUT plays "
				+ "synchronized in all category 6.2 TBADs?");
		if(response!=0){//1==NO
			fail("Audio stream sent by DUT not plays synchronized in all category "
					+ "6.2 TBADs.");						
			return;}
		
	}
	
	


	/**
	 * IOP audio source_v1_04.
	 */
	private  void IOP_AudioSource_v1_04() {
		
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream to "
				+ "be played, according to Preconditions 5, and play it on TBAD1.");
		int response=message.showQuestion("Pass/Fail Criteria","Does audio stream play on TBAD1?");
		if(response!=0){//1==NO
			fail("Audio stream not plays on TBAD1.");						
			return;}
		
		message.showMessage("Test Procedure","Step 7) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 8) Operate DUT to pause the audio stream "
				+ "playback on TBAD1.");
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream pause on TBAD1?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not pauses on TBAD1.");						
			return;}
		
		message.showMessage("Test Procedure","Step 9) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 10) Operate DUT to resume the audio stream "
				+ "playback on TBAD1.");
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream resume playing "
				+ "on TBAD1?");
		if(response!=0){//1==NO
			fail("Audio stream not resumes playing on TBAD1.");						
			return;}
		
		
		
		message.showMessage("Test Procedure","Step 11) Waits for 20 s.");
		message.showMessage("Test Procedure","Step 12) Operate DUT to stop (flush) the audio "
				+ "stream playback on TBAD1.");
		message.showMessage("Test Procedure","Step 13) Waits for 10 s.");
		message.showMessage("Test Procedure","Step 14) Operate DUT to select an audio stream (long"
				+ " enough for the duration of the test) and play ii on all category 6.2 "
				+ " TBADs in the Test Bed.");
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream play on all "
				+ "category 6.2 TBADs?");
		if(response!=0){//1==NO
			fail("Audio stream not plays on all category 6.2 TBADs.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 15) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 16) Operate DUT to pause the audio stream "
				+ "playback.");
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream pause "
				+ "on all category 6.2 TBADs?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not pauses on all category 6.2 TBADs.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 17) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 18) Operate DUT to resume the audio stream "
				+ "playback on the DUT.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream resume "
				+ "on all category 6.2 TBADs?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not resumes on all category 6.2 TBADs.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 19) Waits for 30 s.");
		message.showMessage("Test Procedure","Step 20) Operate DUT to stop (flush) the audio stream playback.");

		
	}
	
	
	/**
	 * IOP audio source_v1_05.
	 */
	private  void IOP_AudioSource_v1_05() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream to "
				+ "be played, according to Precondition 5, and command DUT to play the audio "
				+ "on TBAD1.");
		message.showMessage("Test Procedure","Step 7) Waits for 60 s.");
		message.showMessage("Test Procedure","Step 8) Operate DUT to stop (flush) the audio stream "
				+ "playback on the DUT.");
		int response=message.showQuestion("Pass/Fail Criteria","Does Ongoing audio stream stop "
				+ "on TBAD1?");
		if(response!=0){//1==NO
			fail("Ongoing audio stream not stops on TBAD1.");						
			return;}
		
		
		message.showMessage("Test Procedure","Step 9) Operate DUT to select again step 6 audio "
				+ "stream and play it on all category 6.2 TBADs.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream start playing "
				+ "all category 6.2 TBADs from the beginning of the audio stream?");
		if(response!=0){//1==NO
			fail("Audio stream not starts playing all category 6.2 TBADs from "
					+ "the beginning of the audio stream");						
			return;}

		
		message.showMessage("Test Procedure","Step 10) Waits for 60 s.");
		message.showMessage("Test Procedure","Step 11) Operate DUT to stop (flush) the audio "
				+ "stream playback.");
		
		response=message.showQuestion("Pass/Fail Criteria","Does Audio stream stops on all "
				+ "category 6.2 TBADs?");
		if(response!=0){//1==NO
			fail("Audio stream not stop on all category 6.2 TBADs");						
			return;}

	}
	
	
	/**
	 * IOP audio source_v1_06.
	 */
	private  void IOP_AudioSource_v1_06() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream to be"
				+ " played, according to section 2.1, and command"
				+ " DUT to play the audio on TBAD1.");
		message.showMessage("Test Procedure","Step 7) Command DUT to mute TBAD1 (by modifying Mute"
				+ " property).");
		int response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on TBAD1?");
		if(response!=0){//1==NO
			fail("Audio stream is not muted on TBAD1.");						
			return;}
		message.showMessage("Test Procedure","Step 8) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 9) Command DUT to unmute the TBAD1.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream unmuted on TBAD1?");
		if(response!=0){//1==NO
			fail("Audio stream is not unmuted on TBAD1.");						
			return;}
		
		message.showMessage("Test Procedure","Step 10) Operate DUT to select an audio stream (long"
				+ " enough for the duration of the test) to be played and"
				+ " command DUT to play the audio on all category 6.2"
				+ " TBADs.");
		message.showMessage("Test Procedure","Step 11) Command DUT to mute the audio stream on all"
				+ " category 6.2 TBADs (by modifying Mute property).");
		
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on all category 6.2"
		 		+ " TBADs?");
			if(response!=0){//1==NO
				fail("Audio stream is not muted on all category 6.2"
						+ " TBADs.");						
				return;}
			
		message.showMessage("Test Procedure","Step 12) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 13) Command DUT to unmute the audio stream on all"
				+ " category 6.2 TBADs.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream unmuted on all category 6.2"
			 		+ " TBADs?");
				if(response!=0){//1==NO
					fail("Audio stream is not unmuted on all category 6.2"
							+ " TBADs.");	
					return;	}
		
	}
	

	/**
	 * IOP audio source_v1_07.
	 */
	private  void IOP_AudioSource_v1_07() {
		message.showMessage("Initial Conditions","DUT and TBADs are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all TBADs of the Test Bed.");
		message.showMessage("Test Procedure","Step 3) Connect the TBADs and/or DUT to the AP "
				+ "network if they are not connected yet. In case any device needs "
				+ "to be onboarded,\n use DUT to onboard the device if DUT can act "
				+ "as an Onboarder, else use TBAD_O to onboard the device.");
		message.showMessage("Test Procedure","Step 4) Establish an AllJoyn connection between "
				+ "the DUT and all the TBADs if is not established automatically.");
		message.showMessage("Test Procedure","Step 5) Command DUT to discover nearby Audio Sinks.");
		message.showMessage("Test Procedure","Step 6) Operate TBAD1 to select an audio stream to be"
				+ " played, according to section 2.1, and command"
				+ " DUT to play the audio on TBAD1.");
		message.showMessage("Test Procedure","Step 7) If Stream is muted, Command DUT to unmute the"
				+ " stream.");
		message.showMessage("Test Procedure","Step 8) Command DUT to set stream to its higher volume"
				+ " value.");
		int response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume increased on"
				+ " TBAD1 (if it was not previously set to its maximum"
				+ " value)?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not increased on"
					+ " TBAD1 (if it was not previously set to its maximum"
					+ " value).");						
			return;}
		message.showMessage("Test Procedure","Step 9) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 10) Command DUT to set stream to a medium volume"
				+ " value.");
		 response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased to a"
		 		+ " medium TBAD1 value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased to a"
					+ " medium TBAD1 value.");						
			return;}
		message.showMessage("Test Procedure","Step 11) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 12) Command DUT to play the stream also on TBAD2.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream on on both TBADs and"
				+ " playing at a medium volume value?");
		if(response!=0){//1==NO
			fail("Audio stream is not on on both TBADs and"
					+ " playing at a medium volume value.");						
			return;}
		message.showMessage("Test Procedure","Step 13) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 14) Command DUT to set stream to its lower volume"
				+ " value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased to"
				+ " TBADs minimum value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased to"
					+ " TBADs minimum value.");						
			return;}
		message.showMessage("Test Procedure","Step 15) Wait for 10 s.");
		message.showMessage("Test Procedure","Step 16) Command DUT to increase gradually volume value"
				+ " up to the maximum value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume gradually"
				+ " increased on both TBADs up to its maximum"
				+ " volume value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not gradually"
					+ " increased on both TBADs up to its maximum"
					+ " volume value.");						
			return;}
		message.showMessage("Test Procedure","Step 17) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 18) Command DUT to set stream to a medium volume"
				+ " value.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream volume decreased on both"
				+ " TBADs to a medium value?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not decreased on both"
					+ " TBADs to a medium value.");						
			return;}
		message.showMessage("Test Procedure","Step 19) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 20) Command DUT to mute the stream on both TBADs.");
		response=message.showQuestion("Pass/Fail Criteria","Is audio stream muted on both TBADs?");
		if(response!=0){//1==NO
			fail("Audio stream is not muted on both TBADs.");						
			return;}
		message.showMessage("Test Procedure","Step 21) Wait for 5 s.");
		message.showMessage("Test Procedure","Step 22) Command DUT to unmute the stream on both"
				+ " TBADs.");
		
		response=message.showQuestion("Pass/Fail Criteria","Is Audio stream volume unmuted on both"
				+ " TBADs and volume is approximately the same than"
				+ " in step 18?");
		if(response!=0){//1==NO
			fail("Audio stream volume is not unmuted on both"
					+ " TBADs and volume is approximately the same than"
					+ " in step 18.");						
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

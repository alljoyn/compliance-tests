/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.testcases.conf.audio;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.client.AboutClientImpl;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Mutable.IntegerValue;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.ClientBase;
import org.xml.sax.SAXException;













































































































import static com.at4wireless.alljoyn.core.audio.AudioSinkParameterSignature.Channels;
import static com.at4wireless.alljoyn.core.audio.AudioSinkParameterSignature.Format;
import  static com.at4wireless.alljoyn.core.audio.AudioSinkParameterSignature.Rate;
import static com.at4wireless.alljoyn.core.audio.AudioSinkPlayState.Idle;
import static com.at4wireless.alljoyn.core.audio.AudioSinkPlayState.Paused;
import static com.at4wireless.alljoyn.core.audio.AudioSinkPlayState.Playing;
import static com.at4wireless.alljoyn.core.audio.MediaType.ApplicationXMetadata;
import static com.at4wireless.alljoyn.core.audio.MediaType.AudioPrefix;
import static com.at4wireless.alljoyn.core.audio.MediaType.AudioXAlac;
import static com.at4wireless.alljoyn.core.audio.MediaType.AudioXRaw;
import static com.at4wireless.alljoyn.core.audio.MediaType.AudioXUnknown;
import static com.at4wireless.alljoyn.core.audio.MediaType.ImagePrefix;
import static com.at4wireless.alljoyn.core.audio.MediaType.ImageJpeg;


















































































import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.audio.AudioSinkParameter;
import com.at4wireless.alljoyn.core.audio.AudioSinkPlayStateChangedSignal;
import com.at4wireless.alljoyn.core.audio.AudioSourceObject;
import com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink;
import com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSource;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Clock;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Configuration;
import com.at4wireless.alljoyn.core.audio.AudioTransports.ImageSource;
import com.at4wireless.alljoyn.core.audio.AudioTransports.MetadataSource;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Port;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Stream;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Volume;
import com.at4wireless.alljoyn.core.audio.AudioTransports.VolumeRange;
import com.at4wireless.alljoyn.core.audio.ImageSourceObject;
import com.at4wireless.alljoyn.core.audio.MetadataSourceObject;
import com.at4wireless.alljoyn.core.audio.handlers.AudioSinkSignalHandler;
import com.at4wireless.alljoyn.core.audio.handlers.OwnershipLostSignalHandler;
import com.at4wireless.alljoyn.core.audio.handlers.VolumeControlSignalHandler;
import com.at4wireless.alljoyn.core.commons.BusAttachmentMgr;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.UserInputDetails;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionSubNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

public class AudioTestSuite
{
	private static final String TAG = "AudioTestSuite";
	//private static final Logger logger = LoggerFactory.getLogger(TAG);
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private static final int DATA_SIZE = 16 * 1024;
	private static final byte[] DATA = new byte[DATA_SIZE];
	private static final int INVALID_HIGH_VOLUME = 32767;
	private static final int INVALID_LOW_VOLUME = -32768;
	private static final String VOLUME_INTERFACE_NAME = "org.alljoyn.Control.Volume";
	private static final String AUDIO_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.AudioSink";
	private static final String IMAGE_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.ImageSink";
	private static final String METADATA_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.Application.MetadataSink";
	private static final String PORT_INTERFACE_NAME = "org.alljoyn.Stream.Port";
	private static final String ER_BUS_REPLY_IS_ERROR_MESSAGE = "ER_BUS_REPLY_IS_ERROR_MESSAGE";
	private static final int TIME_DIFFERENCE_IN_NANOSECONDS = 1000000000;
	private static final int NUMBER_OF_NANOSECONDS_IN_ONE_MILLISECOND = 1000000;
	private static final String ALBUM_TITLE_VALUE = "album title";
	private static final String ALBUM_TITLE_KEY = "Album";
	private static final String ITEM_NAME_VALUE = "item name";
	private static final String ITEM_NAME_KEY = "Name";
	private static final String METADATA_SOURCE_PATH = "/Player/Out/Metadata";
	private static final String IMAGE_SOURCE_PATH = "/Player/Out/Image";
	private static final String AUDIO_SOURCE_PATH = "/Player/Out/Audio";
	private static final short MANDATORY_RATE_ONE = -21436;
	private static final short MANDATORY_RATE_TWO = -17536;
	private static final String MANDATORY_FORMAT = "s16le";
	private static final byte MANDATORY_CHANNEL_ONE = 1;
	private static final byte MANDATORY_CHANNEL_TWO = 2;
	//private static final short INTERFACE_VERSION = 1; //[AT4] Not needed since IXIT creation
	private static final String SLASH_CHARACTER = "/";
	private static final String BUS_APPLICATION_NAME = "Audio";
	//private static final long SIGNAL_TIMEOUT_IN_SECONDS = 30; //[AT4] Not "static final" since GP creation
	private long SIGNAL_TIMEOUT_IN_SECONDS = 30;
	//private static final int LINK_TIMEOUT_IN_SECONDS = 120; //[AT4] Not "static final" since GP creation
	private int LINK_TIMEOUT_IN_SECONDS = 120;
	private static final byte PORT_DIRECTION = 1;

	private AboutClient aboutClient;
	private AboutAnnouncementDetails deviceAboutAnnouncement;
	private ServiceHelper serviceHelper;
	private String streamObjectPath;
	private Stream stream;
	
	//private AppUnderTestDetails appUnderTestDetails;
	private UUID dutAppId;
	private String dutDeviceId;
	
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	boolean pass = true;
	boolean inconc = false;
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;
	private Ics icsList;
	private Ixit ixitList;

	public AudioTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		/** 
		 * [AT4] Attributes initialization
		 * */
		this.icsList = icsList;
		this.ixitList = ixitList;
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = gpList.GPCO_AnnouncementTimeout;
		SIGNAL_TIMEOUT_IN_SECONDS = gpList.GPAU_Signal;
		LINK_TIMEOUT_IN_SECONDS = gpList.GPAU_Link;
		
		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			inconc = true;
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		setUp();
		
		try
		{
			logger.info("Running testcase: "+testCase);
	
			if (testCase.equals("Audio-v1-01")) {
				testAudio_v1_01_ValidateStreamObjects();
			} else if(testCase.equals("Audio-v1-02")) {
				testAudio_v1_02_OpenStreamObject();
			} else if(testCase.equals("Audio-v1-03")) {
				testAudio_v1_03_OpenAndCloseStreamObject();
			} else if(testCase.equals("Audio-v1-04")) {
				testAudio_v1_04_CloseUnopenedStreamObject();
			} else if(testCase.equals("Audio-v1-05")) {
				testAudio_v1_05_VerifyAudioSinkCapabilities();
			} else if(testCase.equals("Audio-v1-06")) {
				testAudio_v1_06_VerifyImageSinkCapabilities();
			} else if(testCase.equals("Audio-v1-07")) {
				testAudio_v1_07_VerifyApplicationMetadataCapabilities();
			} else if(testCase.equals("Audio-v1-08")) {
				testAudio_v1_08_ConfigureAudioSinkPort();
			} else if(testCase.equals("Audio-v1-09")) {
				testAudio_v1_09_ConfigureAudioSinkPortWithInvalidConfiguration();
			} else if(testCase.equals("Audio-v1-10")) {
				testAudio_v1_10_ConfigureAudioSinkPortTwice();
			} else if(testCase.equals("Audio-v1-11")) {
				testAudio_v1_11_CheckOwnershipLostSignal();
			} else if(testCase.equals("Audio-v1-12")) {
				testAudio_v1_12_PlaybackAudioSink();
			} else if(testCase.equals("Audio-v1-13")) {
				testAudio_v1_13_PauseAudioSinkPlayback();
			} else if(testCase.equals("Audio-v1-14")) {
				testAudio_v1_14_FlushPausedAudioSink();
			} else if(testCase.equals("Audio-v1-15")) {
				testAudio_v1_15_FlushPlayingAudioSink();
			} else if(testCase.equals("Audio-v1-16")) {
				testAudio_v1_16_VerifyPausedAudioSinkRemainsPausedAfterSendingData();
			} else if(testCase.equals("Audio-v1-17")) {
				testAudio_v1_17_VerifyPlayingEmptyAudioSinkRemainsIdle();
			} else if(testCase.equals("Audio-v1-18")) {
				testAudio_v1_18_FlushIdleAudioSink();
			} else if(testCase.equals("Audio-v1-19")) {
				testAudio_v1_19_SendDataToImageSink();
			} else if(testCase.equals("Audio-v1-20")) {
				testAudio_v1_20_SendDataToMetadataSink();
			} else if(testCase.equals("Audio-v1-21")) {
				testAudio_v1_21_VerifyAudioSinkCanBeMutedUnmuted();
			} else if(testCase.equals("Audio-v1-22")) {
				testAudio_v1_22_VerifyVolumeCanBeSetOnAudioSink();
			} else if(testCase.equals("Audio-v1-23")) {
				testAudio_v1_23_SetInvalidVolumeOnAudioSink();
			} else if(testCase.equals("Audio-v1-24")) {
				testAudio_v1_24_VerifyIndependenceOfMuteAndVolumeOnAudioSink();
			} else if(testCase.equals("Audio-v1-25")) {
				testAudio_v1_25_SynchronizeClocksOnAudioSink();
			} else if(testCase.equals("Audio-v1-26")) {
				testAudio_v1_26_VerifyVolumeCanBeAdjustedOnAudioSink();
			} else if(testCase.equals("Audio-v1-27")) {
				testAudio_v1_27_VerifyVolumeCanBeAdjustedByPercentOnAudioSink();
			} else if(testCase.equals("Audio-v1-28")) {
				testAudio_v1_28_VerifyVolumeNotAdjustableWhenDisabled();
			} else {
				fail("Test Case not valid");
			}
		}
		catch (Exception exception)
		{
			logger.error("Exception executing Test Case: %s", exception.getMessage()); //[AT4]
			
			try 
			{
				tearDown();
			} 
			catch (Exception newException) 
			{
				logger.error("Exception releasing resources: %s", newException.getMessage());
			}
			
			throw exception;
		}

		tearDown();
	}
	
	private void setUp() throws Exception
	{
		//super.setUp();
		
		try
		{
			logger.noTag("====================================================");
			logger.info("test setUp started");

			//appUnderDetails = getValidationTestContext().getAppUnderTestDetails();
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixitList.IXITCO_DeviceId;
			logger.info(String.format("Running test case against Device ID: %s", dutDeviceId));
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = ixitList.IXITCO_AppId;
			logger.info(String.format("Running test case against App ID: %s", dutAppId));

			//streamObjectPath = getValidationTestContext().getTestObjectPath();
			streamObjectPath = ixitList.IXITAU_TestObjectPath;

			if (streamObjectPath == null || streamObjectPath.isEmpty())
			{
				throw new RuntimeException("Audio Stream object path not specified");
			}

			logger.info(String.format("Executing Audio test against Stream object found at %s", streamObjectPath));

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			//deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			//assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			
			if (deviceAboutAnnouncement == null) //[AT4]
			{
				throw new Exception("Timed out waiting for About announcement");
			}	
			
			//serviceHelper.joinSession(BUS_APPLICATION_NAME, port);
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			stream = getIntrospector().getInterface(streamObjectPath, Stream.class);
			logger.info("test setUp done");
		}
		catch (Exception exception)
		{
			try
			{
				//releaseResources(); //[AT4]
				tearDown();
			}
			catch (Exception newException)
			{
				logger.info(String.format("Exception releasing resources: %s", newException.getMessage()));
			}

			throw exception;
		}
		
		logger.noTag("====================================================");
	}
	
	private void tearDown()
	{
		//super.tearDown();
		logger.noTag("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		logger.noTag("====================================================");
	}

	public void testAudio_v1_01_ValidateStreamObjects() throws Exception
	{
		BusIntrospector busIntrospector = getIntrospector();
		validateStreamInterface(streamObjectPath, busIntrospector);
		NodeDetail nodeDetail = busIntrospector.introspect(streamObjectPath);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();
		boolean portObjectValidated = false;

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = streamObjectPath + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, PORT_INTERFACE_NAME))
			{
				validatePortInterface(subNodePath, busIntrospector);
				portObjectValidated = true;
			}
		}

		assertTrue("The object implementing the Stream interface must have at least one child object implementing org.alljoyn.Stream.Port", portObjectValidated);
	}

	public void testAudio_v1_02_OpenStreamObject() throws Exception
	{
		stream.Open();
	}

	public void testAudio_v1_03_OpenAndCloseStreamObject() throws Exception
	{
		stream.Open();
		stream.Close();
	}

	public void testAudio_v1_04_CloseUnopenedStreamObject() throws Exception
	{
		stream.Open();
		stream.Close();

		try
		{
			stream.Close();
			fail("Closing an unopened stream must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			logger.info("Exception caught while closing an unopened stream", errorReplyBusException);
		}
	}

	public void testAudio_v1_05_VerifyAudioSinkCapabilities() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			validateAudioSinkCapabilities(audioSinkPort.getCapabilities());
		}
	}
	
	public void testAudio_v1_06_VerifyImageSinkCapabilities() throws Exception
	{
		Port imageSinkPort = getImageSinkPort(streamObjectPath, getIntrospector());

		if (imageSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support ImageSink!");
			fail("Stream does not support ImageSink!");
			return;
		}
		else
		{
			stream.Open();
			validateImageSinkCapabilities(imageSinkPort.getCapabilities());
		}
	}

	public void testAudio_v1_07_VerifyApplicationMetadataCapabilities() throws Exception
	{
		Port metadataSinkPort = getMetadataSinkPort(streamObjectPath, getIntrospector());

		if (metadataSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support ApplicationMetadataSink!");
			fail("Stream does not support ApplicationMetadataSink!");
			return;
		}
		else
		{
			stream.Open();
			validateMetadataSinkCapabilities(metadataSinkPort.getCapabilities());
		}
	}

	public void testAudio_v1_08_ConfigureAudioSinkPort() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			stream.Close();
		}
	}

	public void testAudio_v1_09_ConfigureAudioSinkPortWithInvalidConfiguration() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			validateConnectingToAudioSinkWithInvalidConfigurationThrowsException(audioSinkPort);
			stream.Close();
		}
	}

	public void testAudio_v1_10_ConfigureAudioSinkPortTwice() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			validateConnectingToAudioSinkTwiceThrowsException(audioSinkPort);
			stream.Close();
		}
	}

	public void testAudio_v1_11_CheckOwnershipLostSignal() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			CountDownLatch countDownLatch = new CountDownLatch(1);
			OwnershipLostSignalHandler ownershipLostSignalHandler = new OwnershipLostSignalHandler(countDownLatch);
			registerOwnershipLostSignalHandler(streamObjectPath, ownershipLostSignalHandler);
			openNewStream(countDownLatch, ownershipLostSignalHandler);
		}
	}

	public void testAudio_v1_12_PlaybackAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			emitAudioSourceDataSignals(audioSink.getFifoSize());

			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Idle.getValue(), Playing.getValue());
			assertFifoPositionChangedSignalIsReceived(audioSinkSignalHandler);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Playing.getValue(), Idle.getValue());
			stream.Close();
		}
	}

	public void testAudio_v1_13_PauseAudioSinkPlayback() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			emitAudioSourceDataSignals(audioSink.getFifoSize());

			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Idle.getValue(), Playing.getValue());
			audioSink.Pause(0);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Playing.getValue(), Paused.getValue());
			stream.Close();
		}
	}

	public void testAudio_v1_14_FlushPausedAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			emitAudioSourceDataSignals(audioSink.getFifoSize());

			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Idle.getValue(), Playing.getValue());
			audioSink.Pause(0);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Playing.getValue(), Paused.getValue());
			audioSinkSignalHandler.clearFifoPositionChangedSignalQueue();
			audioSink.Flush(0);
			assertFifoPositionChangedSignalIsReceived(audioSinkSignalHandler);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Paused.getValue(), Idle.getValue());
			stream.Close();
		}
	}

	public void testAudio_v1_15_FlushPlayingAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			emitAudioSourceDataSignals(audioSink.getFifoSize());

			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Idle.getValue(), Playing.getValue());
			audioSink.Flush(0);
			assertFifoPositionChangedSignalIsReceived(audioSinkSignalHandler);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Playing.getValue(), Idle.getValue());
			stream.Close();
		}
	}

	public void testAudio_v1_16_VerifyPausedAudioSinkRemainsPausedAfterSendingData() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);

			audioSink.Pause(0);
			assertPlayStateChangedSignalIsReceived(audioSinkSignalHandler, Idle.getValue(), Paused.getValue());
			emitAudioSourceDataSignals(audioSink.getFifoSize());
			assertNull("Received unexpected PlayStateChanged signal", audioSinkSignalHandler.waitForNextPlayStateChangedSignal(1, TimeUnit.SECONDS));
			stream.Close();
		}
	}

	public void testAudio_v1_17_VerifyPlayingEmptyAudioSinkRemainsIdle() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			audioSink.Play();
			assertNull("Received unexpected PlayStateChanged signal", audioSinkSignalHandler.waitForNextPlayStateChangedSignal(1, TimeUnit.SECONDS));
			stream.Close();
		}
	}

	public void testAudio_v1_18_FlushIdleAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			AudioSink audioSink = getAudioSink(streamObjectPath, getIntrospector());
			validateAudioSinkPropertiesCanBeRetrieved(audioSink);

			AudioSinkSignalHandler audioSinkSignalHandler = getAudioSinkSignalHandler();
			registerAudioSinkSignalHandler(streamObjectPath, audioSinkSignalHandler);
			audioSink.Flush(0);
			assertFifoPositionChangedSignalIsReceived(audioSinkSignalHandler);
			stream.Close();
		}
	}

	public void testAudio_v1_19_SendDataToImageSink() throws Exception
	{
		Port imageSinkPort = getImageSinkPort(streamObjectPath, getIntrospector());

		if (imageSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support ImageSink!");
			fail("Stream does not support ImageSink!");
			return;
		}
		else
		{
			stream.Open();
			connect(imageSinkPort, IMAGE_SOURCE_PATH);
			emitImageDataSignal();
			stream.Close();
		}
	}

	public void testAudio_v1_20_SendDataToMetadataSink() throws Exception
	{
		Port metadataSinkPort = getMetadataSinkPort(streamObjectPath, getIntrospector());

		if (metadataSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support MetadataSink!");
			fail("Stream does not support ApplicationMetadataSink!");
			return;
		}
		else
		{
			stream.Open();
			connect(metadataSinkPort, METADATA_SOURCE_PATH);
			emitMetadataDataSignal();
			stream.Close();
		}
	}

	public void testAudio_v1_21_VerifyAudioSinkCanBeMutedUnmuted() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeControlSignalHandler volumeControlSignalHandler = createVolumeControlSignalHandler();
			registerVolumeControlSignalHandler(streamObjectPath, volumeControlSignalHandler);
			invertMuteValueOnVolume(volume, volumeControlSignalHandler);
			invertMuteValueOnVolume(volume, volumeControlSignalHandler);

			stream.Close();
		}
	}

	public void testAudio_v1_22_VerifyVolumeCanBeSetOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeRange volumeRange = volume.getVolumeRange();
			short high = volumeRange.high;
			short low = volumeRange.low;
			short step = volumeRange.step;
			validateVolumeRangeProperties(high, low, step);

			short originalVolumeProperty = volume.getVolume();
			logger.info("Volume Interface returned Volume property value as : " + originalVolumeProperty);
			validateOriginalVolumeProperty(high, low, step, originalVolumeProperty);

			VolumeControlSignalHandler volumeControlSignalHandler = createVolumeControlSignalHandler();
			registerVolumeControlSignalHandler(streamObjectPath, volumeControlSignalHandler);

			short newVolumeProperty = getNewVolumePropertyValue(originalVolumeProperty, volumeRange);
			logger.info("New Volume property to be set on the Volume interface : " + newVolumeProperty);
			setVolumeProperty(volume, newVolumeProperty);
			assertVolumeChangedSignalIsReceived(newVolumeProperty, volumeControlSignalHandler);

			logger.info("Setting volume property value to the original value " + originalVolumeProperty);
			setVolumeProperty(volume, originalVolumeProperty);
			assertVolumeChangedSignalIsReceived(originalVolumeProperty, volumeControlSignalHandler);

			stream.Close();
		}
	}

	public void testAudio_v1_23_SetInvalidVolumeOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeRange volumeRange = volume.getVolumeRange();
			logger.info("Volume Interface returned High Volume Range property value as : " + volumeRange.high);
			logger.info("Volume Interface returned Low Volume Range property value as : " + volumeRange.low);
			logger.info("Volume Interface returned Step Volume Range property value as : " + volumeRange.step);

			short newVolumeProperty = INVALID_LOW_VOLUME;

			setInvalidVolumeValue(volume, newVolumeProperty);

			newVolumeProperty = INVALID_HIGH_VOLUME;

			setInvalidVolumeValue(volume, newVolumeProperty);

			stream.Close();
		}
	}

	public void testAudio_v1_24_VerifyIndependenceOfMuteAndVolumeOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeRange volumeRange = volume.getVolumeRange();
			logger.info("Volume Interface returned High Volume Range property value as : " + volumeRange.high);
			logger.info("Volume Interface returned Low Volume Range property value as : " + volumeRange.low);
			logger.info("Volume Interface returned Step Volume Range property value as : " + volumeRange.step);

			short midRangeVolumeProperty = getMidRangeVolumeProperty(volumeRange);
			logger.info("New Mid Range Volume Property : " + midRangeVolumeProperty);

			setVolumeProperty(volume, midRangeVolumeProperty);
			setMuteProperty(volume, true);
			assertEquals("Setting the Muted property to true has changed the Volume property value",midRangeVolumeProperty, volume.getVolume());

			setMuteProperty(volume, false);
			assertEquals("Setting the Muted property to false has changed the Volume property value",midRangeVolumeProperty, volume.getVolume());

			short oneMoreThanLowestVolumePropertyValue = (short) (volumeRange.low + volumeRange.step);
			setVolumeProperty(volume, oneMoreThanLowestVolumePropertyValue);
			setMuteProperty(volume, true);
			assertEquals("Setting the Volume property to a new value one step value more than the lowest allowed value has changed the Mute property value",oneMoreThanLowestVolumePropertyValue, volume.getVolume());

			stream.Close();
		}
	}

	public void testAudio_v1_25_SynchronizeClocksOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			Clock clock = getClock(streamObjectPath);
			if (clock == null)
			{
				fail("Stream does not support Clock!");
				return;
			}
			else
			{
				stream.Open();
				audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

				long currentTime = System.nanoTime();
				clock.SetTime(System.currentTimeMillis() * NUMBER_OF_NANOSECONDS_IN_ONE_MILLISECOND);
				long newCurrentTime = System.nanoTime();

				long timeToAdjust = (newCurrentTime - currentTime) / 2;
				clock.AdjustTime(timeToAdjust);
				stream.Close();
			}
		}
	}

	public void testAudio_v1_26_VerifyVolumeCanBeAdjustedOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeRange volumeRange = volume.getVolumeRange();
			short high = volumeRange.high;
			short low = volumeRange.low;
			short step = volumeRange.step;
			validateVolumeRangeProperties(high, low, step);

			short originalVolumeProperty = volume.getVolume();
			logger.info("Volume Interface returned Volume property value as : " + originalVolumeProperty);
			validateOriginalVolumeProperty(high, low, step, originalVolumeProperty);

			VolumeControlSignalHandler volumeControlSignalHandler = createVolumeControlSignalHandler();
			registerVolumeControlSignalHandler(streamObjectPath, volumeControlSignalHandler);

			short newVolumeProperty = getNewVolumePropertyValue(originalVolumeProperty, volumeRange);
			short adjustment = (short) (newVolumeProperty - originalVolumeProperty);
			logger.info("New Volume property to be set on the Volume interface : " + adjustment);
			volume.AdjustVolume(adjustment);
			assertVolumeChangedSignalIsReceived(newVolumeProperty, volumeControlSignalHandler);
			assertEquals("The volume Property has not changed",volume.getVolume(), newVolumeProperty);

			logger.info("Setting volume property value to the original value " + originalVolumeProperty);
			setVolumeProperty(volume, originalVolumeProperty);
			assertVolumeChangedSignalIsReceived(originalVolumeProperty, volumeControlSignalHandler);

			stream.Close();
		}
	}

	public void testAudio_v1_27_VerifyVolumeCanBeAdjustedByPercentOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

			Volume volume = getVolume(streamObjectPath, getIntrospector());
			VolumeRange volumeRange = volume.getVolumeRange();
			short high = volumeRange.high;
			short low = volumeRange.low;
			short step = volumeRange.step;
			validateVolumeRangeProperties(high, low, step);

			short originalVolumeProperty = volume.getVolume();
			logger.info("Volume Interface returned Volume property value as : " + originalVolumeProperty);
			validateOriginalVolumeProperty(high, low, step, originalVolumeProperty);

			VolumeControlSignalHandler volumeControlSignalHandler = createVolumeControlSignalHandler();
			registerVolumeControlSignalHandler(streamObjectPath, volumeControlSignalHandler);

			setVolumeProperty(volume, low);
			assertVolumeChangedSignalIsReceived(low, volumeControlSignalHandler);

			short expectedVolume = (short) ((low + (high - low)) / 2);
			volume.AdjustVolumePercent(0.5);
			assertTrue("The volume Property has not changed",(volume.getVolume() - expectedVolume) < step);
			volumeControlSignalHandler.waitForNextVolumeChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);

			logger.info("Setting volume property value to the original value " + originalVolumeProperty);
			setVolumeProperty(volume, originalVolumeProperty);
			assertVolumeChangedSignalIsReceived(originalVolumeProperty, volumeControlSignalHandler);

			stream.Close();
		}
	}

	public void testAudio_v1_28_VerifyVolumeNotAdjustableWhenDisabled() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			//getValidationContext().addNote("Stream does not support AudioSink!");
			fail("Stream does not support AudioSink!");
			return;
		}

		stream.Open();
		audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

		Volume volume = getVolume(streamObjectPath, getIntrospector());
		boolean enabled = volume.getEnabled();
		if (enabled)
		{
			/*getValidationTestContext()
					.waitForUserInput(new UserInputDetails("Disable Device", "Please switch the audio device to disable if this option is supported", "Continue"));*/
			String[] msg = {"Continue"};
			new UserInputDetails("Disable Device", "Please switch the audio device to disable if this option is supported", msg); //[AT4]
			if (enabled)
			{
				//getValidationContext().addNote("Audio volume control still enabled, assuming not supported");
				fail("Audio volume control still enabled, assuming not supported");
				return;
			}
		}

		boolean caughtSomething = false;
		try
		{
			volume.setVolume((short) (volume.getVolume() + 5));
		}
		catch (BusException e)
		{
			caughtSomething = true;
		}
		//assertTrue(caughtSomething);
		assertTrue("It must return errors when in disabled mode", caughtSomething);

		caughtSomething = false;
		try
		{
			volume.AdjustVolume((short) 5);
		}
		catch (BusException e)
		{
			caughtSomething = true;
		}
		//assertTrue(caughtSomething);
		assertTrue("It must return errors when in disabled mode", caughtSomething);

		caughtSomething = false;
		try
		{
			volume.AdjustVolumePercent(1.0);
		}
		catch (BusException e)
		{
			caughtSomething = true;
		}
		//assertTrue(caughtSomething);
		assertTrue("It must return errors when in disabled mode",caughtSomething);
		
		//getValidationTestContext().waitForUserInput(new UserInputDetails("Enable Device", "Please enable the volume control", "Continue"));
		String[] msg = {"Continue"};
		new UserInputDetails("Enable Device", "Please enable the volume control", msg); //[AT4]
	}
	
	AudioSinkSignalHandler getAudioSinkSignalHandler()
	{
		return new AudioSinkSignalHandler();
	}

	VolumeControlSignalHandler createVolumeControlSignalHandler()
	{
		return new VolumeControlSignalHandler();
	}
	
	BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}

	BusIntrospector getIntrospector(BusAttachment busAttachment, AboutClient newAboutClient)
	{
		return new XmlBasedBusIntrospector(busAttachment, newAboutClient.getPeerName(), newAboutClient.getSessionId());
	}

	BusIntrospector getIntrospector(ServiceHelper serviceHelper, AboutClient aboutClient)
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}

	ServiceHelper getServiceHelper()
	{
		//return new ServiceHelper(new AndroidLogger());
		return new ServiceHelper(logger);
	}
	
	SignalEmitter createSignalEmitter(BusObject busObject)
	{
		SignalEmitter signalEmitter = new SignalEmitter(busObject, aboutClient.getSessionId(), SignalEmitter.GlobalBroadcast.Off);
		signalEmitter.setSessionlessFlag(false);
		signalEmitter.setTimeToLive(0);

		return signalEmitter;
	}
	
	long getSignalTimeout()
	{
		return SIGNAL_TIMEOUT_IN_SECONDS;
	}
	
	BusAttachmentMgr createBusAttachmentManager() throws BusException
	{
		BusAttachmentMgr busAttachmentManager = new BusAttachmentMgr();
		busAttachmentManager.create(BUS_APPLICATION_NAME, RemoteMessage.Receive);
		busAttachmentManager.connect();

		return busAttachmentManager;
	}
	
	AboutClient createAboutClient(BusAttachment busAttachment) throws Exception
	{
		AboutClient newAboutClient = new AboutClientImpl(deviceAboutAnnouncement.getServiceName(), busAttachment, null, deviceAboutAnnouncement.getPort());
		logger.info(String.format("Connecting AboutClient for serviceName: %s", deviceAboutAnnouncement.getServiceName()));
		Status status = connectClient(newAboutClient, busAttachment);

		if (status != Status.OK)
		{
			throw new BusException(String.format("Failed to connect AboutClient to client: %s", status));
		}

		return newAboutClient;
	}
	
	short getMidRangeVolumeProperty(VolumeRange volumeRange)
	{
		return (short) ((short) volumeRange.low + (((volumeRange.high - volumeRange.low) / volumeRange.step / 2) * volumeRange.step));
	}
	
	private void setVolumeProperty(Volume volume, short volumeProperty) throws BusException
	{
		volume.setVolume(volumeProperty);
		//assertEquals(volumeProperty, volume.getVolume());
		assertEquals("The volume has not been set correctly", volumeProperty, volume.getVolume()); //[AT4]
	}
	
	private void setMuteProperty(Volume volume, boolean mute) throws BusException
	{
		volume.setMute(mute);
		//assertEquals(mute, volume.getMute());
		assertEquals("Mute has not been set correctly", mute, volume.getMute()); //[AT4]
	}
	
	private void assertVolumeChangedSignalIsReceived(short newVolume, VolumeControlSignalHandler volumeControlSignalHandler) throws InterruptedException
	{
		Short volumeChangedSignalValue = volumeControlSignalHandler.waitForNextVolumeChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for VolumeChanged signal", volumeChangedSignalValue);
		//assertEquals(newVolume, volumeChangedSignalValue.shortValue());
		assertEquals("Volume changed signal has not been received", newVolume, volumeChangedSignalValue.shortValue());
	}
	
	private void validateOriginalVolumeProperty(short high, short low, short step, short originalVolumeProperty)
	{
		if (originalVolumeProperty < low || originalVolumeProperty > high || ((originalVolumeProperty - low) % step) != 0)
		{
			fail("Current volume value " + originalVolumeProperty + " is not within Volume Range properties of low: " + low + " and high: " + high + " and step: " + step);
		}
	}
	
	private void validateVolumeRangeProperties(short high, short low, short step)
	{
		if (low > high)
		{
			fail("Low Volume Range property value is greater than high!");
		}
		if (step < 0)
		{
			fail("Volume Range step value is less than zero!");
		}
		if (((high - low) % step) != 0)
		{
			fail("Volume Range high - low must be divisible by step!");
		}
	}
	
	private void setInvalidVolumeValue(Volume volume, short newVolumeProperty)
	{
		logger.info("New Volume Property : " + newVolumeProperty);

		try
		{
			volume.setVolume(newVolumeProperty);
			fail(String.format("Setting volume to %d should throw ErrorReplyBusException", newVolumeProperty));
		}
		catch (BusException busException)
		{
			logger.info("Setting volume to " + newVolumeProperty + " throws ErrorReplyBusException!");
			//assertEquals(ER_BUS_REPLY_IS_ERROR_MESSAGE, busExceptino.getMessage());
			assertEquals("Exception thrown is different to ER_BUS_REPLY_IS_ERROR_MESSAGE", ER_BUS_REPLY_IS_ERROR_MESSAGE, busException.getMessage());
		}
	}
	
	private void invertMuteValueOnVolume(Volume volume, VolumeControlSignalHandler volumeControlSignalHandler) throws BusException, InterruptedException
	{
		boolean volumeMuteProperty = getMutePropertyOnVolume(volume);
		boolean muteInverted = invertMuteProperty(volumeMuteProperty);

		setMuteProperty(volume, muteInverted);
		Boolean muteChangedSignalValue = volumeControlSignalHandler.waitForNextMuteChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for MuteChanged signal", muteChangedSignalValue);
		//assertEquals(muteInverted, muteChangedSignalValue.booleanValue());
		assertEquals("Mute has not been changed correctly", muteInverted, muteChangedSignalValue.booleanValue());
	}
	
	private boolean invertMuteProperty(boolean volumeMuteProperty)
	{
		boolean muteInverted = !volumeMuteProperty;
		logger.info("Inverted property value : " + muteInverted);
		return muteInverted;
	}

	private boolean getMutePropertyOnVolume(Volume volume) throws BusException
	{
		boolean volumeMuteProperty = volume.getMute();
		logger.info("Volume Interface returned Mute property value as : " + volumeMuteProperty);
		return volumeMuteProperty;
	}
	
	private Clock getClock(String path)
	{
		Clock clock = null;
		try
		{
			clock = getIntrospector().getInterface(path, Clock.class);
			//assertEquals(1, clock.getVersion());
			assertEquals(String.format("The clock Version is not: %s", ixitList.IXITAU_ClockVersion),
					ixitList.IXITAU_ClockVersion, clock.getVersion());
		}
		catch (Exception e)
		{
			logger.info("Stream does not support Clock!");
		}
		return clock;
	}

	private void registerOwnershipLostSignalHandler(String path, OwnershipLostSignalHandler ownershipLostSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(ownershipLostSignalHandler, path);
		serviceHelper.registerSignalHandler(ownershipLostSignalHandler);
	}

	private Status connectClient(ClientBase client, BusAttachment busAttachment) throws BusException, Exception
	{
		Status status = client.connect();

		if (status == Status.ALLJOYN_JOINSESSION_REPLY_ALREADY_JOINED)
		{
			logger.info("Ignoring ALLJOYN_JOINSESSION_REPLY_ALREADY_JOINED error code");
			status = Status.OK;
		}
		else if (status == Status.OK)
		{
			setLinkTimeout(client, busAttachment);
		}

		return status;
	}
	
	private void setLinkTimeout(ClientBase clientBase, BusAttachment busAttachment) throws BusException
	{
		Status linkTimeoutstatus = busAttachment.setLinkTimeout(clientBase.getSessionId(), new IntegerValue(LINK_TIMEOUT_IN_SECONDS));

		if (linkTimeoutstatus != Status.OK)
		{
			throw new BusException(String.format("Failed to set link timeout value on bus attachment for session (%d): %s", clientBase.getSessionId(), linkTimeoutstatus));
		}
	}

	public short getNewVolumePropertyValue(short volumeProperty, VolumeRange volumeRange)
	{
		short newVolumeProperty = volumeProperty;

		if (volumeRange.low != volumeRange.high)
		{
			if (volumeProperty == Short.MIN_VALUE || volumeProperty == volumeRange.low)
			{
				newVolumeProperty = (short) (volumeProperty + volumeRange.step);

			}
			else if (volumeProperty == Short.MAX_VALUE || volumeProperty == volumeRange.high)
			{
				newVolumeProperty = (short) (volumeProperty - volumeRange.step);
			}
			else
			{
				newVolumeProperty = (short) (volumeProperty + volumeRange.step);
			}
		}
		else
		{
			logger.info("Volume property cannot be set to a new value since volume range low and high are the same");
		}

		return newVolumeProperty;
	}

	private void registerVolumeControlSignalHandler(String path, VolumeControlSignalHandler volumeControlSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(volumeControlSignalHandler, path);
		serviceHelper.registerSignalHandler(volumeControlSignalHandler);

	}

	private Volume getVolume(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		NodeDetail nodeDetail = busIntrospector.introspect(path);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = path + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, VOLUME_INTERFACE_NAME))
			{
				return busIntrospector.getInterface(subNodePath, Volume.class);
			}
		}

		return null;
	}
	
	private void emitAudioSourceDataSignals(int fifoSize) throws BusException, InterruptedException
	{
		int numberOfDataPackets = fifoSize / DATA_SIZE;

		if (numberOfDataPackets == 0)
		{
			emitAudioSourceDataSignal(new byte[fifoSize]);
		}
		else
		{
			for (int count = 1; count <= numberOfDataPackets; count++)
			{
				emitAudioSourceDataSignal(DATA);
			}
		}
	}
	
	private void assertFifoPositionChangedSignalIsReceived(AudioSinkSignalHandler audioSinkSignalHandler) throws InterruptedException
	{
		assertNotNull("Timed out waiting for FifoPositionChanged signal", audioSinkSignalHandler.waitForNextFifoPositionChangedSignal(getSignalTimeout(), TimeUnit.SECONDS));
	}

	private void assertPlayStateChangedSignalIsReceived(AudioSinkSignalHandler audioSinkSignalHandler, byte oldState, byte newState) throws InterruptedException
	{
		AudioSinkPlayStateChangedSignal audioSinkPlayStateChangedSignal = audioSinkSignalHandler.waitForNextPlayStateChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for PlayStateChanged signal", audioSinkPlayStateChangedSignal);
		//assertEquals(oldState, audioSinkPlayStateChangedSignal.getOldState());
		assertEquals("The state has not change correctly",oldState, audioSinkPlayStateChangedSignal.getOldState());
		//assertEquals(newState, audioSinkPlayStateChangedSignal.getNewState());
		assertEquals("The state has not change correctly",newState, audioSinkPlayStateChangedSignal.getNewState());
	}
	
	private void validateAudioSinkPropertiesCanBeRetrieved(AudioSink audioSink) throws BusException
	{
		audioSink.getFifoSize();
		audioSink.getFifoPosition();
	}
	
	private void emitAudioSourceDataSignal(byte[] data) throws BusException
	{
		AudioSourceObject audioSourceObject = new AudioSourceObject();
		serviceHelper.registerBusObject(audioSourceObject, AUDIO_SOURCE_PATH);
		AudioSource audioSource = createSignalEmitter(audioSourceObject).getInterface(AudioSource.class);
		audioSource.Data((System.currentTimeMillis() * NUMBER_OF_NANOSECONDS_IN_ONE_MILLISECOND) + TIME_DIFFERENCE_IN_NANOSECONDS, data);
	}
	
	private void registerAudioSinkSignalHandler(String path, AudioSinkSignalHandler audioSinkSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(audioSinkSignalHandler, path);
		serviceHelper.registerSignalHandler(audioSinkSignalHandler);
	}
	
	private void connect(Port port, String sourcePath) throws BusException
	{
		Configuration[] configurations = port.getCapabilities();
		port.Connect(serviceHelper.getBusUniqueName(), sourcePath, configurations[0]);
	}

	private void emitImageDataSignal() throws BusException
	{
		ImageSourceObject imageSourceObject = new ImageSourceObject();
		serviceHelper.registerBusObject(imageSourceObject, IMAGE_SOURCE_PATH);
		ImageSource imageSource = createSignalEmitter(imageSourceObject).getInterface(ImageSource.class);
		imageSource.Data(new byte[0]);
	}
	
	private void emitMetadataDataSignal() throws BusException
	{
		MetadataSourceObject metadataSourceObject = new MetadataSourceObject();
		serviceHelper.registerBusObject(metadataSourceObject, METADATA_SOURCE_PATH);
		MetadataSource metadataSource = createSignalEmitter(metadataSourceObject).getInterface(MetadataSource.class);
		metadataSource.Data(constructMetadataSignalData());
	}

	private Map<String, Variant> constructMetadataSignalData()
	{
		Map<String, Variant> dictionary = new HashMap<String, Variant>();
		dictionary.put(ITEM_NAME_KEY, new Variant(ITEM_NAME_VALUE));
		dictionary.put(ALBUM_TITLE_KEY, new Variant(ALBUM_TITLE_VALUE));

		return dictionary;
	}
	
	private Configuration getValidSinkConfiguration()
	{
		Configuration configuration = new Configuration();
		configuration.mediaType = AudioXRaw.getValue();
		configuration.parameters = getSinkConfigurationParameters();

		return configuration;
	}
	
	private Map<String, Variant> getSinkConfigurationParameters()
	{
		Map<String, Variant> parameters = new HashMap<String, Variant>();
		parameters.put(AudioSinkParameter.Channels.name(), new Variant(MANDATORY_CHANNEL_ONE));
		parameters.put(AudioSinkParameter.Format.name(), new Variant(MANDATORY_FORMAT));
		parameters.put(AudioSinkParameter.Rate.name(), new Variant(MANDATORY_RATE_ONE, "q"));

		return parameters;
	}
	
	private Configuration getInvalidSinkConfiguration()
	{
		Configuration configuration = new Configuration();
		configuration.mediaType = AudioXUnknown.getValue();
		configuration.parameters = Collections.emptyMap();

		return configuration;
	}
	
	private void validateConnectingToAudioSinkWithInvalidConfigurationThrowsException(Port audioSinkPort) throws BusException
	{
		try
		{
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getInvalidSinkConfiguration());
			fail("Connecting with invalid configuration must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			logger.info("Exception caught while connecting to stream"+ errorReplyBusException.toString());
		}
	}
	
	private void validateConnectingToAudioSinkTwiceThrowsException(Port audioSinkPort) throws BusException
	{
		audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

		try
		{
			audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());
			fail("Connecting twice to audio sink must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			logger.info("Exception caught while connecting to stream", errorReplyBusException);
		}
	}
	
	private Port getAudioSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		logger.info("Introspecting " + path);
		NodeDetail nodeDetail = busIntrospector.introspect(path);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();
		logger.info("It has this many subnodes: " + introspectionSubNodes.size());

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = path + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, AUDIO_SINK_INTERFACE_NAME))
			{
				return busIntrospector.getInterface(subNodePath, Port.class);
			}
		}

		return null;
	}
	
	private AudioSink getAudioSink(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		NodeDetail nodeDetail = busIntrospector.introspect(path);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = path + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, AUDIO_SINK_INTERFACE_NAME))
			{
				return busIntrospector.getInterface(subNodePath, AudioSink.class);
			}
		}

		return null;
	}
	
	private void validateAudioSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
	{
		assertNotNull("No capabilities found", configurations);
		assertTrue("No capabilities found", configurations.length > 0);
		boolean audioRawCapabilityFound = false;

		for (Configuration configuration : configurations)
		{
			String mediaType = configuration.mediaType;

			if (mediaType.equals(AudioXRaw.getValue()))
			{
				audioRawCapabilityFound = true;
				validateAudioConfigurationStrictly(configuration);
			}
			else if (mediaType.equals(AudioXAlac.getValue()))
			{
				if(icsList.ICSAU_AudioXalac) { //[AT4]
					validateAudioConfigurationStrictly(configuration);
				} else {
					fail("Received mediaType \"audio/x-alac\" and ICSAU_AudioXalac is false");
				}
			}
			else
			{
				validateAudioConfigurationLeniently(configuration);
			}
		}

		assertTrue(String.format("%s media type capability not found", AudioXRaw.getValue()), audioRawCapabilityFound);
	}
	
	private void validateAudioConfigurationStrictly(Configuration configuration) throws BusException
	{
		String mediaType = configuration.mediaType;
		Map<String, Variant> parameters = configuration.parameters;
		Set<String> parameterNames = parameters.keySet();
		assertMandatoryParametersArePresent(parameterNames, mediaType);

		for (String parameterName : parameterNames)
		{
			if (parameterName.equals(AudioSinkParameter.Channels.name()))
			{
				validateChannelsParameterStrictly(parameters, mediaType);
			}
			else if (parameterName.equals(AudioSinkParameter.Format.name()))
			{
				validateFormatParameterStrictly(parameters, mediaType);
			}
			else if (parameterName.equals(AudioSinkParameter.Rate.name()))
			{
				validateRateParameterStrictly(parameters, mediaType);
			}
		}
	}
	
	private void validateAudioConfigurationLeniently(Configuration configuration) throws AnnotationBusException
	{
		String mediaType = configuration.mediaType;
		Map<String, Variant> parameters = configuration.parameters;
		Set<String> parameterNames = parameters.keySet();
		assertTrue(String.format("%s does not match expected media type pattern %s*", mediaType, AudioPrefix.getValue()), mediaType.startsWith(AudioPrefix.getValue()));

		if (parameterNames.contains(AudioSinkParameter.Channels.name()))
		{
			validateChannelsParameterLeniently(parameters, mediaType);
		}

		if (parameterNames.contains(AudioSinkParameter.Format.name()))
		{
			validateFormatParameterLeniently(parameters, mediaType);
		}

		if (parameterNames.contains(AudioSinkParameter.Rate.name()))
		{
			validateRateParameterLeniently(parameters, mediaType);
		}
	}
	
	private void assertMandatoryParametersArePresent(Set<String> parameterNames, String mediaType)
	{
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Channels.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Channels.name()));
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Format.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Format.name()));
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Rate.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Rate.name()));
	}
	
	private void validateRateParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		short[] rates = parameters.get(AudioSinkParameter.Rate.name()).getObject(short[].class);
		assertTrue(String.format("Mandatory rate value %s not found for media type %s", MANDATORY_RATE_ONE, mediaType), isElementPresentInArray(rates, MANDATORY_RATE_ONE));
		assertTrue(String.format("Mandatory rate value %s not found for media type %s", MANDATORY_RATE_TWO, mediaType), isElementPresentInArray(rates, MANDATORY_RATE_TWO));
	}
	
	private void validateFormatParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		String[] formats = parameters.get(AudioSinkParameter.Format.name()).getObject(String[].class);
		assertTrue(String.format("Mandatory format value %s not found for media type %s", MANDATORY_FORMAT, mediaType), isElementPresentInArray(formats, MANDATORY_FORMAT));
	}

	private void validateChannelsParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		byte[] channels = parameters.get(AudioSinkParameter.Channels.name()).getObject(byte[].class);
		assertTrue(String.format("Mandatory channel value %s not found for media type %s", MANDATORY_CHANNEL_ONE, mediaType),
				isElementPresentInArray(channels, MANDATORY_CHANNEL_ONE));
		assertTrue(String.format("Mandatory channel value %s not found for media type %s", MANDATORY_CHANNEL_TWO, mediaType),
				isElementPresentInArray(channels, MANDATORY_CHANNEL_TWO));
	}
	
	private void validateRateParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Rate.name()).getSignature();
		assertTrue(
				String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Rate.name(),
						Rate.getValue(), mediaType), parameterSignature.equals(Rate.getValue()));
	}

	private void validateFormatParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Format.name()).getSignature();
		assertTrue(
				String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Format.name(),
						Format.getValue(), mediaType), parameterSignature.equals(Format.getValue()));
	}

	private void validateChannelsParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Channels.name()).getSignature();
		assertTrue(String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Channels.name(),
				Channels.getValue(), mediaType), parameterSignature.equals(Channels.getValue()));
	}
	
	private boolean isElementPresentInArray(short[] array, short element)
	{
		for (short arrayElement : array)
		{
			if (arrayElement == element)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isElementPresentInArray(String[] array, String element)
	{
		for (String arrayElement : array)
		{
			if (arrayElement.equals(element))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isElementPresentInArray(byte[] array, byte element)
	{
		for (byte arrayElement : array)
		{
			if (arrayElement == element)
			{
				return true;
			}
		}

		return false;
	}
	
	private Port getImageSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		NodeDetail nodeDetail = busIntrospector.introspect(path);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = path + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, IMAGE_SINK_INTERFACE_NAME))
			{
				return busIntrospector.getInterface(subNodePath, Port.class);
			}
		}

		return null;
	}
	
	private void validateImageSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
	{
		assertNotNull("No capabilities found", configurations);
		assertTrue("No capabilities found", configurations.length > 0);

		for (Configuration configuration : configurations)
		{
			String mediaType = configuration.mediaType;
			if(mediaType.equals(ImageJpeg.getValue())) { //[AT4]
				if(icsList.ICSAU_ImageJpeg) {
					logger.info("Received mediaType \"image/jpeg\" and ICSAU_ImageJpeg is true");
					logger.info("Partial Verdict: PASS");
				} else {
					fail("Received mediaType \"image/jpeg\" and ICSAU_ImageJpeg is false");
				}
			} else {
				assertTrue(String.format("%s does not match expected media type pattern %s*", mediaType, ImagePrefix.getValue()), mediaType.startsWith(ImagePrefix.getValue()));
			}
		}
	}
	
	private Port getMetadataSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		NodeDetail nodeDetail = busIntrospector.introspect(path);
		List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();

		for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
		{
			String subNodePath = path + SLASH_CHARACTER + introspectionSubNode.getName();

			if (busIntrospector.isInterfacePresent(subNodePath, METADATA_SINK_INTERFACE_NAME))
			{
				return busIntrospector.getInterface(subNodePath, Port.class);
			}
		}

		return null;
	}
	
	private void validateMetadataSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
	{
		assertNotNull("No capabilities found", configurations);
		assertTrue("No capabilities found", configurations.length > 0);
		assertEquals("Capabilities length does not match for MetadataSink object", 1, configurations.length);

		String mediaType = configurations[0].mediaType;
		assertEquals("Media type does not match", ApplicationXMetadata.getValue(), mediaType);
	}
	
	private void validateStreamInterface(String path, BusIntrospector busIntrospector) throws BusException
	{
		Stream stream = busIntrospector.getInterface(path, Stream.class);
		//assertEquals("Stream Interface version does not match", INTERFACE_VERSION, stream.getVersion());
		assertEquals("Stream Interface version does not match", ixitList.IXITAU_StreamVersion, stream.getVersion());
	}
	
	private  void validatePortInterface(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		Port port = busIntrospector.getInterface(path, Port.class);
		//assertEquals("Port Interface version does not match", INTERFACE_VERSION, port.getVersion());
		assertEquals("Port Interface version does not match", ixitList.IXITAU_PortVersion, port.getVersion());
		assertEquals("Port Interface direction does not match", PORT_DIRECTION, port.getDirection());
		assertMediaTypeSpecificPortInterfaceIsPresent(path, busIntrospector);
	}
	
	private void assertMediaTypeSpecificPortInterfaceIsPresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException,
			SAXException
	{
		if (!isAudioSinkInterfacePresent(path, busIntrospector) && !isImageSinkInterfacePresent(path, busIntrospector) && !isMetadataSinkInterfacePresent(path, busIntrospector))
		{
			fail("Object implementing the Port interface must also implement one of the media-type specific port interfaces");
		}
	}
	
	private boolean isMetadataSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, METADATA_SINK_INTERFACE_NAME);
	}
	
	private boolean isImageSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, IMAGE_SINK_INTERFACE_NAME);
	}

	private boolean isAudioSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, AUDIO_SINK_INTERFACE_NAME);
	}
	
	private void releaseResources()
	{
		disconnectFromAboutClient();

		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}
	
	private void disconnectFromAboutClient()
	{
		if (aboutClient != null)
		{
			aboutClient.disconnect();
			aboutClient = null;
		}
	}

	private void openNewStream(CountDownLatch countDownLatch, OwnershipLostSignalHandler ownershipLostSignalHandler)
	{
		BusAttachmentMgr busAttachmentManager = null;
		AboutClient newAboutClient = null;

		try
		{
			busAttachmentManager = createBusAttachmentManager();
			BusAttachment busAttachment = busAttachmentManager.getBusAttachment();
			ownershipLostSignalHandler.setExpectedNewOwner(busAttachment.getUniqueName());
			newAboutClient = createAboutClient(busAttachment);

			Stream newStream = getIntrospector(busAttachment, newAboutClient).getInterface(streamObjectPath, Stream.class);
			newStream.Open();
			assertTrue("Did not receive expected OwnershipLost signal", countDownLatch.await(getSignalTimeout(), TimeUnit.SECONDS));
		}
		catch (Exception exception)
		{
			logger.error("Exception caught while executing test: ", exception);
		}
		finally
		{
			if (newAboutClient != null)
			{
				newAboutClient.disconnect();
			}

			if (busAttachmentManager != null)
			{
				busAttachmentManager.release();
			}
		}
	}
	
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * assertNotNull
	 * assertNull
	 * 
	 * */
	private void assertEquals(String errorMessage, short first, short second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second))
		{
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, byte first, byte second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, boolean first, boolean second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}
	
	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition)
		{
			fail(errorMessage);
		}
	}

	private void assertNotNull(String errorMessage, Object object)
	{

		if (object == null)
		{
			fail(errorMessage);
		}
	}

	private void assertNull(String errorMessage, Object object)
	{
		if (object != null)
		{
			fail(errorMessage);
		}
	}

	/**
	 * [AT4] Added methods to manage the verdict
	 * 
	 * fail
	 * getFinalVerdict
	 * 
	 *  */
	private void fail(String msg)
	{
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass = false;
	}

	public String getFinalVerdict()
	{
		if (inconc)
		{
			return "INCONC";
		}
		
		if (pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
	}
}
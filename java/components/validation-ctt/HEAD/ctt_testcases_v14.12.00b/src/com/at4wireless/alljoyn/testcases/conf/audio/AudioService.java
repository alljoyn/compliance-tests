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
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionSubNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;




// TODO: Auto-generated Javadoc
/**
 * The Class AudioService.
 */
public class AudioService {


	/** The pass. */
	Boolean pass=true;
	
	/** The inconc. */
	Boolean inconc=false;
	 
	/** The tag. */
	private  final String TAG = "AudioTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The time out. */
	private  int  timeOut=30;

/** The port. */
private  short port=91;

	/** The data size. */
	private  final int DATA_SIZE = 16 * 1024;
	
	/** The data. */
	private  final byte[] DATA = new byte[DATA_SIZE];
	
	/** The invalid high volume. */
	private  final int INVALID_HIGH_VOLUME = 32767;
	
	/** The invalid low volume. */
	private  final int INVALID_LOW_VOLUME = -32768;
	
	/** The volume interface name. */
	private  final String VOLUME_INTERFACE_NAME = "org.alljoyn.Control.Volume";
	
	/** The audio sink interface name. */
	private  final String AUDIO_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.AudioSink";
	
	/** The image sink interface name. */
	private  final String IMAGE_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.ImageSink";
	
	/** The metadata sink interface name. */
	private  final String METADATA_SINK_INTERFACE_NAME = "org.alljoyn.Stream.Port.Application.MetadataSink";
	
	/** The port interface name. */
	private  final String PORT_INTERFACE_NAME = "org.alljoyn.Stream.Port";
	
	/** The er bus reply is error message. */
	private  final String ER_BUS_REPLY_IS_ERROR_MESSAGE = "ER_BUS_REPLY_IS_ERROR_MESSAGE";
	
	/** The time difference in nanoseconds. */
	private  final int TIME_DIFFERENCE_IN_NANOSECONDS = 1000000000;
	
	/** The number of nanoseconds in one millisecond. */
	private  final int NUMBER_OF_NANOSECONDS_IN_ONE_MILLISECOND = 1000000;
	
	/** The album title value. */
	private  final String ALBUM_TITLE_VALUE = "album title";
	
	/** The album title key. */
	private  final String ALBUM_TITLE_KEY = "Album";
	
	/** The item name value. */
	private  final String ITEM_NAME_VALUE = "item name";
	
	/** The item name key. */
	private  final String ITEM_NAME_KEY = "Name";
	
	/** The metadata source path. */
	private  final String METADATA_SOURCE_PATH = "/Player/Out/Metadata";
	
	/** The image source path. */
	private  final String IMAGE_SOURCE_PATH = "/Player/Out/Image";
	
	/** The audio source path. */
	private  final String AUDIO_SOURCE_PATH = "/Player/Out/Audio";
	
	/** The mandatory rate one. */
	private  final short MANDATORY_RATE_ONE = -21436;
	
	/** The mandatory rate two. */
	private  final short MANDATORY_RATE_TWO = -17536;
	
	/** The mandatory format. */
	private  final String MANDATORY_FORMAT = "s16le";
	
	/** The mandatory channel one. */
	private  final byte MANDATORY_CHANNEL_ONE = 1;
	
	/** The mandatory channel two. */
	private  final byte MANDATORY_CHANNEL_TWO = 2;
	//private  final short INTERFACE_VERSION = 1;
	/** The slash character. */
	private  final String SLASH_CHARACTER = "/";
	
	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "Audio";
	
	/** The signal timeout in seconds. */
	private  long SIGNAL_TIMEOUT_IN_SECONDS = 30;
	
	/** The link timeout in seconds. */
	private  int LINK_TIMEOUT_IN_SECONDS = 120;
	
	/** The port direction. */
	private  final byte PORT_DIRECTION = 1;

	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper;
	
	/** The stream object path. */
	private  String streamObjectPath;
	
	/** The stream. */
	private  Stream stream;

	/*private  UUID dutAppId;
	private  String dutDeviceId;*/

	 /** The ICSA u_ audio service framework. */
	boolean ICSAU_AudioServiceFramework=false;
	 
 	/** The ICSA u_ stream interface. */
 	boolean ICSAU_StreamInterface=false;
	 
 	/** The ICSA u_ stream port interface. */
 	boolean ICSAU_StreamPortInterface=false;
	 
 	/** The ICSA u_ port audio sink interface. */
 	boolean ICSAU_PortAudioSinkInterface=false;
	 
 	/** The ICSA u_ port audio source interface. */
 	boolean ICSAU_PortAudioSourceInterface=false;
	 
 	/** The ICSA u_ port image sink interface. */
 	boolean ICSAU_PortImageSinkInterface=false;
	 
 	/** The ICSA u_ port image source interface. */
 	boolean ICSAU_PortImageSourceInterface=false;
	 
 	/** The ICSA u_ port application metadata sink interface. */
 	boolean ICSAU_PortApplicationMetadataSinkInterface=false;
	 
 	/** The ICSA u_ port application metadata source interface. */
 	boolean ICSAU_PortApplicationMetadataSourceInterface=false;
	 
 	/** The ICSA u_ control volume interface. */
 	boolean ICSAU_ControlVolumeInterface=false;

	 /** The ICSA u_ stream clock interface. */
 	boolean ICSAU_StreamClockInterface=false;

	 /** The ICSA u_ audio xalac. */
 	boolean ICSAU_AudioXalac=false;
	 
 	/** The ICSA u_ image jpeg. */
 	boolean ICSAU_ImageJpeg=false;
	 
 	/** The ICSA u_ application xmetadata. */
 	boolean ICSAU_ApplicationXmetadata=false;
	 
 	/** The ICSA u_ volume control. */
 	boolean ICSAU_VolumeControl=false;
	//
	 /*String IXITCO_AppId=null;
	 String IXITCO_DefaultLanguage=null;
	 String IXITCO_DeviceId=null;
	//

	 String IXITAU_StreamVersion=null;
	 String IXITAU_TestObjectPath=null;
	 String IXITAU_PortVersion=null;
	 String IXITAU_AudioSinkVersion=null;
	 String IXITAU_timeNanos=null;
	 String IXITAU_AudioSourceVersion=null;
	 String IXITAU_ImageSinkVersion=null;
	 String IXITAU_ImageSourceVersion=null;
	 String IXITAU_MetadataSinkVersion=null;

	 String IXITAU_MetadataSourceVersion=null;

	 String IXITAU_ControlVolumeVersion=null;
	 String IXITAU_delta=null;
	 String IXITAU_change=null;
	 String IXITAU_ClockVersion=null;
	 String IXITAU_adjustNanos=null;*/
	 
	 /** The ixit. */
	Map<String,String> ixit;

	/**
	 * Instantiates a new audio service.
	 *
	 * @param testCase the test case
	 * @param iCSAU_AudioServiceFramework the i csa u_ audio service framework
	 * @param iCSAU_StreamInterface the i csa u_ stream interface
	 * @param iCSAU_StreamPortInterface the i csa u_ stream port interface
	 * @param iCSAU_PortAudioSinkInterface the i csa u_ port audio sink interface
	 * @param iCSAU_PortAudioSourceInterface the i csa u_ port audio source interface
	 * @param iCSAU_PortImageSinkInterface the i csa u_ port image sink interface
	 * @param iCSAU_PortImageSourceInterface the i csa u_ port image source interface
	 * @param iCSAU_PortApplicationMetadataSinkInterface the i csa u_ port application metadata sink interface
	 * @param iCSAU_PortApplicationMetadataSourceInterface the i csa u_ port application metadata source interface
	 * @param iCSAU_ControlVolumeInterface the i csa u_ control volume interface
	 * @param iCSAU_StreamClockInterface the i csa u_ stream clock interface
	 * @param iCSAU_AudioXalac the i csa u_ audio xalac
	 * @param iCSAU_ImageJpeg the i csa u_ image jpeg
	 * @param iCSAU_ApplicationXmetadata the i csa u_ application xmetadata
	 * @param iCSAU_VolumeControl the i csa u_ volume control
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITAU_StreamVersion the i xita u_ stream version
	 * @param iXITAU_TestObjectPath the i xita u_ test object path
	 * @param iXITAU_PortVersion the i xita u_ port version
	 * @param iXITAU_AudioSinkVersion the i xita u_ audio sink version
	 * @param iXITAU_timeNanos the i xita u_time nanos
	 * @param iXITAU_AudioSourceVersion the i xita u_ audio source version
	 * @param iXITAU_ImageSinkVersion the i xita u_ image sink version
	 * @param iXITAU_ImageSourceVersion the i xita u_ image source version
	 * @param iXITAU_MetadataSinkVersion the i xita u_ metadata sink version
	 * @param iXITAU_MetadataSourceVersion the i xita u_ metadata source version
	 * @param iXITAU_ControlVolumeVersion the i xita u_ control volume version
	 * @param iXITAU_delta the i xita u_delta
	 * @param iXITAU_change the i xita u_change
	 * @param iXITAU_ClockVersion the i xita u_ clock version
	 * @param iXITAU_adjustNanos the i xita u_adjust nanos
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPAU_Signal the g pa u_ signal
	 * @param gPAU_Link the g pa u_ link
	 */
	public AudioService(String testCase, boolean iCSAU_AudioServiceFramework,
			boolean iCSAU_StreamInterface, boolean iCSAU_StreamPortInterface,
			boolean iCSAU_PortAudioSinkInterface,
			boolean iCSAU_PortAudioSourceInterface,
			boolean iCSAU_PortImageSinkInterface,
			boolean iCSAU_PortImageSourceInterface,
			boolean iCSAU_PortApplicationMetadataSinkInterface,
			boolean iCSAU_PortApplicationMetadataSourceInterface,
			boolean iCSAU_ControlVolumeInterface,
			boolean iCSAU_StreamClockInterface, boolean iCSAU_AudioXalac,
			boolean iCSAU_ImageJpeg, boolean iCSAU_ApplicationXmetadata,
			boolean iCSAU_VolumeControl, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITAU_StreamVersion, String iXITAU_TestObjectPath,
			String iXITAU_PortVersion, String iXITAU_AudioSinkVersion,
			String iXITAU_timeNanos, String iXITAU_AudioSourceVersion,
			String iXITAU_ImageSinkVersion, String iXITAU_ImageSourceVersion,
			String iXITAU_MetadataSinkVersion,
			String iXITAU_MetadataSourceVersion,
			String iXITAU_ControlVolumeVersion, String iXITAU_delta,
			String iXITAU_change, String iXITAU_ClockVersion,
			String iXITAU_adjustNanos, String gPCO_AnnouncementTimeout,
			String gPAU_Signal, String gPAU_Link) {


		ICSAU_AudioServiceFramework=iCSAU_AudioServiceFramework;
		ICSAU_StreamInterface=iCSAU_StreamInterface;
		ICSAU_StreamPortInterface=iCSAU_StreamPortInterface;
		ICSAU_PortAudioSinkInterface=iCSAU_PortAudioSinkInterface;
		ICSAU_PortAudioSourceInterface=iCSAU_PortAudioSourceInterface;
		ICSAU_PortImageSinkInterface=iCSAU_PortImageSinkInterface;
		ICSAU_PortImageSourceInterface=iCSAU_PortImageSourceInterface;
		ICSAU_PortApplicationMetadataSinkInterface=iCSAU_PortApplicationMetadataSinkInterface;
		ICSAU_PortApplicationMetadataSourceInterface=iCSAU_PortApplicationMetadataSourceInterface;
		ICSAU_ControlVolumeInterface=iCSAU_ControlVolumeInterface;
		ICSAU_StreamClockInterface=iCSAU_StreamClockInterface;
		ICSAU_AudioXalac=iCSAU_AudioXalac;
		ICSAU_ImageJpeg=iCSAU_ImageJpeg;
		ICSAU_ApplicationXmetadata=iCSAU_ApplicationXmetadata;
		ICSAU_VolumeControl=iCSAU_VolumeControl;
		/*IXITCO_AppId=iXITCO_AppId;
		IXITCO_DeviceId=iXITCO_DeviceId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITAU_StreamVersion=iXITAU_StreamVersion;
		IXITAU_TestObjectPath=iXITAU_TestObjectPath;
		IXITAU_PortVersion=iXITAU_PortVersion;
		IXITAU_AudioSinkVersion=iXITAU_AudioSinkVersion;
		IXITAU_timeNanos=iXITAU_timeNanos;
		IXITAU_AudioSourceVersion=iXITAU_AudioSourceVersion;
		IXITAU_ImageSinkVersion=iXITAU_ImageSinkVersion;
		IXITAU_ImageSourceVersion=iXITAU_ImageSourceVersion;
		IXITAU_MetadataSinkVersion=iXITAU_MetadataSinkVersion;
		IXITAU_MetadataSourceVersion=iXITAU_MetadataSourceVersion;
		IXITAU_ControlVolumeVersion=iXITAU_ControlVolumeVersion;
		IXITAU_delta=iXITAU_delta;
		IXITAU_change=iXITAU_change;
		IXITAU_ClockVersion=iXITAU_ClockVersion;
		IXITAU_adjustNanos=iXITAU_adjustNanos;*/


		ixit = new HashMap<String,String>();
		ixit.put("IXITCO_AppId",iXITCO_AppId);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITAU_StreamVersion", iXITAU_StreamVersion);
		ixit.put("IXITAU_PortVersion",iXITAU_PortVersion);
		ixit.put("IXITAU_TestObjectPath", iXITAU_TestObjectPath);
		ixit.put("IXITAU_AudioSinkVersion", iXITAU_AudioSinkVersion);
		ixit.put("IXITAU_AudioSourceVersion", iXITAU_AudioSourceVersion);
		ixit.put("IXITAU_timeNanos", iXITAU_timeNanos);
		ixit.put("IXITAU_ImageSinkVersion", iXITAU_ImageSinkVersion);
		ixit.put("IXITAU_ImageSourceVersion", iXITAU_ImageSourceVersion);
		ixit.put("IXITAU_MetadataSinkVersion", iXITAU_MetadataSinkVersion);
		ixit.put("IXITAU_MetadataSourceVersion", iXITAU_MetadataSourceVersion);
		ixit.put("IXITAU_ControlVolumeVersion", iXITAU_ControlVolumeVersion);
		ixit.put("IXITAU_delta", iXITAU_delta);
		ixit.put("IXITAU_change", iXITAU_change);
		ixit.put("IXITAU_ClockVersion", iXITAU_ClockVersion);
		ixit.put("IXITAU_adjustNanos", iXITAU_adjustNanos);
		
		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		SIGNAL_TIMEOUT_IN_SECONDS = Integer.parseInt(gPAU_Signal);
		LINK_TIMEOUT_IN_SECONDS = Integer.parseInt(gPAU_Link);
		

		try{
			runTestCase(testCase);
		}catch(Exception e){
			inconc=true;
			if(e.getMessage().equals("Timed out waiting for About announcement")){
				fail("Timed out waiting for About announcement");
			}else{
				fail("Exception: "+e.toString());
			}
		}
	}


	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
	public  void runTestCase(String testCase) throws Exception{
		//setUp(IXITCO_DeviceId,IXITCO_AppId);
		setUp();
		logger.info("Running testcase: "+testCase);

		if(testCase.equals("Audio-v1-01")){
			testAudio_v1_01_ValidateStreamObjects();
		}else if(testCase.equals("Audio-v1-02")){
			testAudio_v1_02_OpenStreamObject();
		}else if(testCase.equals("Audio-v1-03")){
			testAudio_v1_03_OpenAndCloseStreamObject();
		}else if(testCase.equals("Audio-v1-04")){
			testAudio_v1_04_CloseUnopenedStreamObject();
		}else if(testCase.equals("Audio-v1-05")){
			testAudio_v1_05_VerifyAudioSinkCapabilities();
		}else if(testCase.equals("Audio-v1-06")){
			testAudio_v1_06_VerifyImageSinkCapabilities();
		}else if(testCase.equals("Audio-v1-07")){
			testAudio_v1_07_VerifyApplicationMetadataCapabilities();
		}else if(testCase.equals("Audio-v1-08")){
			testAudio_v1_08_ConfigureAudioSinkPort();
		}else if(testCase.equals("Audio-v1-09")){
			testAudio_v1_09_ConfigureAudioSinkPortWithInvalidConfiguration();
		}else if(testCase.equals("Audio-v1-10")){
			testAudio_v1_10_ConfigureAudioSinkPortTwice();
		}else if(testCase.equals("Audio-v1-11")){
			testAudio_v1_11_CheckOwnershipLostSignal();
		}else if(testCase.equals("Audio-v1-12")){
			testAudio_v1_12_PlaybackAudioSink();
		}else if(testCase.equals("Audio-v1-13")){
			testAudio_v1_13_PauseAudioSinkPlayback();
		}else if(testCase.equals("Audio-v1-14")){
			testAudio_v1_14_FlushPausedAudioSink();
		}else if(testCase.equals("Audio-v1-15")){
			testAudio_v1_15_FlushPlayingAudioSink();
		}else if(testCase.equals("Audio-v1-16")){
			testAudio_v1_16_VerifyPausedAudioSinkRemainsPausedAfterSendingData();
		}else if(testCase.equals("Audio-v1-17")){
			testAudio_v1_17_VerifyPlayingEmptyAudioSinkRemainsIdle();
		}else if(testCase.equals("Audio-v1-18")){
			testAudio_v1_18_FlushIdleAudioSink();
		}else if(testCase.equals("Audio-v1-19")){
			testAudio_v1_19_SendDataToImageSink();
		}else if(testCase.equals("Audio-v1-20")){
			testAudio_v1_20_SendDataToMetadataSink();
		}else if(testCase.equals("Audio-v1-21")){
			testAudio_v1_21_VerifyAudioSinkCanBeMutedUnmuted();
		}else if(testCase.equals("Audio-v1-22")){
			testAudio_v1_22_VerifyVolumeCanBeSetOnAudioSink();
		}else if(testCase.equals("Audio-v1-23")){
			testAudio_v1_23_SetInvalidVolumeOnAudioSink();
		}else if(testCase.equals("Audio-v1-24")){
			testAudio_v1_24_VerifyIndependenceOfMuteAndVolumeOnAudioSink();
		}else if(testCase.equals("Audio-v1-25")){
			testAudio_v1_25_SynchronizeClocksOnAudioSink();
		}else if(testCase.equals("Audio-v1-26")){
			testAudio_v1_26_VerifyVolumeCanBeAdjustedOnAudioSink();
		}else if(testCase.equals("Audio-v1-27")){
			testAudio_v1_27_VerifyVolumeCanBeAdjustedByPercentOnAudioSink();
		}else if(testCase.equals("Audio-v1-28")){
			testAudio_v1_28_VerifyVolumeNotAdjustableWhenDisabled();
		}else {
			fail("TestCase not valid");
		}

		tearDown();
	}


	/**
	 * Test audio_v1_01_ validate stream objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_01_ValidateStreamObjects() throws Exception
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




	/**
	 * Test audio_v1_02_ open stream object.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_02_OpenStreamObject() throws Exception
	{
		stream.Open();
	}




	/**
	 * Test audio_v1_03_ open and close stream object.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_03_OpenAndCloseStreamObject() throws Exception
	{
		stream.Open();
		stream.Close();
	}


	/**
	 * Test audio_v1_04_ close unopened stream object.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_04_CloseUnopenedStreamObject() throws Exception
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



	/**
	 * Test audio_v1_05_ verify audio sink capabilities.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_05_VerifyAudioSinkCapabilities() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			fail("Stream does not support AudioSink!");
			return;
		}
		else
		{
			stream.Open();
			validateAudioSinkCapabilities(audioSinkPort.getCapabilities());
		}
	}




	/**
	 * Test audio_v1_06_ verify image sink capabilities.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_06_VerifyImageSinkCapabilities() throws Exception
	{
		Port imageSinkPort = getImageSinkPort(streamObjectPath, getIntrospector());

		if (imageSinkPort == null)
		{
			fail("Stream does not support ImageSink!");
			return;
		}
		else
		{
			stream.Open();
			validateImageSinkCapabilities(imageSinkPort.getCapabilities());
		}
	}



	/**
	 * Test audio_v1_07_ verify application metadata capabilities.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_07_VerifyApplicationMetadataCapabilities() throws Exception
	{
		Port metadataSinkPort = getMetadataSinkPort(streamObjectPath, getIntrospector());

		if (metadataSinkPort == null)
		{
			fail("Stream does not support ApplicationMetadataSink!");
			return;
		}
		else
		{
			stream.Open();
			validateMetadataSinkCapabilities(metadataSinkPort.getCapabilities());
		}
	}



	/**
	 * Test audio_v1_08_ configure audio sink port.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_08_ConfigureAudioSinkPort() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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



	/**
	 * Test audio_v1_09_ configure audio sink port with invalid configuration.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_09_ConfigureAudioSinkPortWithInvalidConfiguration() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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



	/**
	 * Test audio_v1_10_ configure audio sink port twice.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_10_ConfigureAudioSinkPortTwice() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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


	/**
	 * Test audio_v1_11_ check ownership lost signal.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_11_CheckOwnershipLostSignal() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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




	/**
	 * Test audio_v1_12_ playback audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_12_PlaybackAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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





	/**
	 * Test audio_v1_13_ pause audio sink playback.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_13_PauseAudioSinkPlayback() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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




	/**
	 * Test audio_v1_14_ flush paused audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_14_FlushPausedAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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






	/**
	 * Test audio_v1_15_ flush playing audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_15_FlushPlayingAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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



	/**
	 * Test audio_v1_16_ verify paused audio sink remains paused after sending data.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_16_VerifyPausedAudioSinkRemainsPausedAfterSendingData() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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


	/**
	 * Test audio_v1_17_ verify playing empty audio sink remains idle.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_17_VerifyPlayingEmptyAudioSinkRemainsIdle() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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



	/**
	 * Test audio_v1_18_ flush idle audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_18_FlushIdleAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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




	/**
	 * Test audio_v1_19_ send data to image sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_19_SendDataToImageSink() throws Exception
	{
		Port imageSinkPort = getImageSinkPort(streamObjectPath, getIntrospector());

		if (imageSinkPort == null)
		{
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




	/**
	 * Test audio_v1_20_ send data to metadata sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_20_SendDataToMetadataSink() throws Exception
	{
		Port metadataSinkPort = getMetadataSinkPort(streamObjectPath, getIntrospector());

		if (metadataSinkPort == null)
		{
			fail("Stream does not support MetadataSink!");
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




	/**
	 * Test audio_v1_21_ verify audio sink can be muted unmuted.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_21_VerifyAudioSinkCanBeMutedUnmuted() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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




	/**
	 * Test audio_v1_22_ verify volume can be set on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_22_VerifyVolumeCanBeSetOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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


	/**
	 * Test audio_v1_23_ set invalid volume on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_23_SetInvalidVolumeOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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







	/**
	 * Test audio_v1_24_ verify independence of mute and volume on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_24_VerifyIndependenceOfMuteAndVolumeOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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



	/**
	 * Test audio_v1_25_ synchronize clocks on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_25_SynchronizeClocksOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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


	/**
	 * Test audio_v1_26_ verify volume can be adjusted on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_26_VerifyVolumeCanBeAdjustedOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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




	/**
	 * Test audio_v1_27_ verify volume can be adjusted by percent on audio sink.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_27_VerifyVolumeCanBeAdjustedByPercentOnAudioSink() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
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





	/**
	 * Test audio_v1_28_ verify volume not adjustable when disabled.
	 *
	 * @throws Exception the exception
	 */
	public  void testAudio_v1_28_VerifyVolumeNotAdjustableWhenDisabled() throws Exception
	{
		Port audioSinkPort = getAudioSinkPort(streamObjectPath, getIntrospector());

		if (audioSinkPort == null)
		{
			fail("Stream does not support AudioSink!");
			return;
		}

		stream.Open();
		audioSinkPort.Connect(serviceHelper.getBusUniqueName(), AUDIO_SOURCE_PATH, getValidSinkConfiguration());

		Volume volume = getVolume(streamObjectPath, getIntrospector());
		boolean enabled = volume.getEnabled();
		if (enabled)
		{
			String[] msg={"Continue"};
			new UserInputDetails("Disable Device", "Please switch the audio device to disable if this option is supported",msg);
			if (enabled)
			{
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
		assertTrue("It must return errors when in disabled mode",caughtSomething);

		caughtSomething = false;
		try
		{
			volume.AdjustVolume((short) 5);
		}
		catch (BusException e)
		{
			caughtSomething = true;
		}
		assertTrue("It must return errors when in disabled mode",caughtSomething);

		caughtSomething = false;
		try
		{
			volume.AdjustVolumePercent(1.0);
		}
		catch (BusException e)
		{
			caughtSomething = true;
		}
		assertTrue("It must return errors when in disabled mode",caughtSomething);
		String[] msg={"Continue"};
		new UserInputDetails("Enable Device", "Please enable the volume control", msg);
	}





	/**
	 * Gets the clock.
	 *
	 * @param path the path
	 * @return the clock
	 */
	private  Clock getClock(String path)
	{
		Clock clock = null;
		try
		{
			clock = getIntrospector().getInterface(path, Clock.class);
			assertEquals("The clock Version is not: "+ixit.get("IXITAU_ClockVersion"),ixit.get("IXITAU_ClockVersion"), clock.getVersion());
		}
		catch (Exception e)
		{
			logger.info("Stream does not support Clock!");
		}
		return clock;
	}


	 /**
 	 * Gets the mid range volume property.
 	 *
 	 * @param volumeRange the volume range
 	 * @return the mid range volume property
 	 */
 	short getMidRangeVolumeProperty(VolumeRange volumeRange)
	{
		return (short) ((short) volumeRange.low + (((volumeRange.high - volumeRange.low) / volumeRange.step / 2) * volumeRange.step));
	}

	/**
	 * Sets the invalid volume value.
	 *
	 * @param volume the volume
	 * @param newVolumeProperty the new volume property
	 */
	private  void setInvalidVolumeValue(Volume volume, short newVolumeProperty)
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
			assertEquals("The error is not: ER_BUS_REPLY_IS_ERROR_MESSAGE",ER_BUS_REPLY_IS_ERROR_MESSAGE, busException.getMessage());
		}
	}








	/**
	 * Gets the new volume property value.
	 *
	 * @param volumeProperty the volume property
	 * @param volumeRange the volume range
	 * @return the new volume property value
	 */
	public  short getNewVolumePropertyValue(short volumeProperty, VolumeRange volumeRange)
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


	/**
	 * Sets the volume property.
	 *
	 * @param volume the volume
	 * @param volumeProperty the volume property
	 * @throws BusException the bus exception
	 */
	private  void setVolumeProperty(Volume volume, short volumeProperty) throws BusException
	{
		volume.setVolume(volumeProperty);
		assertEquals("The volume has not been set correctly",volumeProperty, volume.getVolume());
	}

	/**
	 * Validate original volume property.
	 *
	 * @param high the high
	 * @param low the low
	 * @param step the step
	 * @param originalVolumeProperty the original volume property
	 */
	private  void validateOriginalVolumeProperty(short high, short low, short step, short originalVolumeProperty)
	{
		if (originalVolumeProperty < low || originalVolumeProperty > high || ((originalVolumeProperty - low) % step) != 0)
		{
			fail("Current volume value " + originalVolumeProperty + " is not within Volume Range properties of low: " + low + " and high: " + high + " and step: " + step);
		}
	}

	/**
	 * Validate volume range properties.
	 *
	 * @param high the high
	 * @param low the low
	 * @param step the step
	 */
	private  void validateVolumeRangeProperties(short high, short low, short step)
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

	/**
	 * Assert volume changed signal is received.
	 *
	 * @param newVolume the new volume
	 * @param volumeControlSignalHandler the volume control signal handler
	 * @throws InterruptedException the interrupted exception
	 */
	private  void assertVolumeChangedSignalIsReceived(short newVolume, VolumeControlSignalHandler volumeControlSignalHandler) throws InterruptedException
	{
		Short volumeChangedSignalValue = volumeControlSignalHandler.waitForNextVolumeChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for VolumeChanged signal", volumeChangedSignalValue);
		assertEquals("Volume changed signal has not been received",newVolume, volumeChangedSignalValue.shortValue());
	}


	/**
	 * Invert mute value on volume.
	 *
	 * @param volume the volume
	 * @param volumeControlSignalHandler the volume control signal handler
	 * @throws BusException the bus exception
	 * @throws InterruptedException the interrupted exception
	 */
	private  void invertMuteValueOnVolume(Volume volume, VolumeControlSignalHandler volumeControlSignalHandler) throws BusException, InterruptedException
	{
		boolean volumeMuteProperty = getMutePropertyOnVolume(volume);
		boolean muteInverted = invertMuteProperty(volumeMuteProperty);

		setMuteProperty(volume, muteInverted);
		Boolean muteChangedSignalValue = volumeControlSignalHandler.waitForNextMuteChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for MuteChanged signal", muteChangedSignalValue);
		assertEquals("Mute has not been changed correctly",muteInverted, muteChangedSignalValue.booleanValue());
	}








	/**
	 * Sets the mute property.
	 *
	 * @param volume the volume
	 * @param mute the mute
	 * @throws BusException the bus exception
	 */
	private  void setMuteProperty(Volume volume, boolean mute) throws BusException
	{
		volume.setMute(mute);
		assertEquals("Mute has not been set correctly",mute, volume.getMute());
	}


	/**
	 * Invert mute property.
	 *
	 * @param volumeMuteProperty the volume mute property
	 * @return true, if successful
	 */
	private  boolean invertMuteProperty(boolean volumeMuteProperty)
	{
		boolean muteInverted = !volumeMuteProperty;
		logger.info("Inverted property value : " + muteInverted);
		return muteInverted;
	}

	/**
	 * Gets the mute property on volume.
	 *
	 * @param volume the volume
	 * @return the mute property on volume
	 * @throws BusException the bus exception
	 */
	private  boolean getMutePropertyOnVolume(Volume volume) throws BusException
	{
		boolean volumeMuteProperty = volume.getMute();
		logger.info("Volume Interface returned Mute property value as : " + volumeMuteProperty);
		return volumeMuteProperty;
	}

	/**
	 * Register volume control signal handler.
	 *
	 * @param path the path
	 * @param volumeControlSignalHandler the volume control signal handler
	 * @throws BusException the bus exception
	 */
	private  void registerVolumeControlSignalHandler(String path, VolumeControlSignalHandler volumeControlSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(volumeControlSignalHandler, path);
		serviceHelper.registerSignalHandler(volumeControlSignalHandler);

	}


	 /**
 	 * Creates the volume control signal handler.
 	 *
 	 * @return the volume control signal handler
 	 */
 	VolumeControlSignalHandler createVolumeControlSignalHandler()
	{
		return new VolumeControlSignalHandler();
	}


	/**
	 * Gets the volume.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return the volume
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  Volume getVolume(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
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

	/**
	 * Emit metadata data signal.
	 *
	 * @throws BusException the bus exception
	 */
	private  void emitMetadataDataSignal() throws BusException
	{
		MetadataSourceObject metadataSourceObject = new MetadataSourceObject();
		serviceHelper.registerBusObject(metadataSourceObject, METADATA_SOURCE_PATH);
		MetadataSource metadataSource = createSignalEmitter(metadataSourceObject).getInterface(MetadataSource.class);
		metadataSource.Data(constructMetadataSignalData());
	}



	/**
	 * Construct metadata signal data.
	 *
	 * @return the map
	 */
	private  Map<String, Variant> constructMetadataSignalData()
	{
		Map<String, Variant> dictionary = new HashMap<String, Variant>();
		dictionary.put(ITEM_NAME_KEY, new Variant(ITEM_NAME_VALUE));
		dictionary.put(ALBUM_TITLE_KEY, new Variant(ALBUM_TITLE_VALUE));

		return dictionary;
	}


	/**
	 * Connect.
	 *
	 * @param port the port
	 * @param sourcePath the source path
	 * @throws BusException the bus exception
	 */
	private  void connect(Port port, String sourcePath) throws BusException
	{
		Configuration[] configurations = port.getCapabilities();
		port.Connect(serviceHelper.getBusUniqueName(), sourcePath, configurations[0]);
	}

	/**
	 * Emit image data signal.
	 *
	 * @throws BusException the bus exception
	 */
	private  void emitImageDataSignal() throws BusException
	{
		ImageSourceObject imageSourceObject = new ImageSourceObject();
		serviceHelper.registerBusObject(imageSourceObject, IMAGE_SOURCE_PATH);
		ImageSource imageSource = createSignalEmitter(imageSourceObject).getInterface(ImageSource.class);
		imageSource.Data(new byte[0]);
	}



	/**
	 * Assert fifo position changed signal is received.
	 *
	 * @param audioSinkSignalHandler the audio sink signal handler
	 * @throws InterruptedException the interrupted exception
	 */
	private  void assertFifoPositionChangedSignalIsReceived(AudioSinkSignalHandler audioSinkSignalHandler) throws InterruptedException
	{
		assertNotNull("Timed out waiting for FifoPositionChanged signal", audioSinkSignalHandler.waitForNextFifoPositionChangedSignal(getSignalTimeout(), TimeUnit.SECONDS));
	}

	/**
	 * Assert play state changed signal is received.
	 *
	 * @param audioSinkSignalHandler the audio sink signal handler
	 * @param oldState the old state
	 * @param newState the new state
	 * @throws InterruptedException the interrupted exception
	 */
	private  void assertPlayStateChangedSignalIsReceived(AudioSinkSignalHandler audioSinkSignalHandler, byte oldState, byte newState) throws InterruptedException
	{
		AudioSinkPlayStateChangedSignal audioSinkPlayStateChangedSignal = audioSinkSignalHandler.waitForNextPlayStateChangedSignal(getSignalTimeout(), TimeUnit.SECONDS);
		assertNotNull("Timed out waiting for PlayStateChanged signal", audioSinkPlayStateChangedSignal);
		assertEquals("The state has not change correctly",oldState, audioSinkPlayStateChangedSignal.getOldState());
		assertEquals("The state has not change correctly",newState, audioSinkPlayStateChangedSignal.getNewState());
	}

	/**
	 * Emit audio source data signals.
	 *
	 * @param fifoSize the fifo size
	 * @throws BusException the bus exception
	 * @throws InterruptedException the interrupted exception
	 */
	private  void emitAudioSourceDataSignals(int fifoSize) throws BusException, InterruptedException
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



	/**
	 * Emit audio source data signal.
	 *
	 * @param data the data
	 * @throws BusException the bus exception
	 */
	private  void emitAudioSourceDataSignal(byte[] data) throws BusException
	{
		AudioSourceObject audioSourceObject = new AudioSourceObject();
		serviceHelper.registerBusObject(audioSourceObject, AUDIO_SOURCE_PATH);
		AudioSource audioSource = createSignalEmitter(audioSourceObject).getInterface(AudioSource.class);
		audioSource.Data((System.currentTimeMillis() * NUMBER_OF_NANOSECONDS_IN_ONE_MILLISECOND) + TIME_DIFFERENCE_IN_NANOSECONDS, data);
	}



	 /**
 	 * Creates the signal emitter.
 	 *
 	 * @param busObject the bus object
 	 * @return the signal emitter
 	 */
 	SignalEmitter createSignalEmitter(BusObject busObject)
	{
		SignalEmitter signalEmitter = new SignalEmitter(busObject, aboutClient.getSessionId(), SignalEmitter.GlobalBroadcast.Off);
		signalEmitter.setSessionlessFlag(false);
		signalEmitter.setTimeToLive(0);

		return signalEmitter;
	}
	
	/**
	 * Register audio sink signal handler.
	 *
	 * @param path the path
	 * @param audioSinkSignalHandler the audio sink signal handler
	 * @throws BusException the bus exception
	 */
	private  void registerAudioSinkSignalHandler(String path, AudioSinkSignalHandler audioSinkSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(audioSinkSignalHandler, path);
		serviceHelper.registerSignalHandler(audioSinkSignalHandler);
	}



	 /**
 	 * Gets the audio sink signal handler.
 	 *
 	 * @return the audio sink signal handler
 	 */
 	AudioSinkSignalHandler getAudioSinkSignalHandler()
	{
		return new AudioSinkSignalHandler();
	}



	/**
	 * Gets the audio sink.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return the audio sink
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  AudioSink getAudioSink(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
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



	/**
	 * Validate audio sink properties can be retrieved.
	 *
	 * @param audioSink the audio sink
	 * @throws BusException the bus exception
	 */
	private  void validateAudioSinkPropertiesCanBeRetrieved(AudioSink audioSink) throws BusException
	{
		audioSink.getFifoSize();
		audioSink.getFifoPosition();
	}








	/**
	 * Register ownership lost signal handler.
	 *
	 * @param path the path
	 * @param ownershipLostSignalHandler the ownership lost signal handler
	 * @throws BusException the bus exception
	 */
	private  void registerOwnershipLostSignalHandler(String path, OwnershipLostSignalHandler ownershipLostSignalHandler) throws BusException
	{
		serviceHelper.registerBusObject(ownershipLostSignalHandler, path);
		serviceHelper.registerSignalHandler(ownershipLostSignalHandler);
	}



	/**
	 * Open new stream.
	 *
	 * @param countDownLatch the count down latch
	 * @param ownershipLostSignalHandler the ownership lost signal handler
	 */
	private  void openNewStream(CountDownLatch countDownLatch, OwnershipLostSignalHandler ownershipLostSignalHandler)
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
 	 * Gets the signal timeout.
 	 *
 	 * @return the signal timeout
 	 */
 	long getSignalTimeout()
	{
		return SIGNAL_TIMEOUT_IN_SECONDS;
	}


	 /**
 	 * Creates the about client.
 	 *
 	 * @param busAttachment the bus attachment
 	 * @return the about client
 	 * @throws Exception the exception
 	 */
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



	/**
	 * Connect client.
	 *
	 * @param client the client
	 * @param busAttachment the bus attachment
	 * @return the status
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	private  Status connectClient(ClientBase client, BusAttachment busAttachment) throws BusException, Exception
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


	/**
	 * Sets the link timeout.
	 *
	 * @param clientBase the client base
	 * @param busAttachment the bus attachment
	 * @throws BusException the bus exception
	 */
	private  void setLinkTimeout(ClientBase clientBase, BusAttachment busAttachment) throws BusException
	{
		Status linkTimeoutstatus = busAttachment.setLinkTimeout(clientBase.getSessionId(), new IntegerValue(LINK_TIMEOUT_IN_SECONDS));

		if (linkTimeoutstatus != Status.OK)
		{
			throw new BusException(String.format("Failed to set link timeout value on bus attachment for session (%d): %s", clientBase.getSessionId(), linkTimeoutstatus));
		}
	}

	 /**
 	 * Creates the bus attachment manager.
 	 *
 	 * @return the bus attachment mgr
 	 * @throws BusException the bus exception
 	 */
 	BusAttachmentMgr createBusAttachmentManager() throws BusException
	{
		BusAttachmentMgr busAttachmentManager = new BusAttachmentMgr();
		busAttachmentManager.create(BUS_APPLICATION_NAME, RemoteMessage.Receive);
		busAttachmentManager.connect();

		return busAttachmentManager;
	}

	/**
	 * Validate connecting to audio sink twice throws exception.
	 *
	 * @param audioSinkPort the audio sink port
	 * @throws BusException the bus exception
	 */
	private  void validateConnectingToAudioSinkTwiceThrowsException(Port audioSinkPort) throws BusException
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




	/**
	 * Validate connecting to audio sink with invalid configuration throws exception.
	 *
	 * @param audioSinkPort the audio sink port
	 * @throws BusException the bus exception
	 */
	private  void validateConnectingToAudioSinkWithInvalidConfigurationThrowsException(Port audioSinkPort) throws BusException
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
	
	/**
	 * Gets the invalid sink configuration.
	 *
	 * @return the invalid sink configuration
	 */
	private  Configuration getInvalidSinkConfiguration()
	{
		Configuration configuration = new Configuration();
		configuration.mediaType = AudioXUnknown.getValue();
		configuration.parameters = Collections.emptyMap();

		return configuration;
	}

	/**
	 * Gets the valid sink configuration.
	 *
	 * @return the valid sink configuration
	 */
	private  Configuration getValidSinkConfiguration()
	{
		Configuration configuration = new Configuration();
		configuration.mediaType = AudioXRaw.getValue();
		configuration.parameters = getSinkConfigurationParameters();

		return configuration;
	}

	/**
	 * Gets the sink configuration parameters.
	 *
	 * @return the sink configuration parameters
	 */
	private  Map<String, Variant> getSinkConfigurationParameters()
	{
		Map<String, Variant> parameters = new HashMap<String, Variant>();
		parameters.put(AudioSinkParameter.Channels.name(), new Variant(MANDATORY_CHANNEL_ONE));
		parameters.put(AudioSinkParameter.Format.name(), new Variant(MANDATORY_FORMAT));
		parameters.put(AudioSinkParameter.Rate.name(), new Variant(MANDATORY_RATE_ONE, "q"));

		return parameters;
	}

	/**
	 * Gets the metadata sink port.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return the metadata sink port
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  Port getMetadataSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
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

	/**
	 * Validate metadata sink capabilities.
	 *
	 * @param configurations the configurations
	 * @throws BusException the bus exception
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateMetadataSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
	{
		assertNotNull("No capabilities found", configurations);
		assertTrue("No capabilities found", configurations.length > 0);
		assertEquals("Capabilities length does not match for MetadataSink object", 1, configurations.length);

		String mediaType = configurations[0].mediaType;
		assertEquals("Media type does not match", ApplicationXMetadata.getValue(), mediaType);
	}







	/**
	 * Validate image sink capabilities.
	 *
	 * @param configurations the configurations
	 * @throws BusException the bus exception
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateImageSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
	{
		assertNotNull("No capabilities found", configurations);
		assertTrue("No capabilities found", configurations.length > 0);

		for (Configuration configuration : configurations)
		{
			String mediaType = configuration.mediaType;
			if(mediaType.equals(ImageJpeg.getValue())) {
				if(ICSAU_ImageJpeg) {
					logger.info("Received mediaType \"image/jpeg\" and ICSAU_ImageJpeg= "+ICSAU_ImageJpeg);
					logger.info("Partial Verdict: PASS");
				} else {
					fail("Received mediaType \"image/jpeg\" and ICSAU_ImageJpeg= "+ICSAU_ImageJpeg);
				}
			} else {
				assertTrue(String.format("%s does not match expected media type pattern %s*", mediaType, ImagePrefix.getValue()), mediaType.startsWith(ImagePrefix.getValue()));
			}
		}
	}


	/**
	 * Gets the image sink port.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return the image sink port
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  Port getImageSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
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

	/**
	 * Validate audio sink capabilities.
	 *
	 * @param configurations the configurations
	 * @throws BusException the bus exception
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateAudioSinkCapabilities(Configuration[] configurations) throws BusException, AnnotationBusException
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
				if(ICSAU_AudioXalac) {
					validateAudioConfigurationStrictly(configuration);
				} else {
					fail("Received mediaType \"audio/x-alac\" and ICSAU_AudioXalac = "+ICSAU_AudioXalac);
				}
			}
			else
			{
				validateAudioConfigurationLeniently(configuration);
			}
		}

		assertTrue(String.format("%s media type capability not found", AudioXRaw.getValue()), audioRawCapabilityFound);
	}



	/**
	 * Validate audio configuration leniently.
	 *
	 * @param configuration the configuration
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateAudioConfigurationLeniently(Configuration configuration) throws AnnotationBusException
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



	/**
	 * Validate rate parameter leniently.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateRateParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Rate.name()).getSignature();
		assertTrue(
				String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Rate.name(),
						Rate.getValue(), mediaType), parameterSignature.equals(Rate.getValue()));
	}

	/**
	 * Validate format parameter leniently.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateFormatParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Format.name()).getSignature();
		assertTrue(
				String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Format.name(),
						Format.getValue(), mediaType), parameterSignature.equals(Format.getValue()));
	}

	/**
	 * Validate channels parameter leniently.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateChannelsParameterLeniently(Map<String, Variant> parameters, String mediaType) throws AnnotationBusException
	{
		String parameterSignature = parameters.get(AudioSinkParameter.Channels.name()).getSignature();
		assertTrue(String.format("%s signature for %s parameter does not match expected signature %s for media type %s", parameterSignature, AudioSinkParameter.Channels.name(),
				Channels.getValue(), mediaType), parameterSignature.equals(Channels.getValue()));
	}



	/**
	 * Validate audio configuration strictly.
	 *
	 * @param configuration the configuration
	 * @throws BusException the bus exception
	 */
	private  void validateAudioConfigurationStrictly(Configuration configuration) throws BusException
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


	/**
	 * Assert mandatory parameters are present.
	 *
	 * @param parameterNames the parameter names
	 * @param mediaType the media type
	 */
	private  void assertMandatoryParametersArePresent(Set<String> parameterNames, String mediaType)
	{
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Channels.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Channels.name()));
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Format.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Format.name()));
		assertTrue(String.format("%s parameter does not exist for media type %s", AudioSinkParameter.Rate.name(), mediaType),
				parameterNames.contains(AudioSinkParameter.Rate.name()));
	}



	/**
	 * Validate rate parameter strictly.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws BusException the bus exception
	 */
	private  void validateRateParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		short[] rates = parameters.get(AudioSinkParameter.Rate.name()).getObject(short[].class);
		assertTrue(String.format("Mandatory rate value %s not found for media type %s", MANDATORY_RATE_ONE, mediaType), isElementPresentInArray(rates, MANDATORY_RATE_ONE));
		assertTrue(String.format("Mandatory rate value %s not found for media type %s", MANDATORY_RATE_TWO, mediaType), isElementPresentInArray(rates, MANDATORY_RATE_TWO));
	}

	/**
	 * Validate format parameter strictly.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws BusException the bus exception
	 */
	private  void validateFormatParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		String[] formats = parameters.get(AudioSinkParameter.Format.name()).getObject(String[].class);
		assertTrue(String.format("Mandatory format value %s not found for media type %s", MANDATORY_FORMAT, mediaType), isElementPresentInArray(formats, MANDATORY_FORMAT));
	}

	/**
	 * Validate channels parameter strictly.
	 *
	 * @param parameters the parameters
	 * @param mediaType the media type
	 * @throws BusException the bus exception
	 */
	private  void validateChannelsParameterStrictly(Map<String, Variant> parameters, String mediaType) throws BusException
	{
		byte[] channels = parameters.get(AudioSinkParameter.Channels.name()).getObject(byte[].class);
		assertTrue(String.format("Mandatory channel value %s not found for media type %s", MANDATORY_CHANNEL_ONE, mediaType),
				isElementPresentInArray(channels, MANDATORY_CHANNEL_ONE));
		assertTrue(String.format("Mandatory channel value %s not found for media type %s", MANDATORY_CHANNEL_TWO, mediaType),
				isElementPresentInArray(channels, MANDATORY_CHANNEL_TWO));
	}



	/**
	 * Checks if is element present in array.
	 *
	 * @param array the array
	 * @param element the element
	 * @return true, if is element present in array
	 */
	private  boolean isElementPresentInArray(short[] array, short element)
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

	/**
	 * Checks if is element present in array.
	 *
	 * @param array the array
	 * @param element the element
	 * @return true, if is element present in array
	 */
	private  boolean isElementPresentInArray(String[] array, String element)
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

	/**
	 * Checks if is element present in array.
	 *
	 * @param array the array
	 * @param element the element
	 * @return true, if is element present in array
	 */
	private  boolean isElementPresentInArray(byte[] array, byte element)
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

	/**
	 * Gets the audio sink port.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return the audio sink port
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  Port getAudioSinkPort(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
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




	/**
	 * Validate stream interface.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @throws BusException the bus exception
	 */
	private  void validateStreamInterface(String path, BusIntrospector busIntrospector) throws BusException
	{
		Stream stream = busIntrospector.getInterface(path, Stream.class);
		assertEquals("Stream Interface version does not match", ixit.get("IXITAU_StreamVersion"), stream.getVersion());
	}


	/**
	 * Validate port interface.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validatePortInterface(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		Port port = busIntrospector.getInterface(path, Port.class);
		assertEquals("Port Interface version does not match", ixit.get("IXITAU_PortVersion"), port.getVersion());
		assertEquals("Port Interface direction does not match", PORT_DIRECTION, port.getDirection());
		assertMediaTypeSpecificPortInterfaceIsPresent(path, busIntrospector);
	}













	/**
	 * Assert media type specific port interface is present.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void assertMediaTypeSpecificPortInterfaceIsPresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException,
	SAXException
	{
		if (!isAudioSinkInterfacePresent(path, busIntrospector) && !isImageSinkInterfacePresent(path, busIntrospector) && !isMetadataSinkInterfacePresent(path, busIntrospector))
		{
			fail("Object implementing the Port interface must also implement one of the media-type specific port interfaces");
		}
	}

	/**
	 * Checks if is metadata sink interface present.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return true, if is metadata sink interface present
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  boolean isMetadataSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, METADATA_SINK_INTERFACE_NAME);
	}

	/**
	 * Checks if is image sink interface present.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return true, if is image sink interface present
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  boolean isImageSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, IMAGE_SINK_INTERFACE_NAME);
	}

	/**
	 * Checks if is audio sink interface present.
	 *
	 * @param path the path
	 * @param busIntrospector the bus introspector
	 * @return true, if is audio sink interface present
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  boolean isAudioSinkInterfacePresent(String path, BusIntrospector busIntrospector) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		return busIntrospector.isInterfacePresent(path, AUDIO_SINK_INTERFACE_NAME);
	}



	/**
	 * Tear down.
	 */
	private  void tearDown() {
		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}





	//private  void setUp(String iXITCO_DeviceId, String iXITCO_AppId) throws Exception {
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	private  void setUp() throws Exception {

		try
		{
			System.out.println("====================================================");
			logger.info("test setUp started");


			/*dutDeviceId = IXITCO_DeviceId;
			logger.info(String.format("Running Audio test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.info(String.format("Running Audio test case against App ID: %s", dutAppId));*/
			logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId")));
			logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId"))));

			streamObjectPath = ixit.get("IXITAU_TestObjectPath");

			if (streamObjectPath == null || streamObjectPath.isEmpty())
			{
				throw new RuntimeException("Audio Stream object path not specified");
			}

			logger.info(String.format("Executing Audio test against Stream object found at %s", streamObjectPath));

			serviceHelper = getServiceHelper();
			//serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
			serviceHelper.initialize(BUS_APPLICATION_NAME, ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId")));

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			
			serviceHelper.joinSession(BUS_APPLICATION_NAME, port);
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			stream = getIntrospector().getInterface(streamObjectPath, Stream.class);
			logger.info("test setUp done");
		}
		catch (Exception exception)
		{
			logger.info("Exception while setting up: "+ exception.toString());

			try
			{
				releaseResources();
			}
			catch (Exception newException)
			{
				logger.info("Exception releasing resources: "+ newException.toString());
			}

			throw exception;
		}
		System.out.println("====================================================");
	}





	 /**
 	 * Gets the introspector.
 	 *
 	 * @return the introspector
 	 */
 	BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}



	 /**
 	 * Gets the introspector.
 	 *
 	 * @param busAttachment the bus attachment
 	 * @param newAboutClient the new about client
 	 * @return the introspector
 	 */
 	BusIntrospector getIntrospector(BusAttachment busAttachment, AboutClient newAboutClient)
	{
		return new XmlBasedBusIntrospector(busAttachment, newAboutClient.getPeerName(), newAboutClient.getSessionId());
	}

	/**
	 * Gets the introspector.
	 *
	 * @param serviceHelper the service helper
	 * @param aboutClient the about client
	 * @return the introspector
	 */
	BusIntrospector getIntrospector(ServiceHelper serviceHelper, AboutClient aboutClient)
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}



	/**
	 * Determine about announcement timeout.
	 *
	 * @return the long
	 */
	private  long determineAboutAnnouncementTimeout() {
		// TODO Auto-generated method stub
		return timeOut;
	}





	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		disconnectFromAboutClient();

		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}


	/**
	 * Disconnect from about client.
	 */
	private  void disconnectFromAboutClient()
	{
		if (aboutClient != null)
		{
			aboutClient.disconnect();
			aboutClient = null;
		}
	}


	 /**
 	 * Gets the service helper.
 	 *
 	 * @return the service helper
 	 */
 	ServiceHelper getServiceHelper()
	{
		return new ServiceHelper(logger);
	}





	/**
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {
		// TODO Auto-generated method stub

		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;

	}


	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param Version the version
	 * @param version the version
	 */
	private  void assertEquals(String errorMsg,
			String Version, short version) {
		// TODO Auto-generated method stub

		if(Short.parseShort(Version)!=version){
			fail(errorMsg);



		}

	}



	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param i the i
	 * @param j the j
	 */
	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
			fail(errorMsg);



		}

	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param string1 the string1
	 * @param string2 the string2
	 */
	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		// TODO Auto-generated method stub

		if(!string1.equals(string2)){
			fail(errorMsg);


		}

	}


	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param byte1 the byte1
	 * @param byte2 the byte2
	 */
	private  void assertEquals(String errorMsg, byte byte1,
			byte byte2) {
		if(!(byte1==byte2)){
			fail(errorMsg);


		}

	}



	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 * @param booleanValue the boolean value
	 */
	private  void assertEquals(String errorMsg, boolean bool,
			boolean booleanValue) {
		if(bool!=booleanValue){
			fail(errorMsg);
		}

	}

	/**
	 * Assert true.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 */
	private  void assertTrue(String errorMsg,
			boolean bool) {
		// TODO Auto-generated method stub
		if(!bool){
			fail(errorMsg);

		}

	}


	/*private  void assertFalse(String errorMsg,
			boolean bool) {
		// TODO Auto-generated method stub
		if(bool){
			fail(errorMsg);

		}

	}*/


	/**
	 * Assert not null.
	 *
	 * @param msgError the msg error
	 * @param notNull the not null
	 */
	private  void assertNotNull(String msgError, Object notNull) {

		if(notNull==null){
			fail(msgError);
		}

	}


	/**
	 * Assert null.
	 *
	 * @param msgError the msg error
	 * @param Null the null
	 */
	private  void assertNull(String msgError,
			Object Null) {
		if(Null!=null){
			fail(msgError);
		}

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

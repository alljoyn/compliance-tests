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
package com.at4wireless.alljoyn;
 
 
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.testcases.conf.about.AboutServ;
import com.at4wireless.alljoyn.testcases.conf.audio.AudioService;
import com.at4wireless.alljoyn.testcases.conf.configuration.ConfigurationService;
import com.at4wireless.alljoyn.testcases.conf.controlpanel.ControlPanelService;
import com.at4wireless.alljoyn.testcases.conf.gateway.GatewayService;
import com.at4wireless.alljoyn.testcases.conf.lighting.LightingService;
import com.at4wireless.alljoyn.testcases.conf.lightingcontroller.LightingControllerService;
import com.at4wireless.alljoyn.testcases.conf.notification.NotificationService;
import com.at4wireless.alljoyn.testcases.conf.onboarding.OnBoardingService;
import com.at4wireless.alljoyn.testcases.conf.smarthome.SmartHomeService;
import com.at4wireless.alljoyn.testcases.conf.time.TimeService;
import com.at4wireless.alljoyn.testcases.iop.about.AboutIOP;
import com.at4wireless.alljoyn.testcases.iop.audio.AudioIOP;
import com.at4wireless.alljoyn.testcases.iop.config.ConfigIOP;
import com.at4wireless.alljoyn.testcases.iop.controlpanel.ControlPanelIOP;
import com.at4wireless.alljoyn.testcases.iop.lighting.LightingIOP;
import com.at4wireless.alljoyn.testcases.iop.notification.NotificationIOP;
import com.at4wireless.alljoyn.testcases.iop.onboarding.OnboardingIOP;


 
/**
 * The Class Manager used to launch testcases.
 */
public class Manager extends Thread {
	static {
		System.loadLibrary("alljoyn_java");
	}
	/** The version. */
	String versionKey="14.06.00a";
	/** The testcase name. */
	String testKey = null;

	/** The testcase verdict. */
	String verdictKey =null;

	/** The testcase description. */
	String descriptionKey = null;

	/** The testcase date time. */
	String dateTimeKey =null;

	/** The testcase id. */
	String iDKey = null;

	/** The testcase log. */
	String logKey = null;

	/** If true the testCase is running. */
	boolean running=false;

	/** The TAG. */
	protected static final String TAG = "TestRunner";

	/** The logger used to print. */
	private static final WindowsLoggerImpl logger =  LoggerFactory.getLogger(TAG);

	/** The time stamp. */
	Timestamp sqlTimestamp;

	/** The doc that contains all values using during execution. */
	private Document doc;

	/** The testcase name. */
	private String testName;



	/** The testcase Verdict. */
	private String Verdict=null;

	/** The testcase Time stamp. */
	private String TimeStamp=null;

	/** The test to run. */
	String test=null;

	/** The test names. */
	static List<String> testNames=new ArrayList<String>();

	/** The ICSCO date of manufacture. */
	static boolean ICSCO_DateOfManufacture=false;

	/** The ICSCO hardware version. */
	static boolean ICSCO_HardwareVersion=false;

	/** The ICSCO support url. */
	static boolean ICSCO_SupportUrl=false;

	/** The ICSCO icon interface. */
	static boolean ICSCO_IconInterface=false;

	/** The ICSCO device name. */
	static boolean ICSCO_DeviceName=false; 
	/////////////////////////////////
	/** The ICSN notification service framework. */
	static boolean ICSN_NotificationServiceFramework=false;

	/** The ICSN notification interface. */
	static boolean ICSN_NotificationInterface=false;

	/** The ICSN rich icon url. */
	static boolean ICSN_RichIconUrl=false;

	/** The ICSN rich audio url. */
	static boolean ICSN_RichAudioUrl=false;

	/** The ICSN resp object path. */
	static boolean ICSN_RespObjectPath=false;

	/** The ICSN notification producer interface. */
	static boolean ICSN_NotificationProducerInterface=false;

	/** The ICSN dismisser interface. */
	static boolean ICSN_DismisserInterface=false;

	/** The ICSN notification consumer. */
	static boolean ICSN_NotificationConsumer=false; //JTF
	///////////////////////////////
	/** The ICSON onboarding service framework. */
	static boolean ICSON_OnboardingServiceFramework=false;

	/** The ICSON onboarding interface. */
	static boolean ICSON_OnboardingInterface=false;

	/** The ICSON channel switching. */
	static boolean ICSON_ChannelSwitching=false;

	/** The ICSON get scan info method. */
	static boolean ICSON_GetScanInfoMethod=false;	

	///////////////////////////////
	/** The ICSCP control panel service framework. */
	static boolean ICSCP_ControlPanelServiceFramework=false;

	/** The ICSCP control panel interface. */
	static boolean ICSCP_ControlPanelInterface=false;

	/** The ICSCP container interface. */
	static boolean ICSCP_ContainerInterface=false;

	/** The ICSCP secured container interface. */
	static boolean ICSCP_SecuredContainerInterface=false;

	/** The ICSCP property interface. */
	static boolean ICSCP_PropertyInterface=false;

	/** The ICSCP secured property interface. */
	static boolean ICSCP_SecuredPropertyInterface=false;

	/** The ICSCP label property interface. */
	static boolean ICSCP_LabelPropertyInterface=false;

	/** The ICSCP action interface. */
	static boolean ICSCP_ActionInterface=false;

	/** The ICSCP secured action interface. */
	static boolean ICSCP_SecuredActionInterface=false;

	/** The ICSCP notification action interface. */
	static boolean ICSCP_NotificationActionInterface=false;

	/** The ICSCP dialog interface. */
	static boolean ICSCP_DialogInterface=false;

	/** The ICSCP d i_ action2. */
	static boolean ICSCP_DI_Action2=false;

	/** The ICSCP d i_ action3. */
	static boolean ICSCP_DI_Action3=false;

	/** The ICSCP secured dialog interface. */
	static boolean ICSCP_SecuredDialogInterface=false;

	/** The ICSCP sd i_ action2. */
	static boolean ICSCP_SDI_Action2=false;

	/** The ICSCP sd i_ action3. */
	static boolean ICSCP_SDI_Action3=false;

	/** The ICSCP list property interface. */
	static boolean ICSCP_ListPropertyInterface=false;

	/** The ICSCP secured list property interface. */
	static boolean ICSCP_SecuredListPropertyInterface=false;

	/** The ICSCP http control interface. */
	static boolean ICSCP_HTTPControlInterface=false;
	////////////////////////////////////
	/** The ICSCF configuration service framework. */
	static boolean ICSCF_ConfigurationServiceFramework=false;

	/** The ICSCF configuration interface. */
	static boolean ICSCF_ConfigurationInterface=false;

	/** The ICSCF factory reset method. */
	static boolean ICSCF_FactoryResetMethod=false;
	/////////////////////////////////////
	/** The ICSAU audio service framework. */
	static boolean ICSAU_AudioServiceFramework=false;

	/** The ICSAU stream interface. */
	static boolean ICSAU_StreamInterface=false;

	/** The ICSAU stream port interface. */
	static boolean ICSAU_StreamPortInterface=false;

	/** The ICSAU port audio sink interface. */
	static boolean ICSAU_PortAudioSinkInterface=false;

	/** The ICSAU port audio source interface. */
	static boolean ICSAU_PortAudioSourceInterface=false;

	/** The ICSAU port image sink interface. */
	static boolean ICSAU_PortImageSinkInterface=false;

	/** The ICSAU port image source interface. */
	static boolean ICSAU_PortImageSourceInterface=false;

	/** The ICSAU port application metadata sink interface. */
	static boolean ICSAU_PortApplicationMetadataSinkInterface=false;

	/** The ICSAU port application metadata source interface. */
	static boolean ICSAU_PortApplicationMetadataSourceInterface=false;

	/** The ICSAU control volume interface. */
	static boolean ICSAU_ControlVolumeInterface=false;

	/** The ICSAU stream clock interface. */
	static boolean ICSAU_StreamClockInterface=false;

	/** The ICSAU audio xalac. */
	static boolean ICSAU_AudioXalac=false;

	/** The ICSAU image jpeg. */
	static boolean ICSAU_ImageJpeg=false;

	/** The ICSAU application xmetadata. */
	static boolean ICSAU_ApplicationXmetadata=false;

	/** The ICSAU volume control. */
	static boolean ICSAU_VolumeControl=false;
	/////////////////////////////////////////
	/** The ICSL lighting service framework. */
	static boolean ICSL_LightingServiceFramework=false;

	/** The ICSL lamp service interface. */
	static boolean ICSL_LampServiceInterface=false;

	/** The ICSL lamp parameters interface. */
	static boolean ICSL_LampParametersInterface=false;

	/** The ICSL lamp details interface. */
	static boolean ICSL_LampDetailsInterface=false;

	/** The ICSL dimmable. */
	static boolean ICSL_Dimmable=false;

	/** The ICSL color. */
	static boolean ICSL_Color=false;

	/** The ICSL color temperature. */
	static boolean ICSL_ColorTemperature=false;

	/** The ICSL effects. */
	static boolean ICSL_Effects=false;

	/** The ICSL lamp state interface. */
	static boolean ICSL_LampStateInterface=false;
	/////////////////////////////////////////

	/** The ICST time service framework. */
	static boolean ICST_TimeServiceFramework=false;

	/** The ICST clock interface. */
	static boolean ICST_ClockInterface=false;

	/** The ICST date. */
	static boolean ICST_Date=false;

	/** The ICST milliseconds. */
	static boolean ICST_Milliseconds=false;

	/** The ICST time authority interface. */
	static boolean ICST_TimeAuthorityInterface=false;

	/** The ICST alarm factory interface. */
	static boolean ICST_AlarmFactoryInterface=false;

	/** The ICST alarm interface. */
	static boolean ICST_AlarmInterface=false;

	/** The ICST timer factory interface. */
	static boolean ICST_TimerFactoryInterface=false;

	/** The ICST timer interface. */
	static boolean ICST_TimerInterface=false;
	///////////////////////////////////////////////////
	/** The ICSG gateway service framework. */
	static boolean ICSG_GatewayServiceFramework=false;

	/** The ICSG profile management interface. */
	static boolean ICSG_ProfileManagementInterface=false;

	/** The ICSG app access interface. */
	static boolean ICSG_AppAccessInterface=false;

	/** The ICSG app management interface. */
	static boolean ICSG_AppManagementInterface=false;
	////////////////////////
	/** The ICSSH smart home service framework. */
	static boolean ICSSH_SmartHomeServiceFramework=false;

	/** The ICSSH centralized management interface. */
	static boolean ICSSH_CentralizedManagementInterface=false;
	////////////////////////////
	/** The ICSLC lighting controller service framework. */
	static boolean ICSLC_LightingControllerServiceFramework=false;

	/** The ICSLC controller service interface. */
	static boolean ICSLC_ControllerServiceInterface=false;

	/** The ICSLC controller service lamp interface. */
	static boolean ICSLC_ControllerServiceLampInterface=false;

	/** The ICSLC controller service lamp group interface. */
	static boolean ICSLC_ControllerServiceLampGroupInterface=false;

	/** The ICSLC controller service preset interface. */
	static boolean ICSLC_ControllerServicePresetInterface=false;

	/** The ICSLC controller service scene interface. */
	static boolean ICSLC_ControllerServiceSceneInterface=false;

	/** The ICSLC controller service master scene interface. */
	static boolean ICSLC_ControllerServiceMasterSceneInterface=false;

	/** The ICSLC leader election and state sync interface. */
	static boolean ICSLC_LeaderElectionAndStateSyncInterface=false;

	///////////////////////////////
	/** The IXITCO about version. */
	static String IXITCO_AboutVersion=null;

	/** The IXITCO app id. */
	static String IXITCO_AppId=null;

	/** The IXITCO default language. */
	static String IXITCO_DefaultLanguage=null;

	/** The IXITCO device name. */
	static String IXITCO_DeviceName=null;

	/** The IXITCO device id. */
	static String IXITCO_DeviceId=null;

	/** The IXITCO app name. */
	static String IXITCO_AppName=null;

	/** The IXITCO manufacturer. */
	static String IXITCO_Manufacturer=null;

	/** The IXITCO model number. */
	static String IXITCO_ModelNumber=null;

	/** The IXITCO software version. */
	static String IXITCO_SoftwareVersion=null;

	/** The IXITCO aj software version. */
	static String IXITCO_AJSoftwareVersion=null;

	/** The IXITCO hardware version. */
	static String IXITCO_HardwareVersion=null;

	/** The IXITCO introspectable version. */
	static String IXITCO_IntrospectableVersion=null;

	/** The IXITCO supported languages. */
	static String IXITCO_SupportedLanguages=null;

	/** The IXITCO description. */
	static String IXITCO_Description=null;

	/** The IXITCO date of manufacture. */
	static String IXITCO_DateOfManufacture=null;

	/** The IXITCO support url. */
	static String IXITCO_SupportUrl=null;
	////////////////////////////////////////////
	/** The IXITN notification version. */
	static String IXITN_NotificationVersion=null;

	/** The ixitn ttl. */
	static String IXITN_TTL=null;

	/** The IXITN notification producer version. */
	static String IXITN_NotificationProducerVersion=null;

	/** The IXITN notification dismisser version. */
	static String IXITN_NotificationDismisserVersion=null;

	///////////////////////////////////

	/** The IXITON onboarding version. */
	static String IXITON_OnboardingVersion=null;

	/** The IXITON soft ap. */
	static String IXITON_SoftAP=null;

	/** The IXITON soft ap auth type. */
	static String IXITON_SoftAPAuthType=null;

	/** The IXITON soft a ppassphrase. */
	static String IXITON_SoftAPpassphrase=null;

	/** The IXITON personal ap. */
	static String IXITON_PersonalAP=null;

	/** The IXITON personal ap auth type. */
	static String IXITON_PersonalAPAuthType=null;

	/** The IXITON personal a ppassphrase. */
	static String IXITON_PersonalAPpassphrase=null;

	////////////////////////////////
	/** The IXITCP control panel version. */
	static String IXITCP_ControlPanelVersion=null;

	/** The IXITCP container version. */
	static String IXITCP_ContainerVersion=null;

	/** The IXITCP property version. */
	static String IXITCP_PropertyVersion=null;

	/** The IXITCP label property version. */
	static String IXITCP_LabelPropertyVersion=null;

	/** The IXITCP action version. */
	static String IXITCP_ActionVersion=null;

	/** The IXITCP notification action version. */
	static String IXITCP_NotificationActionVersion=null;

	/** The IXITCP dialog version. */
	static String IXITCP_DialogVersion=null;

	/** The IXITCP list property version. */
	static String IXITCP_ListPropertyVersion=null;

	/** The IXITCP http control version. */
	static String IXITCP_HTTPControlVersion=null;
	////////////////////////////////
	/** The IXITCF config version. */
	static String IXITCF_ConfigVersion=null;

	/** The IXITCF passcode. */
	static String IXITCF_Passcode=null; //JTF
	/////////////////////////////////////

	/** The IXITAU stream version. */
	static String IXITAU_StreamVersion=null;

	/** The IXITAU test object path. */
	static String IXITAU_TestObjectPath=null;

	/** The IXITAU port version. */
	static String IXITAU_PortVersion=null;

	/** The IXITAU audio sink version. */
	static String IXITAU_AudioSinkVersion=null;

	/** The IXITAUtime nanos. */
	static String IXITAU_timeNanos=null;

	/** The IXITAU audio source version. */
	static String IXITAU_AudioSourceVersion=null;

	/** The IXITAU image sink version. */
	static String IXITAU_ImageSinkVersion=null;

	/** The IXITAU image source version. */
	static String IXITAU_ImageSourceVersion=null;

	/** The IXITAU metadata sink version. */
	static String IXITAU_MetadataSinkVersion=null;

	/** The IXITAU metadata source version. */
	static String IXITAU_MetadataSourceVersion=null;

	/** The IXITAU control volume version. */
	static String IXITAU_ControlVolumeVersion=null;

	/** The IXITAUdelta. */
	static String IXITAU_delta=null;

	/** The IXITAUchange. */
	static String IXITAU_change=null;

	/** The IXITAU clock version. */
	static String IXITAU_ClockVersion=null;

	/** The IXITAUadjust nanos. */
	static String IXITAU_adjustNanos=null;
	//////////////////////////////////////
	/** The IXITL lamp service version. */
	static String IXITL_LampServiceVersion=null;

	/** The IXITL lamp parameters version. */
	static String IXITL_LampParametersVersion=null;

	/** The IXITL lamp details version. */
	static String IXITL_LampDetailsVersion=null;

	/** The IXITL lamp state version. */
	static String IXITL_LampStateVersion=null;
	/////////////////////////////////////////////////
	/** The IXITT clock version. */
	static String IXITT_ClockVersion=null;

	/** The IXITT time authority version. */
	static String IXITT_TimeAuthorityVersion=null;

	/** The IXITT alarm factory version. */
	static String IXITT_AlarmFactoryVersion=null;

	/** The IXITT alarm version. */
	static String IXITT_AlarmVersion=null;

	/** The IXITT timer factory version. */
	static String IXITT_TimerFactoryVersion=null;

	/** The IXITT timer version. */
	static String IXITT_TimerVersion=null;
	/////////////////////////////////
	/** The IXITG app mgmt version. */
	static String IXITG_AppMgmtVersion=null;

	/** The IXITG ctrl app version. */
	static String IXITG_CtrlAppVersion=null;

	/** The IXITG ctrl access version. */
	static String IXITG_CtrlAccessVersion=null;

	/** The IXITG ctrl acl version. */
	static String IXITG_CtrlAclVersion=null;

	/** The IXITG conn app version. */
	static String IXITG_ConnAppVersion=null;
	///////////////////////////////////////////
	/** The IXITSH centralized management version. */
	static String IXITSH_CentralizedManagementVersion=null;

	/** The IXITSH well known name. */
	static String IXITSH_WellKnownName=null;

	/** The IXITSH unique name. */
	static String IXITSH_UniqueName=null;

	/** The IXITSH device id. */
	static String IXITSH_DeviceId=null;

	/** The IXITSH heart beat interval. */
	static String IXITSH_HeartBeatInterval=null;
	////////////////////////////////////////

	/** The IXITLC controller service version. */
	static String IXITLC_ControllerServiceVersion=null;

	/** The IXITLC controller service lamp version. */
	static String IXITLC_ControllerServiceLampVersion=null;

	/** The IXITLC controller service lamp group version. */
	static String IXITLC_ControllerServiceLampGroupVersion=null;

	/** The IXITLC controller service preset version. */
	static String IXITLC_ControllerServicePresetVersion=null;

	/** The IXITLC controller service scene version. */
	static String IXITLC_ControllerServiceSceneVersion=null;

	/** The IXITLC controller service master scene version. */
	static String IXITLC_ControllerServiceMasterSceneVersion=null;

	/** The IXITLC leader election and state sync version. */
	static String IXITLC_LeaderElectionAndStateSyncVersion=null;

	////////////////////////////////////////////

	/** The GPCO announcement timeout. */
	static String GPCO_AnnouncementTimeout=null;

	/** The GPON wait soft ap. */
	static String GPON_WaitSoftAP=null;

	/** The GPON connect soft ap. */
	static String GPON_ConnectSoftAP=null;

	/** The GPON wait soft ap after offboard. */
	static String GPON_WaitSoftAPAfterOffboard=null;

	/** The GPON connect personal ap. */
	static String GPON_ConnectPersonalAP=null;

	/** The GPON disconnect. */
	static String GPON_Disconnect=null;

	/** The GPON next announcement. */
	static String GPON_NextAnnouncement=null;

	/** The GPCF session lost. */
	static String GPCF_SessionLost=null;

	/** The GPCF session close. */
	static String GPCF_SessionClose=null;

	/** The GPL session close. */
	static String GPL_SessionClose=null;

	/** The GPAU signal. */
	static String GPAU_Signal=null;

	/** The GPAU link. */
	static String GPAU_Link=null;

	/** The GPG session close. */
	static String GPG_SessionClose=null;

	/** The GPSH signal. */
	static String GPSH_Signal=null;

	/** The GPLC session close. */
	static String GPLC_SessionClose=null;

	/**
	 * The main method.
	 *
	 * @param arg the arguments
	 */
	public static void main(final String arg[]){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Manager manager = new Manager("About-v1-01","/cfg.xml");
					Manager manager = new Manager(arg[0], arg[1]); 
					manager.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	/**
	 * Instantiates a new manager.
	 *
	 * @param testName the test name
	 * @param docName the doc name
	 */
	public Manager(String testName, String docName) {
		this.testName = testName;


		String path = docName;

		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			String xml = new String(encoded, StandardCharsets.UTF_8);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			dBuilder = dbFactory.newDocumentBuilder();
			this.doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {


		running=true;
		try { 

			NodeList testCases = doc.getElementsByTagName("TestCase");
			System.out.println("==========================");


			for (int i = 0; i < testCases.getLength(); i++) {
				Node node = testCases.item(i);

				Element element= (Element) node;
				String testName=getValue("Name", element);


				if(testName.equals(this.testName)){

					if (node.getNodeType() == Node.ELEMENT_NODE) {


						String id=getValue("Id", element);


						iDKey=id;

						System.out.println("Test Name: " + testName);
						testKey=testName;


						String description=getValue("Description", element);


						System.out.println("Description: " + description);
						descriptionKey=description;



						test= getTestService(testName);



					}
				}
			}




			NodeList ics = doc.getElementsByTagName("Ics");




			for (int i = 0; i < ics.getLength(); i++) {
				Node node = ics.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String icsName=getValue("Name", element);
					String icsValue=getValue("Value", element);



					setIcs(icsName,icsValue);

				}
			}





			NodeList ixit = doc.getElementsByTagName("Ixit");
			for (int i = 0; i < ixit.getLength(); i++) {
				Node node = ixit.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					String ixitName=getValue("Name", element);
					String ixitValue=getValue("Value", element);


					setIxit(ixitName,ixitValue);






				}

			}

			NodeList parameter = doc.getElementsByTagName("Parameter");

			for (int i = 0; i < parameter.getLength(); i++) {
				Node node = parameter.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					String parameterName=getValue("Name", element);
					String parameterValue=getValue("Value", element);


					setParameter(parameterName,parameterValue);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("====================================================");
		Boolean existTest=true;
		existTest=runTest();
		String timeLog= new SimpleDateFormat("yyyyMMdd-HHmmss").format(sqlTimestamp);
		logKey="Log-"+testName+"-"+timeLog;
		generateXML(iDKey,testKey,descriptionKey,dateTimeKey,verdictKey,versionKey,logKey);
		running=false;
	}




	/**
	 * Run test.
	 *
	 * @return true, if successful
	 */
	private boolean runTest() {

		logger.info("started:"+testName);	
		Verdict="";
		boolean existTest = false;


		Date utilDate = new java.util.Date(); //fecha actual
		long lnMilisec = utilDate.getTime();

		sqlTimestamp = new java.sql.Timestamp(lnMilisec);

		String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(sqlTimestamp);


		this.TimeStamp=timeStamp;
		dateTimeKey=timeStamp;

		if(test.equals("ABOUT")){

			System.out.println("====================================================");
			System.out.println("ICSCO_DateOfManufacture: "+ICSCO_DateOfManufacture);
			System.out.println("ICSCO_HardwareVersion: "+ICSCO_HardwareVersion);
			System.out.println("ICSCO_SupportUrl: "+ICSCO_SupportUrl);
			System.out.println("ICSCO_IconInterface: "+ICSCO_IconInterface);
			System.out.println("ICSCO_DeviceName: "+ICSCO_DeviceName);

			System.out.println("IXITCO_AboutVersion: "+IXITCO_AboutVersion);
			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCO_DeviceName: "+IXITCO_DeviceName);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_AppName: "+IXITCO_AppName);
			System.out.println("IXITCO_Manufacturer: "+IXITCO_Manufacturer);
			System.out.println("IXITCO_ModelNumber: "+IXITCO_ModelNumber);
			System.out.println("IXITCO_SoftwareVersion: "+IXITCO_SoftwareVersion);
			System.out.println("IXITCO_AJSoftwareVersion: "+IXITCO_AJSoftwareVersion);
			System.out.println("IXITCO_HardwareVersion: "+IXITCO_HardwareVersion);
			System.out.println("IXITCO_IntrospectableVersion: "+IXITCO_IntrospectableVersion);
			System.out.println("IXITCO_SupportedLanguages: "+IXITCO_SupportedLanguages);
			System.out.println("IXITCO_Description: "+IXITCO_Description);
			System.out.println("IXITCO_DateOfManufacture: "+IXITCO_DateOfManufacture);
			System.out.println("IXITCO_SupportUrl: "+IXITCO_SupportUrl);
			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			//System.out.println("====================================================");

			AboutServ AboutTest=new AboutServ(
					testName,
					ICSCO_DateOfManufacture,
					ICSCO_HardwareVersion,
					ICSCO_SupportUrl,
					ICSCO_IconInterface,
					ICSCO_DeviceName,
					IXITCO_AboutVersion,
					IXITCO_AppId,
					IXITCO_DefaultLanguage,
					IXITCO_DeviceName,
					IXITCO_DeviceId,
					IXITCO_AppName,
					IXITCO_Manufacturer,
					IXITCO_ModelNumber,
					IXITCO_SoftwareVersion,
					IXITCO_AJSoftwareVersion,
					IXITCO_HardwareVersion,
					IXITCO_IntrospectableVersion,
					IXITCO_SupportedLanguages,
					IXITCO_Description,
					IXITCO_DateOfManufacture,
					IXITCO_SupportUrl,
					GPCO_AnnouncementTimeout);
			existTest = true;

			String verdict =AboutTest.getVerdict();

			this.Verdict=verdict;



			verdictKey=verdict;




		}else if(test.equals("NOTIFICATION")){

			System.out.println("====================================================");
			System.out.println("ICSN_NotificationServiceFramework: "+ICSN_NotificationServiceFramework);
			System.out.println("ICSN_NotificationInterface: "+ICSN_NotificationInterface);
			System.out.println("ICSN_RichIconUrl: "+ICSN_RichIconUrl);
			System.out.println("ICSN_RichAudioUrl: "+ICSN_RichAudioUrl);
			System.out.println("ICSN_RespObjectPath: "+ICSN_RespObjectPath);
			System.out.println("ICSN_NotificationProducerInterface: "+ICSN_NotificationProducerInterface);
			System.out.println("ICSN_DismisserInterface: "+ICSN_DismisserInterface);
			System.out.println("ICSN_NotificationConsumer: "+ICSN_NotificationConsumer);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITN_NotificationVersion: "+IXITN_NotificationVersion);
			System.out.println("IXITN_TTL: "+IXITN_TTL);
			System.out.println("IXITN_NotificationProducerVersion: "+IXITN_NotificationProducerVersion);
			System.out.println("IXITN_NotificationDismisserVersion: "+IXITN_NotificationDismisserVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);

			NotificationService NotificationTest=new NotificationService(
					testName,
					ICSN_NotificationServiceFramework,
					ICSN_NotificationInterface,
					ICSN_RichIconUrl,
					ICSN_RichAudioUrl,
					ICSN_RespObjectPath,
					ICSN_NotificationProducerInterface,
					ICSN_DismisserInterface,
					ICSN_NotificationConsumer,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITN_NotificationVersion,
					IXITN_TTL,
					IXITN_NotificationProducerVersion,
					IXITN_NotificationDismisserVersion,
					GPCO_AnnouncementTimeout);

			existTest = true;

			String verdict =NotificationTest.getVerdict();

			this.Verdict=verdict;




			verdictKey=verdict;

		}else if(test.equals("CONTROLPANEL")){

			System.out.println("====================================================");
			System.out.println("ICSCP_ControlPanelServiceFramework: "+ICSCP_ControlPanelServiceFramework);
			System.out.println("ICSCP_ControlPanelInterface: "+ICSCP_ControlPanelInterface);
			System.out.println("ICSCP_ContainerInterface: "+ICSCP_ContainerInterface);
			System.out.println("ICSCP_SecuredContainerInterface: "+ICSCP_SecuredContainerInterface);
			System.out.println("ICSCP_PropertyInterface: "+ICSCP_PropertyInterface);
			System.out.println("ICSCP_SecuredPropertyInterface: "+ICSCP_SecuredPropertyInterface);
			System.out.println("ICSCP_LabelPropertyInterface: "+ICSCP_LabelPropertyInterface);
			System.out.println("ICSCP_ActionInterface: "+ICSCP_ActionInterface);
			System.out.println("ICSCP_SecuredActionInterface: "+ICSCP_SecuredActionInterface);
			System.out.println("ICSCP_NotificationActionInterface: "+ICSCP_NotificationActionInterface);
			System.out.println("ICSCP_DialogInterface: "+ICSCP_DialogInterface);
			System.out.println("ICSCP_DI_Action2: "+ICSCP_DI_Action2);
			System.out.println("ICSCP_DI_Action3: "+ICSCP_DI_Action3);
			System.out.println("ICSCP_ListPropertyInterface: "+ICSCP_ListPropertyInterface);
			System.out.println("ICSCP_SecuredListPropertyInterface: "+ICSCP_SecuredListPropertyInterface);
			System.out.println("ICSCP_HTTPControlInterface: "+ICSCP_HTTPControlInterface);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCP_ControlPanelVersion: "+IXITCP_ControlPanelVersion);
			System.out.println("IXITCP_ContainerVersion: "+IXITCP_ContainerVersion);
			System.out.println("IXITCP_PropertyVersion: "+IXITCP_PropertyVersion);
			System.out.println("IXITCP_LabelPropertyVersion: "+IXITCP_LabelPropertyVersion);
			System.out.println("IXITCP_ActionVersion: "+IXITCP_ActionVersion);
			System.out.println("IXITCP_NotificationActionVersion: "+IXITCP_NotificationActionVersion);
			System.out.println("IXITCP_DialogVersion: "+IXITCP_DialogVersion);
			System.out.println("IXITCP_ListPropertyVersion: "+IXITCP_ListPropertyVersion);
			System.out.println("IXITCP_HTTPControlVersion: "+IXITCP_HTTPControlVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);

			ControlPanelService ControlPanelTest=new ControlPanelService(
					testName,
					ICSCP_ControlPanelServiceFramework,
					ICSCP_ControlPanelInterface,
					ICSCP_ContainerInterface,
					ICSCP_SecuredContainerInterface,
					ICSCP_PropertyInterface,
					ICSCP_SecuredPropertyInterface,
					ICSCP_LabelPropertyInterface,
					ICSCP_ActionInterface,
					ICSCP_SecuredActionInterface,
					ICSCP_NotificationActionInterface,
					ICSCP_DialogInterface,
					ICSCP_DI_Action2,
					ICSCP_DI_Action3,
					ICSCP_SecuredDialogInterface,
					ICSCP_SDI_Action2,
					ICSCP_SDI_Action3,
					ICSCP_ListPropertyInterface,
					ICSCP_SecuredListPropertyInterface,
					ICSCP_HTTPControlInterface,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITCP_ControlPanelVersion,
					IXITCP_ContainerVersion,
					IXITCP_PropertyVersion,
					IXITCP_LabelPropertyVersion,
					IXITCP_ActionVersion,
					IXITCP_NotificationActionVersion,
					IXITCP_DialogVersion,
					IXITCP_ListPropertyVersion,
					IXITCP_HTTPControlVersion,
					GPCO_AnnouncementTimeout);
			existTest = true;
			String verdict =ControlPanelTest.getVerdict();
			this.Verdict=verdict;


			verdictKey=verdict;



		}else if(test.equals("ONBOARDING")){

			System.out.println("====================================================");
			System.out.println("ICSON_OnboardingServiceFramework: "+ICSON_OnboardingServiceFramework);
			System.out.println("ICSON_OnboardingInterface: "+ICSON_OnboardingInterface);
			System.out.println("ICSON_ChannelSwitching: "+ICSON_ChannelSwitching);
			System.out.println("ICSON_GetScanInfoMethod: "+ICSON_GetScanInfoMethod);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITON_OnboardingVersion: "+IXITON_OnboardingVersion);
			System.out.println("IXITON_SoftAP: "+IXITON_SoftAP);
			System.out.println("IXITON_SoftAPAuthType: "+IXITON_SoftAPAuthType);
			System.out.println("IXITON_SoftAPpassphrase: "+IXITON_SoftAPpassphrase);
			System.out.println("IXITON_PersonalAP: "+IXITON_PersonalAP);
			System.out.println("IXITON_PersonalAPAuthType: "+IXITON_PersonalAPAuthType);
			System.out.println("IXITON_PersonalAPpassphrase: "+IXITON_PersonalAPpassphrase);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPON_WaitSoftAP: "+GPON_WaitSoftAP);
			System.out.println("GPON_ConnectSoftAP: "+GPON_ConnectSoftAP);
			System.out.println("GPON_WaitSoftAPAfterOffboard: "+GPON_WaitSoftAPAfterOffboard);
			System.out.println("GPON_ConnectPersonalAP: "+GPON_ConnectPersonalAP);
			System.out.println("GPON_Disconnect: "+GPON_Disconnect);
			System.out.println("GPON_NextAnnouncement: "+GPON_NextAnnouncement);

			OnBoardingService ControlPanelTest=new OnBoardingService(
					testName,
					ICSON_OnboardingServiceFramework,
					ICSON_OnboardingInterface,
					ICSON_ChannelSwitching,
					ICSON_GetScanInfoMethod,	
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITON_OnboardingVersion,
					IXITON_SoftAP,
					IXITON_SoftAPAuthType,
					IXITON_SoftAPpassphrase,
					IXITON_PersonalAP,
					IXITON_PersonalAPAuthType,
					IXITON_PersonalAPpassphrase,
					GPCO_AnnouncementTimeout,
					GPON_WaitSoftAP,
					GPON_ConnectSoftAP,
					GPON_WaitSoftAPAfterOffboard,
					GPON_ConnectPersonalAP,
					GPON_Disconnect,
					GPON_NextAnnouncement
					);


			existTest = true;
			String verdict =ControlPanelTest.getVerdict();
			this.Verdict=verdict;


			verdictKey=verdict;






		}else if(test.equals("CONFIGURATION")){

			System.out.println("====================================================");
			System.out.println("ICSCF_ConfigurationServiceFramework: "+ICSCF_ConfigurationServiceFramework);
			System.out.println("ICSCF_ConfigurationInterface: "+ICSCF_ConfigurationInterface);
			System.out.println("ICSCF_FactoryResetMethod: "+ICSCF_FactoryResetMethod);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCF_ConfigVersion: "+IXITCF_ConfigVersion);
			System.out.println("IXITCF_Passcode: "+IXITCF_Passcode);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPCF_SessionLost: "+GPCF_SessionLost);
			System.out.println("GPCF_SessionClose: "+GPCF_SessionClose);

			ConfigurationService ConfigurationTest=new ConfigurationService(
					testName,
					ICSCF_ConfigurationServiceFramework,
					ICSCF_ConfigurationInterface,
					ICSCF_FactoryResetMethod,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITCF_ConfigVersion,
					IXITCF_Passcode,
					GPCO_AnnouncementTimeout,
					GPCF_SessionLost,
					GPCF_SessionClose
					);
			existTest = true;
			String verdict =ConfigurationTest.getVerdict();
			this.Verdict=verdict;

			verdictKey=verdict;
		}else if(test.equals("AUDIO")){

			System.out.println("====================================================");
			System.out.println("ICSAU_AudioServiceFramework: "+ICSAU_AudioServiceFramework);
			System.out.println("ICSAU_StreamInterface: "+ICSAU_StreamInterface);
			System.out.println("ICSAU_StreamPortInterface: "+ICSAU_StreamPortInterface);
			System.out.println("ICSAU_PortAudioSinkInterface: "+ICSAU_PortAudioSinkInterface);
			System.out.println("ICSAU_PortAudioSourceInterface: "+ICSAU_PortAudioSourceInterface);
			System.out.println("ICSAU_PortImageSinkInterface: "+ICSAU_PortImageSinkInterface);
			System.out.println("ICSAU_PortImageSourceInterface: "+ICSAU_PortImageSourceInterface);
			System.out.println("ICSAU_PortApplicationMetadataSinkInterface: "+ICSAU_PortApplicationMetadataSinkInterface);
			System.out.println("ICSAU_PortApplicationMetadataSourceInterface: "+ICSAU_PortApplicationMetadataSourceInterface);
			System.out.println("ICSAU_ControlVolumeInterface: "+ICSAU_ControlVolumeInterface);
			System.out.println("ICSAU_StreamClockInterface: "+ICSAU_StreamClockInterface);
			System.out.println("ICSAU_AudioXalac: "+ICSAU_AudioXalac);
			System.out.println("ICSAU_ImageJpeg: "+ICSAU_ImageJpeg);
			System.out.println("ICSAU_ApplicationXmetadata: "+ICSAU_ApplicationXmetadata);
			System.out.println("ICSAU_VolumeControl: "+ICSAU_VolumeControl);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITAU_StreamVersion: "+IXITAU_StreamVersion);
			System.out.println("IXITAU_TestObjectPath: "+IXITAU_TestObjectPath);
			System.out.println("IXITAU_PortVersion: "+IXITAU_PortVersion);
			System.out.println("IXITAU_AudioSinkVersion: "+IXITAU_AudioSinkVersion);
			System.out.println("IXITAU_timeNanos: "+IXITAU_timeNanos);
			System.out.println("IXITAU_AudioSourceVersion: "+IXITAU_AudioSourceVersion);
			System.out.println("IXITAU_ImageSinkVersion: "+IXITAU_ImageSinkVersion);
			System.out.println("IXITAU_ImageSourceVersion: "+IXITAU_ImageSourceVersion);
			System.out.println("IXITAU_MetadataSinkVersion: "+IXITAU_MetadataSinkVersion);
			System.out.println("IXITAU_MetadataSourceVersion: "+IXITAU_MetadataSourceVersion);
			System.out.println("IXITAU_ControlVolumeVersion: "+IXITAU_ControlVolumeVersion);
			System.out.println("IXITAU_delta: "+IXITAU_delta);
			System.out.println("IXITAU_change: "+IXITAU_change);
			System.out.println("IXITAU_ClockVersion: "+IXITAU_ClockVersion);
			System.out.println("IXITAU_adjustNanos: "+IXITAU_adjustNanos);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPAU_Signal: "+GPAU_Signal);
			System.out.println("GPAU_Link: "+GPAU_Link);

			AudioService AudioTest=new AudioService(
					testName,
					ICSAU_AudioServiceFramework,
					ICSAU_StreamInterface,
					ICSAU_StreamPortInterface,
					ICSAU_PortAudioSinkInterface,
					ICSAU_PortAudioSourceInterface,
					ICSAU_PortImageSinkInterface,
					ICSAU_PortImageSourceInterface,
					ICSAU_PortApplicationMetadataSinkInterface,
					ICSAU_PortApplicationMetadataSourceInterface,
					ICSAU_ControlVolumeInterface,
					ICSAU_StreamClockInterface,
					ICSAU_AudioXalac,
					ICSAU_ImageJpeg,
					ICSAU_ApplicationXmetadata,
					ICSAU_VolumeControl,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITAU_StreamVersion,
					IXITAU_TestObjectPath,
					IXITAU_PortVersion,
					IXITAU_AudioSinkVersion,
					IXITAU_timeNanos,
					IXITAU_AudioSourceVersion,
					IXITAU_ImageSinkVersion,
					IXITAU_ImageSourceVersion,
					IXITAU_MetadataSinkVersion,
					IXITAU_MetadataSourceVersion,
					IXITAU_ControlVolumeVersion,
					IXITAU_delta,
					IXITAU_change,
					IXITAU_ClockVersion,
					IXITAU_adjustNanos,
					GPCO_AnnouncementTimeout,
					GPAU_Signal,
					GPAU_Link
					);
			existTest = true;
			String verdict =AudioTest.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;

		}else if(test.equals("LIGHTING")){

			System.out.println("====================================================");
			System.out.println("ICSL_LightingServiceFramework: "+ICSL_LightingServiceFramework);
			System.out.println("ICSL_LampServiceInterface: "+ICSL_LampServiceInterface);
			System.out.println("ICSL_LampParametersInterface: "+ICSL_LampParametersInterface);
			System.out.println("ICSL_LampDetailsInterface: "+ICSL_LampDetailsInterface);
			System.out.println("ICSL_Dimmable: "+ICSL_Dimmable);
			System.out.println("ICSL_Color: "+ICSL_Color);
			System.out.println("ICSL_ColorTemperature: "+ICSL_ColorTemperature);
			System.out.println("ICSL_Effects: "+ICSL_Effects);
			System.out.println("ICSL_LampStateInterface: "+ICSL_LampStateInterface);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITL_LampServiceVersion: "+IXITL_LampServiceVersion);
			System.out.println("IXITL_LampParametersVersion: "+IXITL_LampParametersVersion);
			System.out.println("IXITL_LampDetailsVersion: "+IXITL_LampDetailsVersion);
			System.out.println("IXITL_LampStateVersion: "+IXITL_LampStateVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPL_SessionClose: "+GPL_SessionClose);

			LightingService lightingTest=new LightingService(
					testName,
					ICSL_LightingServiceFramework,
					ICSL_LampServiceInterface,
					ICSL_LampParametersInterface,
					ICSL_LampDetailsInterface,
					ICSL_Dimmable,
					ICSL_Color,
					ICSL_ColorTemperature,
					ICSL_Effects,
					ICSL_LampStateInterface,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITL_LampServiceVersion,
					IXITL_LampParametersVersion,
					IXITL_LampDetailsVersion,
					IXITL_LampStateVersion,
					GPCO_AnnouncementTimeout,
					GPL_SessionClose
					);
			existTest = true;
			String verdict =lightingTest.getVerdict();

			this.Verdict=verdict;
			verdictKey=verdict;
		}else if(test.equals("LIGHTINGCONTROLLER")){

			System.out.println("====================================================");
			System.out.println("ICSLC_LightingControllerServiceFramework: "+ICSLC_LightingControllerServiceFramework);
			System.out.println("ICSLC_ControllerServiceInterface: "+ICSLC_ControllerServiceInterface);
			System.out.println("ICSLC_ControllerServiceLampInterface: "+ICSLC_ControllerServiceLampInterface);
			System.out.println("ICSLC_ControllerServiceLampGroupInterface: "+ICSLC_ControllerServiceLampGroupInterface);
			System.out.println("ICSLC_ControllerServicePresetInterface: "+ICSLC_ControllerServicePresetInterface);
			System.out.println("ICSLC_ControllerServiceSceneInterface: "+ICSLC_ControllerServiceSceneInterface);
			System.out.println("ICSLC_ControllerServiceMasterSceneInterface: "+ICSLC_ControllerServiceMasterSceneInterface);
			System.out.println("ICSLC_LeaderElectionAndStateSyncInterface: "+ICSLC_LeaderElectionAndStateSyncInterface);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITLC_ControllerServiceVersion: "+IXITLC_ControllerServiceVersion);
			System.out.println("IXITLC_ControllerServiceLampVersion: "+IXITLC_ControllerServiceLampVersion);
			System.out.println("IXITLC_ControllerServiceLampGroupVersion: "+IXITLC_ControllerServiceLampGroupVersion);
			System.out.println("IXITLC_ControllerServicePresetVersion: "+IXITLC_ControllerServicePresetVersion);
			System.out.println("IXITLC_ControllerServiceSceneVersion: "+IXITLC_ControllerServiceSceneVersion);
			System.out.println("IXITLC_ControllerServiceMasterSceneVersion: "+IXITLC_ControllerServiceMasterSceneVersion);
			System.out.println("IXITLC_LeaderElectionAndStateSyncVersion: "+IXITLC_LeaderElectionAndStateSyncVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPLC_SessionClose: "+GPLC_SessionClose);

			LightingControllerService lightingControllerService=new LightingControllerService(
					testName,
					ICSLC_LightingControllerServiceFramework,
					ICSLC_ControllerServiceInterface,

					ICSLC_ControllerServiceLampInterface,
					ICSLC_ControllerServiceLampGroupInterface,
					ICSLC_ControllerServicePresetInterface,
					ICSLC_ControllerServiceSceneInterface,
					ICSLC_ControllerServiceMasterSceneInterface,
					ICSLC_LeaderElectionAndStateSyncInterface,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITLC_ControllerServiceVersion,
					IXITLC_ControllerServiceLampVersion,
					IXITLC_ControllerServiceLampGroupVersion,
					IXITLC_ControllerServicePresetVersion,
					IXITLC_ControllerServiceSceneVersion,
					IXITLC_ControllerServiceMasterSceneVersion,
					IXITLC_LeaderElectionAndStateSyncVersion,
					GPCO_AnnouncementTimeout,
					GPLC_SessionClose
					);
			existTest = true;
			String verdict =lightingControllerService.getVerdict();

			this.Verdict=verdict;
			verdictKey=verdict;




		}else if(test.equals("TIME")){

			System.out.println("====================================================");
			System.out.println("ICST_TimeServiceFramework: "+ICST_TimeServiceFramework);
			System.out.println("ICST_ClockInterface: "+ICST_ClockInterface);
			System.out.println("ICST_Date: "+ICST_Date);
			System.out.println("ICST_Milliseconds: "+ICST_Milliseconds);
			System.out.println("ICST_TimeAuthorityInterface: "+ICST_TimeAuthorityInterface);
			System.out.println("ICST_AlarmFactoryInterface: "+ICST_AlarmFactoryInterface);
			System.out.println("ICST_AlarmInterface: "+ICST_AlarmInterface);
			System.out.println("ICST_TimerFactoryInterface: "+ICST_TimerFactoryInterface);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITT_ClockVersion: "+IXITT_ClockVersion);
			System.out.println("IXITT_TimeAuthorityVersion: "+IXITT_TimeAuthorityVersion);
			System.out.println("IXITT_AlarmFactoryVersion: "+IXITT_AlarmFactoryVersion);
			System.out.println("IXITT_AlarmVersion: "+IXITT_AlarmVersion);
			System.out.println("IXITT_TimerFactoryVersion: "+IXITT_TimerFactoryVersion);
			System.out.println("IXITT_TimerVersion: "+IXITT_TimerVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);

			TimeService TimeTest=new TimeService(
					testName,
					ICST_TimeServiceFramework,
					ICST_ClockInterface,
					ICST_Date,
					ICST_Milliseconds,
					ICST_TimeAuthorityInterface,
					ICST_AlarmFactoryInterface,
					ICST_AlarmInterface,
					ICST_TimerFactoryInterface,
					ICST_TimerInterface,
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //

					IXITT_ClockVersion,
					IXITT_TimeAuthorityVersion,
					IXITT_AlarmFactoryVersion,
					IXITT_AlarmVersion,
					IXITT_TimerFactoryVersion,
					IXITT_TimerVersion,
					GPCO_AnnouncementTimeout);
			existTest = true;
			String verdict =TimeTest.getVerdict();
			this.Verdict=verdict;

			verdictKey=verdict;

		}else if(test.equals("GATEWAY")){

			System.out.println("====================================================");
			System.out.println("ICSG_GatewayServiceFramework: "+ICSG_GatewayServiceFramework);
			System.out.println("ICSG_ProfileManagementInterface: "+ICSG_ProfileManagementInterface);
			System.out.println("ICSG_AppAccessInterface: "+ICSG_AppAccessInterface);
			System.out.println("ICSG_AppManagementInterface: "+ICSG_AppManagementInterface);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITG_AppMgmtVersion: "+IXITG_AppMgmtVersion);
			System.out.println("IXITG_CtrlAppVersion: "+IXITG_CtrlAppVersion);
			System.out.println("IXITG_CtrlAccessVersion: "+IXITG_CtrlAccessVersion);
			System.out.println("IXITG_CtrlAclVersion: "+IXITG_CtrlAclVersion);
			System.out.println("IXITG_ConnAppVersion: "+IXITG_ConnAppVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPG_SessionClose: "+GPG_SessionClose);

			GatewayService GatewayTest=new GatewayService(
					testName,
					ICSG_GatewayServiceFramework,
					ICSG_ProfileManagementInterface,
					ICSG_AppAccessInterface,
					ICSG_AppManagementInterface,

					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITG_AppMgmtVersion,
					IXITG_CtrlAppVersion,
					IXITG_CtrlAccessVersion,
					IXITG_CtrlAclVersion,
					IXITG_ConnAppVersion,
					GPCO_AnnouncementTimeout,
					GPG_SessionClose);
			existTest = true;
			String verdict =GatewayTest.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;
		}else if(test.equals("SMARTHOME")){

			System.out.println("====================================================");
			System.out.println("ICSSH_SmartHomeServiceFramework: "+ICSSH_SmartHomeServiceFramework);
			System.out.println("ICSSH_CentralizedManagementInterface: "+ICSSH_CentralizedManagementInterface);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITSH_WellKnownName: "+IXITSH_WellKnownName);
			System.out.println("IXITSH_UniqueName: "+IXITSH_UniqueName);
			System.out.println("IXITSH_DeviceId: "+IXITSH_DeviceId);
			System.out.println("IXITSH_HeartBeatInterval: "+IXITSH_HeartBeatInterval);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPSH_Signal: "+GPSH_Signal);

			SmartHomeService SmartHomeTest=new SmartHomeService(
					testName,
					ICSSH_SmartHomeServiceFramework,
					ICSSH_CentralizedManagementInterface,							
					IXITCO_AppId,  //
					IXITCO_DeviceId,  //
					IXITCO_DefaultLanguage,  //
					IXITSH_CentralizedManagementVersion,
					IXITSH_WellKnownName,
					IXITSH_UniqueName,
					IXITSH_DeviceId,
					IXITSH_HeartBeatInterval,
					GPCO_AnnouncementTimeout,
					GPSH_Signal);
			existTest = true;

			String verdict =SmartHomeTest.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;


		}else if(test.equals("IOP_ABOUT")){



			System.out.println("====================================================");
			showGU();
			System.out.println("ICSCO_DateOfManufacture: "+ICSCO_DateOfManufacture);
			System.out.println("ICSCO_HardwareVersion: "+ICSCO_HardwareVersion);
			System.out.println("ICSCO_SupportUrl: "+ICSCO_SupportUrl);
			System.out.println("ICSCO_IconInterface: "+ICSCO_IconInterface);
			System.out.println("ICSCO_DeviceName: "+ICSCO_DeviceName);

			System.out.println("IXITCO_AboutVersion: "+IXITCO_AboutVersion);
			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCO_DeviceName: "+IXITCO_DeviceName);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_AppName: "+IXITCO_AppName);
			System.out.println("IXITCO_Manufacturer: "+IXITCO_Manufacturer);
			System.out.println("IXITCO_ModelNumber: "+IXITCO_ModelNumber);
			System.out.println("IXITCO_SoftwareVersion: "+IXITCO_SoftwareVersion);
			System.out.println("IXITCO_AJSoftwareVersion: "+IXITCO_AJSoftwareVersion);
			System.out.println("IXITCO_HardwareVersion: "+IXITCO_HardwareVersion);
			System.out.println("IXITCO_IntrospectableVersion: "+IXITCO_IntrospectableVersion);
			System.out.println("IXITCO_SupportedLanguages: "+IXITCO_SupportedLanguages);
			System.out.println("IXITCO_Description: "+IXITCO_Description);
			System.out.println("IXITCO_DateOfManufacture: "+IXITCO_DateOfManufacture);
			System.out.println("IXITCO_SupportUrl: "+IXITCO_SupportUrl);
			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);


			AboutIOP aboutIOP=new AboutIOP(testName);
			existTest = true;

			String verdict =aboutIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;

		}else if(test.equals("IOP_CONFIG")){

			System.out.println("====================================================");
			showGU();
			System.out.println("ICSCF_ConfigurationServiceFramework: "+ICSCF_ConfigurationServiceFramework);
			System.out.println("ICSCF_ConfigurationInterface: "+ICSCF_ConfigurationInterface);
			System.out.println("ICSCF_FactoryResetMethod: "+ICSCF_FactoryResetMethod);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCF_ConfigVersion: "+IXITCF_ConfigVersion);
			System.out.println("IXITCF_Passcode: "+IXITCF_Passcode);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPCF_SessionLost: "+GPCF_SessionLost);
			System.out.println("GPCF_SessionClose: "+GPCF_SessionClose);
			ConfigIOP configIOP=new ConfigIOP(testName);
			existTest = true;

			String verdict =configIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;


		}else if(test.equals("IOP_ONBOARDING")){

			System.out.println("====================================================");
			showGU();
			System.out.println("ICSON_OnboardingServiceFramework: "+ICSON_OnboardingServiceFramework);
			System.out.println("ICSON_OnboardingInterface: "+ICSON_OnboardingInterface);
			System.out.println("ICSON_ChannelSwitching: "+ICSON_ChannelSwitching);
			System.out.println("ICSON_GetScanInfoMethod: "+ICSON_GetScanInfoMethod);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITON_OnboardingVersion: "+IXITON_OnboardingVersion);
			System.out.println("IXITON_SoftAP: "+IXITON_SoftAP);
			System.out.println("IXITON_SoftAPAuthType: "+IXITON_SoftAPAuthType);
			System.out.println("IXITON_SoftAPpassphrase: "+IXITON_SoftAPpassphrase);
			System.out.println("IXITON_PersonalAP: "+IXITON_PersonalAP);
			System.out.println("IXITON_PersonalAPAuthType: "+IXITON_PersonalAPAuthType);
			System.out.println("IXITON_PersonalAPpassphrase: "+IXITON_PersonalAPpassphrase);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPON_WaitSoftAP: "+GPON_WaitSoftAP);
			System.out.println("GPON_ConnectSoftAP: "+GPON_ConnectSoftAP);
			System.out.println("GPON_WaitSoftAPAfterOffboard: "+GPON_WaitSoftAPAfterOffboard);
			System.out.println("GPON_ConnectPersonalAP: "+GPON_ConnectPersonalAP);
			System.out.println("GPON_Disconnect: "+GPON_Disconnect);
			System.out.println("GPON_NextAnnouncement: "+GPON_NextAnnouncement);

			OnboardingIOP onboardingIOP=new OnboardingIOP(testName);
			existTest = true;

			String verdict =onboardingIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;


		}else if(test.equals("IOP_CONTROLPANEL")){

			System.out.println("====================================================");
			showGU();
			System.out.println("ICSCP_ControlPanelServiceFramework: "+ICSCP_ControlPanelServiceFramework);
			System.out.println("ICSCP_ControlPanelInterface: "+ICSCP_ControlPanelInterface);
			System.out.println("ICSCP_ContainerInterface: "+ICSCP_ContainerInterface);
			System.out.println("ICSCP_SecuredContainerInterface: "+ICSCP_SecuredContainerInterface);
			System.out.println("ICSCP_PropertyInterface: "+ICSCP_PropertyInterface);
			System.out.println("ICSCP_SecuredPropertyInterface: "+ICSCP_SecuredPropertyInterface);
			System.out.println("ICSCP_LabelPropertyInterface: "+ICSCP_LabelPropertyInterface);
			System.out.println("ICSCP_ActionInterface: "+ICSCP_ActionInterface);
			System.out.println("ICSCP_SecuredActionInterface: "+ICSCP_SecuredActionInterface);
			System.out.println("ICSCP_NotificationActionInterface: "+ICSCP_NotificationActionInterface);
			System.out.println("ICSCP_DialogInterface: "+ICSCP_DialogInterface);
			System.out.println("ICSCP_DI_Action2: "+ICSCP_DI_Action2);
			System.out.println("ICSCP_DI_Action3: "+ICSCP_DI_Action3);
			System.out.println("ICSCP_ListPropertyInterface: "+ICSCP_ListPropertyInterface);
			System.out.println("ICSCP_SecuredListPropertyInterface: "+ICSCP_SecuredListPropertyInterface);
			System.out.println("ICSCP_HTTPControlInterface: "+ICSCP_HTTPControlInterface);


			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITCP_ControlPanelVersion: "+IXITCP_ControlPanelVersion);
			System.out.println("IXITCP_ContainerVersion: "+IXITCP_ContainerVersion);
			System.out.println("IXITCP_PropertyVersion: "+IXITCP_PropertyVersion);
			System.out.println("IXITCP_LabelPropertyVersion: "+IXITCP_LabelPropertyVersion);
			System.out.println("IXITCP_ActionVersion: "+IXITCP_ActionVersion);
			System.out.println("IXITCP_NotificationActionVersion: "+IXITCP_NotificationActionVersion);
			System.out.println("IXITCP_DialogVersion: "+IXITCP_DialogVersion);
			System.out.println("IXITCP_ListPropertyVersion: "+IXITCP_ListPropertyVersion);
			System.out.println("IXITCP_HTTPControlVersion: "+IXITCP_HTTPControlVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);

			ControlPanelIOP controlPanelIOP=new ControlPanelIOP(testName);
			existTest = true;

			String verdict =controlPanelIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;

		}else if(test.equals("IOP_NOTIFICATION")){


			System.out.println("====================================================");
			showGU();
			System.out.println("ICSN_NotificationServiceFramework: "+ICSN_NotificationServiceFramework);
			System.out.println("ICSN_NotificationInterface: "+ICSN_NotificationInterface);
			System.out.println("ICSN_RichIconUrl: "+ICSN_RichIconUrl);
			System.out.println("ICSN_RichAudioUrl: "+ICSN_RichAudioUrl);
			System.out.println("ICSN_RespObjectPath: "+ICSN_RespObjectPath);
			System.out.println("ICSN_NotificationProducerInterface: "+ICSN_NotificationProducerInterface);
			System.out.println("ICSN_DismisserInterface: "+ICSN_DismisserInterface);
			System.out.println("ICSN_NotificationConsumer: "+ICSN_NotificationConsumer);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITN_NotificationVersion: "+IXITN_NotificationVersion);
			System.out.println("IXITN_TTL: "+IXITN_TTL);
			System.out.println("IXITN_NotificationProducerVersion: "+IXITN_NotificationProducerVersion);
			System.out.println("IXITN_NotificationDismisserVersion: "+IXITN_NotificationDismisserVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);

			NotificationIOP notificationIOP=new NotificationIOP(testName);
			existTest = true;

			String verdict =notificationIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;
		}else if(test.equals("IOP_AUDIO")){

			System.out.println("====================================================");
			showGU();
			System.out.println("ICSAU_AudioServiceFramework: "+ICSAU_AudioServiceFramework);
			System.out.println("ICSAU_StreamInterface: "+ICSAU_StreamInterface);
			System.out.println("ICSAU_StreamPortInterface: "+ICSAU_StreamPortInterface);
			System.out.println("ICSAU_PortAudioSinkInterface: "+ICSAU_PortAudioSinkInterface);
			System.out.println("ICSAU_PortAudioSourceInterface: "+ICSAU_PortAudioSourceInterface);
			System.out.println("ICSAU_PortImageSinkInterface: "+ICSAU_PortImageSinkInterface);
			System.out.println("ICSAU_PortImageSourceInterface: "+ICSAU_PortImageSourceInterface);
			System.out.println("ICSAU_PortApplicationMetadataSinkInterface: "+ICSAU_PortApplicationMetadataSinkInterface);
			System.out.println("ICSAU_PortApplicationMetadataSourceInterface: "+ICSAU_PortApplicationMetadataSourceInterface);
			System.out.println("ICSAU_ControlVolumeInterface: "+ICSAU_ControlVolumeInterface);
			System.out.println("ICSAU_StreamClockInterface: "+ICSAU_StreamClockInterface);
			System.out.println("ICSAU_AudioXalac: "+ICSAU_AudioXalac);
			System.out.println("ICSAU_ImageJpeg: "+ICSAU_ImageJpeg);
			System.out.println("ICSAU_ApplicationXmetadata: "+ICSAU_ApplicationXmetadata);
			System.out.println("ICSAU_VolumeControl: "+ICSAU_VolumeControl);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITAU_StreamVersion: "+IXITAU_StreamVersion);
			System.out.println("IXITAU_TestObjectPath: "+IXITAU_TestObjectPath);
			System.out.println("IXITAU_PortVersion: "+IXITAU_PortVersion);
			System.out.println("IXITAU_AudioSinkVersion: "+IXITAU_AudioSinkVersion);
			System.out.println("IXITAU_timeNanos: "+IXITAU_timeNanos);
			System.out.println("IXITAU_AudioSourceVersion: "+IXITAU_AudioSourceVersion);
			System.out.println("IXITAU_ImageSinkVersion: "+IXITAU_ImageSinkVersion);
			System.out.println("IXITAU_ImageSourceVersion: "+IXITAU_ImageSourceVersion);
			System.out.println("IXITAU_MetadataSinkVersion: "+IXITAU_MetadataSinkVersion);
			System.out.println("IXITAU_MetadataSourceVersion: "+IXITAU_MetadataSourceVersion);
			System.out.println("IXITAU_ControlVolumeVersion: "+IXITAU_ControlVolumeVersion);
			System.out.println("IXITAU_delta: "+IXITAU_delta);
			System.out.println("IXITAU_change: "+IXITAU_change);
			System.out.println("IXITAU_ClockVersion: "+IXITAU_ClockVersion);
			System.out.println("IXITAU_adjustNanos: "+IXITAU_adjustNanos);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPAU_Signal: "+GPAU_Signal);
			System.out.println("GPAU_Link: "+GPAU_Link);
			AudioIOP audioIOP=new AudioIOP(testName);
			existTest = true;

			String verdict =audioIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;
		}else if(test.equals("IOP_LSF")){

			System.out.println("====================================================");
			showGU();
			System.out.println("ICSL_LightingServiceFramework: "+ICSL_LightingServiceFramework);
			System.out.println("ICSL_LampServiceInterface: "+ICSL_LampServiceInterface);
			System.out.println("ICSL_LampParametersInterface: "+ICSL_LampParametersInterface);
			System.out.println("ICSL_LampDetailsInterface: "+ICSL_LampDetailsInterface);
			System.out.println("ICSL_Dimmable: "+ICSL_Dimmable);
			System.out.println("ICSL_Color: "+ICSL_Color);
			System.out.println("ICSL_ColorTemperature: "+ICSL_ColorTemperature);
			System.out.println("ICSL_Effects: "+ICSL_Effects);
			System.out.println("ICSL_LampStateInterface: "+ICSL_LampStateInterface);

			System.out.println("IXITCO_AppId: "+IXITCO_AppId);
			System.out.println("IXITCO_DeviceId: "+IXITCO_DeviceId);
			System.out.println("IXITCO_DefaultLanguage: "+IXITCO_DefaultLanguage);
			System.out.println("IXITL_LampServiceVersion: "+IXITL_LampServiceVersion);
			System.out.println("IXITL_LampParametersVersion: "+IXITL_LampParametersVersion);
			System.out.println("IXITL_LampDetailsVersion: "+IXITL_LampDetailsVersion);
			System.out.println("IXITL_LampStateVersion: "+IXITL_LampStateVersion);

			System.out.println("GPCO_AnnouncementTimeout: "+GPCO_AnnouncementTimeout);
			System.out.println("GPL_SessionClose: "+GPL_SessionClose);
			LightingIOP lightingIOP=new LightingIOP(testName);
			existTest = true;

			String verdict =lightingIOP.getVerdict();
			this.Verdict=verdict;
			verdictKey=verdict;
		}else{
			logger.error("Not such service: "+test);
			verdictKey="NOT SUCH TESTCASE";
			existTest=true;
		}

		logger.info("finished:"+testName);
		logger.info("Final Verdict: "+Verdict);


		return existTest;
	}




	/**
	 * Show the Goldens Units.
	 */
	private void showGU() {
		NodeList goldenUnit = doc.getElementsByTagName("GoldenUnit");




		for (int i = 0; i < goldenUnit.getLength(); i++) {
			Node node = goldenUnit.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String goldenUnitName=getValue("Name", element);
				int num=i+1;
				System.out.println("Golden Unit "+num+": "+goldenUnitName);


			}
		}

	}

	/**
	 * Gets the verdict of the testcase.
	 *
	 * @return the verdict
	 */
	public String getVerdict(){


		return Verdict;
	}

	/**
	 * Gets the time stamp of the testcase.
	 *
	 * @return the time stamp
	 */
	public String getTimeStamp(){


		return TimeStamp;
	}




	/**
	 * Checks if the test case is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning(){
		return running;

	}

	/**
	 * Generate Results.xml to store the values from the execution.
	 *
	 * @param iDKey the Testcase ID 
	 * @param testKey the testcase name
	 * @param descriptionKey the testcase description
	 * @param dateTimeKey the testcase date time 
	 * @param verdictKey the testcase verdict 
	 * @param versionKey the testcase version 
	 * @param logKey the testcase log
	 */
	private static void generateXML(String iDKey,String testKey,
			String descriptionKey, String dateTimeKey,
			String verdictKey, String versionKey, String logKey) {
		Document document=null;
		String name="Results";
		File test = new File(name+".xml");
		if(test.exists()&&test.isFile()){//Exists and is a file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				document = dBuilder.parse(test);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DOMImplementation implementation = builder.getDOMImplementation();

			document = implementation.createDocument(null, name, null);
			document.setXmlVersion("1.0");


		}
		//Main Node
		Element raiz = document.getDocumentElement();
		//Item Node
		Element itemNode = document.createElement("TestCase"); 
		Element idNode = document.createElement("Id"); 
		Text idValue = document.createTextNode( iDKey);
		idNode.appendChild(idValue);  
		//Key Node
		Element keyNode = document.createElement("Name"); 
		Text nodeKeyValue = document.createTextNode(testKey);
		keyNode.appendChild(nodeKeyValue);      
		Element descriptionNode = document.createElement("Description"); 
		Text descriptionValue = document.createTextNode( descriptionKey);
		descriptionNode.appendChild(descriptionValue); 
		Element dateTimeNode = document.createElement("DateTime"); 
		Text dateTimeValue = document.createTextNode( dateTimeKey);
		dateTimeNode.appendChild(dateTimeValue); 
		//Value Node
		Element valueNode = document.createElement("Verdict"); 
		String passed= verdictKey;
		Text nodeValueValue = document.createTextNode(passed);                
		valueNode.appendChild(nodeValueValue);
		Element versionNode = document.createElement("Version"); 
		Text versionValue = document.createTextNode( versionKey);
		versionNode.appendChild(versionValue); 

		Element logNode = document.createElement("LogFile"); 
		Text logValue = document.createTextNode( logKey+".log");
		logNode.appendChild(logValue); 
		itemNode.appendChild(idNode);
		itemNode.appendChild(keyNode);
		itemNode.appendChild(descriptionNode);
		itemNode.appendChild(dateTimeNode);
		itemNode.appendChild(valueNode);
		itemNode.appendChild(versionNode); 
		itemNode.appendChild(logNode);
		//append itemNode to raiz
		raiz.appendChild(itemNode); //pegamos el elemento a la raiz "Documento"
		//Generate XML
		Source source = new DOMSource(document);
		Result result = new StreamResult(new java.io.File(name+".xml")); //nombre del archivo
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the test Service.
	 *
	 * @param testName the test name
	 * @return the test
	 */
	private static String getTestService(String testName) {
		String test="";
		if(testName.startsWith("About")||testName.startsWith("EventsActions")){
			test="ABOUT";
		}else if(testName.startsWith("Notification")){
			test="NOTIFICATION";
		}else if(testName.startsWith("ControlPanel")){
			test="CONTROLPANEL";
		}else if(testName.startsWith("Onboarding")){
			test="ONBOARDING";
		}else if(testName.startsWith("Config")){
			test="CONFIGURATION";
		}else if(testName.startsWith("Audio")){
			test="AUDIO";
		}else if(testName.startsWith("LSF_Lamp")){
			test="LIGHTING";
		}else if(testName.startsWith("LSF_Controller")){
			test="LIGHTINGCONTROLLER";
		}else if(testName.startsWith("TimeService")){
			test="TIME";
		}else if(testName.startsWith("GWAgent")){
			test="GATEWAY";
		}else if(testName.startsWith("SmartHome")){
			test="SMARTHOME";
		}else if(testName.startsWith("IOP_About")){
			test="IOP_ABOUT";
		}else if(testName.startsWith("IOP_Config")){
			test="IOP_CONFIG";
		}else if(testName.startsWith("IOP_ControlPanel")){
			test="IOP_CONTROLPANEL";
		}else if(testName.startsWith("IOP_Onboarding")){
			test="IOP_ONBOARDING";
		}else if(testName.startsWith("IOP_Notification")){
			test="IOP_NOTIFICATION";
		}else if(testName.startsWith("IOP_Audio")){
			test="IOP_AUDIO";
		}else if(testName.startsWith("IOP_LSF")){
			test="IOP_LSF";

		}else{
			System.out.println("ERROR: INVALID SERVICE");
		}


		return test;
	}






	/**
	 * Gets the ICS and store it in each ICS variable.
	 *
	 * @param icsName the ics name
	 * @param icsValue the ics value
	 */
	private static void setIcs(String icsName, String icsValue) {
		if(icsName.equals("ICSCO_DateOfManufacture")){
			ICSCO_DateOfManufacture= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCO_HardwareVersion")){
			ICSCO_HardwareVersion= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCO_SupportUrl")){
			ICSCO_SupportUrl= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCO_IconInterface")){
			ICSCO_IconInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCO_DeviceName")){ //JTF
			ICSCO_DeviceName= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_NotificationServiceFramework")){
			ICSN_NotificationServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_NotificationInterface")){
			ICSN_NotificationInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_RichIconUrl")){
			ICSN_RichIconUrl= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_RichAudioUrl")){
			ICSN_RichAudioUrl= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_RespObjectPath")){
			ICSN_RespObjectPath= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_NotificationProducerInterface")){
			ICSN_NotificationProducerInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_DismisserInterface")){
			ICSN_DismisserInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSN_NotificationConsumer")) {
			ICSN_NotificationConsumer= Boolean.parseBoolean(icsValue); //JTF
		}else if(icsName.equals("ICSCP_ControlPanelServiceFramework")){
			ICSCP_ControlPanelServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_ControlPanelInterface")){
			ICSCP_ControlPanelInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_ContainerInterface")){
			ICSCP_ContainerInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SecuredContainerInterface")){
			ICSCP_SecuredContainerInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_PropertyInterface")){
			ICSCP_PropertyInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SecuredPropertyInterface")){
			ICSCP_SecuredPropertyInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_LabelPropertyInterface")){
			ICSCP_LabelPropertyInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_ActionInterface")){
			ICSCP_ActionInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SecuredActionInterface")){
			ICSCP_SecuredActionInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_NotificationActionInterface")){
			ICSCP_NotificationActionInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_DialogInterface")){
			ICSCP_DialogInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_DI_Action2")){
			ICSCP_DI_Action2= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_DI_Action3")){
			ICSCP_DI_Action3= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SecuredDialogInterface")){
			ICSCP_SecuredDialogInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SDI_Action2")){
			ICSCP_SDI_Action2= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SDI_Action3")){
			ICSCP_SDI_Action3= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_ListPropertyInterface")){
			ICSCP_ListPropertyInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_SecuredListPropertyInterface")){
			ICSCP_SecuredListPropertyInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCP_HTTPControlInterface")){
			ICSCP_HTTPControlInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCF_ConfigurationServiceFramework")){
			ICSCF_ConfigurationServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCF_ConfigurationInterface")){
			ICSCF_ConfigurationInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSCF_FactoryResetMethod")){
			ICSCF_FactoryResetMethod= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_AudioServiceFramework")){
			ICSAU_AudioServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_StreamInterface")){
			ICSAU_StreamInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_StreamPortInterface")){
			ICSAU_StreamPortInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortAudioSinkInterface")){
			ICSAU_PortAudioSinkInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortAudioSourceInterface")){
			ICSAU_PortAudioSourceInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortImageSinkInterface")){
			ICSAU_PortImageSinkInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortImageSourceInterface")){
			ICSAU_PortImageSourceInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortApplicationMetadataSinkInterface")){
			ICSAU_PortApplicationMetadataSinkInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_PortApplicationMetadataSourceInterface")){
			ICSAU_PortApplicationMetadataSourceInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_ControlVolumeInterface")){
			ICSAU_ControlVolumeInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_StreamClockInterface")){
			ICSAU_StreamClockInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_AudioXalac")){
			ICSAU_AudioXalac= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_ImageJpeg")){
			ICSAU_ImageJpeg= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_ApplicationXmetadata")){
			ICSAU_ApplicationXmetadata= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSAU_VolumeControl")){
			ICSAU_VolumeControl= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_LightingServiceFramework")){
			ICSL_LightingServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_LampServiceInterface")){
			ICSL_LampServiceInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_LampParametersInterface")){
			ICSL_LampParametersInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_LampDetailsInterface")){
			ICSL_LampDetailsInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_Dimmable")){
			ICSL_Dimmable= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_Color")){
			ICSL_Color= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_ColorTemperature")){
			ICSL_ColorTemperature= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_Effects")){
			ICSL_Effects= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSL_LampStateInterface")){
			ICSL_LampStateInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_TimeServiceFramework")){
			ICST_TimeServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_ClockInterface")){
			ICST_ClockInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_Date")){
			ICST_Date= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_Milliseconds")){
			ICST_Milliseconds= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_TimeAuthorityInterface")){
			ICST_TimeAuthorityInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_AlarmFactoryInterface")){
			ICST_AlarmFactoryInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_AlarmInterface")){
			ICST_AlarmInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_TimerFactoryInterface")){
			ICST_TimerFactoryInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICST_TimerInterface")){
			ICST_TimerInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSG_GatewayServiceFramework")){
			ICSG_GatewayServiceFramework=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSG_ProfileManagementInterface")){
			ICSG_ProfileManagementInterface=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSG_AppAccessInterface")){
			ICSG_AppAccessInterface=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSG_AppManagementInterface")){
			ICSG_AppManagementInterface=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSSH_SmartHomeServiceFramework")){
			ICSSH_SmartHomeServiceFramework=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSSH_CentralizedManagementInterface")){
			ICSSH_CentralizedManagementInterface=Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_LightingControllerServiceFramework")){
			ICSLC_LightingControllerServiceFramework= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServiceInterface")){
			ICSLC_ControllerServiceInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServiceLampInterface")){
			ICSLC_ControllerServiceLampInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServiceLampGroupInterface")){
			ICSLC_ControllerServiceLampGroupInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServicePresetInterface")){
			ICSLC_ControllerServicePresetInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServiceSceneInterface")){
			ICSLC_ControllerServiceSceneInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_ControllerServiceMasterSceneInterface")){
			ICSLC_ControllerServiceMasterSceneInterface= Boolean.parseBoolean(icsValue);
		}else if(icsName.equals("ICSLC_LeaderElectionAndStateSyncInterface")){
			ICSLC_LeaderElectionAndStateSyncInterface= Boolean.parseBoolean(icsValue);
		}else{
			System.out.println("ERROR: INVALID ICS Name "+icsName);

		}

	}

	/**
	 * Gets the IXIT and store it in each IXIT variable.
	 *
	 * @param ixitName the ixit name
	 * @param ixitValue the ixit value
	 */
	private static void setIxit(String ixitName, String ixitValue) {
		if(ixitName.equals("IXITCO_AboutVersion")){
			IXITCO_AboutVersion=ixitValue;
		}else if(ixitName.equals("IXITCO_AppId")){
			IXITCO_AppId=ixitValue;
		}else if(ixitName.equals("IXITCO_DefaultLanguage")){
			IXITCO_DefaultLanguage=ixitValue;
		}else if(ixitName.equals("IXITCO_DeviceName")){
			IXITCO_DeviceName=ixitValue;
		}else if(ixitName.equals("IXITCO_DeviceId")){
			IXITCO_DeviceId=ixitValue;
		}else if(ixitName.equals("IXITCO_AppName")){
			IXITCO_AppName=ixitValue;
		}else if(ixitName.equals("IXITCO_Manufacturer")){
			IXITCO_Manufacturer=ixitValue;
		}else if(ixitName.equals("IXITCO_ModelNumber")){
			IXITCO_ModelNumber=ixitValue;
		}else if(ixitName.equals("IXITCO_SoftwareVersion")){
			IXITCO_SoftwareVersion=ixitValue;
		}else if(ixitName.equals("IXITCO_AJSoftwareVersion")){
			IXITCO_AJSoftwareVersion=ixitValue;
		}else if(ixitName.equals("IXITCO_HardwareVersion")){
			IXITCO_HardwareVersion=ixitValue;
		}else if(ixitName.equals("IXITCO_IntrospectableVersion")){
			IXITCO_IntrospectableVersion=ixitValue;
		}else if(ixitName.equals("IXITCO_SupportedLanguages")) {
			IXITCO_SupportedLanguages=ixitValue;
		}else if(ixitName.equals("IXITCO_Description")){
			IXITCO_Description=ixitValue;
		}else if(ixitName.equals("IXITCO_DateOfManufacture")){
			IXITCO_DateOfManufacture=ixitValue;
		}else if(ixitName.equals("IXITCO_SupportUrl")){
			IXITCO_SupportUrl=ixitValue;
		}else if(ixitName.equals("IXITN_NotificationVersion")){
			IXITN_NotificationVersion=ixitValue;
		}else if(ixitName.equals("IXITN_TTL")){
			IXITN_TTL=ixitValue;	
		}else if(ixitName.equals("IXITN_NotificationProducerVersion")){
			IXITN_NotificationProducerVersion=ixitValue;
		}else if(ixitName.equals("IXITN_NotificationDismisserVersion")){
			IXITN_NotificationDismisserVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_ControlPanelVersion")){
			IXITCP_ControlPanelVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_ContainerVersion")){
			IXITCP_ContainerVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_PropertyVersion")){
			IXITCP_PropertyVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_LabelPropertyVersion")){
			IXITCP_LabelPropertyVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_ActionVersion")){
			IXITCP_ActionVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_NotificationActionVersion")){
			IXITCP_NotificationActionVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_DialogVersion")){
			IXITCP_DialogVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_ListPropertyVersion")){
			IXITCP_ListPropertyVersion=ixitValue;
		}else if(ixitName.equals("IXITCP_HTTPControlVersion")){
			IXITCP_HTTPControlVersion=ixitValue;
		}else if(ixitName.equals("IXITCF_ConfigVersion")){
			IXITCF_ConfigVersion=ixitValue;
		}else if(ixitName.equals("IXITCF_Passcode")){
			IXITCF_Passcode=ixitValue;
		}else if(ixitName.equals("IXITAU_StreamVersion")){
			IXITAU_StreamVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_TestObjectPath")){
			IXITAU_TestObjectPath=ixitValue;
		}else if(ixitName.equals("IXITAU_PortVersion")){
			IXITAU_PortVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_AudioSinkVersion")){
			IXITAU_AudioSinkVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_timeNanos")){
			IXITAU_timeNanos=ixitValue;
		}else if(ixitName.equals("IXITAU_AudioSourceVersion")){
			IXITAU_AudioSourceVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_ImageSinkVersion")){
			IXITAU_ImageSinkVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_ImageSourceVersion")){
			IXITAU_ImageSourceVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_MetadataSinkVersion")){
			IXITAU_MetadataSinkVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_MetadataSourceVersion")){
			IXITAU_MetadataSourceVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_ControlVolumeVersion")){
			IXITAU_ControlVolumeVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_delta")){
			IXITAU_delta=ixitValue;
		}else if(ixitName.equals("IXITAU_change")){
			IXITAU_change=ixitValue;
		}else if(ixitName.equals("IXITAU_ClockVersion")){
			IXITAU_ClockVersion=ixitValue;
		}else if(ixitName.equals("IXITAU_adjustNanos")){
			IXITAU_adjustNanos=ixitValue;
		}else if(ixitName.equals("IXITL_LampServiceVersion")){
			IXITL_LampServiceVersion=ixitValue;
		}else if(ixitName.equals("IXITL_LampParametersVersion")){
			IXITL_LampParametersVersion=ixitValue;
		}else if(ixitName.equals("IXITL_LampDetailsVersion")){
			IXITL_LampDetailsVersion=ixitValue;
		}else if(ixitName.equals("IXITL_LampStateVersion")){
			IXITL_LampStateVersion=ixitValue;
		}else if(ixitName.equals("IXITT_ClockVersion")){
			IXITT_ClockVersion=ixitValue;
		}else if(ixitName.equals("IXITT_TimeAuthorityVersion")){
			IXITT_TimeAuthorityVersion=ixitValue;
		}else if(ixitName.equals("IXITT_AlarmFactoryVersion")){
			IXITT_AlarmFactoryVersion=ixitValue;
		}else if(ixitName.equals("IXITT_AlarmVersion")){
			IXITT_AlarmVersion=ixitValue;
		}else if(ixitName.equals("IXITT_TimerFactoryVersion")){
			IXITT_TimerFactoryVersion=ixitValue;
		}else if(ixitName.equals("IXITT_TimerVersion")){
			IXITT_TimerVersion=ixitValue;
		}else if(ixitName.equals("IXITG_AppMgmtVersion")){
			IXITG_AppMgmtVersion=ixitValue;
		}else if(ixitName.equals("IXITG_CtrlAppVersion")){
			IXITG_CtrlAppVersion=ixitValue;
		}else if(ixitName.equals("IXITG_CtrlAccessVersion")){
			IXITG_CtrlAccessVersion=ixitValue;
		}else if(ixitName.equals("IXITG_CtrlAclVersion")){
			IXITG_CtrlAclVersion=ixitValue;
		}else if(ixitName.equals("IXITG_ConnAppVersion")){
			IXITG_ConnAppVersion=ixitValue;			
		}else if(ixitName.equals("IXITSH_CentralizedManagementVersion")){
			IXITSH_CentralizedManagementVersion=ixitValue;
		}else if(ixitName.equals("IXITSH_WellKnownName")){
			IXITSH_WellKnownName=ixitValue;
		}else if(ixitName.equals("IXITSH_UniqueName")){
			IXITSH_UniqueName=ixitValue;
		}else if(ixitName.equals("IXITSH_DeviceId")){
			IXITSH_DeviceId=ixitValue;
		}else if(ixitName.equals("IXITSH_HeartBeatInterval")){
			IXITSH_HeartBeatInterval=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServiceVersion")){
			IXITLC_ControllerServiceVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServiceLampVersion")){
			IXITLC_ControllerServiceLampVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServiceLampGroupVersion")){
			IXITLC_ControllerServiceLampGroupVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServicePresetVersion")){
			IXITLC_ControllerServicePresetVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServiceSceneVersion")){
			IXITLC_ControllerServiceSceneVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_ControllerServiceMasterSceneVersion")){
			IXITLC_ControllerServiceMasterSceneVersion=ixitValue;
		}else if(ixitName.equals("IXITLC_LeaderElectionAndStateSyncVersion")){
			IXITLC_LeaderElectionAndStateSyncVersion=ixitValue;
		}else if(ixitName.equals("IXITON_OnboardingVersion")){
			IXITON_OnboardingVersion=ixitValue;
		}else if(ixitName.equals("IXITON_SoftAP")){
			IXITON_SoftAP=ixitValue;
		}else if(ixitName.equals("IXITON_SoftAPAuthType")){
			IXITON_SoftAPAuthType=ixitValue;
		}else if(ixitName.equals("IXITON_SoftAPpassphrase")){
			IXITON_SoftAPpassphrase=ixitValue;
		}else if(ixitName.equals("IXITON_PersonalAP")){
			IXITON_PersonalAP=ixitValue;
		}else if(ixitName.equals("IXITON_PersonalAPAuthType")){
			IXITON_PersonalAPAuthType=ixitValue;
		}else if(ixitName.equals("IXITON_PersonalAPpassphrase")){
			IXITON_PersonalAPpassphrase=ixitValue;
		}else{
			System.out.println("ERROR: INVALID IXIT Name "+ixitName);
		}

	}

	/**
	 * Gets the Generals Parameters and store it into each variable.
	 *
	 * @param pName the name
	 * @param pValue the value
	 */
	private static void setParameter(String pName, String pValue) {

		if(pName.equals("GPCO_AnnouncementTimeout")) {
			GPCO_AnnouncementTimeout = pValue;
		} else if (pName.equals("GPON_WaitSoftAP")) {
			GPON_WaitSoftAP = pValue;
		} else if (pName.equals("GPON_ConnectSoftAP")) {
			GPON_ConnectSoftAP = pValue;
		} else if (pName.equals("GPON_WaitSoftAPAfterOffboard")) {
			GPON_WaitSoftAPAfterOffboard = pValue;
		} else if (pName.equals("GPON_ConnectPersonalAP")) {
			GPON_ConnectPersonalAP = pValue;
		} else if (pName.equals("GPON_Disconnect")) {
			GPON_Disconnect = pValue;
		} else if (pName.equals("GPON_NextAnnouncement")) {
			GPON_NextAnnouncement = pValue;
		} else if (pName.equals("GPCF_SessionLost")) {
			GPCF_SessionLost = pValue;
		} else if (pName.equals("GPCF_SessionClose")) {
			GPCF_SessionClose = pValue;
		} else if (pName.equals("GPL_SessionClose")) {
			GPL_SessionClose = pValue;
		} else if (pName.equals("GPAU_Signal")) {
			GPAU_Signal = pValue;
		} else if (pName.equals("GPAU_Link")) {
			GPAU_Link = pValue;
		} else if (pName.equals("GPG_SessionClose")) {
			GPG_SessionClose = pValue;
		} else if (pName.equals("GPSH_Signal")) {
			GPSH_Signal = pValue;
		} else if (pName.equals("GPLC_SessionClose")) {
			GPLC_SessionClose = pValue;
		} else{
			System.out.println("ERROR: INVALID General Parameter Name "+pName);
		}
	}

	/**
	 * Gets the value from the selected tag. 
	 *
	 * @param tag the tag
	 * @param element the element
	 * @return the value
	 */
	private static String getValue(String tag, Element element) {
		String value="";
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		if(node!=null){
			value=node.getNodeValue();
		}
		return value;
	}
}		
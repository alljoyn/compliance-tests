/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.at4wireless.alljoyn.testcases.conf.about.AboutTestSuite;
import com.at4wireless.alljoyn.testcases.conf.audio.AudioTestSuite;
import com.at4wireless.alljoyn.testcases.conf.configuration.ConfigurationTestSuite;
import com.at4wireless.alljoyn.testcases.conf.controlpanel.ControlPanelTestSuite;
import com.at4wireless.alljoyn.testcases.conf.gateway.GWAgentTestSuite;
import com.at4wireless.alljoyn.testcases.conf.lighting.LightingTestSuite;
import com.at4wireless.alljoyn.testcases.conf.lightingcontroller.LightingControllerTestSuite;
import com.at4wireless.alljoyn.testcases.conf.notification.NotificationTestSuite;
import com.at4wireless.alljoyn.testcases.conf.onboarding.OnboardingTestSuite;
import com.at4wireless.alljoyn.testcases.conf.smarthome.SmartHomeTestSuite;
import com.at4wireless.alljoyn.testcases.conf.time.TimeTestSuite;
import com.at4wireless.alljoyn.testcases.iop.about.AboutIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.audio.AudioIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.config.ConfigIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.controlpanel.ControlPanelIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.lighting.LightingIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.notification.NotificationIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.onboarding.OnboardingIOPTestSuite;

/**
 * This Class handles the execution of a certain Test Case, loading from XML
 * all the parameters that are needed. 
 * It is also in charge of the creation of the XML with the results of the execution
 */
public class Manager extends Thread
{
	static
	{
		System.loadLibrary("alljoyn_java");
	}
	
	protected static final String TAG = "TestRunner";
	private static final WindowsLoggerImpl logger =  LoggerFactory.getLogger(TAG);
	private static final String versionKey = "14.12.00b";
	
	private Map<String, List<String>> goldenUnits = new HashMap<String,List<String>>();
	String testKey = null;
	String verdictKey = null;
	String descriptionKey = null;
	String dateTimeKey = null;
	String iDKey = null;
	String logKey = null;
	boolean running = false;
	Timestamp sqlTimestamp;
	private Document doc;
	private String testName;
	private  String Verdict = null; //[AT4] Duplicated (verdictKey). It has to be removed
	private String TimeStamp = null; //[AT4] Duplicated (dateTimeKey). It has to be removed
	String test = null;
	static List<String> testNames = new ArrayList<String>();

	static boolean ICSCO_DateOfManufacture = false; //[AT4] ICS Values have to be static?
	static boolean ICSCO_HardwareVersion = false;
	static boolean ICSCO_SupportUrl = false;
	static boolean ICSCO_IconInterface = false;
	static boolean ICSCO_DeviceName = false;
	static boolean ICSN_NotificationServiceFramework = false;
	static boolean ICSN_NotificationInterface = false;
	static boolean ICSN_RichIconUrl = false;
	static boolean ICSN_RichAudioUrl = false;
	static boolean ICSN_RespObjectPath = false;
	static boolean ICSN_NotificationProducerInterface = false;
	static boolean ICSN_DismisserInterface = false;
	static boolean ICSN_NotificationConsumer = false;
	static boolean ICSON_OnboardingServiceFramework = false;
	static boolean ICSON_OnboardingInterface = false;
	static boolean ICSON_ChannelSwitching = false;
	static boolean ICSON_GetScanInfoMethod = false;	
	static boolean ICSCP_ControlPanelServiceFramework = false;
	static boolean ICSCP_ControlPanelInterface = false;
	static boolean ICSCP_ContainerInterface = false;
	static boolean ICSCP_SecuredContainerInterface = false;
	static boolean ICSCP_PropertyInterface = false;
	static boolean ICSCP_SecuredPropertyInterface = false;
	static boolean ICSCP_LabelPropertyInterface = false;
	static boolean ICSCP_ActionInterface = false;
	static boolean ICSCP_SecuredActionInterface = false;
	static boolean ICSCP_NotificationActionInterface = false;
	static boolean ICSCP_DialogInterface = false;
	static boolean ICSCP_DI_Action2 = false;
	static boolean ICSCP_DI_Action3 = false;
	static boolean ICSCP_SecuredDialogInterface = false;
	static boolean ICSCP_SDI_Action2 = false;
	static boolean ICSCP_SDI_Action3 = false;
	static boolean ICSCP_ListPropertyInterface = false;
	static boolean ICSCP_SecuredListPropertyInterface = false;
	static boolean ICSCP_HTTPControlInterface = false;
	static boolean ICSCF_ConfigurationServiceFramework = false;
	static boolean ICSCF_ConfigurationInterface = false;
	static boolean ICSCF_FactoryResetMethod = false;
	static boolean ICSAU_AudioServiceFramework = false;
	static boolean ICSAU_StreamInterface = false;
	static boolean ICSAU_StreamPortInterface = false;
	static boolean ICSAU_PortAudioSinkInterface = false;
	static boolean ICSAU_PortAudioSourceInterface = false;
	static boolean ICSAU_PortImageSinkInterface = false;
	static boolean ICSAU_PortImageSourceInterface = false;
	static boolean ICSAU_PortApplicationMetadataSinkInterface = false;
	static boolean ICSAU_PortApplicationMetadataSourceInterface = false;
	static boolean ICSAU_ControlVolumeInterface = false;
	static boolean ICSAU_StreamClockInterface = false;
	static boolean ICSAU_AudioXalac = false;
	static boolean ICSAU_ImageJpeg = false;
	static boolean ICSAU_ApplicationXmetadata = false;
	static boolean ICSAU_VolumeControlEnabled = false;
	static boolean ICSL_LightingServiceFramework = false;
	static boolean ICSL_LampServiceInterface = false;
	static boolean ICSL_LampParametersInterface = false;
	static boolean ICSL_LampDetailsInterface = false;
	static boolean ICSL_Dimmable = false;
	static boolean ICSL_Color = false;
	static boolean ICSL_ColorTemperature = false;
	static boolean ICSL_Effects = false;
	static boolean ICSL_LampStateInterface = false;
	static boolean ICST_TimeServiceFramework = false;
	static boolean ICST_ClockInterface = false;
	static boolean ICST_Date = false;
	static boolean ICST_Milliseconds = false;
	static boolean ICST_TimeAuthorityInterface = false;
	static boolean ICST_AlarmFactoryInterface = false;
	static boolean ICST_AlarmInterface = false;
	static boolean ICST_TimerFactoryInterface = false;
	static boolean ICST_TimerInterface = false;
	static boolean ICSG_GatewayServiceFramework = false;
	static boolean ICSG_ProfileManagementInterface = false;
	static boolean ICSG_AppAccessInterface = false;
	static boolean ICSG_AppManagementInterface = false;
	static boolean ICSSH_SmartHomeServiceFramework = false;
	static boolean ICSSH_CentralizedManagementInterface = false;
	static boolean ICSLC_LightingControllerServiceFramework = false;
	static boolean ICSLC_ControllerServiceInterface = false;
	static boolean ICSLC_ControllerServiceLampInterface = false;
	static boolean ICSLC_ControllerServiceLampGroupInterface = false;
	static boolean ICSLC_ControllerServicePresetInterface = false;
	static boolean ICSLC_ControllerServiceSceneInterface = false;
	static boolean ICSLC_ControllerServiceMasterSceneInterface = false;
	static boolean ICSLC_LeaderElectionAndStateSyncInterface = false;

	static String IXITCO_AboutVersion = null;
	static String IXITCO_AppId = null;
	static String IXITCO_DefaultLanguage = null;
	static String IXITCO_DeviceName = null;
	static String IXITCO_DeviceId = null;
	static String IXITCO_AppName = null;
	static String IXITCO_Manufacturer = null;
	static String IXITCO_ModelNumber = null;
	static String IXITCO_SoftwareVersion = null;
	static String IXITCO_AJSoftwareVersion = null;
	static String IXITCO_HardwareVersion = null;
	static String IXITCO_IntrospectableVersion = null;
	static String IXITCO_SupportedLanguages = null;
	static String IXITCO_Description = null;
	static String IXITCO_DateOfManufacture = null;
	static String IXITCO_SupportUrl = null;
	static String IXITN_NotificationVersion = null;
	static String IXITN_TTL = null;
	static String IXITN_NotificationProducerVersion = null;
	static String IXITN_NotificationDismisserVersion = null;
	static String IXITON_OnboardingVersion = null;
	static String IXITON_SoftAP = null;
	static String IXITON_SoftAPAuthType = null;
	static String IXITON_SoftAPpassphrase = null;
	static String IXITON_PersonalAP = null;
	static String IXITON_PersonalAPAuthType = null;
	static String IXITON_PersonalAPpassphrase = null;
	static String IXITCP_ControlPanelVersion = null;
	static String IXITCP_ContainerVersion = null;
	static String IXITCP_PropertyVersion = null;
	static String IXITCP_LabelPropertyVersion = null;
	static String IXITCP_ActionVersion = null;
	static String IXITCP_NotificationActionVersion = null;
	static String IXITCP_DialogVersion = null;
	static String IXITCP_ListPropertyVersion = null;
	static String IXITCP_HTTPControlVersion = null;
	static String IXITCF_ConfigVersion = null;
	static String IXITCF_Passcode = null;
	static String IXITAU_StreamVersion = null;
	static String IXITAU_TestObjectPath = null;
	static String IXITAU_PortVersion = null;
	static String IXITAU_AudioSinkVersion = null;
	static String IXITAU_timeNanos = null;
	static String IXITAU_AudioSourceVersion = null;
	static String IXITAU_ImageSinkVersion = null;
	static String IXITAU_ImageSourceVersion = null;
	static String IXITAU_MetadataSinkVersion = null;
	static String IXITAU_MetadataSourceVersion = null;
	static String IXITAU_ControlVolumeVersion = null;
	static String IXITAU_delta = null;
	static String IXITAU_change = null;
	static String IXITAU_ClockVersion = null;
	static String IXITAU_adjustNanos = null;
	static String IXITL_LampServiceVersion = null;
	static String IXITL_LampParametersVersion = null;
	static String IXITL_LampDetailsVersion = null;
	static String IXITL_LampStateVersion = null;
	static String IXITT_ClockVersion = null;
	static String IXITT_TimeAuthorityVersion = null;
	static String IXITT_AlarmFactoryVersion = null;
	static String IXITT_AlarmVersion = null;
	static String IXITT_TimerFactoryVersion = null;
	static String IXITT_TimerVersion = null;
	static String IXITG_AppMgmtVersion = null;
	static String IXITG_CtrlAppVersion = null;
	static String IXITG_CtrlAccessVersion = null;
	static String IXITG_CtrlAclVersion = null;
	static String IXITG_ConnAppVersion = null;
	static String IXITSH_CentralizedManagementVersion = null;
	static String IXITSH_WellKnownName = null;
	static String IXITSH_UniqueName = null;
	static String IXITSH_DeviceId = null;
	static String IXITSH_HeartBeatInterval = null;
	static String IXITLC_ControllerServiceVersion = null;
	static String IXITLC_ControllerServiceLampVersion = null;
	static String IXITLC_ControllerServiceLampGroupVersion = null;
	static String IXITLC_ControllerServicePresetVersion = null;
	static String IXITLC_ControllerServiceSceneVersion = null;
	static String IXITLC_ControllerServiceMasterSceneVersion = null;
	static String IXITLC_LeaderElectionAndStateSyncVersion = null;
	
	static String GPCO_AnnouncementTimeout = null;
	static String GPON_WaitSoftAP = null;
	static String GPON_ConnectSoftAP = null;
	static String GPON_WaitSoftAPAfterOffboard = null;
	static String GPON_ConnectPersonalAP = null;
	static String GPON_Disconnect = null;
	static String GPON_NextAnnouncement = null;
	static String GPCF_SessionLost = null;
	static String GPCF_SessionClose = null;
	static String GPL_SessionClose = null;
	static String GPAU_Signal = null;
	static String GPAU_Link = null;
	static String GPG_SessionClose = null;
	static String GPSH_Signal = null;
	static String GPLC_SessionClose = null;

	/**
	 * Main method, included here to be able to make a Runnable package
	 * when the JAR file is exported
	 * 
	 * @param arg
	 * 		Input arguments. Two arguments needed:
	 * 			- arg[0]	:	Test Case name
	 * 			- arg[1]	:	location of the configuration.xml file
	 */
	public static void main(final String arg[])
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Manager manager = new Manager(arg[0], arg[1]); 
					manager.run();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Class constructor.
	 * 
	 * @param testName
	 * 			Name of the Test Case to be executed. If the Test Case does not
	 * 			exist, the execution will finish with an INCONCLUSE and the message
	 * 			"Test Case not valid".
	 * @param docName
	 * 			Location of the configuration.xml file associated with the project
	 * 			whose Test Case is going to be executed.
	 */
	public Manager(String testName, String docName)
	{
		this.testName = testName;
		String path = docName;
		byte[] encoded;
		
		try
		{
			encoded = Files.readAllBytes(Paths.get(path));
			String xml = new String(encoded, StandardCharsets.UTF_8);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			dBuilder = dbFactory.newDocumentBuilder();
			this.doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method reads all necessary parameters (ICS, IXIT, General Parameters) from XML
	 * and saves them into variables. After that, it starts the execution of the Test Case.
	 * 
	 * While this method is active 'running' remains set to TRUE, being set to FALSE
	 * when the execution finishes.
	 */
	public void run()
	{		
		running = true;
		
		try
		{ 
			NodeList testCases = doc.getElementsByTagName("TestCase");
			System.out.println("====================================================");
			
			for (int i = 0; i < testCases.getLength(); i++)
			{
				Node node = testCases.item(i);
				Element element= (Element) node;
				String testName = getValue("Name", element);
				
				if (testName.equals(this.testName))
				{
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						String id = getValue("Id", element);
						iDKey = id;

						System.out.println("Test Name: " + testName);
						testKey = testName;
						String description = getValue("Description", element);
						System.out.println("Description: " + description);
						descriptionKey = description;
						test = getTest(testName);
					}
				}
			}
			
			NodeList ics = doc.getElementsByTagName("Ics");
			
			for (int i = 0; i < ics.getLength(); i++)
			{
				Node node = ics.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					String icsName = getValue("Name", element);
					String icsValue = getValue("Value", element);
					setIcs(icsName, icsValue);
				}
			}
			
			NodeList ixit = doc.getElementsByTagName("Ixit");
			//System.out.println("==========================");

			for (int i = 0; i < ixit.getLength(); i++)
			{
				Node node = ixit.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					String ixitName = getValue("Name", element);
					String ixitValue = getValue("Value", element);
					setIxit(ixitName, ixitValue);
				}
			}

			NodeList parameter = doc.getElementsByTagName("Parameter");
			//System.out.println("==========================");

			for (int i = 0; i < parameter.getLength(); i++)
			{
				Node node = parameter.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					String parameterName = getValue("Name", element);
					String parameterValue = getValue("Value", element);
					setParameter(parameterName, parameterValue);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("====================================================");
		runTest();

		String timeLog = new SimpleDateFormat("yyyyMMdd-HHmmss").format(sqlTimestamp);
		logKey = "Log-" + testName + "-" + timeLog;

		//Generate XML
		generateXML(iDKey, testKey, descriptionKey, dateTimeKey, verdictKey, versionKey, logKey);
		running = false;
	}

	/**
	 * This method manages the execution of a certain Test Case, stored in 'testName'
	 * 
	 * @return	TRUE if the Test Case exists, FALSE otherwise
	 */
	private boolean runTest()
	{
		logger.info("started:" + testName);	
		Verdict = "";
		boolean existTest = false;

		Date utilDate = new java.util.Date();
		long lnMilisec = utilDate.getTime();

		sqlTimestamp = new java.sql.Timestamp(lnMilisec);

		String timeStamp = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(sqlTimestamp);

		this.TimeStamp = timeStamp;
		dateTimeKey = timeStamp;

		if (test.equals("ABOUT"))
		{
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

			AboutTestSuite AboutTest = new AboutTestSuite(
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
			String verdict = AboutTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("NOTIFICATION"))
		{
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

			NotificationTestSuite NotificationTest = new NotificationTestSuite(
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
			String verdict = NotificationTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("CONTROLPANEL"))
		{
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

			ControlPanelTestSuite ControlPanelTest = new ControlPanelTestSuite(
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
			String verdict = ControlPanelTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("ONBOARDING"))
		{
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

			OnboardingTestSuite ControlPanelTest = new OnboardingTestSuite(
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
			String verdict = ControlPanelTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("CONFIGURATION"))
		{
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

			ConfigurationTestSuite ConfigurationTest = new ConfigurationTestSuite(
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
			String verdict = ConfigurationTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("AUDIO"))
		{
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
			System.out.println("ICSAU_VolumeControl: "+ICSAU_VolumeControlEnabled);

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

			AudioTestSuite AudioTest = new AudioTestSuite(
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
					ICSAU_VolumeControlEnabled,
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
			String verdict = AudioTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("LIGHTING"))
		{
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

			LightingTestSuite lightingTest = new LightingTestSuite(
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
			String verdict = lightingTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("LIGHTINGCONTROLLER"))
		{
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

			LightingControllerTestSuite lightingControllerService = new LightingControllerTestSuite(
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
			String verdict = lightingControllerService.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("TIME"))
		{
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

			TimeTestSuite TimeTest = new TimeTestSuite(
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
			String verdict = TimeTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("GATEWAY"))
		{
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

			GWAgentTestSuite GatewayTest = new GWAgentTestSuite(
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
			String verdict =GatewayTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("SMARTHOME"))
		{
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

			SmartHomeTestSuite SmartHomeTest = new SmartHomeTestSuite(
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
			String verdict =SmartHomeTest.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_ABOUT"))
		{
			System.out.println("====================================================");
			setGU();
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
			System.out.println("IXITCO_ApGpName: "+IXITCO_AppName);
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

			AboutIOPTestSuite aboutIOP = new AboutIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = aboutIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_CONFIG"))
		{
			System.out.println("====================================================");
			setGU();
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
			
			ConfigIOPTestSuite configIOP = new ConfigIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = configIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_ONBOARDING"))
		{
			System.out.println("====================================================");
			setGU();
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

			OnboardingIOPTestSuite onboardingIOP = new OnboardingIOPTestSuite(
					testName,
					goldenUnits);
			
			existTest = true;
			String verdict = onboardingIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_CONTROLPANEL"))
		{
			System.out.println("====================================================");
			setGU();
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

			ControlPanelIOPTestSuite controlPanelIOP = new ControlPanelIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = controlPanelIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_NOTIFICATION"))
		{
			System.out.println("====================================================");
			setGU();
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

			NotificationIOPTestSuite notificationIOP = new NotificationIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = notificationIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_AUDIO"))
		{
			System.out.println("====================================================");
			setGU();
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
			System.out.println("ICSAU_VolumeControl: "+ICSAU_VolumeControlEnabled);

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
			
			AudioIOPTestSuite audioIOP = new AudioIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = audioIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else if (test.equals("IOP_LSF"))
		{
			System.out.println("====================================================");
			setGU();
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
			
			LightingIOPTestSuite lightingIOP = new LightingIOPTestSuite(
					testName,
					goldenUnits,
					ICSON_OnboardingServiceFramework);
			
			existTest = true;
			String verdict = lightingIOP.getFinalVerdict();
			this.Verdict = verdict;
			verdictKey = verdict;
		}
		else
		{
			logger.error("Not such service: "+test);
			verdictKey = "NOT SUCH TESTCASE";
			existTest = true;
		}

		logger.info("finished:"+testName);
		logger.info("Final Verdict: "+Verdict);

		return existTest;
	}

	/**
	 * This method reads from XML all the information related to Golden
	 * Units, and stores it into 'goldenUnits' list.
	 */
	private void setGU()
	{
		NodeList goldenUnit = doc.getElementsByTagName("GoldenUnit");
		
		for (int i = 0; i < goldenUnit.getLength(); i++)
		{
			Node node = goldenUnit.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String goldenUnitName = getValue("Name", element);
				String goldenUnitCategory = getValue("Type", element);
				int num = i + 1;
				
				System.out.println("Golden Unit "+num+" name: "+goldenUnitName);
				System.out.println("Golden Unit "+num+" Type: "+goldenUnitCategory);
				
				List<String> gu = goldenUnits.get(goldenUnitCategory);
				
				if (gu != null)
				{
					gu.add(goldenUnitName);
				}
				else
				{
					gu = new ArrayList<String>();
					gu.add(goldenUnitName);
				}
				goldenUnits.put(goldenUnitCategory, gu);
			}
		}
	}
	
	/**
	 * @return the verdict of the execution of the Test Case
	 * 		PASS		The DUT has passed the execution of the Test Case
	 * 		INCONC		The Test Case has returned an error non-related to the behavior of the DUT
	 * 		FAIL		The DUT has failed on the execution of the Test Case
	 */
	public String getVerdict()
	{
		return Verdict;
	}

	/**
	 * @return date and time when the Test Case has been executed
	 */
	public String getTimeStamp()
	{
		return TimeStamp;
	}

	/**
	 * @return TRUE if the Test Case is being executed, FALSE otherwise
	 */
	public boolean isRunning()
	{
		return running;
	}

	private static void generateXML(String iDKey, String testKey, String descriptionKey, String dateTimeKey,
			String verdictKey, String versionKey, String logKey)
	{
		Document document = null;
		String name = "Results";
		File test = new File(name+".xml");

		if (test.exists() && test.isFile())
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			
			try
			{
				dBuilder = dbFactory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try
			{
				document = dBuilder.parse(test);
			}
			catch (SAXException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			
			try
			{
				builder = factory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DOMImplementation implementation = builder.getDOMImplementation();
			document = implementation.createDocument(null, name, null);
			document.setXmlVersion("1.0");
		}

		Element raiz = document.getDocumentElement();
		Element itemNode = document.createElement("TestCase"); 
		Element idNode = document.createElement("Id"); 
		Text idValue = document.createTextNode( iDKey);
		
		idNode.appendChild(idValue);  

		Element keyNode = document.createElement("Name"); 
		Text nodeKeyValue = document.createTextNode(testKey);
		keyNode.appendChild(nodeKeyValue);      

		Element descriptionNode = document.createElement("Description"); 
		Text descriptionValue = document.createTextNode( descriptionKey);
		descriptionNode.appendChild(descriptionValue); 

		Element dateTimeNode = document.createElement("DateTime"); 
		Text dateTimeValue = document.createTextNode( dateTimeKey);
		dateTimeNode.appendChild(dateTimeValue); 

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

		raiz.appendChild(itemNode); //pegamos el elemento a la raiz "Documento"

		Source source = new DOMSource(document);

		Result result = new StreamResult(new java.io.File(name+".xml")); //nombre del archivo
		Transformer transformer = null;
		
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
		}
		catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			transformer.transform(source, result);
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method reads the name of the Test Case to know which Service Framework
	 * has to be called.
	 * 
	 * @param testName
	 * 			The name of the Test Case to be parsed.
	 * 
	 * @return 	the name of the Service Framework that has to be called, if exists. Otherwise,
	 * 			it returns the message "ERROR: INVALID SERVICE".
	 */
	private static String getTest(String testName)
	{
		String test = "";
		
		if (testName.startsWith("About") || testName.startsWith("EventsActions"))
		{
			test = "ABOUT";
		}
		else if (testName.startsWith("Notification"))
		{
			test = "NOTIFICATION";
		}
		else if (testName.startsWith("ControlPanel"))
		{
			test = "CONTROLPANEL";
		}
		else if (testName.startsWith("Onboarding"))
		{
			test = "ONBOARDING";
		}
		else if (testName.startsWith("Config"))
		{
			test = "CONFIGURATION";
		}
		else if (testName.startsWith("Audio"))
		{
			test = "AUDIO";
		}
		else if (testName.startsWith("LSF_Lamp"))
		{
			test = "LIGHTING";
		}
		else if (testName.startsWith("LSF_Controller"))
		{
			test = "LIGHTINGCONTROLLER";
		}
		else if (testName.startsWith("TimeService"))
		{
			test = "TIME";
		}
		else if (testName.startsWith("GWAgent"))
		{
			test = "GATEWAY";
		}
		else if (testName.startsWith("SmartHome"))
		{
			test = "SMARTHOME";
		}
		else if (testName.startsWith("IOP_About"))
		{
			test = "IOP_ABOUT";
		}
		else if (testName.startsWith("IOP_Config"))
		{
			test = "IOP_CONFIG";
		}
		else if (testName.startsWith("IOP_ControlPanel"))
		{
			test = "IOP_CONTROLPANEL";
		}
		else if (testName.startsWith("IOP_Onboarding"))
		{
			test = "IOP_ONBOARDING";
		}
		else if (testName.startsWith("IOP_Notification"))
		{
			test = "IOP_NOTIFICATION";
		}
		else if (testName.startsWith("IOP_Audio"))
		{
			test = "IOP_AUDIO";
		}
		else if (testName.startsWith("IOP_LSF"))
		{
			test = "IOP_LSF";
		}
		else
		{
			System.out.println("ERROR: INVALID SERVICE");
		}
		return test;
	}

	/**
	 * This method stores an ICS into the appropriate variable
	 * 
	 * @param icsName
	 * 			Name of the ICS to be stored
	 * @param icsValue
	 * 			Value of the ICS to be stored
	 */
	private static void setIcs(String icsName, String icsValue)
	{
		if (icsName.equals("ICSCO_DateOfManufacture"))
		{
			ICSCO_DateOfManufacture = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCO_HardwareVersion"))
		{
			ICSCO_HardwareVersion = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCO_SupportUrl"))
		{
			ICSCO_SupportUrl = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCO_IconInterface"))
		{
			ICSCO_IconInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCO_DeviceName"))
		{
			ICSCO_DeviceName = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_NotificationServiceFramework"))
		{
			ICSN_NotificationServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_NotificationInterface"))
		{
			ICSN_NotificationInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_RichIconUrl"))
		{
			ICSN_RichIconUrl = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_RichAudioUrl"))
		{
			ICSN_RichAudioUrl = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_RespObjectPath"))
		{
			ICSN_RespObjectPath = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_NotificationProducerInterface"))
		{
			ICSN_NotificationProducerInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_DismisserInterface"))
		{
			ICSN_DismisserInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSN_NotificationConsumer"))
		{
			ICSN_NotificationConsumer = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_ControlPanelServiceFramework"))
		{
			ICSCP_ControlPanelServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_ControlPanelInterface"))
		{
			ICSCP_ControlPanelInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_ContainerInterface"))
		{
			ICSCP_ContainerInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SecuredContainerInterface"))
		{
			ICSCP_SecuredContainerInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_PropertyInterface"))
		{
			ICSCP_PropertyInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SecuredPropertyInterface"))
		{
			ICSCP_SecuredPropertyInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_LabelPropertyInterface"))
		{
			ICSCP_LabelPropertyInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_ActionInterface"))
		{
			ICSCP_ActionInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SecuredActionInterface"))
		{
			ICSCP_SecuredActionInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_NotificationActionInterface"))
		{
			ICSCP_NotificationActionInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_DialogInterface"))
		{
			ICSCP_DialogInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_DI_Action2"))
		{
			ICSCP_DI_Action2 = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_DI_Action3"))
		{
			ICSCP_DI_Action3 = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SecuredDialogInterface"))
		{
			ICSCP_SecuredDialogInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SDI_Action2"))
		{
			ICSCP_SDI_Action2 = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SDI_Action3"))
		{
			ICSCP_SDI_Action3 = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_ListPropertyInterface"))
		{
			ICSCP_ListPropertyInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_SecuredListPropertyInterface"))
		{
			ICSCP_SecuredListPropertyInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCP_HTTPControlInterface"))
		{
			ICSCP_HTTPControlInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSON_OnboardingServiceFramework"))
		{
			ICSON_OnboardingServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSON_OnboardingInterface"))
		{
			ICSON_OnboardingInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSON_ChannelSwitching"))
		{
			ICSON_ChannelSwitching = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSON_GetScanInfoMethod"))
		{
			ICSON_GetScanInfoMethod = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCF_ConfigurationServiceFramework"))
		{
			ICSCF_ConfigurationServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCF_ConfigurationInterface"))
		{
			ICSCF_ConfigurationInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSCF_FactoryResetMethod"))
		{
			ICSCF_FactoryResetMethod = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_AudioServiceFramework"))
		{
			ICSAU_AudioServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_StreamInterface"))
		{
			ICSAU_StreamInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_StreamPortInterface"))
		{
			ICSAU_StreamPortInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortAudioSinkInterface"))
		{
			ICSAU_PortAudioSinkInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortAudioSourceInterface"))
		{
			ICSAU_PortAudioSourceInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortImageSinkInterface"))
		{
			ICSAU_PortImageSinkInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortImageSourceInterface"))
		{
			ICSAU_PortImageSourceInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortApplicationMetadataSinkInterface"))
		{
			ICSAU_PortApplicationMetadataSinkInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_PortApplicationMetadataSourceInterface"))
		{
			ICSAU_PortApplicationMetadataSourceInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_ControlVolumeInterface"))
		{
			ICSAU_ControlVolumeInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_StreamClockInterface"))
		{
			ICSAU_StreamClockInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_AudioXalac"))
		{
			ICSAU_AudioXalac = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_ImageJpeg"))
		{
			ICSAU_ImageJpeg = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_ApplicationXmetadata"))
		{
			ICSAU_ApplicationXmetadata = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSAU_VolumeControlEnabled"))
		{
			ICSAU_VolumeControlEnabled = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_LightingServiceFramework"))
		{
			ICSL_LightingServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_LampServiceInterface"))
		{
			ICSL_LampServiceInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_LampParametersInterface"))
		{
			ICSL_LampParametersInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_LampDetailsInterface"))
		{
			ICSL_LampDetailsInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_Dimmable"))
		{
			ICSL_Dimmable = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_Color"))
		{
			ICSL_Color = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_ColorTemperature"))
		{
			ICSL_ColorTemperature = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_Effects"))
		{
			ICSL_Effects = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSL_LampStateInterface"))
		{
			ICSL_LampStateInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_TimeServiceFramework"))
		{
			ICST_TimeServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_ClockInterface"))
		{
			ICST_ClockInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_Date"))
		{
			ICST_Date = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_Milliseconds"))
		{
			ICST_Milliseconds = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_TimeAuthorityInterface"))
		{
			ICST_TimeAuthorityInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_AlarmFactoryInterface"))
		{
			ICST_AlarmFactoryInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_AlarmInterface"))
		{
			ICST_AlarmInterface= Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_TimerFactoryInterface"))
		{
			ICST_TimerFactoryInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICST_TimerInterface"))
		{
			ICST_TimerInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSG_GatewayServiceFramework"))
		{
			ICSG_GatewayServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSG_ProfileManagementInterface"))
		{
			ICSG_ProfileManagementInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSG_AppAccessInterface"))
		{
			ICSG_AppAccessInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSG_AppManagementInterface"))
		{
			ICSG_AppManagementInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSSH_SmartHomeServiceFramework"))
		{
			ICSSH_SmartHomeServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSSH_CentralizedManagementInterface"))
		{
			ICSSH_CentralizedManagementInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_LightingControllerServiceFramework"))
		{
			ICSLC_LightingControllerServiceFramework = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServiceInterface"))
		{
			ICSLC_ControllerServiceInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServiceLampInterface"))
		{
			ICSLC_ControllerServiceLampInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServiceLampGroupInterface"))
		{
			ICSLC_ControllerServiceLampGroupInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServicePresetInterface"))
		{
			ICSLC_ControllerServicePresetInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServiceSceneInterface"))
		{
			ICSLC_ControllerServiceSceneInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_ControllerServiceMasterSceneInterface"))
		{
			ICSLC_ControllerServiceMasterSceneInterface = Boolean.parseBoolean(icsValue);
		}
		else if (icsName.equals("ICSLC_LeaderElectionAndStateSyncInterface"))
		{
			ICSLC_LeaderElectionAndStateSyncInterface = Boolean.parseBoolean(icsValue);
		}
		else
		{
			System.out.println("ERROR: INVALID ICS Name "+icsName);
		}
	}

	/**
	 * This method stores an IXIT into the appropriate variable
	 * 
	 * @param ixitName
	 * 			Name of the IXIT to be stored
	 * @param ixitValue
	 * 			Value of the IXIT to be stored
	 */
	private static void setIxit(String ixitName, String ixitValue)
	{
		if (ixitName.equals("IXITCO_AboutVersion"))
		{
			IXITCO_AboutVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCO_AppId"))
		{
			IXITCO_AppId = ixitValue;
		}
		else if (ixitName.equals("IXITCO_DefaultLanguage"))
		{
			IXITCO_DefaultLanguage = ixitValue;
		}
		else if (ixitName.equals("IXITCO_DeviceName"))
		{
			IXITCO_DeviceName = ixitValue;
		}
		else if (ixitName.equals("IXITCO_DeviceId"))
		{
			IXITCO_DeviceId = ixitValue;
		}
		else if (ixitName.equals("IXITCO_AppName"))
		{
			IXITCO_AppName = ixitValue;
		}
		else if (ixitName.equals("IXITCO_Manufacturer"))
		{
			IXITCO_Manufacturer = ixitValue;
		}
		else if (ixitName.equals("IXITCO_ModelNumber"))
		{
			IXITCO_ModelNumber = ixitValue;
		}
		else if (ixitName.equals("IXITCO_SoftwareVersion"))
		{
			IXITCO_SoftwareVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCO_AJSoftwareVersion"))
		{
			IXITCO_AJSoftwareVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCO_HardwareVersion"))
		{
			IXITCO_HardwareVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCO_IntrospectableVersion"))
		{
			IXITCO_IntrospectableVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCO_SupportedLanguages"))
		{
			IXITCO_SupportedLanguages = ixitValue;
		}
		else if (ixitName.equals("IXITCO_Description"))
		{
			IXITCO_Description = ixitValue;
		}
		else if (ixitName.equals("IXITCO_DateOfManufacture"))
		{
			IXITCO_DateOfManufacture = ixitValue;
		}
		else if (ixitName.equals("IXITCO_SupportUrl"))
		{
			IXITCO_SupportUrl = ixitValue;
		}
		else if (ixitName.equals("IXITN_NotificationVersion"))
		{
			IXITN_NotificationVersion = ixitValue;
		}
		else if (ixitName.equals("IXITN_TTL"))
		{
			IXITN_TTL = ixitValue;	
		}
		else if (ixitName.equals("IXITN_NotificationProducerVersion"))
		{
			IXITN_NotificationProducerVersion = ixitValue;
		}
		else if (ixitName.equals("IXITN_NotificationDismisserVersion"))
		{
			IXITN_NotificationDismisserVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_ControlPanelVersion"))
		{
			IXITCP_ControlPanelVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_ContainerVersion"))
		{
			IXITCP_ContainerVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_PropertyVersion"))
		{
			IXITCP_PropertyVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_LabelPropertyVersion"))
		{
			IXITCP_LabelPropertyVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_ActionVersion"))
		{
			IXITCP_ActionVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_NotificationActionVersion"))
		{
			IXITCP_NotificationActionVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_DialogVersion"))
		{
			IXITCP_DialogVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_ListPropertyVersion"))
		{
			IXITCP_ListPropertyVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCP_HTTPControlVersion"))
		{
			IXITCP_HTTPControlVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCF_ConfigVersion"))
		{
			IXITCF_ConfigVersion = ixitValue;
		}
		else if (ixitName.equals("IXITCF_Passcode"))
		{
			IXITCF_Passcode = ixitValue;
		}
		else if (ixitName.equals("IXITAU_StreamVersion"))
		{
			IXITAU_StreamVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_TestObjectPath"))
		{
			IXITAU_TestObjectPath = ixitValue;
		}
		else if (ixitName.equals("IXITAU_PortVersion"))
		{
			IXITAU_PortVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_AudioSinkVersion"))
		{
			IXITAU_AudioSinkVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_timeNanos"))
		{
			IXITAU_timeNanos = ixitValue;
		}
		else if (ixitName.equals("IXITAU_AudioSourceVersion"))
		{
			IXITAU_AudioSourceVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_ImageSinkVersion"))
		{
			IXITAU_ImageSinkVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_ImageSourceVersion"))
		{
			IXITAU_ImageSourceVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_MetadataSinkVersion"))
		{
			IXITAU_MetadataSinkVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_MetadataSourceVersion"))
		{
			IXITAU_MetadataSourceVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_ControlVolumeVersion"))
		{
			IXITAU_ControlVolumeVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_delta"))
		{
			IXITAU_delta = ixitValue;
		}
		else if (ixitName.equals("IXITAU_change"))
		{
			IXITAU_change = ixitValue;
		}
		else if (ixitName.equals("IXITAU_ClockVersion"))
		{
			IXITAU_ClockVersion = ixitValue;
		}
		else if (ixitName.equals("IXITAU_adjustNanos"))
		{
			IXITAU_adjustNanos = ixitValue;
		}
		else if (ixitName.equals("IXITL_LampServiceVersion"))
		{
			IXITL_LampServiceVersion = ixitValue;
		}
		else if (ixitName.equals("IXITL_LampParametersVersion"))
		{
			IXITL_LampParametersVersion = ixitValue;
		}
		else if (ixitName.equals("IXITL_LampDetailsVersion"))
		{
			IXITL_LampDetailsVersion = ixitValue;
		}
		else if (ixitName.equals("IXITL_LampStateVersion"))
		{
			IXITL_LampStateVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_ClockVersion"))
		{
			IXITT_ClockVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_TimeAuthorityVersion"))
		{
			IXITT_TimeAuthorityVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_AlarmFactoryVersion"))
		{
			IXITT_AlarmFactoryVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_AlarmVersion"))
		{
			IXITT_AlarmVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_TimerFactoryVersion"))
		{
			IXITT_TimerFactoryVersion = ixitValue;
		}
		else if (ixitName.equals("IXITT_TimerVersion"))
		{
			IXITT_TimerVersion = ixitValue;
		}
		else if (ixitName.equals("IXITG_AppMgmtVersion"))
		{
			IXITG_AppMgmtVersion = ixitValue;
		}
		else if (ixitName.equals("IXITG_CtrlAppVersion"))
		{
			IXITG_CtrlAppVersion = ixitValue;
		}
		else if (ixitName.equals("IXITG_CtrlAccessVersion"))
		{
			IXITG_CtrlAccessVersion = ixitValue;
		}
		else if (ixitName.equals("IXITG_CtrlAclVersion"))
		{
			IXITG_CtrlAclVersion = ixitValue;
		}
		else if (ixitName.equals("IXITG_ConnAppVersion"))
		{
			IXITG_ConnAppVersion = ixitValue;	
		}
		else if (ixitName.equals("IXITSH_CentralizedManagementVersion"))
		{
			IXITSH_CentralizedManagementVersion = ixitValue;
		}
		else if (ixitName.equals("IXITSH_WellKnownName"))
		{
			IXITSH_WellKnownName = ixitValue;
		}
		else if (ixitName.equals("IXITSH_UniqueName"))
		{
			IXITSH_UniqueName = ixitValue;
		}
		else if (ixitName.equals("IXITSH_DeviceId"))
		{
			IXITSH_DeviceId = ixitValue;
		}
		else if (ixitName.equals("IXITSH_HeartBeatInterval"))
		{
			IXITSH_HeartBeatInterval = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServiceVersion"))
		{
			IXITLC_ControllerServiceVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServiceLampVersion"))
		{
			IXITLC_ControllerServiceLampVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServiceLampGroupVersion"))
		{
			IXITLC_ControllerServiceLampGroupVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServicePresetVersion"))
		{
			IXITLC_ControllerServicePresetVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServiceSceneVersion"))
		{
			IXITLC_ControllerServiceSceneVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_ControllerServiceMasterSceneVersion"))
		{
			IXITLC_ControllerServiceMasterSceneVersion = ixitValue;
		}
		else if (ixitName.equals("IXITLC_LeaderElectionAndStateSyncVersion"))
		{
			IXITLC_LeaderElectionAndStateSyncVersion = ixitValue;
		}
		else if (ixitName.equals("IXITON_OnboardingVersion"))
		{
			IXITON_OnboardingVersion = ixitValue;
		}
		else if (ixitName.equals("IXITON_SoftAP"))
		{
			IXITON_SoftAP = ixitValue;
		}
		else if (ixitName.equals("IXITON_SoftAPAuthType"))
		{
			IXITON_SoftAPAuthType = ixitValue;
		}
		else if (ixitName.equals("IXITON_SoftAPpassphrase"))
		{
			IXITON_SoftAPpassphrase = ixitValue;
		}
		else if (ixitName.equals("IXITON_PersonalAP"))
		{
			IXITON_PersonalAP = ixitValue;
		}
		else if (ixitName.equals("IXITON_PersonalAPAuthType"))
		{
			IXITON_PersonalAPAuthType = ixitValue;
		}
		else if (ixitName.equals("IXITON_PersonalAPpassphrase"))
		{
			IXITON_PersonalAPpassphrase = ixitValue;
		}
		else
		{
			System.out.println("ERROR: INVALID IXIT Name "+ixitName);
		}
	}

	/**
	 * This method stores a General Parameter into the appropriate variable
	 * 
	 * @param GpName
	 * 			Name of the General Parameter to be stored
	 * @param pValue
	 * 			Value of the General Parameter to be stored
	 */
	private static void setParameter(String GpName, String pValue)
	{
		if (GpName.equals("GPCO_AnnouncementTimeout"))
		{
			GPCO_AnnouncementTimeout = pValue;
		}
		else if (GpName.equals("GPON_WaitSoftAP"))
		{
			GPON_WaitSoftAP = pValue;
		}
		else if (GpName.equals("GPON_ConnectSoftAP"))
		{
			GPON_ConnectSoftAP = pValue;
		}
		else if (GpName.equals("GPON_WaitSoftAPAfterOffboard"))
		{
			GPON_WaitSoftAPAfterOffboard = pValue;
		}
		else if (GpName.equals("GPON_ConnectPersonalAP"))
		{
			GPON_ConnectPersonalAP = pValue;
		}
		else if (GpName.equals("GPON_Disconnect"))
		{
			GPON_Disconnect = pValue;
		}
		else if (GpName.equals("GPON_NextAnnouncement"))
		{
			GPON_NextAnnouncement = pValue;
		}
		else if (GpName.equals("GPCF_SessionLost"))
		{
			GPCF_SessionLost = pValue;
		}
		else if (GpName.equals("GPCF_SessionClose"))
		{
			GPCF_SessionClose = pValue;
		}
		else if (GpName.equals("GPL_SessionClose"))
		{
			GPL_SessionClose = pValue;
		}
		else if (GpName.equals("GPAU_Signal"))
		{
			GPAU_Signal = pValue;
		}
		else if (GpName.equals("GPAU_Link"))
		{
			GPAU_Link = pValue;
		}
		else if (GpName.equals("GPG_SessionClose"))
		{
			GPG_SessionClose = pValue;
		}
		else if (GpName.equals("GPSH_Signal"))
		{
			GPSH_Signal = pValue;
		}
		else if (GpName.equals("GPLC_SessionClose"))
		{
			GPLC_SessionClose = pValue;
		}
		else
		{
			System.out.println("ERROR: INVALID General Parameter Name "+GpName);
		}
	}

	/**
	 * Method that reads the value of an element from an XML
	 * 
	 * @param tag
	 * 			Element whose value is going to be read
	 * @param element
	 * 			Root element that acts as container
	 * 
	 * @return	the value of the element read
	 */
	private static String getValue(String tag, Element element)
	{
		String value = "";
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		
		if (node != null)
		{
			value = node.getNodeValue();
		}
		return value;
	}
}
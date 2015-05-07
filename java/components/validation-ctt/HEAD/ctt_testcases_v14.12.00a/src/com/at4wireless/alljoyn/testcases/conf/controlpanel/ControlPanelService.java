package com.at4wireless.alljoyn.testcases.conf.controlpanel;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialog;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Container;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ContainerSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ContainerSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.HTTPControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Label;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.NotificationAction;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSuper;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.ListPropertyWidgetRecordAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetConstrainToValuesAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetRangeConstraintAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetThreeShortAJ;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;














import static com.at4wireless.alljoyn.testcases.conf.controlpanel.InterfacePathPattern.ControlPanel;
import static com.at4wireless.alljoyn.testcases.conf.controlpanel.InterfacePathPattern.HttpControl;













import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.AllJoynErrorReplyCodes;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;

public class ControlPanelService {

	boolean pass=true;
	boolean inconc=false;
	short port =91;
	private  AboutClient aboutClient;
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper; 
	private  BusIntrospector busIntrospector;

	private  String  KeyStorePath="/KeyStore";
	private  UUID dutAppId;
	private  String dutDeviceId;
	private  int  timeOut=30;


	private  final String TAG = "ControlPanelTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	private  final String BUS_APPLICATION_NAME = "ControlPanel";
	private  final List<Integer> TWO_STATES_LIST = Arrays.asList(new Integer[]
			{ 0, 1 });
	private  final List<Integer> PROPERTY_STATES_LIST = Arrays.asList(new Integer[]
			{ 0, 1, 2, 3 });
	private  final List<Short> VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES = Arrays.asList(new Short[]
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 });
	private  final List<String> NUMERIC_SIGNATURES = Arrays.asList(new String[]
			{ "y", "n", "q", "i", "u", "x", "t", "d" });


	// short INTERFACE_VERSION;



	boolean ICSCP_ControlPanelServiceFramework=false;
	boolean ICSCP_ControlPanelInterface=false;
	boolean ICSCP_ContainerInterface=false;
	boolean ICSCP_SecuredContainerInterface=false;
	boolean ICSCP_PropertyInterface=false;
	boolean ICSCP_SecuredPropertyInterface=false;
	boolean ICSCP_LabelPropertyInterface=false;
	boolean ICSCP_ActionInterface=false;
	boolean ICSCP_SecuredActionInterface=false;
	boolean ICSCP_NotificationActionInterface=false;
	boolean ICSCP_DialogInterface=false;
	boolean ICSCP_DI_Action2=false;
	boolean ICSCP_DI_Action3=false;
	boolean ICSCP_SecuredDialogInterface=false;
	boolean ICSCP_SDI_Action2=false;
	boolean ICSCP_SDI_Action3=false;
	boolean ICSCP_ListPropertyInterface=false;
	boolean ICSCP_SecuredListPropertyInterface=false;
	boolean ICSCP_HTTPControlInterface=false;
	//
	String IXITCO_AppId=null;
	String IXITCO_DefaultLanguage=null;
	String IXITCO_DeviceId=null;
	//
	String IXITCP_ControlPanelVersion=null;
	String IXITCP_ContainerVersion=null;
	String IXITCP_PropertyVersion=null;
	String IXITCP_LabelPropertyVersion=null;
	String IXITCP_ActionVersion=null;
	String IXITCP_NotificationActionVersion=null;
	String IXITCP_DialogVersion=null;
	String IXITCP_ListPropertyVersion=null;
	String IXITCP_HTTPControlVersion=null;
	public ControlPanelService(String testCase,
			boolean iCSCP_ControlPanelServiceFramework,
			boolean iCSCP_ControlPanelInterface,
			boolean iCSCP_ContainerInterface,
			boolean iCSCP_SecuredContainerInterface,
			boolean iCSCP_PropertyInterface,
			boolean iCSCP_SecuredPropertyInterface,
			boolean iCSCP_LabelPropertyInterface,
			boolean iCSCP_ActionInterface,
			boolean iCSCP_SecuredActionInterface,
			boolean iCSCP_NotificationActionInterface,
			boolean iCSCP_DialogInterface, boolean iCSCP_DI_Action2,
			boolean iCSCP_DI_Action3, boolean iCSCP_SecuredDialogInterface,
			boolean iCSCP_SDI_Action2, boolean iCSCP_SDI_Action3,
			boolean iCSCP_ListPropertyInterface,
			boolean iCSCP_SecuredListPropertyInterface,
			boolean iCSCP_HTTPControlInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITCP_ControlPanelVersion, String iXITCP_ContainerVersion,
			String iXITCP_PropertyVersion, String iXITCP_LabelPropertyVersion,
			String iXITCP_ActionVersion,
			String iXITCP_NotificationActionVersion,
			String iXITCP_DialogVersion, String iXITCP_ListPropertyVersion,
			String iXITCP_HTTPControlVersion, String gPCO_AnnouncementTimeout) {

		//INTERFACE_VERSION=Short.parseShort(iXITCP_ControlPanelVersion);
		ICSCP_ControlPanelServiceFramework=iCSCP_ControlPanelServiceFramework;
		ICSCP_ControlPanelInterface=iCSCP_ControlPanelInterface;
		ICSCP_ContainerInterface=iCSCP_ContainerInterface;
		ICSCP_SecuredContainerInterface=iCSCP_SecuredContainerInterface;
		ICSCP_PropertyInterface=iCSCP_PropertyInterface;
		ICSCP_SecuredPropertyInterface=iCSCP_SecuredPropertyInterface;
		ICSCP_LabelPropertyInterface=iCSCP_LabelPropertyInterface;
		ICSCP_ActionInterface=iCSCP_ActionInterface;
		ICSCP_SecuredActionInterface=iCSCP_SecuredActionInterface;
		ICSCP_NotificationActionInterface=iCSCP_NotificationActionInterface;
		ICSCP_DialogInterface=iCSCP_DialogInterface;
		ICSCP_DI_Action2=iCSCP_DI_Action2;
		ICSCP_DI_Action3=iCSCP_DI_Action3;
		ICSCP_SecuredDialogInterface=iCSCP_SecuredDialogInterface;
		ICSCP_SDI_Action2=iCSCP_SDI_Action2;
		ICSCP_SDI_Action3=iCSCP_SDI_Action3;
		ICSCP_ListPropertyInterface=iCSCP_ListPropertyInterface;
		ICSCP_SecuredListPropertyInterface=iCSCP_SecuredListPropertyInterface;
		ICSCP_HTTPControlInterface=iCSCP_HTTPControlInterface;
		//
		IXITCO_AppId=iXITCO_AppId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITCO_DeviceId=iXITCO_DeviceId;
		//
		IXITCP_ControlPanelVersion=iXITCP_ControlPanelVersion;
		IXITCP_ContainerVersion=iXITCP_ContainerVersion;
		IXITCP_PropertyVersion=iXITCP_PropertyVersion;
		IXITCP_LabelPropertyVersion=iXITCP_LabelPropertyVersion;
		IXITCP_ActionVersion=iXITCP_ActionVersion;
		IXITCP_NotificationActionVersion=iXITCP_NotificationActionVersion;
		IXITCP_DialogVersion=iXITCP_DialogVersion;
		IXITCP_ListPropertyVersion=iXITCP_ListPropertyVersion;
		IXITCP_HTTPControlVersion=iXITCP_HTTPControlVersion;
		
		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		
		try{
			runTestCase(testCase);
		}catch(Exception e){
			inconc=true;
			if(e!=null){
				if(e.getMessage().equals("Timed out waiting for About announcement")){
					logger.error("Timed out waiting for About announcement");
					pass=false;
				}else{
					logger.error("Exception: "+e.toString());
					pass=false;
				}
			}
		}
	}

	public  void runTestCase(String testCase) throws Exception{


		setUp();
		logger.info("Running testcase: "+testCase);
		if(testCase.equals("ControlPanel-v1-01")){
			testControlPanel_v1_01_ValidateControlPanelBusObjects();
		}else if(testCase.equals("ControlPanel-v1-02")){
			testControlPanel_v1_02_ValidateContainerBusObjects();
		}else if(testCase.equals("ControlPanel-v1-03")){
			testControlPanel_v1_03_ValidatePropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-04")){
			testControlPanel_v1_04_ValidateLabelPropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-05")){
			testControlPanel_v1_05_ValidateActionBusObjects();
		}else if(testCase.equals("ControlPanel-v1-06")){
			testControlPanel_v1_06_ValidateDialogBusObjects();
		}else if(testCase.equals("ControlPanel-v1-07")){
			testControlPanel_v1_07_ValidateListPropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-08")){
			testControlPanel_v1_08_ValidateNotificationActionBusObjects();
		}else if(testCase.equals("ControlPanel-v1-09")){
			testControlPanel_v1_09_ValidateHttpControlBusObjects();
		}else if(testCase.equals("ControlPanel-v1-10")){
			testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects();
		}else{
			fail("TestCase not valid");
		}


		tearDown();



	}












	private  void setUp() throws Exception {

		try
		{
			System.out.println("====================================================");
			logger.debug("test setUp started");
			dutDeviceId = IXITCO_DeviceId;
			logger.debug(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.debug(String.format("Running ControlPanel test case against App ID: %s", dutAppId));
			String keyStorePath =KeyStorePath;
			logger.debug(String.format("Running Config test case using KeyStorePath: %s", keyStorePath));
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeOut, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
			serviceHelper.enableAuthentication(keyStorePath);

			busIntrospector = getIntrospector();
			logger.debug("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception exception)
		{
			try
			{
				releaseResources();
			}
			catch (Exception newException)
			{
				logger.debug("Exception releasing resources", newException);
			}

			throw exception;
		}
	}



	private  void assertNotNull(String msg,
			Object notNull) {

		if(notNull==null){
			logger.error(msg);
			pass=false;
		}

	}




	public  void testControlPanel_v1_01_ValidateControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus = busIntrospector
				.getInterfacesExposedOnBusBasedOnName(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME);

		if (controlPanelInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement ControlPanel interface");

			return;
		}

		validateControlPanelInterfaceDetailList(controlPanelInterfaceDetailListExposedOnBus);
	}





	public  void testControlPanel_v1_02_ValidateContainerBusObjects() throws Exception
	{
		List<InterfaceDetail> containerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);

		if (containerInterfaceDetailListExposedOnBus.isEmpty() && securedContainerInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Container nor SecuredContainer interfaces");
			return;
		}

		validateContainerInterfaceDetailList(containerInterfaceDetailListExposedOnBus, false);
		validateContainerInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus, true);
	}






	public  void testControlPanel_v1_03_ValidatePropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControl.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);

		if (propertyInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Property nor SecuredProperty interfaces");

			return;
		}

		validatePropertyInterfaceDetailList(propertyInterfaceDetailListExposedOnBus, false);
		validatePropertyInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus, true);
	}






	public  void testControlPanel_v1_04_ValidateLabelPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Label.IFNAME);

		if (labelPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement LabelProperty interface");

			return;
		}

		validateLabelPropertyInterfaceDetailList(labelPropertyInterfaceDetailListExposedOnBus);
	}





	public  void testControlPanel_v1_05_ValidateActionBusObjects() throws Exception
	{
		List<InterfaceDetail> actionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControl.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);

		if (actionInterfaceDetailListExposedOnBus.isEmpty() && securedActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Action nor SecuredAction interfaces");

			return;
		}

		validateActionInterfaceDetailList(actionInterfaceDetailListExposedOnBus, false);
		validateActionInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus, true);
	}



	public  void testControlPanel_v1_06_ValidateDialogBusObjects() throws Exception
	{
		List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);

		if (dialogInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Dialog nor SecuredDialog interfaces");

			return;
		}

		validateDialogInterfaceDetailList(dialogInterfaceDetailListExposedOnBus, false);
		validateDialogInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus, true);
	}



	public  void testControlPanel_v1_07_ValidateListPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControl.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (listPropertyInterfaceDetailListExposedOnBus.isEmpty() && securedListPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement ListProperty nor SecuredListProperty interfaces");

			return;
		}

		validateListPropertyInterfaceDetailList(listPropertyInterfaceDetailListExposedOnBus, false);
		validateListPropertyInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus, true);
	}



	public  void testControlPanel_v1_08_ValidateNotificationActionBusObjects() throws Exception
	{
		List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(NotificationAction.IFNAME);

		if (notificationActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement NotificationAction interface");
			return;
		}

		validateNotificationActionInterfaceDetailList(notificationActionInterfaceDetailListExposedOnBus);
	}



	public  void testControlPanel_v1_09_ValidateHttpControlBusObjects() throws Exception
	{
		List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME);

		if (httpControlInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement HTTPControl interface");
			return;
		}

		validateHttpControlInterfaceDetailList(httpControlInterfaceDetailListExposedOnBus);
	}





	public  void testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (isEmpty(securedContainerInterfaceDetailListExposedOnBus, securedPropertyInterfaceDetailListExposedOnBus, securedActionInterfaceDetailListExposedOnBus,
				securedDialogInterfaceDetailListExposedOnBus, securedListPropertyInterfaceDetailListExposedOnBus))
		{
			fail("No bus objects implement one of the secured ControlPanel interfaces");
			return;
		}

		validateSecuredContainerInvalidPasscodeInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus);
		validateSecuredPropertyInvalidPasscodeInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus);
		validateSecuredActionInvalidPasscodeInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus);
		validateSecuredDialogInvalidPasscodeInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus);
		validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus);
	}



	private  void validateSecuredContainerInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus) throws BusException,
	IOException
	{
		for (InterfaceDetail interfaceDetail : securedContainerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredContainerInvalidPasscodeBusObject(path);
		}
	}

	private  void validateSecuredPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : securedPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredPropertyControlInvalidPasscodeBusObject(path);
		}
	}

	private  void validateSecuredActionInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredActionInvalidPasscodeBusObject(path);
		}
	}

	private  void validateSecuredDialogInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : securedDialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredDialogInvalidPasscodeBusObject(path);
		}
	}

	private  void validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredListPropertyInvalidPasscodeBusObject(path);
		}
	}





	private  void validateSecuredContainerInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ContainerSecured securedContainer = busIntrospector.getInterface(path, ContainerSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			securedContainer.getVersion();
			invalidPasswordAccessFailureToThrowException("container");
		}
		catch (BusException busException)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(busException);
		}
	}

	private  void validateSecuredPropertyControlInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		PropertyControlSecured propertyControl = busIntrospector.getInterface(path, PropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			propertyControl.getVersion();
			invalidPasswordAccessFailureToThrowException("property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private  void validateSecuredActionInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ActionControlSecured action = busIntrospector.getInterface(path, ActionControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			action.getVersion();
			invalidPasswordAccessFailureToThrowException("action");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private  void validateSecuredDialogInvalidPasscodeBusObject(String path) throws IOException, BusException
	{
		AlertDialogSecured securedDialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			securedDialog.getVersion();
			invalidPasswordAccessFailureToThrowException("dialog");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private  void validateSecuredListPropertyInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ListPropertyControlSecured listPropertyControlSecured = busIntrospector.getInterface(path, ListPropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			listPropertyControlSecured.getVersion();
			invalidPasswordAccessFailureToThrowException("list property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}


	private  void handleBusExceptionOnInvalidPasswordBusObjectAccess(BusException busException)
	{
		logger.debug("Exception thrown on setting WrongPasscode", busException);
		boolean isPeerAuthenticatedAttempted = serviceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement);
		boolean isPeerAuthenticationSuccessful = serviceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement);
		assertTrue("Authentication should have failed", isPeerAuthenticatedAttempted && !isPeerAuthenticationSuccessful);
	}


	private  void setInvalidPassword()
	{
		serviceHelper.clearPeerAuthenticationFlags(deviceAboutAnnouncement);
		serviceHelper.setAuthPassword(deviceAboutAnnouncement, "123456".toCharArray());
	}


	private  void invalidPasswordAccessFailureToThrowException(String interfaceName)
	{
		fail("Exception should be thrown on connecting with the wrong password to retrieve " + interfaceName + " version!");
	}



	private  boolean isEmpty(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus)
	{
		return securedContainerInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty()
				&& securedActionInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty()
				&& securedListPropertyInterfaceDetailListExposedOnBus.isEmpty();
	}



	private  void validateHttpControlInterfaceDetailList(List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus) throws BusException, IOException
	{
		for (InterfaceDetail interfaceDetail : httpControlInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating HTTPControl object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/HTTPControl", path), isValidPath(HttpControl.getValue(), path));
			validateHttpControlBusObject(path);
			// getValidationTestContext().addInterfaceDetails(HTTPControl.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}

	private  void validateHttpControlBusObject(String path) throws BusException, IOException
	{
		HTTPControl httpControl = busIntrospector.getInterface(path, HTTPControl.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_HTTPControlVersion), httpControl.getVersion());
		validateRootUrl(httpControl.GetRootURL());
	}


	private  void validateRootUrl(String rootUrl) throws IOException
	{
		assertNotNull("Root URL returned is null", rootUrl);
		assertTrue(String.format("%s is not a valid URL", rootUrl), isValidUrl(rootUrl));
		HttpGet httpGet = new HttpGet(rootUrl);
		HttpResponse httpResponse = getHttpClient().execute(httpGet);
		assertNotNull("Root URL is not responding",httpResponse.getStatusLine());
	}


	 HttpClient getHttpClient()
	{
		HttpClient httpClient = HttpClientBuilder.create().build();
		return httpClient;
	}

	boolean isValidUrl(String supportUrl)
	{
		try
		{
			new URL(supportUrl).toURI();
		}
		catch (MalformedURLException malformedURLException)
		{
			logger.debug("Invalid URL", malformedURLException);
			return false;
		}
		catch (URISyntaxException e)
		{
			logger.debug("Invalid URL", e);
			return false;
		}

		return true;
	}


	private  void validateNotificationActionInterfaceDetailList(List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : notificationActionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating NotificationAction object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{actionPanelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateNotificationActionBusObject(path);
			//   getValidationTestContext().addInterfaceDetails(NotificationAction.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}


	private  void validateNotificationActionBusObject(String path) throws Exception
	{
		NotificationAction notificationAction = busIntrospector.getInterface(path, NotificationAction.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_NotificationActionVersion), notificationAction.getVersion());
		assertContainerOrDialogObjectExists(path);
	}


	private  void assertContainerOrDialogObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
		List<InterfaceDetail> dialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialogSecured.IFNAME);

		assertTrue(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer nor "
				+ "org.alljoyn.ControlPanel.Dialog nor org.alljoyn.ControlPanel.SecuredDialog is found under path %s", path),
				isRootContainerPresent(containerInterfaceDetailList, securedContainerInterfaceDetailList, dialogInterfaceDetailList, securedDialogInterfaceDetailList));
	}



	private  boolean isRootContainerPresent(List<InterfaceDetail> containerInterfaceDetailList, List<InterfaceDetail> securedContainerInterfaceDetailList,
			List<InterfaceDetail> dialogInterfaceDetailList, List<InterfaceDetail> securedDialogInterfaceDetailList)
	{
		return !(containerInterfaceDetailList.isEmpty() && securedContainerInterfaceDetailList.isEmpty() && dialogInterfaceDetailList.isEmpty() && securedDialogInterfaceDetailList
				.isEmpty());
	}







	private  void validateListPropertyInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.ListProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateListPropertyBusObject(path, isSecured);
			// addListPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	private  void validateListPropertyBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ListPropertyControlSuper listPropertyControl = null;

		if (isSecured)
		{
			listPropertyControl = busIntrospector.getInterface(path, ListPropertyControlSecured.class);
		}
		else
		{
			listPropertyControl = busIntrospector.getInterface(path, ListPropertyControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ListPropertyVersion), listPropertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", listPropertyControl.getStates()),
				isValidState(listPropertyControl.getStates(), TWO_STATES_LIST));
		validateListPropertyParameters(listPropertyControl.getOptParams());
		validateListPropertyValue(listPropertyControl);
	}


	private  void validateListPropertyValue(ListPropertyControlSuper listPropertyControl) throws BusException
	{
		ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = listPropertyControl.getValue();
		assertNotNull("Interface value cannot be null", listPropertyWidgetRecordAJs);

		Set<Short> recordIds = new HashSet<Short>();

		for (ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ : listPropertyWidgetRecordAJs)
		{
			assertNotNull("Label cannot be null", listPropertyWidgetRecordAJ.label);
			assertFalse("Label cannot be empty", listPropertyWidgetRecordAJ.label.isEmpty());
			recordIds.add(listPropertyWidgetRecordAJ.recordId);
		}

		assertTrue("Record IDs need to be unique", listPropertyWidgetRecordAJs.length == recordIds.size());
	}


	private  void validateListPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}


	private  void validateDialogInterfaceDetailList(List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		boolean action3=false;
		boolean action2=false;
		for (InterfaceDetail interfaceDetail : dialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Dialog object at: %s", path));
			assertValidAncestorIsPresentForDialog(path);
			validateDialogBusObject(path, isSecured);


			if(action2IsPresent(path,isSecured)){
				action2=true;
			}

			if(action3IsPresent(path,isSecured)){
				action3=true;
			}



			// addDialogInterfaceDetailsToTestContext(isSecured, path);
		}

		if((ICSCP_DI_Action2||ICSCP_SDI_Action2) && !action2){
			fail("Method Action2 is not present"); 

		}

		if((ICSCP_DI_Action3||ICSCP_SDI_Action3) && !action3){
			fail("Method Action3 is not present");

		}


	}


	private  boolean action2IsPresent(String path,Boolean isSecured) throws BusException {
		AlertDialogSuper dialog = null;

		boolean ispresent=false;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		short numberOfActions = dialog.getNumActions();
		if (numberOfActions == 2){
			ispresent=true;
		}

		return ispresent;
	}



	private  boolean action3IsPresent(String path,Boolean isSecured) throws BusException {
		AlertDialogSuper dialog = null;

		boolean ispresent=false;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		short numberOfActions = dialog.getNumActions();

		if (numberOfActions == 3){
			ispresent=true;
		}

		return ispresent;
	}



	void assertValidAncestorIsPresentForDialog(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found",
				busIntrospector.isAncestorInterfacePresent(path, ActionControl.IFNAME) || busIntrospector.isAncestorInterfacePresent(path, ActionControlSecured.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, NotificationAction.IFNAME));
	}







	private  void validateDialogBusObject(String path, boolean isSecured) throws IOException, BusException
	{
		AlertDialogSuper dialog = null;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_DialogVersion), dialog.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", dialog.getStates()), isValidState(dialog.getStates(), TWO_STATES_LIST));
		short numberOfActions = dialog.getNumActions();
		validateDialogParameters(dialog.getOptParams(), numberOfActions);
		logger.debug(String.format("Dialog message: %s", dialog.getMessage()));
		validateDialogActions(dialog, numberOfActions);
	}


	private  void validateDialogActions(AlertDialogSuper dialog, short numberOfActions) throws BusException
	{
		if (numberOfActions == 1)
		{
			validateInvokingDialogAction2ThrowsException(dialog);
			validateInvokingDialogAction3ThrowsException(dialog);
		}

		if (numberOfActions == 2)
		{
			validateInvokingDialogAction3ThrowsException(dialog);
		}
	}

	private  void validateInvokingDialogAction2ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action2();
			fail("Invoking Action2() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}

	private  void validateInvokingDialogAction3ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action3();
			fail("Invoking Action3() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}


	private  void assertErrorIsMethodNotAllowed(ErrorReplyBusException errorReplyBusException)
	{
		logger.debug("Expected exception caught while involing Action()", errorReplyBusException);
		assertEquals("Error name does not match", AllJoynErrorReplyCodes.METHOD_NOT_ALLOWED, errorReplyBusException.getErrorName());
	}

	private  void fail(String msg) {


		logger.error(msg);
		pass=false;

	}






	private  void validateDialogParameters(Map<Short, Variant> parameters, short numberOfActions) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
		validateDialogParameterActionLabelIds(parameters, numberOfActions);
	}


	private  void validateDialogParameterActionLabelIds(Map<Short, Variant> parameters, short numberOfActions) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 6))
		{
			validateDialogParameterActionLabelId(parameters.get((short) 6), "6");
		}

		if (numberOfActions >= 2)
		{
			validateDialogParameterActionLabelId(parameters.get((short) 7), "7");
		}

		if (numberOfActions == 3)
		{
			validateDialogParameterActionLabelId(parameters.get((short) 8), "8");
		}
	}

	private  void validateDialogParameterActionLabelId(Variant actionLabelIdVariant, String key) throws AnnotationBusException
	{
		assertNotNull(String.format("Key %s is missing", key), actionLabelIdVariant);
		assertEquals(String.format("Signature does not match for key %s", key), "s", actionLabelIdVariant.getSignature());
	}


	private  void validateActionInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Action object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateActionBusObject(path, isSecured);
			// addActionInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	private  void validateActionBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ActionControlSuper action = null;

		if (isSecured)
		{
			action = busIntrospector.getInterface(path, ActionControlSecured.class);
		}
		else
		{
			action = busIntrospector.getInterface(path, ActionControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ActionVersion), action.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", action.getStates()), isValidState(action.getStates(), TWO_STATES_LIST));
		validateActionParameters(action.getOptParams());
	}




	private  void validateActionParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}




	private  void validateLabelPropertyInterfaceDetailList(List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : labelPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.LabelProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateLabelPropertyBusObject(path);
			//getValidationTestContext().addInterfaceDetails(Label.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}



	private  void validateLabelPropertyBusObject(String path) throws BusException, IOException
	{
		Label label = busIntrospector.getInterface(path, Label.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_LabelPropertyVersion), label.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", label.getStates()), isValidState(label.getStates(), TWO_STATES_LIST));
		assertTrue("Label property must be a String", label.getLabel() instanceof String);
		validateLabelPropertyParameters(label.getOptParams());
	}


	private  void validateLabelPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}



	private  void validateParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			assertEquals("Key 2 contains more than one value", 1, layoutHints.length);
			assertEquals("Value does not match for key 2", 1, layoutHints[0]);
		}
	}






	private  void validatePropertyInterfaceDetailList(List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : propertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Property object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validatePropertyBusObject(path, isSecured);
			//addPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	void assertAncestorContainerIsPresent(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Container nor SecuredContainer interface found", busIntrospector.isAncestorInterfacePresent(path, Container.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ContainerSecured.IFNAME));
	}


	private  void validatePropertyBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		PropertyControlSuper propertyControl = null;

		if (isSecured)
		{
			propertyControl = busIntrospector.getInterface(path, PropertyControlSecured.class);
		}
		else
		{
			propertyControl = busIntrospector.getInterface(path, PropertyControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_PropertyVersion), propertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0, 1, 2 or 3", propertyControl.getStates()),
				isValidState(propertyControl.getStates(), PROPERTY_STATES_LIST));
		validatePropertyControlParameters(propertyControl.getOptParams(), propertyControl.getValue());
	}



	private  void validatePropertyControlParameters(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validatePropertyControlParameterLayoutHints(parameters, propertyValue);
		validatePropertyControlOptionalParameter3(parameters);
		validatePropertyControlOptionalParameter4(parameters, propertyValue);
		validatePropertyControlOptionalParameter5(parameters, propertyValue);
	}




	private  void validatePropertyControlOptionalParameter3(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 3))
		{
			assertEquals("Signature does not match for key 3", "s", parameters.get((short) 3).getSignature());
		}
	}

	private  void validatePropertyControlOptionalParameter4(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
	{
		if (parameters.containsKey((short) 4))
		{
			assertEquals("Signature does not match for key 4", "a(vs)", parameters.get((short) 4).getSignature());
			PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = parameters.get((short) 4).getObject(PropertyWidgetConstrainToValuesAJ[].class);

			for (PropertyWidgetConstrainToValuesAJ propertyWidgetConstrainToValuesAJ : propertyWidgetConstrainToValuesAJArray)
			{
				assertEquals("Signature does not match for value Variant instance in value for key 4", propertyValue.getSignature(),
						propertyWidgetConstrainToValuesAJ.value.getSignature());
			}
		}
	}

	private  void validatePropertyControlOptionalParameter5(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
	{
		if (parameters.containsKey((short) 5))
		{
			assertEquals("Signature does not match for key 5", "(vvv)", parameters.get((short) 5).getSignature());
			PropertyWidgetRangeConstraintAJ propertyWidgetRangeConstraintAJ = parameters.get((short) 5).getObject(PropertyWidgetRangeConstraintAJ.class);
			assertEquals("Signature does not match for min Variant instance in value for key 5", propertyValue.getSignature(), propertyWidgetRangeConstraintAJ.min.getSignature());
			assertEquals("Signature does not match for max Variant instance in value for key 5", propertyValue.getSignature(), propertyWidgetRangeConstraintAJ.max.getSignature());
			assertEquals("Signature does not match for increment Variant instance in value for key 5", propertyValue.getSignature(),
					propertyWidgetRangeConstraintAJ.increment.getSignature());
		}
	}



	private  void validatePropertyControlParameterLayoutHints(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.debug(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertEquals("Key 2 contains more than one value", 1, layoutHints.length);
			short layoutHintId = layoutHints[0];
			assertTrue(String.format("%d is not a valid value for key 2", layoutHintId), VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES.contains(layoutHintId));

			validateBasedOnLayoutHintId(parameters, propertyValue, layoutHintId);
		}
	}


	private  void assertEquals(String msg, int i, int k) {

		if(k!=i){
			logger.error(msg);
			pass=false;
		}

	}






	private  void validateBasedOnLayoutHintId(Map<Short, Variant> parameters, Variant propertyValue, short layoutHintId) throws AnnotationBusException, BusException
	{
		if (layoutHintId == (short) 1)
		{
			assertEquals("Signature does not match for property Value when hint id is 1", "b", propertyValue.getSignature());
		}
		else if (layoutHintId == (short) 2 || layoutHintId == (short) 3 || layoutHintId == (short) 4)
		{
			assertOptionalParameter4IsPresent(parameters, layoutHintId);
		}
		else if (layoutHintId == (short) 6)
		{
			validateDateTimeHint(layoutHintId, propertyValue, 1);
		}
		else if (layoutHintId == (short) 7)
		{
			validateDateTimeHint(layoutHintId, propertyValue, 0);
		}
		else if (layoutHintId == (short) 5 || layoutHintId == (short) 8 || layoutHintId == (short) 9 || layoutHintId == (short) 10 || layoutHintId == (short) 12)
		{
			assertTrue(String.format("Property Value type needs to be numeric when hint id is %d. Found signature: %s", layoutHintId, propertyValue.getSignature()),
					NUMERIC_SIGNATURES.contains(propertyValue.getSignature()));
		}
		else if (layoutHintId == (short) 11 || layoutHintId == (short) 13)
		{
			assertEquals(String.format("Signature does not match for property Value when hint id is %d", layoutHintId), "s", propertyValue.getSignature());
		}
	}


	private  void validateDateTimeHint(short layoutHintId, Variant propertyValue, int compositeType) throws AnnotationBusException, BusException
	{
		assertEquals(String.format("Signature does not match for property Value when hint id is %d", layoutHintId), "q(qqq)", propertyValue.getSignature());
		PropertyWidgetThreeShortAJ propertyWidgetThreeShortAJ = propertyValue.getObject(PropertyWidgetThreeShortAJ.class);
		assertEquals(String.format("The first value in the composite type does not match when hint id is %d", layoutHintId), compositeType, propertyWidgetThreeShortAJ.dataType);
	}

	private  void assertOptionalParameter4IsPresent(Map<Short, Variant> parameters, short layoutHintId) throws AnnotationBusException, BusException
	{
		assertTrue(String.format("Parameters should contain key 4 when hint id is %d", layoutHintId), parameters.containsKey((short) 4));
		assertEquals("Signature does not match for key 4", "a(vs)", parameters.get((short) 4).getSignature());
		PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = parameters.get((short) 4).getObject(PropertyWidgetConstrainToValuesAJ[].class);
		assertNotNull("Values array for key 4 cannot be null", propertyWidgetConstrainToValuesAJArray);
		assertTrue("Values array for key 4 cannot be empty", propertyWidgetConstrainToValuesAJArray.length >= 1);
	}





	private  void validateContainerInterfaceDetailList(List<InterfaceDetail> containerInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : containerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Container object at: %s", path));
			assertValidAncestorIsPresentForContainer(path);
			validateContainerBusObject(path, isSecured);
			// addContainerInterfaceDetailsToTestContext(isSecured, path);
		}
	}






	void assertValidAncestorIsPresentForContainer(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{

		assertTrue(
				"No parent bus object that implements ControlPanel nor NotificationAction nor Container nor SecuredContainer nor ListProperty nor SecuredListProperty interface found",
				busIntrospector.isAncestorInterfacePresent(path, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, NotificationAction.IFNAME) || busIntrospector.isAncestorInterfacePresent(path, Container.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ContainerSecured.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ListPropertyControl.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ListPropertyControlSecured.IFNAME));
	}






	private  void validateContainerBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ContainerSuper container = null;

		if (isSecured)
		{
			container = busIntrospector.getInterface(path, ContainerSecured.class);
		}
		else
		{
			container = busIntrospector.getInterface(path, Container.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ContainerVersion), container.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", container.getStates()), isValidState(container.getStates(), TWO_STATES_LIST));
		validateContainerParameters(container.getOptParams());
	}

	private  boolean isValidState(int state, List<Integer> validStates) throws BusException
	{
		return validStates.contains(state);
	}


	private  void validateContainerParameters(Map<Short, Variant> parameters) throws AnnotationBusException, BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateContainerParameterLayoutHints(parameters);
	}


	private  void validateOptionalParameter0(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 0))
		{
			assertEquals("Signature does not match for key 0", "s", parameters.get((short) 0).getSignature());
		}
	}


	private  void validateContainerParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.debug(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertTrue("Key 2 contains no value", layoutHints.length > 0);

			for (short layoutHint : layoutHints)
			{
				assertTrue(String.format("%d does not match expected value of 1 or 2 for key 2", layoutHint), layoutHint == 1 || layoutHint == 2);
			}
		}
	}



	private  void validateLayoutHintsSignature(Variant layoutHintsVariant) throws AnnotationBusException
	{
		assertNotNull("Key 2 for LayoutHints is missing", layoutHintsVariant);
		assertEquals("Signature does not match for key 2", "aq", layoutHintsVariant.getSignature());
	}








	private  void assertEquals(String msg, String sig, String signature) {


		if(!signature.equals(sig)){
			pass=false;
			logger.error(msg);
		}


	}






	private  void validateOptionalParameter1(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 1))
		{
			assertEquals("Signature does not match for key 1", "u", parameters.get((short) 1).getSignature());
		}
	}


	private  void validateControlPanelInterfaceDetailList(List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : controlPanelInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{panelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateControlPanelBusObject(path);
			// getValidationTestContext().addInterfaceDetails(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}



	private  void assertTrue(String msg, boolean bool) {

		if(!bool){
			logger.error(msg);
			pass=false;
		}

	}






	boolean isValidPath(String pathPattern, String path)
	{
		Pattern pattern = Pattern.compile(pathPattern);
		Matcher matcher = pattern.matcher(path);

		return matcher.matches();
	}

	private  void validateControlPanelBusObject(String path) throws Exception
	{
		ControlPanel controlPanel = busIntrospector.getInterface(path, ControlPanel.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ControlPanelVersion), controlPanel.getVersion());
		assertContainerObjectExists(path);
	}


	private  void assertEquals(String msg, short interfaceVersion,
			short version) {

		if(interfaceVersion!=version){
			logger.error(msg);
			pass=false;
		}

	}






	private  void assertContainerObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);

		if (containerInterfaceDetailList.isEmpty())
		{
			containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
			assertFalse(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer is under path %s", path),
					containerInterfaceDetailList.isEmpty());
		}
	}



	private  void assertFalse(String msg, boolean bool) {


		if(bool){
			logger.error(msg);
			pass=false;
		}

	}

	public  void tearDown() throws Exception
	{	
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}


	private  void releaseResources()
	{
		disconnectFromAboutClient();

		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}

	private  void disconnectFromAboutClient()
	{
		if (aboutClient != null)
		{
			aboutClient.disconnect();
			aboutClient = null;
		}
	}



	protected  ServiceHelper getServiceHelper()
	{
		return new ServiceHelper( logger);
	}



	BusIntrospector getIntrospector() throws Exception
	{
		return serviceHelper.getBusIntrospector(aboutClient);
	}



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

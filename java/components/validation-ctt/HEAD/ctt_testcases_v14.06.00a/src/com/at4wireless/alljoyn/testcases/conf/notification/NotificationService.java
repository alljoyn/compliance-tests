package com.at4wireless.alljoyn.testcases.conf.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.NotificationText;



import org.alljoyn.services.common.PropertyStore;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.PropertyStoreImpl;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.UserInputDetails;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.notification.NotificationValidator;
import com.at4wireless.alljoyn.core.notification.NotificationValidator.NotificationValidationExceptionHandler;

public class NotificationService {

	Boolean pass=true;
	boolean inconc=false;
	private  final String TAG = "NotificationTestSuite";
	private  final String BUS_APPLICATION_NAME = "NotificationConsumerTestSuite";
	private  org.alljoyn.ns.Notification notif;
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private  int  timeOut=30;
	private short PORT=1080;
	private  ExecutorService executorService;
	private  AboutClient aboutClient;
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper;
	private  NotificationSender nSender;
	private  PropertyStore propertyStore;
	private volatile  Exception notificationAssertionFailure = null;
	private volatile Thread waitingforUserInputThread;

	private NotificationValidator notificationValidator=null;

	Map<String,Boolean> ics;
	Map<String,String> ixit;

	public NotificationService(String testCase,
			boolean iCSN_NotificationServiceFramework,
			boolean iCSN_NotificationInterface, boolean iCSN_RichIconUrl,
			boolean iCSN_RichAudioUrl, boolean iCSN_RespObjectPath,
			boolean iCSN_NotificationProducerInterface,
			boolean iCSN_DismisserInterface, boolean iCSN_NotificationConsumer,
			String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITN_NotificationVersion, String iXITN_TTL,
			String iXITN_NotificationProducerVersion,
			String iXITN_NotificationDismisserVersion,
			String gPCO_AnnouncementTimeout) {

		ics = new HashMap<String,Boolean>();
		ics.put("ICSN_NotificationServiceFramework", iCSN_NotificationServiceFramework);
		ics.put("ICSN_NotificationInterface", iCSN_NotificationInterface);
		ics.put("ICSN_RichIconUrl", iCSN_RichIconUrl);
		ics.put("ICSN_RichAudioUrl", iCSN_RichAudioUrl);
		ics.put("ICSN_RespObjectPath", iCSN_RespObjectPath);
		ics.put("ICSN_NotificationProducerInterface", iCSN_NotificationProducerInterface);
		ics.put("ICSN_DismisserInterface", iCSN_DismisserInterface);
		ics.put("ICSN_NotificationConsumer", iCSN_NotificationConsumer);
		
		ixit = new HashMap<String,String>();
		ixit.put("IXITCO_AppId", iXITCO_AppId);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITCO_DefaultLanguage", iXITCO_DefaultLanguage);
		ixit.put("IXITN_NotificationVersion", iXITN_NotificationVersion);
		ixit.put("IXITN_TTL", iXITN_TTL);
		ixit.put("IXITN_NotificationProducerVersion", iXITN_NotificationProducerVersion);
		ixit.put("IXITN_NotificationDismisserVersion", iXITN_NotificationDismisserVersion);
		
		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);

		try{
			runTestCase(testCase);
		}catch(Exception e){
			if(e!=null){
				if(e.getMessage().equals("Timed out waiting for About announcement")){
					inconc=true;
					fail("Timed out waiting for About announcement");
				}else{
					inconc=true;
					fail("Exception: "+e.toString());
				}
			}
		}
	}

	public  void runTestCase(String testCase) throws Exception{

		if(testCase.equals("Notification-Consumer-v1-01")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_01();

		}else if(testCase.equals("Notification-Consumer-v1-02")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_02();

		/*}else if(testCase.equals("Notification-Consumer-v1-03")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_03();*/

		}else if(testCase.equals("Notification-Consumer-v1-04")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_04();

		}else if(testCase.equals("Notification-Consumer-v1-05")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_05();

		}else if(testCase.equals("Notification-Consumer-v1-06")){
			setUp();
			logger.info("Running testcase: "+testCase);
			testNotification_Consumer_v1_06();

		}else if(testCase.equals("Notification-v1-01")){	
			setUpReceiver();
			logger.info("Running testcase: "+testCase);
			testNotificationsReceived();

		}else {
			fail("TestCase not valid");
		}

		tearDown();
	}
	
	private  void setUp() throws Exception {
		try
		{ 
			System.out.println("====================================================");
			logger.info("test setUp started");
			logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId")));
			logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId"))));

			 propertyStore = getPropertyStoreImpl();

	            serviceHelper = getServiceHelper();
	            serviceHelper.initialize(BUS_APPLICATION_NAME, null, null);

	           
				serviceHelper.startAboutServer(PORT, propertyStore);

	            nSender = serviceHelper.initNotificationSender(propertyStore);
			logger.info("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception e)
		{
			logger.error("Received exception during setup "+ e.toString());
			inconc = true;
			releaseResources();
			throw e;
		}
	}

	private void setUpReceiver() throws Exception {
		System.out.println("====================================================");
		logger.info("test setUp started");

		logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId")));
		logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId"))));
		waitingforUserInputThread = Thread.currentThread();

		executorService = getExecutorService();

		try
		{
			serviceHelper = getServiceHelper();

			serviceHelper.initialize(BUS_APPLICATION_NAME, ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId")));

			notificationValidator = getNotificationValidator();
			notificationValidator.setTestParameters(ics.get("ICSN_RichIconUrl"), ics.get("ICSN_RichAudioUrl"),
					ics.get("ICSN_RespObjectPath"), ixit.get("IXITN_NotificationVersion"));
			serviceHelper.initNotificationReceiver(notificationValidator);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeOut, TimeUnit.SECONDS);
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}
			
			logger.info("Partial Verdict: PASS");

			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			logger.info("test setUp done");
		}
		catch (Exception e)
		{
			logger.error("Exception in setup method", e);
			releaseResources();
			throw e;
		}
		System.out.println("====================================================");
	}
	
	private  void tearDown() {
		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}
	
	/* TestCases */

	private  void testNotification_Consumer_v1_01() throws NotificationServiceException {

		String[] msgArray =
			{ "Test Msg 1", "Test Msg 2", "Test Msg 3" };

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);
		nSender.send(notif, 120);
		logger.info("Message sent");
		
		checkUserInput(msgArray, msgToSend);
	}

	public  void testNotification_Consumer_v1_02() throws Exception
	{
		String[] msgArray =
			{ "Two languages Msg 1", "Two languages Msg 2", "Two languages Msg 3" };

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		text.add(new NotificationText("fr", msgToSend));
		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

		nSender.send(notif, 120);
		logger.info("Message sent");
		checkUserInput(msgArray, msgToSend);
	}

	/*public  void testNotification_Consumer_v1_03() throws Exception
	{
		String[] msgArray =
			{ "No langTag Msg 1", "No langTag Msg 2", "No langTag Msg 3" };

		final String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText()
		{
			@Override
			public String getLanguage()
			{
				return "";
			}

			@Override
			public String getText()
			{
				return msgToSend;
			}
		});

		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

		nSender.send(notif, 120);
		checkUserInput(msgArray, msgToSend);
	}*/

	public  void testNotification_Consumer_v1_04() throws Exception
	{
		String[] msgArray =
			{ "Invalid langTag Msg 1", "Invalid langTag Msg 2", "Invalid langTag Msg 3" };

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("INVALID", msgToSend));

		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

		nSender.send(notif, 120);
		logger.info("Message sent");
		checkUserInput(msgArray, msgToSend);
	}

	public  void testNotification_Consumer_v1_05() throws Exception
	{

		List<List<MsgSet>> msgSets = new ArrayList<List<MsgSet>>();

		List<MsgSet> msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.EMERGENCY));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.WARNING));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.INFO));
		msgSets.add(msgSet);

		msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.EMERGENCY));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.INFO));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.WARNING));
		msgSets.add(msgSet);

		msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.WARNING));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.EMERGENCY));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.INFO));
		msgSets.add(msgSet);

		msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.WARNING));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.INFO));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.EMERGENCY));
		msgSets.add(msgSet);

		msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.INFO));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.EMERGENCY));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.WARNING));
		msgSets.add(msgSet);

		msgSet = new ArrayList<MsgSet>();
		msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.INFO));
		msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.WARNING));
		msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.EMERGENCY));
		msgSets.add(msgSet);

		List<MsgSet> messagesToSend = msgSets.get(getRandomNumber(msgSets.size()));

		for (MsgSet messageToSend : messagesToSend)
		{
			List<NotificationText> text = new LinkedList<NotificationText>();

			text.add(new NotificationText("en", messageToSend.text));

			notif = new org.alljoyn.ns.Notification(messageToSend.priority, text);

			nSender.send(notif, 120);
			logger.info("Message sent");
		}

		List<String> selectionOptions = new ArrayList<String>();
		for (List<MsgSet> messageSet : msgSets)
		{
			selectionOptions.add(buildPromptText(messageSet));
		}

		String msgSent = buildPromptText(messagesToSend);

		checkUserInput(selectionOptions.toArray(new String[selectionOptions.size()]), msgSent);
	}

	public  void testNotification_Consumer_v1_06() throws Exception
	{
		//serviceHelper.initNotificationReceiver(notificationValidator);
		String[] msgArray =
			{ "Msg w/ attributes 1", "Msg w/ attributes 2", "Msg w/ attributes 3" };

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		Map<String, String> customAttributes = new HashMap<String, String>();
		customAttributes.put("org.alljoyn.validation.test", "value");

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));

		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);
		notif.setCustomAttributes(customAttributes);
		nSender.send(notif, 120);

		checkUserInput(msgArray, msgToSend);
	}

	public void testNotificationsReceived() throws Exception
	{
		BusIntrospector busIntrospector = serviceHelper.getBusIntrospector(aboutClient);
		notificationValidator.initializeForDevice(deviceAboutAnnouncement, busIntrospector, new NotificationValidationExceptionHandler()
		{

			@Override
			public void onNotificationValidationException(Exception exception)
			{
				logger.info("Notificiation failed validation checks", exception);
				fail("Notificiation failed validation checks");
				notificationAssertionFailure = exception;
				waitingforUserInputThread.interrupt();
			}
		});

		executorService.submit(notificationValidator);

		logger.info("Waiting for user input");
		String[] msg={"Continue"};
		UserInputDetails userInputDetails = new UserInputDetails("Notification Producer Test",
				"Please initiate the sending of notifications from the DUT and click Continue when all notifications that you want to test have been sent",msg);
		logger.info(String.format("Received: %d notifications", notificationValidator.getNumberOfNotificationsReceived()));
		assertTrue("No notifications received!", notificationValidator.getNumberOfNotificationsReceived() > 0);
	}
	
	/* 
	 *  Utilities 
	 */

	protected NotificationValidator getNotificationValidator()
	{
		return new NotificationValidator(ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId")));
	}


	protected  ExecutorService getExecutorService()
	{
		return java.util.concurrent.Executors.newSingleThreadExecutor();
	}
	protected  String buildPromptText(List<MsgSet> messagesToSend)
	{
		StringBuilder msgSent = new StringBuilder();
		for (MsgSet messageToSend : messagesToSend)
		{
			if (msgSent.length() > 0)
			{
				msgSent.append("; ");
			}
			msgSent.append(messageToSend.text);
			msgSent.append(" (");
			msgSent.append(messageToSend.priority);
			msgSent.append(")");
		}
		return msgSent.toString();
	}

	private  class MsgSet
	{
		public MsgSet(String text, NotificationMessageType priority)
		{
			this.text = text;
			this.priority = priority;
		}

		public String text;
		public NotificationMessageType priority;
	}


	private  void checkUserInput(String[] msgArray, String msgToSend) {

		logger.info("Waiting for user response...");
		UserInputDetails userResponse = new UserInputDetails(TAG,"Select the message(s) received", msgArray);

		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		logger.info("Option sent: "+msgToSend);
		logger.info("Option selected: "+messageSelected);
		assertEquals("Incorrect message option selected", msgToSend, messageSelected);
	}

	protected  int getRandomNumber(int n)
	{
		Random r = new Random();
		return r.nextInt(n);
	}

	private  void releaseResources()
	{
		if (serviceHelper != null)
		{
			serviceHelper.release();
		}
	}

	protected  PropertyStore getPropertyStoreImpl()

	{
		PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl();
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, UUID.fromString(ixit.get("IXITCO_AppId")), "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, ixit.get("IXITCO_DeviceId"), "");

		return propertyStoreImpl;
	}


	ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}

	private  void fail(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;
	}

	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		if(!string1.equals(string2)){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private  void assertTrue(String errorMsg,
			boolean bool) {
		if(!bool){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	public String getVerdict() {

		String verdict=null;
		if(inconc){
			verdict="INCONC";
		}
		else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}


}

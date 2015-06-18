/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationService.
 */
public class NotificationService {

	/** The pass. */
	Boolean pass=true;
	
	/** The inconc. */
	boolean inconc=false;
	
	/** The tag. */
	private  final String TAG = "NotificationTestSuite";
	
	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "NotificationConsumerTestSuite";
	
	/** The notif. */
	private  org.alljoyn.ns.Notification notif;
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The time out. */
	private  int  timeOut=30;
	
	/** The port. */
	private short PORT=1080;
	
	/** The executor service. */
	private  ExecutorService executorService;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper;
	
	/** The n sender. */
	private  NotificationSender nSender;
	
	/** The property store. */
	private  PropertyStore propertyStore;
	
	/** The notification assertion failure. */
	private volatile  Exception notificationAssertionFailure = null;
	
	/** The waitingfor user input thread. */
	private volatile Thread waitingforUserInputThread;

	/** The notification validator. */
	private NotificationValidator notificationValidator=null;

	/** The ics. */
	Map<String,Boolean> ics;
	
	/** The ixit. */
	Map<String,String> ixit;

	/**
	 * Instantiates a new notification service.
	 *
	 * @param testCase the test case
	 * @param iCSN_NotificationServiceFramework the i cs n_ notification service framework
	 * @param iCSN_NotificationInterface the i cs n_ notification interface
	 * @param iCSN_RichIconUrl the i cs n_ rich icon url
	 * @param iCSN_RichAudioUrl the i cs n_ rich audio url
	 * @param iCSN_RespObjectPath the i cs n_ resp object path
	 * @param iCSN_NotificationProducerInterface the i cs n_ notification producer interface
	 * @param iCSN_DismisserInterface the i cs n_ dismisser interface
	 * @param iCSN_NotificationConsumer the i cs n_ notification consumer
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITN_NotificationVersion the i xit n_ notification version
	 * @param iXITN_TTL the i xit n_ ttl
	 * @param iXITN_NotificationProducerVersion the i xit n_ notification producer version
	 * @param iXITN_NotificationDismisserVersion the i xit n_ notification dismisser version
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 */
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

	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
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
			logger.error("Received exception during setup "+ e.getMessage());
			inconc = true;
			releaseResources();
			throw e;
		}
	}

	/**
	 * Sets the up receiver.
	 *
	 * @throws Exception the exception
	 */
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
			logger.error("Received exception during setup "+ e.getMessage());
			releaseResources();
			throw e;
		}
		System.out.println("====================================================");
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
	
	/* TestCases */

	/**
	 * Test notification_ consumer_v1_01.
	 *
	 * @throws NotificationServiceException the notification service exception
	 */
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

	/**
	 * Test notification_ consumer_v1_02.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test notification_ consumer_v1_04.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test notification_ consumer_v1_05.
	 *
	 * @throws Exception the exception
	 */
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

		checkUserInputLongText(selectionOptions.toArray(new String[selectionOptions.size()]), msgSent);
	}

	/**
	 * Test notification_ consumer_v1_06.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test notifications received.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Gets the notification validator.
	 *
	 * @return the notification validator
	 */
	protected NotificationValidator getNotificationValidator()
	{
		return new NotificationValidator(ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId")));
	}


	/**
	 * Gets the executor service.
	 *
	 * @return the executor service
	 */
	protected  ExecutorService getExecutorService()
	{
		return java.util.concurrent.Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Builds the prompt text.
	 *
	 * @param messagesToSend the messages to send
	 * @return the string
	 */
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

	/**
	 * The Class MsgSet.
	 */
	private  class MsgSet
	{
		
		/**
		 * Instantiates a new msg set.
		 *
		 * @param text the text
		 * @param priority the priority
		 */
		public MsgSet(String text, NotificationMessageType priority)
		{
			this.text = text;
			this.priority = priority;
		}

		/** The text. */
		public String text;
		
		/** The priority. */
		public NotificationMessageType priority;
	}


	/**
	 * Check user input.
	 *
	 * @param msgArray the msg array
	 * @param msgToSend the msg to send
	 */
	private  void checkUserInput(String[] msgArray, String msgToSend) {

		logger.info("Waiting for user response...");
		UserInputDetails userResponse = new UserInputDetails(TAG,"Select the message(s) received", msgArray);

		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		logger.info("Option sent: "+msgToSend);
		logger.info("Option selected: "+messageSelected);
		assertEquals("Incorrect message option selected", msgToSend, messageSelected);
	}
	
	/**
	 * Check user input long text.
	 *
	 * @param msgArray the msg array
	 * @param msgToSend the msg to send
	 */
	private void checkUserInputLongText(String[] msgArray, String msgToSend) {
		logger.info("Waiting for user response...");
		String[] buttons={"Message 1","Message 2","Message 3"};
		UserInputDetails userResponse = new UserInputDetails(TAG,"Select the message(s) received: \n"+
				"\n Message 1: "+msgArray[0]+"\n Message 2: "+msgArray[1]+"\n Message 3: "+msgArray[2], buttons);


		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		logger.info("Option sent: "+msgToSend);
		logger.info("Option selected: "+messageSelected);
		assertEquals("Incorrect message option selected", msgToSend, messageSelected);
	
		
	}
	
	/**
	 * Gets the random number.
	 *
	 * @param n the n
	 * @return the random number
	 */
	protected  int getRandomNumber(int n)
	{
		Random r = new Random();
		return r.nextInt(n);
	}

	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		if (serviceHelper != null)
		{
			serviceHelper.release();
		}
	}

	/**
	 * Gets the property store impl.
	 *
	 * @return the property store impl
	 */
	protected  PropertyStore getPropertyStoreImpl()

	{
		PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl();
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, UUID.fromString(ixit.get("IXITCO_AppId")), "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, ixit.get("IXITCO_DeviceId"), "");

		return propertyStoreImpl;
	}


	/**
	 * Gets the service helper.
	 *
	 * @return the service helper
	 */
	ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}

	/**
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;
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
		if(!string1.equals(string2)){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
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
		if(!bool){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
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
		}
		else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}


}
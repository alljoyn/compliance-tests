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

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.AboutKeys;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationSender;
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

/**
 * Notification Service conformance test cases
 */
public class NotificationService
{
	/** 
	 * Notification Consumer attributes
	 * 
	 * */
	private PropertyStore propertyStore;
	private NotificationSender nSender;
	
	private org.alljoyn.ns.Notification notif;
	
	private ServiceHelper serviceHelper;
	
	//private UUID consumerAppId = UUID.randomUUID(); //[AT4] Not Needed
	//private String consumerDeviceId = consumerAppId.toString(); //[AT4] Not Needed
	
    /*private static final String BUS_APPLICATION_NAME = "NotificationConsumerTestSuite";
    private static final String TAG = "NotifConsumerTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);*/

	private final String BUS_APPLICATION_NAME = "NotificationTestSuite"; //[AT4] Common tags for Consumer and Producer
	private final String TAG = "NotificationTestSuite";
	private final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** 
	 * Notification Producer attributes
	 * 
	 * */
	/*private static final String BUS_APPLICATION_NAME = "NotificationProducerTestSuite";
	
	protected static final String TAG = "NotfProdTestSuite";
	private static final Logger logger = LoggerFactory.getLogger(TAG);*/
	
	private static final long TIMEOUT_FOR_SHUTDOWN_IN_SECONDS = 1;

    private UserInputDetails userInputDetails = null;
    private AboutAnnouncementDetails deviceAboutAnnouncement;

    private volatile Thread waitingforUserInputThread;
    private volatile Exception notificationAssertionFailure = null;

    //private ServiceHelper serviceHelper; [AT4] Duplicated attribute

    private NotificationValidator notificationValidator;

    private AboutClient aboutClient;

    private ExecutorService executorService;

    //private AppUnderTestDetails appUnderTestDetails; //[AT4] Not Needed
    //private UUID dutAppId; //[AT4] Not Needed
    //private String dutDeviceId; //[AT4] Not Needed
    
	//private final String BUS_APPLICATION_NAME = "NotificationConsumerTestSuite"; //[AT4] Common tags for Consumer and Producer
	//private final String TAG = "NotificationTestSuite";
	//private final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG); //[AT4] Changed to Windows' Logger implementation
	
	/**
	 * [AT4] Added attributes to perform the test cases
	 *  
	 * pass
	 * inconc
	 * ics
	 * ixit
	 * ANNOUNCEMENT_TIMEOUT_IN_SECONDS 
	 *  
	 *  */
	
	Boolean pass=true;
	boolean inconc=false;
	Map<String,Boolean> ics;
	Map<String,String> ixit;
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS;

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
			String gPCO_AnnouncementTimeout)
	{
		/** 
		 * [AT4] Attributes initialization
		 * */
		
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
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);

		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			inconc = true;
			if (e != null)
			{
				if (e.getMessage().equals("Timed out waiting for About announcement"))
				{
					fail("Timed out waiting for About announcement");
				}
				else
				{
					fail("Exception: "+e.toString());
				}
			}
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		if (testCase.equals("Notification-Consumer-v1-01"))
		{
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_01();
		}
		else if (testCase.equals("Notification-Consumer-v1-02"))
		{
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_02();
		/*} else if (testCase.equals("Notification-Consumer-v1-03")) {
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_03();*/
		}
		else if(testCase.equals("Notification-Consumer-v1-04"))
		{
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_04();
		}
		else if(testCase.equals("Notification-Consumer-v1-05"))
		{
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_05();
		}
		else if(testCase.equals("Notification-Consumer-v1-06"))
		{
			setUp();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotification_Consumer_v1_06();
		}
		else if(testCase.equals("Notification-v1-01"))
		{	
			setUpReceiver();
			logger.info(String.format("Running testcase: %s", testCase));
			testNotificationsReceived();
		}
		else
		{
			fail("Test Case not valid");
		}
		
		if (testCase.equals("Notification-v1-01"))
		{
			tearDownReceiver();
		} else
		{
			tearDown();
		}
	}
	
	private void setUp() throws Exception
	{
		try
		{ 
			logger.noTag("====================================================");
			logger.info("test setUp started");
			
			logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId")));
			logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId"))));

			propertyStore = getPropertyStoreImpl();

			serviceHelper = getServiceHelper();
			serviceHelper.initializeSender(BUS_APPLICATION_NAME, null, null);

			//serviceHelper.startAboutServer((short) 1080, propertyStore); //[AT4] Disabled to avoid BUS_OJB_ALREADY_EXISTS error

			nSender = serviceHelper.initNotificationSender(propertyStore);
			
			logger.info("test setUp done");
			logger.noTag("====================================================");
		}
		catch (Exception e)
		{
			inconc = true;
			
			logger.error(String.format("Received exception during setup: ", e.getMessage()));
			tearDown(); //[AT4] Changed from releaseResources() to tearDown() to include logs
			throw e;
		}
	}
	
	private  void tearDown()
	{
		logger.noTag("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		logger.noTag("====================================================");
	}
	
	public void testNotification_Consumer_v1_01() throws Exception
	{
		String[] msgArray = {"Test Msg 1", "Test Msg 2", "Test Msg 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);
		
		nSender.send(notif, 120);
		
		logger.info("Notification message sent"); //[AT4] 
		
		checkUserInput(msgArray, msgToSend);
	}

	public void testNotification_Consumer_v1_02() throws Exception
	{
		String[] msgArray = {"Two languages Msg 1", "Two languages Msg 2", "Two languages Msg 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		text.add(new NotificationText("fr", msgToSend));
		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

		nSender.send(notif, 120);
		
		logger.info("Notification message sent"); //[AT4]
		
		checkUserInput(msgArray, msgToSend);
	}

	/*public void testNotification_Consumer_v1_03() throws Exception
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
	}*/ //[AT4] Disabled Test Case

	public void testNotification_Consumer_v1_04() throws Exception
	{
		String[] msgArray = {"Invalid langTag Msg 1", "Invalid langTag Msg 2", "Invalid langTag Msg 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("INVALID", msgToSend));

		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

		nSender.send(notif, 120);
		
		logger.info("Notification message sent"); //[AT4]
		
		checkUserInput(msgArray, msgToSend);
	}

	public void testNotification_Consumer_v1_05() throws Exception
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
			
			logger.info("Notification message sent"); //[AT4]
		}

		List<String> selectionOptions = new ArrayList<String>();
		for (List<MsgSet> messageSet : msgSets)
		{
			selectionOptions.add(buildPromptText(messageSet));
		}

		String msgSent = buildPromptText(messagesToSend);
		
		//checkUserInput(selectionOptions.toArray(new String[selectionOptions.size()]), msgSent); //[AT4] Changed to manage the display of the text
		checkUserInputLongText(selectionOptions.toArray(new String[selectionOptions.size()]), msgSent);
	}

	public void testNotification_Consumer_v1_06() throws Exception
	{
		String[] msgArray = {"Msg w/ attributes 1", "Msg w/ attributes 2", "Msg w/ attributes 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		Map<String, String> customAttributes = new HashMap<String, String>();
		customAttributes.put("org.alljoyn.validation.test", "value");

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));

		notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);
		notif.setCustomAttributes(customAttributes);
		nSender.send(notif, 120);
		
		logger.info("Notification Message sent"); //[AT4]

		checkUserInput(msgArray, msgToSend);
	}
	
	private class MsgSet
	{
		public MsgSet(String text, NotificationMessageType priority)
		{
			this.text = text;
			this.priority = priority;
		}

		public String text;
		public NotificationMessageType priority;
	}
	
	protected String buildPromptText(List<MsgSet> messagesToSend)
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
	
	private void checkUserInput(String[] msgArray, String msgToSend)
	{
		logger.info("Waiting for user response..."); //[AT4]
		
		//UserInputDetails userInputDetails = new UserInputDetails("Select the message(s) received",msgArray);
		
		//UserResponse userResponse = getValidationTestContext().waitForUserInput(userInputDetails);
		
		UserInputDetails userResponse = new UserInputDetails(TAG,"Select the message(s) received", msgArray); //[AT4] Changed previous lines to this one

		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		
		logger.info(String.format("Option sent: %s",msgToSend)); //[AT4]
		logger.info(String.format("Option selected: %s",messageSelected)); //[AT4]
		
		assertEquals("Incorrect message option selected", msgToSend, messageSelected);
	}
	
	protected int getRandomNumber(int n)
	{
		Random r = new Random();
		return r.nextInt(n);
	}

	private void releaseResources()
	{
		if (serviceHelper != null)
		{
			serviceHelper.release();
		}
	}
	
	protected  PropertyStore getPropertyStoreImpl()
	{
		/*PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl((Context) getValidationTestContext().getContext());
        propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, getAppId(), Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, getDeviceId(), Property.NO_LANGUAGE);*/
        
		PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl();
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, UUID.fromString(ixit.get("IXITCO_AppId")), "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, ixit.get("IXITCO_DeviceId"), "");

		return propertyStoreImpl;
	}

	ServiceHelper getServiceHelper()
	{
		return new ServiceHelper(logger);
	}
	
	/*public String getDeviceId()
	{
		return consumerDeviceId;
	}*/
	
	/*public UUID getAppId()
	{
		return consumerAppId;
	}*/
	
	private void setUpReceiver() throws Exception //[AT4] Changed method name to setUpReceiver
	{ 
		logger.noTag("====================================================");
		logger.info("test setUp started");

		/*appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
        dutDeviceId = appUnderTestDetails.getDeviceId();
        logger.debug(String.format("Running NotificationProducer test case against Device ID: %s", dutDeviceId));
        dutAppId = appUnderTestDetails.getAppId();
        logger.debug(String.format("Running NotificationProducer test case against App ID: %s", dutAppId)); */
		
		logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId"))); //[AT4] Added IXIT
		logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId")))); //[AT4] Added IXIT
		
		waitingforUserInputThread = Thread.currentThread();

		executorService = getExecutorService();

		try
		{
			serviceHelper = getServiceHelper();

			serviceHelper.initialize(BUS_APPLICATION_NAME, ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId"))); //[AT4] Added IXITs

			notificationValidator = getNotificationValidator();
			notificationValidator.setTestParameters(ics.get("ICSN_RichIconUrl"), ics.get("ICSN_RichAudioUrl"),
					ics.get("ICSN_RespObjectPath"), ixit.get("IXITN_NotificationVersion")); //[AT4] Added method to set IXIT values for the testcase
			serviceHelper.initNotificationReceiver(notificationValidator);

			//deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS); //[AT4] Changed to make it customizable
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}
			
			//logger.info("Partial Verdict: PASS"); //[AT4]

			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			logger.info("test setUp done");
			logger.noTag("====================================================");
		}
		catch (Exception e)
		{
			logger.error("Received exception during setup "+ e.getMessage());
			//releaseResources();
			tearDown();
			throw e;
		}
	}
	
	protected NotificationValidator getNotificationValidator()
	{
		return new NotificationValidator(ixit.get("IXITCO_DeviceId"), UUID.fromString(ixit.get("IXITCO_AppId")));
	}
	
	/*protected ServiceHelper getServiceHelper()
	{
		return new ServiceHelper(new AndroidLogger);
	}*/ //[AT4] Duplicated method 
	
	protected ExecutorService getExecutorService()
	{
		return java.util.concurrent.Executors.newSingleThreadExecutor();
	}

	public void testNotificationsReceived() throws Exception
	{
		//logger.info("Starting testNotificationReceived method");
		String[] msg = {"Continue"};
		//UserInputDetails userInputDetails = new UserInputDetails("Notification Producer Test",
		//		"Please initiate the sending of notifications from the DUT and click Continue when all notifications that you want to test have been sent", msg);
		
		BusIntrospector busIntrospector = serviceHelper.getBusIntrospector(aboutClient);
		notificationValidator.initializeForDevice(deviceAboutAnnouncement, busIntrospector, new NotificationValidationExceptionHandler()
		{
			@Override
			public void onNotificationValidationException(Exception exception)
			{
				//logger.debug("Notification failed validation checks", exception);
				fail(String.format("Notification failed validation checks: %s", exception.getMessage()));
				notificationAssertionFailure = exception;
				waitingforUserInputThread.interrupt();
			}
		});

		executorService.submit(notificationValidator);
		
		/*try
        {
            logger.debug("Waiting for user input");
            getValidationTestContext().waitForUserInput(userInputDetails);
        }
        catch (InterruptedException e)
        {
            if (notificationAssertionFailure != null)
            {
                Thread.interrupted();
                throw notificationAssertionFailure;
            }
            throw e;
        }*/
		logger.info("Waiting for user input");
		UserInputDetails userInputDetails = new UserInputDetails("Notification Producer Test",
				"Please initiate the sending of notifications from the DUT and click Continue when all notifications that you want to test have been sent", msg);
		
		
		logger.info(String.format("Received: %d notifications", notificationValidator.getNumberOfNotificationsReceived()));
		assertTrue("No notifications received!", notificationValidator.getNumberOfNotificationsReceived() > 0);
	}
	
    protected void tearDownReceiver() throws Exception
    {
        //super.tearDown();
    	logger.noTag("====================================================");
        logger.debug("test tearDown started");

        releaseResourcesReceiver();

        logger.debug("test tearDown done");
        logger.noTag("====================================================");
    }

    private void releaseResourcesReceiver() throws Exception
    {
        InterruptedException exception = null;
        executorService.shutdownNow();
        try
        {
            logger.info("Waiting for thread to shutdown");
            executorService.awaitTermination(TIMEOUT_FOR_SHUTDOWN_IN_SECONDS, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            exception = e;
        }

        disconnectFromAboutClient();
        releaseServiceHelper();
        if (exception != null)
        {
            logger.warn("Interrupted while waiting for a thread to exit", exception);
            throw new Exception("Interrupted while waiting for thread to exit", exception);
        }
    }

    private void releaseServiceHelper()
    {
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

	/**
	 * Displays text messages with \n to avoid screen overflow
	 *
	 * @param msgArray 	array of possible messages
	 * @param msgToSend message to be sent
	 */
	private void checkUserInputLongText(String[] msgArray, String msgToSend)
	{
		logger.info("Waiting for user response...");
		String[] buttons = {"Message 1","Message 2","Message 3", "Message 4", "Message 5", "Message 6"};
		UserInputDetails userResponse = new UserInputDetails(TAG,"Select the message(s) received: \n"
				+ "\n Message 1: " + msgArray[0] + "\n Message 2: " + msgArray[1] + "\n Message 3: " + msgArray[2]
				+ "\n Message 4: " + msgArray[3] + "\n Message 5: " + msgArray[4] + "\n Message 6: " + msgArray[5], buttons);

		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		
		logger.info("Notification messages sent: " + msgToSend);
		logger.info("Option selected: " + messageSelected);
		
		assertEquals("Incorrect message option selected", msgToSend, messageSelected);
	}

	/** 
	 * [AT4] Added functions to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * 
	 * */
	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second))
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
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

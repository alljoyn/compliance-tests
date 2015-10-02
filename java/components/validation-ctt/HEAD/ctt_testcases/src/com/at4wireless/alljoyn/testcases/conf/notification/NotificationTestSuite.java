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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.AboutKeys;
import org.alljoyn.bus.AboutProxy;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.services.common.PropertyStore;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.PropertyStoreImpl;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.UserInputDetails;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.notification.NotificationValidator;
import com.at4wireless.alljoyn.core.notification.NotificationValidator.NotificationValidationExceptionHandler;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

/**
 * Notification Service conformance test cases
 */
public class NotificationTestSuite
{
	/** 
	 * Notification Consumer attributes
	 * 
	 * */
	private PropertyStore propertyStore; //[AT4] Cannot delete it, a non-deprecated method needs it as input
	private NotificationSender nSender;
	private Notification notif;
	
	private ServiceHelper serviceHelper;

	private final String BUS_APPLICATION_NAME = "NotificationTestSuite"; //[AT4] Common tags for Consumer and Producer
	private static final String TAG = NotificationTestSuite.class.getSimpleName();
	private static final Logger logger = new WindowsLoggerImpl(TAG);
	
	/** 
	 * Notification Producer attributes
	 * 
	 * */
	private static final long TIMEOUT_FOR_SHUTDOWN_IN_SECONDS = 1;
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    private volatile Thread waitingforUserInputThread;
    private NotificationValidator notificationValidator;
    private AboutProxy aboutProxy;
    private ExecutorService executorService;
	
	/**
	 * [AT4] Added attributes to perform the test cases
	 *  
	 * pass
	 * inconc
	 * icsList
	 * ixitList
	 * ANNOUNCEMENT_TIMEOUT_IN_SECONDS 
	 *  
	 *  */
	private boolean pass = true;
	private boolean inconc = false;
	private Ics icsList;
	private Ixit ixitList;
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS;

	public NotificationTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		/** 
		 * [AT4] Attributes initialization
		 * */
		this.icsList = icsList;
		this.ixitList = ixitList;
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = gpList.GPCO_AnnouncementTimeout;

		try
		{
			runTestCase(testCase);
		}
		catch(Exception e)
		{
			logger.error("Exception: ", e);
			inconc = true;
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		if (testCase.equals("Notification-v1-01"))
		{
			setUpReceiver();
		}
		else
		{
			setUp();
		}
		
		logger.info(String.format("Running testcase: %s", testCase));
		
		if (testCase.equals("Notification-Consumer-v1-01"))
		{
			testNotification_Consumer_v1_01();
		}
		else if (testCase.equals("Notification-Consumer-v1-02"))
		{
			testNotification_Consumer_v1_02();
		/*}
		else if (testCase.equals("Notification-Consumer-v1-03"))
		{
			testNotification_Consumer_v1_03();*/
		}
		else if(testCase.equals("Notification-Consumer-v1-04"))
		{
			testNotification_Consumer_v1_04();
		}
		else if(testCase.equals("Notification-Consumer-v1-05"))
		{
			testNotification_Consumer_v1_05();
		}
		else if(testCase.equals("Notification-Consumer-v1-06"))
		{
			testNotification_Consumer_v1_06();
		}
		else if(testCase.equals("Notification-v1-01"))
		{	
			testNotificationsReceived();
		}
		else
		{
			fail("Test Case not valid");
		}
		
		if (testCase.equals("Notification-v1-01"))
		{
			tearDownReceiver();
		}
		else
		{
			tearDown();
		}
	}
	
	private void setUp() throws Exception
	{
		try
		{ 
			logger.raw("====================================================");
			logger.info("test setUp started");
			logger.info(String.format("Running test case against Device ID: %s", ixitList.IXITCO_DeviceId));
			logger.info(String.format("Running test case against App ID: %s", ixitList.IXITCO_AppId));

			propertyStore = getPropertyStoreImpl();

			serviceHelper = getServiceHelper();
			serviceHelper.initializeSender(BUS_APPLICATION_NAME, null, null);

			nSender = serviceHelper.initNotificationSender(propertyStore);
			
			logger.info("test setUp done");
			logger.raw("====================================================");
		}
		catch (Exception e)
		{
			logger.error("Received exception during setup: ", e.getMessage());
			
			tearDown();
			
			throw e;
		}
	}
	
	private void tearDown()
	{
		logger.raw("====================================================");
		logger.info("test tearDown started");
		
		releaseResources();
		
		logger.info("test tearDown done");
		logger.raw("====================================================");
	}
	
	public void testNotification_Consumer_v1_01() throws Exception
	{
		String[] msgArray = {"Test Msg 1", "Test Msg 2", "Test Msg 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		notif = new Notification(NotificationMessageType.INFO, text);
		
		nSender.send(notif, 120);
		
		logger.info("Notification message sent");
		
		checkUserInput(msgArray, msgToSend);
	}

	public void testNotification_Consumer_v1_02() throws Exception
	{
		String[] msgArray = {"Two languages Msg 1", "Two languages Msg 2", "Two languages Msg 3"};

		String msgToSend = msgArray[getRandomNumber(msgArray.length)];

		List<NotificationText> text = new LinkedList<NotificationText>();

		text.add(new NotificationText("en", msgToSend));
		text.add(new NotificationText("fr", msgToSend));
		notif = new Notification(NotificationMessageType.INFO, text);

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

		notif = new Notification(NotificationMessageType.INFO, text);

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

			notif = new Notification(messageToSend.priority, text);

			nSender.send(notif, 120);
			
			logger.info("Notification message sent"); //[AT4]
		}

		List<String> selectionOptions = new ArrayList<String>();
		for (List<MsgSet> messageSet : msgSets)
		{
			selectionOptions.add(buildPromptText(messageSet));
		}

		String msgSent = buildPromptText(messagesToSend);
		
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

		notif = new Notification(NotificationMessageType.INFO, text);
		notif.setCustomAttributes(customAttributes);
		nSender.send(notif, 120);
		
		logger.info("Notification Message sent"); //[AT4]

		checkUserInput(msgArray, msgToSend);
	}
	
	private class MsgSet
	{
		public String text;
		public NotificationMessageType priority;
		
		public MsgSet(String text, NotificationMessageType priority)
		{
			this.text = text;
			this.priority = priority;
		}
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
		
		UserInputDetails userResponse = new UserInputDetails(TAG, "Select the message(s) received", msgArray); //[AT4] Changed previous lines to this one

		assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
		String messageSelected = msgArray[userResponse.getOptionSelected()];
		
		logger.info("Option sent: %s", msgToSend); //[AT4]
		logger.info("Option selected: %s", messageSelected); //[AT4]
		
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
		PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl();
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, ixitList.IXITCO_AppId, "");
		propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, ixitList.IXITCO_DeviceId, "");

		return propertyStoreImpl;
	}

	ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}
	
	private void setUpReceiver() throws Exception //[AT4] Changed method name to setUpReceiver
	{ 
		logger.raw("====================================================");
		logger.info("test setUp started");
		
		logger.info("Running test case against Device ID: %s", ixitList.IXITCO_DeviceId); //[AT4] Added IXIT
		logger.info("Running test case against App ID: %s", ixitList.IXITCO_AppId); //[AT4] Added IXIT
		
		waitingforUserInputThread = Thread.currentThread();

		executorService = getExecutorService();

		try
		{
			serviceHelper = getServiceHelper();

			serviceHelper.initialize(BUS_APPLICATION_NAME, ixitList.IXITCO_DeviceId, ixitList.IXITCO_AppId); //[AT4] Added IXITs

			notificationValidator = getNotificationValidator();
			notificationValidator.setTestParameters(icsList.ICSN_RichIconUrl, icsList.ICSN_RichAudioUrl,
					icsList.ICSN_RespObjectPath, ixitList.IXITN_NotificationVersion); //[AT4] Added method to set IXIT values for the testcase
			serviceHelper.initNotificationReceiver(notificationValidator);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS); //[AT4] Changed to make it customizable
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}

			aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

			logger.info("test setUp done");
			logger.raw("====================================================");
		}
		catch (Exception e)
		{
			logger.error("Received exception during setup "+ e.getMessage());
			tearDown();
			throw e;
		}
	}
	
	protected NotificationValidator getNotificationValidator()
	{
		return new NotificationValidator(ixitList.IXITCO_DeviceId, ixitList.IXITCO_AppId);
	}
	
	protected ExecutorService getExecutorService()
	{
		return java.util.concurrent.Executors.newSingleThreadExecutor();
	}

	public void testNotificationsReceived() throws Exception
	{
		String[] msg = {"Continue"};
		
		BusIntrospector busIntrospector = serviceHelper.getBusIntrospector(deviceAboutAnnouncement);

		notificationValidator.initializeForDevice(deviceAboutAnnouncement, busIntrospector, new NotificationValidationExceptionHandler()
		{
			@Override
			public void onNotificationValidationException(Exception exception)
			{
				fail(String.format("Notification failed validation checks: %s", exception.getMessage()));
				waitingforUserInputThread.interrupt();
			}
		});

		executorService.submit(notificationValidator);
		
		logger.info("Waiting for user input");
		UserInputDetails userInputDetails = new UserInputDetails("Notification Producer Test",
				"Please initiate the sending of notifications from the DUT and click Continue when all notifications that you want to test have been sent", msg);
		
		logger.info("Received %d notifications", notificationValidator.getNumberOfNotificationsReceived());
		assertTrue("No notifications received!", notificationValidator.getNumberOfNotificationsReceived() > 0);
	}
	
    protected void tearDownReceiver() throws Exception
    {
    	logger.raw("====================================================");
        logger.debug("test tearDown started");

        releaseResourcesReceiver();

        logger.debug("test tearDown done");
        logger.raw("====================================================");
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

        disconnectFromAboutProxy();
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
    
    private void disconnectFromAboutProxy()
    {
    	if (aboutProxy != null)
    	{
    		aboutProxy = null;
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
		
		logger.info("Notification messages sent: %s", msgToSend);
		logger.info("Option selected: %s", messageSelected);
		
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

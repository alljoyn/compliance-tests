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
package org.alljoyn.validation.testing.suites.notification;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.testing.suites.BaseTestSuite;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.notification.NotificationValidator;
import org.alljoyn.validation.testing.utils.notification.NotificationValidator.NotificationValidationExceptionHandler;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

@ValidationSuite(name = "Notification-v1")
public class NotificationProducerTestSuite extends BaseTestSuite
{
    private static final String BUS_APPLICATION_NAME = "NotificationProducerTestSuite";

    protected static final String TAG = "NotfProdTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    private static final long TIMEOUT_FOR_SHUTDOWN_IN_SECONDS = 1;

    private UserInputDetails userInputDetails = null;
    private AboutAnnouncementDetails deviceAboutAnnouncement;

    private volatile Thread waitingforUserInputThread;
    private volatile Exception notificationAssertionFailure = null;

    private ServiceHelper serviceHelper;

    private NotificationValidator notificationValidator;

    private AboutClient aboutClient;

    private ExecutorService executorService;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        logger.debug("test setUp started");

        appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
        dutDeviceId = appUnderTestDetails.getDeviceId();
        logger.debug(String.format("Running NotificationProducer test case against Device ID: %s", dutDeviceId));
        dutAppId = appUnderTestDetails.getAppId();
        logger.debug(String.format("Running NotificationProducer test case against App ID: %s", dutAppId));

        waitingforUserInputThread = Thread.currentThread();

        executorService = getExecutorService();

        try
        {
            serviceHelper = getServiceHelper();

            serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

            notificationValidator = getNotificationValidator();
            serviceHelper.initNotificationReceiver(notificationValidator);

            deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
            if (deviceAboutAnnouncement == null)
            {
                throw new Exception("Timed out waiting for About announcement");
            }

            aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

            logger.debug("test setUp done");
        }
        catch (Exception e)
        {
            logger.error("Exception in setup method", e);
            releaseResources();
            throw e;
        }
    }

    protected NotificationValidator getNotificationValidator()
    {
        return new NotificationValidator(getValidationTestContext());
    }

    protected ServiceHelper getServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }

    protected ExecutorService getExecutorService()
    {
        return java.util.concurrent.Executors.newSingleThreadExecutor();
    }

    @ValidationTest(name = "Notification-v1-01")
    public void testNotificationsReceived() throws Exception
    {
        logger.debug("Starting testNotificationReceived method");
        userInputDetails = new UserInputDetails("Notification Producer Test",
                "Please initiate the sending of notifications from the DUT and click Continue when all notifications that you want to test have been sent", "Continue");

        BusIntrospector busIntrospector = serviceHelper.getBusIntrospector(aboutClient);
        notificationValidator.initializeForDevice(deviceAboutAnnouncement, busIntrospector, new NotificationValidationExceptionHandler()
        {

            @Override
            public void onNotificationValidationException(Exception exception)
            {
                logger.debug("Notificiation failed validation checks", exception);
                notificationAssertionFailure = exception;
                waitingforUserInputThread.interrupt();
            }
        });

        executorService.submit(notificationValidator);

        try
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
        }
        logger.debug(String.format("Received: %d notifications", notificationValidator.getNumberOfNotificationsReceived()));
        assertTrue("No notifications received!", notificationValidator.getNumberOfNotificationsReceived() > 0);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        logger.debug("test tearDown started");

        releaseResources();

        logger.debug("test tearDown done");
    }

    private void releaseResources() throws Exception
    {

        InterruptedException exception = null;
        executorService.shutdownNow();
        try
        {
            logger.debug("Waiting for thread to shutdown");
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
}

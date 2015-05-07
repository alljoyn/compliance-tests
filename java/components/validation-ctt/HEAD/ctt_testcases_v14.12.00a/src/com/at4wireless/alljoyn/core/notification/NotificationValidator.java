package com.at4wireless.alljoyn.core.notification;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.RichAudioUrl;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;



public class NotificationValidator implements NotificationReceiver, Runnable
{
	
    
    private int IXITN_NotificationVersion;
    private boolean ICSN_RichIconUrl;
    private boolean  ICSN_RichAudioUrl;
    private boolean  ICSN_RespObjectPath;
    private static final String TAG = "NotificationValidator";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    private AtomicInteger notificationCounter = new AtomicInteger();
    private BusIntrospector busIntrospector;
    String deviceId;
    UUID appId;
    
    private NotificationValidationExceptionHandler notificationValidationExceptionHandler;
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    
    private NotificationHandler notificationHandler;

    public interface NotificationValidationExceptionHandler
    {
        void onNotificationValidationException(Exception exception);
    }

    public NotificationValidator(String dutDeviceId, UUID dutAppIddeviceId)
    {
    	deviceId=dutDeviceId;
    			appId=	dutAppIddeviceId;
        notificationHandler = getNotificationHandler();
        notificationHandler.initializeForDevice(dutDeviceId, dutAppIddeviceId);
   
    }

    protected NotificationHandler getNotificationHandler()
    {
        return new NotificationHandler();
    }

    public void initializeForDevice(AboutAnnouncementDetails deviceAboutAnnouncement, BusIntrospector busIntrospector,
            NotificationValidationExceptionHandler notificationValidationExceptionHandler)
    {
        this.deviceAboutAnnouncement = deviceAboutAnnouncement;
        this.busIntrospector = busIntrospector;
        this.notificationValidationExceptionHandler = notificationValidationExceptionHandler;
    }

    @Override
    public void receive(Notification notification)
    {
        notificationHandler.receive(notification);
    }

    @Override
    public void dismiss(int arg0, UUID arg1)
    {
    }

    @Override
    public void run()
    {
        Notification notification;

        try
        {
            while ((notification = notificationHandler.getReceivedNotification()) != null)
            {
                logger.info(String.format("Validating notification from DUT with messageId: %d", notification.getMessageId()));

                try
                {
                	 notificationCounter.getAndIncrement();
                    boolean includedRichIconUrl = false;
                    boolean includedRichAudioUrl = false;
                    boolean includesResponseObjectPath = false;

                    assertEquals(String.format("Notification version received: "+notification.getVersion()+" does not match IXIT: "
                    		+IXITN_NotificationVersion), IXITN_NotificationVersion, notification.getVersion());
                    logger.info("Notification version received: "+notification.getVersion()+" matches IXIT: "
                    		+IXITN_NotificationVersion);
                    logger.info("Partial Verdict: PASS");
                    //String notifAppName = notification.getAppName();
                    assertEquals("AppName parameter received in notification: "+notification.getAppName()+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getAppName(), deviceAboutAnnouncement.getAppName(), notification.getAppName());
                    logger.info("AppName parameter received: "+notification.getAppName()+" matches About Announcement parameter: "
                    		+deviceAboutAnnouncement.getAppName());
                    logger.info("Partial Verdict: PASS");
                    //String notifdeviceName = notification.getDeviceName();
                    assertEquals("DeviceName parameter received in notification: "+notification.getDeviceName()+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getDeviceName(), deviceAboutAnnouncement.getDeviceName(), notification.getDeviceName());
                    logger.info("DeviceName parameter received in notification: "+notification.getDeviceName()+" matches About Announcement parameter: "
                    		+deviceAboutAnnouncement.getDeviceName());
                    logger.info("Partial Verdict: PASS");
                    assertNotNull("Notification messageType value not valid!", notification.getMessageType());
                    logger.info("Valid notification messageType value: "+notification.getMessageType());
                    logger.info("Partial Verdict: PASS");
                    logger.info("The received Message Id: " + notification.getMessageId());

                    String richIconUrlString = notification.getRichIconUrl();

                    if (ICSN_RichIconUrl && richIconUrlString != null)
                    {
                        try
                        {
                            logger.info("Rich icon URL: '" + richIconUrlString + "'");
                            new URL(richIconUrlString);
                            includedRichIconUrl = true;
                        }
                        catch (MalformedURLException e)
                        {
                            throw new Exception("RichIconUrl malformed: '" + richIconUrlString + "'", e);
                        }
                    }

                    List<RichAudioUrl> richAudioUrls = notification.getRichAudioUrl();

                    if (ICSN_RichAudioUrl && richAudioUrls != null)
                    {
                        for (RichAudioUrl richAudioUrl : richAudioUrls)
                        {
                            String urlString = richAudioUrl.getUrl();
                            try
                            {
                                new URL(urlString);
                                logger.info("Rich audio URL: '" + urlString + "'");
                                includedRichAudioUrl = true;
                            }
                            catch (MalformedURLException e)
                            {
                                throw new Exception("RichAudioUrl malformed: '" + urlString + "'", e);
                            }
                        }
                    }

                    String responseObjectPath = notification.getResponseObjectPath();
                    if (ICSN_RespObjectPath && responseObjectPath != null)
                    {
                        logger.info("Control Panel Service response Object Path: '" + responseObjectPath + "'");

                        try
                        {
                            busIntrospector.introspect(responseObjectPath);
                        }
                        catch (ErrorReplyBusException e)
                        {
                            throw new Exception("Exception retrieving responseObject", e);
                        }

                        includesResponseObjectPath = true;
                    }
                   

                    StringBuilder noteMsg = new StringBuilder();
                    noteMsg.append("Notification Received: ");
                    noteMsg.append("messageType: ");
                    noteMsg.append(notification.getMessageType());
                    noteMsg.append("; richIconUrl: ");
                    noteMsg.append(includedRichIconUrl);
                    noteMsg.append("; richAudioUrl: ");
                    noteMsg.append(includedRichAudioUrl);
                    noteMsg.append("; responseObject: ");
                    noteMsg.append(includesResponseObjectPath);
                    String noteMsgString = noteMsg.toString();
                    logger.addNote(noteMsgString);
                    logger.info(noteMsgString);
                }
                catch (Throwable throwable)
                {
                    if (throwable instanceof Exception)
                    {
                        notificationValidationExceptionHandler.onNotificationValidationException((Exception) throwable);
                    }
                    else
                    {
                        notificationValidationExceptionHandler.onNotificationValidationException(new Exception(throwable));
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
    
    

    private void assertNotNull(String errorMsg,
			NotificationMessageType messageType) throws Exception {
		// TODO Auto-generated method stub
    	if(messageType==null)
    	{
    		logger.error(errorMsg);
    		throw new Exception(errorMsg);
    	}
    	
		
	}

	private void assertEquals(String errorMsg, String appName, String notifAppName) throws Exception {
		// TODO Auto-generated method stub
		if(!appName.equals(notifAppName)){
			
			logger.error(errorMsg);
			throw new Exception(errorMsg);
			
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMsg, int notificationServiceVersion,
			int version) throws Exception {
		// TODO Auto-generated method stub
		
		if(notificationServiceVersion!=version){
		logger.error(errorMsg);
		throw new Exception(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	public int getNumberOfNotificationsReceived()
    {
        return notificationCounter.get();
    }

	public void setTestParameters(boolean iCSN_RichIconUrl ,boolean iCSN_RichAudioUrl,boolean iCSN_RespObjectPath, String iXITN_NotificationVersion) {
		// TODO Auto-generated method stub
		
		ICSN_RichIconUrl=iCSN_RichIconUrl;
		ICSN_RichAudioUrl=iCSN_RichAudioUrl;
		ICSN_RespObjectPath=iCSN_RespObjectPath;
		
		IXITN_NotificationVersion=Integer.parseInt(iXITN_NotificationVersion);
		
	}

	
}
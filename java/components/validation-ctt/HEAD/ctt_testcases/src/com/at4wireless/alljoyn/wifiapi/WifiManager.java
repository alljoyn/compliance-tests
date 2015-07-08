package com.at4wireless.alljoyn.wifiapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class WifiManager
{
	private PointerByReference hClient;
	private Guid.GUID interfaceGuid;
	
    private static final String TAG = "WifiManager";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	private WlanApi.WlanNotificationCallback notificationCallback = new WlanApi.WlanNotificationCallback()
	{
		private boolean connected = false;
		private boolean disconnected = false;
		
		public boolean isConnected()
		{
			return connected;
		}
		
		public boolean isDisconnected()
		{
			return disconnected;
		}
		
		@Override
		public void callback(WlanNotificationData.ByReference pNotifData, Pointer pContext)
		{
			WlanConnectionNotificationData.ByReference pConnNotifData = null;
			
			if (pNotifData != null)
			{
				switch (pNotifData.NotificationSource)
				{
					case 8: //wlan_notification_source_acm
						//print some notifications as examples
						switch (pNotifData.NotificationCode)
						{
							case 10: //wlan_notification_acm_connection_complete
								if (pNotifData.dwDataSize < pNotifData.size())
								{
									return;
								}
								pConnNotifData = new WlanConnectionNotificationData.ByReference(pNotifData.pData.getValue());
								if (pConnNotifData.wlanReasonCode == 0)
								{
									logger.debug("The connection succeeded.");
									connected = true;
									disconnected = false;
									
									if (pConnNotifData.wlanConnectionMode == 3 ||
											pConnNotifData.wlanConnectionMode == 2)
									{
										//the temporary profile generated for discovery
										logger.debug("The profile used for this connection is as follows");
										logger.debug(pConnNotifData.strProfileXml);
									}
								}
								else
								{
									logger.debug("The connection failed.");
									logger.debug("Reason code: "+pConnNotifData.wlanReasonCode);
								}
								break;
							case 9: //wlan_notification_acm_connection_start
								if (pNotifData.dwDataSize != pNotifData.size())
								{
									return;
								}
								pConnNotifData = new WlanConnectionNotificationData.ByReference(pNotifData.pData.getValue());
								//print out some connection information
								System.out.print("Currently connecting to "+pConnNotifData.dot11Ssid.ucSSID);
								System.out.print(" using profile "+pConnNotifData.strProfileName.toString());
								System.out.print(", connection mode is "+pConnNotifData.wlanConnectionMode);
								logger.debug(", BSS type is "+pConnNotifData.dot11BssType);
								break;
							case 21: //wlan_notification_acm_disconnected
								if (pNotifData.dwDataSize < pNotifData.size())
								{
									return;
								}
								pConnNotifData = new WlanConnectionNotificationData.ByReference(pNotifData.pData.getValue());
								if (pConnNotifData.wlanReasonCode == 0)
								{
									logger.debug("The disconnection succeeded.");
									disconnected = true;
									connected = false;
								}
								else
								{
									logger.debug("The disconnection failed.");
									logger.debug("Reason code: "+pConnNotifData.wlanReasonCode);
								}
								return;
							
						}
					
						return;
					case 16: //wlan_notification_source_msm
						logger.debug("Got notification "+pNotifData.NotificationCode+" from MSM.");
						return;
				}
			}
		}
	};
	
	public WifiManager()
	{
		hClient = openHandle();
		interfaceGuid = enumInterfaces().InterfaceInfo[0].InterfaceGuid;
	}
	
	private PointerByReference openHandle()
	{
		int dwError;
		PointerByReference pdwNegotiatedVersion  = new PointerByReference();
        PointerByReference phClientHandle  = new PointerByReference();
        
		if ((dwError = WlanApi.INSTANCE.WlanOpenHandle(
				2, 
				null, 
				pdwNegotiatedVersion,
				//hClient.getPointer()
				phClientHandle.getPointer()
				)) != 0)
		{
			return null;
		}
		
		return phClientHandle;
	}
	
	private WlanInterfaceInfoList.ByReference enumInterfaces()
	{
		int dwError;
		PointerByReference p = new PointerByReference();
		//PWlanInterfaceInfoList.ByReference ppInterfaceInfoList = new PWlanInterfaceInfoList.ByReference();
		//enumerate wireless interfaces

		if ((dwError = WlanApi.INSTANCE.WlanEnumInterfaces(
				hClient.getValue(),
				null,
				p
				)) != 0)
		{
			return null;
		}
		
		WlanInterfaceInfoList.ByReference pInterfaceInfoList = new WlanInterfaceInfoList.ByReference(p.getValue());
		return pInterfaceInfoList;
	}
	
	private void scan()
	{
		int dwError;
		
		dwError = WlanApi.INSTANCE.WlanScan(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				null,
				null,
				null
				);
	}
	
	//get the list of visible wireless networks
	private List<ScanResult> getVisibleNetworkList()
	{
		int dwError;
        WlanAvailableNetworkList.ByReference pAvailableNetworkList;
        PointerByReference p = new PointerByReference();
        
		if ((dwError = WlanApi.INSTANCE.WlanGetAvailableNetworkList(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				1,
				null,
				p)) != 0)
		{
			return null;
		}
		
        pAvailableNetworkList = new WlanAvailableNetworkList.ByReference(p.getValue());
		
		List<ScanResult> scanResults = new ArrayList<ScanResult>();
		ScanResult network;
		
		for (int i = 0; i< pAvailableNetworkList.dwNumberOfItems; i++)
		{
			int ssidArrayLength = pAvailableNetworkList.Network[i].dot11Ssid.uSSIDLength;
			if (ssidArrayLength > Dot11Ssid.DOT11_SSID_MAX_LENGTH)
			{
				ssidArrayLength = Dot11Ssid.DOT11_SSID_MAX_LENGTH;
			}
			
			String ssid = new String(Arrays.copyOfRange(pAvailableNetworkList.Network[i].dot11Ssid.ucSSID, 0, ssidArrayLength));
			String bssid = Integer.toString(pAvailableNetworkList.Network[i].uNumberOfBssids);
			String capabilities = Integer.toString(pAvailableNetworkList.Network[i].dot11DefaultAuthAlgorithm)
					+", "+Integer.toString(pAvailableNetworkList.Network[i].dot11DefaultCipherAlgorithm);
			int level = -100 +(pAvailableNetworkList.Network[i].wlanSignalQuality)/2;
			int frequency = 2400;
			
			network = new ScanResult(ssid, bssid, capabilities, level, frequency);
			scanResults.add(network);
		}
		
		return scanResults;

	}
	
	public void connect(String targetNetworkType, String targetNetworkProfile)
	{
		WlanConnectionParameters.ByReference wlanConnPara = new WlanConnectionParameters.ByReference();
		PDot11Ssid pDot11Ssid = new PDot11Ssid();
		String type = targetNetworkType;
		int dwError;
		
		//set the connection mode (connecting using a profile)
		wlanConnPara.wlanConnectionMode = 0;
		//set the profile name
		wlanConnPara.strProfile = new WString(targetNetworkProfile);
		//set the SSID
		pDot11Ssid.dot11Ssid = new Dot11Ssid.ByReference();
		pDot11Ssid.dot11Ssid.ucSSID = targetNetworkType.getBytes();
		pDot11Ssid.dot11Ssid.uSSIDLength = targetNetworkType.length();

		wlanConnPara.pDot11Ssid = pDot11Ssid;
		
		//get BSS type
		if (type.equalsIgnoreCase("adhoc") || type.equalsIgnoreCase("a"))
		{
			wlanConnPara.dot11BssType = 2;
		}
		else if (type.equalsIgnoreCase("infrastructure") || type.equalsIgnoreCase("i"))
		{
			wlanConnPara.dot11BssType = 1;
		}
		else
		{
			dwError = 1;
			return;
		}
		
		//the desired BSSID list is empty
		//pWlanConnPara.WlanConnectionParameters.pDesiredBssidList = null;
		//no connection flags
		wlanConnPara.dwFlags = 0;
		
		dwError = WlanApi.INSTANCE.WlanConnect(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				wlanConnPara,
				null
				);
	}
	
	private void disconnect()
	{
		int dwError;
        
		dwError = WlanApi.INSTANCE.WlanDisconnect(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				null
				);
	}
	
	private void registerNotification()
	{
		int dwError;
		int WLAN_NOTIFICATION_SOURCE_ACM = 8;
		int WLAN_NOTIFICATION_SOURCE_MCM = 16;
			
		if ((dwError = WlanApi.INSTANCE.WlanRegisterNotification(
				hClient.getValue(),
				WLAN_NOTIFICATION_SOURCE_ACM,
				false,
				notificationCallback,
				null,
				null,
				new IntByReference(0))) != 0)
		{
			return;
		}
	}
	
	private void unregisterNotification()
	{
		int dwError;
	
		//unregister notifications
		if ((dwError = WlanApi.INSTANCE.WlanRegisterNotification(
				hClient.getValue(),
				0,
				false,
				null,
				null,
				null,
				new IntByReference(0)
				)) == 0)
		{
			return;
			//logger.debug("ACM notifications are successfully unregistered.");
		}
		/*else
		{
			logger.debug("Error "+dwError+" occurs when unregister ACM notifications.");
		}*/
	}
	

	
	private List<String> getProfileList()
	{
		int dwError;
		//PWlanProfileInfoList.ByReference ppProfileList = new PWlanProfileInfoList.ByReference();
		PointerByReference p = new PointerByReference();
		
		if ((dwError = WlanApi.INSTANCE.WlanGetProfileList(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				null,
				p)) != 0)
		{
			return null;
		}
		
		WlanProfileInfoList.ByReference pProfileList = new WlanProfileInfoList.ByReference(p.getValue());

		List<String> profileList = new ArrayList<String>();
		for (int i = 0; i < pProfileList.dwNumberOfItems; i++)
		{
			String profileName = new String(pProfileList.ProfileInfo[i].strProfileName);
			profileList.add(profileName.trim());
		}
		
		return profileList;
	}
	
	private boolean setProfile(String profile)
	{
		int dwError;
		int dwFlags = 0;
		IntByReference dwReason = new IntByReference();
		
		if ((dwError = WlanApi.INSTANCE.WlanSetProfile(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				dwFlags,
				new WString(profile),
				null,
				true,
				null,
				dwReason)) != 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private boolean deleteProfile(String profileName)
	{
		int dwError;
		
		if ((dwError = WlanApi.INSTANCE.WlanDeleteProfile(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				new WString(profileName),
				null)) != 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isWifiEnabled()
	{
		return (enumInterfaces().dwNumberOfItems != 0);
	}
	
	public String connectedSsid()
	{
		int dwError;
		IntByReference dwDataSize = new IntByReference();
		IntByReference pWlanOpcodeValueType = new IntByReference();
		WlanConnectionAttributes.ByReference pWlanConnectionAttributes = new WlanConnectionAttributes.ByReference();
		PointerByReference p = new PointerByReference();
		
		WlanApi.INSTANCE.WlanFreeMemory(p.getValue());
		p.setValue(null);
		
		if((dwError = WlanApi.INSTANCE.WlanQueryInterface(
				hClient.getValue(),
				interfaceGuid.getPointer(),
				7,
				null,
				dwDataSize,
				p,
				pWlanOpcodeValueType
				)) != 0)
		{
			return null;
		}
		
		pWlanConnectionAttributes = new WlanConnectionAttributes.ByReference(p.getValue());
		
		int ssidArrayLength = pWlanConnectionAttributes.wlanAssociationAttributes.dot11Ssid.uSSIDLength;
		if (ssidArrayLength > Dot11Ssid.DOT11_SSID_MAX_LENGTH)
		{
			ssidArrayLength = Dot11Ssid.DOT11_SSID_MAX_LENGTH;
		}

		return new String(Arrays.copyOfRange(pWlanConnectionAttributes.wlanAssociationAttributes.dot11Ssid.ucSSID, 0, ssidArrayLength));
	}
	
	public List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException
	{
		List<ScanResult> scanResults = null;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();
		scan();
		while ((scanResults == null) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			scanResults = getVisibleNetworkList();
			Thread.sleep(10);
		}
		
		return scanResults;
	}
	
	public String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException
	{
		String ssid = connectedSsid();
		registerNotification();
		disconnect();
		
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();
		
		while ((!notificationCallback.isDisconnected()) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			Thread.sleep(100);
		}
		
		unregisterNotification();
		
		if (notificationCallback.isDisconnected())
		{
			return ssid;
		}
		else
		{
			return null;
		}
	}
	
	public String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException
	{
		String currentSsid = connectedSsid();
		if (ssid.equals(currentSsid))
		{
			return currentSsid;
		}
		scan();
		registerNotification();
		
		connect("adhoc", ssid);
		
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();
		
		while ((!notificationCallback.isConnected()) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			Thread.sleep(100);
		}
		
		unregisterNotification();
		
		if (notificationCallback.isConnected())
		{
			return ssid;
		}
		else
		{
			return null;
		}
	}
	
	public List<String> getConfiguredNetworks()
	{
		return getProfileList();
	}
	
	public boolean addNetwork(String profile)
	{
		return setProfile(profile);
	}
	
	public boolean removeNetwork(String profileName)
	{
		return deleteProfile(profileName);
	}
	
	public void release()
	{
		if (hClient != null)
		{
			WlanApi.INSTANCE.WlanCloseHandle(
					hClient.getPointer(),
					null
					);
		}
		if (interfaceGuid != null)
		{
			interfaceGuid = null;
			//WlanApi.INSTANCE.WlanFreeMemory(interfaceGuid.getPointer());
		}
	}
}

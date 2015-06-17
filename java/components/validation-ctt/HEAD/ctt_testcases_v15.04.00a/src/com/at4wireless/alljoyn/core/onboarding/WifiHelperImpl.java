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
package  com.at4wireless.alljoyn.core.onboarding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class WifiHelperImpl implements WifiHelper
{
	private static final String TAG = "WifiHelperImpl";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	//private Context context;
	//private WifiBroadcastReceiver wifiBroadcastReceiver;
	//private WifiManager wifiManager;

	String SSID;

	@Override
	public void initialize()
	{
		/*wifiManager = getWifiManager(context);

        wifiBroadcastReceiver = new WifiBroadcastReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiBroadcastReceiver, filter);*/	
		SSID = null;
	}
	
	@Override
	public void release()
	{
		/*if (wifiBroadcastReceiver != null)
		{
			context.unregisterReceiver(wifiBroadcastReceiver);
			wifiBroadcastReceiver = null;
		} */
		
		SSID = null;
		disconnect();
	}
	
	/*protected WifiManager getWifiManager(Context context)
	{
		return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}*/

	@Override
	public boolean isWifiEnabled()
	{
		//return wifiManager.isWifiEnabled();
		return true;
	}

	@Override
	public String getCurrentSSID()
	{
		//checkForWifiEnabled();
		//return wifiBroadcastReceiver.connectedSSID();
		return SSID;
	}

	@Override
	public List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException
	{
		//checkForWifiEnabled();
        //return wifiBroadcastReceiver.waitForScanResults(timeout, unit);
		List<ScanResult> scanResults=null;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();
		while ((scanResults==null) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			scanResults=getScanResults();
		}
		return scanResults;
	}

	@Override
	public String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException
	{
		//checkForWifiEnabled();
        //return wifiBroadcastReceiver.waitForDisconnect(timeout, unit);
		String disconnectedSsid=null;
		boolean disconnected = false;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();

		while ((!disconnected) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			disconnected=disconnect();
		}
		if(disconnected){
			logger.info("It has been disconnected from the Wifi Network");
			disconnectedSsid=SSID;
			SSID=null;
		}else{
			logger.info("It has not been disconnected from the Wifi Network");
		}

		return disconnectedSsid;
	}

	@Override
	public String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException
	{
		//checkForWifiEnabled();
        //return wifiBroadcastReceiver.waitForConnect(ssid, timeout, unit);
		boolean connected = false;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();

		while ((!connected) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			connected=connectToProfile(ssid);
		}
		if(connected){
			logger.info("Connected to: "+ssid);
			return ssid;
		}else{
			logger.info("Not connected to: "+ssid);
			return null;
		}
	}

	@Override
	public boolean waitForNetworkAvailable(String ssid, long timeout, TimeUnit unit) throws InterruptedException
	{
		logger.info("Waiting for network "+ssid+" to be available"); //[AT4]
		
		boolean isAvailable = false;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();
		
		while ((!isAvailable) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{
			long timeRemaining = startTime + timeToWaitInMs - System.currentTimeMillis();
			
			List<ScanResult> scanResults = waitForScanResults(timeRemaining, TimeUnit.MILLISECONDS);
			if (scanResults != null)
			{
				for (ScanResult scanResult : scanResults)
				{						
					if (ssid.equals(scanResult.SSID))
					{
						isAvailable = true;
						break;
					}
				}
			}
		}
		return isAvailable;
	}

	@Override
	public String connectToNetwork(WifiNetworkConfig wifiNetworkConfig, boolean recreate, long timeout, TimeUnit unit) throws InterruptedException
	{
		/*String ssid = wifiNetworkConfig.getSsid();
        String password = wifiNetworkConfig.getPassphrase();
        String securityType = wifiNetworkConfig.getSecurityType();

        long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
        long startTime = System.currentTimeMillis();

        logger.debug(String.format("Attempting to connect to SSID is: %s", ssid));

        Integer networkId = null;
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration tempConfig : configuredNetworks)
        {
            if (tempConfig.SSID != null && tempConfig.SSID.equals("\"" + ssid + "\""))
            {
                networkId = tempConfig.networkId;
                break;
            }
        }

        if (null == networkId)
        {
            WifiConfiguration tmpConfig = createWifiConfiguration(ssid, password, securityType);
            logger.debug("Adding Wifi network");
            networkId = wifiManager.addNetwork(tmpConfig);
            saveWifiConfiguration();
        }
        else if (recreate)
        {
            WifiConfiguration tmpConfig = createWifiConfiguration(ssid, password, securityType);
            logger.debug("Removing Wifi network config");
            tmpConfig.networkId = networkId;
            removeWifiNetwork(networkId);

            saveWifiConfiguration();
            logger.debug("Adding Wifi network");
            networkId = wifiManager.addNetwork(tmpConfig);
            saveWifiConfiguration();
        }

        if (-1 == networkId)
        {
            throw new WifiUnableToAddNetworkException("Unable to add wifi network with ssid: " + ssid);
        }

        String currentSsid = wifiBroadcastReceiver.connectedSSID();

        if (!wifiManager.enableNetwork(networkId, false))
        {
            throw new WifiHelperException("WifiManager.enableNetwork returned false");
        }

        if (!wifiManager.disconnect())
        {
            throw new WifiHelperException("WifiManager.disconnect returned false");
        }

        long timeRemaining = startTime + timeToWaitInMs - System.currentTimeMillis();
        if (currentSsid != null)
        {
            if (waitForDisconnect(timeRemaining, TimeUnit.MILLISECONDS) == null)
            {
                throw new WifiHelperException(String.format("Timed out waiting for disconnect from %s", currentSsid));
            }
        }

        if (!wifiManager.enableNetwork(networkId, true))
        {
            throw new WifiHelperException("WifiManager.enableNetwork returned false");
        }

        timeRemaining = startTime + timeToWaitInMs - System.currentTimeMillis();
        return waitForConnect(ssid, timeRemaining, TimeUnit.MILLISECONDS);*/
		
		Boolean validAuth = createWifiProfile(wifiNetworkConfig.getSsid(), wifiNetworkConfig.getSsid(),
				wifiNetworkConfig.getSecurityType(),
				wifiNetworkConfig.getPassphrase());

		SSID=waitForConnect(wifiNetworkConfig.getSsid(), timeout, unit);
		return SSID;
	}

	/*private WifiConfiguration createWifiConfiguration(String ssid, String password, String securityType)
    {
        WifiConfiguration tmpConfig = new WifiConfiguration();
        tmpConfig.SSID = "\"" + ssid + "\"";

        if (isValidSecurityType(securityType))
        {
            tmpConfig.preSharedKey = "\"" + password + "\"";
        }
        else if (OnboardingService.AuthType.OPEN.name().equalsIgnoreCase(securityType) || "NONE".equalsIgnoreCase(securityType))
        {
            tmpConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        else
        {
            throw new IllegalArgumentException("Invalid security type: " + securityType);
        }

        return tmpConfig;
    }*/

	/*private boolean isValidSecurityType(String securityType)
    {
        boolean validSecurityType = false;

        if (OnboardingService.AuthType.WPA2_AUTO.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPA_AUTO.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WEP.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPA_TKIP.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPA_CCMP.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPA2_TKIP.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPA2_CCMP.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if (OnboardingService.AuthType.WPS.name().equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if ("WPA".equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }
        else if ("WPA2".equalsIgnoreCase(securityType))
        {
            validSecurityType = true;
        }

        return validSecurityType;
    }*/
	
	/*private void removeWifiNetwork(Integer networkId)
    {
        if (!wifiManager.removeNetwork(networkId))
        {
            throw new WifiHelperException("WifiManager.removeNetwork returned false");
        }
    }*/

    /*private void saveWifiConfiguration()
    {
        if (!wifiManager.saveConfiguration())
        {
            throw new WifiHelperException("WifiManager.saveConfiguration returned false");
        }
    }*/
	
	/*private void checkForWifiEnabled()
	{
		if (false == isWifiEnabled())
		{
			throw new WifiNotEnabledException();
		}
	}*/
	
	/**
	 * [AT4] 	Temporary functions to manage WiFi connections with netsh. The main problem
	 * 			found when using this method is not to be able to receive other feedback than
	 * 			console output, what makes it strongly dependent of the language. It has to be
	 * 			changed to JNI ASAP.
	 */

	private boolean connectToProfile(String profileName)
	{
		List<String> commands = new ArrayList<String>();
			commands.add("netsh");
			commands.add("wlan");
			commands.add("connect");
			commands.add("name="+profileName);
		ProcessBuilder pb = new ProcessBuilder(commands);
		//ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "connect","name=\""+profileName+"\"");
		pb.redirectErrorStream(true);
		Process process = null;
		try { 
			process = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				//if(line.contains("correct")){ //[AT4] This has to be changed to Native API
					return true; 
				//}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private Boolean createWifiProfile(String profileName,String ssid, String securityType,
			String passphrase)
	{
		String profile=null;

		if (securityType.equals("0")) {	
			profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "<name>"+profileName+"</name>"
					+ "<SSIDConfig>"
					+ "	<SSID>"
					+ "		<name>"+ssid+"</name>"
					+ "	</SSID>"
					//+ "	<nonBroadcast>false</nonBroadcast>"
					+ "</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					//+ "<connectionMode>manual</connectionMode>"
					+ "<MSM>"
					+ "	<security>"
					+ "		<authEncryption>"
					+ "			<authentication>open</authentication>"
					+ "			<encryption>none</encryption>"
					+ "			<useOneX>false</useOneX>"
					+ "		</authEncryption>"
					+ "	</security>"
					+ "</MSM>"
					+ "</WLANProfile>";
		} else if (securityType.equals("1")) {
        	profile="<?xml version=\"1.0\"?>"
        			+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
        			+ "<name>"+profileName+"</name>"
        			+ "<SSIDConfig>"
        			+ "	<SSID>"
        			+ "		<name>"+ssid+"</name>"
        			+ "	</SSID>"
        			+ "</SSIDConfig>"
        			+ "<connectionType>ESS</connectionType>"
        			+ "<MSM>"
        			+ "	<security>"
        			+ "		<authEncryption>"
        			+ "			<authentication>open</authentication>"
        			+ "			<encryption>WEP</encryption>"
        			+ "			<useOneX>false</useOneX>"
        			+ "		</authEncryption>"
        			+ "		<sharedKey>"
        			+ "			<keyType>networkKey</keyType>"
        			+ "			<protected>false</protected>"
        			+ "			<keyMaterial>"+passphrase+"</keyMaterial>"
        			+ "		</sharedKey>"
        			+ "		<keyIndex>0</keyIndex>"
        			+ "	</security>"
        			+ "</MSM>"
        			+ "</WLANProfile>";
        } else if (securityType.equals("2")) {
			profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "<name>"+profileName+"</name>"
					+ "<SSIDConfig>"
					+ "	<SSID>"						
					+ "		<name>"+ssid+"</name>"
					+ "	</SSID>"
					+ "</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					+ "<connectionMode>auto</connectionMode>"
					+ "<MSM>"
					+ "	<security>"
					+ "		<authEncryption>"
					+ "			<authentication>WPAPSK</authentication>"
					+ "			<encryption>TKIP</encryption>"
					+ "			<useOneX>false</useOneX>"
					+ "		</authEncryption>"
					+ "		<sharedKey>"
					+ "			<keyType>passPhrase</keyType>"
					+ "			<protected>false</protected>"
					+ "			<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "		</sharedKey>"
					+ "	</security>"
					+ "</MSM>"
					+ "</WLANProfile>";
        } else if (securityType.equals("3")) {
			profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "<name>"+profileName+"</name>"
					+ "<SSIDConfig>"
					+ "	<SSID>"						
					+ "		<name>"+ssid+"</name>"
					+ "	</SSID>"
					+ "</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					+ "<connectionMode>auto</connectionMode>"
					+ "<MSM>"
					+ "	<security>"
					+ "		<authEncryption>"
					+ "			<authentication>WPAPSK</authentication>"
					+ "			<encryption>AES</encryption>"
					+ "			<useOneX>false</useOneX>"
					+ "		</authEncryption>"
					+ "		<sharedKey>"
					+ "			<keyType>passPhrase</keyType>"
					+ "			<protected>false</protected>"
					+ "			<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "		</sharedKey>"
					+ "	</security>"
					+ "</MSM>"
					+ "</WLANProfile>";
        } else if (securityType.equals("4")) {
        	profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "<name>"+profileName+"</name>"
					+ "<SSIDConfig>"
					+ "	<SSID>"
					+ "		<name>"+ssid+"</name>"
					+ "	</SSID>"
					+ "</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					+ "<connectionMode>auto</connectionMode>"
					+ "<MSM>"
					+ "	<security>"
					+ "		<authEncryption>"
					+ "			<authentication>WPA2PSK</authentication>"
					+ "			<encryption>TKIP</encryption>"
					+ "			<useOneX>false</useOneX>"
					+ "		</authEncryption>"
					+ "		<sharedKey>"
					+ "			<keyType>passPhrase</keyType>"
					+ "			<protected>false</protected>"
					+ "			<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "		</sharedKey>"
					+ "	</security>"
					+ "</MSM>"
					+ "</WLANProfile>";
        } else if (securityType.equals("5")) {
        	profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "<name>"+profileName+"</name>"
					+ "<SSIDConfig>"
					+ "	<SSID>"
					+ "		<name>"+ssid+"</name>"
					+ "	</SSID>"
					+ "</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					+ "<connectionMode>auto</connectionMode>"
					+ "<MSM>"
					+ "	<security>"
					+ "		<authEncryption>"
					+ "			<authentication>WPA2PSK</authentication>"
					+ "			<encryption>AES</encryption>"
					+ "			<useOneX>false</useOneX>"
					+ "		</authEncryption>"
					+ "		<sharedKey>"
					+ "			<keyType>passPhrase</keyType>"
					+ "			<protected>false</protected>"
					+ "			<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "		</sharedKey>"
					+ "	</security>"
					+ "</MSM>"
					+ "</WLANProfile>";
        } else {
        	logger.info("Security type not supported");
        	return false;
        }

		PrintWriter xml = null;
		try {
			xml = new PrintWriter(profileName+".xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		xml.println(profile);
		xml.close();

		List<String> commands = new ArrayList<String>();
		commands.add("netsh");
		commands.add("wlan");
		commands.add("add");
		commands.add("profile");
		commands.add("filename="+profileName+".xml");
		ProcessBuilder pb = new ProcessBuilder(commands);
		/*ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "add",
				"profile","filename=\""+profileName+".xml\"");*/
		pb.redirectErrorStream(true);
		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<ScanResult> getScanResults()
	{
		List<ScanResult> scanResults= new ArrayList<ScanResult>();
		ScanResult scannedNetwork = new ScanResult();
		ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "show","networks");
		pb.redirectErrorStream(true);
		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				if(line.contains("SSID")){
					//We only need SSID 

					String[] splitedLine = line.split(":");
					scannedNetwork = new ScanResult();
					scannedNetwork.SSID=splitedLine[1].replaceAll(" ", "");
					scanResults.add(scannedNetwork);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return scanResults;
	}

	private boolean disconnect()
	{
		ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "disconnect");
		pb.redirectErrorStream(true);
		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				//if(line.contains("correct")){ //[AT4] This has to be changed to native API
					return true; 
				//}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}

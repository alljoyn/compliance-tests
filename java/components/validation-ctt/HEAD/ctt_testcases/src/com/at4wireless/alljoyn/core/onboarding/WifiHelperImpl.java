/*
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package  com.at4wireless.alljoyn.core.onboarding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.w3c.dom.Text;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.onboarding.ScanResult;
import com.at4wireless.alljoyn.core.onboarding.WifiHelper;
import com.at4wireless.alljoyn.core.onboarding.WifiNetworkConfig;
import com.at4wireless.alljoyn.core.onboarding.WifiNotEnabledException;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiHelperImpl.
 */
public class WifiHelperImpl implements WifiHelper {



	/** The ssid. */
	String SSID=null;

	/** The Constant TAG. */
	private static final String TAG = "WifiHelperImpl";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);


	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#initialize()
	 */
	@Override
	public void initialize() {
		SSID=null;

	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#isWifiEnabled()
	 */
	@Override
	public boolean isWifiEnabled() {
		// TODO Auto-generated method stub

		return true;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#getCurrentSSID()
	 */
	@Override
	public String getCurrentSSID() {

		return SSID;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#waitForScanResults(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public List<ScanResult> waitForScanResults(long timeout, TimeUnit unit)
			throws InterruptedException {

		
		List<ScanResult> scanResults=null;

		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();

		while ((scanResults==null) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{

			scanResults=getScanResults();

		}
		return scanResults;
	}


	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#waitForDisconnect(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public String waitForDisconnect(long timeout, TimeUnit unit)
			throws InterruptedException {
		String disconnectedSsid=null;
		boolean disconnected = false;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();

		while ((!disconnected) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{

			disconnected=disconnect();


		}
		if(disconnected){
			logger.debug("It has been disconnected from the Wifi Network");
			disconnectedSsid=SSID;
			SSID=null;
		}else{
			logger.debug("It has not been disconnected from the Wifi Network");

		}

		return disconnectedSsid;
	}



	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#waitForConnect(java.lang.String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public String waitForConnect(String ssid, long timeout, TimeUnit unit)
			throws InterruptedException {

		boolean connected = false;
		long timeToWaitInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long startTime = System.currentTimeMillis();

		while ((!connected) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
		{

			connected=connectToProfile(ssid);


		}
		if(connected){
			logger.info("Connected to: "+ssid);
			//SSID=ssid;
			return ssid;
		}else{
			logger.info("Not connected to: "+ssid);
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#waitForNetworkAvailable(java.lang.String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean waitForNetworkAvailable(String ssid, long timeout, TimeUnit unit) throws InterruptedException
	{
		//disconnect();
		logger.info("Waiting for network "+ssid+" to be available");
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

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#connectToNetwork(com.at4wireless.alljoyn.core.onboarding.WifiNetworkConfig, boolean, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public String connectToNetwork(WifiNetworkConfig wifiNetworkConfig,
			boolean recreate, long timeout, TimeUnit unit)
					throws InterruptedException {

		//TODO Connect to open wifi
		Boolean validAuth = createWifiProfile(wifiNetworkConfig.getSsid(), wifiNetworkConfig.getSsid(),
				wifiNetworkConfig.getSecurityType(),
				wifiNetworkConfig.getPassphrase());
		
			SSID=waitForConnect(wifiNetworkConfig.getSsid(), timeout, unit);

			return SSID;
		

	}




	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.onboarding.WifiHelper#release()
	 */
	@Override
	public void release() {
		SSID=null;
		disconnect();

	}









	/**
	 * Connect to profile.
	 *
	 * @param profileName the profile name
	 * @return true, if successful
	 */
	private boolean connectToProfile(String profileName) {




		ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "connect","name=\""+profileName+"\"");
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
				if(line.contains("correct")){
					return true; 
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;


	}







	/**
	 * Creates the wifi profile to connects WIFI using netsh.
	 *
	 * @param profileName the profile name
	 * @param ssid the ssid
	 * @param securityType the security type
	 * @param passphrase the passphrase
	 * @return the boolean
	 */
	private Boolean createWifiProfile(String profileName,String ssid, String securityType,
			String passphrase) {
		
		String profile=null;

		Boolean correctSecurityType=true;
		if(securityType.startsWith("WPA2")){
			profile="<?xml version=\"1.0\"?>"
					+ "<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "	<name>"+profileName+"</name>"
					+ "		<SSIDConfig>"
					+ "			<SSID>"
					+ "				<name>"+ssid+"</name>"
					+ "			</SSID>"
					+ "		</SSIDConfig>"
					+ "<connectionType>ESS</connectionType>"
					+ "	<connectionMode>auto</connectionMode>"
					+ "	<MSM>"
					+ "		<security>"
					+ "			<authEncryption>"
					+ "				<authentication>WPA2PSK</authentication>"
					+ "				<encryption>AES</encryption>"
					+ "								<useOneX>false</useOneX>"
					+ "			</authEncryption>"
					+ "					<sharedKey>"
					+ "				<keyType>passPhrase</keyType>"
					+ "				<protected>false</protected>"
					+ "				<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "			</sharedKey>"
					+ "		</security>"
					+ "	</MSM>"
					+ "</WLANProfile>";


		}else if(securityType.startsWith("WPA")){
			profile="<?xml version=\"1.0\"?>"
					+ "		<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "			<name>"+profileName+"</name>"
					+ "				<SSIDConfig>"
					+ "					<SSID>"						
					+ "						<name>"+ssid+"</name>"
					+ "					</SSID>"
					+ "				</SSIDConfig>"
					+ "				<connectionType>ESS</connectionType>"
					+ "				<connectionMode>auto</connectionMode>"
					+ "				<MSM>"
					+ "					<security>"
					+ "						<authEncryption>"
					+ "							<authentication>WPAPSK</authentication>"
					+ "							<encryption>TKIP</encryption>"
					+ "							<useOneX>false</useOneX>"
					+ "						</authEncryption>"
					+ "						<sharedKey>"
					+ "							<keyType>passPhrase</keyType>"
					+ "							<protected>false</protected>"
					+ "							<keyMaterial>"+passphrase+"</keyMaterial>"
					+ "						</sharedKey>"
					+ "					</security>"
					+ "				</MSM>"
					+ "			</WLANProfile>";

		}else if(securityType.equalsIgnoreCase("OPEN")){	

			profile="<?xml version=\"1.0\"?>"
					+ "	<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">"
					+ "		<name>"+profileName+"</name>"
					+ "			<SSIDConfig>"
					+ "				<SSID>"
					+ "					<name>"+ssid+"</name>"
					+ "				</SSID>"
					+ "				<nonBroadcast>false</nonBroadcast>"
					+ "			</SSIDConfig>"
					+ "			<connectionType>ESS</connectionType>"
					+ "			<connectionMode>manual</connectionMode>"
					+ "			<MSM>"
					+ "				<security>"
					+ "					<authEncryption>"
					+ "									<authentication>open</authentication>"
					+ "						<encryption>none</encryption>"
					+ "						<useOneX>false</useOneX>"
					+ "					</authEncryption>"
					+ "				</security>"
					+ "			</MSM>"
					+ "	</WLANProfile>";
		}else  {
			logger.info("It isn't a supported Security Type");
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

		ProcessBuilder pb = new ProcessBuilder("netsh", "wlan", "add",
				"profile","filename=\""+profileName+".xml\"");
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



	/**
	 * Gets the scan results.
	 *
	 * @return the scan results
	 */
	private List<ScanResult> getScanResults() {

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


	/**
	 * Check for wifi enabled.
	 */
	private void checkForWifiEnabled()
	{
		if (false == isWifiEnabled())
		{
			throw new WifiNotEnabledException();
		}
	}

	/**
	 * Disconnect.
	 *
	 * @return true, if successful
	 */
	private boolean disconnect() {



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
				if(line.contains("correct")){
					return true; 
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return false;

	}





 


}
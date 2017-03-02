/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package org.alljoyn.validation.testing.utils.onboarding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alljoyn.onboarding.OnboardingService;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiHelperImpl implements WifiHelper
{
    private static final String TAG = "WifiHelperImpl";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private WifiManager wifiManager;

    public WifiHelperImpl(Context context)
    {
        this.context = context;
    }

    @Override
    public void initialize()
    {
        wifiManager = getWifiManager(context);

        wifiBroadcastReceiver = new WifiBroadcastReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiBroadcastReceiver, filter);
    }

    @Override
    public void release()
    {
        if (wifiBroadcastReceiver != null)
        {
            context.unregisterReceiver(wifiBroadcastReceiver);
            wifiBroadcastReceiver = null;
        }

    }

    protected WifiManager getWifiManager(Context context)
    {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public boolean isWifiEnabled()
    {
        return wifiManager.isWifiEnabled();
    }

    @Override
    public String getCurrentSSID()
    {
        checkForWifiEnabled();
        return wifiBroadcastReceiver.connectedSSID();
    }

    @Override
    public List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException
    {
        checkForWifiEnabled();
        return wifiBroadcastReceiver.waitForScanResults(timeout, unit);
    }

    @Override
    public String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException
    {
        checkForWifiEnabled();
        return wifiBroadcastReceiver.waitForDisconnect(timeout, unit);
    }

    @Override
    public String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException
    {
        checkForWifiEnabled();
        return wifiBroadcastReceiver.waitForConnect(ssid, timeout, unit);
    }

    @Override
    public boolean waitForNetworkAvailable(String ssid, long timeout, TimeUnit unit) throws InterruptedException
    {
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

        String ssid = wifiNetworkConfig.getSsid();
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
        return waitForConnect(ssid, timeRemaining, TimeUnit.MILLISECONDS);
    }

    private WifiConfiguration createWifiConfiguration(String ssid, String password, String securityType)
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
    }

    private boolean isValidSecurityType(String securityType)
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
    }

    private void removeWifiNetwork(Integer networkId)
    {
        if (!wifiManager.removeNetwork(networkId))
        {
            throw new WifiHelperException("WifiManager.removeNetwork returned false");
        }
    }

    private void saveWifiConfiguration()
    {
        if (!wifiManager.saveConfiguration())
        {
            throw new WifiHelperException("WifiManager.saveConfiguration returned false");
        }
    }

    private void checkForWifiEnabled()
    {
        if (false == isWifiEnabled())
        {
            throw new WifiNotEnabledException();
        }
    }

}
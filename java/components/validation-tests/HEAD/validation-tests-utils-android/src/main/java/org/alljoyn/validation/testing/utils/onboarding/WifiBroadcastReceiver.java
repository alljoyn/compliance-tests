/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.utils.onboarding;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

public class WifiBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "WifiBroadcastRecvr";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private WifiManager wifiManager = null;
    private ConnectivityManager connectivityManager = null;
    private volatile boolean waitingForScanResults = false;
    private volatile String lastSsid = null;

    private LinkedBlockingDeque<List<ScanResult>> scanResultsQueue = new LinkedBlockingDeque<List<ScanResult>>();
    private LinkedBlockingDeque<String> networkDisconnectQueue = new LinkedBlockingDeque<String>();
    private LinkedBlockingDeque<String> networkConnectQueue = new LinkedBlockingDeque<String>();

    public WifiBroadcastReceiver(Context context)
    {
        wifiManager = getWifiManager(context);
        connectivityManager = getConnectivityManager(context);
    }

    protected ConnectivityManager getConnectivityManager(Context context)
    {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    protected WifiManager getWifiManager(Context context)
    {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        logger.debug(String.format("onReceive: %s", intent.getAction()));
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
        {
            handleScanResultsAvailable(wifiManager);
        }
        else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            handleWifiStateChanged(intent);
        }
        else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            handleSupplicantStateChanged(intent);
        }
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
        {
            handleChangeInConnectivity(intent);
        }
    }

    private void handleChangeInConnectivity(Intent intent)
    {
        String currentSsid = connectedSSID();
        if (currentSsid != null)
        {
            logger.debug(String.format("Detected connection to SSID: %s", currentSsid));
            lastSsid = currentSsid;

            networkConnectQueue.add(currentSsid);
            networkDisconnectQueue.clear();
        }
    }

    public String connectedSSID()
    {
        String ssid = null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && networkInfo.isConnected())
        {
            ssid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

        }
        return ssid;
    }

    protected void handleScanResultsAvailable(WifiManager wifiManager)
    {
        if (waitingForScanResults)
        {
            scanResultsQueue.add(wifiManager.getScanResults());
        }
    }

    private void handleWifiStateChanged(Intent intent)
    {
        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        if (false == isWifiEnabled)
        {
            // TODO if Wifi gets disabled and/or enabled
        }
    }

    private void handleSupplicantStateChanged(Intent intent)
    {
        if (intent.hasExtra(WifiManager.EXTRA_NEW_STATE))
        {
            SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (supplicantState.equals(SupplicantState.DISCONNECTED))
            {
                String disconnectedSsid = lastSsid;
                if (disconnectedSsid == null)
                {
                    disconnectedSsid = "WASN'T CONNECTED";
                }
                else
                {
                    logger.debug(String.format("Detected disconnect from SSID: %s", disconnectedSsid));
                }
                networkDisconnectQueue.add(disconnectedSsid);
                networkConnectQueue.clear();
            }
        }

        if (intent.hasExtra(WifiManager.EXTRA_SUPPLICANT_ERROR))
        {
            if (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0) == WifiManager.ERROR_AUTHENTICATING)
            {
                // TODO record that the connection failed
                // this would happen if we tried to connect to a soft AP with
                // security
                // or if we failed to connect to personal AP for some reason
                // TODO remove the network configuration, since it is bad
            }
        }
    }

    public List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException
    {
        waitingForScanResults = true;
        scanResultsQueue.clear();
        wifiManager.startScan();
        logger.info("Waiting for WiFi scan results");
        List<ScanResult> scanResults = scanResultsQueue.poll(timeout, unit);
        if (scanResults != null)
        {
            logger.info(String.format("Received %d scan results", scanResults.size()));
        }
        return scanResults;
    }

    public String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info("Waiting for WiFi to disconnect");
        String ssid = networkDisconnectQueue.poll(timeout, unit);
        logger.info(String.format("WiFi disconnected from %s", ssid));
        return ssid;
    }

    public String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info(String.format("Waiting for WiFi to connect to %s", ssid));

        String currentSsid = connectedSSID();
        if (ssid.equals(currentSsid))
        {
            return currentSsid;
        }
        return networkConnectQueue.poll(timeout, unit);
    }
}
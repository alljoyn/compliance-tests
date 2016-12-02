/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.testing.utils.onboarding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.net.wifi.ScanResult;

public interface WifiHelper
{

    void initialize();

    boolean isWifiEnabled();

    String getCurrentSSID();

    List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException;

    String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException;

    String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException;

    boolean waitForNetworkAvailable(String ssid, long timeout, TimeUnit unit) throws InterruptedException;

    String connectToNetwork(WifiNetworkConfig wifiNetworkConfig, boolean recreate, long timeout, TimeUnit unit) throws InterruptedException;

    void release();

}
/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.onboarding;

import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
//import android.net.wifi.ScanResult;//Android

/**
 * The Interface WifiHelper.
 */
public interface WifiHelper
{

    /**
     * Initialize.
     */
    void initialize();

    /**
     * Checks if is wifi enabled.
     *
     * @return true, if is wifi enabled
     */
    boolean isWifiEnabled();

    /**
     * Gets the current ssid.
     *
     * @return the current ssid
     */
    String getCurrentSSID();

    /**
     * Wait for scan results.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the list
     * @throws InterruptedException the interrupted exception
     */
    List<ScanResult> waitForScanResults(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Wait for disconnect.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    String waitForDisconnect(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Wait for connect.
     *
     * @param ssid the ssid
     * @param timeout the timeout
     * @param unit the unit
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    String waitForConnect(String ssid, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Wait for network available.
     *
     * @param ssid the ssid
     * @param timeout the timeout
     * @param unit the unit
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    boolean waitForNetworkAvailable(String ssid, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Connect to network.
     *
     * @param wifiNetworkConfig the wifi network config
     * @param recreate the recreate
     * @param timeout the timeout
     * @param unit the unit
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    String connectToNetwork(WifiNetworkConfig wifiNetworkConfig, boolean recreate, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Release.
     */
    void release();

}
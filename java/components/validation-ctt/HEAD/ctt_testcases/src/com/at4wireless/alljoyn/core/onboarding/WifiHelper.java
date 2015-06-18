/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.onboarding;

import java.util.List;
import java.util.concurrent.TimeUnit;



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

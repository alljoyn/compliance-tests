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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.alljoyn.validation.testing.utils.onboarding.WifiBroadcastReceiver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WifiBroadcastReceiverTest
{

    protected static final String CURRENT_SSID = "currentSsid";
    private static final String DIFFERENT_SSID = "differentSsid";
    private Context mockContext;
    private WifiBroadcastReceiver wifiStateManager;
    private WifiManager mockWifiManager;
    private List<ScanResult> expectedScanResults = new ArrayList<ScanResult>();
    private Thread thread;
    private ExecutorService executorService;
    private ConnectivityManager mockConnectivityManager;
    private NetworkInfo networkInfo;
    private WifiInfo wifiInfo;

    @Before
    public void setup() throws Exception
    {
        executorService = Executors.newSingleThreadExecutor();

        mockContext = mock(Context.class);
        mockWifiManager = mock(WifiManager.class);
        mockConnectivityManager = mock(ConnectivityManager.class);
        wifiStateManager = new WifiBroadcastReceiver(mockContext)
        {

            @Override
            protected WifiManager getWifiManager(Context context)
            {
                return mockWifiManager;
            }

            @Override
            protected ConnectivityManager getConnectivityManager(Context context)
            {
                return mockConnectivityManager;
            }
        };
        when(mockWifiManager.getScanResults()).thenReturn(expectedScanResults);
        networkInfo = mock(NetworkInfo.class);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        when(networkInfo.isConnected()).thenReturn(true);
        wifiInfo = mock(WifiInfo.class);
        when(mockWifiManager.getConnectionInfo()).thenReturn(wifiInfo);
        when(wifiInfo.getSSID()).thenReturn(CURRENT_SSID);

        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);

    }

    @Test
    public void testWaitForScanResults() throws InterruptedException, Exception
    {
        Callable<List<ScanResult>> callable = new Callable<List<ScanResult>>()
        {
            @Override
            public List<ScanResult> call() throws Exception
            {
                return wifiStateManager.waitForScanResults(1, TimeUnit.SECONDS);
            }
        };

        Future<List<ScanResult>> results = executorService.submit(callable);

        Intent scanIntent = new Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiStateManager.onReceive(mockContext, scanIntent);

        List<ScanResult> scanResults = results.get();
        assertEquals(expectedScanResults, scanResults);
    }

    @Test
    public void testScanResultsNobodyWaiting() throws InterruptedException, Exception
    {
        Intent scanIntent = new Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiStateManager.onReceive(mockContext, scanIntent);
    }

    @Test
    public void testWaitForScanResultsTimedOut() throws InterruptedException, Exception
    {
        Callable<List<ScanResult>> callable = new Callable<List<ScanResult>>()
        {
            @Override
            public List<ScanResult> call() throws Exception
            {
                return wifiStateManager.waitForScanResults(10, TimeUnit.MILLISECONDS);
            }
        };

        Future<List<ScanResult>> results = executorService.submit(callable);

        assertNull(results.get());
    }

    @Test
    public void testWaitForScanResultsInterrupted() throws InterruptedException, Exception
    {
        Callable<List<ScanResult>> callable = new Callable<List<ScanResult>>()
        {
            @Override
            public List<ScanResult> call() throws Exception
            {
                thread = Thread.currentThread();
                return wifiStateManager.waitForScanResults(10, TimeUnit.SECONDS);
            }
        };

        Future<List<ScanResult>> results = executorService.submit(callable);

        try
        {
            results.get(20, TimeUnit.MILLISECONDS);
            fail();
        }
        catch (TimeoutException e)
        {
        }

        thread.interrupt();

        try
        {
            results.get();
        }
        catch (ExecutionException e)
        {
            assertEquals(InterruptedException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testWaitForDisconnect() throws InterruptedException, Exception
    {
        Intent networkChangeIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        networkChangeIntent = new Intent(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        networkChangeIntent.putExtra(WifiManager.EXTRA_NEW_STATE, (Parcelable) SupplicantState.DISCONNECTED);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        Callable<String> callable = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                return wifiStateManager.waitForDisconnect(1, TimeUnit.SECONDS);
            }
        };
        Future<String> results = executorService.submit(callable);

        when(networkInfo.isConnected()).thenReturn(false);

        networkChangeIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        assertEquals(CURRENT_SSID, results.get());
    }

    @Test
    public void testWaitForDisconnectTimedOut() throws InterruptedException, Exception
    {
        Intent networkChangeIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        Callable<String> callable = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                return wifiStateManager.waitForDisconnect(10, TimeUnit.MILLISECONDS);
            }
        };
        Future<String> results = executorService.submit(callable);

        assertNull(results.get());
    }

    @Test
    public void testWaitForConnectAlreadyConnected() throws InterruptedException, Exception
    {
        Intent networkChangeIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        Callable<String> callable = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                return wifiStateManager.waitForConnect(CURRENT_SSID, 1, TimeUnit.SECONDS);
            }
        };
        Future<String> results = executorService.submit(callable);

        assertEquals(CURRENT_SSID, results.get());
    }

    @Test
    public void testWaitForConnectedTImedOut() throws InterruptedException, Exception
    {
        when(networkInfo.isConnected()).thenReturn(false);

        Callable<String> callable = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                return wifiStateManager.waitForConnect(CURRENT_SSID, 10, TimeUnit.MILLISECONDS);
            }
        };
        Future<String> results = executorService.submit(callable);

        assertNull(results.get());
    }

    @Test
    public void testWaitForConnectDifferentSsid() throws InterruptedException, Exception
    {
        when(wifiInfo.getSSID()).thenReturn(DIFFERENT_SSID);

        Callable<String> callable = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                return wifiStateManager.waitForConnect(CURRENT_SSID, 1, TimeUnit.SECONDS);
            }
        };
        Future<String> results = executorService.submit(callable);

        Intent networkChangeIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateManager.onReceive(mockContext, networkChangeIntent);

        assertEquals(DIFFERENT_SSID, results.get());
    }

}
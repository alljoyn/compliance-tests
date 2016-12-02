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
package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * Contains information about the capabilities of an interface
 */
public class WlanInterfaceCapability extends Structure
{
	public static class ByReference extends WlanInterfaceCapability implements Structure.ByReference 
    {
    	
    }
	
	private static int WLAN_MAX_PHY_INDEX = 64;
	
	/**
	 * A WLAN_INTERFACE_TYPE value that indicates the type of the interface.
	 */
	public int interfaceType;
	
	/**
	 * Indicates whether 802.11d is supported by the interface. If TRUE, 802.11d is supported.
	 */
	public boolean bDot11DSupported;
	
	/**
	 * The maximum size of the SSID list supported by this interface.
	 */
	public int dwMaxDesiredSsidListSize;
	
	/**
	 * The maximum size of the basic service set (BSS) identifier list supported by this interface.
	 */
	public int dwMaxDesiredBssidListSize;
	
	/**
	 * Contains the number of supported PHY types.
	 */
	public int dwNumberOfSupportedPhys;
	
	/**
	 * An array of DOT11_PHY_TYPE values that specify the supported PHY types. WLAN_MAX_PHY_INDEX is set to 64.
	 */
	public int[] dot11PhyTypes;
	
	public WlanInterfaceCapability()
	{
		dot11PhyTypes = new int[WLAN_MAX_PHY_INDEX];
	}

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("interfaceType", "bDot11DSupported", "dwMaxDesiredSsidListSize",
				"dwMaxDesiredBssidListSize", "dwNumberOfSupportedPhys", "dot11PhyTypes");
	}
}
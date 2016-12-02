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
import com.sun.jna.platform.win32.Guid;

public class WlanInterfaceInfo extends Structure
{
    public static class ByReference extends WlanInterfaceInfoList implements Structure.ByReference 
    {
    	
    }

    /**
     * Contains the GUID of the interface.
     */
	public Guid.GUID InterfaceGuid;
	
	/**
	 * Contains the description of the interface
	 */
	public char[] strInterfaceDescription = new char[256];
	
	/**
	 * Contains a WLAN_INTERFACE_STATE value that indicates the current state of the interface.
	 * 
	 * Windows XP with SP3 and Wireless LAN API for Windows XP with SP2: Only the
	 * wlan_interface_state_connected, wlan_interface_state_disconnected, and 
	 * wlan_interface_state_authenticating values are supported.
	 */
	public int isState;
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("InterfaceGuid", "strInterfaceDescription", "isState");
	}

}

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
 * Contains the SSID of an interface
 */
public class Dot11Ssid extends Structure
{
	public static class ByReference extends Dot11Ssid implements Structure.ByReference 
    {
    	
    }
	
	public static int DOT11_SSID_MAX_LENGTH = 32;
	
	/**
	 * The length, in bytes, of the ucSSID array.
	 */
    public int uSSIDLength;
    
    /**
     * The SSID. DOT11_SSID_MAX_LENGTH is set to 32.
     */
    public byte[] ucSSID;

    public Dot11Ssid()
    {
        ucSSID = new byte[DOT11_SSID_MAX_LENGTH];
    }
    
    /*public Dot11Ssid(Pointer p)
    {
    	uSSIDLength = p.getInt(0);
    	ucSSID = p.getByteArray(4, uSSIDLength);
    }*/

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("uSSIDLength", "ucSSID");
	}
}

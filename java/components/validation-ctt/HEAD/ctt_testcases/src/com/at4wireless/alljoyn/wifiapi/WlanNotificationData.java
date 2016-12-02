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
import com.sun.jna.ptr.PointerByReference;

/**
 * Contains information provided when receiving notifications
 */
public class WlanNotificationData extends Structure
{
	public static class ByReference extends WlanNotificationData implements Structure.ByReference 
    {
    	
    }
	
	/**
	 * TODO
	 */
	public int NotificationSource;
	
	/**
	 * TODO
	 */
	public int NotificationCode;
	
	/**
	 * TODO
	 */
	public Guid.GUID InterfaceGuid;
	
	/**
	 * TODO
	 */
	public int dwDataSize;
	
	/**
	 * TODO
	 */
	public PointerByReference pData;

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("NotificationSource", "NotificationCode", "InterfaceGuid", "dwDataSize", "pData");
	}
}
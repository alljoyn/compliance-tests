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

public class PWlanInterfaceCapability extends Structure
{
    public static class ByReference extends PWlanInterfaceCapability implements Structure.ByReference 
    {
    	
    }
	
    public WlanInterfaceCapability.ByReference WlanInterfaceCapability;
    //public WlanInterfaceCapability WlanInterfaceCapability;
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("WlanInterfaceCapability");
	}

};
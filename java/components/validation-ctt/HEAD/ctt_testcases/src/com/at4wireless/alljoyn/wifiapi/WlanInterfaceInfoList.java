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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class WlanInterfaceInfoList extends Structure
{
    public static class ByReference extends WlanInterfaceInfoList implements Structure.ByReference 
    {
    	public ByReference()
    	{
    		
    	}
    	
    	public ByReference(Pointer p)
    	{
    		super(p);
    	}
    }
    
    /**
     * Contains the number of items in the InterfaceInfo member
     */
	public int dwNumberOfItems;
	
	/**
	 * The index of the current item. The index of the first item is 0. dwIndex must be less than
	 * dwNumberOfItems.
	 * 
	 * This member is not used by the wireless service. Applications can use this member when processing
	 * individual interfaces in the WLAN_INTERFACE_INFO_LIST structure. When an application passes this
	 * structure from one function to another, it can set the value of dwIndex to the index of the item currently
	 * being processed. This can help an application maintain state.
	 * 
	 * dwIndex should always be initialized before use.
	 */
	public int dwIndex;
	
	/**
	 * An array of WLAN_INTERFACE_INFO structures containing interface information.
	 */
	// public Pointer[] InterfaceInfo;
	public WlanInterfaceInfo[] InterfaceInfo;

    public WlanInterfaceInfoList()
    {
        InterfaceInfo = new WlanInterfaceInfo[1];
    }
    
    public WlanInterfaceInfoList(Pointer p)
    {
    	super(p);
    	//dwNumberOfItems = p.getInt(0);
    	//dwIndex = p.getInt(4);
    	
    	if (p.getInt(0) > 0)
    	{
    		InterfaceInfo = new WlanInterfaceInfo[p.getInt(0)];
    	}
    	else
    	{
    		InterfaceInfo = new WlanInterfaceInfo[1];
    	}
    	read();
    }
    
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("dwNumberOfItems", "dwIndex", "InterfaceInfo");
	}

};

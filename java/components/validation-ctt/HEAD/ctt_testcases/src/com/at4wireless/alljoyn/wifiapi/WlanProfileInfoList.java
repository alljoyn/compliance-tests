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

public class WlanProfileInfoList extends Structure
{
	public static class ByReference extends WlanProfileInfoList implements Structure.ByReference
	{
		public ByReference()
		{
			
		}
		
		public ByReference(Pointer p)
		{
			super(p);
		}
	}
	
	public int dwNumberOfItems;
	
	public int dwIndex;
	
	public WlanProfileInfo[] ProfileInfo;
	
	public WlanProfileInfoList()
	{
		ProfileInfo = new WlanProfileInfo[10];
	}
	
	public WlanProfileInfoList(Pointer p)
	{
		super(p);
		/*dwNumberOfItems = p.getInt(0);
		dwIndex = p.getInt(4);*/
		if(p.getInt(0) != 0)
		{
			ProfileInfo = new WlanProfileInfo[p.getInt(0)];
		}
		else
		{
			ProfileInfo = new WlanProfileInfo[1];
		}
		
		/*for (int i = 0; i < dwNumberOfItems; i++)
		{
			ProfileInfo[i].strProfileName = p.getCharArray(8+516*i, 256);
			ProfileInfo[i].dwFlags = p.getInt(520+516*i);
		}*/
		read();
	}

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("dwNumberOfItems", "dwIndex", "ProfileInfo");
	}
}
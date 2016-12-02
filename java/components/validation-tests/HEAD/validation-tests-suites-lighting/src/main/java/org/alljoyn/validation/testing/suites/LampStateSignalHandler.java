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
package org.alljoyn.validation.testing.suites;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.bus.BusObject;

import android.util.*;


public class LampStateSignalHandler
{
	//Load AllJoyn Library
	static
	{
		System.loadLibrary("alljoyn_java");
	}
	
	/* Interface variables */
	private LSF_LampTestSuite updateListener;
	
	public LampStateSignalHandler()
	{
		//Empty Constructor
	}
	
	public void setUpdateListener(LSF_LampTestSuite listener)
	{
		updateListener = listener;
	}

	@BusSignalHandler(iface="org.allseen.LSF.LampState", signal="LampStateChanged")
	public void handleLampStateChanged(String LampID)
	{
		Log.d("LampStateSignalHandler", "LampStateChanged for LampID: " + LampID);
		
		if (updateListener != null)
		{
			updateListener.handleLampStateChanged(LampID);
		}
	}
}
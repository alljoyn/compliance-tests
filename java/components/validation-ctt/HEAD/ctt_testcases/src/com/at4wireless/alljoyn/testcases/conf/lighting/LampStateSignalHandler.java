/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.conf.lighting;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.bus.BusObject;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;




// TODO: Auto-generated Javadoc
/**
 * The Class LampStateSignalHandler.
 */
public class LampStateSignalHandler
{
	
	/** The Constant TAG. */
	protected static final String TAG = "LampStateSignalHandler";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The signal received. */
	Boolean signalReceived=false;
	//Load AllJoyn Library
	static
	{
		System.loadLibrary("alljoyn_java");
	}
	
	/* Interface variables */
	/** The update listener. */
	private LightingService updateListener;
	
	/**
	 * Instantiates a new lamp state signal handler.
	 */
	public LampStateSignalHandler()
	{
		//Empty Constructor
	}
	
	
	/**
	 * Handle lamp state changed.
	 *
	 * @param LampID the lamp id
	 */
	@BusSignalHandler(iface="org.allseen.LSF.LampState", signal="LampStateChanged")
	public void handleLampStateChanged(String LampID)
	{
		logger.debug( "LampStateChanged for LampID: " + LampID);
		
		
			LampStateChanged(LampID);
		
	}
	
	
	/**
	 * Lamp state changed.
	 *
	 * @param LampID the lamp id
	 */
	public void LampStateChanged(String LampID)
	 {
		logger.info("LSF_Lamp signal LampStateChanged for " + LampID);

		signalReceived = true;
	 }
	
	
	
	/**
	 * Checks if is signal received.
	 *
	 * @return the boolean
	 */
	public Boolean isSignalReceived(){
		
		return signalReceived;
	}
	
	
	
}
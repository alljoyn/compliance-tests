/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

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
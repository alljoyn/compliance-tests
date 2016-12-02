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
package com.at4wireless.alljoyn.core.notification;


import org.alljoyn.ns.commons.GenericLogger;
import org.alljoyn.ns.commons.NativePlatform;

import com.at4wireless.alljoyn.core.commons.GenericLoggerImp;

// TODO: Auto-generated Javadoc
/**
 * The Class NativePlatformWindows.
 */
public class NativePlatformWindows implements  NativePlatform {

	/**
	 * Reference to logger
	 */
	protected GenericLogger logger; 
	
	/**
	 * Constructor
	 */
	public NativePlatformWindows() {
		createLogger();
	}

	/**
	 * Creates and set logger object
	 */
	protected  void createLogger() {
		logger=new GenericLoggerImp(); 
	}
	
	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#getNativeLogger()
	 */
	@Override
	public GenericLogger getNativeLogger() {
		return logger;
	}

	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#setNativeLogger(org.alljoyn.ns.commons.GenericLogger)
	 */
	@Override
	public void setNativeLogger(GenericLogger logger) {
		this.logger = logger;
	}
}
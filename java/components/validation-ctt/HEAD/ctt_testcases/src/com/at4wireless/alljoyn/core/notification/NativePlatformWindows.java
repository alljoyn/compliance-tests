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
package com.at4wireless.alljoyn.core.notification;

import org.alljoyn.ns.commons.GenericLogger;
import org.alljoyn.ns.commons.NativePlatform;

import com.at4wireless.alljoyn.core.commons.GenericLoggerImp;

public class NativePlatformWindows implements  NativePlatform
{
	protected GenericLogger logger; 
	
	public NativePlatformWindows() {
		createLogger();
	}

	protected  void createLogger() {
		logger = new GenericLoggerImp(); 
	}
	
	@Override
	public GenericLogger getNativeLogger() {
		return logger;
	}

	@Override
	public void setNativeLogger(GenericLogger logger) {
		this.logger = logger;
	}
}
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
package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.concurrent.CountDownLatch;

import org.alljoyn.bus.Variant;
import org.alljoyn.smarthome.centralizedmanagement.client.ReturnValueSignalHandler;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class ReturnValueHandler extends ReturnValueSignalHandler
{
	private static final Logger logger = new WindowsLoggerImpl(ReturnValueHandler.class.getSimpleName());
	CountDownLatch countDownLatch;
	
	Boolean pass=true;
	
	public ReturnValueHandler(CountDownLatch countDownLatch)
	{
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void ReturnValue(String methodName, String status, Variant value)
	{
		logger.debug("New owner: "  );
		countDownLatch.countDown();
	}
}
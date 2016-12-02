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
package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.concurrent.CountDownLatch;

import org.alljoyn.bus.Variant;
import org.alljoyn.smarthome.centralizedmanagement.client.ReturnValueSignalHandler;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class ReturnValueHandler.
 */
public class ReturnValueHandler extends ReturnValueSignalHandler{
	
	/** The count down latch. */
	CountDownLatch countDownLatch;
	
	/** The pass. */
	Boolean pass=true;
	
	/** The Constant TAG. */
	private static final String TAG = "ReturnValueSignalHandler";
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TAG);

	
	
	
	
	/**
	 * Instantiates a new return value handler.
	 *
	 * @param countDownLatch the count down latch
	 */
	public ReturnValueHandler(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
		}

	/* (non-Javadoc)
	 * @see org.alljoyn.smarthome.centralizedmanagement.client.ReturnValueSignalHandler#ReturnValue(java.lang.String, java.lang.String, org.alljoyn.bus.Variant)
	 */
	@Override
	public void ReturnValue(String methodName, String status, Variant value) {

		logger.debug("New owner: "  );
		
		countDownLatch.countDown();
		
	}

}
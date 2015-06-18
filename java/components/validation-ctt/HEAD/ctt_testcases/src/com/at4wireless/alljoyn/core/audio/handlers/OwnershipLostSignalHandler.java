/*
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.audio.handlers;



import java.util.concurrent.CountDownLatch;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import com.at4wireless.alljoyn.core.audio.AudioTransports.Configuration;
import com.at4wireless.alljoyn.core.audio.AudioTransports.Port;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;




// TODO: Auto-generated Javadoc
/**
 * The Class OwnershipLostSignalHandler.
 */
public class OwnershipLostSignalHandler implements Port, BusObject
{
	
	/** The pass. */
	Boolean pass=true;
	
	/** The Constant TAG. */
	private static final String TAG = "OwnershipLostHandler";
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TAG);
	
	/** The count down latch. */
	private CountDownLatch countDownLatch;
	
	/** The expected new owner. */
	private String expectedNewOwner;

	/**
	 * Instantiates a new ownership lost signal handler.
	 *
	 * @param countDownLatch the count down latch
	 */
	public OwnershipLostSignalHandler(CountDownLatch countDownLatch)
	{
		this.countDownLatch = countDownLatch;
	}

	/**
	 * Handle ownership lost signal.
	 *
	 * @param newOwner the new owner
	 */
	@BusSignalHandler(iface = "org.alljoyn.Stream.Port", signal = "OwnershipLost")
	public void handleOwnershipLostSignal(String newOwner)
	{
		logger.debug("New owner: " + newOwner);
		assertEquals("New owner name does not match", expectedNewOwner, newOwner);
		countDownLatch.countDown();
	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param expectedNewOwner the expected new owner
	 * @param newOwner the new owner
	 */
	private void assertEquals(String errorMsg, String expectedNewOwner,
			String newOwner) {
		if(!expectedNewOwner.equals(newOwner)){
			logger.error(errorMsg);
			pass=false;
		}

	}

	/**
	 * Sets the expected new owner.
	 *
	 * @param expectedNewOwner the new expected new owner
	 */
	public void setExpectedNewOwner(String expectedNewOwner)
	{
		this.expectedNewOwner = expectedNewOwner;
	}

	/**
	 * Gets the count down latch.
	 *
	 * @return the count down latch
	 */
	public CountDownLatch getCountDownLatch()
	{
		return countDownLatch;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Port#getVersion()
	 */
	@Override
	public short getVersion() throws BusException
	{
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Port#getDirection()
	 */
	@Override
	public byte getDirection() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Port#getCapabilities()
	 */
	@Override
	public Configuration[] getCapabilities() throws BusException
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Port#OwnershipLost(java.lang.String)
	 */
	@Override
	public void OwnershipLost(String newOwner) throws BusException
	{
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Port#Connect(java.lang.String, java.lang.String, com.at4wireless.alljoyn.core.audio.AudioTransports.Configuration)
	 */
	@Override
	public void Connect(String host, String objectPath, Configuration configuration)
	{
	}
}
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lighting;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * The Interface LampServiceBusInterface.
 */
@BusInterface(name="org.allseen.LSF.LampService")
public interface LampServiceBusInterface
{
	
	/**
	 * The Class Values.
	 */
	public class Values
	{
		
		/** The Lamp response code. */
		@Position(0)
		public int LampResponseCode;

		/** The Lamp fault code. */
		@Position(1)
		public int LampFaultCode;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	/**
	 * Gets the lamp service version.
	 *
	 * @return the lamp service version
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getLampServiceVersion() throws BusException;

	/**
	 * Clear lamp fault.
	 *
	 * @param LampFaultCode the lamp fault code
	 * @return the values
	 * @throws BusException the bus exception
	 */
	@BusMethod(signature="u", replySignature="uu")
	public Values ClearLampFault(int LampFaultCode) throws BusException;

	/**
	 * Gets the lamp faults.
	 *
	 * @return the lamp faults
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="au")
	public int[] getLampFaults() throws BusException;
}
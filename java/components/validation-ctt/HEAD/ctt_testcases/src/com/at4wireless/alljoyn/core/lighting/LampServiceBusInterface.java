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
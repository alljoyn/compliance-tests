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
package com.at4wireless.alljoyn.core.lighting;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name="org.allseen.LSF.LampService")
public interface LampServiceBusInterface
{
	public class Values
	{
		@Position(0)
		public int LampResponseCode;

		@Position(1)
		public int LampFaultCode;
	}

	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	@BusProperty(signature="u")
	public int getLampServiceVersion() throws BusException;

	@BusMethod(signature="u", replySignature="uu")
	public Values ClearLampFault(int LampFaultCode) throws BusException;

	@BusProperty(signature="au")
	public int[] getLampFaults() throws BusException;
}
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
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class LampParametersBusObject.
 */
public class LampParametersBusObject implements BusObject, LampParametersBusInterface
{

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampParametersBusInterface#getVersion()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getVersion() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampParametersBusInterface#getEnergy_Usage_Milliwatts()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getEnergy_Usage_Milliwatts() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampParametersBusInterface#getBrightness_Lumens()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getBrightness_Lumens() throws BusException
	{
		return 0;
	}

}
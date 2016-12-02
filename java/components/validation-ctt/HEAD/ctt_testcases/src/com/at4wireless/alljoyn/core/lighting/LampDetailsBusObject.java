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
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;

public class LampDetailsBusObject implements BusObject, LampDetailsBusInterface
{
	@Override
	@BusProperty(signature = "u")
	public int getVersion() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMake() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getModel() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getType() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getLampType() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getLampBaseType() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getLampBeamAngle() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "b")
	public boolean getDimmable() throws BusException
	{
		return false;
	}

	@Override
	@BusProperty(signature = "b")
	public boolean getColor() throws BusException
	{
		return false;
	}

	@Override
	@BusProperty(signature = "b")
	public boolean getVariableColorTemp() throws BusException
	{
		return false;
	}

	@Override
	@BusProperty(signature = "b")
	public boolean getHasEffects() throws BusException
	{
		return false;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMinVoltage() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMaxVoltage() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getWattage() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getIncandescentEquivalent() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMaxLumens() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMinTemperature() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getMaxTemperature() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public int getColorRenderingIndex() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "s")
	public String getLampID() throws BusException
	{
		return null;
	}
}
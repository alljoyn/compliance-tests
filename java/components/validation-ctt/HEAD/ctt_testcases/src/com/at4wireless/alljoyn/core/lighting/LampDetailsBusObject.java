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
 * The Class LampDetailsBusObject.
 */
public class LampDetailsBusObject implements BusObject, LampDetailsBusInterface
{
	
	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getVersion()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getVersion() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMake()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMake() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getModel()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getModel() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getType()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getType() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getLampType()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getLampType() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getLampBaseType()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getLampBaseType() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getLampBeamAngle()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getLampBeamAngle() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getDimmable()
	 */
	@Override
	@BusProperty(signature = "b")
	public boolean getDimmable() throws BusException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getColor()
	 */
	@Override
	@BusProperty(signature = "b")
	public boolean getColor() throws BusException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getVariableColorTemp()
	 */
	@Override
	@BusProperty(signature = "b")
	public boolean getVariableColorTemp() throws BusException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getHasEffects()
	 */
	@Override
	@BusProperty(signature = "b")
	public boolean getHasEffects() throws BusException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMinVoltage()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMinVoltage() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMaxVoltage()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMaxVoltage() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getWattage()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getWattage() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getIncandescentEquivalent()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getIncandescentEquivalent() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMaxLumens()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMaxLumens() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMinTemperature()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMinTemperature() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getMaxTemperature()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getMaxTemperature() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getColorRenderingIndex()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getColorRenderingIndex() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface#getLampID()
	 */
	@Override
	@BusProperty(signature = "s")
	public String getLampID() throws BusException
	{
		return null;
	}

}
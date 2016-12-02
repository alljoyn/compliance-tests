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

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class LampStateBusObject.
 */
public class LampStateBusObject implements BusObject, LampStateBusInterface
{
	
	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getVersion()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getVersion() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#TransitionLampState(long, java.util.Map, int)
	 */
	@Override
	@BusMethod(signature = "ta{sv}u", replySignature = "u")
	public int TransitionLampState(long Timestamp, Map<String, Variant> NewState, int TransitionPeriod) throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#ApplyPulseEffect(java.util.Map, java.util.Map, int, int, int, long)
	 */
	@Override
	@BusMethod(signature="a{sv}a{sv}uuut", replySignature="u")
	public int ApplyPulseEffect(Map<String, Variant> FromState, Map<String, Variant> ToState, int period, int duration, int numPulses,
			long startTimeStamp) throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#LampStateChanged(java.lang.String)
	 */
	@Override
	@BusSignal(signature = "s")
	public void LampStateChanged(String LampID) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#setOnOff(boolean)
	 */
	@Override
	@BusProperty
	public void setOnOff(boolean onOff) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getOnOff()
	 */
	@Override
	@BusProperty
	public boolean getOnOff() throws BusException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#setHue(int)
	 */
	@Override
	@BusProperty(signature = "u")
	public void setHue(int hue) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getHue()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getHue() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#setSaturation(int)
	 */
	@Override
	@BusProperty(signature = "u")
	public void setSaturation(int saturation) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getSaturation()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getSaturation() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#setColorTemp(int)
	 */
	@Override
	@BusProperty(signature = "u")
	public void setColorTemp(int colorTemp) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getColorTemp()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getColorTemp() throws BusException
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#setBrightness(int)
	 */
	@Override
	@BusProperty(signature = "u")
	public void setBrightness(int brightness) throws BusException
	{
		// Intentionally left blank
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.lighting.LampStateBusInterface#getBrightness()
	 */
	@Override
	@BusProperty(signature = "u")
	public int getBrightness() throws BusException
	{
		return 0;
	}
}
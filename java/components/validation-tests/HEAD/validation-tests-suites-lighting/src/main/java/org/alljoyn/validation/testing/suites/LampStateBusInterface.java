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
package org.alljoyn.validation.testing.suites;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name="org.allseen.LSF.LampState")
public interface LampStateBusInterface
{
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	@BusMethod(signature="ta{sv}u", replySignature="u")
	public int TransitionLampState(long Timestamp, Map<String, Variant> NewState, int TransitionPeriod) throws BusException;

	@BusMethod(signature="a{sv}a{sv}uuut", replySignature="u")
	public int ApplyPulseEffect(Map<String, Variant> FromState, Map<String, Variant> ToState, int period, int duration, int numPulses,
			long startTimeStamp) throws BusException;

	@BusSignal
	public void LampStateChanged(String LampID) throws BusException;

	@BusProperty
	public void setOnOff(boolean onOff) throws BusException;

	@BusProperty
	public boolean getOnOff() throws BusException;

	@BusProperty(signature="u")
	public void setHue(int hue) throws BusException;

	@BusProperty(signature="u")
	public int getHue() throws BusException;

	@BusProperty(signature="u")
	public void setSaturation(int saturation) throws BusException;

	@BusProperty(signature="u")
	public int getSaturation() throws BusException;

	@BusProperty(signature="u")
	public void setColorTemp(int colorTemp) throws BusException;

	@BusProperty(signature="u")
	public int getColorTemp() throws BusException;

	@BusProperty(signature="u")
	public void setBrightness(int brightness) throws BusException;

	@BusProperty(signature="u")
	public int getBrightness() throws BusException;
}
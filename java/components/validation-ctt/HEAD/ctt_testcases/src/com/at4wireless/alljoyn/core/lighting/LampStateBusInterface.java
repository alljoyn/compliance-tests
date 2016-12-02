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
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Interface LampStateBusInterface.
 */
@BusInterface(name="org.allseen.LSF.LampState")
public interface LampStateBusInterface
{
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	/**
	 * Transition lamp state.
	 *
	 * @param Timestamp the timestamp
	 * @param NewState the new state
	 * @param TransitionPeriod the transition period
	 * @return the int
	 * @throws BusException the bus exception
	 */
	@BusMethod(signature="ta{sv}u", replySignature="u")
	public int TransitionLampState(long Timestamp, Map<String, Variant> NewState, int TransitionPeriod) throws BusException;

	/**
	 * Apply pulse effect.
	 *
	 * @param FromState the from state
	 * @param ToState the to state
	 * @param period the period
	 * @param duration the duration
	 * @param numPulses the num pulses
	 * @param startTimeStamp the start time stamp
	 * @return the int
	 * @throws BusException the bus exception
	 */
	@BusMethod(signature="a{sv}a{sv}uuut", replySignature="u")
	public int ApplyPulseEffect(Map<String, Variant> FromState, Map<String, Variant> ToState, int period, int duration, int numPulses,
			long startTimeStamp) throws BusException;

	/**
	 * Lamp state changed.
	 *
	 * @param LampID the lamp id
	 * @throws BusException the bus exception
	 */
	@BusSignal
	public void LampStateChanged(String LampID) throws BusException;

	/**
	 * Sets the on off.
	 *
	 * @param onOff the new on off
	 * @throws BusException the bus exception
	 */
	@BusProperty
	public void setOnOff(boolean onOff) throws BusException;

	/**
	 * Gets the on off.
	 *
	 * @return the on off
	 * @throws BusException the bus exception
	 */
	@BusProperty
	public boolean getOnOff() throws BusException;

	/**
	 * Sets the hue.
	 *
	 * @param hue the new hue
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public void setHue(int hue) throws BusException;

	/**
	 * Gets the hue.
	 *
	 * @return the hue
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getHue() throws BusException;

	/**
	 * Sets the saturation.
	 *
	 * @param saturation the new saturation
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public void setSaturation(int saturation) throws BusException;

	/**
	 * Gets the saturation.
	 *
	 * @return the saturation
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getSaturation() throws BusException;

	/**
	 * Sets the color temp.
	 *
	 * @param colorTemp the new color temp
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public void setColorTemp(int colorTemp) throws BusException;

	/**
	 * Gets the color temp.
	 *
	 * @return the color temp
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getColorTemp() throws BusException;

	/**
	 * Sets the brightness.
	 *
	 * @param brightness the new brightness
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public void setBrightness(int brightness) throws BusException;

	/**
	 * Gets the brightness.
	 *
	 * @return the brightness
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getBrightness() throws BusException;
}
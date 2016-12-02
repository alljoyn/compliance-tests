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
import org.alljoyn.bus.annotation.BusProperty;

// TODO: Auto-generated Javadoc
/**
 * The Interface LampDetailsBusInterface.
 */
@BusInterface(name="org.allseen.LSF.LampDetails")
public interface LampDetailsBusInterface
{
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	@BusProperty(signature="u")
	public int getMake() throws BusException;

	@BusProperty(signature="u")
	public int getModel() throws BusException;

	@BusProperty(signature="u")
	public int getType() throws BusException;

	@BusProperty(signature="u")
	public int getLampType() throws BusException;

	@BusProperty(signature="u")
	public int getLampBaseType() throws BusException;

	@BusProperty(signature="u")
	public int getLampBeamAngle() throws BusException;

	@BusProperty(signature="b")
	public boolean getDimmable() throws BusException;

	@BusProperty(signature="b")
	public boolean getColor() throws BusException;

	@BusProperty(signature="b")
	public boolean getVariableColorTemp() throws BusException;

	@BusProperty(signature="b")
	public boolean getHasEffects() throws BusException;

	@BusProperty(signature="u")
	public int getMinVoltage() throws BusException;

	@BusProperty(signature="u")
	public int getMaxVoltage() throws BusException;

	@BusProperty(signature="u")
	public int getWattage() throws BusException;

	@BusProperty(signature="u")
	public int getIncandescentEquivalent() throws BusException;

	@BusProperty(signature="u")
	public int getMaxLumens() throws BusException;

	@BusProperty(signature="u")
	public int getMinTemperature() throws BusException;

	@BusProperty(signature="u")
	public int getMaxTemperature() throws BusException;

	@BusProperty(signature="u")
	public int getColorRenderingIndex() throws BusException;

	@BusProperty(signature="s")
	public String getLampID() throws BusException;
}
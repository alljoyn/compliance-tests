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
import org.alljoyn.bus.annotation.BusProperty;

// TODO: Auto-generated Javadoc
/**
 * The Interface LampDetailsBusInterface.
 */
@BusInterface(name="org.allseen.LSF.LampDetails")
public interface LampDetailsBusInterface
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
	 * Gets the make.
	 *
	 * @return the make
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMake() throws BusException;

	/**
	 * Gets the model.
	 *
	 * @return the model
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getModel() throws BusException;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getType() throws BusException;

	/**
	 * Gets the lamp type.
	 *
	 * @return the lamp type
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getLampType() throws BusException;

	/**
	 * Gets the lamp base type.
	 *
	 * @return the lamp base type
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getLampBaseType() throws BusException;

	/**
	 * Gets the lamp beam angle.
	 *
	 * @return the lamp beam angle
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getLampBeamAngle() throws BusException;

	/**
	 * Gets the dimmable.
	 *
	 * @return the dimmable
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="b")
	public boolean getDimmable() throws BusException;

	/**
	 * Gets the color.
	 *
	 * @return the color
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="b")
	public boolean getColor() throws BusException;

	/**
	 * Gets the variable color temp.
	 *
	 * @return the variable color temp
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="b")
	public boolean getVariableColorTemp() throws BusException;

	/**
	 * Gets the checks for effects.
	 *
	 * @return the checks for effects
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="b")
	public boolean getHasEffects() throws BusException;

	/**
	 * Gets the min voltage.
	 *
	 * @return the min voltage
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMinVoltage() throws BusException;

	/**
	 * Gets the max voltage.
	 *
	 * @return the max voltage
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMaxVoltage() throws BusException;

	/**
	 * Gets the wattage.
	 *
	 * @return the wattage
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getWattage() throws BusException;

	/**
	 * Gets the incandescent equivalent.
	 *
	 * @return the incandescent equivalent
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getIncandescentEquivalent() throws BusException;

	/**
	 * Gets the max lumens.
	 *
	 * @return the max lumens
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMaxLumens() throws BusException;

	/**
	 * Gets the min temperature.
	 *
	 * @return the min temperature
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMinTemperature() throws BusException;

	/**
	 * Gets the max temperature.
	 *
	 * @return the max temperature
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getMaxTemperature() throws BusException;

	/**
	 * Gets the color rendering index.
	 *
	 * @return the color rendering index
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getColorRenderingIndex() throws BusException;

	/**
	 * Gets the lamp id.
	 *
	 * @return the lamp id
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="s")
	public String getLampID() throws BusException;
}
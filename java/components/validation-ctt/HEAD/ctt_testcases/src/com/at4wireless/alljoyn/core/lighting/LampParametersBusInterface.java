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

@BusInterface(name="org.allseen.LSF.LampParameters")
public interface LampParametersBusInterface
{
	@BusProperty(signature="u")
	public int getVersion() throws BusException;
	
	@BusProperty(signature="u")
	public int getEnergy_Usage_Milliwatts() throws BusException;
	
	@BusProperty(signature="u")
	public int getBrightness_Lumens() throws BusException;
}
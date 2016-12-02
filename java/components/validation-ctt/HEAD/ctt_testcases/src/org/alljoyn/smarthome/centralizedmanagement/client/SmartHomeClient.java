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
package org.alljoyn.smarthome.centralizedmanagement.client;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "org.alljoyn.SmartHome.CentralizedManagement")
public interface SmartHomeClient
{
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	@BusMethod(signature = "sssa(sov)")
	public void ApplianceRegistration(String wellKnownName, String uniqueName, String deviceId); 

	@BusMethod(signature = "s")
	public void ApplianceUnRegistration(String deviceId);
	
	@BusMethod(signature = "bsosv")
	public void Execute(boolean isReturn, String deviceId, String objectPath, String methodName, Variant arguments);

	@BusMethod(signature = "s")
	public void DeviceHeartBeat(String deviceId);
	
	@BusSignal(signature = "ssv")
	public void ReturnValue(String methodName, String status, Variant value);
}
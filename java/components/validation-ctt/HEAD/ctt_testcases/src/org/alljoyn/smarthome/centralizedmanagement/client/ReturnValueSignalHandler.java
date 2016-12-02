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

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignalHandler;

public abstract class ReturnValueSignalHandler
{
	@BusSignalHandler(iface = "org.alljoyn.SmartHome.CentralizedManagement", signal = "ReturnValue")
	public abstract void ReturnValue(String methodName, String status, Variant value);
}
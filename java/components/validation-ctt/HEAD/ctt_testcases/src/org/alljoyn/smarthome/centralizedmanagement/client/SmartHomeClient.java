/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package org.alljoyn.smarthome.centralizedmanagement.client;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The interface used to interact with AllJoyn SmartHome gateway and implement some specific functions.
 */

@BusInterface(name = "org.alljoyn.SmartHome.CentralizedManagement")
public interface SmartHomeClient {
	
	
	
	
	
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 * @throws BusException the bus exception
	 */
	@BusProperty(signature="u")
	public int getVersion() throws BusException;
	
	/**
	 * Use the method to submit the registration information of appliance to the AllJoyn SmartHome gateway
	 * @param wellKnownName	The well-known name of appliance
	 * @param uniqueName	The unique name of appliance
	 * @param deviceId		The device id of appliance
	 */
	
	@BusMethod(signature = "sssa(sov)")
	public void ApplianceRegistration(String wellKnownName, String uniqueName, String deviceId); 

	/**
	 * Use the method to unregister the appliance
	 * @param deviceId	The device id of appliance
	 */
	
	@BusMethod(signature = "s")
	public void ApplianceUnRegistration(String deviceId);
	
	/**
	 * Use the method to implement method-call through the AllJoyn SmartHome gateway
	 * @param isReturn		If the method has a return value, the isReturn will be true
	 * @param deviceId		Device ID
	 * @param objectPath	The bus object path
	 * @param methodName	Method Name
	 * @param arguments		The arguments of method
	 */
	
	@BusMethod(signature = "bsosv")
	public void Execute(boolean isReturn, String deviceId, String objectPath, String methodName, Variant arguments);

	/**
	 * Use the method to implement heartbeat mechanism
	 * @param deviceId		Device ID
	 */
	
	@BusMethod(signature = "s")
	public void DeviceHeartBeat(String deviceId);
	
	/**
	 * Use the method to obtain the return value
	 * @param methodName	Method Name
	 * @param status		The status of method call
	 * @param value			Return Value
	 */
	
	@BusSignal(signature = "ssv")
	public void ReturnValue(String methodName, String status, Variant value);
	
}
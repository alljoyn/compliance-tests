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

import org.alljoyn.bus.annotation.Position;

public class ArgsDesc
{
	@Position(0)
	public String methodName;

	@Position(1)
	public String inputValueType;

	@Position(2)
	public String returnValueType;

	@Position(3)
	public String argsName;
}
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
import org.alljoyn.bus.annotation.Position;

public class ObjectInfo
{
	@Position(0)
	public String interfaceName;

	@Position(1)
	public String objectPath;

	@Position(2)
	public Variant argsDescs;	
}
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

package org.alljoyn.smarthome.centralizedmanagement.client;

import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 *	This class used to describe the arguments of method.
 */

public class ArgsDesc {
	
	// Method Name
	/** The method name. */
	@Position(0)
	public String methodName;
	
	// The data type of input parameter
	/** The input value type. */
	@Position(1)
	public String inputValueType;
	
	// The data type of output parameter
	/** The return value type. */
	@Position(2)
	public String returnValueType;
	
	// The name of input parameter and output parameter
	/** The args name. */
	@Position(3)
	public String argsName;

}
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

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * 	This class used to describe the information of bus object.
 */

public class ObjectInfo {
	
	// Interface Name
	/** The interface name. */
	@Position(0)
	public String interfaceName;

	// Bus Object Path
	/** The object path. */
	@Position(1)
	public String objectPath;
	
	// The descriptions of arguments
	/** The args descs. */
	@Position(2)
	public Variant argsDescs;
	
}
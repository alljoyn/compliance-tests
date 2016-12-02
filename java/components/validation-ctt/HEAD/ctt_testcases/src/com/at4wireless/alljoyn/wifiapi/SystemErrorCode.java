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
package com.at4wireless.alljoyn.wifiapi;

public enum SystemErrorCode 
{
	ERROR_SUCCESS(0),
	ERROR_NOT_ENOUGH_MEMORY(8),
	ERROR_INVALID_PARAMETER(87),
	ERROR_REMOTE_SESSION_LIMIT_EXCEEDED(1220);
	
	private int value;
	
	private SystemErrorCode(int value)
	{
		this.value = value;
	}
}
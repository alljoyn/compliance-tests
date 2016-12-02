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
package com.at4wireless.alljoyn.localagent.controller.exception;

@SuppressWarnings("serial")
public class SendLogException extends Exception
{
	public SendLogException()
	{
		super();
	}
	
	public SendLogException(String message)
	{
		super(message);
	}
	
	public SendLogException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public SendLogException(Throwable cause)
	{
		super(cause);
	}
}
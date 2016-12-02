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
package com.at4wireless.alljoyn.localagent.model;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class LogModel
{
	public LogModel()
	{
		
	}
	
	public HttpResponse getLogContentFromServer(String authenticatedUser, String sessionToken, int projectId, String logFileName)
			throws URISyntaxException, IOException
	{
		System.out.println(ConfigParameter.LOG_CONTENT_URL + ModelCommons.encodePathVariable(authenticatedUser) + "/" + projectId + "/" + logFileName);
		return ModelCommons.securedGetRequest(ConfigParameter.LOG_CONTENT_URL + ModelCommons.encodePathVariable(authenticatedUser) + "/" + projectId + "/" + logFileName, sessionToken);
	}
}
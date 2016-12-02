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
package com.at4wireless.alljoyn.localagent.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.model.LogModel;
import com.at4wireless.alljoyn.localagent.model.common.FileEncryption;

public class LogController
{
	private static final Logger logger = LogManager.getLogger(LogController.class.getName());
	private LogModel logModel;
	
	public LogController()
	{
		logModel = new LogModel();
	}
	
	public String getLogContentFromServer(String authenticatedUser, String sessionToken, int projectId, String logFileNameWithExtension, SecretKey cipherKey) 
			throws URISyntaxException, IOException, GeneralSecurityException
	{
		HttpResponse encodedLogFromServer = logModel.getLogContentFromServer(authenticatedUser, sessionToken, projectId, logFileNameWithExtension);
		FileEncryption fE = new FileEncryption();
		
		logger.debug(String.format("Response status code: %s", encodedLogFromServer.getStatusLine().getStatusCode()));
		
		fE.setAesSecretKey(cipherKey);
		
		return fE.decrypt(ControllerCommons.HttpResponseToString(encodedLogFromServer));
	}
}
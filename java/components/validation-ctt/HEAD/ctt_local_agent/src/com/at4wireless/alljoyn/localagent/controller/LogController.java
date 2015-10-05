/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
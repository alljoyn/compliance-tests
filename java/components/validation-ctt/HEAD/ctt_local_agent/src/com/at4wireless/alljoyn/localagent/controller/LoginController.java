/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.model.LoginModel;
import com.at4wireless.alljoyn.localagent.model.common.FileEncryption;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

public class LoginController
{
	private static final Logger logger = LogManager.getLogger(LoginController.class.getName());
	
	private LoginModel loginModel;
	private String authenticatedUser;
	private String sessionToken;
	private SecretKey cipherKey;
	
	public LoginController()
	{
		loginModel = new LoginModel();
	}
	
	public boolean loginWithWebServer(String userToAuthenticate, String passwordUsedInAuthentication) throws ClientHandlerException, JSONException
	{
		ClientResponse loginResponse = loginModel.getTokenFromWebServer(userToAuthenticate, passwordUsedInAuthentication);
		String getTokenFromServerResponse = loginResponse.getEntity(String.class);
		logger.debug(String.format("OAuth login response: %s", getTokenFromServerResponse));
		
		if (loginResponse.getStatus() == HttpStatus.SC_OK)
		{
			JSONObject receivedJson = new JSONObject(getTokenFromServerResponse);
			loginResponse = loginModel.refreshToken(receivedJson.optString("refresh_token"));
			
			if (loginResponse.getStatus() == HttpStatus.SC_OK)
			{
				String refreshTokenResponse = loginResponse.getEntity(String.class);
				logger.debug(String.format("OAuth refresh response: %s", refreshTokenResponse));
				JSONObject refreshJson = new JSONObject(refreshTokenResponse);

				this.authenticatedUser = userToAuthenticate;
				this.sessionToken = refreshJson.optString("access_token");
				
				return true;
			}
			else
			{
				logger.warn(String.format("Response code: %s", loginResponse.getStatus()));
				return false;
			}
		}
		else
		{
			logger.warn(String.format("Response code: %s", loginResponse.getStatus()));
			return false;
		}
	}
	
	public void exchangeEncryption() throws GeneralSecurityException, URISyntaxException, IOException
	{
		FileEncryption fEncryption = new FileEncryption();
		fEncryption.makeKeys();

		X509EncodedKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(fEncryption.getRsaPublicKey(),X509EncodedKeySpec.class);
		
		String str = DatatypeConverter.printBase64Binary(spec.getEncoded());
		
		HttpResponse response = loginModel.exchangeCipherKeys(authenticatedUser, sessionToken, str);
		
		cipherKey = fEncryption.decryptAESwithRSA(ControllerCommons.HttpResponseToString(response));
	}
	
	public List<Object> isTestToolUpdated(String testToolLocalVersion) throws URISyntaxException, IOException
	{
		HttpResponse responseFromServer = loginModel.isTestToolUpdated(testToolLocalVersion, sessionToken);
		List<Object> list = new ArrayList<Object>();
		String responseString = ControllerCommons.HttpResponseToString(responseFromServer);
			
		if (responseString.contains("new version available"))
		{
			list.add(false);
			list.add(responseString.split(":")[1].substring(1));
		}
		else if (responseString.equals("version up to date"))
		{
			list.add(true);
		}
		
		return list;
	}
	
	public String getAuthenticatedUser()
	{
		return authenticatedUser;
	}
	
	public String getSessionToken()
	{
		return sessionToken;
	}
	
	public SecretKey getCipherKey()
	{
		return cipherKey;
	}
}
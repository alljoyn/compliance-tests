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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;

public class LoginModel
{	
	public LoginModel()
	{
		
	}
	
	public ClientResponse getTokenFromWebServer(String userToAuthenticate, String passwordUsedInAuthentication)
	{
		Form form = new Form();
		form.add("username", userToAuthenticate);
		form.add("password", ModelCommons.hashString(passwordUsedInAuthentication, "SHA-256"));
		form.add("client_id", "restapp");
		form.add("client_secret", "restapp");
		form.add("grant_type", "password");
		
		return ModelCommons.unsecuredPostRequest(ConfigParameter.AUTHENTICATION_URL, form);
	}
	
	public ClientResponse refreshToken(String tokenToBeRefreshed)
	{
		Form refreshForm = new Form();
		refreshForm.add("refresh_token", tokenToBeRefreshed);
		refreshForm.add("client_id", "restapp");
		refreshForm.add("client_secret", "restapp");
		refreshForm.add("grant_type", "refresh_token");
		
		return ModelCommons.unsecuredPostRequest(ConfigParameter.AUTHENTICATION_URL, refreshForm);
	}
	
	public HttpResponse exchangeCipherKeys(String authenticatedUser, String sessionToken, String localRsaPublicKey)
			throws URISyntaxException, IOException
	{
		HttpEntity requestBody = MultipartEntityBuilder.create()
				.addTextBody("user", authenticatedUser)
				.addTextBody("publicKey", localRsaPublicKey)
				.build();
		return ModelCommons.securedPostRequest(ConfigParameter.KEY_EXCHANGE_URL, sessionToken, requestBody);
	}
	
	public HttpResponse isTestToolUpdated(String testToolLocalVersion, String sessionToken) throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.TEST_TOOL_VERSION_URL
				+ testToolLocalVersion.replace(".", "_").substring(1), sessionToken);
	}
}
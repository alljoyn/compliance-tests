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
package com.at4wireless.allseen.localagent.model;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.at4wireless.allseen.localagent.model.common.ConfigParameter;
import com.at4wireless.allseen.localagent.model.common.ModelCommons;
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
	
	public HttpResponse exchangeCipherKeys(String authenticatedUser, String sessionToken, String localRsaPublicKey) throws URISyntaxException, IOException
	{
		HttpEntity requestBody = MultipartEntityBuilder.create()
				.addTextBody("user", authenticatedUser)
				.addTextBody("publicKey", localRsaPublicKey)
				.build();
		return ModelCommons.securedPostRequest(ConfigParameter.KEY_EXCHANGE_URL, sessionToken, requestBody);
	}
	
	public HttpResponse isTestToolUpdated(String testToolLocalVersion, String sessionToken) throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.TEST_TOOL_VERSION_URL + testToolLocalVersion.replace(".", "_").substring(1), sessionToken);
	}
}

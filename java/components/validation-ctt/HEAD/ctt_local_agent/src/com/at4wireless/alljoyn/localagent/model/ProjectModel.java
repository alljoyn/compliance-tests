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
package com.at4wireless.alljoyn.localagent.model;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class ProjectModel
{
	public ProjectModel()
	{
		
	}
	
	public HttpResponse getProjectsListFromServer(String authenticatedUser, String sessionToken) throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.PROJECTS_LIST_URL + ModelCommons.encodePathVariable(authenticatedUser), sessionToken);
	}
	
	public HttpResponse checkIfReleaseExists(String sessionToken, String certificationRelease) throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.CHECK_IF_RELEASE_EXISTS_URL + certificationRelease.replaceAll("\\.",  "_"), sessionToken);
	}
	
	public HttpResponse isTestCasesPackageVersionUpdated(String sessionToken, String certificationRelease, String packageVersion)
			throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.CHECK_IF_VERSION_IS_UPDATED_URL + certificationRelease.replaceAll("\\.",  "_")
				+ "_" + packageVersion, sessionToken);
	}
}
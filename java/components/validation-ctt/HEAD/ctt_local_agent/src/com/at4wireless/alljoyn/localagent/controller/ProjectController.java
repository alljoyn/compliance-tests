/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.model.ProjectModel;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class ProjectController
{
	private static final Logger logger = LogManager.getLogger(ProjectController.class.getName());
	private ProjectModel projectModel;
	
	public ProjectController()
	{
		projectModel = new ProjectModel();
	}
	
	public Object[][] getProjectsListFromServer(String authenticatedUser, String sessionToken) throws IOException, URISyntaxException, JSONException
	{
		HttpResponse projectsListResponse = projectModel.getProjectsListFromServer(authenticatedUser, sessionToken);
		String projects = ControllerCommons.HttpResponseToString(projectsListResponse);
		Object[][] data = null;

		try
		{
			JSONArray json = new JSONArray(projects);
			data = new Object[json.length()][10];
			
			for (int i = 0; i < json.length(); i++)
			{
				data[i][0] = json.getJSONObject(i).getInt("idProject");
				data[i][1] = json.getJSONObject(i).get("name");
				data[i][2] = json.getJSONObject(i).getString("type");
				data[i][3] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(json.getJSONObject(i).getLong("createdDate")));
				data[i][4] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(json.getJSONObject(i).getLong("modifiedDate")));
				data[i][5] = json.getJSONObject(i).getString("certRel");
				data[i][6] = json.getJSONObject(i).get("dut");
				data[i][7] = json.getJSONObject(i).get("golden");
				data[i][8] = json.getJSONObject(i).getBoolean("isConfigured") ? "Yes" : "No";
				data[i][9] = json.getJSONObject(i).getBoolean("hasResults");
			}
			logger.debug(String.format("Data structure length: %d", data.length));
		}
		catch (JSONException e)
		{
			JSONObject json = new JSONObject(projects);
			data = new Object[1][10];
			
			data[0][0] = json.getInt("idProject");
			data[0][1] = json.get("name");
			data[0][2] = json.getString("type");
			data[0][3] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(json.getLong("createdDate")));
			data[0][4] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(json.getLong("modifiedDate")));
			data[0][5] = json.getString("certRel");
			data[0][6] = json.get("dut");
			data[0][7] = json.get("golden");
			data[0][8] = json.getBoolean("isConfigured");
			data[0][9] = json.getBoolean("hasResults");
			
			logger.debug(String.format("Data structure length: %d", data.length));
		}

		return data;
	}
	
	public boolean isTestCasesPackageInstalled(String certificationRelease)
	{
		return (ModelCommons.getTestCasesPackageFiles(certificationRelease).length > 0);
	}
	
	public boolean isDebugAndReleaseExists(String sessionToken, String certificationRelease) throws URISyntaxException, IOException
	{
		File[] testCasesPackageFiles = ModelCommons.getTestCasesPackageFiles(certificationRelease);
		
		if (testCasesPackageFiles.length == 0)
		{
			return false;
		}
		else
		{
			for (File f : testCasesPackageFiles)
			{
				if (f.getName().contains("_R"))
				{
					return false;
				}
			}
			
			HttpResponse isDebugAndReleaseExistsResponse = projectModel.checkIfReleaseExists(sessionToken, certificationRelease);
			
			logger.debug(String.format("Response code: %s", isDebugAndReleaseExistsResponse.getStatusLine().getStatusCode()));

			return ControllerCommons.HttpResponseToString(isDebugAndReleaseExistsResponse).equals("true");
		}
	}
	
	public List<Object> isTestCasesPackageVersionUpdated(String sessionToken, String certificationRelease) throws URISyntaxException, IOException
	{		
		String newestPackageVersion = ControllerCommons.getNewestPackageVersion(certificationRelease);
		HttpResponse isVersionUpdatedResponse = projectModel.isTestCasesPackageVersionUpdated(sessionToken, certificationRelease, newestPackageVersion);
		List<Object> serverResponse = new ArrayList<Object>();	
		String isVersionUpdatedString = ControllerCommons.HttpResponseToString(isVersionUpdatedResponse);

		if (isVersionUpdatedString.contains("true"))
		{
			serverResponse.add(true);
			serverResponse.add(newestPackageVersion);
		}
		else
		{
			serverResponse.add(false);
			serverResponse.add(isVersionUpdatedString.split(", ")[1]);
		}
		
		return serverResponse;
	}
}
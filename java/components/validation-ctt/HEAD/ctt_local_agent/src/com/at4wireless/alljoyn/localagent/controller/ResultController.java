/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.model.ResultModel;

public class ResultController
{
	private static final Logger logger = LogManager.getLogger(ResultController.class.getName());
	
	private ResultModel resultModel;
	
	public ResultController()
	{
		resultModel = new ResultModel();
	}
	
	public Object[][] getResultsListFromServer(String authenticatedUser, String sessionToken, int projectId) throws URISyntaxException,
			IOException, JSONException
	{
		HttpResponse projectsListResponse = resultModel.getResultsListFromServer(authenticatedUser, sessionToken, projectId);
		String results = ControllerCommons.HttpResponseToString(projectsListResponse);
		Object[][] data = new Object[1][1];
		JSONObject jsonObject = new JSONObject(results);

		try
		{
			JSONArray json = jsonObject.getJSONObject("Results").getJSONArray("TestCase");
			
			data = new Object[json.length()][6];
			
			for (int i = 0; i < json.length(); i++)
			{
				data[i][0] = json.getJSONObject(i).getString("Name");
				data[i][1] = json.getJSONObject(i).getString("Description");
				data[i][2] = json.getJSONObject(i).getString("DateTime");
				data[i][3] = json.getJSONObject(i).getString("Version");
				data[i][4] = json.getJSONObject(i).getString("Verdict");
				data[i][5] = json.getJSONObject(i).getString("LogFile").split("\\.")[0];
			}
		}
		catch (JSONException e)
		{
			JSONObject json = jsonObject.getJSONObject("Results").getJSONObject("TestCase");
			
			data = new Object[1][6];
			
				data[0][0] = json.getString("Name");
				data[0][1] = json.getString("Description");
				data[0][2] = json.getString("DateTime");
				data[0][3] = json.getString("Version");
				data[0][4] = json.getString("Verdict");
				data[0][5] = json.getString("LogFile").split("\\.")[0];
		}
		
		logger.debug(String.format("Data structure length: %d", data.length));
		
		return data;
	}
}
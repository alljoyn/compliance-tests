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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.model.UpdateTestToolModel;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class UpdateTestToolController
{
	private static final Logger logger = LogManager.getLogger(UpdateTestToolController.class.getName());
	private UpdateTestToolModel updateTestToolModel;
	
	public UpdateTestToolController()
	{
		updateTestToolModel = new UpdateTestToolModel();
	}
	
	public void downloadUpdate(String sessionToken) throws URISyntaxException, IOException
	{
		HttpResponse testToolUpdateResponse = updateTestToolModel.getUpdate(sessionToken);
		String downloadPath = (new File("")).getAbsolutePath() + File.separator + ModelCommons.getConfigValue("DownloadPath");
		String fileName = testToolUpdateResponse.getFirstHeader("Content-disposition").getValue().split("=")[1];	
		BufferedHttpEntity bufferedUpdate = new BufferedHttpEntity(testToolUpdateResponse.getEntity());
		FileOutputStream updateLocation = null;
		
		logger.debug(String.format("Download path: %s. File saved: %s", downloadPath, fileName));
		
		try
		{
			updateLocation = new FileOutputStream(downloadPath + fileName);
		}
		catch (FileNotFoundException e)
		{
			logger.warn(String.format("Directory %s does not exist. Creating...", downloadPath));
			
			File directoryToBeCreated = new File(downloadPath);
			if (directoryToBeCreated.mkdirs())
			{	
				updateLocation = new FileOutputStream(downloadPath + fileName);
			}
		}
		
		bufferedUpdate.writeTo(updateLocation);
	}
}
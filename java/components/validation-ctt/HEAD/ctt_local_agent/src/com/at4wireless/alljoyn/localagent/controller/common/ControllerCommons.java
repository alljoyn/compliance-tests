/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class ControllerCommons
{
	private static final Logger logger = LogManager.getLogger(ControllerCommons.class.getName());
	private static final String toolPath = (new File("")).getAbsolutePath();
	private static final String configurationFilePath = toolPath + File.separator + "cfg.xml";
	
	public static String getNewestPackageVersion(String certificationRelease)
	{
		File[] testCasesPackageFiles = ModelCommons.getTestCasesPackageFiles(certificationRelease);
		String newestPackageVersion = "D0";
		
		for (File f : testCasesPackageFiles)
		{
			String fileName = f.getName();
			String currentPackageVersion = fileName.split("\\.")[2].split("_")[1];
			
			if (((currentPackageVersion.charAt(0) == 'R') && (newestPackageVersion.charAt(0) =='D'))
					|| ((currentPackageVersion.charAt(0) == newestPackageVersion.charAt(0)) && (Integer.parseInt(currentPackageVersion.substring(1)) > Integer.parseInt(newestPackageVersion.substring(1)))))
			{
				newestPackageVersion = currentPackageVersion;
			}
		}
		
		logger.debug(String.format("Newest %s package version: %s", certificationRelease, newestPackageVersion));
		
		return newestPackageVersion;
	}
	
	public static String HttpResponseToString(HttpResponse response) throws UnsupportedEncodingException, IllegalStateException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		
		for (String line = null; (line = reader.readLine()) != null;)
		{
		    builder.append(line);
		}
		
		return builder.toString();
	}
	
	public static void clean()
	{
		logger.debug(String.format("Cleaning file: %s", configurationFilePath));
		ModelCommons.deleteFile(configurationFilePath);
	}
}
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
package com.at4wireless.alljoyn.localagent.model.common;

import java.io.File;

public class ConfigParameter
{
	private static final String serverUrl = ModelCommons.getConfigValue("TestToolWebAppUrl");
	
	public static final String AUTHENTICATION_URL = serverUrl + ModelCommons.getConfigValue("AuthenticateUrl");
	public static final String CHECK_IF_RELEASE_EXISTS_URL = serverUrl + ModelCommons.getConfigValue("ExistsRelease");
	public static final String CHECK_IF_VERSION_IS_UPDATED_URL = serverUrl + ModelCommons.getConfigValue("isLastTechnologyVersionUrl");
	public static final String KEY_EXCHANGE_URL = serverUrl + ModelCommons.getConfigValue("keyExchange");
	public static final String LOG_CONTENT_URL = serverUrl + ModelCommons.getConfigValue("GetLog");
	public static final String PROJECTS_LIST_URL = serverUrl + ModelCommons.getConfigValue("GetListOfProjectsUrl");
	public static final String RESULTS_LIST_URL = serverUrl + ModelCommons.getConfigValue("GetResults");
	public static final String SEND_LOG_URL = serverUrl + ModelCommons.getConfigValue("UploadLogFile");
	public static final String SEND_RESULTS_XML_URL = serverUrl + ModelCommons.getConfigValue("SendResultsUrl");
	public static final String TEST_CASES_LIST_URL = serverUrl + ModelCommons.getConfigValue("GetTestCases");
	public static final String TEST_TOOL_VERSION_URL = serverUrl + ModelCommons.getConfigValue("isLastVersion");
	public static final String UPDATE_TEST_CASES_URL = serverUrl + ModelCommons.getConfigValue("GetTechnology");
	public static final String UPDATE_TEST_TOOL_URL = serverUrl + ModelCommons.getConfigValue("getLastVersion");
	
	public static final String DETAILS_FILE = "cfg.xml";
	public static final String RESULTS_FILE = "Results.xml";
	
	public static final String LOG_LEVEL = ModelCommons.getConfigValue("LogLevel");
	
	public static final String RESOURCES_PATH = "res" + File.separator + "drawable";
}
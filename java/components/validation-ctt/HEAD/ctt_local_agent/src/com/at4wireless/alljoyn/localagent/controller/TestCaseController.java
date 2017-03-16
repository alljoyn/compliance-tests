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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.controller.exception.SendLogException;
import com.at4wireless.alljoyn.localagent.controller.exception.SendResultsException;
import com.at4wireless.alljoyn.localagent.controller.reader.LogStreamReader;
import com.at4wireless.alljoyn.localagent.model.TestCaseModel;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class TestCaseController
{
	private static final Logger logger = LogManager.getLogger(TestCaseController.class.getName());
	private static final String toolPath = (new File("")).getAbsolutePath();
	private static final String libraryPath = toolPath + File.separator + "lib";
	private static final String testCasesPath = toolPath + File.separator + ModelCommons.getConfigValue("TestCasesPackagePath");
	
	private TestCaseModel testCaseModel;
	private Process testCaseProcess;
	
	public TestCaseController()
	{
		testCaseModel = new TestCaseModel();
	}
	
	public Object[][] getTestCasesListFromServer(String authenticatedUser, String sessionToken, int projectId) 
			throws URISyntaxException, IOException, JSONException
	{
		HttpResponse projectsListResponse = testCaseModel.getTestCasesListFromServer(authenticatedUser, sessionToken, projectId);
		String testCases = ControllerCommons.HttpResponseToString(projectsListResponse);
		Object[][] data;
		
		JSONObject jsonObject = new JSONObject(testCases);
		
		try
		{
			JSONArray testcasesJsonArray = jsonObject.getJSONObject("Project").getJSONArray("TestCase");
			
			data = new Object[testcasesJsonArray.length()][4];
			
			for (int i = 0; i < testcasesJsonArray.length(); i++)
			{
				data[i][0] = testcasesJsonArray.getJSONObject(i).getString("Name");
				data[i][1] = testcasesJsonArray.getJSONObject(i).getString("Description");
				data[i][2] = testcasesJsonArray.getJSONObject(i).getString("LastVerdict");
				data[i][3] = testcasesJsonArray.getJSONObject(i).getString("LastExec");
			}
		}
		catch (JSONException e)
		{
			JSONObject testcasesJsonObject = jsonObject.getJSONObject("Project").getJSONObject("TestCase");
			
			data = new Object[1][4];
			
			data[0][0] = testcasesJsonObject.getString("Name");
			data[0][1] = testcasesJsonObject.getString("Description");
			data[0][2] = testcasesJsonObject.getString("LastVerdict");
			data[0][3] = testcasesJsonObject.getString("LastExec");
		}
		
		logger.debug(String.format("Data structure length: %d", data.length));
		
		testCaseModel.saveTestCaseParametersToFile(testCases);

		return data;
	}
	
	public void runTestCase(String certificationRelease, String testCaseName, String language) throws IOException
	{   
		ProcessBuilder processBuilder = null;
		String packageVersion = ControllerCommons.getNewestPackageVersion(certificationRelease);
		
		File cTestCases = new File(testCasesPath + File.separator + "TestCases_Package_" + certificationRelease + "_" + packageVersion + ".exe");
		
		if (cTestCases.exists() && language.equals("c++"))
		{
			processBuilder = new ProcessBuilder(testCasesPath + File.separator + "TestCases_Package_" + certificationRelease
					+ "_" + packageVersion + ".exe", testCaseName, toolPath + File.separator + ConfigParameter.DETAILS_FILE);
		}
		else
		{
			processBuilder = new ProcessBuilder("java",
					"-Djava.library.path=" + libraryPath + File.separator + certificationRelease,
					"-Dfile.encoding=UTF-8", "-jar", 
					testCasesPath + File.separator + "TestCases_Package_" + certificationRelease + "_" + packageVersion +".jar", 
					testCaseName, toolPath + File.separator + ConfigParameter.DETAILS_FILE);
			processBuilder.directory(new File(toolPath));
		}
		
		testCaseProcess = processBuilder.start();

		LogStreamReader lsr = new LogStreamReader(testCaseProcess.getInputStream());
		LogStreamReader lsr2 = new LogStreamReader(testCaseProcess.getErrorStream());
		Thread executionThread = new Thread(lsr, "executionThread");
		Thread errorThread = new Thread(lsr2, "errorThread");
		executionThread.start();
		errorThread.start();
	}
	
	public void destroyTestCaseProcess()
	{
		logger.debug("Destroying test case process...");
		testCaseProcess.destroy();
	}
	
	public List<String> getResultVerdictAndDatetime(String testCaseName) throws SAXException, IOException, ParserConfigurationException
	{
		Document resultsXmlDocument = testCaseModel.getResultsXml();
		List<String> resultVerdictAndDatetime = new ArrayList<String>();
		
		if (resultsXmlDocument != null)
		{
			NodeList resultsNodeList = resultsXmlDocument.getElementsByTagName("TestCase");
			
			for (int i = resultsNodeList.getLength() - 1; i >= 0; i--)
			{
				Node node = resultsNodeList.item(i);
				Element element = (Element) node;
				
				if (ModelCommons.getValue("Name", element).equals(testCaseName))
				{
					resultVerdictAndDatetime.add(ModelCommons.getValue("Verdict", element));
					resultVerdictAndDatetime.add(ModelCommons.getValue("DateTime", element));
					
					return resultVerdictAndDatetime;
				}
			}
		}
		
		return resultVerdictAndDatetime;
	}
	
	public void sendResultsToServer(String authenticatedUser,  String sessionToken, SecretKey cipherKey, int projectId, 
			String testCaseName, String resultLogToBeSent) throws URISyntaxException, IOException, GeneralSecurityException, 
			SAXException, ParserConfigurationException, SendResultsException, SendLogException
	{
		String encryptedLogContentToBeSent = testCaseModel.encryptLogContentToBeSent(cipherKey, resultLogToBeSent);
		
		testCaseModel.sendResultsToServer(authenticatedUser, sessionToken, projectId,
				testCaseName, encryptedLogContentToBeSent);
	}
		
	private String getResultLogName(String testCaseName) throws SAXException, IOException, ParserConfigurationException
	{
		Document resultsXmlDocument = testCaseModel.getResultsXml();
		
		if (resultsXmlDocument != null)
		{
			NodeList resultsNodeList = resultsXmlDocument.getElementsByTagName("TestCase");
			
			for (int i = resultsNodeList.getLength() - 1; i >= 0; i--)
			{
				Node node = resultsNodeList.item(i);
				Element element = (Element) node;
				
				if(ModelCommons.getValue("Name", element).equals(testCaseName))
				{	
					return ModelCommons.getValue("LogFile", element);
				}
			}
		}
		
		return null;
	}
	
	public Object[][] getSamplesFromFile() throws SAXException, IOException, ParserConfigurationException
	{
		Document projectDetailsXmlDocument = testCaseModel.getDetailsFile();
		Object[][] data = null;
		
		if (projectDetailsXmlDocument != null)
		{
			NodeList resultsNodeList = projectDetailsXmlDocument.getElementsByTagName("Sample");
			data = new Object[resultsNodeList.getLength()][4];
			
			for (int i = 0; i < resultsNodeList.getLength(); i++)
			{
				Node node = resultsNodeList.item(i);
				Element element = (Element) node;
				
				data[i][0] = ModelCommons.getValue("DeviceId", element);
				data[i][1] = ModelCommons.getValue("AppId", element);
				data[i][2] = ModelCommons.getValue("swVer", element);
				
				try
				{
					data[i][3] = ModelCommons.getValue("hwVer", element);
				}
				catch (NullPointerException e)
				{
					logger.warn("No Hardware Version available for this sample");
				}
			}
		}

		return data;
	}
	
	public void setIxitWithSelectedSample(String deviceId, String appId, String swVer, String hwVer) throws SAXException, IOException, 
			ParserConfigurationException, TransformerException
	{
		testCaseModel.setIxitWithSelectedSample(deviceId, appId, swVer, hwVer);
	}
	
	public void saveEncryptedLogFile(SecretKey cipherKey, int projectId, String testCaseName, String resultLogToBeSaved) throws GeneralSecurityException, IOException, SAXException, ParserConfigurationException
	{
		String encryptedLogToBeSaved = testCaseModel.encryptLogContentToBeSent(cipherKey, resultLogToBeSaved);
		String logName = getResultLogName(testCaseName);
		
		testCaseModel.saveEncryptedLogFile(projectId, logName, encryptedLogToBeSaved);
	}
}

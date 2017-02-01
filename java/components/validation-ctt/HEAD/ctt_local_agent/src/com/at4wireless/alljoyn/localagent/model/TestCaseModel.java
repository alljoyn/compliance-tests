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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.FileEncryption;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class TestCaseModel
{	
	public TestCaseModel()
	{
		
	}
	
	public HttpResponse getTestCasesListFromServer(String authenticatedUser, String sessionToken, int projectId)
			throws URISyntaxException, IOException
	{
		return ModelCommons.securedGetRequest(ConfigParameter.TEST_CASES_LIST_URL + ModelCommons.encodePathVariable(authenticatedUser) + "/" + projectId, sessionToken);
	}
	
	public void saveTestCaseParametersToFile(String serverResponse)
	{
		Writer writer = null;
		
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File("").getAbsolutePath() + File.separator + ConfigParameter.DETAILS_FILE), "UTF-8"));
			writer.write(XML.toString(new JSONObject(serverResponse)));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Document getResultsXml() throws SAXException, IOException, ParserConfigurationException
	{
		Document resultsXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(ConfigParameter.RESULTS_FILE));
		
		resultsXmlDocument.getDocumentElement().normalize();
		
		return resultsXmlDocument;
	}
	
	public void sendResultsToServer(String authenticatedUser, String sessionToken, int projectId, 
			String testCaseName, String encryptedLog) throws SAXException, IOException, ParserConfigurationException, URISyntaxException
	{
		Document resultsXmlDocument = getResultsXml();
		int response = HttpStatus.SC_NOT_FOUND;
		String logName = "";
		
		if (resultsXmlDocument != null)
		{
			NodeList resultsNodeList = resultsXmlDocument.getElementsByTagName("TestCase");
			
			if (resultsNodeList.getLength() > 0)
			{
				for (int i = resultsNodeList.getLength() - 1; i >= 0; i--)
				{
					Node node = resultsNodeList.item(i);
					Element element = (Element) node;
					
					if (ModelCommons.getValue("Name", element).equals(testCaseName))
					{	
						logName = ModelCommons.getValue("LogFile", element);
						
						String calculatedHash = ModelCommons.hashString(encryptedLog, "MD5");
						HttpEntity httpEntity = MultipartEntityBuilder.create()
								.addTextBody("file", encryptedLog)
								.addTextBody("hash", calculatedHash)
								.addTextBody("id-test", ModelCommons.getValue("Id", element))
								.addTextBody("name", ModelCommons.getValue("Name", element))
								.addTextBody("description", ModelCommons.getValue("Description", element))
								.addTextBody("date-time", ModelCommons.getValue("DateTime", element))
								.addTextBody("verdict", ModelCommons.getValue("Verdict", element))
								.addTextBody("version", ModelCommons.getValue("Version", element))
								.addTextBody("log-name", logName)
								.build();
						
						response = ModelCommons.securedPostRequest(ConfigParameter.SEND_LOG_URL
								+ ModelCommons.encodePathVariable(authenticatedUser)
								+ "/" + projectId, sessionToken, httpEntity)
								.getStatusLine().getStatusCode();
					}
				}
			}
		}
		
		if (response == HttpStatus.SC_OK)
		{
			ModelCommons.deleteFile(ConfigParameter.RESULTS_FILE);
		}
		else
		{
			//ModelCommons.saveFile("", logName, encryptedLog);
		}
	}
	
	public List<String> getLogNamesFromFile() throws SAXException, IOException, ParserConfigurationException
	{
		List<String> logFileList = new ArrayList<String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document doc = dbFactory.newDocumentBuilder().parse(new File(ConfigParameter.RESULTS_FILE));
		Element element;
		
		for (int i = 0; i < doc.getElementsByTagName("Results").getLength(); i++)
		{
			element = (Element) doc.getElementsByTagName("Results").item(i);
			logFileList.add(ModelCommons.getValue("LogFile", element));
		}
		
		return logFileList;
	}
	
	public String encryptLogContentToBeSent(SecretKey cipherKey, String logToBeSent) throws GeneralSecurityException, IOException
	{
		FileEncryption fE;
		
		fE = new FileEncryption();
		fE.setAesSecretKey(cipherKey);

		return fE.encrypt(logToBeSent.getBytes());
	}
	
	public Document getDetailsFile() throws SAXException, IOException, ParserConfigurationException
	{
		Document projectDetailsXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(ConfigParameter.DETAILS_FILE));
		
		projectDetailsXmlDocument.getDocumentElement().normalize();
		
		return projectDetailsXmlDocument;
	}
	
	public void setIxitWithSelectedSample(String deviceId, String appId, String swVer, String hwVer) throws SAXException, IOException,
			ParserConfigurationException, TransformerException
	{
		Document projectDetailsXmlDocument = getDetailsFile();
		NodeList ixit = projectDetailsXmlDocument.getElementsByTagName("Ixit");

		for (int i = 0; i < ixit.getLength(); i++)
		{
			Node node = ixit.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;

				String ixitName = ModelCommons.getValue("Name", element);

				if (ixitName.equals("IXITCO_DeviceId"))
				{
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(deviceId);
				} else if (ixitName.equals("IXITCO_SoftwareVersion"))
				{
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(swVer);
				} else if (ixitName.equals("IXITCO_HardwareVersion"))
				{
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(hwVer);
				} else if (ixitName.equals("IXITCO_AppId"))
				{
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(appId);
				}
			}
		}

		Source source = new DOMSource(projectDetailsXmlDocument);
		Result result = new StreamResult(new File(new File("").getAbsolutePath() + File.separator + ConfigParameter.DETAILS_FILE));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		transformer.transform(source, result);
	}
	
	public void saveEncryptedLogFile(int projectId, String logName, String encryptedLogToBeSaved)
	{
		ModelCommons.saveFile("log" + File.separator + projectId, logName, encryptedLogToBeSaved);
	}
}
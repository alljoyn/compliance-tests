/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.testcases.conf.about.AboutTestSuite;
import com.at4wireless.alljoyn.testcases.conf.audio.AudioTestSuite;
import com.at4wireless.alljoyn.testcases.conf.configuration.ConfigurationTestSuite;
import com.at4wireless.alljoyn.testcases.conf.controlpanel.ControlPanelTestSuite;
import com.at4wireless.alljoyn.testcases.conf.gateway.GWAgentTestSuite;
import com.at4wireless.alljoyn.testcases.conf.lighting.LSF_LampTestSuite;
import com.at4wireless.alljoyn.testcases.conf.lightingcontroller.LSF_ControllerTestSuite;
import com.at4wireless.alljoyn.testcases.conf.notification.NotificationTestSuite;
import com.at4wireless.alljoyn.testcases.conf.onboarding.OnboardingTestSuite;
import com.at4wireless.alljoyn.testcases.conf.smarthome.SmartHomeTestSuite;
import com.at4wireless.alljoyn.testcases.conf.time.TimeTestSuite;
import com.at4wireless.alljoyn.testcases.iop.about.AboutIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.audio.AudioIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.config.ConfigIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.controlpanel.ControlPanelIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.lighting.LightingIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.notification.NotificationIOPTestSuite;
import com.at4wireless.alljoyn.testcases.iop.onboarding.OnboardingIOPTestSuite;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

/**
 * This Class handles the execution of a certain Test Case, loading from XML
 * all the parameters that are needed. 
 * It is also in charge of the creation of the XML with the results of the execution
 */
public class Manager extends Thread
{
	static
	{
		System.loadLibrary("alljoyn_java");
	}
	
	private static final String CERTIFICATION_RELEASE = "16.04.00";
	private static final Logger logger = new WindowsLoggerImpl(Manager.class.getSimpleName());
	private static final String XML_NAME = "Results.xml";
	
	private String idKey;
	private String testName;
	private String descriptionKey;
	private String verdictKey;

	private Document configurationXmlDocument;
	
	private Ics icsList;
	private Ixit ixitList;
	private GeneralParameter gpList;
	private Map<String, List<String>> goldenUnits;

	/**
	 * Main method, included here to be able to make a Runnable package
	 * when the JAR file is exported
	 * 
	 * @param arg
	 * 		Input arguments. Two arguments needed:
	 * 			- arg[0]	:	Test Case name
	 * 			- arg[1]	:	location of the configuration.xml file
	 */
	public static void main(final String arg[])
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Manager manager = new Manager(arg[0], arg[1]); 
					manager.run();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Class constructor.
	 * 
	 * @param testName
	 * 			Name of the Test Case to be executed. If the Test Case does not
	 * 			exist, the execution will finish with an INCONCLUSE and the message
	 * 			"Test Case not valid".
	 * @param docName
	 * 			Location of the configuration.xml file associated with the project
	 * 			whose Test Case is going to be executed.
	 */
	public Manager(String testName, String docName)
	{
		this.testName = testName;
		this.configurationXmlDocument = getXmlContent(docName);
	}
	
	private Document getXmlContent(String xmlPath)
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document xmlDocument = null;
		String xmlContent = null;
		
		try
		{
			xmlContent = new String(Files.readAllBytes(Paths.get(xmlPath)), StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			xmlDocument = dbFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlContent)));
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlDocument;
	}

	/**
	 * This method reads all necessary parameters (ICS, IXIT, General Parameters) from XML
	 * and saves them into variables. After that, it starts the execution of the Test Case.
	 * 
	 * While this method is active 'running' remains set to TRUE, being set to FALSE
	 * when the execution finishes.
	 */
	public void run()
	{	
		if (loadTestCaseInfo())
		{
			logger.raw("====================================================");
			logger.raw("Test Name: %s", testName);
			logger.raw("Description: %s", descriptionKey);
			logger.raw("%s", getJarFile());
			logger.raw("====================================================");
			
			Timestamp timeStamp = new Timestamp(new java.util.Date().getTime());
			loadParameters();
			runTest();

			generateXML(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(timeStamp),
					String.format("Log-%s-%s", testName, new SimpleDateFormat("yyyyMMdd-HHmmss").format(timeStamp)));
		}
		else
		{
			logger.error("Test Case not existent or not selected in project configuration");
		}
	}
	
	private String getJarFile()
	{
	    String path = Manager.class.getResource(Manager.class.getSimpleName() + ".class").getFile();
	    
	    if (path.startsWith("/"))
	    {
	        return "Running in debug mode";
	    }
	    path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

	    return new File(path.substring(0, path.lastIndexOf('!'))).getName();
	}
	
	private void loadParameters()
	{
		this.icsList = new Ics(configurationXmlDocument);
		this.ixitList = new Ixit(configurationXmlDocument);
		this.gpList = new GeneralParameter(configurationXmlDocument);
	}
	
	private boolean loadTestCaseInfo()
	{
		try
		{ 
			NodeList testCases = configurationXmlDocument.getElementsByTagName("TestCase");
			
			for (int i = 0; i < testCases.getLength(); i++)
			{
				Node node = testCases.item(i);
				Element element= (Element) node;
				String testName = getValue("Name", element);
				
				if (testName.equals(this.testName))
				{
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						idKey = getValue("Id", element);
						descriptionKey = getValue("Description", element);
						
						return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}

	/**
	 * This method manages the execution of a certain Test Case, stored in 'testName'
	 * 
	 * @return	TRUE if the Test Case exists, FALSE otherwise
	 */
	private boolean runTest()
	{
		logger.info(String.format("started: %s", testName));	
		boolean existTest = true;

		if (testName.startsWith("About") || testName.startsWith("EventsActions"))
		{
			printParameters("CO");
			AboutTestSuite AboutTest = new AboutTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = AboutTest.getFinalVerdict();
		}
		else if (testName.startsWith("Notification"))
		{
			printParameters("N");
			NotificationTestSuite NotificationTest = new NotificationTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = NotificationTest.getFinalVerdict();
		}
		else if (testName.startsWith("ControlPanel"))
		{
			printParameters("CP");
			ControlPanelTestSuite ControlPanelTest = new ControlPanelTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = ControlPanelTest.getFinalVerdict();
		}
		else if (testName.startsWith("Onboarding"))
		{
			printParameters("ON");
			OnboardingTestSuite ControlPanelTest = new OnboardingTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = ControlPanelTest.getFinalVerdict();
		}
		else if (testName.startsWith("Config"))
		{
			printParameters("CF");
			ConfigurationTestSuite ConfigurationTest = new ConfigurationTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = ConfigurationTest.getFinalVerdict();
		}
		else if (testName.startsWith("Audio"))
		{
			printParameters("AU");
			AudioTestSuite AudioTest = new AudioTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = AudioTest.getFinalVerdict();
		}
		else if (testName.startsWith("LSF_Lamp"))
		{
			printParameters("L");
			LSF_LampTestSuite lightingTest = new LSF_LampTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = lightingTest.getFinalVerdict();
		}
		else if (testName.startsWith("LSF_Controller"))
		{
			printParameters("LC");
			LSF_ControllerTestSuite lightingControllerService = new LSF_ControllerTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = lightingControllerService.getFinalVerdict();
		}
		else if (testName.startsWith("TimeService"))
		{
			printParameters("T");
			TimeTestSuite TimeTest = new TimeTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = TimeTest.getFinalVerdict();
		}
		else if (testName.startsWith("GWAgent"))
		{
			printParameters("G");
			GWAgentTestSuite GatewayTest = new GWAgentTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = GatewayTest.getFinalVerdict();
		}
		else if (testName.startsWith("SmartHome"))
		{
			printParameters("SH");
			SmartHomeTestSuite SmartHomeTest = new SmartHomeTestSuite(testName, icsList, ixitList, gpList);
			verdictKey = SmartHomeTest.getFinalVerdict();
		}
		else if (testName.startsWith("IOP_About"))
		{
			setGU();
			printParameters("CO");
			AboutIOPTestSuite aboutIOP = new AboutIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = aboutIOP.getFinalVerdict();
		}
		else if (testName.startsWith("IOP_Config"))
		{
			setGU();
			printParameters("CF");
			ConfigIOPTestSuite configIOP = new ConfigIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = configIOP.getFinalVerdict();
		}
		else if (testName.startsWith("IOP_Onboarding"))
		{
			setGU();
			printParameters("ON");
			OnboardingIOPTestSuite onboardingIOP = new OnboardingIOPTestSuite(testName, goldenUnits);
			verdictKey = onboardingIOP.getFinalVerdict();
		}
		else if (testName.startsWith("IOP_ControlPanel"))
		{
			setGU();
			printParameters("CP");
			ControlPanelIOPTestSuite controlPanelIOP = new ControlPanelIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = controlPanelIOP.getFinalVerdict();
		}
		else if (testName.startsWith("IOP_Notification"))
		{
			setGU();
			printParameters("N");
			NotificationIOPTestSuite notificationIOP = new NotificationIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = notificationIOP.getFinalVerdict();
		}
		else if (testName.startsWith("Audio"))
		{
			setGU();
			printParameters("AU");
			AudioIOPTestSuite audioIOP = new AudioIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = audioIOP.getFinalVerdict();
		}
		else if (testName.startsWith("LSF"))
		{
			setGU();
			printParameters("L");
			LightingIOPTestSuite lightingIOP = new LightingIOPTestSuite(testName, goldenUnits, icsList);
			verdictKey = lightingIOP.getFinalVerdict();
		}
		else
		{
			logger.error("This Service Framework does not exist or is not implemented yet.");
			verdictKey = "NO SUCH TESTCASE";
			existTest = false;
		}

		logger.info(String.format("finished: %s", testName));
		logger.info(String.format("Final Verdict: %s", verdictKey));

		return existTest;
	}
	
	private void printParameters(String serviceFramework)
	{
		logger.raw("====================================================");
		icsList.printIcsValues("ICS" + serviceFramework);
		ixitList.printIxitValues("IXIT" + serviceFramework);
		gpList.printGpValues("GP" + serviceFramework);
	}

	/**
	 * This method reads from XML all the information related to Golden
	 * Units, and stores it into 'goldenUnits' list.
	 */
	private void setGU()
	{
		goldenUnits = new HashMap<String, List<String>>();
		NodeList goldenUnit = configurationXmlDocument.getElementsByTagName("GoldenUnit");
		
		for (int i = 0; i < goldenUnit.getLength(); i++)
		{
			Node node = goldenUnit.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String goldenUnitName = getValue("Name", element);
				String goldenUnitCategory = getValue("Type", element);
				int num = i + 1;
				
				logger.raw("Golden Unit %d name: %s", num, goldenUnitName);
				logger.raw("Golden Unit %d type: %s", num, goldenUnitCategory);
				
				List<String> gu = goldenUnits.get(goldenUnitCategory);
				
				if (gu != null)
				{
					gu.add(goldenUnitName);
				}
				else
				{
					gu = new ArrayList<String>();
					gu.add(goldenUnitName);
				}
				goldenUnits.put(goldenUnitCategory, gu);
			}
		}
	}

	private void generateXML(String dateTime, String logName)
	{
		File testResultsXml = new File(XML_NAME);
		Document document = null;

		if (testResultsXml.exists() && testResultsXml.isFile())
		{
			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(testResultsXml);
			}
			catch (SAXException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			
			try
			{
				builder = factory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DOMImplementation implementation = builder.getDOMImplementation();
			document = implementation.createDocument(null, XML_NAME.split("\\.")[0], null);
			document.setXmlVersion("1.0");
		}

		Element testCaseNode = document.createElement("TestCase"); 

		testCaseNode.appendChild(createNode(document, "Id", idKey));
		testCaseNode.appendChild(createNode(document, "Name", testName));
		testCaseNode.appendChild(createNode(document, "Description", descriptionKey));
		testCaseNode.appendChild(createNode(document, "DateTime", dateTime));
		testCaseNode.appendChild(createNode(document, "Verdict", verdictKey));
		testCaseNode.appendChild(createNode(document, "Version", CERTIFICATION_RELEASE)); 
		testCaseNode.appendChild(createNode(document, "LogFile", String.format("%s.log", logName)));

		document.getDocumentElement().appendChild(testCaseNode);

		Source source = new DOMSource(document);

		Result result = new StreamResult(new java.io.File(XML_NAME));
		
		try
		{
			TransformerFactory.newInstance().newTransformer().transform(source, result);
		}
		catch (TransformerConfigurationException | TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Element createNode(Document testResultsXml, String nodeTag, String nodeValue)
	{
		Element node = testResultsXml.createElement(nodeTag); 
		Text nodeText = testResultsXml.createTextNode(nodeValue);
		node.appendChild(nodeText);
		
		return node;
	}

	/**
	 * Method that reads the value of an element from an XML
	 * 
	 * @param tag
	 * 			Element whose value is going to be read
	 * @param element
	 * 			Root element that acts as container
	 * 
	 * @return	the value of the element read
	 */
	private static String getValue(String tag, Element element)
	{
		String value = "";
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		
		if (node != null)
		{
			value = node.getNodeValue();
		}
		return value;
	}
}
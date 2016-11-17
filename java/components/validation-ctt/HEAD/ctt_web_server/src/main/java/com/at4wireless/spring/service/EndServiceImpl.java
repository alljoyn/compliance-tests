package com.at4wireless.spring.service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.common.ConfigParam;
import com.at4wireless.spring.dao.CategoryDAO;
import com.at4wireless.spring.model.GoldenUnit;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Parameter;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.model.TestCase;

@Service
public class EndServiceImpl implements EndService
{
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private DutService dutService;
	
	@Autowired
	private GoldenUnitService guService;
	
	@Autowired
	private IcsService icsService;
	
	@Autowired
	private IxitService ixitService;
	
	@Autowired
	private TestCaseService tcService;
	
	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private CategoryDAO categoryDao;
	
	static final Logger log = LogManager.getLogger(EndServiceImpl.class);
	private static final String USERS_PATH = File.separator + "Allseen" + File.separator + "Users" + File.separator;
	
	@Override
	public String createXML(String username, Map<String, String[]> map)
	{
		List<Ics> icsList = new ArrayList<Ics>();
		List<Ixit> ixitList = new ArrayList<Ixit>();
		List<TestCase> tcList = new ArrayList<TestCase>();
		List<Sample> sampleList = new ArrayList<Sample>();
		
		Project p = projectService.getFormData(username, Integer.parseInt(map.get("data[idProject]")[0]));
		
		loadAvailableConfiguration(p, icsList, ixitList, tcList);
			
		// -----------------------------------------------------------------------
		// CREATE XML
		// -----------------------------------------------------------------------
		DocumentBuilder documentBuilder = null;
		try
		{
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = documentBuilder.newDocument();
		
		// -----------------------------------------------------------------------
		// ADD PROJECT DATA TO XML
		// -----------------------------------------------------------------------
		log.debug("Creating root element...");
		Element mainRootElement = doc.createElement("Project");
		doc.appendChild(mainRootElement);
		
		log.debug("Adding samples data...");
		sampleList.addAll(dutService.getSampleData(p.getIdDut()));
		Sample sampleToBeAdded;
		Iterator<Sample> samplesToBeAdded = sampleList.iterator();
		while (samplesToBeAdded.hasNext())
		{
			sampleToBeAdded = samplesToBeAdded.next();
			mainRootElement.appendChild(getSample(doc, sampleToBeAdded));
		}
		
		if (!p.getType().equalsIgnoreCase("Conformance"))
		{
			List<GoldenUnit> goldenUnitsList = guService.getGuList(p.getIdProject());
			if (!goldenUnitsList.isEmpty())
			{
				log.debug("Adding golden units data...");
				GoldenUnit goldenUnitToBeAdded;
				Iterator<GoldenUnit> goldenUnitsToBeAdded = goldenUnitsList.iterator();
				
				while (goldenUnitsToBeAdded.hasNext())
				{
					goldenUnitToBeAdded = goldenUnitsToBeAdded.next();
					mainRootElement.appendChild(getGu(doc, goldenUnitToBeAdded.getName(),
							categoryDao.getById(goldenUnitToBeAdded.getCategory()).getName()));
				}
			}
		}
		
		log.debug("Adding ics data...");
		Ics icsToBeAdded;
		Iterator<Ics> icsListToBeAdded = icsList.iterator();
		while (icsListToBeAdded.hasNext())
		{
			icsToBeAdded = icsListToBeAdded.next();
			mainRootElement.appendChild(getNode(doc, "Ics", icsToBeAdded.getId(), icsToBeAdded.getName(), 
					map.get("data[" + icsToBeAdded.getName() + "]")[0]));
		}
		
		log.debug("Adding ixit data...");
		Ixit ixitToBeAdded;
		Iterator<Ixit> ixitsToBeAdded = ixitList.iterator();
		while (ixitsToBeAdded.hasNext())
		{
			ixitToBeAdded = ixitsToBeAdded.next();
			mainRootElement.appendChild(getNode(doc, "Ixit", ixitToBeAdded.getIdIxit(), ixitToBeAdded.getName(), 
					map.get("data[" + ixitToBeAdded.getName() + "]")[0]));
		}
		
		log.debug("Adding general parameters data...");
		Parameter parameterToBeAdded;
		Iterator<Parameter> parametersToBeAdded = parameterService.list().iterator();
		while (parametersToBeAdded.hasNext())
		{
			parameterToBeAdded = parametersToBeAdded.next();
			mainRootElement.appendChild(getNode(doc, "Parameter", parameterToBeAdded.getIdParam(),
					parameterToBeAdded.getName(), map.get("data[" + parameterToBeAdded.getName() + "]")[0]));
		}
		
		log.debug("Adding test cases data...");
		mainRootElement.appendChild(getElements(doc, mainRootElement, "ApplyTC", map.get("data[applyTC]")[0]));
		
		TestCase testCaseToBeAdded;
		Iterator<TestCase> testCasesToBeAdded = tcList.iterator();
		while (testCasesToBeAdded.hasNext())
		{
			testCaseToBeAdded = testCasesToBeAdded.next();
			try
			{
				if (map.get("data[" + testCaseToBeAdded.getName() + "]")[0].equals("true"))
				{
					mainRootElement.appendChild(getTestCase(doc, testCaseToBeAdded.getIdTC().intValue(), testCaseToBeAdded.getName(), testCaseToBeAdded.getDescription()));
				}
			}
			catch (Exception e)
			{
				//Nothing will be done if a non-configured Test Case is searched
			}
		}
		
		// -----------------------------------------------------------------------
		// SAVE XML
		// -----------------------------------------------------------------------
		String configurationFolder = USERS_PATH + username + File.separator + map.get("data[idProject]")[0];
		new File(configurationFolder).mkdirs();
		String configurationFilename = configurationFolder + File.separator + "configuration.xml";
		saveXml(doc, configurationFilename);
		
		return configurationFilename;
	}
	
	public void modifyXML(String username, Map<String, String[]> map)
	{
		List<Ics> icsList = new ArrayList<Ics>();
		List<Ixit> ixitList = new ArrayList<Ixit>();
		List<TestCase> tcList = new ArrayList<TestCase>();
		
		int configuredProjectID = Integer.parseInt(map.get("data[idProject]")[0]);
		String projectFolder = USERS_PATH + username + File.separator + configuredProjectID;
		String configurationFilename = projectFolder + File.separator + "configuration.xml";
		
		log.debug("Retrieving existing project's information from database...");
		Project configuredProject = projectService.getFormData(username, configuredProjectID);
		
		log.debug("Retrieving existing project's configuration from database...");
		loadAvailableConfiguration(configuredProject, icsList, ixitList, tcList);
		
		// -----------------------------------------------------------------------
		// BOOLEAN VARIABLES TO DECIDE WHETHER A SECTION IS GOING TO BE MODIFIED
		// OR NOT
		// -----------------------------------------------------------------------
		boolean modifiedDut;
		try
		{
			modifiedDut = Boolean.parseBoolean(map.get("data[modifiedDut]")[0]);
		}
		catch (NullPointerException e)
		{
			modifiedDut = false;
		}
		
		boolean modifiedGus;
		try
		{
			modifiedGus = Boolean.parseBoolean(map.get("data[modifiedGus]")[0]);
		}
		catch (NullPointerException e)
		{
			modifiedGus = false;
		}
		
		boolean modifiedIcs;
		try
		{
			modifiedIcs = Boolean.parseBoolean(map.get("data[modifiedIcs]")[0]);
		}
		catch (NullPointerException e)
		{
			modifiedIcs = false;
		}
		
		boolean modifiedIxit;
		try
		{
			modifiedIxit = Boolean.parseBoolean(map.get("data[modifiedIxit]")[0]);
		}
		catch (NullPointerException e)
		{
			modifiedIxit = false;
		}
		
		boolean modifiedParameters;
		try
		{
			modifiedParameters = Boolean.parseBoolean(map.get("data[modifiedParameters]")[0]);
		}
		catch (NullPointerException e)
		{
			modifiedParameters = false;
		}
		
		int numberOfApplicableTestCases;
		try
		{
			numberOfApplicableTestCases = Integer.parseInt(map.get("data[applyTC]")[0]);
		}
		catch (NullPointerException e)
		{
			numberOfApplicableTestCases = 0;
		}
		
		log.debug(String.format("DUT: %b, GU: %b, ICS: %b, IXIT: %b, GP: %b, applicable TCs: %d", modifiedDut, modifiedGus, modifiedIcs, modifiedIxit,
				modifiedParameters, numberOfApplicableTestCases));
		
		// -----------------------------------------------------------------------
		// OPEN XML
		// -----------------------------------------------------------------------
		DocumentBuilder documentBuilder = null;
		try
		{
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = null;
		try
		{
			doc = documentBuilder.parse(new File(configurationFilename));
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
		
		// -----------------------------------------------------------------------
		// DELETE EXISTING DATA
		// -----------------------------------------------------------------------
		Node projectNode = doc.getFirstChild();
		NodeList childNodes = projectNode.getChildNodes();
		for (int i = childNodes.getLength() - 1; i >= 0; i--)
		{
		    Node child = childNodes.item(i);
		    
		    // If Node is not an element but a text node, jump to next Node
		    if (!(child instanceof Element))
		    {
		        continue;
		    }
		    
		    if (modifiedDut && child.getNodeName().equals("Sample"))
		    {
	    		log.debug("Deleting project's DUT information...");
	    		removeChildNode(projectNode, child);
		    }
		    
		    if (modifiedGus && child.getNodeName().equals("GoldenUnit"))
		    {
		    	log.debug("Deleting project's GUs information...");
		    	removeChildNode(projectNode, child);
		    }
		    
		    if (modifiedIcs && child.getNodeName().equals("Ics"))
	    	{
	    		log.debug("Deleting project's ICS information...");
	    		removeChildNode(projectNode, child);
	    	}
		    
		    if (modifiedIxit && child.getNodeName().equals("Ixit"))
		    {
		    	log.debug("Deleting project's IXIT information...");
		    	removeChildNode(projectNode, child);
		    }
		    
		    if (modifiedParameters && child.getNodeName().equals("Parameter"))
		    {
		    	log.debug("Deleting project's General Parameter information...");
		    	removeChildNode(projectNode, child);    	
		    }

		    if (numberOfApplicableTestCases > 0)
		    {
			    if (child.getNodeName().equals("ApplyTC"))
		    	{
			    	log.debug("Modifying project's applicable test cases number...");
			    	child.getFirstChild().setNodeValue(map.get("data[applyTC]")[0]);
		    	}
			    else if (child.getNodeName().equals("TestCase"))
			    {
			    	log.debug("Deleting project's Test Cases information...");
			    	removeChildNode(projectNode, child);
			    }
		    }
		}
		
		// -----------------------------------------------------------------------
		// ADD NEW DATA
		// -----------------------------------------------------------------------
		if (modifiedDut)
		{
			log.debug("Adding project's new samples data...");
	    	Sample sampleToBeAdded;
			Iterator<Sample> samplesToBeAdded = dutService.getSampleData(configuredProject.getIdDut()).iterator();
			while (samplesToBeAdded.hasNext())
			{
				sampleToBeAdded = samplesToBeAdded.next();
				projectNode.appendChild(getSample(doc, sampleToBeAdded));
			}
		}
		
		if (modifiedGus && (!configuredProject.getType().equals("Conformance")))
		{
			log.debug("Adding project's new golden units data...");
			List<GoldenUnit> goldenUnitsList = guService.getGuList(configuredProject.getIdProject());
			
			if (!goldenUnitsList.isEmpty())
			{
				GoldenUnit goldenUnitToBeAdded;
				Iterator<GoldenUnit> goldenUnitsToBeAdded = goldenUnitsList.iterator();
				while (goldenUnitsToBeAdded.hasNext())
				{
					goldenUnitToBeAdded = goldenUnitsToBeAdded.next();
					projectNode.appendChild(getGu(doc, goldenUnitToBeAdded.getName(),
							categoryDao.getById(goldenUnitToBeAdded.getCategory()).getName()));
				}
			}
	    }
		
		if (modifiedIcs)
		{
			log.debug("Adding project's new ICS data...");
	    	Ics IcsToBeAdded;
			Iterator<Ics> IcsListToBeAdded = icsList.iterator();
			while (IcsListToBeAdded.hasNext())
			{
				IcsToBeAdded = IcsListToBeAdded.next();
				projectNode.appendChild(getNode(doc, "Ics", IcsToBeAdded.getId(), IcsToBeAdded.getName(), 
						map.get("data[" + IcsToBeAdded.getName() + "]")[0]));
			}
		}
		
		if (modifiedIxit)
		{
			log.debug("Adding project's new IXITs data...");
	    	Ixit ixitToBeAdded;
			Iterator<Ixit> ixitsToBeAdded = ixitList.iterator();
			while (ixitsToBeAdded.hasNext())
			{
				ixitToBeAdded = ixitsToBeAdded.next();
				projectNode.appendChild(getNode(doc, "Ixit", ixitToBeAdded.getIdIxit(), ixitToBeAdded.getName(), 
						map.get("data[" + ixitToBeAdded.getName() + "]")[0]));
			}
		}
		
		if (modifiedParameters)
		{
			log.debug("Adding project's new general parameters data...");
			Parameter parameterToBeAdded;
			Iterator<Parameter> parametersToBeAdded = parameterService.list().iterator();
			while (parametersToBeAdded.hasNext())
			{
				parameterToBeAdded = parametersToBeAdded.next();
				projectNode.appendChild(getNode(doc, "Parameter", parameterToBeAdded.getIdParam(),
						parameterToBeAdded.getName(), map.get("data["+parameterToBeAdded.getName()+"]")[0]));
			}
		}
		
		if (numberOfApplicableTestCases > 0)
		{
			log.debug("Adding project's new test cases data...");
		    TestCase testCaseToBeAdded;
			Iterator<TestCase> testCasesToBeAdded = tcList.iterator();
			while (testCasesToBeAdded.hasNext())
			{
				testCaseToBeAdded = testCasesToBeAdded.next();
				try
				{
					if (map.get("data[" + testCaseToBeAdded.getName() + "]")[0].equals("true"))
					{
						projectNode.appendChild(getTestCase(doc, testCaseToBeAdded.getIdTC().intValue(), testCaseToBeAdded.getName(), testCaseToBeAdded.getDescription()));
					}
				}
				catch (Exception e)
				{
					//Nothing will be done if a non-configured Test Case is searched
				}
			}
		}
		
		// -----------------------------------------------------------------------
		// SAVE XML
		// -----------------------------------------------------------------------
		saveXml(doc, configurationFilename);
	}
	
	/**
	 * Removes a certain node and its indent
	 * 
	 * @param parentNode
	 * @param childNode
	 */
	private void removeChildNode(Node parentNode, Node childNode)
	{
		Node prev = childNode.getPreviousSibling();
		 if (prev != null && prev.getNodeType() == Node.TEXT_NODE &&
				 prev.getNodeValue().trim().length() == 0)
		 {
		     parentNode.removeChild(prev);
		 }

		 parentNode.removeChild(childNode);
	}
	
	private void saveXml(Document doc, String path)
	{
		Transformer transformer = null;
		
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(doc);
		StreamResult console = new StreamResult(new File(path));
		
		try
		{
			transformer.transform(source, console);
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("XML created successfully");
	}
	
	private void loadAvailableConfiguration(Project p, List<Ics> icsList, List<Ixit> ixitList, List<TestCase> tcList)
	{	
		for (BigInteger bi : projectService.getServicesData(p.getIdProject()))
		{
			icsList.addAll(icsService.getService(bi.intValue()));
			ixitList.addAll(ixitService.getService(bi.intValue()));
			
			if (p.getType().equals("Conformance") || p.getType().equals("Interoperability"))
			{
				tcList.addAll(tcService.getService(p.getType(),bi));
			}
			else
			{
				tcList.addAll(tcService.getService("Interoperability",bi));
				tcList.addAll(tcService.getService("Conformance",bi));
			}
		}
	}
	
	/**
	 * Adds ICS/IXIT/General Parameter values to XML
	 * 
	 * @param doc
	 * 			XML document where nodes are going to be appended
	 * @param type
	 * 			type of node to be created
	 * @param id
	 * 			node ID
	 * @param name
	 * 			node name
	 * @param value
	 * 			node value
	 * 
	 * @return created node
	 */
	private static Node getNode(Document doc, String type, int id, String name, String value)
	{
		Element element = doc.createElement(type);
		element.appendChild(getElements(doc, element, "Id", Integer.toString(id)));
		element.appendChild(getElements(doc, element, "Name", name));
		element.appendChild(getElements(doc, element, "Value", value));
		
		return element;
	}
	
	/**
	 * Adds Test Case to XML
	 * 
	 * @param doc
	 * @param id
	 * @param name
	 * @param description
	 * 
	 * @return
	 */
	private static Node getTestCase(Document doc, int id, String name, String description)
	{
		Element testCaseElement = doc.createElement("TestCase");
		testCaseElement.appendChild(getElements(doc, testCaseElement, "Id", Integer.toString(id)));
		testCaseElement.appendChild(getElements(doc, testCaseElement, "Name", name));
		testCaseElement.appendChild(getElements(doc, testCaseElement, "Description", description));
		testCaseElement.appendChild(getElements(doc, testCaseElement, "LastExec", "Not executed"));
		testCaseElement.appendChild(getElements(doc, testCaseElement, "LastVerdict", "Not executed"));
		
		return testCaseElement;
	}
	
	/**
	 * Appends an element to a node
	 * 
	 * @param doc
	 * 			XML document where element is going to be appened
	 * @param element
	 * 			element to be appened
	 * @param name
	 * 			element name
	 * @param value
	 * 			element value
	 * 
	 * @return				created node
	 */
	private static Node getElements(Document doc, Element element, String name, String value)
	{
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
	
	/**
	 * Adds a sample to XML
	 * 
	 * @param doc
	 * 			XML document where sample is going to be appened
	 * @param sample
	 * 			sample to be appened
	 * 
	 * @return created node
	 */
	private static Node getSample(Document doc, Sample sample)
	{
		Element sampleElement = doc.createElement("Sample");
		sampleElement.appendChild(getElements(doc, sampleElement, "Id", Integer.toString(sample.getIdSample())));
		sampleElement.appendChild(getElements(doc, sampleElement, "DeviceId", sample.getDeviceId()));
		sampleElement.appendChild(getElements(doc, sampleElement, "AppId", sample.getAppId()));
		sampleElement.appendChild(getElements(doc, sampleElement, "swVer", sample.getSwVer()));
		sampleElement.appendChild(getElements(doc, sampleElement, "hwVer", sample.getHwVer()));
		
		return sampleElement;
	}
		
	/**
	 * Adds a Golden Unit to the XML
	 * 
	 * @param doc
	 * 			XML document where Golden Unit is going to be appened
	 * @param str
	 * 			Golden Unit name
	 * 
	 * @return created node
	 */
	private static Node getGu(Document doc, String name, String type)
	{
		Element goldenUnitElement = doc.createElement("GoldenUnit");
		goldenUnitElement.appendChild(getElements(doc, goldenUnitElement, "Name", name));
		goldenUnitElement.appendChild(getElements(doc, goldenUnitElement, "Type", type));
		return goldenUnitElement;
	}
	
	public String lastUpload()
	{		
		File localAgentsFolder = new File(ConfigParam.INSTALLERS_PATH);
		File[] localAgents = localAgentsFolder.listFiles();
		String higher = null;
		
		if (localAgents.length > 0)
		{
			higher = localAgents[0].getName();
			
			for (int i = 1; i < localAgents.length; i++)
			{
				String result = compare(higher, localAgents[i].getName());
				if (result.equals("higher"))
				{
					higher = localAgents[i].getName();
				}
			}
		}
		
		return higher;
	}
	
	/**
	 * Compares two versions X.Y.Z and determines if second is higher than first
	 * 
	 * @param localAgentV1
	 * 			version 1
	 * @param localAgentV2
	 * 			version 2
	 * 
	 * @return the verdict of the comparison
	 */
	private String compare(String localAgentV1, String localAgentV2)
	{
		String aux1 = localAgentV1.replaceAll("\\D+", "_");
		String aux2 = localAgentV2.replaceAll("\\D+", "_");
		String[] aux3 = aux1.split("_");
		String[] aux4 = aux2.split("_");
			
		if (Integer.parseInt(aux3[1]) > Integer.parseInt(aux4[1]))
		{
			return "lower";
		}
		else if (Integer.parseInt(aux3[1]) == Integer.parseInt(aux4[1]))
		{
			if (Integer.parseInt(aux3[2]) > Integer.parseInt(aux4[2]))
			{
				return "lower";
			}
			else if (Integer.parseInt(aux3[2]) == Integer.parseInt(aux4[2]))
			{
				if (Integer.parseInt(aux3[3]) > Integer.parseInt(aux4[3]))
				{
					return "lower";
				}
				else if (Integer.parseInt(aux3[3]) == Integer.parseInt(aux4[3]))
				{
					return "equal";
				}
			}
		}
		
		return "higher";
	}
}
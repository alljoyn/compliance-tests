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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	private static final String USERS_PATH = File.separator + "Allseen" + File.separator + "Users" + File.separator;
	
	@Override
	@Transactional
	public String createXML(String username, Map<String, String[]> map)
	{
		DocumentBuilderFactory cfFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder cfBuilder;
		List<Ics> icsList = new ArrayList<Ics>();
		List<Ixit> ixitList = new ArrayList<Ixit>();
		List<TestCase> tcList = new ArrayList<TestCase>();
		List<Sample> sampleList = new ArrayList<Sample>();
		String url = "";
		String folder = "";
		
		Project p = projectService.getFormData(username, Integer.parseInt(map.get("data[idProject]")[0]));
		
		loadAvailableConfiguration(p, icsList, ixitList, tcList);
		
		sampleList.addAll(dutService.getSampleData(p.getIdDut()));
		
		try
		{
			cfBuilder = cfFactory.newDocumentBuilder();
			Document doc = cfBuilder.newDocument();
			
			Element mainRootElement = doc.createElement("Project");
			doc.appendChild(mainRootElement);
			
			Ics ics;
			Iterator<Ics> iter = icsList.iterator();
			while (iter.hasNext())
			{
				ics = iter.next();
				mainRootElement.appendChild(getNode(doc, "Ics", ics.getId(), ics.getName(), 
						map.get("data["+ics.getName()+"]")[0]));
			}
			
			Ixit ixit;
			Iterator<Ixit> iter2 = ixitList.iterator();
			while (iter2.hasNext())
			{
				ixit = iter2.next();
				mainRootElement.appendChild(getNode(doc, "Ixit", ixit.getIdIxit(), ixit.getName(), 
						map.get("data["+ixit.getName()+"]")[0]));
						//map.get("data["+ixit.getName()+"]")[0].replaceAll("\"", "&quot;")));
			}
			
			mainRootElement.appendChild(getElements(doc, mainRootElement, "ApplyTC", map.get("data[applyTC]")[0]));
			
			TestCase tc;
			Iterator<TestCase> iter3 = tcList.iterator();
			while (iter3.hasNext())
			{
				tc = iter3.next();
				try
				{
					if (map.get("data["+tc.getName()+"]")[0].equals("true")) {
						mainRootElement.appendChild(getTestCase(doc, tc.getIdTC(), tc.getName(), tc.getDescription()));
					}
				}
				catch (Exception e)
				{
					
				}
			}
			
			Sample s;
			Iterator<Sample> iter4 = sampleList.iterator();
			while (iter4.hasNext())
			{
				s = iter4.next();
				mainRootElement.appendChild(getSample(doc, s));
			}
			
			Parameter param;
			Iterator<Parameter> iter5 = parameterService.list().iterator();
			while (iter5.hasNext())
			{
				param = iter5.next();
				mainRootElement.appendChild(getNode(doc, "Parameter", param.getIdParam()
						, param.getName(), map.get("data["+param.getName()+"]")[0]));
			}
			
			if (!p.getType().equalsIgnoreCase("Conformance")) {
				GoldenUnit gu;
				List<GoldenUnit> guList = guService.getGuList(p.getIdProject());
				
				if (!guList.isEmpty())
				{
					Iterator<GoldenUnit> iter6 = guService.getGuList(p.getIdProject()).iterator();
					while(iter6.hasNext()) {
						gu = iter6.next();
						mainRootElement.appendChild(getGu(doc,gu.getName(),categoryDao.getCategoryById(gu.getCategory()).getName()));
					}
				}
			}
				
			folder = File.separator+"Allseen"+File.separator+"Users"+File.separator+username+File.separator
					+map.get("data[idProject]")[0];
			new File(folder).mkdirs();
			url = folder+File.separator+"configuration.xml";
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult console = new StreamResult(new File(url));
			transformer.transform(source, console);
			
			System.out.println("\nXML DOM Created Successfully..");
			
			url = File.separator+File.separator+"Allseen"+File.separator+File.separator+"Users"
					+File.separator+File.separator+username
					+File.separator+File.separator+map.get("data[idProject]")[0]
					+File.separator+File.separator+"configuration.xml";
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return url;
	}
	
	public void modifyXML(String username, Map<String, String[]> map)
	{
		String folder = USERS_PATH + username + File.separator + map.get("data[idProject]")[0];
		String filename = folder + File.separator + "configuration.xml";
		List<Ics> icsList = new ArrayList<Ics>();
		List<Ixit> ixitList = new ArrayList<Ixit>();
		List<TestCase> tcList = new ArrayList<TestCase>();
		
		Project p = projectService.getFormData(username, Integer.parseInt(map.get("data[idProject]")[0]));
		
		loadAvailableConfiguration(p, icsList, ixitList, tcList);
		
		// -----------------------------------------------------------------------
		// OPEN XML
		// -----------------------------------------------------------------------
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = null;
		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = null;
		try
		{
			doc = db.parse(new File(filename));
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
		
		Node projectNode = doc.getFirstChild();
		
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
		
		// -----------------------------------------------------------------------
		// MODIFY CONFIGURATION
		// -----------------------------------------------------------------------
		NodeList childNodes = projectNode.getChildNodes();
		for (int i = 0; i != childNodes.getLength(); ++i)
		{
		    Node child = childNodes.item(i);
		    if (!(child instanceof Element))
		        continue;
		    
		    if (modifiedDut)
		    {
		    	if (child.getNodeName().equals("Sample"))
		    	{
		    		projectNode.removeChild(child);
		    	}
		    }
		    
		    if (modifiedGus)
		    {
		    	if (child.getNodeName().equals("GoldenUnit"))
		    	{
		    		projectNode.removeChild(child);
		    		guService.deleteProjectGus(p.getIdProject());
		    	}
		    }
		    if (modifiedIcs)
		    {
		    	if (child.getNodeName().equals("Ics"))
		    	{
		    		projectNode.removeChild(child);
		    	}
		    }
		    
		    if (modifiedIxit)
		    {
		    	if (child.getNodeName().equals("Ixit"))
		    	{
		    		projectNode.removeChild(child);
		    	}
		    }
		    
		    if (modifiedParameters)
		    {
		    	if (child.getNodeName().equals("Parameter"))
		    	{
		    		projectNode.removeChild(child);
		    	}
		    }

		    if (numberOfApplicableTestCases > 0)
		    {
			    if (child.getNodeName().equals("ApplyTC"))
			    {
			        child.getFirstChild().setNodeValue(map.get("data[applyTC]")[0]);
			    }
			    else if (child.getNodeName().equals("TestCase"))
			    {
			        projectNode.removeChild(child);
			    }
		    }
		}
		
		if (modifiedDut)
		{
			Sample s;
			Iterator<Sample> iter4 = dutService.getSampleData(p.getIdDut()).iterator();
			while (iter4.hasNext())
			{
				s = iter4.next();
				projectNode.appendChild(getSample(doc, s));
			}
		}
		
		if (modifiedGus)
		{
			if (!p.getType().equalsIgnoreCase("Conformance")) {
				GoldenUnit gu;
				List<GoldenUnit> guList = guService.getGuList(p.getIdProject());
				
				if (!guList.isEmpty())
				{
					Iterator<GoldenUnit> iter6 = guService.getGuList(p.getIdProject()).iterator();
					while(iter6.hasNext()) {
						gu = iter6.next();
						projectNode.appendChild(getGu(doc,gu.getName(),categoryDao.getCategoryById(gu.getCategory()).getName()));
					}
				}
			}
		}
		
		if (modifiedIcs)
		{
			Ics ics;
			Iterator<Ics> iter = icsList.iterator();
			while (iter.hasNext())
			{
				ics = iter.next();
				projectNode.appendChild(getNode(doc, "Ics", ics.getId(), ics.getName(), 
						map.get("data["+ics.getName()+"]")[0]));
			}
		}
		
		if (modifiedIxit)
		{
			Ixit ixit;
			Iterator<Ixit> iter2 = ixitList.iterator();
			while (iter2.hasNext())
			{
				ixit = iter2.next();
				projectNode.appendChild(getNode(doc, "Ixit", ixit.getIdIxit(), ixit.getName(), 
						map.get("data["+ixit.getName()+"]")[0]));
			}
		}
		
		if (modifiedParameters)
		{
			Parameter param;
			Iterator<Parameter> iter5 = parameterService.list().iterator();
			while (iter5.hasNext())
			{
				param = iter5.next();
				projectNode.appendChild(getNode(doc, "Parameter", param.getIdParam()
						, param.getName(), map.get("data["+param.getName()+"]")[0]));
			}
		}
		
		if (numberOfApplicableTestCases > 0)
		{
			TestCase tc;
			Iterator<TestCase> iter3 = tcList.iterator();
			while (iter3.hasNext())
			{
				tc = iter3.next();
				try
				{
					if (map.get("data[" + tc.getName() + "]")[0].equals("true"))
					{
						projectNode.appendChild(getTestCase(doc, tc.getIdTC(), tc.getName(), tc.getDescription()));
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
		saveXml(doc, filename);
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
		
		System.out.println("\nXML DOM Created Successfully..");
	}
	
	private void loadAvailableConfiguration(Project p, List<Ics> icsList, List<Ixit> ixitList, List<TestCase> tcList)
	{	
		for (BigInteger bi : projectService.getServicesData(p.getIdProject()))
		{
			icsList.addAll(icsService.getService(bi.intValue()));
			ixitList.addAll(ixitService.getService(bi.intValue()));
			
			if (p.getType().equals("Conformance") || p.getType().equals("Interoperability"))
			{
				tcList.addAll(tcService.getService(p.getType(),bi.intValue()));
			}
			else
			{
				tcList.addAll(tcService.getService("Interoperability",bi.intValue()));
				tcList.addAll(tcService.getService("Conformance",bi.intValue()));
			}
		}
	}
	
	/**
	 * Adds ICS/IXIT/General Parameter values to XML
	 * 
	 * @param 	doc		XML document where nodes are going to be appended
	 * @param	type	type of node to be created
	 * @param 	id		node ID
	 * @param 	name	node name
	 * @param 	value	node value
	 * @return			created node
	 */
	private static Node getNode(Document doc, String type, int id, String name, String value) {
		Element ics = doc.createElement(type);
		ics.appendChild(getElements(doc, ics, "Id", Integer.toString(id)));
		ics.appendChild(getElements(doc, ics, "Name", name));
		ics.appendChild(getElements(doc, ics, "Value", value));
		return ics;
	}
	
	/**
	 * Adds Test Case to XML
	 * 
	 * @param doc
	 * @param id
	 * @param name
	 * @param description
	 * @return
	 */
	private static Node getTestCase(Document doc, int id, String name, String description) {
		Element tc = doc.createElement("TestCase");
		tc.appendChild(getElements(doc, tc, "Id", Integer.toString(id)));
		tc.appendChild(getElements(doc, tc, "Name", name));
		tc.appendChild(getElements(doc, tc, "Description", description));
		tc.appendChild(getElements(doc, tc, "LastExec", "Not executed"));
		tc.appendChild(getElements(doc, tc, "LastVerdict", "Not executed"));
		return tc;
	}
	
	/**
	 * Appends a element to a node
	 * 
	 * @param 	doc			XML document where element is going to be appened
	 * @param 	element		element to be appened
	 * @param 	name		element name
	 * @param 	value		element value
	 * @return				created node
	 */
	private static Node getElements(Document doc, Element element, String name, String value) {
		
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
	
	/**
	 * Adds a sample to XML
	 * @param 	doc		XML document where sample is going to be appened
	 * @param 	sample	sample to be appened
	 * @return			created node
	 */
	private static Node getSample(Document doc, Sample sample) {
		Element s = doc.createElement("Sample");
		s.appendChild(getElements(doc, s, "Id", Integer.toString(sample.getIdSample())));
		s.appendChild(getElements(doc, s, "DeviceId", sample.getDeviceId()));
		s.appendChild(getElements(doc, s, "AppId", sample.getAppId()));
		s.appendChild(getElements(doc, s, "swVer", sample.getSwVer()));
		s.appendChild(getElements(doc, s, "hwVer", sample.getHwVer()));
		return s;
	}
		
	/**
	 * Adds a Golden Unit to the XML
	 * @param 	doc		XML document where Golden Unit is going to be appened
	 * @param 	str		Golden Unit name
	 * @return			created node
	 */
	private static Node getGu(Document doc, String name, String type) {
		Element s = doc.createElement("GoldenUnit");
		s.appendChild(getElements(doc, s, "Name", name));
		s.appendChild(getElements(doc, s, "Type", type));
		return s;
	}
	
	public String lastUpload() {
		String url = File.separator+"Allseen"
				+File.separator+"localAgent";
		
		File folder = new File(url);
		File[] listOfFiles = folder.listFiles();
		String higher=null;
		
		if (listOfFiles.length>0) {
			higher = listOfFiles[0].getName();
			
			for (int i=1; i<listOfFiles.length; i++) {
				String result = compare(higher,listOfFiles[i].getName());
				if(result.equals("higher")) {
					higher = listOfFiles[i].getName();
				}
			}
		}
		return higher;
	}
	
	/**
	 * Compares two versions X.Y.Z and determines if second is higher than first
	 * @param 	s1	version1
	 * @param 	s2	version2
	 * @return		the verdict of the comparison
	 */
	private String compare(String s1, String s2) {
		String aux1 = s1.replaceAll("\\D+", "_");
		String aux2 = s2.replaceAll("\\D+", "_");
		String[] aux3 = aux1.split("_");
		String[] aux4 = aux2.split("_");
			
		if (Integer.parseInt(aux3[1])>Integer.parseInt(aux4[1])) {
			return "lower";
		} else if (Integer.parseInt(aux3[1])==Integer.parseInt(aux4[1])) {
			if (Integer.parseInt(aux3[2])>Integer.parseInt(aux4[2])) {
				return "lower";
			} else if (Integer.parseInt(aux3[2])==Integer.parseInt(aux4[2])) {
				if (Integer.parseInt(aux3[3])>Integer.parseInt(aux4[3])) {
					return "lower";
				} else if (Integer.parseInt(aux3[3])==Integer.parseInt(aux4[3])) {
					return "equal";
				}
			}
		}
		return "higher";
	}

}

package com.at4wireless.spring.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.dao.TcDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.TestCase;

@Service
public class TestCaseServiceImpl implements TestCaseService {
	
	@Autowired
	private IcsDAO icsDao;
	
	@Autowired
	private TcDAO tcDao;
	
	@Autowired
	private CertificationReleaseDAO crDao;
	
	@Autowired
	private TcclDAO tcclDao;
	
	@Override
	@Transactional
	public List<TestCase> load(List<BigInteger> services, Map<String,String[]> map, String type,
			int idCertRel) {

		List<Ics> listIcs = new ArrayList<Ics>();
		List<TestCase> listTC = new ArrayList<TestCase>();
		//Get Project services to filter ICS			
		for (BigInteger bi : services) {
			listIcs.addAll(icsDao.getService(bi.intValue()));
			if(bi.intValue()==3) {
				listIcs.addAll(icsDao.getService(5)); //JTF: Change to a generic way
			}
			List<Integer> intList = crDao.getIds(idCertRel);
			if(type.equals("Pre-certification")||type.equals("Conformance and Interoperability")) {
				/*listTC.addAll(tcDao.getService("Conformance", bi.intValue()));
				listTC.addAll(tcDao.getService("Interoperability", bi.intValue()));*/
				listTC.addAll(tcDao.getServiceWithRestriction("Conformance", bi.intValue(), intList));
				listTC.addAll(tcDao.getServiceWithRestriction("Interoperability", bi.intValue(), intList));
			} else {
				//listTC.addAll(tcDao.getService(type, bi.intValue()));
				listTC.addAll(tcDao.getServiceWithRestriction(type, bi.intValue(), intList));
			}
		}
		return checkApplicability(listTC, listIcs, map);
	}
	
	//Check applicability method
	private List<TestCase> checkApplicability(List<TestCase> listTC, List<Ics> listIcs, 
			Map<String, String[]> map) {
		
		String condition = new String();
		List<TestCase> applyTC = new ArrayList<TestCase>();
		boolean b = false;
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		
		//Load boolean values on engine
		for (Ics i : listIcs) {

			if(map.containsKey("data["+i.getName()+"]")) {
				b = Boolean.parseBoolean(map.get("data["+i.getName()+"]")[0]);
					try {
						if (b) engine.eval(i.getName()+" = 1");
						else engine.eval(i.getName()+" = 0");
					} catch (ScriptException e) {
						e.printStackTrace();
					}
			} else {
				try {
					engine.eval(i.getName()+" = 0");
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		}
		
		//When values are loaded, engine can evaluate Applicability expressions
		for (TestCase tc : listTC) {
			
			condition = tc.getApplicability();

			if (!condition.isEmpty()) {
				try {
					if ((Double)engine.eval(condition)==1.0) {
						applyTC.add(tc);
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			} else {
				applyTC.add(tc);
			}
		}
		
		return applyTC;
	}

	@Override
	@Transactional
	public List<TestCase> getService(String type, int idService) {
		return tcDao.getService(type, idService);
	}

	@Override
	@Transactional
	public List<TestCase> list() {
		return tcDao.list();
	}

	@Override
	@Transactional
	public List<TestCase> list(int idCertRel) {
		return tcDao.list(idCertRel);
	}
	
	@Override
	@Transactional
	public List<Integer> getEnabled(int idTccl) {
		return tcclDao.getIds(idTccl);
	}
	
	@Override
	@Transactional
	public List<Integer> getDisabled(int idTccl) {
		return tcclDao.getIdsDisabled(idTccl);
	}

	@Override
	public List<String> pdfData(String configuration, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator = ": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/TestCase/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			listString.add("====================");
			listString.add("SELECTED TESTCASES AND RESULTS");
			listString.add("====================");
			for (int i = 0; i < nodeList.getLength(); i++) {
				String str = lastExecution(nodeList.item(i).getFirstChild().getNodeValue(), results);
			    listString.add(nodeList.item(i).getFirstChild().getNodeValue()+separator
			    		+str);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return listString;
	}
	
	private String lastExecution(String tcName, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String r = "";
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(results));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Results/TestCase/Name";
			String expression2 = "/Results/TestCase/DateTime";
			String expression3 = "/Results/TestCase/Verdict";
			
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			NodeList nodeList3 = (NodeList) xPath.compile(expression3).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				if(nodeList.item(i).getFirstChild().getNodeValue().equals(tcName)) {
					r=(nodeList2.item(i).getFirstChild().getNodeValue()+", "
							+nodeList3.item(i).getFirstChild().getNodeValue());
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return r;
	}
	
	@Override
	public List<String> zipData(String configuration, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator = ": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/TestCase/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				String str = lastLog(nodeList.item(i).getFirstChild().getNodeValue(), results);
			    if(!str.equals("")) {
			    	listString.add(str);
			    }
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return listString;
	}
	
	private String lastLog(String tcName, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String r = "";
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(results));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Results/TestCase/Name";
			String expression2 = "/Results/TestCase/LogFile";
			
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				if(nodeList.item(i).getFirstChild().getNodeValue().equals(tcName)) {
					r=nodeList2.item(i).getFirstChild().getNodeValue();
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return r;
	}
	
	@Override
	public boolean ranAll(String configuration, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/TestCase/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				String str = lastLog(nodeList.item(i).getFirstChild().getNodeValue(), results);
			    if(str.equals("")) {
			    	return false;
			    }
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}

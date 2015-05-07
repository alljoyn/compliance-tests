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

import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Result;

@Service
public class IcsServiceImpl implements IcsService {
	
	@Autowired
	private IcsDAO icsDao;

	@Override
	//@Transactional
	public List<Ics> load(List<BigInteger> services, boolean isConfigured, String configuration) {
		List<Ics> listIcs = new ArrayList<Ics>();
		
		for (BigInteger bi : services) {
			listIcs.addAll(icsDao.getService(bi.intValue()));
		}
		
		//If project is configured, load saved values
		if(isConfigured) {			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			
			try {
				//Load XML
				builder = builderFactory.newDocumentBuilder();
				Document source = builder.parse(new FileInputStream(configuration));
				
				//Get Value nodes
				XPath xPath = XPathFactory.newInstance().newXPath();	
				String expression = "/Project/Ics/Value";
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
				
				//If true, set ICS (default load is false)
				for (int i = 0; i < nodeList.getLength(); i++) {
				    if(nodeList.item(i).getFirstChild().getNodeValue().equalsIgnoreCase("true")) {
				    	listIcs.get(i).setValue(true);
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
		}
		
		return listIcs;
	}
	
	@Override
	@Transactional
	public List<Result> check(List<BigInteger> services, Map<String,String[]> map) {
		List<Ics> listIcs = new ArrayList<Ics>();
		
		for (BigInteger bi : services) {
			listIcs.addAll(icsDao.getService(bi.intValue()));
		}
	
		return checkScr(listIcs, map);
	}
	
	//Check SCR method
	private List<Result> checkScr(List<Ics> listIcs, 
			Map<String, String[]> map) {
		
		String condition = new String();
		List<Result> listResult = new ArrayList<Result>();
		boolean b = false;
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		
		//Load boolean values on engine
		for (Ics ics : listIcs) {
			b = Boolean.parseBoolean(map.get("data["+ics.getName()+"]")[0]);
			try {
				if (b) engine.eval(ics.getName()+" = 1");
				else engine.eval(ics.getName()+" = 0");
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		
		//When values are loaded, engine can evaluate SCR expressions
		for (Ics ics : listIcs) {
			condition = ics.getScrExpression();
			
			//Result is a model to return {idIcs, scrResult, serviceGroup}
			Result r = new Result(ics.getId(), ics.getServiceGroup(), true);
			if((condition!=null)&&(!condition.isEmpty())) {
				try {
					r.setResult((Double)engine.eval(condition)==1.0);
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
			listResult.add(r);
		}
		
		return listResult;
	}

	@Override
	@Transactional
	public List<Ics> getService(int idService) {
		return icsDao.getService(idService);
	}

	@Override
	public List<String> pdfData(String configuration) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator=": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			//Load XML
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			//Get Value nodes
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Ics/Value";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/Ics/Name";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("====================");
			listString.add("CONFIGURED ICS");
			listString.add("====================");
			for (int i = 0; i < nodeList.getLength(); i++) {
			    listString.add(nodeList2.item(i).getFirstChild().getNodeValue()+separator
			    		+nodeList.item(i).getFirstChild().getNodeValue());
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

}

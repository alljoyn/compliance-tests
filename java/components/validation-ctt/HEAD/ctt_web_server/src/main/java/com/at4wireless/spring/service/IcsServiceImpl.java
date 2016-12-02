/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.spring.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.common.XMLManager;
import com.at4wireless.spring.dao.IcsDAO;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Result;

@Service
public class IcsServiceImpl implements IcsService
{
	@Autowired
	private IcsDAO icsDao;
	
	static final Logger log = LogManager.getLogger(IcsServiceImpl.class);

	@Override
	//@Transactional
	public List<Ics> load(List<BigInteger> services, boolean isConfigured, String configuration)
	{
		List<Ics> listIcs = new ArrayList<Ics>();
		
		// get all available ICS for supported services
		for (BigInteger bi : services)
		{
			listIcs.addAll(icsDao.getByService(bi.intValue()));
		}
		
		// if project is configured, load configured values
		if (isConfigured)
		{	
			XMLManager xmlManager = new XMLManager();
			// load IDs and values
			List<String> listOfIcsIds = xmlManager.retrieveNodeValuesFromFile(configuration, "/Project/Ics/Id");
			List<String> listOfIcsValues = xmlManager.retrieveNodeValuesFromFile(configuration, "/Project/Ics/Value");
			// store in a map			
			Iterator<String> i1 = listOfIcsIds.iterator();
			Iterator<String> i2 = listOfIcsValues.iterator();
			Map<Integer, Boolean> icsMap = new HashMap<Integer, Boolean>();
			
			while (i1.hasNext() && i2.hasNext())
			{
			    icsMap.put(Integer.parseInt(i1.next()), Boolean.parseBoolean(i2.next()));
			}
			
			if (i1.hasNext() || i2.hasNext())
			{
				// if the size of the lists is different, an error occurred
				log.error(String.format("The number of Id nodes (%d) and Value nodes (%d) is not the same", 
						listOfIcsIds.size(), listOfIcsValues.size()));
			}
			else
			{
				// else, load the stored ICS values
				for (Ics ics : listIcs)
				{
					if (icsMap.get(ics.getId()) != null)
					{
						ics.setValue(icsMap.get(ics.getId()));
					}
				}
			}
		}
		
		return listIcs;
	}
	
	@Override
	@Transactional
	public List<Result> check(List<BigInteger> services, Map<String,String[]> map)
	{
		List<Ics> listIcs = new ArrayList<Ics>();
		
		for (BigInteger bi : services)
		{
			listIcs.addAll(icsDao.getByService(bi.intValue()));
		}
	
		return checkScr(listIcs, map);
	}
	
	private List<Result> checkScr(List<Ics> listIcs, Map<String, String[]> map)
	{
		String condition = new String();
		List<Result> listResult = new ArrayList<Result>();
		boolean b = false;
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		
		for (Ics ics : listIcs)
		{
			b = Boolean.parseBoolean(map.get("data["+ics.getName()+"]")[0]);
			
			try
			{
				if (b) engine.eval(ics.getName()+" = 1");
				else engine.eval(ics.getName()+" = 0");
			}
			catch (ScriptException e)
			{
				e.printStackTrace();
			}
		}
		
		for (Ics ics : listIcs)
		{
			condition = ics.getScrExpression();
			
			Result r = new Result(ics.getId(), ics.getServiceGroup(), true);
			if ((condition!=null)&&(!condition.isEmpty()))
			{
				try
				{
					r.setResult((Integer)engine.eval(condition)==1.0);
				}
				catch (ScriptException e)
				{
					e.printStackTrace();
				}
			}
			
			listResult.add(r);
		}
		
		return listResult;
	}

	@Override
	@Transactional
	public List<Ics> getService(int idService)
	{
		return icsDao.getByService(idService);
	}

	@Override
	public List<String> pdfData(String configuration)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator=": ";
		List<String> listString = new ArrayList<String>();
		
		try
		{
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Ics/Value";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/Ics/Name";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("2. Configured ICS");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
			    listString.add(nodeList2.item(i).getFirstChild().getNodeValue()+separator
			    		+nodeList.item(i).getFirstChild().getNodeValue());
			}
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		
		return listString;
	}

	@Override
	@Transactional
	public String add(Ics newIcs)
	{
		String stringToReturn = null;
		Ics existingIcs = icsDao.get(newIcs.getName());
		
		if (existingIcs != null)
		{
			existingIcs.setName(newIcs.getName());
			existingIcs.setScrExpression(newIcs.getScrExpression());
			existingIcs.setServiceGroup(newIcs.getServiceGroup());
			existingIcs.setValue(false);
			existingIcs.setDescription(newIcs.getDescription());
			
			stringToReturn = String.format("%s already existed. It was successfully updated", newIcs.getName());
		}
		else
		{
			icsDao.add(newIcs);
			stringToReturn = String.format("%s successfully added to database", newIcs.getName());
		}

		return stringToReturn;
	}
}
/*******************************************************************************
 *  *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
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
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCase;

@Service
public class TestCaseServiceImpl implements TestCaseService
{
	@Autowired
	private ProjectService projectService;
	
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
	public List<TestCase> load(Project p, Map<String,String[]> map)
	{
		String type = p.getType();
		List<Ics> listIcs = new ArrayList<Ics>();
		List<TestCase> listTC = new ArrayList<TestCase>();
		
		for (BigInteger bi : projectService.getServicesData(p.getIdProject()))
		{
			listIcs.addAll(icsDao.getByService(bi.intValue()));
			
			if (bi.intValue() == 3)
			{
				listIcs.addAll(icsDao.getByService(5)); //JTF: Change to a generic way
			}
			List<BigInteger> intList = crDao.getTestCasesByID(p.getIdCertrel());
			
			if (type.equals("Development") || type.equals("Conformance and Interoperability"))
			{
				listTC.addAll(tcDao.getServiceWithRestriction("Conformance", bi, intList));
				listTC.addAll(tcDao.getServiceWithRestriction("Interoperability", bi, intList));
			}
			else
			{
				listTC.addAll(tcDao.getServiceWithRestriction(type, bi, intList));
			}
		}
		return checkApplicability(p, listTC, listIcs, map);
	}
	
	private List<TestCase> checkApplicability(Project p, List<TestCase> listTC, List<Ics> listIcs, 
			Map<String, String[]> map)
	{
		String condition = new String();
		List<TestCase> applyTC = new ArrayList<TestCase>();
		boolean b = false;
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		boolean setIcs = map.containsKey("data[setIcs]") ? Boolean.parseBoolean(map.get("data[setIcs]")[0]) : false;
		List<String> configuredIcs = null;
		
		if (!setIcs)
		{
			configuredIcs = loadFromXml(p);
		}
		
		for (Ics i : listIcs)
		{
			if (setIcs)
			{
				if (map.containsKey("data[" + i.getName() + "]"))
				{
					b = Boolean.parseBoolean(map.get("data[" + i.getName() + "]")[0]);
					
					try
					{
						if (b) engine.eval(i.getName() + " = 1");
						else engine.eval(i.getName() + " = 0");
					}
					catch (ScriptException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					try
					{
						engine.eval(i.getName() + " = 0");
					}
					catch (ScriptException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				int state = configuredIcs.contains(i.getName()) ? 1 : 0;
				try
				{
					engine.eval(i.getName() + " = " + state);
				}
				catch (ScriptException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		for (TestCase tc : listTC)
		{
			condition = tc.getApplicability();

			if (!condition.isEmpty())
			{
				try
				{
					if ((Integer)engine.eval(condition)==1.0)
					{
						applyTC.add(tc);
					}
				}
				catch (ScriptException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				applyTC.add(tc);
			}
		}
		return applyTC;
	}
	
	private List<String> loadFromXml(Project p)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		List<String> listEnabledIcs = new ArrayList<String>();
		
		try
		{
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(p.getConfiguration()));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Ics/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			String expression2 = "/Project/Ics/Value";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				if (Boolean.parseBoolean(nodeList2.item(i).getFirstChild().getNodeValue()))
				{
					listEnabledIcs.add(nodeList.item(i).getFirstChild().getNodeValue());
				}
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
		
		return listEnabledIcs;
	}

	@Override
	@Transactional
	public List<TestCase> getService(String type, BigInteger idService)
	{
		return tcDao.getByTypeAndService(type, idService);
	}

	@Override
	@Transactional
	public List<TestCase> list() {
		return tcDao.list();
	}

	@Override
	@Transactional
	public List<TestCase> list(int idCertRel) {
		return tcDao.listByCertRel(idCertRel);
	}
	
	@Override
	@Transactional
	public List<Integer> getEnabled(int idTccl) {
		return tcclDao.getIds(idTccl);
	}
	
	@Override
	@Transactional
	public List<BigInteger> getDisabled(int idTccl)
	{
		return tcclDao.getIdsDisabled(idTccl);
	}

	@Override
	public List<String> pdfData(String configuration, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator = ": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/TestCase/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/ApplyTC";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("5. Test Cases and Results");
			listString.add("Executed "+nodeList.getLength()+" of "+nodeList2.item(0).getFirstChild().getNodeValue()+ " applicable testcases");
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
	
	public String lastExecution(String tcName, String results) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String r = "";
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(results));
			
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
		List<String> listString = new ArrayList<String>();
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
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
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(results));
			
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
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
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

	@Override
	public List<Integer> getConfigured(Project p)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		List<Integer> configuredTc = new ArrayList<Integer>();
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(p.getConfiguration()));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/TestCase/Id";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				configuredTc.add(Integer.parseInt(nodeList.item(i).getFirstChild().getNodeValue()));
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
		
		return configuredTc;
	}

	@Override
	@Transactional
	public String add(TestCase testCase)
	{
		String name = testCase.getName();
		for (TestCase tc : tcDao.list())
		{
			if (tc.getName().equals(name))
			{
				testCase.setIdTC(tc.getIdTC());
				tcDao.update(testCase);
				return String.format("%s already existed. It was successfully updated", name);
			}
		}
		
		tcDao.add(testCase);
		
		return String.format("%s successfully added to database", testCase.getName());
	}
}
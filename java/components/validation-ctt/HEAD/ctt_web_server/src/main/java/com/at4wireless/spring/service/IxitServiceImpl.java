/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.at4wireless.spring.dao.IxitDAO;
import com.at4wireless.spring.model.Ixit;

@Service
public class IxitServiceImpl implements IxitService
{
	@Autowired
	private IxitDAO ixitDao;
	
	static final Logger log = LogManager.getLogger(IxitServiceImpl.class);
	
	@Override
	//@Transactional
	public List<Ixit> load(List<BigInteger> services, boolean isConfigured,
			String configuration)
	{
		List<Ixit> listIxit = new ArrayList<Ixit>();
		
		// First of all, load all available IXIT for supported services
		for (BigInteger bi : services)
		{
			listIxit.addAll(ixitDao.getByService(bi.intValue()));
		}
		
		// if the project is already configured, update the values of the IXIT to return with the configured values
		if (isConfigured)
		{			
			XMLManager xmlManager = new XMLManager();
			// load the IDs and values
			List<String> listOfIxitIds = xmlManager.retrieveNodeValuesFromFile(configuration, "/Project/Ixit/Id");
			List<String> listOfIxitValues = xmlManager.retrieveNodeValuesFromFile(configuration, "/Project/Ixit/Value");
			// convert to a map			
			Iterator<String> i1 = listOfIxitIds.iterator();
			Iterator<String> i2 = listOfIxitValues.iterator();
			Map<Integer, String> ixitMap = new HashMap<Integer, String>();
			
			while (i1.hasNext() && i2.hasNext())
			{
			    ixitMap.put(Integer.parseInt(i1.next()), i2.next());
			}
			
			if (i1.hasNext() || i2.hasNext())
			{
				// if lists have different size and error occurred
				log.error(String.format("The number of Id nodes (%d) and Value nodes (%d) is not the same", 
						listOfIxitIds.size(), listOfIxitValues.size()));
			}
			else
			{
				// else, load all stored values
				for (Ixit ixit : listIxit)
				{
					if (ixitMap.get(ixit.getIdIxit()) != null)
					{
						ixit.setValue(ixitMap.get(ixit.getIdIxit()));
					}
				}
			}
		}
		
		return listIxit;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ixit> getService(int idService)
	{
		return ixitDao.getByService(idService);
	}
	
	@Override
	public List<String> pdfData(String configuration)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator=": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Ixit/Value";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/Ixit/Name";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("3. Configured IXIT");
			for (int i = 0; i < nodeList2.getLength(); i++) {
				if(nodeList.item(i).getFirstChild()!=null) {
				    listString.add(nodeList2.item(i).getFirstChild().getNodeValue()+separator
				    		+nodeList.item(i).getFirstChild().getNodeValue());
				} else {
					listString.add(nodeList2.item(i).getFirstChild().getNodeValue()+separator
				    		+"");
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

	@Override
	@Transactional
	public String add(Ixit newIxit)
	{
		String stringToReturn = null;
		Ixit existingIxit = ixitDao.get(newIxit.getName());
		
		if (existingIxit != null)
		{
			existingIxit.setName(newIxit.getName());
			existingIxit.setServiceGroup(newIxit.getServiceGroup());
			existingIxit.setValue(newIxit.getValue());
			existingIxit.setDescription(newIxit.getDescription());
			
			stringToReturn = String.format("%s already existed. It was successfully updated", newIxit.getName());
		}
		else
		{
			ixitDao.add(newIxit);
			
			stringToReturn = String.format("%s successfully added to database", newIxit.getName());
		}
		
		return stringToReturn;
	}
}
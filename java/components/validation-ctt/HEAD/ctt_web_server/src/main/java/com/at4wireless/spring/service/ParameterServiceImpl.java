/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.dao.ParameterDAO;
import com.at4wireless.spring.model.Parameter;

@Service
public class ParameterServiceImpl implements ParameterService {

	@Autowired
	private ParameterDAO parameterDao;
	
	@Override
	//@Transactional
	public List<Parameter> list() {
		return parameterDao.list();
	}
	
	@Override
	//@Transactional
	public List<Parameter> load(boolean isConfigured, String configuration) {
		List<Parameter> listParameter = parameterDao.list();
		
		if(isConfigured) {			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			
			try {
				builder = builderFactory.newDocumentBuilder();
				Document source = builder.parse(new File(configuration));
				
				XPath xPath = XPathFactory.newInstance().newXPath();	
				String expression = "/Project/Parameter/Value";
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					if(nodeList.item(i).getFirstChild()!=null) {
						listParameter.get(i).setValue(nodeList.item(i).getFirstChild().getNodeValue());
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
		
		return listParameter;
	}
	
	@Override
	public List<String> pdfData(String configuration) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String separator=": ";
		List<String> listString = new ArrayList<String>();
		
		try {
			builder = builderFactory.newDocumentBuilder();
			Document source = builder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Parameter/Value";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/Parameter/Name";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("4. Configured General Parameters");
			for (int i = 0; i < nodeList.getLength(); i++) {
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

}
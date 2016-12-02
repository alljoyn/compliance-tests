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
package com.at4wireless.spring.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLManager
{
	private DocumentBuilderFactory builderFactory;
	private DocumentBuilder builder;
	
	public XMLManager()
	{
		builderFactory = DocumentBuilderFactory.newInstance();
		
		try
		{
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Document stringToDocument(String xmlString)
	{
		Document parsedXml = null;
		
		try
		{
			parsedXml = builder.parse(new InputSource(new StringReader(xmlString)));
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
		
		return parsedXml;
	}
	
	public Document fileToDocument(String filePath)
	{
		Document parsedXml = null;
		
		try
		{
			parsedXml = builder.parse(new FileInputStream(filePath));
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
		
		return parsedXml;
	}
	
	public List<String> retrieveNodeValuesFromXMLString(String xmlString, String nodePath)
	{
		List<String> listOfValues = new ArrayList<String>();
		Document xmlDocument = stringToDocument(xmlString);
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		
		try
		{
			nodeList = (NodeList) xPath.compile(nodePath).evaluate(xmlDocument, XPathConstants.NODESET);
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			listOfValues.add(nodeList.item(i).getFirstChild().getNodeValue());
		}
		
		return listOfValues;
	}
	
	public List<String> retrieveNodeValuesFromFile(String filePath, String nodePath)
	{
		
		Document xmlDocument = fileToDocument(filePath);
		
		return getNodeSetFromDocument(xmlDocument, nodePath);
	}
	
	private List<String> getNodeSetFromDocument(Document xmlDocument, String nodePath)
	{
		NodeList nodeList = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		List<String> listOfValues = new ArrayList<String>();
		
		try
		{
			nodeList = (NodeList) xPath.compile(nodePath).evaluate(xmlDocument, XPathConstants.NODESET);
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			listOfValues.add(nodeList.item(i).getFirstChild() != null ? nodeList.item(i).getFirstChild().getNodeValue() : "");
		}
		
		return listOfValues;
	}
	
	public int getNodeListLengthFromXMLString(String xmlString, String nodePath)
	{
		Document xmlDocument = stringToDocument(xmlString);
		XPath xPath = XPathFactory.newInstance().newXPath();
		int xmlNodeLength = 0;
		
		try
		{
			xmlNodeLength = ((NodeList) xPath.compile(nodePath).evaluate(xmlDocument, XPathConstants.NODESET)).getLength();
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlNodeLength;
	}
	
	public void removeNodesFromDocument(Document xmlDocument, String nodePath)
	{
		NodeList nodeList = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		try
		{
			nodeList = (NodeList) xPath.compile(nodePath).evaluate(xmlDocument, XPathConstants.NODESET);
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = nodeList.getLength() - 1; i >= 0; i--)
		{
			Node nodeToDelete = nodeList.item(i);
			removeChildNode(nodeToDelete.getParentNode(), nodeToDelete);
		}
	}
	
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
	
	public void addNodeToDocument(Document xmlDocument, String nodeName, List<String> listOfChildNodesNames, List<String> listOfChildNodesValues)
	{			
		Node parentNode = xmlDocument.getFirstChild();
		
		Element node = xmlDocument.createElement(nodeName);
		
		for (int i = 0; i < listOfChildNodesNames.size(); i++)
		{
			Element childNode = xmlDocument.createElement(listOfChildNodesNames.get(i));
			childNode.appendChild(xmlDocument.createTextNode(listOfChildNodesValues.get(i)));
			node.appendChild(childNode);
		}
		
		parentNode.appendChild(node);
	}
	
	public void saveDocumentToFile(Document xmlDocument, String filePath)
	{
		Transformer transformer;
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			Source input = new DOMSource(xmlDocument);

			try
			{
				transformer.transform(input, new StreamResult(new File(filePath)));
			}
			catch (TransformerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	}
	
	public void replaceNodeValueByName(Document xmlDocument, String nodePath, String nodeName, String nodeNewValue)
	{
		Node node = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		try
		{
			String expression = String.format("%s[Name = \"%s\"]/Value", nodePath, nodeName);
			node = (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
			
			if (node.getFirstChild() != null)
			{
				node.getFirstChild().setNodeValue(nodeNewValue);
			}
			else
			{
				node.appendChild(xmlDocument.createTextNode(nodeNewValue));
			}
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
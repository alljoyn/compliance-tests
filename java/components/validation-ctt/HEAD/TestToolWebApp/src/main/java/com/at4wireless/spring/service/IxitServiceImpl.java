package com.at4wireless.spring.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
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
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.dao.IxitDAO;
import com.at4wireless.spring.model.Ixit;

@Service
public class IxitServiceImpl implements IxitService {
	@Autowired
	private IxitDAO ixitDao;
	
	@Override
	//@Transactional
	public List<Ixit> load(List<BigInteger> services, boolean isConfigured,
			String configuration) {
		List<Ixit> listIxit = new ArrayList<Ixit>();
		
		for (BigInteger bi : services) {
			listIxit.addAll(ixitDao.getService(bi.intValue()));
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
				String expression = "/Project/Ixit/Value";
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
				
				//If true, set ICS (default load is false)
				for (int i = 0; i < nodeList.getLength(); i++) {
					if(nodeList.item(i).getFirstChild()!=null) {
						listIxit.get(i).setValue(nodeList.item(i).getFirstChild().getNodeValue());
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
		
		return listIxit;
	}

	@Override
	@Transactional
	public List<Ixit> getService(int idService) {
		return ixitDao.getService(idService);
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
			String expression = "/Project/Ixit/Value";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			
			String expression2 = "/Project/Ixit/Name";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			listString.add("====================");
			listString.add("CONFIGURED IXIT");
			listString.add("====================");
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

}

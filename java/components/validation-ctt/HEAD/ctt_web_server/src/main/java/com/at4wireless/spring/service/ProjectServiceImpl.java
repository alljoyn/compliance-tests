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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.DutDAO;
import com.at4wireless.spring.dao.GoldenUnitDAO;
import com.at4wireless.spring.dao.ProjectDAO;
import com.at4wireless.spring.dao.SampleDAO;
import com.at4wireless.spring.dao.ServiceFrameworkDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.model.Tccl;

@Service
public class ProjectServiceImpl implements ProjectService
{	
	@Autowired
	private ProjectDAO projectDao;
	@Autowired
	private CertificationReleaseDAO crDao;
	@Autowired
	private DutDAO dutDao;
	@Autowired
	private GoldenUnitDAO guDao;
	@Autowired
	private SampleDAO sampleDao;
	@Autowired
	private TcclDAO tcclDao;
	@Autowired
	private ServiceFrameworkDAO serviceDao;
	
	static final Logger log = LogManager.getLogger(ProjectServiceImpl.class);
	
	@Override
	@Transactional
	public Project create(Project p)
	{
		java.sql.Timestamp date = new java.sql.Timestamp(
				new java.util.Date().getTime());

		p.setCreatedDate(date);
		p.setModifiedDate(date);

		if (projectDao.getByName(p.getUser(), p.getName()) != null)
		{
			return null;
		}
		else
		{
			projectDao.add(p);
			checkTcclContent(p);
			parseSupportedServices(p);
			parseGoldenUnits(p);
			return p;
		}
	}
	
	private void checkTcclContent(Project p)
	{
		if ((!p.getType().equals("Development")) && (tcclDao.getTcclName(p.getIdTccl()) == null))
		{
			if (tcclDao.getNumber(crDao.getByID(p.getIdCertrel()).getName().substring(1)) > 0)
			{
				List<Tccl> listAvailableTccl = tcclDao.listByCR(p.getIdCertrel());
				p.setIdTccl(listAvailableTccl.get(listAvailableTccl.size() - 1).getIdTccl());
				p.setIsConfigured(true);
			}
			else
			{
				p.setIdTccl(0);
				p.setIsConfigured(false);
			}
		}
	}
	
	private void parseSupportedServices(Project p)
	{
		List<String> sList = serviceDao.getServicesByName(p.getIdProject());
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < sList.size(); i++)
		{
			str.append(sList.get(i));
			
			if (i != (sList.size() - 1))
			{
				str.append(", ");
			}
		}
		p.setSupportedServices(str.toString());
	}
	
	private void parseGoldenUnits(Project p)
	{
		List<String> sList = projectDao.getGoldenUnitsByProjectID(p.getIdProject());
		if (!sList.isEmpty())
		{
			StringBuilder str= new StringBuilder();
			
			for (int i = 0; i < sList.size(); i++)
			{
				str.append(sList.get(i));
				
				if (i != (sList.size() - 1))
				{
					str.append(", ");
				}
			}
			p.setgUnits(str.toString());
		}
		else
		{
			if (p.getType().equals("Conformance"))
			{
				p.setgUnits("N/A");
			}
			else
			{
				p.setgUnits("Not selected");
			}
		}
	}
	
	@Override
	@Transactional
	public List<Project> getTableData(String username)
	{
		List<Project> projectList = projectDao.list(username);
		
		for (Project p : projectList)
		{
			checkTcclContent(p);
			parseSupportedServices(p);
			parseGoldenUnits(p);	
		}
		
		return projectList;
	}
	
	@Override
	@Transactional
	public Project getFormData(String username, int idProject)
	{
		Project p = projectDao.getByID(username,idProject);
		
		log.trace(guDao.listByProjectID(p.getIdProject()).size());

		List<BigInteger> biList = serviceDao.getServices(p.getIdProject());
		
		StringBuilder str = new StringBuilder();
		
		for (BigInteger bi : biList)
		{
			str.append(bi.toString() + ".");
		}
			
		p.setSupportedServices(str.toString());

		return p;
	}
	
	@Override
	@Transactional
	public Project update(Project p)
	{
		Project saved = projectDao.getByID(p.getUser(), p.getIdProject());
		
		if (saved != null)
		{
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
			p.setModifiedDate(date);
			String[] var = p.getSupportedServices().split("[\\.]+");
			List<BigInteger> listServices = serviceDao.getServices(saved.getIdProject());
			boolean equals = (var.length == listServices.size());
			int i = 0;
			
			while ((equals) && (i < var.length))
			{
				equals = (Integer.parseInt(var[i]) == listServices.get(i).intValue());
				i++;
			}
			
			projectDao.update(p);

			checkTcclContent(p);
			parseSupportedServices(p);
			parseGoldenUnits(p);
			p.setIdDut(saved.getIdDut());
			p.setIsConfigured(saved.isIsConfigured());
			p.setResults(saved.getResults());
			
			return p;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	@Transactional
	public boolean delete(String username, int idProject)
	{
		Project p = projectDao.getByID(username, idProject);
		
		if (p != null)
		{
			try
			{
				String cfg = p.getConfiguration();
				if (cfg != null)
				{
					File filePath = new File(cfg.substring(0, cfg.lastIndexOf(File.separator)));
					
					if (filePath.isDirectory())
					{
						File files[] = filePath.listFiles();
						
						for (int i = 0; i < files.length; i++)
						{
							if (files[i].exists())
							{
								files[i].delete();
							}
						}
						filePath.delete();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			projectDao.delete(idProject);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	@Transactional
	public void configProject(int idProject, String url)
	{
		projectDao.setConfig(idProject, url);
	}

	@Override
	@Transactional
	public void resultProject(int idProject, String url)
	{
		projectDao.setResults(idProject, url);
	}

	@Override
	@Transactional
	public String setDut(String username, Project project)
	{
		Project p = projectDao.getByID(username, project.getIdProject());
		
		if (project.getIdDut() != 0)
		{
			if ((p.getIdDut() != 0) && (p.isIsConfigured()) && (project.getIdDut() != p.getIdDut()))
			{
				/*try
				{
					String cfg = p.getConfiguration();
					
					if (cfg != null)
					{
						File fileTemp = new File(cfg);
						if (fileTemp.exists())
						{
							fileTemp.delete();
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				projectDao.configProject(Integer.toString(project.getIdProject()), null);*/
				modifyIxit(p.getConfiguration(), dutDao.get(p.getIdDut(), username));
			}
			projectDao.setDut(project);
		}
		return p.getType();
	}
	
	private void modifyIxit(String configuration, Dut dut)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		Sample s = sampleDao.list(dut.getIdDut()).get(0);
		
		Document source = null;
		
		try {
			builder = builderFactory.newDocumentBuilder();
			source = builder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();	
			String expression = "/Project/Ixit/Name";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
			String expression2 = "/Project/Ixit/Value";
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(source, XPathConstants.NODESET);
			
			
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node currentNode = nodeList.item(i).getFirstChild();
				Node currentNode2 = nodeList2.item(i).getFirstChild();
				
				if (currentNode.getNodeValue().equals("IXITCO_AppId"))
				{
					currentNode2.setNodeValue(s.getAppId());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_DeviceName"))
				{
					currentNode2.setNodeValue(dut.getName());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_DeviceId"))
				{
					currentNode2.setNodeValue(s.getDeviceId());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_Manufacturer"))
				{
					currentNode2.setNodeValue(dut.getManufacturer());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_ModelNumber"))
				{
					currentNode2.setNodeValue(dut.getModel());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_SoftwareVersion"))
				{
					currentNode2.setNodeValue(s.getSwVer());
				}
				else if (currentNode.getNodeValue().equals("IXITCO_HardwareVersion"))
				{
					String value;
					
					try
					{
						value = s.getHwVer();
					}
					catch (NullPointerException e)
					{
						value = "";
					}

					if (currentNode2 != null)
					{
						currentNode2.setNodeValue(value);
					}
					else
					{
						Element node = source.createElement("Value");
						node.appendChild(source.createTextNode(value));
						currentNode2 = node;
					}
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
		
		saveXml(source, configuration);
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

	@Override
	@Transactional(rollbackFor={Exception.class})
	public void setGu(String username, Project project)
	{
		
		try
		{
			projectDao.unassignGoldenUnits(project.getIdProject());
			
			if (!project.getgUnits().isEmpty())
			{
				projectDao.assignGoldenUnits(project);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	@Override
	@Transactional
	public List<RestProject> getRestData(String username)
	{
		List<RestProject> listRestProject = new ArrayList<RestProject>();
		
		for (Project p : getTableData(username))
		{
			RestProject rp = new RestProject();
			
			rp.setIdProject(p.getIdProject());
			rp.setName(p.getName());
			rp.setCreatedDate(p.getCreatedDate());
			rp.setModifiedDate(p.getModifiedDate());
			rp.setType(p.getType());
			rp.setSupportedServices(p.getSupportedServices());
			
			for (CertificationRelease cr : crDao.list())
			{
				if (p.getIdCertrel() == cr.getIdCertrel())
				{
					rp.setCertRel(cr.getName());
				}
			}
			
			rp.setIsConfigured(p.isIsConfigured());
			rp.setHasResults(p.isHasResults());
			
			if (p.getIdDut() != 0)
			{
				rp.setDut(dutDao.get(p.getIdDut(), username).getName());
			}
			rp.setGolden(p.getgUnits());
			
			listRestProject.add(rp);
		}
		return listRestProject;
	}
	
	@Override
	@Transactional
	public List<Project> list(String username)
	{
		return projectDao.list(username);
	}

	@Override
	@Transactional
	public boolean clearConfigByDut(String username, int idDut)
	{
		List<Project> lp = projectDao.getByDUT(username, idDut);
		
		if (lp != null)
		{
			for (Project p : projectDao.getByDUT(username, idDut))
			{
				if (p.isIsConfigured())
				{	
					try
					{
						String cfg = p.getConfiguration();
						
						if (cfg != null)
						{
							File fileTemp = new File(cfg);
							
							if (fileTemp.exists())
							{
								fileTemp.delete();
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					projectDao.setConfig(p.getIdProject(), null);
				}
				Project p2 = new Project();
				p2.setIdProject(p.getIdProject());
				p2.setIdDut(0);
				projectDao.setDut(p2);
			}
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public List<BigInteger> getServicesData(int idProject)
	{
		return serviceDao.getServices(idProject);
	}
	
	@Override
	@Transactional
	public List<String> pdfData(String username, int idProject)
	{
		Project p = projectDao.getByID(username,idProject);
		List<String> sList = serviceDao.getServicesByName(p.getIdProject());
		
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < sList.size(); i++)
		{
			str.append(sList.get(i));
			
			if (i < (sList.size() - 1))
			{
				str.append(", ");
			}
		}
		p.setSupportedServices(str.toString());

		String separator = ": ";
		List<String> listString = new ArrayList<String>();
		listString.add("1. Project Details");
		listString.add("Project Name"+separator+p.getName());
		listString.add("User Name"+separator+p.getUser());
		java.util.Date date = new java.util.Date();
		listString.add("Test Report Creation Date and Time"+separator+date.toString());
		listString.add("Certification Application Request ID"+separator+p.getCarId());
		listString.add("Type of Project"+separator+p.getType());
		listString.add("Supported Services"+separator+p.getSupportedServices());
		listString.add("Certification Release Version"+separator+crDao.getByID(p.getIdCertrel()).getName()
				+" ("+crDao.getDescription(crDao.getByID(p.getIdCertrel()).getName())+")");
		listString.add("TCCL Version"+separator+tcclDao.getTcclName(p.getIdTccl()));
		
		return listString;
		
	}

	@Override
	@Transactional
	public void testReport(int idProject, String path)
	{
		projectDao.setTestReport(idProject, path);
	}

	@Override
	@Transactional
	public boolean exists(String username, String name, int id)
	{
		Project p = projectDao.getByName(username, name);
		
		if (p == null)
		{
			return false;
		}
		else if (p.getIdProject() == id)
		{
			return false;
		}
		return true;
	}
}
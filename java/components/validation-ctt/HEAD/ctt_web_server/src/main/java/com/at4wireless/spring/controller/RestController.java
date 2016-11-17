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

package com.at4wireless.spring.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.common.XMLManager;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.TestCaseService;
import com.at4wireless.spring.service.UserService;

/**
 * This class manages all actions related with REST 
 * 
 */
@Controller
@RequestMapping(value = "/rest")
public class RestController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private DutService dutService;
	@Autowired
	private TestCaseService tcService;
	@Autowired
	private UserService userService;
	
	private static final String USERS_PATH = File.separator + "Allseen"
			+File.separator+"Users"+File.separator;
	
	static final Logger log = LogManager.getLogger(RestController.class);
	
	@RequestMapping(value = "/keyExchange", method = RequestMethod.POST,
			produces = "application/json; charset=utf-8")
	public @ResponseBody String keyExchange(@RequestParam("user") String user, @RequestParam("publicKey") String publicKey)
	{
		return userService.keyExchange(user, publicKey);
	}
	
	/**
	 * Sends the list of projects of a certain user
	 * 
	 * @param user
	 * 			user whose projects are going to be sent
	 * 
	 * @return project list
	 */
	@RequestMapping(value = "/getList/{encodedUser}", method = RequestMethod.GET)
	public @ResponseBody List<RestProject> getList(@PathVariable String encodedUser)
	{
		;
		return projectService.getRestData(new String(DatatypeConverter.parseBase64Binary(encodedUser)));
	}
	
	/**
	 * Checks if a technology package is updated
	 * 
	 * @param technology
	 * 			certification release version
	 * 
	 * @return true if updated, false and release number otherwise
	 */
	@RequestMapping(value = "/isLastTechnologyVersion/{certificationReleaseWithPackageVersion}", method = RequestMethod.GET)
	public @ResponseBody String isLastTechnologyVersion(@PathVariable String certificationReleaseWithPackageVersion)
	{
		if (certificationReleaseWithPackageVersion.matches("v[\\d]+_[\\d]+_[\\d]+[a-z]?_[RD][\\d]+"))
		{
			String[] str = certificationReleaseWithPackageVersion.split("_");
			File releasesFolder = new File(File.separator + "Allseen" + File.separator + "Technology");
			File[] listOfPackages = releasesFolder.listFiles();
			String releaseToFind = str[0] + "." + str[1] + "." + str[2];
			String packageWithHighestVersion = "TestCases_Package_" + releaseToFind + "_" + str[3] + ".jar";
			
			for (File currentPackage : listOfPackages)
			{
				if (currentPackage.getName().toLowerCase().contains(releaseToFind.toLowerCase() + "_"))
				{
					if ((currentPackage.getName().split("_")[3].charAt(0) == 'R') && (packageWithHighestVersion.split("_")[3].charAt(0) == 'D'))
					{
						packageWithHighestVersion = currentPackage.getName();
					}
					else if (((currentPackage.getName().split("_")[3].charAt(0) == 'R') && (packageWithHighestVersion.split("_")[3].charAt(0) == 'R')) 
							|| ((currentPackage.getName().split("_")[3].charAt(0) == 'D') && (packageWithHighestVersion.split("_")[3].charAt(0) == 'D')))
					{
						String resultOfComparison = compare(packageWithHighestVersion.split("_")[3], currentPackage.getName().split("_")[3]);
						
						if (resultOfComparison.equals("higher"))
						{
							packageWithHighestVersion = currentPackage.getName();
						}
					}
				}
			}
			
			if (packageWithHighestVersion.equals("TestCases_Package_" + releaseToFind + "_" + str[3] + ".jar"))
			{
				return "true";
			}
			else
			{
				return "false, " + packageWithHighestVersion.replaceAll("\\.", "_").split("_")[5];
			}
		}
		
		return "bad request";
	}
	
	/**
	 * Sends a technology package
	 * 
	 * @param technology
	 * 			requested package
	 * @param response
	 * 			servlet response with the requested file
	 */
	@RequestMapping(value = "/getTechnology/{certificationReleaseAndPackageVersion}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody void getTechnology(@PathVariable String certificationReleaseAndPackageVersion, HttpServletResponse response)
	{
		String[] splitted = certificationReleaseAndPackageVersion.split("_");
		String formattedCertificationReleaseWithPackageVersion = splitted[0] + "." + splitted[1] + "." + splitted[2] + "_" + splitted[3];
		String fullPath = File.separator + "Allseen" + File.separator + "Technology" + File.separator +
				"TestCases_Package_" + formattedCertificationReleaseWithPackageVersion + ".jar";
	
		try
		{
			File fileToBeSent = new File(fullPath);
		    InputStream is = new FileInputStream(fileToBeSent);
		    response.setHeader("Content-Disposition", "attachment; filename=" + formattedCertificationReleaseWithPackageVersion + ".jar");
		    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		    response.flushBuffer();
		    is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends all information related to a project
	 * 
	 * @param user
	 * 			user
	 * @param id
	 * 			project id
	 * 
	 * @return project configuration file
	 * @throws IOException if file does not exist
	 */
	@RequestMapping(value="/getProject/{encodedUser}/{id}", method = RequestMethod.GET)
	public @ResponseBody String getProject(@PathVariable String encodedUser, @PathVariable int id) throws IOException
	{
		String config = null;
		JSONObject xmlJSONObj = null;
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		
		Project p = projectService.getFormData(user, id);
		
		if (p.isHasResults())
		{
			appendLastExecution(p.getConfiguration(), p.getResults());
		}
		
		// Update samples to be sure that sample list has the newest values
		updateSamples(p.getIdDut(), p.getConfiguration());
		
		config = readFile(p.getConfiguration(), StandardCharsets.UTF_8);
		
		try
		{
			xmlJSONObj = XML.toJSONObject(config);
		}
		catch (JSONException e)
		{
			// Error while creating JSON
		}
		return xmlJSONObj.toString();
	}
	
	/**
	 * Writes last execution of Test Cases when configuration file is requested
	 * 
	 * @param 	configuration	configuration file path
	 * @param 	results			results file path
	 */
	private void appendLastExecution(String configuration, String results)
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		try
		{
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new FileInputStream(configuration));
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			
			String expression = "/Project/TestCase/Name";
			String expression2 = "/Project/TestCase/LastExec";
			String expression3 = "/Project/TestCase/LastVerdict";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(doc, XPathConstants.NODESET);
			NodeList nodeList3 = (NodeList) xPath.compile(expression3).evaluate(doc, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++)
			{
			    String str = tcService.lastExecution(nodeList.item(i).getFirstChild().getNodeValue(), results);
			    String str2[] = str.split(", ");
			    
			    if(str2.length==2)
			    {
			    	nodeList2.item(i).getFirstChild().setNodeValue(str2[0]);
			    	nodeList3.item(i).getFirstChild().setNodeValue(str2[1]);
			    }
			}
			
			Transformer transformer;
			try
			{
				transformer = TransformerFactory.newInstance().newTransformer();
				Source input = new DOMSource(doc);

				try
				{
					transformer.transform(input, new StreamResult(new File(configuration)));
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
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateSamples(int idDut, String configurationPath)
	{
		XMLManager xmlManager = new XMLManager();
		Document xmlDocument = xmlManager.fileToDocument(configurationPath);
		List<String> sampleNodes = Arrays.asList("Id", "DeviceId", "AppId", "swVer", "hwVer");
		
		xmlManager.removeNodesFromDocument(xmlDocument, "Project/Sample");
		
		for (Sample sample : dutService.getSampleData(idDut))
		{
			List<String> sampleValues = Arrays.asList(Integer.toString(sample.getIdSample()),
					sample.getDeviceId(), sample.getAppId(), sample.getSwVer(), sample.getHwVer());
			
			xmlManager.addNodeToDocument(xmlDocument, "Sample", sampleNodes, sampleValues);
		}
		
		xmlManager.saveDocumentToFile(xmlDocument, configurationPath);
	}
	
	/**
	 * Sends results of a certain project
	 * 
	 * @param 	user		user
	 * @param 	id			project id
	 * @return				results xml
	 * @throws 	IOException	if file does not exist
	 */
	@RequestMapping(value="/getResults/{encodedUser}/{id}", method = RequestMethod.GET)
	public @ResponseBody String getResults(@PathVariable String encodedUser, @PathVariable int id) throws IOException
	{
		String config = null;
		JSONObject xmlJSONObj = null;
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		
		config = readFile(projectService.getFormData(user, id).getResults(), StandardCharsets.UTF_8);
		
		try
		{
			xmlJSONObj = XML.toJSONObject(config);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmlJSONObj.toString();
	}
	
	
	/**
	 * Sends full log of a certain execution
	 * 
	 * @param 	user		user
	 * @param 	id			project id
	 * @param 	log			log file requested
	 * @return				log file content
	 * @throws IOException	if file does not exist
	 */
	@RequestMapping(value="/getFullLog/{encodedUser}/{id}/{log}", method = RequestMethod.GET)
	public @ResponseBody String getFullLog(@PathVariable String encodedUser, @PathVariable int id,
			@PathVariable String log) throws IOException
	{
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		String path = File.separator+"Allseen"+File.separator+"Users"+File.separator+
				user+File.separator+id+File.separator+log+".log";

		return readFile(path, StandardCharsets.UTF_8);
	}
	
	/**
	 * Receives the result of the execution of a testcase
	 * @param 	user		user
	 * @param 	id			project id
	 * @param 	requestBody	body of the servlet request
	 * @return				OK if processed, UNAUTHORIZED otherwise
	 */
	@RequestMapping(value="/sendResult/{encodedUser}/{id}", method = RequestMethod.POST,
			consumes = "application/xml")
	public ResponseEntity<String> sendResult(@PathVariable String encodedUser, @PathVariable int id,
			@RequestBody String requestBody)
	{
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		
		if (!requestBody.isEmpty())
		{
			for (Project p : projectService.list(user)) //change this for [AT4]
			{
				if (p.getIdProject() == id)
				{
					Writer writer = null;
					String url = File.separator+"Allseen"
							+File.separator+"Users"+File.separator+user+File.separator+id
							+File.separator+"result.xml"; //change this to a Config parameter [AT4]
	
					File f = new File(url);
					
					if (f.exists() & (!f.isDirectory()))
					{
						DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = null;
						DocumentBuilder builder2 = null;
						
						try
						{
							builder = builderFactory.newDocumentBuilder();
							builder2 = builderFactory.newDocumentBuilder();
						}
						catch (ParserConfigurationException e)
						{
							e.printStackTrace();
						}
						
						try
						{
							Document target = builder.parse(new FileInputStream(File.separator+"Allseen"
									+File.separator+"Users"+File.separator+user+File.separator+id
									+File.separator+"result.xml"));
							
							Document source = builder2.parse(new ByteArrayInputStream(requestBody.getBytes("utf-8")));
							
							XPath xPath = XPathFactory.newInstance().newXPath();
							
							String expression = "/Results/TestCase";
							NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
							
							for (int i = 0; i < nodeList.getLength(); i++)
							{
							    //Create a duplicate node and transfer ownership of the
							    //new node into the destination document
							    Node newNode = target.importNode(nodeList.item(i), true);
							    //Make the new node an actual item in the target document
							    target.getDocumentElement().appendChild(newNode);
							}
							
							Transformer transformer;
							try
							{
								transformer = TransformerFactory.newInstance().newTransformer();
								
								Result output = new StreamResult(new File(File.separator+"Allseen"
										+File.separator+"Users"+File.separator+user+File.separator+id
										+File.separator+"result.xml"));
								Source input = new DOMSource(target);
	
								try
								{
									transformer.transform(input, output);
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
					}
					else
					{
						try
						{
						    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url), "utf-8"));
						    writer.write(requestBody);
							
						    url = File.separator+File.separator+"Allseen"
									+File.separator+File.separator+"Users"+File.separator+File.separator
									+user+File.separator+File.separator+id
									+File.separator+File.separator+"result.xml";
						    projectService.resultProject(id, url);
						}
						catch (IOException ex)
						{
							  // report
						}
						finally
						{
						   try
						   {
						   	   //out.close();
							   writer.close();
						   }
						   catch (Exception ex) {}
						}
					}
					
					log.debug("XML created successfully");
					
					return new ResponseEntity<String>(
							"Handled application/xml request. Request body was: "
							+ requestBody,
							new HttpHeaders(),
							HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);			
	}
	
	/**
	 * Receives a log file
	 * 
	 * @param 	user	user
	 * @param	id		project id
	 * @param 	name	log file name
	 * @param 	file	file content
	 * @return			success or fail response
	 */
	@RequestMapping(value="/upload/{encodedUser}/{id}", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@PathVariable String encodedUser, @PathVariable int id,
			@RequestParam("name") String name, @RequestParam("file") String file,
			@RequestParam("hash") String hash)
	{
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		
		for (Project p : projectService.list(user))
		{
			if (p.getIdProject()==id)
			{
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				boolean existsOnXML = false;
				Document xmlDocument = null;
				Node found = null;
				
				try
				{
					builder = builderFactory.newDocumentBuilder();
				}
				catch (ParserConfigurationException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					xmlDocument = builder.parse(new FileInputStream(File.separator+"Allseen"
							+File.separator+"Users"+File.separator+user+File.separator+id
							+File.separator+"result.xml"));
					
					XPath xPath = XPathFactory.newInstance().newXPath();
					
					String expression = "/Results/TestCase/LogFile";
					NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

					for (int i = 0; i < nodeList.getLength(); i++)
					{
					    if(nodeList.item(i).getFirstChild().getNodeValue().equalsIgnoreCase(name))
					    {
					    	existsOnXML=true;
					    	found = nodeList.item(i);
					    }
					}
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
				
				if ((!file.isEmpty())&(existsOnXML))
				{		
		            try
		            {
		            	byte[] messageDigest = MessageDigest.getInstance("MD5").digest(file.getBytes());
		                
		                StringBuffer hexString = new StringBuffer();
		                
		                for (int i=0; i<messageDigest.length; i++)
		                {
		                	String hex = Integer.toHexString(0xFF & messageDigest[i]);
		                	if (hex.length() == 1)
		                	{
		                		hexString.append('0');
		                	}
		                	hexString.append(hex);
		                }
		                
		                if(hash.equalsIgnoreCase(hexString.toString()))
		                {
			                BufferedOutputStream stream =
			                        new BufferedOutputStream(new FileOutputStream(new File(File.separator+"Allseen"
			    							+File.separator+"Users"+File.separator+user+File.separator+id
			    							+File.separator+name)));
			                stream.write(file.getBytes());
			                stream.close();
			                return "You successfully uploaded " + name + "!";
		                }
		                else
		                {
		                	xmlDocument.getFirstChild().removeChild(found.getParentNode());
		                	Transformer transformer;
							
		                	try
		                	{
								transformer = TransformerFactory.newInstance().newTransformer();
								
								Result output = new StreamResult(new File(File.separator+"Allseen"
										+File.separator+"Users"+File.separator+user+File.separator+id
										+File.separator+"result.xml"));
								Source input = new DOMSource(xmlDocument);
	
								try
								{
									transformer.transform(input, output);
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
		                	return "You failed to upload "+name+". Wrong MD5.";
		                }
		            }
		            catch (Exception e)
		            {
		                return "You failed to upload " + name + " => " + e.getMessage();
		            }
		        }
				else
				{
		            return "You failed to upload " + name + " because the file was empty.";
		        }
			}
		}
		return "Bad user or projectId";
    }
	
	@RequestMapping(value="/uploadV2/{encodedUser}/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> handleFileUploadV2(@PathVariable String encodedUser, @PathVariable int id,
			@RequestParam("log-name") String log_name, @RequestParam("file") String file,
			@RequestParam("hash") String hash, @RequestParam("id-test") String id_test,
			@RequestParam("name") String name, @RequestParam("description") String description,
			@RequestParam("date-time") String date_time, @RequestParam("verdict") String verdict,
			@RequestParam("version") String version)
	{
		// Decode the user
		String user = new String(DatatypeConverter.parseBase64Binary(encodedUser));
		// Retrieve the target project
		Project p = projectService.getFormData(user, id);
		// If project exists and received parameters are not null...
		if ((p != null) && (log_name != null) && (file != null) && (hash != null)
				&& (name != null) && (date_time != null) && (verdict != null)
				&& (version != null) && (id_test != null))
		{
			// Calculate MD5 from file, to check if log has been received correctly
			byte[] messageDigest;
			StringBuffer hexString;
			try
			{
				messageDigest = MessageDigest.getInstance("MD5").digest(file.getBytes());
				
				hexString = new StringBuffer();
		        
		        for (int i = 0; i < messageDigest.length; i++)
		        {
		        	String hex = Integer.toHexString(0xFF & messageDigest[i]);
		        	if (hex.length() == 1)
		        	{
		        		hexString.append('0');
		        	}
		        	hexString.append(hex);
		        }
			} 
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
	        
	        
	        // If received MD5 is the same than calculated...
	        if (hash.equalsIgnoreCase(hexString.toString()))
	        {
	        	boolean newFile = false;
				String resultsXmlPath = USERS_PATH + user + File.separator + id + File.separator + "result.xml"; 
				File resultsXmlFile = new File(resultsXmlPath);
				// Check if target file exists. If not, create it.
				if (!resultsXmlFile.exists())
				{
					try
					{
						resultsXmlFile.createNewFile();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);	
					}
					
					newFile = true;
				}
				
				// Add new node with received data
				Document resultsXmlDocument;
				try
				{
					if (!newFile)
					{
						resultsXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
								.parse(new FileInputStream(resultsXmlPath));
					}
					else
					{
						resultsXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
					}
				}
				catch (SAXException | IOException | ParserConfigurationException | FactoryConfigurationError e)
				{
					e.printStackTrace();
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				if (newFile)
				{
					Element rootElement = resultsXmlDocument.createElement("Results");
					resultsXmlDocument.appendChild(rootElement);
				}
				
				List<String> nodeNames = new ArrayList<String>(Arrays.asList("Id", 
						"Name", "Description", "DateTime", "Verdict", "Version", "LogFile"));
				
				List<String> nodeValues = new ArrayList<String>(Arrays.asList(id_test,
						name, description, date_time, verdict, version, log_name));
				
				addNodesToDocument(nodeNames, nodeValues, resultsXmlDocument);
				
				Transformer transformer;
				try
				{
					transformer = TransformerFactory.newInstance().newTransformer();
				}
				catch (TransformerConfigurationException | TransformerFactoryConfigurationError e)
				{
					e.printStackTrace();
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);	
				}
				
				Result output = new StreamResult(new File(resultsXmlPath));
				Source input = new DOMSource(resultsXmlDocument);
				
				try
				{
					transformer.transform(input, output);
				}
				catch (TransformerException e)
				{
					e.printStackTrace();
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);	
				}
				
				projectService.resultProject(id, resultsXmlPath);
				
				// Store log
				BufferedOutputStream stream;
				try
				{
					stream = new BufferedOutputStream(new FileOutputStream(new File(USERS_PATH + user
							+ File.separator + id + File.separator + log_name)));
					stream.write(file.getBytes());
	                stream.close();
				}
				catch (Exception e)
				{
					// Here a deletion of xml info should be included
					e.printStackTrace();
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);	
				}
				
				return new ResponseEntity<String>(HttpStatus.OK);	
	        }
	        else
	        {
	        	return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);	
	        }
		}
		else
		{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
    }
		
	private void addNodesToDocument(List<String> nodeNames, List<String> nodeValues, Document document)
	{
		Element rootElement = document.createElement("TestCase");
		for (int i = 0; i < nodeNames.size(); i++)
		{
			Element node = document.createElement(nodeNames.get(i));
			node.appendChild(document.createTextNode(nodeValues.get(i)));
			rootElement.appendChild(node);
		}
		
		document.getDocumentElement().appendChild(rootElement);
	}
	
	
	/**
	 * Reads characters of an input file
	 * 
	 * @param 	path		location of the file
	 * @param 	encoding	encoding of the file
	 * @return				string with the content of the file
	 * @throws 	IOException	if file does not exist
	 */
	public static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	@RequestMapping(value="/existsRelease/{version}", method = RequestMethod.GET)
	public @ResponseBody String existsRelease(HttpServletResponse response,
			@PathVariable String version)
	{
		final String v = version.replaceAll("_", "\\.");
		File folder = new File(File.separator+"Allseen"+File.separator+"Technology");
		
		File[] files = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.contains(v);
			}
		});

		if (files.length == 0) 
			return version+" is not an existing certification release";
		else
		{
			for (File f : files)
			{
				if (f.getName().contains("_R"))
				{
					return "true";
				}
			}
			return "false";
		}
	}
	
	/**
	 * Sends last version of Local Agent
	 * 
	 * @param response	servlet response with the required file
	 */
	@RequestMapping(value="/getLastVersion", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	 public @ResponseBody void getLastVersion(HttpServletResponse response)
	{
		String lastVersion = lastUpload();
		String fullPath = File.separator + "Allseen" + File.separator + "localAgent" + File.separator +
					lastVersion;
			
		try
		{
			File f = new File(fullPath);
		    InputStream is = new FileInputStream(f);
		    response.setHeader("Content-Disposition", "attachment; filename="+lastVersion);
		    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		    response.flushBuffer();
		    is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the Local Agent in use is updated
	 * 
	 * @param 	v	local agent version
	 * @return		string with the result of the checking
	 */
	@RequestMapping(value="/isLastVersion/{v}", method=RequestMethod.GET)
	public @ResponseBody String isLastVersion(@PathVariable String v)
	{
		if (v.matches("\\d+_\\d+_\\d+"))
		{
			String highest = lastUpload();
			String result = compare(highest,"_"+v);
			
			if(result.equals("lower"))
			{
				return "new version available: "+highest.split("_")[3];
			}
			else if (result.equals("equal"))
			{
				return "version up to date";
			}
			else
			{
				return "lower version";
			}
		}
		return "bad request";
	}
	
	/**
	 * Returns the highest version stored of the Local Agent
	 * @return	string with the name of the file
	 */
	private String lastUpload()
	{
		String url = File.separator+"Allseen"
				+File.separator+"localAgent";
		
		File folder = new File(url);
		File[] listOfFiles = folder.listFiles();
		String higher=null;
		
		if (listOfFiles.length>0)
		{
			higher = listOfFiles[0].getName();
			
			for (int i=1; i<listOfFiles.length; i++)
			{
				String result = compare(higher,listOfFiles[i].getName());
				
				if(result.equals("higher"))
				{
					higher = listOfFiles[i].getName();
				}
			}
		}
		return higher;
	}
	
	/**
	 * Compares two versions of Local Agent
	 * @param 	s1	version one of comparison
	 * @param 	s2	version two of comparison
	 * @return		higher/lower/equal
	 */
	private String compare(String s1, String s2)
	{
		String aux1 = s1.replaceAll("\\D+", "_");
		String aux2 = s2.replaceAll("\\D+", "_");
		String[] aux3 = aux1.split("_");
		String[] aux4 = aux2.split("_");
				
		for (int i=1; i<aux3.length; i++)
		{
			if (Integer.parseInt(aux3[i])>Integer.parseInt(aux4[i]))
			{
				return "lower";
			}
			else if (Integer.parseInt(aux3[i])<Integer.parseInt(aux4[i]))
			{
				return "higher";
			}
		}
		return "equal";
	}
}
package com.at4wireless.spring.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.dao.GoldenUnitDAO;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.ProjectService;

@Controller
@RequestMapping(value="/rest")
public class RestController {
		
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private CertificationReleaseService crService;
	
	@Autowired
	private DutService dutService;
	
	@Autowired
	private GoldenUnitDAO guDao;
	
	@RequestMapping(value="/getList/{user}", method = RequestMethod.GET)
	public @ResponseBody List<RestProject> getList(@PathVariable String user) {
		return projectService.getRestData(user);
	}
	
	@RequestMapping(value="/getTechnology/{technology}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void getTechnology(@PathVariable String technology, HttpServletResponse response) {
		
		String n = technology.replaceAll("_", "\\.");
		String fullPath = File.separator + "Allseen" + File.separator + "Technology" + File.separator +
				"TestCases_Package_" +n+".jar";
		//return new FileSystemResource(new File(fullPath));
		
		try {
			File f = new File(fullPath);
		    InputStream is = new FileInputStream(f);
		    response.setHeader("Content-Disposition", "attachment; filename="+technology+".jar");
		    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
		    response.flushBuffer();
		    is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //File downloadFile = new File(fullPath);
        /*return Response.ok(downloadFile, MediaType.APPLICATION_OCTET_STREAM)
        	      .header("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"" ) //optional
        	      .build();*/
	}
	
	@RequestMapping(value="/getProject/{user}/{id}", method = RequestMethod.GET)
	public @ResponseBody String getProject(@PathVariable String user, @PathVariable int id) throws IOException {
		//List<Project> projectList = projectDao.list(user);
		
		String config = null;
		JSONObject xmlJSONObj = null;
		
		for (Project p : projectService.list(user)) {
			if(p.getIdProject()==id) {
				config = readFile(p.getConfiguration(), StandardCharsets.UTF_8);
				try {
					xmlJSONObj = XML.toJSONObject(config);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//return readFile("/config_"+id+".xml", StandardCharsets.UTF_8);
		return xmlJSONObj.toString();
	}
	
	@RequestMapping(value="/getResults/{user}/{id}", method = RequestMethod.GET)
	public @ResponseBody String getResults(@PathVariable String user, @PathVariable int id) throws IOException {
		
		String config = null;
		JSONObject xmlJSONObj = null;
		
		config = readFile(projectService.getFormData(user, id).getResults(), StandardCharsets.UTF_8);
		try {
			xmlJSONObj = XML.toJSONObject(config);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for (Project p : projectDao.list(user)) {
			if(p.getIdProject()==id) {
				config = readFile(p.getConfiguration(), StandardCharsets.UTF_8);
				xmlJSONObj = XML.toJSONObject(config);
			}
		}*/
		//return readFile("/config_"+id+".xml", StandardCharsets.UTF_8);
		return xmlJSONObj.toString();
	}
	
	@RequestMapping(value="/getFullLog/{user}/{id}/{log}", method = RequestMethod.GET)
	public @ResponseBody String getFullLog(@PathVariable String user, @PathVariable int id,
			@PathVariable String log) throws IOException {
		
		String path = File.separator+"Allseen"+File.separator+"Users"+File.separator+
				user+File.separator+id+File.separator+log+".log";
		return readFile(path, StandardCharsets.UTF_8);
	}
	
	@RequestMapping(value="/sendResult/{user}/{id}", method = RequestMethod.POST,
			consumes = "application/xml")
	public ResponseEntity<String> sendResult(@PathVariable String user, @PathVariable int id,
			@RequestBody String requestBody) {
		
		if(!requestBody.isEmpty()) {
			for (Project p : projectService.list(user)) {
				if (p.getIdProject()==id) {
					
					Writer writer = null;
					String url = File.separator+"Allseen"
							+File.separator+"Users"+File.separator+user+File.separator+id
							+File.separator+"result.xml";
	
					File f = new File(url);
					
					if(f.exists()&(!f.isDirectory())) {
						DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = null;
						DocumentBuilder builder2 = null;
						
						try {
							builder = builderFactory.newDocumentBuilder();
							builder2 = builderFactory.newDocumentBuilder();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						}
						
						try {
							Document target = builder.parse(new FileInputStream(File.separator+"Allseen"
									+File.separator+"Users"+File.separator+user+File.separator+id
									+File.separator+"result.xml"));
							
							Document source = builder2.parse(new ByteArrayInputStream(requestBody.getBytes("utf-8")));
							
							XPath xPath = XPathFactory.newInstance().newXPath();
							
							String expression = "/Results/TestCase";
							NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(source, XPathConstants.NODESET);
							for (int i = 0; i < nodeList.getLength(); i++) {
							    //Create a duplicate node and transfer ownership of the
							    //new node into the destination document
							    Node newNode = target.importNode(nodeList.item(i),true);
							    //Make the new node an actual item in the target document
							    target.getDocumentElement().appendChild(newNode);
							}
							
							Transformer transformer;
							try {
								transformer = TransformerFactory.newInstance().newTransformer();
								
								Result output = new StreamResult(new File(File.separator+"Allseen"
										+File.separator+"Users"+File.separator+user+File.separator+id
										+File.separator+"result.xml"));
								Source input = new DOMSource(target);
	
								try {
									transformer.transform(input, output);
								} catch (TransformerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (TransformerConfigurationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (TransformerFactoryConfigurationError e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (XPathExpressionException e) {
							e.printStackTrace();
						}
					} else {
						try{
						    writer = new BufferedWriter(new OutputStreamWriter(
						          new FileOutputStream(url), "utf-8"));
						    writer.write(requestBody);
							
						    url = File.separator+File.separator+"Allseen"
									+File.separator+File.separator+"Users"+File.separator+File.separator
									+user+File.separator+File.separator+id
									+File.separator+File.separator+"result.xml";
						    projectService.resultProject(id, url);
						    System.out.println("\nXML DOM Created Successfully..");
						} catch (IOException ex) {
							  // report
						} finally {
						   try {;
						   	   //out.close();
							   writer.close();
						   } catch (Exception ex) {}
						}
					}
					
					
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
	
	@RequestMapping(value="/upload/{user}/{id}", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@PathVariable String user, @PathVariable int id,
			@RequestParam("name") String name, @RequestParam("file") MultipartFile file) {

		for (Project p : projectService.list(user)) {
			if (p.getIdProject()==id) {
				
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				boolean existsOnXML = false;
				
				try {
					builder = builderFactory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
				
				try {
					Document xmlDocument = builder.parse(new FileInputStream(File.separator+"Allseen"
							+File.separator+"Users"+File.separator+user+File.separator+id
							+File.separator+"result.xml"));
					
					XPath xPath = XPathFactory.newInstance().newXPath();
					
					String expression = "/Results/TestCase/LogFile";
					NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
					for (int i = 0; i < nodeList.getLength(); i++) {
					    if(nodeList.item(i).getFirstChild().getNodeValue().equalsIgnoreCase(name)) {
					    	existsOnXML=true;
					    }
					}
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}		
				
				if ((!file.isEmpty())&(existsOnXML)) {
		            try {
		                byte[] bytes = file.getBytes();
		                BufferedOutputStream stream =
		                        new BufferedOutputStream(new FileOutputStream(new File(File.separator+"Allseen"
		    							+File.separator+"Users"+File.separator+user+File.separator+id
		    							+File.separator+name)));
		                stream.write(bytes);
		                stream.close();
		                return "You successfully uploaded " + name + "!";
		            } catch (Exception e) {
		                return "You failed to upload " + name + " => " + e.getMessage();
		            }
		        } else {
		            return "You failed to upload " + name + " because the file was empty.";
		        }
			}
		}
		
		return "Bad user or projectId";

    }
	
	protected static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	@RequestMapping(value="/getLastVersion", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	 public @ResponseBody void getLastVersion(HttpServletResponse response) {
			
			String lastVersion = lastUpload();
			String fullPath = File.separator + "Allseen" + File.separator + "localAgent" + File.separator +
					lastVersion;
			
			try {
				File f = new File(fullPath);
			    InputStream is = new FileInputStream(f);
			    response.setHeader("Content-Disposition", "attachment; filename="+lastVersion);
			    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			    response.flushBuffer();
			    is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	
	@RequestMapping(value="/isLastVersion/{v}", method=RequestMethod.GET)
	public @ResponseBody String isLastVersion(@PathVariable String v) {
		
		System.out.println(v);
		if (v.matches("\\d+_\\d+_\\d+")) {
			//System.out.println(lastUpload());
			String highest = lastUpload();
			String result = compare(highest,"_"+v);
			
			if(result.equals("lower")) {
				return "new version available";
			} else if (result.equals("equal")) {
				return "version up to date";
			} else {
				return "lower version";
			}
		}
		return "bad request";
	}
	
	private String lastUpload() {
		String url = File.separator+"Allseen"
				+File.separator+"localAgent";
		
		File folder = new File(url);
		File[] listOfFiles = folder.listFiles();
		String higher=null;
		
		if (listOfFiles.length>0) {
			higher = listOfFiles[0].getName();
			
			for (int i=1; i<listOfFiles.length; i++) {
				String result = compare(higher,listOfFiles[i].getName());
				if(result.equals("higher")) {
					higher = listOfFiles[i].getName();
				}
			}
		}
		return higher;
	}
	
	private String compare(String s1, String s2) {
		String aux1 = s1.replaceAll("\\D+", "_");
		String aux2 = s2.replaceAll("\\D+", "_");
		String[] aux3 = aux1.split("_");
		String[] aux4 = aux2.split("_");
		
		/*for(int i=0; i<aux3.length; i++) {
			System.out.println(aux3[i]+":"+aux4[i]);
		}*/
		
		if (Integer.parseInt(aux3[1])>Integer.parseInt(aux4[1])) {
			return "lower";
		} else if (Integer.parseInt(aux3[1])==Integer.parseInt(aux4[1])) {
			if (Integer.parseInt(aux3[2])>Integer.parseInt(aux4[2])) {
				return "lower";
			} else if (Integer.parseInt(aux3[2])==Integer.parseInt(aux4[2])) {
				if (Integer.parseInt(aux3[3])>Integer.parseInt(aux4[3])) {
					return "lower";
				} else if (Integer.parseInt(aux3[3])==Integer.parseInt(aux4[3])) {
					return "equal";
				}
			}
		}
		return "higher";
	}
}

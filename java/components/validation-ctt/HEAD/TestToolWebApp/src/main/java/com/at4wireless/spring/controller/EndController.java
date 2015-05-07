package com.at4wireless.spring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Parameter;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.model.TestCase;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.IcsService;
import com.at4wireless.spring.service.IxitService;
import com.at4wireless.spring.service.ParameterService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.TestCaseService;

@Controller
@RequestMapping(value="/end")
public class EndController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired 
	private IcsService icsService;
	
	@Autowired
	private IxitService ixitService;
	
	@Autowired
	private TestCaseService tcService;
	
	@Autowired
	private DutService dutService;
	
	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private GoldenUnitService guService;
	
	private static final int BUFFER_SIZE = 4096;
	
	//View pre-charging method
	@RequestMapping(method=RequestMethod.GET)
	public String end() {
	
		return "end";
	}
	
	@RequestMapping(value="/download", method=RequestMethod.GET)
    public void download(HttpServletResponse response) throws IOException {
 
		String fullPath = File.separator+"Allseen"+File.separator
				+"localAgent"+File.separator+lastUpload();
        File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);
         
        String mimeType = "application/octet-stream";
 
        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
 
        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);
 
        // get output stream of the response
        OutputStream outStream = response.getOutputStream();
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
 
        inputStream.close();
        outStream.close();
 
    }
	
	//Save configuration method
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String save(HttpServletRequest request) {
		
		//Check login before saving	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Map<String, String[]> map = request.getParameterMap();
			String username = auth.getName();
			String url = CreateXML(username, map);
			projectService.configProject(request.getParameter("data[idProject]"), url);
		}

		return "redirect:/end";
	}
 	
	//Create XML method
	private String CreateXML(String username, Map<String, String[]> map) {
		
		DocumentBuilderFactory cfFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder cfBuilder;
		List<Ics> icsList = new ArrayList<Ics>();
		List<Ixit> ixitList = new ArrayList<Ixit>();
		List<TestCase> tcList = new ArrayList<TestCase>();
		List<Sample> sampleList = new ArrayList<Sample>();
		String url = "";
		String folder="";
		
		//Get DUT services to filter ICS, IXIT and TestCases
		Project p = projectService.getFormData(username, Integer.parseInt(map.get("data[idProject]")[0]));
		
		for (BigInteger bi : projectService.getServicesData(p.getIdProject())) {
			icsList.addAll(icsService.getService(bi.intValue()));
			ixitList.addAll(ixitService.getService(bi.intValue()));
			if(p.getType().equals("Conformance")||p.getType().equals("Interoperability")) {
				tcList.addAll(tcService.getService(p.getType(),bi.intValue()));
			} else {
				tcList.addAll(tcService.getService("Interoperability",bi.intValue()));
				tcList.addAll(tcService.getService("Conformance",bi.intValue()));
			}
		}
		sampleList.addAll(dutService.getSampleData(p.getIdDut()));
		
		try {
			//Create XML
			cfBuilder = cfFactory.newDocumentBuilder();
			Document doc = cfBuilder.newDocument();
			
			//Add root element
			Element mainRootElement = doc.createElement("Project");
			doc.appendChild(mainRootElement);
			
			//Append child elements to root element
			Ics ics;
			Iterator<Ics> iter = icsList.iterator();
			while(iter.hasNext()) {
				ics = iter.next();
				mainRootElement.appendChild(getIcs(doc, "Ics", ics.getId(), ics.getName(), 
						map.get("data["+ics.getName()+"]")[0]));
			}
			
			Ixit ixit;
			Iterator<Ixit> iter2 = ixitList.iterator();
			while(iter2.hasNext()) {
				ixit = iter2.next();
				mainRootElement.appendChild(getIcs(doc, "Ixit", ixit.getIdIxit(), ixit.getName(), 
						map.get("data["+ixit.getName()+"]")[0]));
			}
			
			TestCase tc;
			Iterator<TestCase> iter3 = tcList.iterator();
			while(iter3.hasNext()) {
				tc = iter3.next();
				try{
					if (map.get("data["+tc.getName()+"]")[0].equals("true")) {
						mainRootElement.appendChild(getTestCase(doc, tc.getIdTC(), tc.getName(), tc.getDescription()));
					}
				} catch (Exception e) {
					
				}
			}
			
			Sample s;
			Iterator<Sample> iter4 = sampleList.iterator();
			while(iter4.hasNext()) {
				s = iter4.next();
				mainRootElement.appendChild(getSample(doc, s));
			}
			
			Parameter param;
			Iterator<Parameter> iter5 = parameterService.list().iterator();
			while(iter5.hasNext()) {
				param = iter5.next();
				mainRootElement.appendChild(getParam(doc,param,map.get("data["+param.getName()+"]")[0]));
			}
			
			String str;
			Iterator<String> iter6 = guService.getGuList(p.getIdProject()).iterator();
			while(iter6.hasNext()) {
				str = iter6.next();
				mainRootElement.appendChild(getGu(doc,str));
			}
				
			//Create folder if it does not exist
			folder = File.separator+"Allseen"+File.separator+"Users"+File.separator+username+File.separator
					+map.get("data[idProject]")[0];
			new File(folder).mkdirs();
			url = folder+File.separator+"configuration.xml";
			
			//Output DOM XML to file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult console = new StreamResult(new File(url));
			transformer.transform(source, console);
			
			System.out.println("\nXML DOM Created Successfully..");
			
			//Change url string to save it in database (mysql)
			url = File.separator+File.separator+"Allseen"+File.separator+File.separator+"Users"
					+File.separator+File.separator+username
					+File.separator+File.separator+map.get("data[idProject]")[0]
					+File.separator+File.separator+"configuration.xml";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	private static Node getIcs(Document doc, String type, int id, String name, String value) {
		Element ics = doc.createElement(type);
		ics.appendChild(getElements(doc, ics, "Id", Integer.toString(id)));
		ics.appendChild(getElements(doc, ics, "Name", name));
		ics.appendChild(getElements(doc, ics, "Value", value));
		return ics;
	}
	
	private static Node getTestCase(Document doc, int id, String name, String description) {
		Element tc = doc.createElement("TestCase");
		tc.appendChild(getElements(doc, tc, "Id", Integer.toString(id)));
		tc.appendChild(getElements(doc, tc, "Name", name));
		tc.appendChild(getElements(doc, tc, "Description", description));
		return tc;
	}
	
	private static Node getElements(Document doc, Element element, String name, String value) {
		
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
	
	private static Node getSample(Document doc, Sample sample) {
		Element s = doc.createElement("Sample");
		s.appendChild(getElements(doc, s, "Id", Integer.toString(sample.getIdSample())));
		s.appendChild(getElements(doc, s, "DeviceId", sample.getDeviceId()));
		s.appendChild(getElements(doc, s, "AppId", sample.getAppId()));
		s.appendChild(getElements(doc, s, "swVer", sample.getSwVer()));
		s.appendChild(getElements(doc, s, "hwVer", sample.getHwVer()));
		return s;
	}
	
	private static Node getParam(Document doc, Parameter param, String value) {
		Element s = doc.createElement("Parameter");
		s.appendChild(getElements(doc, s, "Id", Integer.toString(param.getIdParam())));
		s.appendChild(getElements(doc, s, "Name", param.getName()));
		s.appendChild(getElements(doc, s, "Value", value));
		s.appendChild(getElements(doc, s, "Description", param.getDescription()));
		return s;
	}
	
	private static Node getGu(Document doc, String str) {
		Element s = doc.createElement("GoldenUnit");
		s.appendChild(getElements(doc, s, "Name", str));
		return s;
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

package com.at4wireless.spring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCaseResult;
import com.at4wireless.spring.service.IcsService;
import com.at4wireless.spring.service.IxitService;
import com.at4wireless.spring.service.ParameterService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.TestCaseService;

@Controller
@RequestMapping(value="/results")
public class ResultsController {

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private IcsService icsService;
	
	@Autowired
	private IxitService ixitService;
	
	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private TestCaseService tcService;
	
	private static final int BUFFER_SIZE = 4096;
	
	@RequestMapping(method = RequestMethod.GET)
	public String results(Model model, @ModelAttribute("newProject") Project newProject) {
		
		String username = "";
		String projectName = "";
		List<TestCaseResult> listTCResult = new ArrayList<TestCaseResult>();
		
			
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			username = auth.getName();
			
			/*for (Project p : projectService.list(username)) {
				if (p.getIdProject()==newProject.getIdProject()){
					projectName = p.getName();
				}
			}*/
			Project p = projectService.getFormData(username, newProject.getIdProject());
			
			projectName = p.getName();
		
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			
			try {
				builder = builderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
			try {
				/*Document xmlDocument = builder.parse(new FileInputStream(File.separator+"Allseen"
						+File.separator+"Users"+File.separator+username+File.separator+newProject.getIdProject()
						+File.separator+"result.xml"));*/
				Document xmlDocument = builder.parse(new FileInputStream(p.getResults()));
				
				XPath xPath = XPathFactory.newInstance().newXPath();
				
				String root = "/Results/TestCase/";
	
				NodeList nameList = (NodeList) xPath.compile(root+"Name").evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList descList = (NodeList) xPath.compile(root+"Description").evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList dateList = (NodeList) xPath.compile(root+"DateTime").evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList verdictList = (NodeList) xPath.compile(root+"Verdict").evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList versionList = (NodeList) xPath.compile(root+"Version").evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList logList = (NodeList) xPath.compile(root+"LogFile").evaluate(xmlDocument, XPathConstants.NODESET);
				
				for (int i = 0; i < nameList.getLength(); i++) {
				    TestCaseResult tcResult = new TestCaseResult();
				    tcResult.setName(nameList.item(i).getFirstChild().getNodeValue());
				    tcResult.setDescription(descList.item(i).getFirstChild().getNodeValue());
				    tcResult.setExecTimestamp(dateList.item(i).getFirstChild().getNodeValue());
				    tcResult.setVerdict(verdictList.item(i).getFirstChild().getNodeValue());
				    tcResult.setVersion(versionList.item(i).getFirstChild().getNodeValue());
				    tcResult.setLog(logList.item(i).getFirstChild().getNodeValue());
				    
				    listTCResult.add(tcResult);
				}
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		
		}
		
		model.addAttribute("projectName", projectName);
		model.addAttribute("listTCResult", listTCResult);
		return "results";
	}
	
	@RequestMapping(value="/fullLog", method=RequestMethod.GET, produces="text/plain")
	public @ResponseBody String showLog(HttpServletRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String log = "";
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			try {
				System.out.println(File.separator+"Allseen"+File.separator+username+File.separator+
						request.getParameter("id")+File.separator+request.getParameter("file"));
				log = RestController.readFile(File.separator+"Allseen"+File.separator+"Users"+
								File.separator+username+File.separator+
						request.getParameter("id")+File.separator+request.getParameter("file"), 
						StandardCharsets.UTF_8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return log;
	}
	
	@RequestMapping(value="/tr/create", method=RequestMethod.POST)
	public @ResponseBody boolean createTestReport(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			Project p = projectService.getFormData(username,
					Integer.parseInt(request.getParameter("idProject")));
			String outputFileName = File.separator+"Allseen"
					+File.separator+"Users"+File.separator+username+File.separator
					+p.getIdProject()
					+File.separator+"TestReport.pdf";
			
			System.out.println(outputFileName);
			
			if(tcService.ranAll(p.getConfiguration(), p.getResults())) {
			
				float fontSize = 12;
				float leading = 1.5f * fontSize;
				
				//Create a document and add a page to it
				PDDocument document = new PDDocument();
				PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);
				//PDPage.PAGE_SIZE_LETTER is also possible
				PDRectangle rect = page1.getMediaBox();
				float margin = 72;
				float width = rect.getWidth() -2*margin;
				float height = rect.getHeight() -2*margin;
				float startX = rect.getLowerLeftX()+margin;
				float startY = rect.getUpperRightY()-margin;
				//rect can be used to get the page width and height
				document.addPage(page1);
				
				//Create a new font object selecting one of the PDF base fonts
				PDFont fontPlain = PDType1Font.HELVETICA;
				PDFont fontBold = PDType1Font.HELVETICA_BOLD;
				PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
				PDFont fontMono = PDType1Font.COURIER;
				
				List<String> printableText = new ArrayList<String>();
				
				try {
					//Start a new content stream which will "hold" the to be created content
					PDPageContentStream cos = new PDPageContentStream(document, page1);
					printableText.addAll(projectService.pdfData(username, p.getIdProject()));
					printableText.addAll(icsService.pdfData(p.getConfiguration()));
					printableText.addAll(ixitService.pdfData(p.getConfiguration()));
					printableText.addAll(parameterService.pdfData(p.getConfiguration()));
					printableText.addAll(tcService.pdfData(p.getConfiguration(), p.getResults()));
					//Define a text content stream using the selected font, move the cursor and draw some text
					cos.beginText();
					cos.setFont(fontPlain, fontSize);
					cos.moveTextPositionByAmount(startX,startY);
					float filled = 0;
					for (String s : printableText) {
						cos.drawString(s);
						filled+=leading;
						if (filled>height) {
							cos.endText();
							cos.close();
							PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
							document.addPage(page);
							cos = new PDPageContentStream(document,page);
							cos.beginText();
							cos.setFont(fontPlain, fontSize);
							cos.moveTextPositionByAmount(startX,startY);
							filled=0;
						} else {
							cos.moveTextPositionByAmount(0,-leading);
						}
					}
					//cos.drawString("Italic");
					cos.endText();
					
					cos.close();
					document.save(outputFileName);
					document.close();
					outputFileName = File.separator+File.separator+"Allseen"
							+File.separator+File.separator+"Users"+File.separator
							+File.separator+username+File.separator+File.separator
							+p.getIdProject()+File.separator
							+File.separator+"TestReport.pdf";
					projectService.testReport(p.getIdProject(),outputFileName);
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (COSVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	@RequestMapping(value="/tr/view", method = RequestMethod.GET)
	public void getTestReport(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			File downloadFile = new File(p.getTestReport());
	        FileInputStream inputStream = new FileInputStream(downloadFile);
	         
	        String mimeType = "application/pdf";
	 
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
	}
	
	/*@RequestMapping(value="tr/hasTr", method = RequestMethod.GET)
	public @ResponseBody boolean hasTr(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return projectService.getFormData(auth.getName(), 
					Integer.parseInt(request.getParameter("idProject"))).isHasTestReport();
		} else {
			return false;
		}
	}*/
	
	@RequestMapping(value="tr/send", method = RequestMethod.GET)
	public void send(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			FileInputStream in = new FileInputStream(p.getTestReport());
			
			/*File f = new File(File.separator+"Allseen"+File.separator
					+"Users"+File.separator+p.getUser()+File.separator+
					p.getIdProject()+File.separator+"TestReport.zip");
			if(!f.exists()) {
			    f.createNewFile();
			}*/ 
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(File.separator+"Allseen"+File.separator
					+"Users"+File.separator+p.getUser()+File.separator+
					p.getIdProject()+File.separator+"TestReport.zip"));
			out.putNextEntry(new ZipEntry("TestReport.pdf"));
			
			byte[] b = new byte[1024];
			int count;
			
			while ((count = in.read(b))>0) {
				out.write(b,0,count);
			}
			
			for (String str : tcService.zipData(p.getConfiguration(), p.getResults())) {
				out.putNextEntry(new ZipEntry(str));
				in = new FileInputStream(File.separator+"Allseen"+File.separator+"Users"
						+File.separator+p.getUser()
						+File.separator+p.getIdProject()+File.separator+str);
				while ((count = in.read(b))>0) {
					out.write(b,0,count);
				}
			}
			out.close();
			in.close();
			
			//DOWNLOAD AFTER CREATING
			
			File downloadFile = new File(File.separator+"Allseen"+File.separator
					+"Users"+File.separator+p.getUser()+File.separator+p.getIdProject()+File.separator+"TestReport.zip");
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
	}
	
	@RequestMapping(value="tr/ranAll", method=RequestMethod.GET)
	public @ResponseBody boolean ranAll(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			return tcService.ranAll(p.getConfiguration(), p.getResults());
		} else {
			return false;
		}
	}
}

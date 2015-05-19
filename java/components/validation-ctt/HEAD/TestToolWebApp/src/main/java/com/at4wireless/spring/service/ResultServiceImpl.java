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

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCaseResult;

@Service
public class ResultServiceImpl implements ResultService {

	private final PDFont TITLE_FONT = PDType1Font.TIMES_BOLD;
	private final float TITLE_SIZE = 20;
	private final float TITLE_LEADING = 1.5f*TITLE_SIZE;
	
	private final PDFont SECTION_FONT = PDType1Font.TIMES_BOLD;
	private final float SECTION_SIZE = 16;
	private final float SECTION_LEADING = 1.5f*SECTION_SIZE;

	private final PDFont CONTENT_FONT = PDType1Font.TIMES_ROMAN;
	private final float CONTENT_SIZE = 12;
	private final float CONTENT_LEADING = 1.5f*CONTENT_SIZE;

	private float filled = 0;

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

	@Override
	public List<TestCaseResult> getResults(Project p) {
		List<TestCaseResult> listTCResult = new ArrayList<TestCaseResult>();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
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
			return listTCResult;

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return listTCResult;
	}

	@Override
	public boolean createTestReport(String username, Project p) {
		String outputFileName = File.separator+"Allseen"
				+File.separator+"Users"+File.separator+username+File.separator
				+p.getIdProject()
				+File.separator+"TestReport.pdf";

		System.out.println(outputFileName);

		filled = 0;

		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);

		PDRectangle rect = page1.getMediaBox();
		float margin = 72;

		float height = rect.getHeight() -2*margin;
		float startX = rect.getLowerLeftX()+margin;
		float startY = rect.getUpperRightY()-margin;

		try {
			
			addTitle(document, "TEST REPORT", startX, startY);

			addSection(document, projectService.pdfData(username, p.getIdProject()),
					startX, startY, height);
			addSection(document, icsService.pdfData(p.getConfiguration()),
					startX, startY, height);
			addSection(document, ixitService.pdfData(p.getConfiguration()),
					startX, startY, height);
			addSection(document, parameterService.pdfData(p.getConfiguration()),
					startX, startY, height);
			addSection(document, tcService.pdfData(p.getConfiguration(), p.getResults()),
					startX, startY, height);

			addReportFooter(document);
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
	
	private void addTitle(PDDocument document, String title, float startX, float startY) throws IOException {

		PDPageContentStream contentStream = null;
		PDPage page = null;
		float yStart = startY;
		
		float titleWidth = TITLE_FONT.getStringWidth(title)/1000*TITLE_SIZE;

		page = new PDPage(PDPage.PAGE_SIZE_A4);
		document.addPage(page);

		contentStream = new PDPageContentStream(document, page, true, true);
		contentStream.beginText();
		contentStream.moveTextPositionByAmount((page.getMediaBox().getWidth() - titleWidth)/2, yStart);

		//ADD TITLE
		contentStream.setFont(TITLE_FONT, TITLE_SIZE);
		contentStream.drawString(title);
		filled+=TITLE_LEADING;
		contentStream.moveTextPositionByAmount(0, -TITLE_LEADING);
		contentStream.setFont(CONTENT_FONT, CONTENT_SIZE);

		filled+= TITLE_LEADING-SECTION_LEADING;
		contentStream.endText();
		contentStream.close();
	}

	private void addSection(PDDocument document, List<String> section, float startX, float startY, 
			float height) throws IOException {

		PDPageContentStream contentStream = null;
		PDPage page = null;
		float yStart = startY;
		
		if((filled==0)||(filled>height)) {
			page = new PDPage(PDPage.PAGE_SIZE_A4);
			document.addPage(page);
		} else {
			@SuppressWarnings("rawtypes")
			List pages = document.getDocumentCatalog().getAllPages();
			page = (PDPage) pages.get(pages.size()-1);
			yStart-=filled;
		}

		contentStream = new PDPageContentStream(document, page, true, true);
		contentStream.beginText();
		contentStream.moveTextPositionByAmount(startX, yStart);

		//ADD SECTION TITLE
		contentStream.setFont(SECTION_FONT, SECTION_SIZE);
		contentStream.drawString(section.get(0));
		filled+=SECTION_LEADING;
		contentStream.moveTextPositionByAmount(0, -SECTION_LEADING);
		contentStream.setFont(CONTENT_FONT, CONTENT_SIZE);

		//ADD SECTION CONTENT
		for (String s : section.subList(1, section.size())) {
			if (filled>height) {
				System.out.println("Entra mayor");
				contentStream.endText();
				contentStream.close();
				page = new PDPage(PDPage.PAGE_SIZE_A4);
				document.addPage(page);
				contentStream = new PDPageContentStream(document,page, true, true);
				contentStream.beginText();
				contentStream.setFont(CONTENT_FONT, CONTENT_SIZE);
				contentStream.moveTextPositionByAmount(startX,startY);
				filled=0;
			}
			contentStream.drawString(s);
			filled+=CONTENT_LEADING;
			contentStream.moveTextPositionByAmount(0,-CONTENT_LEADING);
		}
		filled+= SECTION_LEADING-CONTENT_LEADING;
		contentStream.endText();
		contentStream.close();
	}

	/** 
	 * This method for include the footer to the each page in pdf document. 
	 * 
	 * @param doc 
	 * Set the pdf document. 
	 * @param reportName 
	 * Set the report name. 
	 * @throws IOException 
	 */ 
	@SuppressWarnings("rawtypes") 
	private void addReportFooter(final PDDocument doc) throws IOException { 

		PDPageContentStream footercontentStream = null;
		float fontSize = 10;
		float margin = 72;
		float footerY = PDPage.PAGE_SIZE_A4.getLowerLeftY()+41;
		float startX = PDPage.PAGE_SIZE_A4.getLowerLeftX()+margin;
		float headerY = PDPage.PAGE_SIZE_A4.getUpperRightY()-41;
		PDFont font = PDType1Font.TIMES_ROMAN;
		String footer = null;
		try { 

			List pages = doc.getDocumentCatalog().getAllPages();
			System.out.println("Number of pages: "+pages.size());
			PDXObjectImage img = new PDJpeg(doc,new FileInputStream(File.separator+"Allseen"+File.separator+"logo.jpg"));

			for (int i = 0; i < pages.size(); i++) {
				PDPage page = ((PDPage) pages.get(i));
				footer = (i + 1) + "/" + pages.size();
				float titleWidth = font.getStringWidth(footer)/1000*fontSize;

				footercontentStream = new PDPageContentStream(doc, page, true, true);
				footercontentStream.drawXObject(img, startX, headerY, img.getWidth()/2, img.getHeight()/2);
				footercontentStream.beginText(); 
				footercontentStream.setFont(font, fontSize);
				footercontentStream.moveTextPositionByAmount((page.getMediaBox().getWidth() - titleWidth)/2, 
						footerY); 
				footercontentStream.drawString(footer); 
				footercontentStream.endText(); 
				footercontentStream.close(); 
			} 
		} catch (final IOException exception) { 
			throw new RuntimeException(exception); 
		} finally { 
			if (footercontentStream != null) { 
				try { 
					footercontentStream.close(); 
				} catch (final IOException exception) { 
					throw new RuntimeException(exception); 
				} 
			} 
		} 
	}

}

/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class TestCasesWindow.
 */
public class TestCasesWindow extends JPanel
{ 
	/** The test cases window. */
	TestCasesWindow testCasesWindow;

	/** The test to execute. */
	private String test;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7430591674214212172L;

	/** The table that shows the testcases. */
	private JTable table;

	/** If true the program is running all test cases. */
	private Boolean runAllTestCases=false;

	/** If true the results had been sent. */
	private Boolean sendedResults=false;

	/** The send button used to stop execution or send results. */
	private JButton sendButton;

	/** The Test cases Array. */
	private String[] TestCases;

	/** The test case process. */
	private Process testCaseProcess;

	/** The doc obtained from the web. */
	static Document doc;

	/** The version. */
	private String version;

	/** The dialog used to select the sample. */
	private JDialog selectSample;

	/** The dialog that shows the end run all testcases message. */
	private JDialog dialogEndRunAll;

	/** The table sample. */
	JTable tableSample;

	/** The software version of the sample. */
	String[] swVer;

	/** The Device id of the sample . */
	String[] DeviceId;

	/** The hardware version of the sample. */
	String[] hwVer;

	/** The App id of the sample. */
	String[] AppId;

	/** The id. */
	int id; 

	/** The selected sample. */
	int selectedSample=-1;

	/** The user used to authenticate. */
	String user;

	/** The token obtained when authenticate. */
	String token;

	/** The table size. */
	int tableSize;

	/** If true the testCase is executing . */
	Boolean running=false;

	/** The log frame that shows the execution log. */
	JDialog logFrame;

	/** The capture pane. */
	CapturePane capturePane;

	/** The execution thread. */
	Thread executionThread;

	/** Class logger */
	private  Logger logger;

	/** The main window class. */
	MainWindow mainWindow=null;
	
	SecretKey cipherKey;


	/**
	 * Instantiates a new test cases window.
	 *
	 * @param 	mainWindow 						main window to associate with "back" click
	 * @param 	id 								project ID
	 * @param 	version 						certification release
	 * @param 	user 							authenticated user
	 * @param 	token 							authentication token
	 * @throws 	SAXException 					the SAX exception
	 * @throws 	ParserConfigurationException 	the parser configuration exception
	 * @throws 	IOException 					Signals that an I/O exception has occurred.
	 */
	public TestCasesWindow(MainWindow mainWindow,int id, String version, String user,String token, Level logLevel,
			SecretKey cipherKey) throws SAXException, ParserConfigurationException, IOException
	{
		this.logger = Logger.getLogger(this.getClass().getName());
		this.logger.setLevel(logLevel);
		this.mainWindow= mainWindow;
		this.version=version;
		testCasesWindow=this;	
		this.id=id;
		this.user=user;
		this.token=token;
		this.cipherKey = cipherKey;
		
		Object col[] = {"Test Case","Description",
				"Certification Release","Action", "Result",
				"Date & Time"};
		
		doc=getXML(id, user,token);
		tableSize = doc.getElementsByTagName("TestCase").getLength();
		TestCases=new String[tableSize];
		
		TableModel model = new DefaultTableModel(col,tableSize){

			private static final long serialVersionUID = -5114222498322422255L;

			public boolean isCellEditable(int row, int column)
			{
				if(column==3){
					return true;
				}else{
					return false;
				}
			}
		};
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5};
		gridBagLayout.rowWeights = new double[]{0.5, 0.9};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;
		add(mainWindow.getHeaderPanel(),gbc_header);
		
		table=new JTable(model){
			
			private static final long serialVersionUID = -8369977836878349660L;
			
			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column)
			{
				//Change colors depending on test result
				int viewIdx = row;
				int modelIdx = convertRowIndexToModel(viewIdx);
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setForeground(Color.black);
				Font font=new Font("Arial", Font.BOLD, 12);
				comp.setFont(font);
				if(column==4||column==6){
					if("INCONC".equals(table.getModel().getValueAt(modelIdx, column))){
						comp.setForeground(new Color(168,161,0));
					}else if("PASS".equals(table.getModel().getValueAt(modelIdx, column))){
						comp.setForeground(new Color(103,154,0));
					}else if("FAIL".equals(table.getModel().getValueAt(modelIdx, column))){
						comp.setForeground(new Color(217,61,26));
					}
				}else{
					comp = super.prepareRenderer(renderer, row, column);
				}
				return comp;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBorder(null);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(3, false);
		table.setRowSorter(sorter);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						table.repaint();
					}
				}
		);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setViewportBorder(null);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.anchor=GridBagConstraints.NORTH;
		gbc_scrollPane.fill=GridBagConstraints.BOTH;
		add(scrollPane,gbc_scrollPane);
		drawTestCases(table,doc);
		table.getColumn("Action").setMaxWidth(80);
		table.getColumn("Action").setMinWidth(80);
		table.getColumn("Action").setCellRenderer(new ButtonRenderer());
		table.getColumn("Action").setCellEditor(
				new ButtonEditor(new JCheckBox()));
	}

	/**
	 * Gets the Test Case list from the document and fill the table with its values.
	 *
	 * @param 	table 	the table
	 * @param 	doc 	the document that contains the Test Cases values
	 * @return 			the test cases
	 */
	private void drawTestCases(JTable table,Document doc)
	{
		NodeList testCases = doc.getElementsByTagName("TestCase");
		for (int i = 0; i < testCases.getLength(); i++) {
			Node node = testCases.item(i);
			Element element = (Element) node;
			
			String name=getValue("Name", element);
			table.setValueAt(name,i,0);
			TestCases[i]=name;
			
			String description=getValue("Description", element);
			table.setValueAt(description,i,1);
			
			table.setValueAt(version,i,2);

			String LastVerdict=getValue("LastVerdict", element);
			table.setValueAt(LastVerdict,i,4);
			
			String LastExec=getValue("LastExec", element);
			if(LastExec.equals("Not executed")){
				table.setValueAt("",i,5); 
			}else{
				table.setValueAt(LastExec,i,5); 
			}
		}
	}
	/**
	 * Save results in web server.
	 */
	protected void saveResults()
	{
		getResults();
		saveLog(capturePane.getLog());
		
		logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//capturePane.deleteLog();
		capturePane.clear();
		running=false;
		capturePane.removeAll();
		logFrame.dispose();
		
		sendResults();
		
		System.out.close();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}

	/**
	 * Sends results to the web server.
	 */
	protected void sendResults()
	{
		sendedResults=true; 

		if(sendResultsXML().getStatusLine().getStatusCode()==200){
			if(sendLog().getStatusLine().getStatusCode()==200){
				File log = getLog(getLogName());
				log.delete();
				File res = new File("Results.xml");
				res.delete();
			}	
		}
	}

	/**
	 * Sends log to the Web Server.
	 *
	 * @return	HTTP response of the server
	 */
	private HttpResponse sendLog()
	{
		String sendLogUrl=getConfigValue("TestToolWebAppUrl")+getConfigValue("UploadLogFile")+user+"/"+id;
		String logName=getLogName();
		File log=getLog(logName);
		//String hash=hashFile(log);
		
		FileEncryption fE;
		String encryptedLog = "";
		try {
			fE = new FileEncryption();
			fE.setAesSecretKey(cipherKey);
			encryptedLog = fE.encrypt(Files.readAllBytes(Paths.get(getConfigValue("LogPath")+logName)));
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String hash = hashFile(encryptedLog);
		
		HttpPost post = new HttpPost(sendLogUrl);
		HttpResponse resp = null;
		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.addTextBody("file", encryptedLog)
				.addTextBody("name", logName)
				.addTextBody("hash", hash)
				.build();
		
		post.addHeader("Authorization", "bearer "+token);
		post.setEntity(httpEntity);
		
		try {
			resp = HttpClientBuilder.create().build().execute(post);
		} catch (IOException e) {
			logger.warn("Error while sending log");
			e.printStackTrace();
		}

		log.delete();

		return resp;
	}

	/**
	 * Hashes a file.
	 *
	 * @param 	file 						file to be hashed
	 * @return 								hash
	 */
	//private String hashFile(File file)
	private String hashFile(String str)
	{
		String algorithm="MD5";
		//FileInputStream inputStream = null;
		ByteArrayInputStream inputStream = null;
		byte[] hashedBytes = null;
		try {
			//inputStream = new FileInputStream(file);
			inputStream = new ByteArrayInputStream(str.getBytes());
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			
			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;
			while((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}
			
			hashedBytes = digest.digest();
		} catch (FileNotFoundException e) {
			logger.error("File does not exist");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm "+algorithm+" not supported");
		} catch (IOException e) {
			logger.error("Cannot read file");
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return convertByteArrayToHexString(hashedBytes);
	}

	/**
	 * Converts byte array to hex string.
	 *
	 * @param 	arrayBytes 	hash as bytes array
	 * @return 				hash as string
	 */
	private String convertByteArrayToHexString(byte[] arrayBytes)
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Sends results XML to Web Server.
	 *
	 * @return 	HTTP response from the server
	 */
	private HttpResponse sendResultsXML()
	{
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("SendResultsUrl")+user+"/"+id;

		HttpPost postRequest = new HttpPost(url);
		HttpResponse response = null;
		
		try {
			StringEntity input = new StringEntity(getResult());
			input.setContentType("application/xml");
			postRequest.setEntity(input);
			postRequest.addHeader("Authorization", "bearer "+token);
			response = HttpClientBuilder.create().build().execute(postRequest);
		} catch (UnsupportedEncodingException e2) {
			logger.error("Unsupported encoding for results.xml");
			e2.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error while sending results to the Web Server");
		}
		
		return response;
	}


	/**
	 * Gets the log of the last executed Test Case.
	 *
	 * @param 	logName 	name of the log to be obtained
	 * @return 				log contents
	 */
	private File getLog(String logName)
	{
		/*String logPath=getConfigValue("LogPath");
		File log = new File(logPath+logName);

		return log;*/
		return new File(getConfigValue("LogPath")+logName);
	}

	/**
	 * Gets the log name of the last executed Test Case.
	 *
	 * @return 		log name
	 */
	private String getLogName()
	{
		String logName="";

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			Document doc = dbFactory.newDocumentBuilder().parse(new File("Results.xml"));
			Element element = (Element) doc.getElementsByTagName("Results").item(0);
			
			logName = getValue("LogFile", element);
		} catch (ParserConfigurationException e) {
			logger.error("DocumentBuilder configuration problem");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not possible to read Results.xml");
		}

		return logName;
	}

	/**
	 * Gets the result of the last executed Test Case
	 *
	 * @return 	result of the execution
	 */
	private String getResult()
	{
		String result="";
		try {
			result = readFile("Results.xml", StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Not possible to read Results.xml");
		}
		return result;
	}

	/**
	 * Reads a file.
	 *
	 * @param 	path 			location of the file
	 * @param 	encoding 		encoding of the information
	 * @return 					file contents
	 * @throws 	IOException 	if file does not exist
	 */
	static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Gets the results of the last executed Test and refresh the table with these values.
	 *
	 */
	protected void getResults()
	{
		Document docu=getResultDoc();
		
		if(docu != null) {	
			NodeList result = docu.getElementsByTagName("TestCase");
	
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				Element element = (Element) node;
				String name=getValue("Name", element);
				String dateTime=getValue("DateTime", element);
				String verdict=getValue("Verdict", element);
				
				for (int j = 0; j < tableSize ; j++) {
					if(name.equals(table.getValueAt(j, 0))) {
						table.setValueAt(verdict,j,4);
						table.setValueAt(dateTime,j,5);
					}
				}
			}
		} else {
			
			for (int j = 0; j < tableSize ; j++) {
				
				if("INCONC".equals(table.getValueAt(j, 4))){
					JTextField res=new JTextField("INCONC");
					res.setForeground(Color.YELLOW);
					table.setValueAt(res,j,4);
					Date utilDate = new java.util.Date(); 
					long lnMilisec = utilDate.getTime();
					Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisec);
					String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(sqlTimestamp);
					table.setValueAt(timeStamp,j,5);
				}
			}
		}
	}

	/**
	 * Gets the results doc of the last executed Test.
	 *
	 * @return 		results Document
	 */
	private Document getResultDoc()
	{
		/*File test = new File("Results.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;*/
		Document doc = null;
		try {
			//dBuilder = dbFactory.newDocumentBuilder();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			doc = dbFactory.newDocumentBuilder().parse(new File("Results.xml"));
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			logger.error("DocumentBuilder configuration problem");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not possible to read Results.xml");
		}
		/*Document docu = null;
		try {
			docu = dBuilder.parse(test);
			docu.getDocumentElement().normalize();
		} catch (SAXException e) {
			logger.error("Not posible to parse Results.xml");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not posible to parse Results.xml");
			e.printStackTrace();
		}*/
		return doc;
	}

	/**
	 * Gets the JSON string from the web server and converts it to an XML document.
	 *
	 * @param 	id 		project ID
	 * @param 	user 	authenticated user
	 * @param 	token 	authentication token
	 * @return 			XML data
	 */
	private Document getXML(int id,String user,String token)
	{
		Document doc = null;
		String testCases = "";
		Writer writer = null;
		String path = File.separator+"cfg.xml";
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetTestCases");
		try {
			URI URI = new URI(url+user+"/"+id);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);
			HttpEntity entity = null;
			BufferedReader br = null;
			
			postRequest.addHeader("Authorization", "bearer "+token);
			entity = httpClient.execute(postRequest).getEntity();
	
			br = new BufferedReader(new InputStreamReader(
					(entity.getContent())));
			String input;
			while ((input = br.readLine()) != null) {
				testCases+=input;
			}
		} catch (IOException e1) {
			logger.error("Communication with Web Server failed");
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server. The application will close");
			System.exit(0);
		} catch (URISyntaxException e) {
			logger.error("Malformed URL");
			e.printStackTrace();
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			JSONArray json = new JSONArray("["+testCases+"]");
			String xml = "";
			for (int i=0; i<json.length(); i++) {
				xml+=XML.toString(json.get(0));
			}
			
			logger.debug("XML obtained from Web Server: "+xml);
			
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path), "UTF-8"));
			writer.write(xml);
			
			doc = dbFactory.newDocumentBuilder().parse(new InputSource(
					new StringReader(xml)));
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			logger.error("DocumentBuilder configuration problem");
		} catch (UnsupportedEncodingException e) {
			logger.error("Encoding format is not supported");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not possible to parse XML from Web Server");
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error("Cannot close Writer");
			}
		}

		return doc;
	}

	/**
	 * Gets the value of the selected tag from config.xml. 
	 *
	 * @param 	tag 		desired value to recover
	 * @param 	element 	element with the xml string
	 * @return 				value
	 */
	private static String getValue(String tag, Element element)
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	/**
	 * The Class ButtonRenderer.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer
	{
		/** Serializable version number */
		private static final long serialVersionUID = 1657789990766718498L;

		/**
		 * Instantiates a new button renderer.
		 */
		public ButtonRenderer()
		{
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 

			Image img = null;
			try {
				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e) {
				logger.error("Resource not found");
			}
			setIcon(new ImageIcon(img));
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			JButton button=new JButton("");
			button.setOpaque(true);
			button.setBorderPainted(false); 
			button.setContentAreaFilled(false); 

			Image img = null;
			try {
				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e) {
				logger.error("Resource not found");
			}
			button.setIcon(new ImageIcon(img));

			return button;
		}
	}

	/**
	 * The Class ButtonEditor.
	 */
	class ButtonEditor extends DefaultCellEditor
	{
		/** Serializable version number */
		private static final long serialVersionUID = 1L;

		/** The button. */
		protected JButton button;

		/** The label. */
		private String label;

		/** The rows. */
		int rows;

		/** True when the button is pushed. */
		private boolean isPushed;

		/**
		 * Instantiates a new button editor.
		 *
		 * @param checkBox the check box
		 */
		public ButtonEditor(JCheckBox checkBox)
		{
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.setBorderPainted(false); 
			button.setContentAreaFilled(false); 

			Image img = null;
			try {
				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e) {
				logger.error("Resource not found");
			}
			button.setIcon(new ImageIcon(img));
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
		 */
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			label = (value == null) ? "" : value.toString();
			isPushed = true;
			rows=row;
			test=(String) table.getValueAt(row, 0);
			return button;
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue()
		{
			if (isPushed&&!running) {
				selectSample(doc);
			} else{
				JOptionPane.showMessageDialog(button, "Wait until the end of the execution");
			}
			isPushed = false;
			return new String(label);
		}
	}

	/**
	 * Gets the configuration value from config.xml.
	 *
	 * @param 	key 	the key whose value is going to be obtained
	 * @return 			value
	 */
	private String getConfigValue(String key)
	{
		File cfFile = new File("config.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		Element element = null;
		
		logger.debug("Retrieving: "+key);
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(cfFile);
			element = (Element) doc.getElementsByTagName("Configuration").item(0);
		} catch (ParserConfigurationException e) {
			logger.error("DB configuration problem");
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Configuration file not found");
		}
		
		return getValue(key, element);
	}

	/**
	 * Runs a Test Case
	 *
	 * @param 	test 	name of the Test Case to be executed
	 */
	private void runTestCase(String test)
	{
		String path = (new File("")).getAbsolutePath();
		String releaseVersion=mainWindow.getTestcasesPackageVersion(version);
		ProcessBuilder pb = null;
		
		capturePane = new CapturePane(testCasesWindow);
		
		/*String fonts[] = 
			      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

			    for ( int i = 0; i < fonts.length; i++ )
			    {
			      System.out.println(fonts[i]);
			    }*/
		
		System.setOut(new PrintStream(new StreamCapturer(capturePane, System.out)));
		
		pb = new ProcessBuilder("java",
				"-Djava.library.path="+path+File.separator+"lib"+File.separator+version,
				"-Dfile.encoding=UTF-8","-jar", path+File.separator+getConfigValue("TestCasesPackagePath")
				+File.separator+"TestCases_Package_"+version
				+"_"+releaseVersion+".jar", test, "\\cfg.xml");
		pb.directory(new File(path));
		
		running=true;
		sendedResults=false;
		try {
			testCaseProcess = pb.start();

			LogStreamReader lsr = new LogStreamReader(testCaseProcess.getInputStream());
			LogStreamReader lsr2 = new LogStreamReader(testCaseProcess.getErrorStream());
			executionThread = new Thread(lsr, "LogStreamReader");
			executionThread.start();

			Thread errorLoggerThread = new Thread(lsr2, "errorLoggerThread");
			errorLoggerThread.start();

			getLogFrame();
		} catch (IOException e) {
			logger.error("Cannot retrieve logs from Test Case execution");
		}
	}

	/**
	 * The Class LogStreamReader.
	 */
	class LogStreamReader implements Runnable
	{
		/** The reader. */
		private BufferedReader reader;

		/**
		 * Instantiates a new log stream reader.
		 *
		 * @param	is 	input stream with the logs of the execution
		 */
		public LogStreamReader(InputStream is)
		{
			this.reader = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8));
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			try {
				String line;
				while ((line = reader.readLine()) != null) {					
					System.out.println(line);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the frame that will display the execution of the Test Case.
	 */
	private void getLogFrame() {
		logFrame = new JDialog();
		logFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		logFrame.setResizable(false);
		logFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.99, 0.01};		
		logFrame.setLayout(gridBagLayout);		
		GridBagConstraints gbc_capturePane = new GridBagConstraints();	
		gbc_capturePane.gridx = 0;
		gbc_capturePane.gridy = 0;
		gbc_capturePane.fill=GridBagConstraints.BOTH;
		logFrame.add(capturePane, gbc_capturePane);		
		logFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				if(!running){
					saveResults();
				}else{
					JOptionPane.showMessageDialog(null, "Wait for verdict");
				}
			}
		});		
		sendButton=new JButton("");
		Image img = null;
		try {
			img = ImageIO.read(new File("res\\drawable\\ico_stop.jpg"));
		} catch (IOException e2) {
			logger.error("Resource not found");
		}
		sendButton.setIcon(new ImageIcon(img));
		Dimension preferredSize=new Dimension(83,23);
		sendButton.setPreferredSize(preferredSize);

		sendButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(!running){
					saveResults();
				}else{
					stopTestCaseExecution();
				}
			}
		});
		
		GridBagConstraints gbc_sendButton = new GridBagConstraints();

		gbc_sendButton.gridx = 0;
		gbc_sendButton.gridy = 1;

		logFrame.add(sendButton,gbc_sendButton);
		logFrame.setBounds(100, 100, 700, 300);
		logFrame.setLocationRelativeTo(null);
		logFrame.setVisible(true);

		img = null;
		try {
			img = ImageIO.read(new File("res\\drawable\\save.jpg"));
		} catch (IOException e2) {
			logger.error("Resource not found");
		}
		sendButton.setIcon(new ImageIcon(img));
	}

	/**
	 * Shows sample selector before Test Case execution.
	 *
	 * @param 	doc 	document that contains samples information
	 */
	public void selectSample(Document doc)
	{
		selectedSample=-1;

		NodeList samples = doc.getElementsByTagName("Sample");
		int size=samples.getLength();
		if(size>1){
			swVer=new String[size];
			DeviceId=new String[size];
			hwVer=new String[size];
			AppId=new String[size];
			
			Object col[] = {"Device Id",
					"App Id", "Software Version","Hardware Version"};

			TableModel model = new DefaultTableModel(col,size){
				
				private static final long serialVersionUID = -5114222498322422255L;

				public boolean isCellEditable(int row, int column)
				{					
					return false;
				}
			};

			tableSample = new JTable(model);
			tableSample.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane scrollPane = new JScrollPane(tableSample);

			selectSample = new JDialog();
			selectSample.setResizable(false);
			selectSample.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int width=500;
			int height=300;
			Rectangle bounds = new Rectangle((int) (dim.width/2)-width/2, 
					(int) (dim.height/2)-height/2,
					width, 
					height);
			
			selectSample.setBounds(bounds);

			for (int i = 0; i < samples.getLength(); i++) {
				Node sample = samples.item(i);			
				Element element = (Element) sample;
				swVer[i]=getValue("swVer", element);

				DeviceId[i]=getValue("DeviceId", element);

				hwVer[i]=getValue("hwVer", element);

				AppId[i]=getValue("AppId", element);	
				tableSample.setValueAt(DeviceId[i],i,0);
				tableSample.setValueAt(AppId[i],i,1);
				tableSample.setValueAt(swVer[i],i,2);
				tableSample.setValueAt(hwVer[i],i,3);
			}

			selectSample.setTitle("Select a Sample");

			GridBagLayout gridBagLayout = new GridBagLayout();

			gridBagLayout.columnWeights = new double[]{0.9};
			gridBagLayout.rowWeights = new double[]{ 0.99, 0.01};
			selectSample.setLayout(gridBagLayout);

			GridBagConstraints gbc_table = new GridBagConstraints();

			gbc_table.insets = new Insets(0, 0, 0, 0);
			gbc_table.gridx = 0;
			gbc_table.gridy = 0;
			gbc_table.fill=GridBagConstraints.BOTH;

			selectSample.add(scrollPane, gbc_table);

			JButton buttonSample=new JButton("Next");

			Dimension preferredButtonSize=new Dimension(55,11);
			buttonSample.setPreferredSize(preferredButtonSize);

			GridBagConstraints gbc_button = new GridBagConstraints();	

			gbc_button.gridx = 0;
			gbc_button.gridy = 1;

			selectSample.add(buttonSample, gbc_button);

			buttonSample.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					selectedSample=tableSample.getSelectedRow();
					if(selectedSample!=-1){
						selectSample.dispose();
						testCasesWindow.setSampleIxit(selectedSample);
						if(!runAllTestCases){
							testCasesWindow.runTestCase(test);
						}else{
							runAll();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Select a Sample");
					}
				}
			});
			selectSample.setVisible(true);
			selectSample.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}else{//Only 1 Sample
			if(!runAllTestCases){
				testCasesWindow.runTestCase(test);
			}else{
				runAll();
			}
		}
		return;
	}

	/**
	 * Runs all the Test Cases.
	 */
	private void runAll()
	{
		String testCase = null;
		for(int i=0;i<TestCases.length;i++){
			testCase = (String)table.getValueAt(i, 0);
			runTestCase(testCase);
			if(!runAllTestCases){
				break;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.warn("Waiting time was interrupted");
			}
		}
	}

	/**
	 * Selects sample and run all the test cases.
	 */
	protected void runAllTestCases()
	{
		logger.info("Running all Testcases");
		runAllTestCases=true;
		selectSample(doc);
		runAllTestCases=false;
	}
	
	/**
	 * Sets the sample IXITs.
	 *
	 * @param 	selectedSample 	sample used to run Test Cases
	 */
	protected void setSampleIxit(int selectedSample)
	{
		NodeList ixit = doc.getElementsByTagName("Ixit");

		for (int i = 0; i < ixit.getLength(); i++) {
			Node node = ixit.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String ixitName=getValue("Name", element);

				if(ixitName.equals("IXITCO_DeviceId")) {
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(DeviceId[selectedSample]);
					logger.debug("Selected sample Device ID: "+DeviceId[selectedSample]);
				} else if(ixitName.equals("IXITCO_SoftwareVersion")) {
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(swVer[selectedSample]);
					logger.debug("Selected sample Software Version: "+swVer[selectedSample]);
				} else if(ixitName.equals("IXITCO_HardwareVersion")) {
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(hwVer[selectedSample]);
					logger.debug("Selected sample Hardware Version: "+hwVer[selectedSample]);
				} else if(ixitName.equals("IXITCO_AppId")) {
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(AppId[selectedSample]);
					logger.debug("Selected sample App ID: "+AppId[selectedSample]);
				}
			}
		}

		//Generate XML
		Source source = new DOMSource(doc);

		Result result = new StreamResult(new java.io.File("\\cfg.xml"));
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			logger.error("Not possible to generate cfg.xml");
			e.printStackTrace();
		}
	}

	/**
	 * Saves the log.
	 *
	 * @param 	log 	log contents
	 */
	protected void saveLog(String log)
	{
		Document docu=getResultDoc();
		if(docu!=null) {
			NodeList result = docu.getElementsByTagName("TestCase");
			int lastResult=result.getLength()-1;

			Node node = result.item(lastResult);
			Element element = (Element) node;

			String logName=getValue("LogFile", element);
			PrintWriter out = null;
			String logPath=getConfigValue("LogPath");
			try {
				File dir = new File(logPath);
				if(!dir.exists()){
					dir.mkdirs();
				}
				out = new PrintWriter(logPath+logName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Encoding format not supported");
			} catch (FileNotFoundException e) {
				logger.error("Output file not found");
			}
			out.println(log);
			out.close();
		} else {
			JOptionPane.showMessageDialog(null, "Log not generated");
		}
	}

	/**
	 * Enables send results button or if it�s running all the Test Cases sends them automatically.
	 */
	public void enableSendResults()
	{
		if(running){
			running=false;
			Image img = null;
			try {
				img = ImageIO.read(new File("res\\drawable\\save.jpg"));
			} catch (IOException e2) {
				logger.error("Resource not found");
			}
			if(!runAllTestCases){
				if(dialogEndRunAll!=null){
					dialogEndRunAll.dispose();
				}
			}
			sendButton.setIcon(new ImageIcon(img));
			if(runAllTestCases&&!sendedResults){
				try {     
					//wait for Results.xml to be generated
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.warn("Waiting time was interrupted");
				}
				saveResults();			
			}
		}
	}

	/**
	 * Stops Test Case execution.
	 */
	private void stopTestCaseExecution()
	{
		String msg="Do you want to finish the execution?";
		JTextPane textPane = new JTextPane();
		textPane.setText(msg);
		textPane.setEditable(false);
		textPane.setSize(new Dimension(480, 150));
		textPane.setPreferredSize(new Dimension(480, 150));
		JScrollPane scroll=new JScrollPane(textPane);
		dialogEndRunAll =new JDialog();
		Rectangle bounds = null ;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width=500;
		int height=200;
		bounds = new Rectangle((int) (dim.width/2)-width/2, 
				(int) (dim.height/2)-height/2,
				width, 
				height);
		dialogEndRunAll.setBounds(bounds);
		dialogEndRunAll.add(scroll,BorderLayout.CENTER);
		dialogEndRunAll.setResizable(false);
		JButton buttonYes=new JButton("Yes");
		buttonYes.setForeground(new Color(255, 255, 255));
		buttonYes.setBackground(new Color(68, 140, 178));
		buttonYes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("Stopped Testcase Execution");

				running=false;
				runAllTestCases=false;
				testCaseProcess.destroy();
				//capturePane.deleteLog();
				capturePane.clear();
				capturePane.removeAll();
				capturePane=null;
				sendedResults=true;
				executionThread.interrupt();

				dialogEndRunAll.dispose();
				logFrame.dispose();

				System.out.close();
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			}
		});
		
		JButton buttonNo=new JButton("No");
		buttonNo.setForeground(new Color(255, 255, 255));
		buttonNo.setBackground(new Color(68, 140, 178));
		buttonNo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				runAllTestCases=true;
				dialogEndRunAll.dispose();
			}});

		JPanel buttonPanel=new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5, 0.5};
		gridBagLayout.rowWeights = new double[]{1.0};
		buttonPanel.setLayout(gridBagLayout);
		GridBagConstraints gbc_yes = new GridBagConstraints();
		gbc_yes.gridx = 0;
		gbc_yes.gridy = 0;
		gbc_yes.anchor=GridBagConstraints.EAST;
		buttonPanel.add(buttonYes,gbc_yes);		
		gbc_yes.insets=new Insets(20,0,20,0);
		GridBagConstraints gbc_no = new GridBagConstraints();
		gbc_no.gridx = 1;
		gbc_no.gridy = 0;
		gbc_no.anchor=GridBagConstraints.WEST;
		buttonPanel.add(buttonNo,gbc_no);
		dialogEndRunAll.add(buttonPanel,BorderLayout.SOUTH);
		dialogEndRunAll.setAlwaysOnTop(true); 
		dialogEndRunAll.setModal(true);
		dialogEndRunAll.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogEndRunAll.setVisible(true);
	}
}

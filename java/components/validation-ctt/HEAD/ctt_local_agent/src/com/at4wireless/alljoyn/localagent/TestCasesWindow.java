/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
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
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import org.json.JSONArray;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * The Class TestCasesWindow.
 */
public class TestCasesWindow extends JPanel { 

	/** The test cases window. */
	TestCasesWindow testCasesWindow;

	/** The executing testcase row. */
	private int executingTestcaseRow;
	
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
	
	
	

	/** The main window class. */
	MainWindow mainWindow=null;


	/**
	 * Instantiates a new test cases window.
	 *
	 * @param mainWindow the main window
	 * @param id the project id
	 * @param version the TestCase Package version
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 * @throws SAXException the SAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TestCasesWindow(MainWindow mainWindow,int id, String version, String user,String token) throws SAXException, ParserConfigurationException, IOException {
		this.mainWindow= mainWindow;
		this.version=version;

		testCasesWindow=this;
		Object col[] = {"Test Case","Description",
				"Certification Release","Action", "Result",
		"Date & Time"};//,"Last Verdict","Last Execution"};
		this.id=id;
		this.user=user;
		this.token=token;
		doc=getXML(id, user,token);
		tableSize = getTableSize(doc);
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
		getTestCases(table,doc);
		table.getColumn("Action").setMaxWidth(80);

		table.getColumn("Action").setMinWidth(80);
		table.getColumn("Action").setCellRenderer(new ButtonRenderer());
		table.getColumn("Action").setCellEditor(
				new ButtonEditor(new JCheckBox()));
	}
	/**
	 * Gets the table size.
	 *
	 * @param doc the xml with the list of testcases
	 * @return the table size
	 */
	private int getTableSize(Document doc) {
		NodeList projects = doc.getElementsByTagName("TestCase");
		return projects.getLength();
	}

	/**
	 * Gets the Test Cases from the document and fill the table with it´s values.
	 *
	 * @param table the table
	 * @param doc the document that contains the Test Cases values
	 * @return the test cases
	 */
	private void getTestCases(JTable table,Document doc) {
		NodeList projects = doc.getElementsByTagName("TestCase");
		for (int i = 0; i < projects.getLength(); i++) {
			Node node = projects.item(i);
			Element element = (Element) node;
			String description=getValue("Description", element);
			String name=getValue("Name", element);
			table.setValueAt(name,i,0);
			TestCases[i]=name;
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
	protected void saveResults() {
		getResults();
		saveLog(capturePane.getLog());
		logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		capturePane.deleteLog();
		running=false;
		capturePane.removeAll();
		logFrame.dispose();
		sendResults();
		System.out.close();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}

	/**
	 * Send results to the web server.
	 */
	protected void sendResults() {
		sendedResults=true; 
		HttpResponse responseCode=sendResultsXML();
		if(responseCode.getStatusLine().getStatusCode()==200){
			HttpResponse response=sendLog();
			if(response.getStatusLine().getStatusCode()==200){
				File log=getLog(getLogName());
				log.delete();
				File res = new File("Results.xml");
				res.delete();

			}	
		}
	}


	/**
	 * Send log to web server.
	 *
	 * @return the http response
	 */
	private HttpResponse sendLog() {
		String sendLogUrl=getConfigValue("TestToolWebAppUrl")+getConfigValue("UploadLogFile")+user+"/"+id;
		HttpPost post = new HttpPost(sendLogUrl);
		post.addHeader("Authorization", "bearer "+token);
		String logName=getLogName();
		File log=getLog(logName);
		String hash=hashFile(log);
		System.out.println(hash);
		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.addBinaryBody("file", log)
				.addTextBody("name", logName)
				.addTextBody("hash", hash)
				.build();
		post.setEntity(httpEntity);
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse resp = null;
		try {
			resp = httpClient.execute(post);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println(resp.getStatusLine().getStatusCode());
		//if(resp.getStatusLine().getStatusCode()==200){
		log.delete();
		//}		
		return resp;	
	}

	/**
	 * Hash file.
	 *
	 * @param file the file
	 * @return the string that contains the hash
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private String hashFile(File file) {
		String algorithm="MD5";
		FileInputStream inputStream = null;	
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytesBuffer = new byte[1024];
		int bytesRead = -1;
		try {
			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] hashedBytes = digest.digest();
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertByteArrayToHexString(hashedBytes);

	}





	/**
	 * Convert byte array to hex string.
	 *
	 * @param arrayBytes the array bytes
	 * @return the string that contains the hash
	 */
	private String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}
	/**
	 * Send results xml to web server.
	 *
	 * @return the http response
	 */
	private HttpResponse sendResultsXML() {

		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("SendResultsUrl")+user+"/"+id;

		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(url);
		postRequest.addHeader("Authorization", "bearer "+token);
		StringEntity input = null;
		String resultXML=getResult();
		try {
			input = new StringEntity(resultXML);
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		input.setContentType("application/xml");
		postRequest.setEntity(input);
		HttpResponse response = null;
		try {
			response = httpClient.execute(postRequest);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return response;
	}


	/**
	 * Gets the log of the last executed Test.
	 *
	 * @param logName the log name
	 * @return the log
	 */
	private File getLog(String logName) {

		String logPath=getConfigValue("LogPath");
		File log = new File(logPath+logName);

		return log;
	}

	/**
	 * Gets the log name of the last executed Test.
	 *
	 * @return the log name
	 */
	private String getLogName() {

		String logName="";


		File resultXML = new File("Results.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document doc=null;
		try {

			doc = dBuilder.parse(resultXML);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList testResult = doc.getElementsByTagName("Results");

		Node node = testResult.item(0);



		Element element = (Element) node;


		logName=getValue("LogFile", element);



		return logName;
	}

	/**
	 * Gets the result of the last executed Test..
	 *
	 * @return the result
	 */
	private String getResult() {
		String result="";


		try {
			result = readFile("Results.xml", StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return result;
	}


	/**
	 * Read file.
	 *
	 * @param path the path
	 * @param encoding the encoding
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
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
	protected void getResults() {
		Document docu=getResultDoc();
		if(docu!=null){
			NodeList result = docu.getElementsByTagName("TestCase");
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				Element element = (Element) node;
				String name=getValue("Name", element);
				String dateTime=getValue("DateTime", element);
				String verdict=getValue("Verdict", element);
				for (int j = 0; j < tableSize ; j++) {
					if(name.equals(table.getValueAt(j, 0))){


						table.setValueAt(verdict,j,4);
						table.setValueAt(dateTime,j,5);
						//table.setValueAt(verdict,j,6);
						//table.setValueAt(dateTime,j,7);
					}
				}
			}
		}else{
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
					//table.setValueAt(res,j,6);
					//table.setValueAt(timeStamp,j,7);

				}
			}

		}




	}

	/**
	 * Gets the result doc of the last executed Test.
	 *
	 * @return the result doc
	 */
	private Document getResultDoc() {


		File test = new File("Results.xml");



		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Document docu = null;
		try {


			docu = dBuilder.parse(test);
			docu.getDocumentElement().normalize();

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return docu;
	}

	/**
	 * Gets the jsons string from the web server and convert it to a xml document.
	 *
	 * @param id the project id.
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 * @return the xml
	 */
	private static Document getXML(int id,String user,String token)  {
		Document doc = null;
		String test = "";
		Writer writer = null;
		String path = File.separator+"cfg.xml";
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetTestCases");
		try {
			URI URI = new URI(url+user+"/"+id);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);
			postRequest.addHeader("Authorization", "bearer "+token);
			HttpResponse response = httpClient.execute(postRequest);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(entity.getContent())));
			String output;
			while ((output = br.readLine()) != null) {

				test=test+output;
			}

		} catch (IOException e1) {

			e1.printStackTrace();



			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server. The application will close"
					);
			System.exit(0);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			JSONArray json=new JSONArray("["+test+"]");
			String xml="";
			for(int i=0;i<json.length();i++){
				System.out.println(i);
				xml=xml+XML.toString(json.get(0));
			}
			System.out.println(xml);
			try{
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path), "utf-8"));
				writer.write(xml);
			} catch (IOException ex) {
				ex.printStackTrace();

			} finally {
				try {;

				writer.close();
				} catch (Exception ex) {}
			}




			doc = dBuilder.parse(new InputSource(new StringReader(xml)));



		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		doc.getDocumentElement().normalize();
		return doc;
	}


	/**
	 * Gets the value from the selected tag. 
	 *
	 * @param tag the tag
	 * @param element the element
	 * @return the value
	 */
	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);

		return node.getNodeValue();
	}


	/**
	 * The Class ButtonRenderer.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {




		/**
		 * 
		 */
		private static final long serialVersionUID = 1657789990766718498L;

		/**
		 * Instantiates a new button renderer.
		 */
		public ButtonRenderer() {
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 

			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			setIcon(new ImageIcon(img));
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			JButton button=new JButton("");
			button.setOpaque(true);
			button.setBorderPainted(false); 
			button.setContentAreaFilled(false); 

			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			button.setIcon(new ImageIcon(img));

			return button;
		}
	}

	/**
	 * The Class ButtonEditor.
	 */
	class ButtonEditor extends DefaultCellEditor {
		/**
		 * 
		 */
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
		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.setBorderPainted(false); 
			button.setContentAreaFilled(false); 

			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\run.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
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
				boolean isSelected, int row, int column) {


			label = (value == null) ? "" : value.toString();
			isPushed = true;
			rows=row;
			test=(String) table.getValueAt(row, 0);
			return button;
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue() {
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
	 * Gets the config value.
	 *
	 * @param key the key
	 * @return the config value
	 */
	static String getConfigValue(String key) {
		String value="";
		File test = new File("config.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document conf=null;
		try {
			conf = dBuilder.parse(test);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList projects = conf.getElementsByTagName("Configuration");
		Node node = projects.item(0);
		Element element = (Element) node;

		value=getValue(key, element);


		return value;
	}

	/**
	 * Run test case.
	 *
	 * @param test the test
	 * @param row the row
	 */
	private void runTestCase(String test,int row) {

		executingTestcaseRow=row;
		
		
		capturePane  = new CapturePane(testCasesWindow);
		System.setOut(new PrintStream(new StreamCapturer("STDOUT", capturePane, System.out)));
		
		//System.setOut(new PrintStream(new StreamCapturer("STDOUT", capturePane, System.err)));

		running=true;
		sendedResults=false;
		String path = (new File("")).getAbsolutePath();
		int releaseVersion=mainWindow.getTestcasesPackageReleaseVersion(version);
		ProcessBuilder pb = new ProcessBuilder("java",
				"-Djava.library.path="+path+File.separator+"lib"+File.separator+version,
				"-jar", path+File.separator+getConfigValue("TestCasesPackagePath")
				+File.separator+"TestCases_Package_"+version+
				"_R"+releaseVersion+".jar", test, "\\cfg.xml");
		pb.directory(new File(path));
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
			e.printStackTrace();
		}


	}


	/**
	 * The Class LogStreamReader.
	 */
	class LogStreamReader implements Runnable {

		/** The reader. */
		private BufferedReader reader;

		/**
		 * Instantiates a new log stream reader.
		 *
		 * @param is the is
		 */
		public LogStreamReader(InputStream is) {
			this.reader = new BufferedReader(new InputStreamReader(is));
		}

		

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				String line = reader.readLine();
				while (line != null) {
					System.out.println(line);
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Gets the log frame that shows the execution of the testcase.
	 *
	 * @return the log frame
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
			public void windowClosing(WindowEvent e) {
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
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sendButton.setIcon(new ImageIcon(img));
		Dimension preferredSize=new Dimension(83,23);
		sendButton.setPreferredSize(preferredSize);
		
		sendButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sendButton.setIcon(new ImageIcon(img));

	}



	/**
	 * Select sample and runs testcases.
	 *
	 * @param doc the doc
	 */
	public void selectSample(Document doc) {
		selectedSample=-1;

		NodeList samples = doc.getElementsByTagName("Sample");
		int size=samples.getLength();
		if(size>1){
			swVer=new String[size] ;
			DeviceId=new String[size] ;
			hwVer=new String[size] ;
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
		
			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\ico_next.png"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}			
			//buttonSample.setIcon(new ImageIcon(img));						
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
							testCasesWindow.runTestCase(test,table.getSelectedRow());
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
				testCasesWindow.runTestCase(test,table.getSelectedRow());
			}else{
				runAll();
			}
		}
		return;
	}

	/**
	 * Run all the testcases.
	 */
	private void runAll() {
		for(int i=0;i<TestCases.length;i++){
			String testCase=(String) table.getValueAt(i, 0);

			runTestCase(testCase,i);
			if(!runAllTestCases){
				break;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}



	/**
	 * Select sample and run all the test cases.
	 */
	protected void runAllTestCases() {
		runAllTestCases=true;
		selectSample(doc);
		runAllTestCases=false;

	}



	/**
	 * Sets the sample ixits.
	 *
	 * @param selectedSample the sample used to run testcases
	 */
	protected void setSampleIxit(int selectedSample) {
		
		NodeList ixit = doc.getElementsByTagName("Ixit");


		for (int i = 0; i < ixit.getLength(); i++) {
			Node node = ixit.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String ixitName=getValue("Name", element);

				if(ixitName.equals("IXITCO_DeviceId")){
				
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(DeviceId[selectedSample]);
				}
				else if(ixitName.equals("IXITCO_SoftwareVersion")){
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(swVer[selectedSample]);
				}
				else if(ixitName.equals("IXITCO_HardwareVersion")){
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(hwVer[selectedSample]);
				}
				else if(ixitName.equals("IXITCO_SoftwareVersion")){
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(swVer[selectedSample]);
				}
				else if(ixitName.equals("IXITCO_AppId")){
					NodeList nodes = element.getElementsByTagName("Value");
					nodes.item(0).setTextContent(AppId[selectedSample]);
					
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}

	/**
	 * Save the log.
	 *
	 * @param log the log
	 */
	protected void saveLog(String log)  {
		Document docu=getResultDoc();
		if(docu!=null){
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
			} catch (FileNotFoundException e) {

				e.printStackTrace();
				System.out.println("FileNotFoundException");
			} catch (UnsupportedEncodingException e) {
				// 
				e.printStackTrace();
				System.out.println("UnsupportedEncodingException");
			}
			out.println(log);
			out.close();

		}else{
			JOptionPane.showMessageDialog(null, "Log not generated");

		}

	}


	/**
	 * Enable send results button or if it´s running all the testcases send it automatically.
	 */
	public void enableSendResults() {
		if(running){
			running=false;
			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\save.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveResults();			
			}
		}
	}




	/**
	 * Stops testcase execution.
	 */
	private void stopTestCaseExecution() {
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
				running=false;
				runAllTestCases=false;
				testCaseProcess.destroy();
				capturePane.deleteLog();
				capturePane.removeAll();
				capturePane=null;
				sendedResults=true;
				executionThread.interrupt();
			
				dialogEndRunAll.dispose();
				logFrame.dispose();
				Date utilDate = new java.util.Date(); 
				long lnMilisec = utilDate.getTime();
				Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisec);
				String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(sqlTimestamp);
				//table.setValueAt(timeStamp,executingTestcaseRow,5);
				System.out.close();
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			}});
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

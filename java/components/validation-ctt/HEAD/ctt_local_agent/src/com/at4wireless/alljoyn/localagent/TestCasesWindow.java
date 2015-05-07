/**
 *Author: AT4 Wireless
 *Date: 2015/02/12
 *Version: 1
 *Description: It displays the test cases Window 
 *and we can use it to run the test cases.
 *
 */
package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class TestCasesWindow extends JPanel {

	TestCasesWindow testCasesWindow;
	private String test;
	
	private static final long serialVersionUID = 7430591674214212172L;
	private JTable table;
	private Boolean runAllTestCases=false;
	private Boolean sendedResults=false;
	private JButton sendButton;
	private String[] TestCases;
	static Document doc;
	private String version;
	private JDialog selectSample;
	JTable tableSample;
	String[] swVer;
	String[] DeviceId;
	String[] hwVer;
	String[] AppId;
	int id; 
	int selectedSample=0;
	String user;
	String token;
	int tableSize;
	Boolean running=false;
	JDialog logFrame;
	CapturePane capturePane;
	Thread executionThread;
	Boolean loadLib=false;
	MainWindow mainWindow=null;

	URLClassLoader loader;

	public TestCasesWindow(MainWindow mainWindow,int id, String version, String user,String token, URLClassLoader loader) throws SAXException, ParserConfigurationException, IOException {

		this.loader = loader;
		this.mainWindow= mainWindow;
				this.version=version;
		loadLib=true;
		testCasesWindow=this;
		Object col[] = {"Test Case","Description",
				"Certification Release","Action", "Result",
		"Date & Time"};


		this.id=id;
		this.user=user;
		this.token=token;
		doc=getXML(id, user,token);
		
		tableSize = getTableSize(doc);

		TestCases=new String[tableSize];


		TableModel model = new DefaultTableModel(col,tableSize);
		
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5};
		gridBagLayout.rowWeights = new double[]{0.5, 0.8};
		
		setLayout(gridBagLayout);
		
		
		GridBagConstraints gbc_header = new GridBagConstraints();
		
		
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;

		add(mainWindow.getHeaderPanel(),gbc_header);

		table=new JTable(model){
		    /**
			 * 
			 */
			private static final long serialVersionUID = -8369977836878349660L;

			public Component prepareRenderer(
		            TableCellRenderer renderer, int row, int column)
		        {
	            Component comp = super.prepareRenderer(renderer, row, column);
	            comp.setForeground(Color.black);
	            Font font=new Font("Arial", Font.BOLD, 12);
	            comp.setFont(font);
	            if(column==4){
	            	if("INCONC".equals(table.getModel().getValueAt(row, column))){
	            		comp.setForeground(new Color(168,161,0));
	            		
	            	}else if("PASS".equals(table.getModel().getValueAt(row, column))){
	            		comp.setForeground(new Color(103,154,0));
	            	}else if("FAIL".equals(table.getModel().getValueAt(row, column))){
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



	private int getTableSize(Document doc) {

		NodeList projects = doc.getElementsByTagName("TestCase");
		return projects.getLength();
	}

	private void getTestCases(JTable table,Document doc) {

		NodeList projects = doc.getElementsByTagName("TestCase");


		for (int i = 0; i < projects.getLength(); i++) {

			Node node = projects.item(i);

			Element element = (Element) node;


			String id=getValue("Id", element);

			String description=getValue("Description", element);


			String name=getValue("Name", element);

			table.setValueAt(name,i,0);

			TestCases[i]=name;

			table.setValueAt(description,i,1);

			table.setValueAt(version,i,2);



			//table.setValueAt("Run",i,3);

			table.setValueAt("Not Executed",i,4);

		}


	}



	protected void saveResults() {
		//wait for the execution to finish
		while(executionThread.isAlive()){ 

			synchronized(this){
				try {
					wait(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		getResults();
		saveLog(capturePane.getLog());
		logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		capturePane.deleteLog();
		running=false;
		capturePane.removeAll();
		logFrame.dispose();
		sendResults();
		/*SendResultsWindow resultWindow=new SendResultsWindow(mainWindow,id,user);
		resultWindow.setModalityType(JDialog.DEFAULT_MODALITY_TYPE.APPLICATION_MODAL);
		resultWindow.setProjectId(id);
		resultWindow.setUser(user);
		resultWindow.setToken(token);
		resultWindow.setBounds(100, 100, 700, 300);
		resultWindow.setLocationRelativeTo(null);
		resultWindow.setVisible(true);
		 */

		System.out.close();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
	
	protected void sendResults() {
		sendedResults=true; // You press send and we can close the window
		HttpResponse responseCode=sendResultsXML();

		//OK: Sending Log file.
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
	
	
	private HttpResponse sendLog() {


		String sendLogUrl=getConfigValue("TestToolWebAppUrl")+getConfigValue("UploadLogFile")+user+"/"+id;
		System.out.println(sendLogUrl);
		HttpPost post = new HttpPost(sendLogUrl);


		post.addHeader("Authorization", "bearer "+token);

		String logName=getLogName();
		File log=getLog(logName);
		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.addBinaryBody("file", log)
				.addTextBody("name", logName)
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
		System.out.println("Response code: "+resp.getStatusLine().getStatusCode());
		System.out.println(resp.toString());
		if(resp.getStatusLine().getStatusCode()==200){
			log.delete();
		}		
		return resp;	}

	private HttpResponse sendResultsXML() {
		System.out.println("Sending result");
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("SendResultsUrl")+user+"/"+id;
		System.out.println(url);
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
		System.out.println("Response code: "+response.getStatusLine().getStatusCode());
		return response;
	}

	
	private File getLog(String logName) {

		String logPath=getConfigValue("LogPath");
		File log = new File(logPath+logName);

		return log;
	}

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


	static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

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
					}
				}
			}
		}else{
			for (int j = 0; j < tableSize ; j++) {
				if("INCONC".equals(table.getValueAt(j, 4))){

					JTextField res=new JTextField("INCONC");
					
					res.setForeground(Color.YELLOW);
					table.setValueAt(res,j,4);
					
					Date utilDate = new java.util.Date(); //fecha actual
					long lnMilisec = utilDate.getTime();

					Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisec);

					String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(sqlTimestamp);

					table.setValueAt(timeStamp,j,5);

				}
			}

		}




	}

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

	private static Document getXML(int id,String user,String token)  {


		Document doc = null;

		String test = "";

		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetTestCases");
		try {
			URI URI = new URI(url+user+"/"+id);
			System.out.println(url+user);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);
			postRequest.addHeader("Authorization", "bearer "+token);
			HttpResponse response = httpClient.execute(postRequest);
			HttpEntity entity = response.getEntity();
			System.out.println("Entity:"+entity);
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

			System.out.println(test);


			JSONArray json=new JSONArray("["+test+"]");

			String xml="";





			for(int i=0;i<json.length();i++){

				System.out.println(i);

				xml=xml+XML.toString(json.get(0));
			}





			System.out.println(xml);


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


	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);

		return node.getNodeValue();
	}


	class ButtonRenderer extends JButton implements TableCellRenderer {

		/**
		 * 
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
	 * @version 1.0 11/09/98
	 */
	class ButtonEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JButton button;

		private String label;

		int rows;
		private boolean isPushed;

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
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			
			if (isSelected) {
			//	button.setForeground(table.getSelectionForeground());
			//	button.setBackground(table.getSelectionBackground());
			} else {
			//	button.setForeground(table.getForeground());
				//button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			//button.setText(label);
			isPushed = true;
			rows=row;
			test=(String) table.getValueAt(row, 0);
			return button;
		}
		public Object getCellEditorValue() {
			if (isPushed&&!running) {
				selectSample(doc);


			} else{
				JOptionPane.showMessageDialog(button, "Wait until the end of the execution");
			}
			isPushed = false;
			return new String(label);
		}

		/*public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}
		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}*/
	}


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

		System.out.println(value);
		return value;
	}

	public void runTestCase(String test,int row) {
		try {
			Class localAgent = Class.forName("com.at4wireless.alljoyn.Manager", true, loader);
			if(localAgent!=null){
				Object obj = null;
			



				try {
					Constructor<?> constructor = localAgent.getConstructor(String.class ,Document.class,Boolean.class);
					obj= constructor.newInstance(test, doc, loadLib);
					constructor=null;
					localAgent=null;
					loadLib=true;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();

					System.out.println("Error :"+e);
				}
				table.setValueAt("INCONC", row, 3);

				capturePane  = new CapturePane(testCasesWindow);
				System.setOut(new PrintStream(new StreamCapturer("STDOUT", capturePane, System.out)));
				executionThread=new Thread((Runnable) obj);
				running=true;
				obj=null;
				
				//JOptionPane.showMessageDialog(null, "Running: "+test);
				executionThread.start();
				getLogFrame();

			}


		} catch (ClassNotFoundException e) {
			e.printStackTrace();


		}


	}
 
	private void getLogFrame() {

		logFrame = new JDialog();
		logFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		logFrame.setResizable(false);
		logFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		logFrame.add(capturePane,BorderLayout.CENTER);
		logFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(!executionThread.isAlive()){

					
					saveResults();
				}else{
					JOptionPane.showMessageDialog(null, "Wait for verdict");
				}

			}


		});
		
		sendButton=new JButton("");
		sendButton.setBorderPainted(false); 
		sendButton.setContentAreaFilled(false); 
		 Image img = null;
		try {
			
			img = ImageIO.read(new File("res\\drawable\\save.jpg"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sendButton.setIcon(new ImageIcon(img));
		
		
		
		
		sendButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				saveResults();
			}});
		sendButton.setEnabled(false);
		logFrame.add(sendButton,BorderLayout.SOUTH);
		logFrame.setBounds(100, 100, 700, 300);
		logFrame.setLocationRelativeTo(null);
		logFrame.setVisible(true);
		
		while(executionThread.isAlive()){ 

			synchronized(this){
				try {
					System.out.println("WAITING FOR FINISH");
					wait(100);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}
		//if(!executionThread.isAlive()){
		sendButton.setEnabled(true);
		//}
		
	}



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

			TableModel model = new DefaultTableModel(col,size);

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

			JLabel label=new JLabel("Select a Sample");
			selectSample.add(label, BorderLayout.NORTH);
			selectSample.add(scrollPane, BorderLayout.CENTER);


			JButton buttonSample=new JButton("");
			buttonSample.setBorderPainted(false); 
			buttonSample.setContentAreaFilled(false); 
			 Image img = null;
			try {
				
				img = ImageIO.read(new File("res\\drawable\\ico_next.png"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			buttonSample.setIcon(new ImageIcon(img));
			
			
			selectSample.add(buttonSample, BorderLayout.SOUTH);

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
							for(int i=0;i<TestCases.length;i++){
								String testCase=TestCases[i];
								runTestCase(testCase,i);
							}
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
				for(int i=0;i<TestCases.length;i++){
					String testCase=TestCases[i];
					runTestCase(testCase,i);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			
			
			
			
		}


		}

		return;
	}



	protected void runAllTestCases() {
		runAllTestCases=true;
		selectSample(doc);
		runAllTestCases=false;

	}



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

	}



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


	private Class loadLocalAgent(String path) {


		Class localAgent = null;


		File file = new File(path);

		try {
			// Convert File to a URL
			URL url = file.toURL();          // file:/c:/myclasses/
			URL[] urls = new URL[]{url};

			// Create a new class loader with the directory
			URLClassLoader cl = new URLClassLoader(urls);

			// Load in the class; MyClass.class should be located in
			// the directory file:/c:/myclasses/com/mycompany
			localAgent = cl.loadClass("com.at4wireless.alljoyn.Manager");
			cl.close();

		} catch (MalformedURLException e) {

			System.out.println("Malformed URL");

		} catch (ClassNotFoundException e) {


			Component button = null;
			
			JOptionPane.showMessageDialog(button, "You need to install the following Test Cases Package:\n"
					+"-TestCases_Package_"+version+".jar"
					);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return localAgent;
	}



	public void enableSendResults() {
		
		sendButton.setEnabled(true);
		if(runAllTestCases){
			saveResults();			
		}
	}
















}

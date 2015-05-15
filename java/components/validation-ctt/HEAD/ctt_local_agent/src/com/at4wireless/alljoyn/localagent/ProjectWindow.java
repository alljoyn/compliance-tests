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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



// TODO: Auto-generated Javadoc
/**
 * The Class ProjectWindow.
 */

public class ProjectWindow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2003933446843668539L;

	/** The url to connect. */
	static String url="";    
	/** The table to show. */
	JTable table;

	/** The user name used to authenticate. */
	String user;

	/** The authentication token obtained when the application logs in. */
	String token;

	/** The main window. */
	MainWindow mainWindow;


	/** The Technology version array. */
	String TVersion[]=null;
	/** The project id array. */
	int projectId[]=null;
	/** The number of projects. */
	int size;
	/** The project name array. */
	String projectName[]=null;

	/**
	 * Instantiates a new project window.
	 *
	 * @param mainWindow the main window
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public ProjectWindow(final MainWindow mainWindow,final String user,final String token) throws SAXException, IOException, ParserConfigurationException {

		setBorder(null); 
		this.mainWindow=mainWindow;
		this.user=user;
		this.token=token;
		setForeground(Color.LIGHT_GRAY);


		url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetListOfProjectsUrl");


		File file = new File("Results.xml");
		file.delete();


		Object col[] = {"Project Name","Type","Created","Modified",
				"Certification Release","Associated DUT","Associated GU",
				"Configured","Results"};


		Document doc=getXML(user, token);



		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5};
		gridBagLayout.rowWeights = new double[]{0.5, 0.9};

		setLayout(gridBagLayout);



		GridBagConstraints gbc_header = new GridBagConstraints();


		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;

		add(mainWindow.getHeaderPanel(),gbc_header);



		size=getTableSize(doc);

		projectId=new int[size];
		projectName=new String[size];
		TVersion=new String[size];

		TableModel model = new DefaultTableModel(col,size){

			/**
			 * 
			 */
			private static final long serialVersionUID = -5114222498322422255L;

			public boolean isCellEditable(int row, int column)
			{
				return false;//This causes all cells to be not editable
			}


		};




		table=new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(8, false);
		table.setRowSorter(sorter);

		table.setBorder(null);


		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setViewportBorder(null);


		GridBagConstraints gbc_scrollPane = new GridBagConstraints();


		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.anchor=GridBagConstraints.NORTH;
		gbc_scrollPane.fill=GridBagConstraints.BOTH;
		add(scrollPane,gbc_scrollPane);

		table.getColumn("Results").setCellRenderer(new ButtonRenderer());



		table.getColumn("Results").setMaxWidth(80);
		table.getColumn("Results").setMinWidth(80);


		getProject(table,doc);


		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();

				if((column==8&&table.getValueAt(row, column).equals("Link to results"))){

					mainWindow.getResultsWindows(projectId[table.getSelectedRow()]);

				}
			}


		});



	}

	/**
	 * Gets the table size.
	 *
	 * @param doc the xml with the list of projects
	 * @return the table size
	 */
	private int getTableSize(Document doc) {
		int size=0;

		NodeList projects = doc.getElementsByTagName("Project");

		int projectLenght= projects.getLength();
		size=projectLenght;
		return size;

	}

	/**
	 * Gets the project from the document and fill the table with it�s values.
	 *
	 * @param table the table
	 * @param doc the document that contains the projects values
	 * 
	 */
	private void getProject(JTable table,Document doc) {



		NodeList projects = doc.getElementsByTagName("Project");


		for (int i = 0; i < projects.getLength(); i++) {

			Node node = projects.item(i);

			Element element = (Element) node;


			String id=getValue("idProject", element);

			projectId[i]=Integer.parseInt(id);

			String name=getValue("name", element);

			table.setValueAt(name,i,0);
			projectName[i]=name;

			String created=getValue("createdDate", element);



			String type=getValue("type", element);

			table.setValueAt(type,i,1);
			String date=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(Long.parseLong(created)));

			table.setValueAt(date,i,2);

			String modified=getValue("modifiedDate", element);

			date= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(Long.parseLong(modified)));

			table.setValueAt(date,i,3);
			String TechnologyVersion=getValue("certRel", element);
			TVersion[i]=TechnologyVersion;
			table.setValueAt(TechnologyVersion,i,4);
			String AssociatedDUT=getValue("dut", element);
			if(AssociatedDUT.equals("null")){
				table.setValueAt("Not selected",i,5);
			}else{
				table.setValueAt(AssociatedDUT,i,5);
			}
			String golden=getValue("golden", element);

			if(golden.equals("null")){
				table.setValueAt("N/A",i,6);
			}else{
				table.setValueAt(golden,i,6);
			}

			String configured=getValue("isConfigured", element);

			if(configured.equals("true")){
				table.setValueAt("Yes",i,7);

			}else{
				table.setValueAt("No",i,7);
			}

			String hasResult=getValue("hasResults", element);
			

			if(hasResult.equals("true")){
				table.setValueAt("Link to results",i,8);
			}else{

				table.setValueAt("No",i,8);
			}
		}
	}

	/**
	 * Gets the jsons string from the web server and convert it to a xml document.
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 * @return the document that contains the projects values
	 */
	private static Document getXML(String user, String token)  {
		Document doc = null;
		String test = "";
		try {
			URI URL = new URI(url+user);
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
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
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server"
					);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}

		try {

			

			JSONArray json=new JSONArray(test);

			String xml="<ListOfProjects>";

			for(int i=0;i<json.length();i++){


				xml=xml+XML.toString(json.get(i),"Project");
			}
			xml+="</ListOfProjects>";
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



	/**
	 * The Class ButtonRenderer used to render the results buttons inside the table.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {


		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 7249023283151869493L;


		/**
		 * Instantiates a new button renderer.
		 */
		public ButtonRenderer() {
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 
			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\results.jpg"));
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
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground( new Color(255, 255, 255));//Background);
				setBackground(UIManager.getColor("Button.background"));
			}
			if(table.getValueAt(row, column).equals("Link to results")){
				setEnabled(true);
			}else{
				setEnabled(false);
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
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
	 * Gets the project id from the selected row.
	 *
	 * @return the project id
	 * @throws FileNotFoundException the file not found exception
	 */
	public int getProjectId() throws FileNotFoundException{
		int row=-1;
		if(table.getValueAt(table.getSelectedRow(), 7).toString().equals("Yes")){
		String name=table.getValueAt(table.getSelectedRow(), 0).toString();


		for(int i=0;i<size;i++){
			if(projectName[i].equals(name)){
				row=i;	
				break;
			}		
		}
		}

		
		return projectId[row];
	}

	/**
	 * Gets the version from the selected row.
	 *
	 * @return the version
	 */
	public String getVersion() {

		return TVersion[table.getSelectedRow()];
	}

	/**
	 * Gets the configuration value from config.xml.
	 *
	 * @param key the key to obtain
	 * @return the configuration value
	 */
	private String getConfigValue(String key) {
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
		Document doc = null;
		try {
			doc = dBuilder.parse(test);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList projects = doc.getElementsByTagName("Configuration");
		Node node = projects.item(0);
		Element element = (Element) node;
		value=getValue(key, element);
	
		return value;

	}
}

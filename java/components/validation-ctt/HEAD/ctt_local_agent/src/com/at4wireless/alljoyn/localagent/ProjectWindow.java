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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;

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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
 * The Class ProjectWindow.
 */
public class ProjectWindow extends JPanel
{	
	/** Serializable version number */
	private static final long serialVersionUID = -2003933446843668539L;

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
	
	/** Class logger*/
	Logger logger;

	/**
	 * Instantiates a new project window.
	 *
	 * @param 	mainWindow 						main window to display the data
	 * @param 	user 							authenticated user
	 * @param 	token 							authentication token
	 * @throws 	SAXException 					the SAX exception
	 * @throws 	IOException 					Signals that an I/O exception has occurred.
	 * @throws 	ParserConfigurationException 	the parser configuration exception
	 */
	public ProjectWindow(final MainWindow mainWindow,final String user,final String token, Level logLevel)
			throws SAXException, IOException, ParserConfigurationException
	{ 
		this.logger = Logger.getLogger(this.getClass().getName());
		this.logger.setLevel(logLevel);
		this.mainWindow=mainWindow;
		this.user=user;
		this.token=token;
		
		setBorder(null);
		setForeground(Color.LIGHT_GRAY);

		File file = new File("Results.xml");
		file.delete();

		Object col[] = {"Project Name","Type","Created","Modified",
				"Certification Release","Associated DUT","Associated GU",
				"Configured","Results"};

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5};
		gridBagLayout.rowWeights = new double[]{0.5, 0.9};
		setLayout(gridBagLayout);

		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;
		add(mainWindow.getHeaderPanel(),gbc_header);
		
		Document doc=getXML(user, token);
		size = doc.getElementsByTagName("Project").getLength();
		projectId=new int[size];
		projectName=new String[size];
		TVersion=new String[size];

		TableModel model = new DefaultTableModel(col,size)
		{
			/** Serializable version number */
			private static final long serialVersionUID = -5114222498322422255L;

			public boolean isCellEditable(int row, int column) {
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

		drawProjects(table,doc);

		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();

				if ((column == 8) && table.getValueAt(row, column).equals("Link to results")) {
					mainWindow.getResultsWindows(projectId[table.getSelectedRow()]);
				}
			}
		});
	}

	/**
	 * Gets the project list from the document and fill the table with its values.
	 *
	 * @param 	table 	the table
	 * @param 	doc 	the document that contains the projects values
	 * 
	 */
	private void drawProjects(JTable table,Document doc)
	{
		NodeList projects = doc.getElementsByTagName("Project");

		for (int i = 0; i < projects.getLength(); i++) {
			Node node = projects.item(i);
			Element element = (Element) node;

			String id=getValue("idProject", element);
			projectId[i]=Integer.parseInt(id);

			String name=getValue("name", element);
			table.setValueAt(name,i,0);
			projectName[i]=name;

			String type=getValue("type", element);
			table.setValueAt(type,i,1);
			
			String created=getValue("createdDate", element);
			String date=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(Long.parseLong(created)));
			table.setValueAt(date,i,2);

			String modified=getValue("modifiedDate", element);
			date= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(Long.parseLong(modified)));
			table.setValueAt(date,i,3);
			
			String TechnologyVersion=getValue("certRel", element);
			TVersion[i]=TechnologyVersion;
			table.setValueAt(TechnologyVersion,i,4);
			
			String AssociatedDUT=getValue("dut", element);
			if (AssociatedDUT.equals("null")) {
				table.setValueAt("Not selected",i,5);
			} else {
				table.setValueAt(AssociatedDUT,i,5);
			}
			
			String golden=getValue("golden", element);
			if (golden.equals("null")) {
				table.setValueAt("N/A",i,6);
			} else {
				table.setValueAt(golden,i,6);
			}

			String configured=getValue("isConfigured", element);
			if (configured.equals("true")) {
				table.setValueAt("Yes",i,7);
			} else {
				table.setValueAt("No",i,7);
			}

			String hasResult=getValue("hasResults", element);
			if (hasResult.equals("true")) {
				table.setValueAt("Link to results",i,8);
			} else {
				table.setValueAt("No",i,8);
			}
		}
	}
	
	/**
	 * Gets JSON projects data from the Web Server and parses it to XML.
	 * 
	 * @param 	user 		authenticated user
	 * @param 	token 		authentication token
	 * @return 				document with projects data
	 */
	private Document getXML(String user, String token)
	{
		Document doc = null;
		String projects = "";

		try {
			//URI URL = new URI(url+user);
			URI URL = new URI(getConfigValue("TestToolWebAppUrl")+getConfigValue("GetListOfProjectsUrl")+user);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer "+token);
			entity = httpClient.execute(postRequest).getEntity();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(entity.getContent())));
			
			String input;
			while ((input = br.readLine()) != null) {
				projects+=input;
			}
		} catch (IOException e1) {
			logger.error("Communication with Web Server failed");
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server. The application will close");
			System.exit(0);
		} catch (URISyntaxException e) {
			logger.error("Malformed URL");
		}
		
		try {
			JSONArray json=new JSONArray(projects);

			String xml="<ListOfProjects>";

			for(int i=0;i<json.length();i++){
				xml+=XML.toString(json.get(i),"Project");
			}
			xml+="</ListOfProjects>";
			
			logger.debug("XML ListOfProjects from webserver: "+xml);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			logger.error("Not posible to parse the projects xml from webserver");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not posible to parse the projects xml from webserver");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			logger.error("DocumentBuilder configuration problem");
		}

		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Used to render the results buttons inside the table.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {

		/** Serializable version number */
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
				logger.error("Resource not found");
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
	 * Gets the project id from the selected row.
	 *
	 * @return 	project ID	
	 */
	public int getProjectId()
	{
		int row=-1;
		String name = null;
		
		if(table.getValueAt(table.getSelectedRow(), 7).toString().equals("Yes")){
			name=table.getValueAt(table.getSelectedRow(), 0).toString();
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
	 * @return	version
	 */
	public String getVersion()
	{
		return TVersion[table.getSelectedRow()];
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
}

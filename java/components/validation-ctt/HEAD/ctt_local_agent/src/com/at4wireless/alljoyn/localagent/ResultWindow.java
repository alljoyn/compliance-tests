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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * The Class ResultWindow.
 */
public class ResultWindow extends JDialog {


	/** Serializable version number*/
	private static final long serialVersionUID = -6343745562165783534L;

	/** The log window used to display logs. */
	LogWindow logWindow;

	/** The table that shows results. */
	private JTable table = new JTable();
	
	/** Used logger*/
	Logger logger;

	/** The main window. */
	MainWindow mainWindow;
	
	SecretKey cipherKey; 

	/**
	 * Instantiates a new result window.
	 *
	 * @param 	mainwindow 	main window to associate with "back" click
	 * @param 	projectId 	project ID
	 * @param 	user 		authenticated user
	 * @param 	token 		authentication token
	 * @param	logLevel	
	 */
	public ResultWindow(MainWindow mainwindow, final int projectId, final String user, final String token, 
			Level logLevel, SecretKey cipherKey) {

		logger = Logger.getLogger(this.getClass().getName());
		logger.setLevel(logLevel);
		this.cipherKey = cipherKey;

		this.mainWindow=mainwindow;
		int width=900;
		int height=400;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(dim.width/2-width/2,
				dim.height/2-height/2,
				width,
				height);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);

		Object col[] = {"Name","Description","Date and Time execution",
				"Certification Release","Final Verdict","Full Log"};

		Document doc=getXML(user, token, projectId);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				mainWindow.getProjectWindow();
				dispose();
			}
		});
		
		int size = doc.getElementsByTagName("TestCase").getLength();
	
		TableModel model = new DefaultTableModel(col,size){			
			private static final long serialVersionUID = -2458127522509467589L;

			public boolean isCellEditable(int row, int column)
			{
				return false;//This causes all cells to be not editable
			}
		};

		table = new JTable(model){
			private static final long serialVersionUID = -8369977836878349660L;
			
			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column)
			{
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setForeground(Color.black);
				Font font=new Font("Arial", Font.BOLD, 12);
				comp.setFont(font);
				if(column==4) {
					if("INCONC".equals(table.getValueAt(row, column))) {
						comp.setForeground(new Color(168,161,0));
					} else if("PASS".equals(table.getValueAt(row, column))) {
						comp.setForeground(new Color(103,154,0));
					} else if("FAIL".equals(table.getValueAt(row, column))) {
						comp.setForeground(new Color(217,61,26));
					}
				} else {
					comp = super.prepareRenderer(renderer, row, column);
				}
				return comp;
			}
		};
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(5, false);
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
		GridBagLayout gridBagLayout = new GridBagLayout();

		gridBagLayout.columnWeights = new double[]{0.9};
		gridBagLayout.rowWeights = new double[]{0.4, 0.8, 0.2};
		setLayout(gridBagLayout);

		drawResults(table,doc);

		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.anchor = GridBagConstraints.SOUTHWEST;
		gbc_header.insets = new Insets(0, 0, 0, 0);
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;

		add(mainWindow.getHeaderPanel(),gbc_header);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();

		gbc_scrollPane.insets = new Insets(0, 0, 0, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.fill=GridBagConstraints.BOTH;
		add(scrollPane,gbc_scrollPane);

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				if((column==5)){
					getLogWindow(projectId,user,token, table.getValueAt(row, 5).toString());
				}
			}
		});

		ImagePanel buttonPanel=new ImagePanel("res\\drawable\\short_footer.jpg");

		GridBagLayout gridBagLayoutFooter = new GridBagLayout();

		gridBagLayoutFooter.columnWeights = new double[]{1.0};
		gridBagLayoutFooter.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout);

		buttonPanel.setLayout(gridBagLayoutFooter);
		JButton back=new JButton("");

		Image img2 = null;
		try {
			img2 = ImageIO.read(new File("res\\drawable\\back.jpg"));
		} catch (IOException e2) {
			logger.error("Not posible to find image resources");
			e2.printStackTrace();
		}
		back.setIcon(new ImageIcon(img2));

		table.getColumn("Full Log").setCellRenderer(new ButtonRenderer());
		table.getColumn("Full Log").setMaxWidth(85);
		table.getColumn("Full Log").setMinWidth(85);
		
		Dimension preferredSize=new Dimension(83,23);
		back.setPreferredSize(preferredSize);

		back.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				mainWindow.getProjectWindow();
			}
		});

		buttonPanel.add(back);
		
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.anchor = GridBagConstraints.SOUTHWEST;
		gbc_buttonPane.insets = new Insets(0, 0, 0, 0);
		gbc_buttonPane.gridx = 0;
		gbc_buttonPane.gridy = 2;
		gbc_buttonPane.fill=GridBagConstraints.BOTH;
		gbc_buttonPane.anchor=GridBagConstraints.CENTER;
		getContentPane().add(buttonPanel, gbc_buttonPane);

		setVisible(true);
	}

	/**
	 * The Class ButtonRenderer.
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {

		/** Serializable version number */
		private static final long serialVersionUID = -5722198665540400718L;

		/**
		 * Instantiates a new button renderer.
		 */
		public ButtonRenderer() {
			Image img = null;
			
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 
			
			try {
				img = ImageIO.read(new File("res\\drawable\\log.jpg"));
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
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * Gets the project results from the document and fill the table with its values.
	 *
	 * @param 	table 	table to fill with results 
	 * @param 	doc 	document that contains results
	 *
	 */
	private void drawResults(JTable table,Document doc)
	{
		NodeList results = doc.getElementsByTagName("TestCase");
		for (int i = 0; i < results.getLength(); i++) {
			Node node = results.item(i);
			Element element = (Element) node;
			String name=getValue("Name", element);
			table.setValueAt(name,i,0);

			String desc=getValue("Description", element);
			table.setValueAt(desc,i,1);
			String date=getValue("DateTime", element);
			table.setValueAt(date,i,2);
			String version=getValue("Version", element);
			table.setValueAt(version,i,3);

			String verd=getValue("Verdict", element);
			table.setValueAt(verd,i,4);

			table.setValueAt(getValue("LogFile", element),i,5);
		}
	}

	/**
	 * Gets JSON results data from the Web Server and parses it to XML.
	 * 
	 * @param 	user 		authenticated user
	 * @param 	token 		authentication token
	 * @param 	projectId 	project ID
	 * @return 				document with results data
	 */
	private Document getXML(String user, String token, int projectId)
	{
		Document doc = null;
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetResults");
		String results = "";

		try {
			URI URL = new URI(url+user+"/"+projectId);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer "+token);
			entity = httpClient.execute(postRequest).getEntity();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(entity.getContent())));
			
			String input;
			while ((input = br.readLine()) != null) {
				results+=input;
			}
		} catch (IOException e1) {
			logger.error("Communication with Web Server failed");
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server. The application will close");
			System.exit(0);
		} catch (URISyntaxException e) {
			logger.error("Malformed URL");
		}

		try {
			JSONObject js=new JSONObject(results);
			String xml="<Results>";
			JSONObject jsonResult=(JSONObject) js.get("Results");
			try{
				JSONArray json=(JSONArray) jsonResult.get("TestCase");

				for(int i=0;i<json.length();i++){
					xml+=XML.toString(json.get(i),"TestCase");
				}
			}catch(ClassCastException e){
				JSONObject json=(JSONObject) jsonResult.get("TestCase");
				xml=xml+XML.toString(json,"TestCase");
			}
			xml+="</Results>";
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			logger.error("Not posible to parse the results xml from webserver");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Not posible to parse the results xml from webserver");
			e.printStackTrace();
		} catch (JSONException e) {
			dispose();
		} catch (ParserConfigurationException e) {
			logger.error("DB Configuration problem");
		}
		
		doc.getDocumentElement().normalize();
		return doc;
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

	/**
	 * Gets the log window.
	 *
	 * @param 	projectId 	project ID
	 * @param 	user 		authenticated user
	 * @param 	token 		authentication token
	 * @param 	logName 	name of the low to be displayed
	 * @return 				the log window
	 */
	private void getLogWindow(int projectId, String user, String token, String logName)
	{
		logWindow = new LogWindow(this,projectId,user,token,logName,logger.getLevel(),cipherKey);
		logWindow.setModalityType(ModalityType.APPLICATION_MODAL);
		logWindow.setAlwaysOnTop(true);
		setEnabled(false);
	}
}

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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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


	/**
	 * 
	 */
	private static final long serialVersionUID = -6343745562165783534L;

	/** The log window used to log stored logs. */
	LogWindow logWindow;

	/** The table that shows results. */
	private JTable table = new JTable();



	/** The main window. */
	MainWindow mainWindow;

	/**
	 * Instantiates a new result window.
	 *
	 * @param mainwindow the main window.
	 * @param projectId the project id
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 */
	public ResultWindow(MainWindow mainwindow,final int projectId, final String user, final String token) {

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
				"Certification Release"
				,"Final Verdict","Full Log"};


		Document doc=getXML(user, token, projectId);


		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				mainWindow.getProjectWindow();
				dispose();
			}

		});


		int size=getTableSize(doc);
	
		TableModel model = new DefaultTableModel(col,size){			
			private static final long serialVersionUID = -2458127522509467589L;

			public boolean isCellEditable(int row, int column)
			{
				return false;//This causes all cells to be not editable
			}
		};

		table=new JTable(model){
			private static final long serialVersionUID = -8369977836878349660L;
			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column)
			{
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setForeground(Color.black);
				Font font=new Font("Arial", Font.BOLD, 12);
				comp.setFont(font);
				if(column==4){
					if("INCONC".equals(table.getValueAt(row, column))){
						comp.setForeground(new Color(168,161,0));

					}else if("PASS".equals(table.getValueAt(row, column))){
						comp.setForeground(new Color(103,154,0));
					}else if("FAIL".equals(table.getValueAt(row, column))){
						comp.setForeground(new Color(217,61,26));
					}
				}else{
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

		getProject(table,doc);


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
					table.getValueAt(target.getSelectedRow(), 5);
					//getLogWindow(projectId,user,token, logNames[row]);//getLog/user/projectid/name.log
					getLogWindow(projectId,user,token, table.getValueAt(target.getSelectedRow(), 5).toString());
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
			// TODO Auto-generated catch block
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



		/**
		 * 
		 */
		private static final long serialVersionUID = -5722198665540400718L;

		/**
		 * Instantiates a new button renderer.
		 */
		public ButtonRenderer() {
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 
			Image img = null;
			try {

				img = ImageIO.read(new File("res\\drawable\\log.jpg"));
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
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}


	/**
	 * Gets the project results from the document and fill the table with it´s values.
	 *
	 * @param table the table 
	 * @param doc the document that contains the projects values
	 *
	 */
	private void getProject(JTable table,Document doc) {
		NodeList projects = doc.getElementsByTagName("TestCase");
		for (int i = 0; i < projects.getLength(); i++) {
			Node node = projects.item(i);
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
	 * Gets the table size.
	 *
	 * @param doc the doc
	 * @return the table size
	 */
	private int getTableSize(Document doc) {
		int size=0;

		NodeList projects = doc.getElementsByTagName("TestCase");

		int projectLenght= projects.getLength();
		size=projectLenght;
		return size;
	}




	/**
	 * Gets the jsons string from the web server and convert it to a xml document.
	 * @param user the user name used to authenticate.
	 * @param token the authentication token obtained when the application logs in
	 * @param projectId the project id
	 * @return the document that contains the projects values
	 */
	private  Document getXML(String user, String token, int projectId)  {


		Document doc = null;



		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetResults");

		String test = "";

		try {

			URI URL = new URI(url+user+"/"+projectId);


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
			e.printStackTrace();
		}
		try {

			JSONObject js=new JSONObject(test);
			String xml="<Results>";
			JSONObject jsonResult=(JSONObject) js.get("Results");
			try{
				JSONArray json=(JSONArray) jsonResult.get("TestCase");

				for(int i=0;i<json.length();i++){
					xml=xml+XML.toString(json.get(i),"TestCase");
				}
			}catch(ClassCastException e){
				JSONObject json=(JSONObject) jsonResult.get("TestCase");
				xml=xml+XML.toString(json,"TestCase");
			}
			xml+="</Results>";
			System.out.println(xml);
			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			dispose();


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
	 * Gets the configuration value from config.xml.
	 *
	 * @param key the key to obtain
	 * @return the configuration value
	 */
	private static String getConfigValue(String key) {
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


	/**
	 * Gets the log window.
	 *
	 * @param projectId the project id
	 * @param user the user
	 * @param token the token
	 * @param logName the log name
	 * @return the log window
	 */
	private void getLogWindow(int projectId, String user, String token, String logName) {


		logWindow=new LogWindow(this,projectId,user,token,logName);
		logWindow.setModalityType(JDialog.DEFAULT_MODALITY_TYPE.APPLICATION_MODAL);

		logWindow.setAlwaysOnTop(true);
		setEnabled(false);
	}
}

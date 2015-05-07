package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
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

public class ResultWindow extends JDialog {

	/**
	 * 
	 */
	LogWindow logWindow;
	
	private JTable table = new JTable();
	String[] logNames;
	MainWindow mainWindow;


	/**
	 * Create the dialog.
	 * @param token 
	 * @param user 
	 * @param projectId 
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
		logNames=new String[size];
		TableModel model = new DefaultTableModel(col,size){

			/**
			 * 
			 */
			private static final long serialVersionUID = -2458127522509467589L;

			public boolean isCellEditable(int row, int column)
			{
				return false;//This causes all cells to be not editable
			}


		};

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
				System.out.println("Row: "+row+"Colunm: "+column);
				if((column==5)){
					getLogWindow(projectId,user,token, logNames[row]);//getLog/user/projectid/name.log

				}
			}




		});

		ImagePanel buttonPanel=new ImagePanel("res\\drawable\\short_footer.jpg");

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton back=new JButton("");
		
		back.setBorderPainted(false); 
		back.setContentAreaFilled(false); 
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

		back.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

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
		gbc_buttonPane.anchor=GridBagConstraints.PAGE_END;
		getContentPane().add(buttonPanel, gbc_buttonPane);




		setVisible(true);
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
				
				img = ImageIO.read(new File("res\\drawable\\log.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			setIcon(new ImageIcon(img));
			
		}

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


			logNames[i]=getValue("LogFile", element);
			table.setValueAt("Link to log file",i,5);

		}
	}




	private int getTableSize(Document doc) {
		int size=0;

		NodeList projects = doc.getElementsByTagName("TestCase");

		int projectLenght= projects.getLength();
		size=projectLenght;
		return size;
	}




	private  Document getXML(String user, String token, int projectId)  {


		Document doc = null;



		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetResults");

		String test = "";
		//IOException Web is not found
		//try {



		try {

			URI URL = new URI(url+user+"/"+projectId);
			System.out.println(url+user);


			HttpClient httpClient = HttpClientBuilder.create().build();




			HttpGet postRequest = new HttpGet(URL);

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
			e.printStackTrace();
		}
		try {
			System.out.println(test);

			JSONObject js=new JSONObject(test);
			String xml="<Results>";
			JSONObject jsonResult=(JSONObject) js.get("Results");
			try{
				JSONArray json=(JSONArray) jsonResult.get("TestCase");
				//System.out.println(json);
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

			//mainWindow.getProjectWindow();
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
		System.out.println(value);
		return value;
	}


	private void getLogWindow(int projectId, String user, String token, String logName) {


		logWindow=new LogWindow(this,projectId,user,token,logName);
		logWindow.setModalityType(JDialog.DEFAULT_MODALITY_TYPE.APPLICATION_MODAL);

		logWindow.setAlwaysOnTop(true);
		setEnabled(false);
	}
}

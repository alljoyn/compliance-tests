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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
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

public class ProjectWindow extends JPanel
{	
	private static final long serialVersionUID = -2003933446843668539L;
	private static final String TAG = "ProjectWindow";
	private static final Logger logger = Logger.getLogger(TAG);
	private static final String RESOURCES_PATH = "res" + File.separator + "drawable";
	private static final String RESULTS_BUTTON = RESOURCES_PATH + File.separator + "results.jpg";
	private static final String projectsTableColumns[] = {"Project Name", "Type", "Created", "Modified",
			"Certification Release", "Associated DUT", "Associated GU", "Configured", "Results"};
	private static final int resultsColumn = 8;
	private static final String configurationFileName = "config.xml";
	
	private JTable table;
	
	private List<Integer> projectIdList = new ArrayList<Integer>();
	private List<String> projectNameList = new ArrayList<String>();
	private List<String> technologyVersionList = new ArrayList<String>();

	/**
	 * Instantiates a new project window.
	 *
	 * @param mainWindow
	 * 			main window to display the data
	 * @param user
	 * 			authenticated user
	 * @param token
	 * 			authentication token
	 * 
	 * @throws SAXException
	 * 			the SAX exception
	 * @throws IOException
	 * 			Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 * 			the parser configuration exception
	 */
	public ProjectWindow(final MainWindow mainWindow,final String user,final String token, Level logLevel)
			throws SAXException, IOException, ParserConfigurationException
	{ 
		ProjectWindow.logger.setLevel(logLevel);
		
		Document doc = getProjectsInfoFromServer(user, token);
		
		setBorder(null);
		setForeground(Color.LIGHT_GRAY);

		File file = new File("Results.xml");
		file.delete();

		TableModel noEditableCellsModel = new DefaultTableModel(projectsTableColumns, doc.getElementsByTagName("Project").getLength())
		{
			private static final long serialVersionUID = -5114222498322422255L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		table = new JTable(noEditableCellsModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(resultsColumn, false);
		sorter.addRowSorterListener(new RowSorterListener()
		{
			@Override
			public void sorterChanged(RowSorterEvent e)
			{
				String sortedProject;
				List<Integer> tempProjectIdList = new ArrayList<Integer>();
				List<String> tempProjectNameList = new ArrayList<String>();
				List<String> tempTechnologyVersionList = new ArrayList<String>();
				int oldPosition;
				
				for (int i = 0; i < table.getRowCount(); i++)
				{
					sortedProject = (String) table.getValueAt(i, 0);
					oldPosition = -1;
					
					for (int j = 0; j < table.getRowCount(); j++)
					{
						if (sortedProject.equals(projectNameList.get(j)))
						{
							oldPosition = j;
						}
					}
					
					if (oldPosition != -1)
					{
						tempProjectIdList.add(projectIdList.get(oldPosition));
						tempProjectNameList.add(projectNameList.get(oldPosition));
						tempTechnologyVersionList.add(technologyVersionList.get(oldPosition));
					}
				}

				projectIdList = tempProjectIdList;
				projectNameList = tempProjectNameList;
				technologyVersionList = tempTechnologyVersionList;
			}	
		});
		
		table.setRowSorter(sorter);
		table.setBorder(null);
		
		setPanelLayout();
		setPanelHeader(mainWindow);
		setPanelTable(mainWindow, doc);
	}
	
	private void setPanelLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5};
		gridBagLayout.rowWeights = new double[]{0.5, 0.9};
		setLayout(gridBagLayout);
	}
	
	public void setPanelHeader(MainWindow mainWindow)
	{
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill = GridBagConstraints.BOTH;
		add(mainWindow.getHeaderPanel(), gbc_header);
	}
	
	public void setPanelTable(final MainWindow mainWindow, Document documentWithProjectsInfo)
	{
		fillProjectsTable(table, documentWithProjectsInfo);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setViewportBorder(null);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.anchor = GridBagConstraints.NORTH;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		add(scrollPane, gbc_scrollPane);

		table.getColumn("Results").setCellRenderer(new ButtonRenderer());
		table.getColumn("Results").setMaxWidth(80);
		table.getColumn("Results").setMinWidth(80);
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();

				if ((column == 8) && table.getValueAt(row, column).equals("Link to results"))
				{
					mainWindow.getResultsWindows(projectIdList.get(table.getSelectedRow()));
				}
			}
		});
	}
	
	class ButtonRenderer extends JButton implements TableCellRenderer
	{
		private static final long serialVersionUID = 7249023283151869493L;

		public ButtonRenderer()
		{
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 
			setIcon(new ImageIcon(readImageFromFile(RESULTS_BUTTON)));
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (isSelected)
			{
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}
			else
			{
				setForeground( new Color(255, 255, 255));
				setBackground(UIManager.getColor("Button.background"));
			}
			
			if (table.getValueAt(row, column).equals("Link to results"))
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
			
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}
	
	private Image readImageFromFile(String imagePathAndName)
	{
		Image image = null;
		
		try
		{
			image = ImageIO.read(new File(imagePathAndName));
		}
		catch (IOException e)
		{
			logger.error("Resource '" + imagePathAndName + "' not found");
		}
		
		return image;
	}

	/**
	 * Gets the project list from the document and fill the table with its values.
	 *
	 * @param projectsTable
	 * 			table to be filled with projects details
	 * @param xmlDocument
	 * 			document that contains all the necessary data to fill the table 
	 */
	private void fillProjectsTable(JTable projectsTable, Document xmlDocument)
	{
		NodeList projects = xmlDocument.getElementsByTagName("Project");

		for (int i = 0; i < projects.getLength(); i++)
		{
			Node node = projects.item(i);
			Element element = (Element) node;

			String id = getValue("idProject", element);
			projectIdList.add(Integer.parseInt(id));
			
			projectNameList.add(fillColumn("name", i, 0, element, projectsTable));
			fillColumn("type", i, 1, element, projectsTable);
			fillColumn("createdDate", i, 2, element, projectsTable);
			fillColumn("modifiedDate", i, 3, element, projectsTable);
			technologyVersionList.add(fillColumn("certRel", i, 4, element, projectsTable));
			fillColumn("dut", i, 5, element, projectsTable);
			fillColumn("golden", i, 6, element, projectsTable);
			fillColumn("isConfigured", i, 7, element, projectsTable);
			fillColumn("hasResults", i, 8, element, projectsTable);
		}
	}
	
	private String fillColumn(String xmlDataName, int row, int column, Element xmlElement, JTable tableToFill)
	{
		String dataValue = getValue(xmlDataName, xmlElement);
		
		if (xmlDataName.equals("createdDate") || xmlDataName.equals("modifiedDate"))
		{
			tableToFill.setValueAt(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(Long.parseLong(dataValue))), row, column);
		}
		else if (xmlDataName.equals("dut"))
		{
			if (dataValue.equals("null"))
			{
				tableToFill.setValueAt("Not selected", row, column);
			}
			else
			{
				tableToFill.setValueAt(dataValue, row, column);
			}
		}
		else if (xmlDataName.equals("golden"))
		{
			if (dataValue.equals("null"))
			{
				tableToFill.setValueAt("N/A", row, column);
			}
			else
			{
				tableToFill.setValueAt(dataValue, row, column);
			}
		}
		else if (xmlDataName.equals("isConfigured"))
		{
			if (dataValue.equals("true"))
			{
				tableToFill.setValueAt("Yes", row, column);
			}
			else
			{
				tableToFill.setValueAt("No", row, column);
			}
		}
		else if (xmlDataName.equals("hasResults"))
		{
			if (dataValue.equals("true"))
			{
				tableToFill.setValueAt("Link to results", row, column);
			}
			else
			{
				tableToFill.setValueAt("No", row, column);
			}
		}
		else
		{
			tableToFill.setValueAt(dataValue, row, column);
		}
		
		return dataValue;
	}
	
	/**
	 * Gets JSON projects data from the Web Server and parses it to XML.
	 * 
	 * @param user
	 * 			user authenticated into the application
	 * @param token
	 * 			session OAuth 2.0 token that allows message exchange with the server
	 * 
	 * @return XML parsed document from server received data
	 */
	private Document getProjectsInfoFromServer(String user, String token)
	{
		Document doc = null;
		String projects = "";

		try
		{
			URI URL = new URI(getConfigValue("TestToolWebAppUrl") + getConfigValue("GetListOfProjectsUrl") + user);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer " + token);
			entity = httpClient.execute(postRequest).getEntity();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
			
			String input;
			while ((input = br.readLine()) != null)
			{
				projects += input;
			}
		}
		catch (IOException e1)
		{
			logger.error("Communication with Web Server failed");
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server. The application will close");
			System.exit(0);
		}
		catch (URISyntaxException e)
		{
			logger.error("Malformed URL");
		}
		
		try
		{
			JSONArray json = new JSONArray(projects);
			String xml = "<ListOfProjects>";

			for(int i = 0; i < json.length(); i++)
			{
				xml += XML.toString(json.get(i),"Project");
			}
			
			xml += "</ListOfProjects>";
			
			logger.debug("XML ListOfProjects from webserver: " + xml);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		}
		catch (SAXException e)
		{
			logger.error("Not posible to parse the projects xml from webserver");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			logger.error("Not posible to parse the projects xml from webserver");
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			logger.error("DocumentBuilder configuration problem");
		}

		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Gets the project id from the selected row.
	 *
	 * @return project ID	
	 */
	public int getProjectId()
	{
		int row = -1;
		String name = null;
		
		if (table.getValueAt(table.getSelectedRow(), 7).toString().equals("Yes"))
		{
			name = table.getValueAt(table.getSelectedRow(), 0).toString();
			
			for (int i = 0; i < table.getRowCount(); i++)
			{
				if (projectNameList.get(i).equals(name))
				{
					row = i;	
					break;
				}		
			}
		}		

		return projectIdList.get(row);
	}

	/**
	 * Gets the version from the selected row.
	 *
	 * @return	version
	 */
	public String getVersion()
	{
		return technologyVersionList.get(table.getSelectedRow());
	}

	/**
	 * Gets a configuration value from config.xml.
	 *
	 * @param dataToRetrieve
	 * 			the key whose value is going to be searched
	 * 
	 * @return value associated with the input key
	 */
	private String getConfigValue(String dataToRetrieve)
	{
		File cfFile = new File(configurationFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		Element element = null;
		
		logger.debug("Retrieving: " + dataToRetrieve);
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(cfFile);
			element = (Element) doc.getElementsByTagName("Configuration").item(0);
		}
		catch (ParserConfigurationException e)
		{
			logger.error("DB configuration problem");
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			logger.error("Configuration file not found");
		}
		
		return getValue(dataToRetrieve, element);
	}
	
	/**
	 * Gets the value of the selected tag from config.xml. 
	 *
	 * @param xmlSelectedTag
	 * 			desired field to recover its value
	 * @param xmlElement
	 * 			XML element that contains the tag
	 * 
	 * @return value associated with the input tag
	 */
	private static String getValue(String xmlSelectedTag, Element xmlElement)
	{
		NodeList nodes = xmlElement.getElementsByTagName(xmlSelectedTag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}

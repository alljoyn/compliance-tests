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

public class ResultWindow extends JDialog
{
	private static final long serialVersionUID = -6343745562165783534L;
	private static final String TAG = "ResultWindow";
	private static final Logger logger = Logger.getLogger(TAG);
	private static final int RESULT_WINDOW_WIDTH = 900;
	private static final int RESULT_WINDOW_HEIGHT = 400;
	private static final String RESOURCES_PATH = "res" + File.separator + "drawable";
	private static final String SHORT_FOOTER = RESOURCES_PATH + File.separator + "short_footer.jpg";
	private static final String BACK_BUTTON = RESOURCES_PATH + File.separator + "back.jpg";
	private static final String LOG_BUTTON = RESOURCES_PATH + File.separator + "log.jpg";
	private static final Object resultsTableColumns[] = {"Name", "Description", "Date and Time execution", "Certification Release",
			"Final Verdict", "Full Log"};
	private static final String TEXT_FONT = "Arial";
	private static final int TEXT_FONT_SIZE = 12;
	private static final int TEXT_FONT_STYLE = Font.BOLD;
	private static final int verdictColumn = 4;
	private static final int linkToLogColumn = 5;
	private static final String passVerdict = "PASS";
	private static final String failVerdict = "FAIL";
	private static final String inconcluseVerdict = "INCONC";
	private static final String configurationFileName = "config.xml";
	
	private JTable table;

	/**
	 * Instantiates a new results window.
	 *
	 * @param mainwindow 	
	 * 				window to be associated with BACK button click
	 * @param projectId
	 * 				ID of the project whose results are being shown
	 * @param user
	 * 				user authenticated into the application
	 * @param token
	 * 				session OAuth 2.0 token that allows message exchange with the server
	 * @param logLevel
	 * 				level of log set by config.xml file
	 * @param cipherKey
	 * 				unique key needed to decode log data	
	 */
	public ResultWindow(final MainWindow mainWindow, final int projectId, final String user, final String token, 
			Level logLevel, SecretKey cipherKey)
	{
		ResultWindow.logger.setLevel(logLevel);
		
		Document doc = getResultsInfoFromServer(user, token, projectId);

		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenDimensions.width - RESULT_WINDOW_WIDTH)/2, (screenDimensions.height - RESULT_WINDOW_HEIGHT)/2,
				RESULT_WINDOW_WIDTH, RESULT_WINDOW_HEIGHT);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				mainWindow.getProjectWindow();
				dispose();
			}
		});
		
		TableModel noEditableCellsModel = new DefaultTableModel(resultsTableColumns, doc.getElementsByTagName("TestCase").getLength())
		{			
			private static final long serialVersionUID = -2458127522509467589L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		table = new JTable(noEditableCellsModel)
		{
			private static final long serialVersionUID = -8369977836878349660L;
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setForeground(Color.black);
				Font font = new Font(TEXT_FONT, TEXT_FONT_STYLE, TEXT_FONT_SIZE);
				comp.setFont(font);
				
				if (column == verdictColumn)
				{
					if (inconcluseVerdict.equals(table.getValueAt(row, column)))
					{
						comp.setForeground(new Color(168, 161, 0));
					}
					else if (passVerdict.equals(table.getValueAt(row, column)))
					{
						comp.setForeground(new Color(103, 154, 0));
					}
					else if (failVerdict.equals(table.getValueAt(row, column)))
					{
						comp.setForeground(new Color(217, 61, 26));
					}
				}
				else
				{
					comp = super.prepareRenderer(renderer, row, column);
				}
				return comp;
			}
		};
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(linkToLogColumn, false);
		table.setRowSorter(sorter);
		
		setPanelLayout();
		setPanelHeader(mainWindow);
		setPanelTable(doc, projectId, user, token, cipherKey);
		setPanelFooter(mainWindow);

		setVisible(true);
	}
	
	private void setPanelLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.9};
		gridBagLayout.rowWeights = new double[]{0.4, 0.8, 0.2};
		setLayout(gridBagLayout);
	}
	
	private void setPanelHeader(MainWindow mainWindow)
	{
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.anchor = GridBagConstraints.SOUTHWEST;
		gbc_header.insets = new Insets(0, 0, 0, 0);
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill = GridBagConstraints.BOTH;

		add(mainWindow.getHeaderPanel(), gbc_header);
	}
	
	private void setPanelFooter(MainWindow mainWindow)
	{
		ImagePanel buttonPanel = new ImagePanel(SHORT_FOOTER);

		GridBagLayout gridBagLayoutFooter = new GridBagLayout();

		gridBagLayoutFooter.columnWeights = new double[]{1.0};
		gridBagLayoutFooter.rowWeights = new double[]{1.0};

		buttonPanel.setLayout(gridBagLayoutFooter);
		JButton back = new JButton("");

		back.setIcon(new ImageIcon(readImageFromFile(BACK_BUTTON)));

		table.getColumn("Full Log").setCellRenderer(new ButtonRenderer());
		table.getColumn("Full Log").setMaxWidth(85);
		table.getColumn("Full Log").setMinWidth(85);
		
		Dimension preferredSize = new Dimension(83,23);
		back.setPreferredSize(preferredSize);

		back.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
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
		gbc_buttonPane.fill = GridBagConstraints.BOTH;
		gbc_buttonPane.anchor = GridBagConstraints.CENTER;
		getContentPane().add(buttonPanel, gbc_buttonPane);
	}
	
	private void setPanelTable(Document documentWithResultsInfo, int projectId, String authenticatedUser, String sessionToken, SecretKey cipherKey)
	{
		fillResultsTable(table, documentWithResultsInfo);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();

		gbc_scrollPane.insets = new Insets(0, 0, 0, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		add(scrollPane, gbc_scrollPane);

		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				
				if (column == linkToLogColumn)
				{
					getLogWindow(projectId, authenticatedUser, sessionToken, table.getValueAt(row, linkToLogColumn).toString(), cipherKey);
				}
			}
		});
	}
	
	/**
	 * Gets the log window.
	 *
	 * @param projectId
	 * 			ID of the project whose log is being asked
	 * @param user
	 * 			user authenticated into the application
	 * @param token
	 * 			session OAuth 2.0 token that allows message exchange with the server
	 * @param logName
	 * 			name of the log to be displayed
	 */
	private void getLogWindow(int projectId, String user, String token, String logName, SecretKey cipherKey)
	{
		LogContentWindow logWindow = new LogContentWindow(this, projectId, user, token, logName, logger.getLevel(), cipherKey);
		logWindow.setModalityType(ModalityType.APPLICATION_MODAL);
		logWindow.setAlwaysOnTop(true);
		setEnabled(false);
	}

	class ButtonRenderer extends JButton implements TableCellRenderer
	{
		private static final long serialVersionUID = -5722198665540400718L;

		public ButtonRenderer()
		{
			setOpaque(true);
			setBorderPainted(false); 
			setContentAreaFilled(false); 
			setIcon(new ImageIcon(readImageFromFile(LOG_BUTTON)));
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
	 * Gets the project results from the document and fill the table with its values.
	 *
	 * @param table
	 * 			table to fill with results 
	 * @param doc
	 * 			document that contains results
	 */
	private void fillResultsTable(JTable table, Document doc)
	{
		NodeList results = doc.getElementsByTagName("TestCase");
		
		for (int i = 0; i < results.getLength(); i++)
		{
			Node node = results.item(i);
			Element element = (Element) node;
			
			fillColumn("Name", i, 0, element, table);
			fillColumn("Description", i, 1, element, table);
			fillColumn("DateTime", i, 2, element, table);
			fillColumn("Version", i, 3, element, table);
			fillColumn("Verdict", i, verdictColumn, element, table);
			fillColumn("LogFile", i, linkToLogColumn, element, table);
		}
	}
	
	private void fillColumn(String xmlDataName, int row, int column, Element xmlElement, JTable tableToFill)
	{
		String dataValue = getValue(xmlDataName, xmlElement);
		tableToFill.setValueAt(dataValue, row, column);
	}

	/**
	 * Gets JSON results data from the Web Server and parses it to XML.
	 * 
	 * @param user
	 * 			user authenticated into the application
	 * @param token
	 * 			session OAuth 2.0 token that allows message exchange with the server
	 * @param projectId
	 * 			ID of the project whose results are being shown
	 * 
	 * @return XML parsed document from server received data
	 */
	private Document getResultsInfoFromServer(String user, String token, int projectId)
	{
		Document doc = null;
		String url = getConfigValue("TestToolWebAppUrl") + getConfigValue("GetResults");
		String results = "";

		try
		{
			URI URL = new URI(url + user + "/" + projectId);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer "+ token);
			entity = httpClient.execute(postRequest).getEntity();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
			
			String input;
			while ((input = br.readLine()) != null)
			{
				results += input;
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
			JSONObject js = new JSONObject(results);
			String xml = "<Results>";
			JSONObject jsonResult = (JSONObject) js.get("Results");
			try
			{
				JSONArray json = (JSONArray) jsonResult.get("TestCase");

				for (int i = 0; i < json.length(); i++)
				{
					xml += XML.toString(json.get(i), "TestCase");
				}
			}
			catch(ClassCastException e)
			{
				JSONObject json = (JSONObject) jsonResult.get("TestCase");
				xml = xml + XML.toString(json, "TestCase");
			}
			
			xml += "</Results>";
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		}
		catch (SAXException e)
		{
			logger.error("Not posible to parse the results xml from webserver");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			logger.error("Not posible to parse the results xml from webserver");
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			dispose();
		}
		catch (ParserConfigurationException e)
		{
			logger.error("DB Configuration problem");
		}
		
		doc.getDocumentElement().normalize();
		return doc;
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

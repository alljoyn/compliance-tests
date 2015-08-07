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

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LogContentWindow extends JDialog
{
	private static final long serialVersionUID = -480896840677856306L;
	private static final String TAG = "LogContentWindow";
	private static final Logger logger = Logger.getLogger(TAG);
	private static final int LOG_WINDOW_WIDTH = 900;
	private static final int LOG_WINDOW_HEIGHT = 450;
	private static final int LOG_CONTENT_PANE_WIDTH = 800;
	private static final int LOG_CONTENT_PANE_HEIGHT = 300;
	private static final String RESOURCES_PATH = "res"+File.separator+"drawable";
	private static final String HEADER = RESOURCES_PATH + File.separator + "header.jpg";
	private static final String SHORT_FOOTER = RESOURCES_PATH + File.separator + "short_footer.jpg";
	private static final String BACK_BUTTON = RESOURCES_PATH + File.separator + "back.jpg";
	private static final String configurationFileName = "config.xml";

	/**
	 * Instantiates a new log window.
	 *
	 * @param resultWindow
	 * 			window to be associated with BACK button click
	 * @param projectId
	 * 			ID of the project whose log is being shown
	 * @param user
	 * 			user authenticated into the application
	 * @param token
	 * 			session OAuth 2.0 token that allows message exchange with the server
	 * @param logName
	 * 			name of the log to be retrieved
	 * @param logLevel
	 * 			level of log set by config.xml file
	 * @param cipherKey
	 * 			unique key needed to decode log data
	 */
	public LogContentWindow(final ResultWindow resultWindow, int projectId, String user, String token, String logName, 
			Level logLevel, SecretKey cipherKey) 
	{
		LogContentWindow.logger.setLevel(logLevel);
		
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		
		setBounds((screenDimensions.width - LOG_WINDOW_WIDTH)/2, (screenDimensions.height - LOG_WINDOW_HEIGHT)/2,
				LOG_WINDOW_WIDTH, LOG_WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				resultWindow.setEnabled(true);
				dispose();
			}
		});
		
		setPanelLayout();
		setPanelHeader();
		setLogContent(user, token, projectId, logName, cipherKey);
		setPanelFooter(resultWindow);
		setVisible(true);
	}
	
	private void setPanelLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		
		gridBagLayout.columnWeights = new double[]{0.9};
		gridBagLayout.rowWeights = new double[]{0.4, 0.8, 0.2};
		
		setLayout(gridBagLayout);
	}
	
	private void setPanelHeader()
	{
		GridBagConstraints headerConstraints = new GridBagConstraints();
		
		headerConstraints.anchor = GridBagConstraints.SOUTHWEST;
		headerConstraints.insets = new Insets(0, 0, 0, 0);
		headerConstraints.gridx = 0;
		headerConstraints.gridy = 0;
		headerConstraints.fill = GridBagConstraints.BOTH;
		
		getContentPane().add(new ImagePanel(HEADER), headerConstraints);
	}
	
	private void setLogContent(String authenticatedUser, String sessionToken, int projectId, String logName, SecretKey cipherKey)
	{
		JTextPane logContentPane = new JTextPane();
		JScrollPane logContentScrollPane;
		GridBagConstraints logContentPaneConstraints = new GridBagConstraints();
		
		logContentPane.setEditable(false);
		logContentPane.setText(decryptLog(authenticatedUser, sessionToken, projectId, logName, cipherKey));
		
		logContentScrollPane = new JScrollPane(logContentPane);
		logContentScrollPane.setSize(LOG_CONTENT_PANE_HEIGHT, LOG_CONTENT_PANE_WIDTH);
		
		logContentPaneConstraints.insets = new Insets(0, 0, 0, 0);
		logContentPaneConstraints.gridx = 0;
		logContentPaneConstraints.gridy = 1;
		logContentPaneConstraints.fill = GridBagConstraints.BOTH;	
		
		getContentPane().add(logContentScrollPane, logContentPaneConstraints);
	}
	
	private String decryptLog(String authenticatedUser, String sessionToken, int projectId, String logName, SecretKey cipherKey)
	{
		String decryptedLog = null;
		
		try
		{
			FileEncryption fE = new FileEncryption();
			fE.setAesSecretKey(cipherKey);
			decryptedLog = fE.decrypt(getLogContentFromServer(authenticatedUser, sessionToken, projectId, logName));
		}
		catch (GeneralSecurityException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return decryptedLog;
	}
	
	private void setPanelFooter(ResultWindow resultWindow)
	{
		ImagePanel footerPane = new ImagePanel(SHORT_FOOTER);
		GridBagLayout gridBagLayoutFooter = new GridBagLayout();
		GridBagConstraints footerPaneConstraints = new GridBagConstraints();
		
		gridBagLayoutFooter.columnWeights = new double[]{1.0};
		gridBagLayoutFooter.rowWeights = new double[]{1.0};
		
		footerPane.setLayout(gridBagLayoutFooter);
		
		setBackButton(resultWindow, footerPane);

		footerPaneConstraints.anchor = GridBagConstraints.SOUTHWEST;
		footerPaneConstraints.insets = new Insets(0, 0, 0, 0);
		footerPaneConstraints.gridx = 0;
		footerPaneConstraints.gridy = 2;
		footerPaneConstraints.fill = GridBagConstraints.BOTH;
		footerPaneConstraints.anchor = GridBagConstraints.PAGE_END;
		
		getContentPane().add(footerPane, footerPaneConstraints);
	}
	
	private void setBackButton(ResultWindow resultWindow, ImagePanel footerPane)
	{
		JButton back = new JButton("");
		Dimension preferredSize = new Dimension(83,23);
		
		back.setIcon(new ImageIcon(readImageFromFile(BACK_BUTTON)));
		back.setPreferredSize(preferredSize);
		back.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				resultWindow.setEnabled(true);
				dispose();
			}
		});
		
		footerPane.add(back);
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
	 * Gets the log from the Web Server.
	 *
	 * @param user
	 * 			user authenticated into the application
	 * @param token
	 * 			session OAuth 2.0 token that allows message exchange with the server
	 * @param projectId
	 * 			ID of the project whose log is being shown
	 * @param logName
	 * 			name of the log to be retrieved
	 * 
	 * @return the decrypted content of the target log file
	 */
	private String getLogContentFromServer(String user, String token, int projectId, String logName )
	{
		String url = getConfigValue("TestToolWebAppUrl") + getConfigValue("GetLog");
		String log = "";

		try
		{
			URI URL = new URI(url + user + "/" + projectId + "/" + logName);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer " + token);
			entity = httpClient.execute(postRequest).getEntity();

			logger.debug("Entity:" + entity + "Encoding: " + entity.getContentEncoding());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			
			for (String line = null; (line = reader.readLine()) != null;)
			{
			    builder.append(line);
			}
			log = builder.toString();
		}
		catch (IOException e1)
		{
			logger.error("Communication with Web Server failed");
			e1.printStackTrace();
			Component button = new Button("rg");
			button.setVisible(false);
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server");
		}
		catch (URISyntaxException e)
		{
			logger.error("Malformed URL");
			e.printStackTrace();
		}
		return log;
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
	private static String getValue(String tag, Element element)
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}

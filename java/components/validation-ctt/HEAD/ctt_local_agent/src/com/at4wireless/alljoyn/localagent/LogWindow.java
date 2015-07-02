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


/**
 * The Class LogWindow.
 */
public class LogWindow extends JDialog
{
	/** Serializable version number */
	private static final long serialVersionUID = -480896840677856306L;

	/** Class logger */
	Logger logger;

	/**
	 * Instantiates a new log window.
	 *
	 * @param 	resultWindow 	result window to associate with "back" click
	 * @param 	projectId 		project ID
	 * @param 	user 			authenticated user
	 * @param 	token 			authentication token
	 * @param 	logname 		name of the log to be retrieved
	 */
	public LogWindow(final ResultWindow resultWindow, int projectId, String user, String token, String logName, 
			Level logLevel, SecretKey cipherKey) 
	{
		this.logger = Logger.getLogger(this.getClass().getName());
		this.logger.setLevel(logLevel);
		
		int width=900;
		int height=450;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(dim.width/2-width/2,
				dim.height/2-height/2,
				width,
				height);
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

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.9};
		gridBagLayout.rowWeights = new double[]{0.4, 0.8, 0.2};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.anchor = GridBagConstraints.SOUTHWEST;
		gbc_header.insets = new Insets(0, 0, 0, 0);
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;
		getContentPane().add(new ImagePanel("res\\drawable\\header.jpg"),gbc_header);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.fill=GridBagConstraints.BOTH;	
		JTextPane logPane=new JTextPane();
		logPane.setEditable(false);
		
		String decryptedLog = null;
		try {
			FileEncryption fE = new FileEncryption();
			fE.setAesSecretKey(cipherKey);
			decryptedLog = fE.decrypt(getLog(user, token, projectId, logName));
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//logPane.setText(getLog(user, token, projectId, logName));
		logPane.setText(decryptedLog);
		JScrollPane scrollPane = new JScrollPane(logPane);
		scrollPane.setSize(300,800);
		getContentPane().add(scrollPane,gbc_scrollPane);

		ImagePanel buttonPane=new ImagePanel("res\\drawable\\short_footer.jpg");
		GridBagLayout gridBagLayoutFooter = new GridBagLayout();
		gridBagLayoutFooter.columnWeights = new double[]{1.0};
		gridBagLayoutFooter.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		buttonPane.setLayout(gridBagLayoutFooter);
		
		JButton back=new JButton("");
		Image img2 = null;
		try {
			img2 = ImageIO.read(new File("res\\drawable\\back.jpg"));
		} catch (IOException e2) {
			logger.error("Resource not found");
		}
		back.setIcon(new ImageIcon(img2));
		Dimension preferredSize=new Dimension(83,23);
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
		buttonPane.add(back);
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.anchor = GridBagConstraints.SOUTHWEST;
		gbc_buttonPane.insets = new Insets(0, 0, 0, 0);
		gbc_buttonPane.gridx = 0;
		gbc_buttonPane.gridy = 2;
		gbc_buttonPane.fill=GridBagConstraints.BOTH;
		gbc_buttonPane.anchor=GridBagConstraints.PAGE_END;
		getContentPane().add(buttonPane,gbc_buttonPane);
		setVisible(true);
	}

	/**
	 * Gets the log from the Web Server.
	 *
	 * @param 	user 		authenticated user
	 * @param 	token 		authentication token 
	 * @param 	projectId 	project ID
	 * @param 	logName 	log name
	 * @return 				log file content
	 */
	private String getLog(String user, String token, int projectId, String logName )
	{
		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetLog");
		String log = "";

		try {
			URI URL = new URI(url+user+"/"+projectId+"/"+logName);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URL);
			HttpEntity entity = null;
			
			postRequest.addHeader("Authorization", "bearer "+token);
			entity = httpClient.execute(postRequest).getEntity();

			logger.debug("Entity:"+entity+"Encoding: "+entity.getContentEncoding());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line);
			}
			log = builder.toString();
		} catch (IOException e1) {
			logger.error("Communication with Web Server failed");
			e1.printStackTrace();
			Component button = new Button("rg");
			button.setVisible(false);
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server");
		} catch (URISyntaxException e) {
			logger.error("Malformed URL");
			e.printStackTrace();
		}
		return log;
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

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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class InstallLastVersionWindow extends JDialog
{
	private static final long serialVersionUID = 7573195888353975145L;
	private final JPanel contentPanel = new JPanel();
	String token = "";
	MainWindow mainWindow;
	private JLabel downloadingLabel;
	Logger logger;
	
	private static final int INSTALL_LAST_VERSION_PANEL_HEIGHT = 300;
	private static final int INSTALL_LAST_VERSION_PANEL_WIDTH = 650;
	private final static String RESOURCES_PATH = "res"+File.separator+"drawable";
	private final static String INSTALL_BUTTON = "install.jpg";
	private final static String BACK_BUTTON = "back.jpg";
	private final static String SHORT_FOOTER = "short_footer.jpg";
	
	public InstallLastVersionWindow(final MainWindow mainWindow, String token, 
			String version, String newestVersion, Level logLevel)
	{	
		this.logger = Logger.getLogger(this.getClass().getName());
		this.logger.setLevel(logLevel);
		
		this.mainWindow = mainWindow;		
		this.token = token;
		
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenDimensions.width/2 - INSTALL_LAST_VERSION_PANEL_WIDTH/2,
				screenDimensions.height/2 - INSTALL_LAST_VERSION_PANEL_HEIGHT/2,
				INSTALL_LAST_VERSION_PANEL_WIDTH,
				INSTALL_LAST_VERSION_PANEL_HEIGHT);
		
		setResizable(false);
		setLayout(new GridLayout(0, 1, 0, 0));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
				mainWindow.initialize();
			}
		});

		add(mainWindow.getHeaderPanel(), BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Arial", Font.PLAIN, 15));
		textPane.setEditable(false);
		
		String text = "You need to upgrade the CTT Local Agent."
				+ "\nVersion installed: " + version
				+ "\nNew version: " + newestVersion;
		
		textPane.setText(text);
		contentPanel.add(textPane);

		ImagePanel buttonPane = new ImagePanel(RESOURCES_PATH+File.separator+SHORT_FOOTER);
		//buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPane.setLayout(new GridBagLayout());
		
		JButton backButton = new JButton("");
		Image img = null;
		try
		{
			img = ImageIO.read(new File(RESOURCES_PATH+File.separator+BACK_BUTTON));
		}
		catch (IOException e2)
		{
			logger.error("Resource not found");
			e2.printStackTrace();
		}
		backButton.setIcon(new ImageIcon(img));
		
		GridBagConstraints backConstraints = new GridBagConstraints();
		//backConstraints.anchor = GridBagConstraints.NORTHEAST;
		backConstraints.gridx = 0;
		backConstraints.gridy = 0;
		backConstraints.gridheight = 1;
		backConstraints.gridwidth = 1;
		backConstraints.anchor = GridBagConstraints.NORTHWEST;
		backConstraints.fill = GridBagConstraints.NONE;
		backConstraints.insets = new Insets(0, 0, 10, 0);
		
		Dimension preferredSize = new Dimension(83,23);
		backButton.setPreferredSize(preferredSize);
		
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
				mainWindow.initialize();
				return;
			}
		});
		
		buttonPane.add(backButton, backConstraints);
		
		JButton installButton = new JButton("");
		Image img2 = null;
		try
		{
			img2 = ImageIO.read(new File(RESOURCES_PATH+File.separator+INSTALL_BUTTON));
		}
		catch (IOException e2)
		{
			logger.error("Resource not found");
			e2.printStackTrace();
		}
		
		installButton.setIcon(new ImageIcon(img2));
		
		GridBagConstraints installConstraints = new GridBagConstraints();
		installConstraints.gridx = 1;
		installConstraints.gridy = 0;
		installConstraints.gridheight = 1;
		installConstraints.gridwidth = 1;
		installConstraints.anchor = GridBagConstraints.NORTHWEST;
		installConstraints.fill = GridBagConstraints.NONE;
		//installConstraints.anchor = GridBagConstraints.NORTH;
		installConstraints.insets = new Insets(0, 10, 0, 0);
		
		installButton.setPreferredSize(preferredSize);
		
		installButton.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				downloadingLabel.setText("Downloading...");
			}

			@Override
			public void focusLost(FocusEvent arg0)
			{
				downloadingLabel.setText(" ");
			}
		});
		
		installButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				installLastVersion();
				dispose();
				mainWindow.initialize();	
			}
		});
		
		buttonPane.add(installButton, installConstraints);
		
		
		downloadingLabel = new JLabel();
		downloadingLabel.setText(" ");
		downloadingLabel.setVisible(true);
		GridBagConstraints downloadingConstraints = new GridBagConstraints();
		//downloadingConstraints.gridheight = 1;
		downloadingConstraints.gridwidth = GridBagConstraints.REMAINDER;
		downloadingConstraints.gridx = 0;
		downloadingConstraints.gridy = 1;
		downloadingConstraints.anchor = GridBagConstraints.CENTER;
		downloadingConstraints.insets = new Insets(0, 0, 0, 0);
		buttonPane.add(downloadingLabel, downloadingConstraints);
		
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}
	
	protected void installLastVersion()
	{
		String path = (new File("")).getAbsolutePath();
		
		String url = getConfigValue("TestToolWebAppUrl")+getConfigValue("getLastVersion");
		
		logger.debug(url);

		try
		{
			URI URI = new URI(url);
			FileOutputStream fos = null;
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);
			HttpEntity entity = null;
			BufferedHttpEntity buf = null;
			
			postRequest.addHeader("Authorization", "bearer "+token);
			HttpResponse response = httpClient.execute(postRequest);
			
			String filename = response.getFirstHeader("Content-disposition").getValue().split("=")[1];
			entity = response.getEntity();
			//entity = httpClient.execute(postRequest).getEntity();
			//BufferedHttpEntity buf = new BufferedHttpEntity(entity);
			buf = new BufferedHttpEntity(entity);
			
			try
			{
				fos = new FileOutputStream(path+File.separator+getConfigValue("DownloadPath")+filename);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				File dir = new File(path+File.separator+getConfigValue("DownloadPath"));
				if (dir.mkdirs())
				{	
					fos = new FileOutputStream(path+File.separator+getConfigValue("DownloadPath")+filename);
				}
			}
			buf.writeTo(fos);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server");
		}
		catch (URISyntaxException e)
		{
			logger.error("Malformed URL");
		}
		
		JOptionPane.showMessageDialog(null, "CTT Local Agent has been successfully downloaded."
				+ "\nProgram will be closed now to allow you to install the new version."
				+ "\nInstaller location: "+ path + "\\" + getConfigValue("DownloadPath").replace("/", "\\")
				+ "\nRemember to right-click on the installation package icon and press \"Run as Administrator\"");
	}
	
	private String getConfigValue(String key)
	{
		File cfFile = new File("config.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		Element element = null;
		
		logger.debug("Retrieving: "+key);
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
		
		return getValue(key, element);
	}

	private static String getValue(String tag, Element element)
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}

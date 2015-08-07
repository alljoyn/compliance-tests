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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
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

public class InstallTestToolWindow extends JDialog
{
	private static final long serialVersionUID = 7573195888353975145L;
	private static final String TAG = "InstallTestToolWindow";
	private static final Logger logger = Logger.getLogger(TAG);
	private static final int INSTALL_TEST_TOOL_PANEL_HEIGHT = 300;
	private static final int INSTALL_TEST_TOOL_PANEL_WIDTH = 650;
	private final static String RESOURCES_PATH = "res"+File.separator+"drawable";
	private final static String INSTALL_BUTTON = "install.jpg";
	private final static String BACK_BUTTON = "back.jpg";
	private final static String SHORT_FOOTER = "short_footer.jpg";
	private static final String configurationFileName = "config.xml";
	
	//private final JPanel contentPanel = new JPanel();
	//String version = null;
	//String token = "";
	//MainWindow mainWindow;
	//Logger logger;
	
	/**
	 * Creates the dialog.
	 * @param 	mainWindow	main window to associate with "back" click
	 * @param 	token		authentication token
	 * @param 	version		certification release to update
	 * @param 	projectId 	project ID
	 * @param 	logLevel 	level of displayed logs
	 */ 
	public InstallTestToolWindow(final MainWindow mainWindow, String token, final String version, 
			final int projectId, Level logLevel, boolean isDebugAndReleaseExists)
	{
		//logger = Logger.getLogger(this.getClass().getName());
		InstallTestToolWindow.logger.setLevel(logLevel);

		//this.version = version;
		//this.mainWindow = mainWindow;		
		//this.token = token;
		
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenDimensions.width/2 - INSTALL_TEST_TOOL_PANEL_WIDTH/2,
				screenDimensions.height/2 - INSTALL_TEST_TOOL_PANEL_HEIGHT/2,
				INSTALL_TEST_TOOL_PANEL_WIDTH,
				INSTALL_TEST_TOOL_PANEL_HEIGHT);
		
		setLayout(new GridLayout(0, 1, 0, 0));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
				mainWindow.getProjectWindow();
			}
		});

		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Arial", Font.PLAIN, 15));
		textPane.setEditable(false);

		//String text = "";
		
		if (isDebugAndReleaseExists)
		{
			/*text+="\nA release version of Test Cases Package "+version+" is available to download.\n";
			text+="Please click on Install to upgrade from debug to release.";*/
			textPane.setText(String.format("\nA release version of Test Cases Package %s is available to download."
					+ "\nPlease click on Install to upgrade from debug to release.", version));
		}
		else
		{
			/*text+="\nThe installation of the Test Cases Package "+version+" is needed.\n";
			text+="Please click on Install to continue";*/
			textPane.setText(String.format("\nThe installation of the Test Cases Package %s is needed."
					+ "\nPlease click on Install to continue", version));
		}
	
		//textPane.setText(text);
		JPanel contentPanel = new JPanel();
		contentPanel.add(textPane);

		ImagePanel buttonPane = new ImagePanel(RESOURCES_PATH+File.separator+SHORT_FOOTER);
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton backButton = new JButton("");
		backButton.setIcon(new ImageIcon(readImageFromFile(RESOURCES_PATH, BACK_BUTTON)));
		
		Dimension preferredSize = new Dimension(83,23);
		backButton.setPreferredSize(preferredSize);
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				mainWindow.getProjectWindow();
			}
		});
		
		JButton installButton = new JButton("");
		installButton.setIcon(new ImageIcon(readImageFromFile(RESOURCES_PATH, INSTALL_BUTTON)));
		installButton.setPreferredSize(preferredSize);
		installButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				installTestToolPackage(mainWindow, version, token);
				dispose();
				mainWindow.getTestCasesWindow(projectId, version);
			}
		});
		
		buttonPane.add(backButton);
		buttonPane.add(installButton);
		
		add(mainWindow.getHeaderPanel(),BorderLayout.NORTH);
		add(contentPanel,BorderLayout.CENTER);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}
	
	private Image readImageFromFile(String imagePath, String imageName)
	{
		Image image = null;
		
		try
		{
			image = ImageIO.read(new File(imagePath + File.separator + imageName));
		}
		catch (IOException e)
		{
			logger.error("Resource '" + imageName + "' not found");
		}
		
		return image;
	}
	
	/**
	 * Install test tool package from the web server.
	 */
	protected void installTestToolPackage(MainWindow mainWindow, String version, String token)
	{
		String releaseVersion = mainWindow.getLastReleaseVersion(version);
		String url = getConfigValue("TestToolWebAppUrl") + getConfigValue("GetTechnology") + version.replace(".", "_") + "_" + releaseVersion;

		logger.debug(url);

		try
		{
			URI URI = new URI(url);
			FileOutputStream fos = null;
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);
			HttpEntity entity = null;
			BufferedHttpEntity buf = null;
			
			postRequest.addHeader("Authorization", "bearer " + token);
			entity = httpClient.execute(postRequest).getEntity();
			buf = new BufferedHttpEntity(entity);
			
			logger.debug("Entity:"+entity);

			try
			{
				fos = new FileOutputStream(getConfigValue("TestCasesPackagePath") + "TestCases_Package_" + version + "_" + releaseVersion + ".jar");
			}
			catch (FileNotFoundException e)
			{
				File dir = new File(getConfigValue("TestCasesPackagePath"));
				if (dir.mkdirs())
				{
					fos = new FileOutputStream(getConfigValue("TestCasesPackagePath") + "TestCases_Package_" + version + "_" + releaseVersion + ".jar");
				}
			}
			buf.writeTo(fos);
		}
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server");
		}
		catch (URISyntaxException e)
		{
			logger.error("Malformed URL");
		}

		InputStream inputStream = null;
		String path = (new File("")).getAbsolutePath();
		String inputFile = "jar:file:/" + path + "/" + getConfigValue("TestCasesPackagePath") + "TestCases_Package_" + version
				+ "_" + releaseVersion + ".jar!/alljoyn_java.dll";
		
		logger.debug(inputFile);
		
		String libPath = path + File.separator + "lib" + File.separator + version + File.separator;
		
		logger.debug(libPath);
		
		File dir = new File(libPath);
		dir.mkdirs();
		
		URL inputURL = null;
		try
		{
			inputURL = new URL(inputFile);
			JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
			inputStream = conn.getInputStream();
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
			System.err.println(String.format("Malformed URL: %s",inputURL));
			return;
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			System.err.println("IO error opening connection");
			return;
		}
		 
		// write the inputStream to a FileOutputStream
		FileOutputStream outputStream = null;
		try
		{
			outputStream = new FileOutputStream(new File(path + File.separator + "lib" + File.separator + version + File.separator + "alljoyn_java.dll"));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		int read = 0;
		byte[] bytes = new byte[1024];
		try
		{
			while ((read = inputStream.read(bytes)) != -1)
			{
				outputStream.write(bytes, 0, read);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			outputStream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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

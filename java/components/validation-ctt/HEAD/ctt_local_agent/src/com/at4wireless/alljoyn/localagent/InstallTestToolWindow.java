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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The Class InstallTestToolWindow.
 */
public class InstallTestToolWindow extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7573195888353975145L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The version to install. */
	String version=null;
	
	/** The authentication token obtained when the application logs in. */
	String token="";
	
	/** The main window. */
	MainWindow mainWindow;
	/**
	 * Create the dialog.
	 * @param version 
	 * @param version 
	 * @param projectId 
	 */ 
	public InstallTestToolWindow(final MainWindow mainWindow,String token, final String version, final int projectId) {
		this.version=version;
		this.mainWindow=mainWindow;		
		this.token=token;
		
		int width=650;
		int height=300;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(dim.width/2-width/2,
				dim.height/2-height/2,
				width,
				height);
		
		setLayout(new GridLayout(0, 1, 0, 0));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();

				mainWindow.getProjectWindow();

			}
		});

		
		
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textPane.setEditable(false);

		
		String text="";
	
		text=text+"\n You need to install the following Test Cases Package: \n";
		text=text+"-TestCases_Package_"+version;
		textPane.setText(text);
		contentPanel.add(textPane);
		{
			ImagePanel buttonPane=new ImagePanel("res\\drawable\\short_footer.jpg");

			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			JButton backButton=new JButton("");
			
			 Image img = null;
			try {
				
				img = ImageIO.read(new File("res\\drawable\\back.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			backButton.setIcon(new ImageIcon(img));
			
			
			
			Dimension preferredSize=new Dimension(83,23);
			backButton.setPreferredSize(preferredSize);
			
			
			backButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("BACK");
					dispose();

					mainWindow.getProjectWindow();

				}});
			JButton installButton=new JButton("");
		
			 Image img2 = null;
			try {
				
				img2 = ImageIO.read(new File("res\\drawable\\install.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			installButton.setIcon(new ImageIcon(img2));
			installButton.setPreferredSize(preferredSize);
			installButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					installTestToolPackage();
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
	}
	
	/**
	 * Install test tool package from the web server.
	 */
	protected void installTestToolPackage() {

		int releaseVersion=mainWindow.getLastReleaseVersion(version);

		
		String url=getConfigValue("TestToolWebAppUrl")+
				getConfigValue("GetTechnology")
				+version.replace(".", "_")
				+"_R"+releaseVersion;
		System.out.println(url);



		try{
			URI URI = new URI(url);
			FileOutputStream fos = null;
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet postRequest = new HttpGet(URI);

			postRequest.addHeader("Authorization", "bearer "+token);


			HttpResponse response = httpClient.execute(postRequest);

			HttpEntity entity = response.getEntity();
			System.out.println("Entity:"+entity);

			BufferedHttpEntity buf = new BufferedHttpEntity(entity);

			String output;
			try{

				
				fos = new FileOutputStream(getConfigValue("TestCasesPackagePath")+"TestCases_Package_"+version+"_R"+releaseVersion+".jar");

			} catch (FileNotFoundException e) {

				File dir=new File(getConfigValue("TestCasesPackagePath"));
				if(dir.mkdirs()){

					fos = new FileOutputStream(getConfigValue("TestCasesPackagePath")+"TestCases_Package_"+version+"_R"+releaseVersion+".jar");


				}

			}

			buf.writeTo(fos);
		}catch(IOException e1){

			JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server"
					);
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}





		InputStream inputStream = null;
		String path = (new File("")).getAbsolutePath();
		String inputFile = "jar:file:/"+path+"/"+getConfigValue("TestCasesPackagePath")+"TestCases_Package_"+version+"_R"+releaseVersion+".jar!/alljoyn_java.dll";
		System.out.println(inputFile);
		
		String libPath=path+File.separator+"lib"+File.separator+version+File.separator;
		System.out.println(libPath);
		File dir=new File(libPath);
		dir.mkdirs();
		
		
			URL inputURL = null;
			try {
				inputURL = new URL(inputFile);
				JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
				inputStream = conn.getInputStream();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				System.err.println("Malformed input URL: "+inputURL);
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				System.err.println("IO error open connection");
				return;
			}
		 

		
		
		// write the inputStream to a FileOutputStream
				FileOutputStream outputStream = null;
				try {
					
					outputStream = new FileOutputStream(new File(path+File.separator+"lib"+File.separator+version+File.separator+"alljoyn_java.dll"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
		 
				int read = 0;
				byte[] bytes = new byte[1024];
		 try{
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
			
			e.printStackTrace();
		}
		Document conf=null;
		try {
			conf = dBuilder.parse(test);
		} catch (SAXException | IOException e) {
			
			e.printStackTrace();
		}
		NodeList projects = conf.getElementsByTagName("Configuration");
		Node node = projects.item(0);
		Element element = (Element) node;
		value=getValue(key, element);
		System.out.println(value);
		return value;
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
		String value="";
		try{
			value=node.getNodeValue();
		}catch(NullPointerException e){
			value="";
		}
		
		return value;
	}

}

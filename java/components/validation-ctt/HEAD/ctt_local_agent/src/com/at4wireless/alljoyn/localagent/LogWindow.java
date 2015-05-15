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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


// TODO: Auto-generated Javadoc
/**
 * The Class LogWindow.
 */
public class LogWindow extends JDialog{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -480896840677856306L;
	
	
	/** The result window. */
	ResultWindow resultWindow;
	
	/**
	 * Instantiates a new log window.
	 *
	 * @param resultWindow the result window
	 * @param projectId the project id
	 * @param user the user
	 * @param token the token
	 * @param logname the logname
	 */
	public LogWindow(final ResultWindow  resultWindow,final int projectId, String user, String token,String logname) {
		this.resultWindow=resultWindow;
		int width=900;
		int height=450;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(dim.width/2-width/2,
				dim.height/2-height/2,
				width,
				height);
		
		
		setResizable(false);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			 public void windowClosing(WindowEvent e) {
				
					
					
					resultWindow.setEnabled(true);
					dispose();
				 
				 
			 }
			
			
			
		});

		String log=getLog(user, token, projectId, logname);
		GridBagLayout gridBagLayout = new GridBagLayout();
		
		gridBagLayout.columnWeights = new double[]{0.9};
		gridBagLayout.rowWeights = new double[]{0.4, 0.8, 0.2};
		
		
		
		setLayout(gridBagLayout);

		
		JTextPane logPane=new JTextPane();
		logPane.setEditable(false);
		logPane.setText(log);
		JScrollPane scrollPane = new JScrollPane(logPane);
		
		scrollPane.setSize(300,800);
		
		
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.anchor = GridBagConstraints.SOUTHWEST;
		gbc_header.insets = new Insets(0, 0, 0, 0);
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		gbc_header.fill=GridBagConstraints.BOTH;
		getContentPane().add(new ImagePanel("res\\drawable\\header.jpg"),gbc_header);//,BorderLayout.NORTH);
		
		
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		
		gbc_scrollPane.insets = new Insets(0, 0, 0, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		
		
		gbc_scrollPane.fill=GridBagConstraints.BOTH;

		getContentPane().add(scrollPane,gbc_scrollPane);//, BorderLayout.CENTER);

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
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		back.setIcon(new ImageIcon(img2));
		
		Dimension preferredSize=new Dimension(83,23);
		back.setPreferredSize(preferredSize);
		back.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

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
		getContentPane().add(buttonPane,gbc_buttonPane);//, BorderLayout.SOUTH);
		setVisible(true);
	}




	/**
	 * Gets the log from the web server.
	 *
	 * @param user the user
	 * @param token The authentication token obtained when the application logs in. 
	 * @param projectId The project id
	 * @param logName the log name
	 * @return the log
	 */
	private String getLog(String user, String token, int projectId, String logName ) {


		String url=getConfigValue("TestToolWebAppUrl")+getConfigValue("GetLog");

		String test = "";
		//IOException Web is not found
		try {
			URI URL = new URI(url+user+"/"+projectId+"/"+logName);

				System.out.println(url+user);


				HttpClient httpClient = HttpClientBuilder.create().build();




				HttpGet postRequest = new HttpGet(URL);

				postRequest.addHeader("Authorization", "bearer "+token);


				HttpResponse response = httpClient.execute(postRequest);

				HttpEntity entity = response.getEntity();
				System.out.println("Entity:"+entity+"Encoding: "+entity.getContentEncoding());

				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(entity.getContent())));
				
				String output;

				while ((output = br.readLine()) != null) {

					test=test+output+"\n";
				}





			} catch (IOException e1) {

				e1.printStackTrace();

				Component button = new Button("rg");

				button.setVisible(false);

				JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server"
						);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
			
		return test;
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
		return node.getNodeValue();
	}



}

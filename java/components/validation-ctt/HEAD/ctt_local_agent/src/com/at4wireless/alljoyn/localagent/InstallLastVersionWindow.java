package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
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
public class InstallLastVersionWindow extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7573195888353975145L;
	private final JPanel contentPanel = new JPanel();
	String version=null;
	String token="";
	MainWindow mainWindow;
	/**
	 * Create the dialog.
	 * @param version 
	 * @param version 
	 */ 
	public InstallLastVersionWindow(final MainWindow mainWindow,String token, String version) {
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
		
		
		
		setResizable(false);
		setLayout(new GridLayout(0, 1, 0, 0));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();

				mainWindow.initialize();

			}
		});


		
		add(mainWindow.getHeaderPanel(), BorderLayout.NORTH);
		
		
		
		add(contentPanel, BorderLayout.CENTER);
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textPane.setEditable(false);
		
		

		String text="";
		
			text="You need to upgrade the Test Tool-Local Agent.\n -Version installed: "+version;
		
		
		textPane.setText(text);
		contentPanel.add(textPane);
		{
		
			ImagePanel buttonPane=new ImagePanel("res\\drawable\\short_footer.jpg");

			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			JButton backButton=new JButton("");
			backButton.setBorderPainted(false); 
			backButton.setContentAreaFilled(false); 
			 Image img = null;
			try {
				
				img = ImageIO.read(new File("res\\drawable\\back.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			backButton.setIcon(new ImageIcon(img));
			
			
			backButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					dispose();

					mainWindow.initialize();
					
					return;

				}});
			JButton installButton=new JButton("");
			
			installButton.setBorderPainted(false); 
			installButton.setContentAreaFilled(false); 
			 Image img2 = null;
			try {
				
				img2 = ImageIO.read(new File("res\\drawable\\install.jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			installButton.setIcon(new ImageIcon(img2));
			
			
			
			installButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					installLastVersion();
					dispose();
					mainWindow.initialize();
					
					
					
				}
			});
			
		

			buttonPane.add(backButton);

			buttonPane.add(installButton);
			

			getContentPane().add(buttonPane, BorderLayout.SOUTH);

		}
	}
	protected void installLastVersion() {
		
		

		JOptionPane.showMessageDialog(null, "The installation package is going to be downloaded in the following folder: ../"
				+ getConfigValue("DownloadPath"));

		String urlVersion=version.replaceAll("\\.", "_");
		String url=getConfigValue("TestToolWebAppUrl")+
				getConfigValue("getLastVersion");
		System.out.println(url);
		
		
		
		try{
			URI URI = new URI(url);
			FileOutputStream fos = null;
			HttpClient httpClient = HttpClientBuilder.create().build();




			HttpGet postRequest = new HttpGet(URI);
			
			postRequest.addHeader("Authorization", "bearer "+token);
			
			
			HttpResponse response = httpClient.execute(postRequest);
			
			HttpEntity entity = response.getEntity();
			  System.out.println("OK");

			  BufferedHttpEntity buf = new BufferedHttpEntity(entity);

				
					try{
				
				
				fos = new FileOutputStream(getConfigValue("DownloadPath")+"CTT_Local_Agent_Installer.exe");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				File dir=new File(getConfigValue("DownloadPath"));
					if(dir.mkdirs()){
						
						fos = new FileOutputStream(getConfigValue("DownloadPath")+"CTT_Local_Agent_Installer.exe");

						
					}
				
				}
			
			buf.writeTo(fos);
			}catch(IOException e1){
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server"
						);
				} catch (URISyntaxException e) {
					
					e.printStackTrace();
				}
		
		
	
		
		
	}
	
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

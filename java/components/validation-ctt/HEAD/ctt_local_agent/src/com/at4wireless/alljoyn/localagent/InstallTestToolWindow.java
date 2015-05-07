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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public class InstallTestToolWindow extends JDialog {
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

		String installed[]=getInstalledVersions();
		String text="";
		
		/*if(installed[0].equals("")){
			text="You need to install the following Test Cases Package:  \n";
			
		}else{
		 text="You have the following Technology Package installed: \n";
		for(int i=0;i<installed.length;i++){
			System.out.println("-"+installed[i]);
			text=text+"-"+installed[i]+"\n";
		}
		}*/
		text=text+"\n You need to install the following Test Cases Package: \n";
		text=text+"-TestCases_Package_"+version;
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
					System.out.println("BACK");
					dispose();

					mainWindow.getProjectWindow();

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
	protected void installTestToolPackage() {
		
		System.out.println("installing.....");

		
		String urlVersion=version.replaceAll("\\.", "_");
		String url=getConfigValue("TestToolWebAppUrl")+
				getConfigValue("GetTechnology")
				+urlVersion;
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
				
				
				fos = new FileOutputStream(getConfigValue("CertificationReleasePath")+"TestCases_Package_"+version+".jar");
				
			} catch (FileNotFoundException e) {
				
				File dir=new File(getConfigValue("CertificationReleasePath"));
					if(dir.mkdirs()){
						
						fos = new FileOutputStream(getConfigValue("CertificationReleasePath")+"TestCases_Package_"+version+".jar");

						
					}
				
				}
			
			buf.writeTo(fos);
			}catch(IOException e1){
					
				JOptionPane.showMessageDialog(null, "Fail in the communication with the Test Tool Web Server"
						);
				} catch (URISyntaxException e) {
					
					e.printStackTrace();
				}
		
		
		String installed=getConfigValue("InstalledTestToolPackages");
		installed.concat(":TestCases_Package_"+version);
		//TODO add to config file
		
		
	}
	private String[] getInstalledVersions() {
		String versions[];
		
		String installed=getConfigValue("InstalledTestToolPackages");
		
		versions=installed.split(":");
		return versions;
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

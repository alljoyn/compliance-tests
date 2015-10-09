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
package com.at4wireless.alljoyn.localagent.model.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

public class ModelCommons
{
	private static final String configurationFileName = "config.xml";
	private static final int CONNECTION_REQUEST_TIMEOUT_IN_MILLISECONDS = 5000;
	private static final short NUMBER_OF_RETRIES = 3;
	private static final HttpClient httpClient = HttpClientBuilder.create().build();
	
	public static ClientResponse unsecuredPostRequest(String url, Form postForm) throws ClientHandlerException
	{
		ClientConfig configuration = new DefaultClientConfig();
		Client client = Client.create(configuration);
		URI uri = UriBuilder.fromUri(url).build();
		WebResource postResource = client.resource(uri);
		
		return postResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, postForm);
	}
	
	public static HttpResponse securedPostRequest(String url, String sessionToken, HttpEntity requestBody) throws URISyntaxException, IOException
	{
		URI URI = new URI(url);
		HttpPost postRequest = new HttpPost(URI);
		RequestConfig Default = RequestConfig.DEFAULT;
		RequestConfig requestConfig = RequestConfig.copy(Default)
				.setSocketTimeout(2000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_IN_MILLISECONDS)
				.build();
		
		postRequest.setConfig(requestConfig);
		
		postRequest.addHeader("Authorization", "bearer " + sessionToken);
		postRequest.addHeader("Accept-Encoding", "UTF-8");
		postRequest.setEntity(requestBody);
		
		short connectionNumber = 0;
		boolean isResponseReceived = false;
		HttpResponse receivedResponse = null;
		
		while ((connectionNumber < NUMBER_OF_RETRIES) && (!isResponseReceived))
		{
			try
			{
				receivedResponse = httpClient.execute(postRequest);
				isResponseReceived = true;
			}
			catch (SocketTimeoutException connectionTimeoutException)
			{
				if (connectionNumber < NUMBER_OF_RETRIES - 1)
				{
					connectionNumber++;
				}
				else
				{
					throw connectionTimeoutException;
				}
			}
		}
		return receivedResponse;
	}
	
	public static HttpResponse securedGetRequest(String url, String sessionToken) throws URISyntaxException, IOException
	{
		URI URI = new URI(url);
		HttpGet getRequest = new HttpGet(URI);
		RequestConfig Default = RequestConfig.DEFAULT;
		RequestConfig requestConfig = RequestConfig.copy(Default)
				.setSocketTimeout(2000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_IN_MILLISECONDS)
				.build();
		
		getRequest.setConfig(requestConfig);
		getRequest.addHeader("Authorization", "bearer " + sessionToken);
		
		short connectionNumber = 0;
		boolean isResponseReceived = false;
		HttpResponse receivedResponse = null;
		
		while ((connectionNumber < NUMBER_OF_RETRIES) && (!isResponseReceived))
		{
			try
			{
				receivedResponse = httpClient.execute(getRequest);
				isResponseReceived = true;
			}
			catch (SocketTimeoutException connectionTimeoutException)
			{
				if (connectionNumber < NUMBER_OF_RETRIES - 1)
				{
					connectionNumber++;
				}
				else
				{
					throw connectionTimeoutException;
				}
			}
		}
		return receivedResponse;
	}
	
	/**
	 * Generates a hash value from an input string
	 * 
	 * @param rawString
	 * 			input string whose hash value is going to be generated
	 * 
	 * @return SHA-256 hash value
	 */
	public static String hashString(String rawString, String algorithm)
	{
		MessageDigest m = null;
		
		try
		{
			m = MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException e1)
		{
			
		}

		m.update(rawString.getBytes(), 0, rawString.length());

		return convertByteArrayToHexString(m.digest());
	}
	
	/**
	 * Converts byte array to hex string.
	 *
	 * @param arrayBytes
	 * 			array of bytes to be converted
	 * 
	 * @return resulting string
	 */
	private static String convertByteArrayToHexString(byte[] arrayBytes)
	{
		StringBuffer stringBuffer = new StringBuffer();
		
		for (int i = 0; i < arrayBytes.length; i++)
		{
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Gets the configuration value from config.xml.
	 *
	 * @param configurationField
	 * 			the configuration field to be used
	 * 
	 * @return value
	 */
	public static String getConfigValue(String configurationField)
	{
		File cfFile = new File(configurationFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		Element element = null;
		
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(cfFile);
			element = (Element) doc.getElementsByTagName("Configuration").item(0);
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getValue(configurationField, element);
	}

	/**
	 * Gets the value of the selected tag from config.xml. 
	 *
	 * @param tag
	 * 			desired value to recover
	 * @param element
	 * 			element with the xml string
	 * 
	 * @return value
	 */
	public static String getValue(String tag, Element element)
	{
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	public static File[] getTestCasesPackageFiles(String certificationRelease)
	{
		File tcPackagesFolder = new File(ModelCommons.getConfigValue("TestCasesPackagePath"));
		
		if (!tcPackagesFolder.exists())
		{
			tcPackagesFolder.mkdirs();
		}
		
		return tcPackagesFolder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.contains(certificationRelease + "_");
			}
		});
	}
	
	/**
	 * Reads a file.
	 *
	 * @param path
	 * 			location of the file
	 * @param encoding
	 * 			encoding of the information
	 * 
	 * @return file contents
	 * @throws IOException 	if file does not exist
	 */
	public static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void deleteFile(String fileName)
	{
		File fileToBeDeleted = new File(fileName);
		
		if (fileToBeDeleted.exists())
		{
			fileToBeDeleted.delete();
		}
	}
	
	public static void saveFile(String filePath, String fileName, String fileContent)
	{
		PrintWriter out = null;
		
		try
		{
			File dir = new File(filePath);
			
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			
			out = new PrintWriter(filePath + File.separator + fileName, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{

		}
		catch (FileNotFoundException e)
		{

		}
		
		out.println(fileContent);
		out.close();
	}
	
	public static String encodePathVariable(String pathVariable)
	{
		return DatatypeConverter.printBase64Binary(pathVariable.getBytes());
	}
}
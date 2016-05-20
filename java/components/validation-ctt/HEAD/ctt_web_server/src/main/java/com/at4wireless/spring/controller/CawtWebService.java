package com.at4wireless.spring.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

import sun.net.www.protocol.https.HttpsURLConnectionImpl;

public class CawtWebService
{
	private String baseURL;
	private String secret;
	
	public CawtWebService(String base, String secret)
	{
		this.baseURL = base;
		this.secret = secret;
	}
	
	// Build the content of an HTTP post, given a set of parameters.
	// We're building x-www-urlencoded data.
	// This is only ok for simple POST. Complex posts with files will need
	// to use multipart/form-data.
	protected String postData(Map<String, String> params) throws Exception 
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		for (String key : params.keySet())
		{
			if (!first)
			{
				sb.append('&');
			}
			
			first = false;
			
			sb.append(URLEncoder.encode(key, "UTF-8"));
			sb.append('=');
			sb.append(URLEncoder.encode(params.get(key), "UTF-8"));
		}
		
		return sb.toString();
	}
	
	// Setup the auth-specific parameters: timestamp, username, password
	// Other params can be added as well, eg: CRI
	protected Map<String, String> authParams(String method, String user) throws Exception
	{
		Map<String, String> params = new HashMap<String, String>();
		
		// Add username and timestamp
		params.put("username", user);
		String timestamp = Long.toString(new Date().getTime() / 1000L);
		params.put("timestamp", timestamp);
		
		// Setup HMAC-MD5
		SecretKey key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacMD5");
		Mac mac = Mac.getInstance("HmacMD5");
		mac.init(key);
		
		// Build the HMAC from method + timestamp + user
		mac.update(method.getBytes("UTF-8"));
		mac.update(timestamp.getBytes("UTF-8"));
		mac.update(user.getBytes("UTF-8"));
		byte[] hmacBytes = mac.doFinal();
		
		// Convert HMAC to hex, that's the password
		StringBuilder hmac = new StringBuilder();
		
		for (byte b : hmacBytes)
		{
			hmac.append(String.format("%02x", b));
		}
		
		params.put("password", hmac.toString());
		
		return params;
	}
	
	// Connect over HTTPS
	//protected HttpURLConnection connect(String method, Map<String, String> params) throws Exception
	protected HttpsURLConnectionImpl connect(String method, Map<String, String> params) throws Exception
	{
		// Build the URL
		StringBuilder sb = new StringBuilder(baseURL);
		sb.append("/webservice/");
		sb.append(method);
		
		//Setup the connection
		URL url = new URL(sb.toString());
		//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		HttpsURLConnectionImpl conn = (HttpsURLConnectionImpl) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		// On docker-dev, we have HTTP Basic Auth. This won't be necessary in prod.
		String info = url.getUserInfo();
		
		if (info != null)
		{
			String auth = Base64.getEncoder().encodeToString(info.getBytes());
			conn.setRequestProperty("Authorization", "Basic " + auth);
		}
		
		// Write the params
		String data = postData(params);
		OutputStream out = conn.getOutputStream();
		out.write(data.getBytes());
		out.close();
		
		return conn;
	}
	
	public String acceptedApplicationsRaw(String user) throws Exception
	{
		String method = "retrieve_accepted_applications";
		Map<String, String> params = authParams(method, user);
		// You could add more things to params here.
		
		// Setup the connection
		//HttpURLConnection conn = connect(method, params);
		HttpsURLConnectionImpl conn = connect(method, params);
		
		// Collect data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = conn.getInputStream();
		byte[] buffer = new byte[512];
		while (true)
		{
			int bytes = in.read(buffer);
			
			if (bytes == -1)
			{
				break;
			}
			
			out.write(buffer, 0, bytes);
		}
		
		conn.disconnect();
		return out.toString("UTF-8");
	}
	
	public String uploadTestResults(String user, String cri, String testResultsFilePath) throws Exception
	{
		String method = "upload_test_results";
		Map<String, String> params = authParams(method, user);
		params.put("cri", cri);
		// You could add more things to params here.
		
		return connectMultipartFile(method, params, testResultsFilePath);
	}
	
	protected String connectMultipartFile(String method, Map<String, String> params, String testResultsFilePath) throws Exception
	{
		URI url = new URI(baseURL+"/webservice/" + method);

		HttpPost postRequest = new HttpPost(url);
		/*RequestConfig Default = RequestConfig.DEFAULT;
		RequestConfig requestConfig = RequestConfig.copy(Default)
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.build();
		
		postRequest.setConfig(requestConfig);*/
		
		String info = url.getUserInfo();
		
		if (info != null)
		{
			String auth = Base64.getEncoder().encodeToString(info.getBytes());
			postRequest.addHeader("Authorization", "Basic " + auth);
		}
		
		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.addTextBody("username", params.get("username"))
				.addTextBody("timestamp", params.get("timestamp"))
				.addTextBody("password", params.get("password"))
				.addTextBody("cri", params.get("cri"))
				.addPart("test_results_file", new FileBody(new File(testResultsFilePath)))
				.build();
		
		
		postRequest.setEntity(httpEntity);
		
		HttpResponse receivedResponse = null;
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		receivedResponse = httpClient.execute(postRequest);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = receivedResponse.getEntity().getContent();
		byte[] buffer = new byte[512];
		while (true)
		{
			int bytes = in.read(buffer);
			
			if (bytes == -1)
			{
				break;
			}
			
			out.write(buffer, 0, bytes);
		}
		
		return out.toString("UTF-8");
	}
}
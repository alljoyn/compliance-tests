package com.at4wireless.security;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.xml.bind.DatatypeConverter;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyRetriever;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

import com.at4wireless.spring.model.User;
import com.at4wireless.spring.service.UserService;

public class CustomCas20ServiceTicketValidator implements TicketValidator {
	//private Logger logger = LogManager.getLogger(this.getClass().getName());
	private boolean renew;
	private Map<String, String> customParameters;
	private final String casServerUrlPrefix;
	private ProxyGrantingTicketStorage proxyGrantingTicketStorage;
	private HostnameVerifier hostnameVerifier;
	private ProxyRetriever proxyRetriever;
	private String encoding;

	@Autowired
	private UserService userService;

	public CustomCas20ServiceTicketValidator(String casServerUrlPrefix)
	{
		this.casServerUrlPrefix = casServerUrlPrefix;
		CommonUtils.assertNotNull(this.casServerUrlPrefix, "casServerUrlPrefix cannot be null.");

		this.proxyRetriever = new Cas20ProxyRetriever(casServerUrlPrefix);
	}

	@Override
	public Assertion validate(String ticket, String service)
			throws TicketValidationException
			{
		try
		{

			//logger.debug(" *** In validate() method of CustomCas20ServiceTicketValidator *** ");

			String validationUrl = constructValidationUrl(ticket, service);


			//logger.debug("#### The  Validation URL is:"+validationUrl);
			String serverResponse = retrieveResponseFromServer(new URL(validationUrl), ticket);
			//logger.debug("#### The Server Response is123:"+serverResponse);

			if (serverResponse == null) {
				throw new TicketValidationException("The CAS server returned no response.");
			}

			return parseResponseFromServer(serverResponse);
		} catch (MalformedURLException e) {

			//logger.equals(e);
			throw new TicketValidationException(e);
		}
			}


	@SuppressWarnings("rawtypes")
	private String constructValidationUrl(String ticket, String serviceUrl)
	{
		Map<String,String> urlParameters = new HashMap<String,String>();

		urlParameters.put("ticket", ticket);
		urlParameters.put("service", encodeUrl(serviceUrl));
		if (this.renew) {
			urlParameters.put("renew", "true");
		}
		//logger.debug("Calling template URL attribute map.");
		populateUrlAttributeMap(urlParameters);
		//logger.debug("Loading custom parameters from configuration.");
		if (this.customParameters != null) {
			urlParameters.putAll(this.customParameters);
		}
		String suffix = getUrlSuffix();
		StringBuilder buffer = new StringBuilder(urlParameters.size() * 10 + this.casServerUrlPrefix.length() + suffix.length() + 1);
		int i = 0;
		buffer.append(this.casServerUrlPrefix);
		if (!this.casServerUrlPrefix.endsWith("/")) {
			buffer.append("/");
		}
		buffer.append(suffix);
		for (Map.Entry entry : urlParameters.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (value != null) {
				buffer.append(i++ == 0 ? "?" : "&");
				buffer.append(key);
				buffer.append("=");
				buffer.append(value);
			}
		}
		return buffer.toString();
	}
	private  Assertion parseResponseFromServer(String response) throws TicketValidationException {
		String error = XmlUtils.getTextForElement(response, "authenticationFailure");
		if (CommonUtils.isNotBlank(error)) {
			throw new TicketValidationException(error);
		}
		String principal = XmlUtils.getTextForElement(response, "user");

		String proxyGrantingTicketIou = XmlUtils.getTextForElement(response, "proxyGrantingTicket");
		String proxyGrantingTicket = this.proxyGrantingTicketStorage != null ? this.proxyGrantingTicketStorage.retrieve(proxyGrantingTicketIou) : null;
		if (CommonUtils.isEmpty(principal)) {
			throw new TicketValidationException("No principal was found in the response from the CAS server.");
		}
		Map<String,String> attributes = extractCustomAttributes(response);
		Assertion assertion;
		System.out.println(attributes.size());

		for (String key : attributes.keySet()) {
			System.out.println(key+" : "+attributes.get(key));
		}

		if(userService.exists(principal)) {
			String role = null;

			if(attributes.get("drupal_roles").equalsIgnoreCase("authenticated user")) {
				role = "ROLE_USER";
			} else if(attributes.get("drupal_roles").equalsIgnoreCase("asa-testtool-admin")) {
				role = "ROLE_ADMIN";
			}
			
			/*if (!userService.hasCipherKey(principal)) {
				try {
					FileEncryption fE = new FileEncryption();
					fE.makeKeys();
					userService.setCipherKey(principal, 
							DatatypeConverter.printBase64Binary(fE.getAesSecretKey().getEncoded()));
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			userService.update(principal,role);
		} else {
			User u = new User();
			u.setUser(principal);
			u.setPassword("");
			u.setRepPassword("");
			if(attributes.get("drupal_roles").equalsIgnoreCase("authenticated user")) {
				u.setRole("ROLE_USER");
			} else if(attributes.get("drupal_roles").equalsIgnoreCase("asa-testtool-admin")) {
				u.setRole("ROLE_ADMIN");
			}
			
			try {
				FileEncryption fE = new FileEncryption();
				fE.makeKeys();
				u.setAesSecretKey(DatatypeConverter.printBase64Binary(fE.getAesSecretKey().getEncoded()));
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userService.addUser(u);
		}

		if (CommonUtils.isNotBlank(proxyGrantingTicket)) {
			AttributePrincipal attributePrincipal = new AttributePrincipalImpl(principal, attributes, proxyGrantingTicket, this.proxyRetriever);
			assertion = new AssertionImpl(attributePrincipal);
		} else {
			assertion = new AssertionImpl(new AttributePrincipalImpl(principal, attributes));
		}
		return assertion;
	}


	private String retrieveResponseFromServer(URL validationUrl, String ticket)
	{
		if (this.hostnameVerifier != null) {
			return CommonUtils.getResponseFromServer(validationUrl);
		}
		return CommonUtils.getResponseFromServer(validationUrl);
	}
	private String encodeUrl(String url)
	{
		if (url == null) {
			return null;
		}
		try {
			return URLEncoder.encode(url, "UTF-8"); } catch (UnsupportedEncodingException e) {
		}
		return url;
	}

	private void populateUrlAttributeMap(Map<String, String> urlParameters)
	{
		//This method can be used for populating urlParameters Map with Proxy call back values
	}

	protected String getUrlSuffix() {
		return "serviceValidate";
		//return "samlValidate";
	}
	private Map<String, String> extractCustomAttributes(String xml)
	{
		System.out.println(xml);
		int pos1 = xml.indexOf("<cas:attributes>");
		int pos2 = xml.indexOf("</cas:attributes>");
		if (pos1 == -1) {
			return Collections.emptyMap();
		}
		Map<String,String> attributes = new HashMap<String,String>();

		try {
			JSONObject json = XML.toJSONObject(xml.substring(pos1 + 16, pos2));

			@SuppressWarnings("unchecked")
			Iterator<String> it = json.keys();
			while(it.hasNext()) {
				String str = it.next();

				if((str.equalsIgnoreCase("cas:drupal_roles"))&&(json.getString(str).contains("["))) {
					JSONArray jsonArray = json.getJSONArray(str);
					for (int i=0; i<jsonArray.length(); i++) {
						System.out.println(jsonArray.getString(i));
						if(jsonArray.getString(i).equalsIgnoreCase("asa-testtool-admin")) {
							attributes.put(str.split(":")[1], jsonArray.getString(i));
							break;
						} else if (jsonArray.getString(i).equalsIgnoreCase("authenticated user")) {
							attributes.put(str.split(":")[1], jsonArray.getString(i));
						}
					}
				} else {
					attributes.put(str.split(":")[1], json.getString(str));
				}

				System.out.println(str+" : "+attributes.get(str.split(":")[1]));
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return attributes;
	}


	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public void setRenew(boolean renew) {
		this.renew = renew;
	}
	public void setCustomParameters(Map<String, String> customParameters) {
		this.customParameters = customParameters;
	}
	public void setProxyGrantingTicketStorage(
			ProxyGrantingTicketStorage proxyGrantingTicketStorage) {
		this.proxyGrantingTicketStorage = proxyGrantingTicketStorage;
	}
	public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}



}


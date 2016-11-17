package com.at4wireless.security;

import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyRetriever;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.AbstractCasProtocolUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.at4wireless.spring.model.User;
import com.at4wireless.spring.service.UserService;

public class CustomCas20ServiceTicketValidator extends AbstractCasProtocolUrlBasedTicketValidator
{
	/** The storage location of the proxy granting tickets. */
	private ProxyGrantingTicketStorage proxyGrantingTicketStorage;
	
	/** Implementation of the proxy retriever. */
	private ProxyRetriever proxyRetriever;
	
	/** CAS and CTT role tags */
	private static final String CTT_ROLE_USER = "ROLE_USER";
	private static final String CTT_ROLE_ADMIN = "ROLE_ADMIN";
	private static final String CAS_ROLE_USER = "authenticated user";
	private static final String CAS_GROUP_ADMIN = "asa-testtool-admin";

	/** The service used to manage User related actions */
	@Autowired
	private UserService userService;

	/**
	 * Constructs an instance of the CAS 2.0 Service Ticket Validator with the supplied
	 * CAS server url prefix.AT4
	 * 
	 * @param casServerUrlPrefix the CAS Server URL prefix.
	 */
	public CustomCas20ServiceTicketValidator(String casServerUrlPrefix) {
		super(casServerUrlPrefix);
		this.proxyRetriever = new Cas20ProxyRetriever(casServerUrlPrefix, getEncoding(), getURLConnectionFactory());
	}
	
	/**
	 * This method can be used for populating urlParameters Map with Proxy call back values.
	 * 
	 * @param urlParameters the Map containing the existing parameters to send to the server.
	 */
	protected final void populateUrlAttributeMap(Map<String, String> urlParameters) {}

	protected String getUrlSuffix() {
		return "serviceValidate";
	}
	
	protected final Assertion parseResponseFromServer(String response) throws TicketValidationException {
		final String error = XmlUtils.getTextForElement(response, "authenticationFailure");
		
		if (CommonUtils.isNotBlank(error)) {
			throw new TicketValidationException(error);
		}
		
		final String principal = XmlUtils.getTextForElement(response, "user");
		final String proxyGrantingTicketIou = XmlUtils.getTextForElement(response, "proxyGrantingTicket");
		
		final String proxyGrantingTicket;
		
		if (CommonUtils.isBlank(proxyGrantingTicketIou) || this.proxyGrantingTicketStorage == null) {
			proxyGrantingTicket = null;
		} else {
			proxyGrantingTicket = this.proxyGrantingTicketStorage.retrieve(proxyGrantingTicketIou);
		}
		
		if (CommonUtils.isEmpty(principal))
		{
			throw new TicketValidationException("No principal was found in the response from the CAS server.");
		}
		
		final Assertion assertion;
		final Map<String, Object> attributes = extractCustomAttributes(response);
				
		addOrUpdateUser(principal, attributes);

		if (CommonUtils.isNotBlank(proxyGrantingTicket)) {
			final AttributePrincipal attributePrincipal = new AttributePrincipalImpl(principal, attributes,
					proxyGrantingTicket, this.proxyRetriever);
			assertion = new AssertionImpl(attributePrincipal);
		} else {
			assertion = new AssertionImpl(new AttributePrincipalImpl(principal, attributes));
		}
		
		return assertion;
	}
	
	/**
	 * Adds new user's information to be used with Web Server and Local Agent to the database.
	 * Also, updates user's role if necessary
	 * 
	 * Admins are referenced as belonging to the group 'asa-testtool-admin'
	 * Users have assigned the drupal-role 'authenticated user'
	 * 
	 * @param principal user to be added or updated
	 * @param attributes CAS custom attributes to check
	 * @throws TicketValidationException if received role is not valid or AES Secret Key is not generated properly
	 */
	private void addOrUpdateUser(final String principal, final Map<String, Object> attributes)
			throws TicketValidationException {
		logger.trace("addOrUpdateUser method starts");
		if (userService.exists(principal)) {
			logger.debug("User already exists");
			String receivedRole = parseRoleFromCasToCtt(attributes);
			
			if (!receivedRole.equals(userService.getUserRole(principal))) {
				logger.trace("Received role is different from stored. Updating...");
				userService.update(principal, receivedRole);
			} else {
				logger.trace("Received role is equal to stored");
			}
		} else {
			logger.debug("User does not exist");
			final String aesSecretKey;
			try {
				logger.trace("Creating AES Secret Key");
				FileEncryption fileEncryption = new FileEncryption();
				fileEncryption.makeKeys();
				aesSecretKey = DatatypeConverter.printBase64Binary(fileEncryption.getAesSecretKey().getEncoded());
			} catch (GeneralSecurityException e) {
				throw new TicketValidationException(e);
			}
			
			logger.trace("Adding user");
			userService.addUser(new User(principal, "", "", parseRoleFromCasToCtt(attributes), aesSecretKey));
		}
		logger.trace("addOrUpdateUser method ends");
	}
	
	/**
	 * Parses user's role from CAS values to CTT values
	 * 
	 * @param attributes CAS attributes from which evaluate user CAS role
	 * @return user CTT role
	 * @throws TicketValidationException if received ticket does not contain a valid role
	 */
	@SuppressWarnings("unchecked")
	protected String parseRoleFromCasToCtt(Map<String, Object> attributes) throws TicketValidationException
	{
		if (attributes.containsKey("group")) {
			if (attributes.get("group") instanceof LinkedList) {
				if (((LinkedList<String>)attributes.get("group")).contains(CAS_GROUP_ADMIN)) {
					return CTT_ROLE_ADMIN;
				}
			} else if (attributes.get("group") instanceof String) {
				if (((String)attributes.get("group")).equals(CAS_GROUP_ADMIN)) {
					return CTT_ROLE_ADMIN;
				}
			}
		}
		
		if (attributes.containsKey("drupal_roles")) {
			if (attributes.get("drupal_roles") instanceof LinkedList) {
				if (((LinkedList<String>)attributes.get("drupal_roles")).contains(CAS_ROLE_USER)) {
					return CTT_ROLE_USER;
				}
			} else if (attributes.get("drupal_roles") instanceof String) {
				if (((String)attributes.get("drupal_roles")).equals(CAS_ROLE_USER)) {
					return CTT_ROLE_USER;
				}
			}
		}
		
		throw new TicketValidationException("Neither admin nor user credentials were received");
	}
	
	/**
	 * Default attribute parsing of attributes that look like the following:
	 * &lt;cas:attributes&gt;
	 *  &lt;cas:attribute1&gt;value&lt;/cas:attribute1&gt;
	 *  &lt;cas:attribute2&gt;value&lt;/cas:attribute2&gt;
	 * &lt;/cas:attributes&gt;
	 * <p>
	 * 
	 * Attributes look like following also parsed correctly:
	 * &lt;cas:attributes&gt;&lt;cas:attribute1&gt;value&lt;/cas:attribute1&gt;&lt;cas:attribute2&gt;value&lt;/cas:attribute2&gt;&lt;/cas:attributes&gt;
	 * <p>
	 * 
	 * @param xml the XML to parse
	 * @return the map of attributes
	 */
	protected Map<String, Object> extractCustomAttributes(final String xml) {
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setValidating(false);
		try {
			final SAXParser saxParser = spf.newSAXParser();
			final XMLReader xmlReader = saxParser.getXMLReader();
			final CustomAttributeHandler handler = new CustomAttributeHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new StringReader(xml)));
			return handler.getAttributes();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			return Collections.emptyMap();
		}
	}
	
	/**
	 * Template method if additional custom parsing (such as Proxying) needs to be done
	 * 
	 * @param response the original response from the CAS server.
	 * @param assertion the partially constructed assertion.
	 * @throws TicketValidationException if there is a problem constructing the Assertion.
	 */
	protected void customParseResponse(final String response, final Assertion assertion)
			throws TicketValidationException {
		// nothing to do
	}

	public final void setProxyGrantingTicketStorage(final ProxyGrantingTicketStorage proxyGrantingTicketStorage) {
		this.proxyGrantingTicketStorage = proxyGrantingTicketStorage;
	}
	
	public final void setProxyRetriever(final ProxyRetriever proxyRetriever) {
		this.proxyRetriever = proxyRetriever;
	}
	
	protected final ProxyGrantingTicketStorage getProxyGrantingTicketStorage() {
		return this.proxyGrantingTicketStorage;
	}
	
	protected final ProxyRetriever getProxyRetriever() {
		return this.proxyRetriever;
	}
	
	private class CustomAttributeHandler extends DefaultHandler {
		
		private Map<String, Object> attributes;
		private boolean foundAttributes;
		private String currentAttribute;
		private StringBuilder value;
		
		@Override
		public void startDocument() throws SAXException {
			this.attributes = new HashMap<String, Object>();
		}
		
		@Override
		public void startElement(final String namespaceURI, final String localName, final String qName,
				final Attributes attributes) throws SAXException {
			if ("attributes".equals(localName)) {
				this.foundAttributes = true;
			} else if (this.foundAttributes) {
				this.value = new StringBuilder();
				this.currentAttribute = localName;
			}
		}
		
		@Override
		public void characters(final char[] chars, final int start, final int length) throws SAXException {
			if (this.currentAttribute != null) {
				value.append(chars, start, length);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void endElement(final String namespaceURI, final String localName, final String qName)
				throws SAXException {
			if ("attributes".equals(localName)) {
				this.foundAttributes = false;
				this.currentAttribute = null;
			} else if (this.foundAttributes) {
				final Object o = this.attributes.get(this.currentAttribute);
				
				if (o == null) {
					this.attributes.put(this.currentAttribute, this.value.toString());
				} else {
					final List<Object> items;
					if (o instanceof List) {
						items = (List<Object>) o;
					} else {
						items = new LinkedList<Object>();
						items.add(o);
						this.attributes.put(this.currentAttribute, items);
					}
					items.add(this.value.toString());
				}
			}
		}
		
		public Map<String, Object> getAttributes() {
			return this.attributes;
		}
	}
}
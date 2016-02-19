package com.at4wireless.alljoyn.core.introspection;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class IntrospectionXmlErrorHandler implements ErrorHandler
{
	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		// Avoid printing messages
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		// Avoid printing messages
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
		// Avoid printing messages
	}
}

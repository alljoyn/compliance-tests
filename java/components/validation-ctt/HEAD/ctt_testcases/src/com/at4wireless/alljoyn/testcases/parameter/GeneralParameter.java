package com.at4wireless.alljoyn.testcases.parameter;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeneralParameter
{
private Document xmlDocument;
	
	public int GPCO_AnnouncementTimeout = -1;
	public int GPON_WaitSoftAP = -1;
	public int GPON_ConnectSoftAP = -1;
	public int GPON_WaitSoftAPAfterOffboard = -1;
	public int GPON_ConnectPersonalAP = -1;
	public int GPON_Disconnect = -1;
	public int GPON_NextAnnouncement = -1;
	public int GPON_TimeToWaitForScanResults = -1;
	public int GPCF_SessionLost = -1;
	public int GPCF_SessionClose = -1;
	public int GPL_SessionClose = -1;
	public int GPAU_Signal = -1;
	public int GPAU_Link = -1;
	public int GPG_SessionClose = -1;
	public int GPSH_Signal = -1;
	public int GPLC_SessionClose = -1;
	
	public GeneralParameter(Document xmlDocument)
	{
		this.xmlDocument = xmlDocument;
		
		loadGpValuesFromXml();
	}
	
	public void printGpValues(String serviceFramework)
	{
		for (Field field : this.getClass().getFields())
		{
			try
			{
				if (field.getName().contains("GPCO_") || (field.getName().contains(serviceFramework+"_")))
				{
					System.out.println(field.getName() + " : " + field.getInt(this));
				}
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void loadGpValuesFromXml()
	{
		NodeList gParameter = xmlDocument.getElementsByTagName("Parameter");
			
		for (int i = 0; i < gParameter.getLength(); i++)
		{
			Node node = gParameter.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String gpName = getValue("Name", element);
				String gpValue = getValue("Value", element);
				saveGpIntoVariable(gpName, Integer.parseInt(gpValue));
			}
		}
	}
	
	private String getValue(String tag, Element element)
	{
		String value = "";
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		
		if (node != null)
		{
			value = node.getNodeValue();
		}
		return value;
	}
	
	private void saveGpIntoVariable(String gpName, int gpValue)
	{
		try
		{
			this.getClass().getField(gpName).setInt(this, gpValue);
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
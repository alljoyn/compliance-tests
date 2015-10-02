package com.at4wireless.alljoyn.testcases.parameter;

import java.lang.reflect.Field;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class Ixit
{
	private static final Logger logger = new WindowsLoggerImpl(Ixit.class.getSimpleName());
	private Document xmlDocument;
	
	public Integer IXITCO_AboutVersion = null;
	public UUID IXITCO_AppId = null;
	public String IXITCO_DefaultLanguage = null;
	public String IXITCO_DeviceName = null;
	public String IXITCO_DeviceId = null;
	public String IXITCO_AppName = null;
	public String IXITCO_Manufacturer = null;
	public String IXITCO_ModelNumber = null;
	public String IXITCO_SoftwareVersion = null;
	public String IXITCO_AJSoftwareVersion = null;
	public String IXITCO_HardwareVersion = null;
	public Integer IXITCO_IntrospectableVersion = null;
	public String IXITCO_SupportedLanguages = null;
	public String IXITCO_Description = null;
	public String IXITCO_DateOfManufacture = null;
	public String IXITCO_SupportUrl = null;
	public Integer IXITN_NotificationVersion = null;
	public Integer IXITN_TTL = null;
	public Integer IXITN_NotificationProducerVersion = null;
	public Integer IXITN_NotificationDismisserVersion = null;
	public Short IXITON_OnboardingVersion = null;
	public String IXITON_SoftAP = null;
	public String IXITON_SoftAPAuthType = null;
	public String IXITON_SoftAPpassphrase = null;
	public String IXITON_PersonalAP = null;
	public String IXITON_PersonalAPAuthType = null;
	public String IXITON_PersonalAPpassphrase = null;
	public Short IXITCP_ControlPanelVersion = null;
	public Short IXITCP_ContainerVersion = null;
	public Short IXITCP_PropertyVersion = null;
	public Short IXITCP_LabelPropertyVersion = null;
	public Short IXITCP_ActionVersion = null;
	public Short IXITCP_NotificationActionVersion = null;
	public Short IXITCP_DialogVersion = null;
	public Short IXITCP_ListPropertyVersion = null;
	public Short IXITCP_HTTPControlVersion = null;
	public Integer IXITCF_ConfigVersion = null;
	public String IXITCF_Passcode = null;
	public Short IXITAU_StreamVersion = null;
	public String IXITAU_TestObjectPath = null;
	public Short IXITAU_PortVersion = null;
	public Short IXITAU_AudioSinkVersion = null;
	public Integer IXITAU_timeNanos = null;
	public Short IXITAU_AudioSourceVersion = null;
	public Short IXITAU_ImageSinkVersion = null;
	public Short IXITAU_ImageSourceVersion = null;
	public Short IXITAU_MetadataSinkVersion = null;
	public Short IXITAU_MetadataSourceVersion = null;
	public Short IXITAU_ControlVolumeVersion = null;
	public String IXITAU_delta = null;
	public String IXITAU_change = null;
	public Integer IXITAU_ClockVersion = null;
	public String IXITAU_adjustNanos = null;
	public Integer IXITL_LampServiceVersion = null;
	public Integer IXITL_LampParametersVersion = null;
	public Integer IXITL_LampDetailsVersion = null;
	public Integer IXITL_LampStateVersion = null;
	public Integer IXITT_ClockVersion = null;
	public Integer IXITT_TimeAuthorityVersion = null;
	public Integer IXITT_AlarmFactoryVersion = null;
	public Integer IXITT_AlarmVersion = null;
	public Integer IXITT_TimerFactoryVersion = null;
	public Integer IXITT_TimerVersion = null;
	public Integer IXITG_AppMgmtVersion = null;
	public Integer IXITG_CtrlAppVersion = null;
	public Integer IXITG_CtrlAccessVersion = null;
	public Integer IXITG_CtrlAclVersion = null;
	public Integer IXITG_ConnAppVersion = null;
	public Integer IXITSH_CentralizedManagementVersion = null;
	public String IXITSH_WellKnownName = null;
	public String IXITSH_UniqueName = null;
	public String IXITSH_DeviceId = null;
	public Integer IXITSH_HeartBeatInterval = null;
	public Integer IXITLC_ControllerServiceVersion = null;
	public Integer IXITLC_ControllerServiceLampVersion = null;
	public Integer IXITLC_ControllerServiceLampGroupVersion = null;
	public Integer IXITLC_ControllerServicePresetVersion = null;
	public Integer IXITLC_ControllerServiceSceneVersion = null;
	public Integer IXITLC_ControllerServiceMasterSceneVersion = null;
	public Integer IXITLC_LeaderElectionAndStateSyncVersion = null;
	
	public Ixit(Document xmlDocument)
	{
		this.xmlDocument = xmlDocument;
		
		loadIxitValuesFromXml();
	}
	
	public void printIxitValues(String serviceFramework)
	{
		for (Field field : this.getClass().getFields())
		{
			try
			{
				if (field.getName().contains("IXITCO_") || (field.getName().contains(serviceFramework+"_")))
				{
					System.out.println(field.getName() + " : " + field.get(this));
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
	
	private void loadIxitValuesFromXml()
	{
		NodeList ixit = xmlDocument.getElementsByTagName("Ixit");
			
		for (int i = 0; i < ixit.getLength(); i++)
		{
			Node node = ixit.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String ixitName = getValue("Name", element);
				String ixitValue = getValue("Value", element);
				saveIxitIntoVariable(ixitName, ixitValue);
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
	
	private void saveIxitIntoVariable(String ixitName, String ixitValue)
	{
		try
		{
			Field field = this.getClass().getField(ixitName);
			
			if (field != null)
			{
				if (field.getType().equals(Integer.class))
				{
					this.getClass().getField(ixitName).set(this, Integer.parseInt(ixitValue));
				}
				else if (field.getType().equals(Short.class))
				{
					this.getClass().getField(ixitName).set(this, Short.parseShort(ixitValue));
				}
				else if (field.getType().equals(UUID.class))
				{
					this.getClass().getField(ixitName).set(this, UUID.fromString(ixitValue));
				}
				else
				{
					this.getClass().getField(ixitName).set(this, ixitValue);
				}
			}
			else
			{
				logger.warn("%s is not defined in Test Cases Package", ixitName);
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
	
	public String get(String ixitName)
	{
		String field = null;
		try
		{
			field = (String) this.getClass().getField(ixitName).get(this);
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
		
		return field;
	}
}
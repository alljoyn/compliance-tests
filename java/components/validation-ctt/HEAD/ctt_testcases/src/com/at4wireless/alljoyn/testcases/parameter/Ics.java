package com.at4wireless.alljoyn.testcases.parameter;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ics
{
	private Document xmlDocument;
	
	public boolean ICSCO_DateOfManufacture = false;
	public boolean ICSCO_HardwareVersion = false;
	public boolean ICSCO_SupportUrl = false;
	public boolean ICSCO_IconInterface = false;
	public boolean ICSCO_DeviceName = false;
	public boolean ICSN_NotificationServiceFramework = false;
	public boolean ICSN_NotificationInterface = false;
	public boolean ICSN_RichIconUrl = false;
	public boolean ICSN_RichAudioUrl = false;
	public boolean ICSN_RespObjectPath = false;
	public boolean ICSN_NotificationProducerInterface = false;
	public boolean ICSN_DismisserInterface = false;
	public boolean ICSN_NotificationConsumer = false;
	public boolean ICSON_OnboardingServiceFramework = false;
	public boolean ICSON_OnboardingInterface = false;
	public boolean ICSON_ChannelSwitching = false;
	public boolean ICSON_GetScanInfoMethod = false;	
	public boolean ICSCP_ControlPanelServiceFramework = false;
	public boolean ICSCP_ControlPanelInterface = false;
	public boolean ICSCP_ContainerInterface = false;
	public boolean ICSCP_SecuredContainerInterface = false;
	public boolean ICSCP_PropertyInterface = false;
	public boolean ICSCP_SecuredPropertyInterface = false;
	public boolean ICSCP_LabelPropertyInterface = false;
	public boolean ICSCP_ActionInterface = false;
	public boolean ICSCP_SecuredActionInterface = false;
	public boolean ICSCP_NotificationActionInterface = false;
	public boolean ICSCP_DialogInterface = false;
	public boolean ICSCP_DI_Action2 = false;
	public boolean ICSCP_DI_Action3 = false;
	public boolean ICSCP_SecuredDialogInterface = false;
	public boolean ICSCP_SDI_Action2 = false;
	public boolean ICSCP_SDI_Action3 = false;
	public boolean ICSCP_ListPropertyInterface = false;
	public boolean ICSCP_SecuredListPropertyInterface = false;
	public boolean ICSCP_HTTPControlInterface = false;
	public boolean ICSCF_ConfigurationServiceFramework = false;
	public boolean ICSCF_ConfigurationInterface = false;
	public boolean ICSCF_FactoryResetMethod = false;
	public boolean ICSAU_AudioServiceFramework = false;
	public boolean ICSAU_StreamInterface = false;
	public boolean ICSAU_StreamPortInterface = false;
	public boolean ICSAU_PortAudioSinkInterface = false;
	public boolean ICSAU_PortAudioSourceInterface = false;
	public boolean ICSAU_PortImageSinkInterface = false;
	public boolean ICSAU_PortImageSourceInterface = false;
	public boolean ICSAU_PortApplicationMetadataSinkInterface = false;
	public boolean ICSAU_PortApplicationMetadataSourceInterface = false;
	public boolean ICSAU_ControlVolumeInterface = false;
	public boolean ICSAU_StreamClockInterface = false;
	public boolean ICSAU_AudioXalac = false;
	public boolean ICSAU_ImageJpeg = false;
	public boolean ICSAU_ApplicationXmetadata = false;
	public boolean ICSAU_VolumeControlEnabled = false;
	public boolean ICSL_LightingServiceFramework = false;
	public boolean ICSL_LampServiceInterface = false;
	public boolean ICSL_LampParametersInterface = false;
	public boolean ICSL_LampDetailsInterface = false;
	public boolean ICSL_Dimmable = false;
	public boolean ICSL_Color = false;
	public boolean ICSL_ColorTemperature = false;
	public boolean ICSL_Effects = false;
	public boolean ICSL_LampStateInterface = false;
	public boolean ICST_TimeServiceFramework = false;
	public boolean ICST_ClockInterface = false;
	public boolean ICST_Date = false;
	public boolean ICST_Milliseconds = false;
	public boolean ICST_TimeAuthorityInterface = false;
	public boolean ICST_AlarmFactoryInterface = false;
	public boolean ICST_AlarmInterface = false;
	public boolean ICST_TimerFactoryInterface = false;
	public boolean ICST_TimerInterface = false;
	public boolean ICSG_GatewayServiceFramework = false;
	public boolean ICSG_ProfileManagementInterface = false;
	public boolean ICSG_AppAccessInterface = false;
	public boolean ICSG_AppManagementInterface = false;
	public boolean ICSSH_SmartHomeServiceFramework = false;
	public boolean ICSSH_CentralizedManagementInterface = false;
	public boolean ICSLC_LightingControllerServiceFramework = false;
	public boolean ICSLC_ControllerServiceInterface = false;
	public boolean ICSLC_ControllerServiceLampInterface = false;
	public boolean ICSLC_ControllerServiceLampGroupInterface = false;
	public boolean ICSLC_ControllerServicePresetInterface = false;
	public boolean ICSLC_ControllerServiceSceneInterface = false;
	public boolean ICSLC_ControllerServiceMasterSceneInterface = false;
	public boolean ICSLC_LeaderElectionAndStateSyncInterface = false;
	
	public Ics(Document xmlDocument)
	{
		this.xmlDocument = xmlDocument;
		
		loadIcsValuesFromXml();
	}
	
	public void printIcsValues(String serviceFramework)
	{
		for (Field field : this.getClass().getFields())
		{
			try
			{
				if (field.getName().contains("ICSCO_") || (field.getName().contains(serviceFramework+"_")))
				{
					System.out.println(field.getName() + " : " + field.getBoolean(this));
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
	
	private void loadIcsValuesFromXml()
	{
		NodeList ics = xmlDocument.getElementsByTagName("Ics");
			
		for (int i = 0; i < ics.getLength(); i++)
		{
			Node node = ics.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String icsName = getValue("Name", element);
				String icsValue = getValue("Value", element);
				saveIcsIntoVariable(icsName, Boolean.parseBoolean(icsValue));
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
	
	private void saveIcsIntoVariable(String icsName, boolean icsValue)
	{
		try
		{
			if (icsValue)
			{
				this.getClass().getField(icsName).setBoolean(this, icsValue);
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
}
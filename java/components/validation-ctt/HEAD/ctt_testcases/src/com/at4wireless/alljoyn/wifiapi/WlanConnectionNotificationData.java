package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class WlanConnectionNotificationData extends Structure
{
	public static class ByReference extends WlanConnectionNotificationData implements Structure.ByReference 
    {
    	public ByReference(Pointer p)
    	{
    		super(p);
    	}
    }
	
	private static final int WLAN_MAX_NAME_LENGTH = 256;
	
	public int wlanConnectionMode;
	
	public char[] strProfileName;
	
	public Dot11Ssid dot11Ssid;
	
	public int dot11BssType;
	
	public boolean bSecurityEnabled;
	
	public int wlanReasonCode;
	
	public int dwFlags;
	
	public String strProfileXml;
	
	public WlanConnectionNotificationData()
	{
		strProfileName = new char[WLAN_MAX_NAME_LENGTH];
	}
	
	public WlanConnectionNotificationData(Pointer p)
	{
		super(p);
		strProfileName = new char[WLAN_MAX_NAME_LENGTH];
	}
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("wlanConnectionMode", "strProfileName", "dot11Ssid", "dot11BssType", "bSecurityEnabled",
				"wlanReasonCode", "dwFlags", "strProfileXml");
	}

}

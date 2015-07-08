package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.ULONG;

public class WlanAssociationAttributes extends Structure
{
	public Dot11Ssid dot11Ssid;
	
	public int dot11BssType;
	
	public char[] dot11Bssid;
	
	public int dot11PhyType;
	
	public ULONG uDot11PhyIndex;
	
	public long wlanSignalQuality;
	
	public ULONG ulRxRate;
	
	public ULONG ulTxRate;
	
	public WlanAssociationAttributes()
	{
		dot11Bssid = new char[6];
	}
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("dot11Ssid", "dot11BssType", "dot11Bssid", "dot11PhyType", 
				"uDot11PhyIndex", "wlanSignalQuality", "ulRxRate", "ulTxRate");
	}

}

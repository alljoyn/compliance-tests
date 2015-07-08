package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class WlanProfileInfo extends Structure
{
	public char[] strProfileName;
	
	public int dwFlags;
	
	public WlanProfileInfo()
	{
		strProfileName = new char[256];
	}
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("strProfileName", "dwFlags");
	}

}

package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class WlanSecurityAttributes extends Structure
{
	public boolean bSecurityEnabled;
	
	public boolean bOneXEnabled;
	
	public int dot11AuthAlgorithm;
	
	public int dot11CipherAlgorithm;
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("bSecurityEnabled", "bOneXEnabled", "dot11AuthAlgorithm", "dot11CipherAlgorithm");
	}
	
}

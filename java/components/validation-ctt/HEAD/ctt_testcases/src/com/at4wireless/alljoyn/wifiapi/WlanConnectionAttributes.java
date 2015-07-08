package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class WlanConnectionAttributes extends Structure
{
	public static class ByReference extends WlanConnectionAttributes implements Structure.ByReference 
    {
		public ByReference()
		{
			
		}
		
		public ByReference(Pointer p)
		{
			super(p);
		}
    }
	
	public WlanConnectionAttributes()
	{
		strProfileName = new char[256];
	}
	
	public WlanConnectionAttributes(Pointer p)
	{
		super(p);
		strProfileName = new char[256];
		read();
	}
	
	public int isState;
	
	public int wlanConnectionMode;
	
	public char[] strProfileName;
	
	public WlanAssociationAttributes wlanAssociationAttributes;
	
	public WlanSecurityAttributes wlanSecurityAttributes;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("isState", "wlanConnectionMode", "strProfileName", "wlanAssociationAttributes", "wlanSecurityAttributes");
	}
}

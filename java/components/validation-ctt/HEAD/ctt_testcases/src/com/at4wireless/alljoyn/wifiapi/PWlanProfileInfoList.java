package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class PWlanProfileInfoList extends Structure
{
	public static class ByReference extends PWlanProfileInfoList implements Structure.ByReference 
    {
    	
    }
	
	public WlanProfileInfoList.ByReference WlanProfileInfoList;
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("WlanProfileInfoList");
	}
}

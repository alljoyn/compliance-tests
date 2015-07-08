package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.ptr.PointerByReference;

/**
 * Contains information provided when receiving notifications
 */
public class WlanNotificationData extends Structure
{
	public static class ByReference extends WlanNotificationData implements Structure.ByReference 
    {
    	
    }
	
	/**
	 * TODO
	 */
	public int NotificationSource;
	
	/**
	 * TODO
	 */
	public int NotificationCode;
	
	/**
	 * TODO
	 */
	public Guid.GUID InterfaceGuid;
	
	/**
	 * TODO
	 */
	public int dwDataSize;
	
	/**
	 * TODO
	 */
	public PointerByReference pData;

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("NotificationSource", "NotificationCode", "InterfaceGuid", "dwDataSize", "pData");
	}
}

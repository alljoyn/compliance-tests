/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
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
/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.wifiapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * Contains a list of basic service set (BSS) identifiers.
 */
public class Dot11BssidList extends Structure
{
	public static class ByReference extends Dot11BssidList implements Structure.ByReference 
    {
    	
    }
		
	/**
	 * An NDIS_OBJECT_HEADER structure that contains the type, version, and size information of an NDIS structure. For most DOT11_BSSID_LIST structures, set
	 * the Type member to NDIS_OBJECT_TYPE_DEFAULT, set the Revision member to DOT11_BSSID_LIST_REVISION_1, and set the Size member to
	 * sizeof(DOT11_BSSID_LIST).
	 */
	public NdisObjectHeader Header;
	
	/**
	 * The number of entries in this structure.
	 */
	public long uNumOfEntries;
	
	/**
	 * The total number of entries supported.
	 */
	public long uTotalNumOfEntries;
	
	/**
	 * A list of BSS identifiers. A BSS identifier is stored as a DOT11_MAC_ADDRESS type.
	 */
	public char[][] BSSIDs;
	
	/*public Dot11BssidList()
	{
		BSSIDs = new char[1][6];
	}*/

	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("Header", "uNumOfEntries", "uTotalNumOfEntries", "BSSIDs");
	}

}
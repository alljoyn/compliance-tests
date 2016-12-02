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
 * Packages the object type, version, and size information that is required in many NDIS 6.0 structures.
 */
public class NdisObjectHeader extends Structure
{
	/**
	 * Specifies the type of NDIS Object that a structure describes.
	 */
	public char Type;
	
	/**
	 * Specifies the revision number of this structure.
	 */
	public char Revision;
	
	/**
	 * Specifies the total size, in bytes, of the NDIS structure that contains the NDIS_OBJECT_HEADER. This size includes the size of the NDIS_OBJECT_HEADER
	 * member and all other members of the structure.
	 */
	public short Size;
	
	@Override
	protected List<String> getFieldOrder()
	{
		return Arrays.asList("Type", "Revision", "Size");
	}
}
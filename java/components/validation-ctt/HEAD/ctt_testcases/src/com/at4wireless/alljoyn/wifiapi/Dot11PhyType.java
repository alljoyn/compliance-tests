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

public enum Dot11PhyType
{
	UNKNOWN,
	ANY,
	FHSS,
	DSSS,
	IRBASEBAND,
	OFDM,
	HRDSSS,
	ERP,
	HT,
	VHT,
	IHV_start, //0x80000000
	IHV_end //0xffffffff
}
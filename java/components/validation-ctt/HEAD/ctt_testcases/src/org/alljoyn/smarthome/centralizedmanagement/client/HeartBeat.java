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
package org.alljoyn.smarthome.centralizedmanagement.client;

import java.util.Timer;

public class HeartBeat
{
	SmartHomeClient smartHomeClient;
	String deviceId;
	
	public HeartBeat(SmartHomeClient smartHomeClient, String deviceId)
	{
		this.smartHomeClient = smartHomeClient;
		this.deviceId = deviceId;
	}

	public void deviceHeartBeat()
	{
		HeartBeatTask heartBeatTask = new HeartBeatTask(this.smartHomeClient, this.deviceId);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(heartBeatTask, 0, 10000);
	}
}
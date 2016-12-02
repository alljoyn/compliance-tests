/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package org.alljoyn.smarthome.centralizedmanagement.client;

import java.util.TimerTask;

// TODO: Auto-generated Javadoc
/**
 * 	This class used to execute one heartbeat task
 */

public class HeartBeatTask extends TimerTask {
	
	/** The smart home client. */
	SmartHomeClient smartHomeClient;
	
	/** The device id. */
	String deviceId;
	
	/**
	 * Instantiates a new heart beat task.
	 *
	 * @param smartHomeClient the smart home client
	 * @param deviceId the device id
	 */
	public HeartBeatTask(SmartHomeClient smartHomeClient, String deviceId) {
		this.smartHomeClient = smartHomeClient;
		this.deviceId = deviceId;
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		this.smartHomeClient.DeviceHeartBeat(this.deviceId);
	}

}
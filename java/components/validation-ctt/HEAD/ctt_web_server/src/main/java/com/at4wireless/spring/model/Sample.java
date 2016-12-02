/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/

package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sample")
public class Sample
{	
	public Sample() {}
	
	public Sample(Dut d)
	{
		this.deviceId = d.getDeviceId();
		this.appId = d.getAppId();
		this.hwVer = d.getHwVer();
		this.swVer = d.getSwVer();
		this.description = d.getDescription();
		this.associatedDut = d.getIdDut();
	}
	
	@Id @GeneratedValue
	@Column(name = "id_sample", nullable = false)
	private int idSample;
	public int getIdSample() { return idSample; }
	public void setIdSample(int idSample) { this.idSample = idSample; }
	
	@Column(name = "device_id", nullable = false)
	private String deviceId;
	public String getDeviceId() { return deviceId; }
	public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
	
	@Column(name = "app_id", nullable = false)
	private String appId;
	public String getAppId() { return appId; }
	public void setAppId(String appId) { this.appId = appId; }
	
	@Column(name = "hw_ver", nullable = false)
	private String hwVer;
	public String getHwVer() { return hwVer; }
	public void setHwVer(String hwVer) { this.hwVer = hwVer; }
	
	@Column(name = "sw_ver", nullable = false)
	private String swVer;
	public String getSwVer() { return swVer; }
	public void setSwVer(String swVer) { this.swVer = swVer; }
	
	@Column(name = "enabled", nullable = false)
	private boolean enabled;
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	@Column(name = "associated_dut", nullable = false)
	private int associatedDut;
	public int getAssociatedDut() { return associatedDut; }
	public void setAssociatedDut(int associatedDut) { this.associatedDut = associatedDut; }
}
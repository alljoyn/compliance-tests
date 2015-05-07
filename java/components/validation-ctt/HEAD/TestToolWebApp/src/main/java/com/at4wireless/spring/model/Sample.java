package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sample")
public class Sample {
	
	@Id @GeneratedValue
	@Column(name="id_sample")
	private int idSample;
	
	@Column(name="device_id")
	private String deviceId;
	
	@Column(name="app_id")
	private String appId;
	
	@Column(name="hw_ver")
	private String hwVer;
	
	@Column(name="sw_ver")
	private String swVer;
	
	@Column(name="enabled")
	private boolean enabled;
	
	@Column(name="description")
	private String description;
	
	@Column(name="associated_dut")
	private int associatedDut;
	
	public Sample() {
		
	}
	
	public Sample(Dut d) {
		this.deviceId = d.getDeviceId();
		this.appId = d.getAppId();
		this.hwVer = d.getHwVer();
		this.swVer = d.getSwVer();
		this.description = d.getDescription();
		this.associatedDut = d.getIdDut();
	}
	
	public int getAssociatedDut() {
		return associatedDut;
	}
	public void setAssociatedDut(int associatedDut) {
		this.associatedDut = associatedDut;
	}
	public int getIdSample() {
		return idSample;
	}
	public void setIdSample(int idSample) {
		this.idSample = idSample;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getHwVer() {
		return hwVer;
	}
	public void setHwVer(String hwVer) {
		this.hwVer = hwVer;
	}
	public String getSwVer() {
		return swVer;
	}
	public void setSwVer(String swVer) {
		this.swVer = swVer;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

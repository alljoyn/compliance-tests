package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="dut")
public class Dut {
	
	@Id @GeneratedValue
	@Column(name="id_dut")
	private int idDut;
	
	@NotEmpty
	@Length(min=1, max=255)
	@Column(name="name")
	private String name;
	
	@Column(name="user")
	private String user;
	
	@Column(name="created_date")
	private java.sql.Timestamp createdDate;
	
	@Column(name="modified_date")
	private java.sql.Timestamp modifiedDate;
	
	@NotEmpty
	@Length(min=1, max=60)
	@Column(name="manufacturer")
	private String manufacturer;
	
	@NotEmpty
	@Length(min=1, max=60)
	@Column(name="model")
	private String model;
	
	@Transient
	private String deviceId;
	
	@Transient
	private String appId;
	
	@Transient
	private String swVer;
	
	@Transient
	private String hwVer;
	
	@NotEmpty
	@Length(min=1, max=255)
	@Column(name="description")
	private String description;
	
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
	public int getIdDut() {
		return idDut;
	}
	public void setIdDut(int idDut) {
		this.idDut = idDut;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSwVer() {
		return swVer;
	}
	public void setSwVer(String swVer) {
		this.swVer = swVer;
	}
	public String getHwVer() {
		return hwVer;
	}
	public void setHwVer(String hwVer) {
		this.hwVer = hwVer;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}

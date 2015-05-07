package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ixit")
public class Ixit {
	
	@Id @GeneratedValue
	@Column(name="id_ixit")
	private int idIxit;
	
	@Column(name="name")
	private String name;
	
	@Column(name="value")
	private String value;
	
	@Column(name="service_group")
	private int serviceGroup;
	
	@Column(name="description")
	private String description;
	
	public int getIdIxit() {
		return idIxit;
	}
	public void setIdIxit(int idIxit) {
		this.idIxit = idIxit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getServiceGroup() {
		return serviceGroup;
	}
	public void setServiceGroup(int serviceGroup) {
		this.serviceGroup = serviceGroup;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

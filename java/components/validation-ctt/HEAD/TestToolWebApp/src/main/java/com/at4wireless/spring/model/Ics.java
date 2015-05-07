package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ics")
public class Ics {
	
	@Id @GeneratedValue
	@Column(name="id_ics")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="value")
	private boolean value;
	
	@Column(name="scr_expression")
	private String scrExpression;
	
	@Column(name="service_group")
	private int serviceGroup;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public int getServiceGroup() {
		return serviceGroup;
	}
	public void setServiceGroup(int service_group) {
		this.serviceGroup = service_group;
	}
	public String getScrExpression() {
		return scrExpression;
	}
	public void setScrExpression(String scrExpression) {
		this.scrExpression = scrExpression;
	}

}

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

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@NamedQueries({
	@NamedQuery(
		name = "select_all_ixit",
		query = "from Ixit"
		),
	@NamedQuery(
		name = "select_ixit_by_service_group",
		query = "from Ixit where serviceGroup = :serviceGroup"
		),
	@NamedQuery(
		name = "select_ixit_by_name",
		query = "from Ixit where name = :name"
		)
})
@Entity
@Table(name = "ixit")
public class Ixit
{	
	@Id @GeneratedValue
	@Column(name = "id_ixit", nullable = false)
	private int idIxit;
	public int getIdIxit() { return idIxit; }
	public void setIdIxit(int idIxit) { this.idIxit = idIxit; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "value", nullable = false)
	private String value;
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
	
	@Column(name = "service_group", nullable = false)
	private int serviceGroup;
	public int getServiceGroup() { return serviceGroup; }
	public void setServiceGroup(int serviceGroup) { this.serviceGroup = serviceGroup; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
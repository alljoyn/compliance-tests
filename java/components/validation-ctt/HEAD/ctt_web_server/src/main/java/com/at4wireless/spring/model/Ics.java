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
		name = "select_all_ics",
		query = "from Ics"
		),
	@NamedQuery(
		name = "select_ics_by_service_group",
		query = "from Ics where serviceGroup = :serviceGroup"
		),
	@NamedQuery(
		name = "select_ics_by_name",
		query = "from Ics where name = :name"
		)
})
@Entity
@Table(name = "ics")
public class Ics
{	
	@Id @GeneratedValue
	@Column(name = "id_ics", nullable = false)
	private int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "value", nullable = false)
	private boolean value;
	public boolean isValue() { return value; }
	public void setValue(boolean value) { this.value = value; }
	
	@Column(name = "service_group", nullable = false)
	private int serviceGroup;
	public int getServiceGroup() { return serviceGroup; }
	public void setServiceGroup(int service_group) { this.serviceGroup = service_group; }
	
	@Column(name = "scr_expression")
	private String scrExpression;
	public String getScrExpression() { return scrExpression; }
	public void setScrExpression(String scrExpression) { this.scrExpression = scrExpression; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
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

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "testcases")
public class TestCase
{
	@Id @GeneratedValue
	@Column(name = "id_test", nullable = false)
	private BigInteger idTC;
	public BigInteger getIdTC() { return idTC; }
	public void setIdTC(BigInteger idTC) { this.idTC = idTC; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "type", nullable = false)
	private String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	@Column(name = "applicability", nullable = false)
	private String applicability;
	public String getApplicability() { return applicability; }
	public void setApplicability(String applicability) { this.applicability = applicability; }
	
	@Column(name = "service_group", nullable = false)
	private BigInteger serviceGroup;
	public BigInteger getServiceGroup() { return serviceGroup; }
	public void setServiceGroup(BigInteger serviceGroup) { this.serviceGroup = serviceGroup; }
	
	@Column(name = "last_id_project")
	private BigInteger lastIDproject;
	public BigInteger getLastIDproject() { return lastIDproject; }
	public void setLastIDproject(BigInteger lastIDproject) { this.lastIDproject = lastIDproject; }
	
	@Column(name = "last_execution")
	private String lastExecution;
	public String getLastExecution() { return lastExecution; }
	public void setLastExecution(String lastExecution) { this.lastExecution = lastExecution; }
	
	@Column(name = "last_result")
	private String lastResult;
	public String getLastResult() { return lastResult; }
	public void setLastResult(String lastResult) { this.lastResult = lastResult; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	@Transient
	private String supportedCrs;
	public String getSupportedCrs() { return supportedCrs; }
	public void setSupportedCrs(String supportedCrs) { this.supportedCrs = supportedCrs; }
}
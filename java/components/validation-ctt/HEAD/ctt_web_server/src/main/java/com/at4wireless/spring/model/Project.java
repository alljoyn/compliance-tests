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
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "project")
public class Project
{	
	@Id @GeneratedValue
	@Column(name = "id_project", nullable = false)
	private int idProject;
	public int getIdProject() { return idProject; }
	public void setIdProject(int idProject) { this.idProject = idProject; }
	
	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "user", nullable = false)
	private String user;
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
	
	@Column(name = "created_date", nullable = false, updatable = false)
	private java.sql.Timestamp createdDate;
	public java.sql.Timestamp getCreatedDate() { return createdDate; }
	public void setCreatedDate(java.sql.Timestamp createdDate) { this.createdDate = createdDate; }
	
	@Column(name = "modified_date", nullable = false)
	private java.sql.Timestamp modifiedDate;
	public java.sql.Timestamp getModifiedDate() { return modifiedDate; }
	public void setModifiedDate(java.sql.Timestamp modifiedDate) { this.modifiedDate = modifiedDate; }
	
	@Column(name = "type", nullable = false)
	private String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	@Column(name = "id_certrel", nullable = false)
	private int idCertrel;
	public int getIdCertrel() { return idCertrel; }
	public void setIdCertrel(int idCertrel) { this.idCertrel = idCertrel; }
	
	@Column(name = "id_tccl")
	private int idTccl;
	public int getIdTccl() { return idTccl; }
	public void setIdTccl(int idTccl) { this.idTccl = idTccl; }
	
	@Column(name = "car_id")
	private String carId;
	public String getCarId() { return carId; }
	public void setCarId(String carId) { this.carId = carId; }
	
	@Column(name = "is_configured", nullable = false)
	private boolean isConfigured;
	public boolean isIsConfigured() { return isConfigured; }
	public void setIsConfigured(boolean isConfigured) { this.isConfigured = isConfigured; }
	
	@Column(name = "configuration")
	private String configuration;
	public String getConfiguration() { return configuration; }
	public void setConfiguration(String configuration) { this.configuration = configuration; }
	
	@Column(name = "id_dut")
	private int idDut;
	public int getIdDut() { return idDut; }
	public void setIdDut(int idDut) { this.idDut = idDut; }
	
	@Column(name = "has_results", nullable = false)
	private boolean hasResults;
	public boolean isHasResults() { return hasResults; }
	public void setHasResults(boolean hasResults) { this.hasResults = hasResults; }
	
	@Column(name = "results")
	private String results;
	public String getResults() { return results; }
	public void setResults(String results) { this.results = results; }
	
	@Column(name = "has_testreport", nullable = false)
	private boolean hasTestReport;
	public boolean isHasTestReport() { return hasTestReport; }
	public void setHasTestReport(boolean hasTestReport) { this.hasTestReport = hasTestReport; }
	
	@Column(name="test_report")
	private String testReport;
	public String getTestReport() { return testReport; }
	public void setTestReport(String testReport) { this.testReport = testReport; }
	
	@Transient
	private String supportedServices;
	public String getSupportedServices() { return supportedServices; }
	public void setSupportedServices(String supportedServices) { this.supportedServices = supportedServices; }
	
	@Transient
	private String gUnits;
	public String getgUnits() { return gUnits; }
	public void setgUnits(String gUnits) { this.gUnits = gUnits; }
}
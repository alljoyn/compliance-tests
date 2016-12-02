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


public class RestProject
{
	private int idProject;
	public int getIdProject() { return idProject; }
	public void setIdProject(int idProject) { this.idProject = idProject; }
	
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private java.sql.Timestamp createdDate;
	public java.sql.Timestamp getCreatedDate() { return createdDate; }
	public void setCreatedDate(java.sql.Timestamp createdDate) { this.createdDate = createdDate; }
	
	private java.sql.Timestamp modifiedDate;
	public java.sql.Timestamp getModifiedDate() { return modifiedDate; }
	public void setModifiedDate(java.sql.Timestamp modifiedDate) { this.modifiedDate = modifiedDate; }
	
	private String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	private String supportedServices;
	public String getSupportedServices() { return supportedServices; }
	public void setSupportedServices(String supportedServices) { this.supportedServices = supportedServices; }
	
	private String certRel;
	public String getCertRel() { return certRel; }
	public void setCertRel(String certRel) { this.certRel = certRel; }
	
	private String tccl;
	public String getTccl() { return tccl; }
	public void setTccl(String tccl) { this.tccl = tccl; }
	
	private boolean isConfigured;
	public boolean isIsConfigured() { return isConfigured; }
	public void setIsConfigured(boolean isConfigured) { this.isConfigured = isConfigured; }
	
	private boolean hasResults;
	public boolean isHasResults() { return hasResults; }
	public void setHasResults(boolean hasResults) { this.hasResults = hasResults; }
	
	private String dut;
	public String getDut() { return dut; }
	public void setDut(String dut) { this.dut = dut; }
	
	private String golden;
	public String getGolden() { return golden; }
	public void setGolden(String golden) { this.golden = golden; }
}
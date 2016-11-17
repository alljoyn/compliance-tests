/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
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
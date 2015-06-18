/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.model;


public class RestProject {
	private int idProject;
	private String name;
	private java.sql.Timestamp createdDate;
	private java.sql.Timestamp modifiedDate;
	private String type;
	private String supportedServices;
	private String certRel;
	private String tccl;
	private boolean isConfigured;
	private boolean hasResults;
	private String dut;
	private String golden;
	
	
	public boolean isHasResults() {
		return hasResults;
	}
	public void setHasResults(boolean hasResults) {
		this.hasResults = hasResults;
	}
	public int getIdProject() {
		return idProject;
	}
	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSupportedServices() {
		return supportedServices;
	}
	public void setSupportedServices(String supportedServices) {
		this.supportedServices = supportedServices;
	}
	public String getCertRel() {
		return certRel;
	}
	public void setCertRel(String certRel) {
		this.certRel = certRel;
	}
	public String getTccl() {
		return tccl;
	}
	public void setTccl(String tccl) {
		this.tccl = tccl;
	}
	public boolean isIsConfigured() {
		return isConfigured;
	}
	public void setIsConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public String getGolden() {
		return golden;
	}
	public void setGolden(String golden) {
		this.golden = golden;
	}
	
	
}

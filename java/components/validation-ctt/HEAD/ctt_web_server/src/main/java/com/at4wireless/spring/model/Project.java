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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="project")
public class Project {
	
	@Id @GeneratedValue
	@Column(name="id_project")
	private int idProject;
	
	@NotEmpty
	@Column(name="name")
	private String name;
	
	@Column(name="user")
	private String user;
	
	@Column(name="created_date")
	private java.sql.Timestamp createdDate;
	
	@Column(name="modified_date")
	private java.sql.Timestamp modifiedDate;
	
	@Column(name="type")
	private String type;
	
	@Transient
	private String supportedServices;
	
	@Column(name="id_certrel")
	private int idCertrel;
	
	@Column(name="id_tccl")
	private int idTccl;
	
	@Column(name="car_id")
	private String carId;
	
	@Column(name="is_configured")
	private boolean isConfigured;
	
	@Column(name="configuration")
	private String configuration;
	
	@Column(name="id_dut")
	private int idDut;
	
	/*@Column(name="id_golden")
	private int idGolden;*/
	
	@Transient
	private String gUnits;
	
	@Column(name="has_results")
	private boolean hasResults;
	
	@Column(name="results")
	private String results;
	
	@Column(name="has_testreport")
	private boolean hasTestReport;
	
	@Column(name="test_report")
	private String testReport;
	
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public int getIdDut() {
		return idDut;
	}
	public void setIdDut(int idDut) {
		this.idDut = idDut;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
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
	public boolean isIsConfigured() {
		return isConfigured;
	}
	public void setIsConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	public boolean isHasResults() {
		return hasResults;
	}
	public void setHasResults(boolean hasResults) {
		this.hasResults = hasResults;
	}
	public String getResults() {
		return results;
	}
	public void setResults(String results) {
		this.results = results;
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
	public String getSupportedServices() {
		return supportedServices;
	}
	public void setSupportedServices(String supportedServices) {
		this.supportedServices = supportedServices;
	}
	public int getIdCertrel() {
		return idCertrel;
	}
	public void setIdCertrel(int idCertrel) {
		this.idCertrel = idCertrel;
	}
	public int getIdTccl() {
		return idTccl;
	}
	public void setIdTccl(int idTccl) {
		this.idTccl = idTccl;
	}
	/*public int getIdGolden() {
		return idGolden;
	}
	public void setIdGolden(int idGolden) {
		this.idGolden = idGolden;
	}*/
	public boolean isHasTestReport() {
		return hasTestReport;
	}
	public void setHasTestReport(boolean hasTestReport) {
		this.hasTestReport = hasTestReport;
	}
	public String getTestReport() {
		return testReport;
	}
	public void setTestReport(String testReport) {
		this.testReport = testReport;
	}
	
	public String getgUnits() {
		return gUnits;
	}
	public void setgUnits(String gUnits) {
		this.gUnits = gUnits;
	}
	public String toString() {
		return (idProject+" : "+name+" : "+modifiedDate+" : "+idCertrel+" : "+type+" : "
				+idDut);
	}
}

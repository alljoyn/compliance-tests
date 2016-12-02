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

@Entity
@Table(name = "tccl")
public class Tccl
{	
	@Id @GeneratedValue
	@Column(name = "id_tccl", nullable = false)
	private int idTccl;
	public int getIdTccl() { return idTccl; }
	public void setIdTccl(int idTccl) { this.idTccl = idTccl; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "created_date", nullable = false, updatable = false)
	private java.sql.Timestamp createdDate;
	public java.sql.Timestamp getCreatedDate() { return createdDate; }
	public void setCreatedDate(java.sql.Timestamp createdDate) { this.createdDate = createdDate; }
	
	@Column(name = "modified_date", nullable = false)
	private java.sql.Timestamp modifiedDate;
	public java.sql.Timestamp getModifiedDate() { return modifiedDate; }
	public void setModifiedDate(java.sql.Timestamp modifiedDate) { this.modifiedDate = modifiedDate; }
	
	@Column(name = "id_certrel", nullable = false)
	private int idCertrel;
	public int getIdCertrel() { return idCertrel; }
	public void setIdCertrel(int idCertrel) { this.idCertrel = idCertrel; }
	
	@Column(name = "num_tc", nullable = false)
	private int numTc;
	public int getNumTc() { return numTc; }
	public void setNumTc(int numTc) { this.numTc = numTc; }
	
	@Transient
	private String nameCertrel;
	public String getNameCertrel() { return nameCertrel; }
	public void setNameCertrel(String nameCertrel) { this.nameCertrel = nameCertrel; }
}
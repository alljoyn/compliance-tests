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

@Entity
@Table(name="certrel")
public class CertificationRelease
{
	@Id @GeneratedValue
	@Column(name = "id_certrel", nullable = false)
	private int idCertrel;
	public int getIdCertrel() { return idCertrel; }
	public void setIdCertrel(int idCertrel) { this.idCertrel = idCertrel; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "enabled", nullable = false)
	private boolean enabled;
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	@Column(name = "isRelease", nullable = false)
	private boolean release;
	public boolean isRelease() { return release; }
	public void setRelease(boolean release) { this.release = release; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
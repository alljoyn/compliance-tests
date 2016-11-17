/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "dut")
public class Dut
{
	@Id @GeneratedValue
	@Column(name = "id_dut", nullable = false)
	private int idDut;
	public int getIdDut() { return idDut; }
	public void setIdDut(int idDut) { this.idDut = idDut; }
	
	@NotEmpty
	@Length(min=1, max=255)
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
	
	@NotEmpty
	@Length(min=1, max=60)
	@Column(name = "manufacturer", nullable = false)
	private String manufacturer;
	public String getManufacturer() { return manufacturer; }
	public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
	
	@NotEmpty
	@Length(min=1, max=60)
	@Column(name = "model", nullable = false)
	private String model;
	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }
	
	@Length(max=255)
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	@Transient
	private String deviceId;
	public String getDeviceId() { return deviceId; }
	public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
	
	@Transient
	private String appId;
	public String getAppId() { return appId; }
	public void setAppId(String appId) { this.appId = appId; }
	
	@Transient
	private String swVer;
	public String getSwVer() { return swVer; }
	public void setSwVer(String swVer) { this.swVer = swVer; }
	
	@Transient
	private String hwVer;
	public String getHwVer() { return hwVer; }
	public void setHwVer(String hwVer) { this.hwVer = hwVer; }
}
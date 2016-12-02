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

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "golden")
public class GoldenUnit
{
	@Id @GeneratedValue
	@Column(name="id_golden")
	private BigInteger idGolden;
	
	@NotEmpty
	@Column(name="name")
	private String name;
	
	@Column(name="user")
	private String user;
	
	@Column(name="created_date")
	private java.sql.Timestamp createdDate;
	
	@Column(name="modified_date")
	private java.sql.Timestamp modifiedDate;
	
	/*@Transient
	private String supportedCategories;*/
	
	@Column(name="id_category")
	private int category;
	
	@Column(name="manufacturer")
	private String manufacturer;
	
	@Column(name="model")
	private String model;
	
	@Column(name="sw_ver")
	private String swVer;
	
	@Column(name="hw_ver")
	private String hwVer;
	
	@Column(name="description")
	private String description;
	
	public BigInteger getIdGolden()
	{
		return idGolden;
	}
	public void setIdGolden(BigInteger idGolden)
	{
		this.idGolden = idGolden;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getUser()
	{
		return user;
	}
	public void setUser(String user)
	{
		this.user = user;
	}
	public java.sql.Timestamp getCreatedDate()
	{
		return createdDate;
	}
	public void setCreatedDate(java.sql.Timestamp createdDate)
	{
		this.createdDate = createdDate;
	}
	public java.sql.Timestamp getModifiedDate()
	{
		return modifiedDate;
	}
	public void setModifiedDate(java.sql.Timestamp modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}
	public int getCategory()
	{
		return category;
	}
	public void setCategory(int category)
	{
		this.category = category;
	}
	public String getManufacturer()
	{
		return manufacturer;
	}
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
	public String getModel()
	{
		return model;
	}
	public void setModel(String model)
	{
		this.model = model;
	}
	public String getSwVer()
	{
		return swVer;
	}
	public void setSwVer(String swVer)
	{
		this.swVer = swVer;
	}
	public String getHwVer()
	{
		return hwVer;
	}
	public void setHwVer(String hwVer)
	{
		this.hwVer = hwVer;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	/*public String getSupportedCategories()
	{
		return supportedCategories;
	}
	public void setSupportedCategories(String supportedCategories)
	{
		this.supportedCategories = supportedCategories;
	}*/
}
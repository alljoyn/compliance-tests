package com.at4wireless.spring.model.dto;

import java.sql.Timestamp;

import com.at4wireless.spring.model.GoldenUnit;

public class GoldenUnitDT
{
	private int id;
	private String name;
	private Timestamp created;
	private Timestamp modified;
	private String category;
	private String manufacturer;
	private String model;
	private String swVer;
	private String hwVer;
	private String description;
	
	public GoldenUnitDT(GoldenUnit gu, String category)
	{
		this.id = gu.getIdGolden();
		this.name = gu.getName();
		this.created = gu.getCreatedDate();
		this.modified = gu.getModifiedDate();
		this.category = category;
		this.manufacturer = gu.getManufacturer();
		this.model = gu.getModel();
		this.swVer = gu.getSwVer();
		this.hwVer = gu.getHwVer();
		this.description = gu.getDescription();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSwVer() {
		return swVer;
	}
	public void setSwVer(String swVer) {
		this.swVer = swVer;
	}
	public String getHwVer() {
		return hwVer;
	}
	public void setHwVer(String hwVer) {
		this.hwVer = hwVer;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}

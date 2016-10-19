package com.at4wireless.spring.model.dto;

import java.sql.Timestamp;

import com.at4wireless.spring.model.GoldenUnit;

public class GoldenUnitDT
{
	public GoldenUnitDT(GoldenUnit gu, String category)
	{
		this.id = gu.getIdGolden().intValue();
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
	
	private int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private Timestamp created;
	public Timestamp getCreated() { return created; }
	public void setCreated(Timestamp created) { this.created = created; }
	
	private Timestamp modified;
	public Timestamp getModified() { return modified; }
	public void setModified(Timestamp modified) { this.modified = modified; }
	
	private String category;
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	
	private String manufacturer;
	public String getManufacturer() { return manufacturer; }
	public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
	
	private String model;
	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }
	
	private String swVer;
	public String getSwVer() { return swVer; }
	public void setSwVer(String swVer) { this.swVer = swVer; }
	
	private String hwVer;
	public String getHwVer() { return hwVer; }
	public void setHwVer(String hwVer) { this.hwVer = hwVer; }
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
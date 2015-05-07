package com.at4wireless.spring.model;


public class TestCaseTccl {
	private int idTC;
	private String name;
	private String description;
	private String type;
	private boolean enabled;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIdTC() {
		return idTC;
	}
	public void setIdTC(int idTC) {
		this.idTC = idTC;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	
}

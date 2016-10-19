package com.at4wireless.spring.model.dto;

import java.sql.Timestamp;

import com.at4wireless.spring.model.Project;

public class ProjectDT
{
	public ProjectDT(Project project, String certificationRelease, String tccl, String dut)
	{
		this.id = project.getIdProject();
		this.name = project.getName();
		this.modified = project.getModifiedDate();
		this.type = project.getType();
		this.cr = certificationRelease;
		this.services = project.getSupportedServices();
		this.configured = project.isIsConfigured() == true ? "Yes" : "No";
		this.results = project.isHasResults();
		this.created = project.getCreatedDate();
		this.tccl = this.type.equals("Development") ? "Not needed" : tccl;
		this.dut = dut;
		this.gu = project.getgUnits();
		this.cri = project.getCarId() != null ? project.getCarId() : "Not needed";
	}
	
	private int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private Timestamp modified;
	public Timestamp getModified() { return modified; }
	public void setModified(Timestamp modified) { this.modified = modified; }
	
	private String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	private String cr;
	public String getCr() { return cr; }
	public void setCr(String cr) { this.cr = cr; }
	
	private String services;
	public String getServices() { return services; }
	public void setServices(String services) { this.services = services; }
	
	private String configured;
	public String getConfigured() { return configured; }
	public void setConfigured(String configured) { this.configured = configured; }
	
	private boolean results;
	public boolean isResults() { return results; }
	public void setResults(boolean results) { this.results = results; }
	
	private Timestamp created;
	public Timestamp getCreated() { return created; }
	public void setCreated(Timestamp created) { this.created = created; }
	
	private String tccl;
	public String getTccl() { return tccl; }
	public void setTccl(String tccl) { this.tccl = tccl; }
	
	private String dut;
	public String getDut() { return dut; }
	public void setDut(String dut) { this.dut = dut; }
	
	private String gu;
	public String getGu() { return gu; }
	public void setGu(String gu) { this.gu = gu; }
	
	private String cri;
	public String getCri() { return cri; }
	public void setCri(String cri) { this.cri = cri; }
}
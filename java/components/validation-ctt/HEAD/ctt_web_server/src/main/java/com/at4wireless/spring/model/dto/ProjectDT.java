package com.at4wireless.spring.model.dto;

import java.sql.Timestamp;

import com.at4wireless.spring.model.Project;

public class ProjectDT
{
	private int id;
	private String name;
	private Timestamp modified;
	private String type;
	private String cr;
	private String services;
	private String configured;
	private boolean results;
	private Timestamp created;
	private String tccl;
	private String dut;
	private String gu;
	private String cri;
	
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

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCr() {
		return cr;
	}

	public void setCr(String cr) {
		this.cr = cr;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getConfigured() {
		return configured;
	}

	public void setConfigured(String configured) {
		this.configured = configured;
	}

	public boolean isResults() {
		return results;
	}

	public void setResults(boolean results) {
		this.results = results;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getTccl() {
		return tccl;
	}

	public void setTccl(String tccl) {
		this.tccl = tccl;
	}

	public String getDut() {
		return dut;
	}

	public void setDut(String dut) {
		this.dut = dut;
	}

	public String getGu() {
		return gu;
	}

	public void setGu(String gu) {
		this.gu = gu;
	}

	public String getCri() {
		return cri;
	}

	public void setCri(String cri) {
		this.cri = cri;
	}
}
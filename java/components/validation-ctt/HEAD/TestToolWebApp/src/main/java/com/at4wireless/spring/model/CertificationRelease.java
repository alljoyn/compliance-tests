package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="certrel")
public class CertificationRelease {
	
	@Id @GeneratedValue
	@Column(name="id_certrel")
	private int idCertrel;
	
	@Column(name="name")
	private String name;
	
	@Column(name="enabled")
	private boolean enabled;
	
	public int getIdCertrel() {
		return idCertrel;
	}
	public void setIdCertrel(int idCertrel) {
		this.idCertrel = idCertrel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}

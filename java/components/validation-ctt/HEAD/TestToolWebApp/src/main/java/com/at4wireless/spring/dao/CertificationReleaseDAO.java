package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.CertificationRelease;

public interface CertificationReleaseDAO {
	public List<CertificationRelease> list();
	public String getName(int id);
	public List<Integer> getIds(int idCertRel);
}

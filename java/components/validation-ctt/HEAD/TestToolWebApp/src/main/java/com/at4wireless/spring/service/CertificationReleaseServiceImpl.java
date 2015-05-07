package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.model.CertificationRelease;

@Service
public class CertificationReleaseServiceImpl implements CertificationReleaseService {

	@Autowired
	private CertificationReleaseDAO certRelDao;
	
	@Override
	@Transactional
	public List<CertificationRelease> list() {
		return certRelDao.list();
	}

}

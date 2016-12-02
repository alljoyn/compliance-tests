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

package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.model.CertificationRelease;

@Service
public class CertificationReleaseServiceImpl implements CertificationReleaseService
{
	@Autowired
	private CertificationReleaseDAO certificationReleaseDAO;
	
	@Override
	@Transactional(readOnly = true)
	public List<CertificationRelease> list()
	{
		return certificationReleaseDAO.list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CertificationRelease> listReleaseVersions()
	{
		return certificationReleaseDAO.listRelease();
	}

	@Override
	@Transactional(readOnly = true)
	public String getCertificationReleaseDescription(String certificationRelease)
	{
		return certificationReleaseDAO.getDescription(certificationRelease);
	}
	
	@Override
	@Transactional(readOnly = true)
	public String getCertificationReleaseName(int certificationReleaseId)
	{
		return certificationReleaseDAO.getByID(certificationReleaseId).getName();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isReleaseVersion(int certificationReleaseId)
	{
		return certificationReleaseDAO.getByID(certificationReleaseId).isRelease();
	}
}
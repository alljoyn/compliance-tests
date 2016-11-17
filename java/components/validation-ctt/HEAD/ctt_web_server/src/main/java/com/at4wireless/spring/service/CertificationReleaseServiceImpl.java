/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
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
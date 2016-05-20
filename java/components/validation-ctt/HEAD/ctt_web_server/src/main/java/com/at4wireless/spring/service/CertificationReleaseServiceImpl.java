/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

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

	@Override
	@Transactional
	public List<CertificationRelease> listReleaseVersions() {
		return certRelDao.listReleaseVersions();
	}

	@Override
	@Transactional
	public String getCertificationReleaseDescription(String certificationRelease) {
		return certRelDao.getCertificationReleaseDescription(certificationRelease);
	}
	
	@Override
	@Transactional
	public String getCertificationReleaseName(int certificationReleaseId)
	{
		return certRelDao.getName(certificationReleaseId);
	}

	@Override
	@Transactional
	public boolean isReleaseVersion(int certificationReleaseId)
	{
		return certRelDao.isReleaseVersion(certRelDao.getName(certificationReleaseId));
	}
}

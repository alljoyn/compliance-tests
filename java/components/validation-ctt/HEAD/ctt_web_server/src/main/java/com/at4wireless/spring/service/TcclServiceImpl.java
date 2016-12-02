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

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.common.ConfigParam;
import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.TcDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

@Service
public class TcclServiceImpl implements TcclService
{
	@Autowired
	private TcclDAO tcclDao;	
	@Autowired
	private CertificationReleaseDAO certrelDao;
	@Autowired
	private TcDAO tcDao;
	
	@Override
	@Transactional
	public List<Tccl> list()
	{
		return tcclDao.list();
	}

	@Override
	@Transactional
	public Tccl create(Map<String, String[]> map)
	{
		Tccl tccl = new Tccl();
		
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		int next = tcclDao.getNumber(map.get("certrel[name]")[0].substring(1))+1;
		tccl.setName("TCCL_"+map.get("certrel[name]")[0].substring(1)+"_v0."+next);
		tccl.setCreatedDate(date);
		tccl.setModifiedDate(date);
		tccl.setIdCertrel(Integer.parseInt(map.get("certrel[id]")[0]));
		tccl.setNumTc((map.size()-2)/3);
		tccl.setNameCertrel(certrelDao.getByID(Integer.parseInt(map.get("certrel[id]")[0])).getName());
		int idTccl = tcclDao.add(tccl);
				
		if (idTccl != 0)
		{
			List<String> types = new ArrayList<String>();
			List<Boolean> enables = new ArrayList<Boolean>();
			List<BigInteger> testcaseIDs = new ArrayList<BigInteger>();
			
			String typeKey = "json[%d][type]";
			String enabledKey = "json[%d][enabled]";
			String testCaseIdKey = "json[%d][id]";
			for (int i = 0; i < ((map.size() - 2) / 3); i++)
			{
				types.add(map.get(String.format(typeKey, i))[0]);
				enables.add(Boolean.valueOf(map.get(String.format(enabledKey, i))[0]));
				testcaseIDs.add(new BigInteger(map.get(String.format(testCaseIdKey, i))[0]));
			}
			
			tcclDao.storeAssociatedTestCases(idTccl, types, enables, testcaseIDs);
		}
		
		return tccl;
	}

	@Override
	@Transactional
	public void delete(int idTccl)
	{
		tcclDao.deleteList(idTccl);
		tcclDao.deleteTccl(idTccl);
	}

	@Override
	@Transactional
	public List<TestCaseTccl> getTccl(int idTccl)
	{
		return tcclDao.getList(idTccl);
	}

	@Override
	@Transactional
	public Tccl update(Map<String, String[]> map)
	{
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		int idTccl = Integer.parseInt(map.get("idTccl")[0]);
		Tccl tccl = new Tccl();
		tccl.setIdTccl(idTccl);
		tccl.setModifiedDate(date);

		tcclDao.updateTccl(idTccl,date);
		tcclDao.deleteList(idTccl);
		
		if (idTccl != 0)
		{
			List<String> types = new ArrayList<String>();
			List<Boolean> enables = new ArrayList<Boolean>();
			List<BigInteger> testcaseIDs = new ArrayList<BigInteger>();
			
			String typeKey = "json[%d][type]";
			String enabledKey = "json[%d][enabled]";
			String testCaseIdKey = "json[%d][id]";
			for (int i = 0; i < ((map.size() - 2) / 3); i++)
			{
				types.add(map.get(String.format(typeKey, i))[0]);
				enables.add(Boolean.valueOf(map.get(String.format(enabledKey, i))[0]));
				testcaseIDs.add(new BigInteger(map.get(String.format(testCaseIdKey, i))[0]));
			}
			
			tcclDao.storeAssociatedTestCases(idTccl, types, enables, testcaseIDs);
		}
		
		return tccl;
	}

	@Override
	@Transactional
	public List<Tccl> listByCR(int idCertRel)
	{
		return tcclDao.listByCR(idCertRel);
	}

	@Override
	@Transactional
	public void addCertificationReleaseIfNotExists(String certificationRelease, String packageVersion,
			String description) throws Exception
	{
		if (!certrelDao.exists(certificationRelease))
		{
			CertificationRelease cRelease = new CertificationRelease();
			cRelease.setName(certificationRelease);
			cRelease.setEnabled(true);
			cRelease.setRelease(isReleaseVersion(packageVersion));
			cRelease.setDescription(description);
			int crId = certrelDao.add(cRelease);
			tcDao.assignTestCasesToCertificationRelease(crId);
		}
		else
		{
			if (certrelDao.isRelease(certificationRelease))
			{
				if (!isReleaseVersion(packageVersion))
				{
					throw new Exception(String.format("A release version of the package already exists. Debug versions of %s are not allowed", certificationRelease));
				}
				else
				{
					certrelDao.updateDescription(certificationRelease, description);
				}
			}
			else
			{
				if (isReleaseVersion(packageVersion))
				{
					certrelDao.fromDebugToRelease(certificationRelease);
					cleanDebugPackages(certificationRelease);
				}
				certrelDao.updateDescription(certificationRelease, description);
			}
		}
	}
	
	private boolean isReleaseVersion(String packageVersion)
	{
		return (packageVersion.charAt(0) == 'R');
	}
	
	private void cleanDebugPackages(final String certificationRelease)
	{
		File folder = new File(ConfigParam.PACKAGES_PATH);
		File[] files = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.contains(certificationRelease + "_D");
			}
		});
		
		for (final File file : files)
		{
			if (!file.delete())
			{
				System.err.println(String.format("Can't remove %s", file.getAbsolutePath()));
			}
		}
	}
	
	@Override
	@Transactional
	public String getTcclName(int idTccl)
	{
		return tcclDao.getTcclName(idTccl);
	}
}
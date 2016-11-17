/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.CertificationRelease;

@Repository
public class CertificationReleaseDAOImpl implements CertificationReleaseDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<CertificationRelease> list()
	{
		TypedQuery<CertificationRelease> query = sessionFactory.getCurrentSession()
				.createQuery("from CertificationRelease where enabled = :enabled", CertificationRelease.class)
				.setParameter("enabled", true);
		
		return query.getResultList();
	}
	
	@Override
	public List<CertificationRelease> listRelease()
	{	
		TypedQuery<CertificationRelease> query = sessionFactory.getCurrentSession()
				.createQuery("from CertificationRelease where enabled = :enabled and release = :release", CertificationRelease.class)
				.setParameter("enabled", true)
				.setParameter("release", true);
		
		return query.getResultList();
	}

	@Override
	public CertificationRelease getByID(int certRelID)
	{
		TypedQuery<CertificationRelease> query = sessionFactory.getCurrentSession()
				.createQuery("from CertificationRelease where idCertrel = :idCertrel", CertificationRelease.class)
				.setParameter("idCertrel", certRelID);
		
		CertificationRelease foundCertificationRelease = null;
		try
		{
			foundCertificationRelease = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundCertificationRelease;
	}

	@Override
	public String getDescription(String certRelName)
	{
		@SuppressWarnings("unchecked")
		TypedQuery<String> query = sessionFactory.getCurrentSession()
				.createQuery("select description from CertificationRelease where name = :name")
				.setParameter("name", certRelName);
		
		String crDescription = null;
		try
		{
			crDescription = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return crDescription;
	}
	
	@Override
	public List<BigInteger> getTestCasesByID(int certRelID)
	{
		/*
		 * TODO : Change query from NativeQuery to TypedQuery
		 * 
		 * */
		@SuppressWarnings("unchecked")
		NativeQuery<BigInteger> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select id_test from testcases_certrel where id_certrel = :idCertrel")
				.setParameter("idCertrel", certRelID);
		
		return query.getResultList();
	}
	
	@Override
	public int add(CertificationRelease certificationRelease)
	{
		sessionFactory.getCurrentSession().save(certificationRelease);
		
		return certificationRelease.getIdCertrel();
	}
	
	@Override
	public void fromDebugToRelease(String certificationRelease)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update CertificationRelease set release = :release where name = :name")
				.setParameter("release", true)
				.setParameter("name", certificationRelease)
				.executeUpdate();
	}

	@Override
	public void updateDescription(String certificationRelease, String description)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update CertificationRelease set description = :description where name = :name")
				.setParameter("description", description)
				.setParameter("name", certificationRelease)
				.executeUpdate();
	}

	@Override
	public boolean exists(String certificationRelease)
	{
		TypedQuery<CertificationRelease> query = sessionFactory.getCurrentSession()
				.createQuery("from CertificationRelease where name = :name", CertificationRelease.class)
				.setParameter("name", certificationRelease);
		
		boolean certificationReleaseExists = true;
		try
		{
			query.getSingleResult();
		}
		catch (NoResultException e)
		{
			certificationReleaseExists = false;
		}
		
		return certificationReleaseExists;
	}

	@Override
	public boolean isRelease(String certificationRelease)
	{
		@SuppressWarnings("unchecked")
		TypedQuery<Boolean> query = sessionFactory.getCurrentSession()
				.createQuery("select release from CertificationRelease where name = :name")
				.setParameter("name", certificationRelease);
		
		boolean isReleaseVersion;
		try
		{
			isReleaseVersion = query.getSingleResult().booleanValue();
		}
		catch (NoResultException e)
		{
			isReleaseVersion = false;
		}
		
		return isReleaseVersion;
	}
}
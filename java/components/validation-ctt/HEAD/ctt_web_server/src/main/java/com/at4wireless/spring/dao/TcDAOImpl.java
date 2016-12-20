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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.TestCase;

@Repository
public class TcDAOImpl implements TcDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<TestCase> list()
	{
		TypedQuery<TestCase> query = sessionFactory.getCurrentSession()
				.createQuery("from TestCase", TestCase.class);
		
		return query.getResultList();
	}
	
	@Override
	public List<TestCase> listByCertRel(int certRelID)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		NativeQuery<TestCase> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select tc.* from testcases tc where tc.id_test in "
						+ "(select tcr.id_test from testcases_certrel tcr where tcr.id_certrel = :idCertrel)", TestCase.class)
				.setParameter("idCertrel", certRelID);
		
		return query.getResultList();
	}
	
	@Override
	public List<TestCase> getByTypeAndService(String type, BigInteger serviceID)
	{
		TypedQuery<TestCase> query = sessionFactory.getCurrentSession()
				.createQuery("from TestCase where serviceGroup = :serviceGroup and type = :type", TestCase.class)
				.setParameter("serviceGroup", serviceID)
				.setParameter("type", type);
		
		return query.getResultList();
	}
	
	@Override
	public List<TestCase> getServiceWithRestriction(String type, BigInteger idService, List<BigInteger> testCaseIDsFromCR)
	{
		TypedQuery<TestCase> query = sessionFactory.getCurrentSession()
				.createQuery("from TestCase where serviceGroup = :serviceGroup and type = :type and idTC in (:ids)", TestCase.class)
				.setParameter("serviceGroup", idService)
				.setParameter("type", type)
				.setParameterList("ids", testCaseIDsFromCR);
		
		return query.getResultList();
	}

	@Override
	public void assignTestCasesToCertificationRelease(int certificationReleaseId)
	{
		/*
		 * TODO : Improve the insertion of test cases to a new certification release, starting with the View.
		 * 
		 */
		List<TestCase> listTC = list();
		for(int i=0; i<listTC.size(); i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into testcases_certrel (id_test, id_certrel) values (:idTest, :idCertrel)")
					.setParameter("idTest", listTC.get(i).getIdTC())
					.setParameter("idCertrel", certificationReleaseId)
					.executeUpdate();
		}
	}
	
	@Override
	public void add(TestCase testCase)
	{
		sessionFactory.getCurrentSession().save(testCase);
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		BigInteger idTestCase = testCase.getIdTC();
		String[] supportedCertificationReleases = testCase.getSupportedCrs().split("[\\.]+");
		for (int i = 0; i < supportedCertificationReleases.length; i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into testcases_certrel (id_test, id_certrel) values (:idTest, :idCertrel)")
					.setParameter("idTest", idTestCase)
					.setParameter("idCertre", supportedCertificationReleases[i])
					.executeUpdate();
		}
	}
	
	@Override
	public void update(TestCase testCase)
	{
		/*
		 * TODO : Look for a shorter way to update existent Object
		 * 
		 */
		BigInteger idTestCase = testCase.getIdTC();
		sessionFactory.getCurrentSession()
				.createQuery("update TestCase set name = :name, type = :type, applicability = :applicability, "
						+ "serviceGroup = :serviceGroup, description = :description where idTC = :idTC")
				.setParameter("name", testCase.getName())
				.setParameter("type", testCase.getType())
				.setParameter("applicability", testCase.getApplicability())
				.setParameter("serviceGroup", testCase.getServiceGroup())
				.setParameter("description", testCase.getDescription())
				.setParameter("idTC", idTestCase)
				.executeUpdate();
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from testcases_certrel where id_test = :idTest")
				.setParameter("idTest", idTestCase)
				.executeUpdate();
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		String[] supportedCertificationReleases = testCase.getSupportedCrs().split("[\\.]+");
		for (int i = 0; i < supportedCertificationReleases.length; i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into testcases_certrel (id_test, id_certrel) values (:idTest, :idCertrel)")
					.setParameter("idTest", idTestCase)
					.setParameter("idCertre", supportedCertificationReleases[i])
					.executeUpdate();
		}
	}
}
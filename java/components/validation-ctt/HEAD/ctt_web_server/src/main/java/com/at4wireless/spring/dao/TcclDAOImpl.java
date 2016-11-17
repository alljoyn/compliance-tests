/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

@Repository
public class TcclDAOImpl implements TcclDAO
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<Tccl> list()
	{
		TypedQuery<Tccl> query = sessionFactory.getCurrentSession()
				.createQuery("from Tccl", Tccl.class);
		
		return query.getResultList();
	}

	@Override
	public int getNumber(String certRel)
	{
		/*
		 * TODO : Modify query to retrieve count
		 * 
		 */
		TypedQuery<Tccl> query = sessionFactory.getCurrentSession()
				.createQuery("from Tccl where name like :nameFilter", Tccl.class)
				.setParameter("nameFilter", "%" + certRel + "_v%");
		
		List<Tccl> listTccl = query.getResultList();
		return listTccl.isEmpty() ? 0 : listTccl.size();
	}

	@Override
	public int add(Tccl tccl)
	{
		sessionFactory.getCurrentSession().save(tccl);
		return tccl.getIdTccl();
	}
	
	@Override
	public void storeAssociatedTestCases(int tcclID, List<String> types, List<Boolean> enables, List<BigInteger> testcaseIDs)
	{
		/*
		 * TODO: Change NativeQuery to TypedQuery
		 * 
		 */
		for (int i = 0; i < testcaseIDs.size(); i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into tccl_testcase (id_tccl, type, enable, id_test) values (:idTccl, :type, :enable, :idTest)")
					.setParameter("idTccl", tcclID)
					.setParameter("type", types.get(i))
					.setParameter("enable", enables.get(i))
					.setParameter("idTest", testcaseIDs.get(i))
					.executeUpdate();
		}
	}

	@Override
	public void deleteList(int idTccl)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from tccl_testcase where id_tccl = :idTccl")
				.setParameter("idTccl", idTccl)
				.executeUpdate();
	}

	@Override
	public void deleteTccl(int idTccl)
	{
		sessionFactory.getCurrentSession()
				.createQuery("delete from Tccl where idTccl = :idTccl")
				.setParameter("idTccl", idTccl)
				.executeUpdate();
	}

	@Override
	public List<TestCaseTccl> getList(int idTccl)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<TestCaseTccl> query = sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT b.id_test, b.name, b.description, a.type, a.enable FROM tccl_testcase a "
						+ "INNER JOIN testcases b ON a.id_test = b.id_test where a.id_tccl = :idTccl")
				.setParameter("idTccl", idTccl);
		
		return query.getResultList();
	}
	
	@Override
	public void updateTccl(int idTccl, java.sql.Timestamp date)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update Tccl set modifiedDate = :modifiedDate where idTccl = :idTccl")
				.setParameter("modifiedDate", date)
				.setParameter("idTccl", idTccl)
				.executeUpdate();
	}

	@Override
	public List<Tccl> listByCR(int idCertRel)
	{
		TypedQuery<Tccl> query = sessionFactory.getCurrentSession()
				.createQuery("from Tccl where idCertrel = :idCertrel", Tccl.class)
				.setParameter("idCertrel", idCertRel);
		
		return query.getResultList();
	}

	@Override
	public List<Integer> getIds(int idTccl)
	{
		/*
		 * TODO : 	Change NativeQuery to TypedQuery
		 * 			Casting BigInteger to Integer
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<Integer> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select id_test from tccl_testcase where id_tccl = :idTccl and enable = :enable")
				.setParameter("idTccl", idTccl)
				.setParameter("enable", true);
		
		return query.getResultList();
	}

	@Override
	public List<BigInteger> getIdsDisabled(int idTccl)
	{
		/*
		 * TODO : 	Change NativeQuery to TypedQuery
		 * 			Casting BigInteger to Integer
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<BigInteger> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select id_test from tccl_testcase where id_tccl = :idTccl and enable = :enable")
				.setParameter("idTccl", idTccl)
				.setParameter("enable", false);
		
		return query.getResultList();
	}

	@Override
	public String getTcclName(int idTccl)
	{
		/*
		 * TODO : Handle NoResultException properly
		 * 
		 */
		@SuppressWarnings("unchecked")
		TypedQuery<String> query = sessionFactory.getCurrentSession()
				.createQuery("select name from Tccl where idTccl = :idTccl")
				.setParameter("idTccl", idTccl);
		
		String tcclName = null;
		try
		{
			tcclName = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// It means there is no Tccl with this ID
		}
		
		return tcclName;
	}
}
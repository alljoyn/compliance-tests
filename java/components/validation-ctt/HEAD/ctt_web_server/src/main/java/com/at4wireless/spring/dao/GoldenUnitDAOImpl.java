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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.GoldenUnit;

@Repository
public class GoldenUnitDAOImpl implements GoldenUnitDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<GoldenUnit> list(String username)
	{
		TypedQuery<GoldenUnit> query = sessionFactory.getCurrentSession()
				.createQuery("from GoldenUnit where user = :user", GoldenUnit.class)
				.setParameter("user", username);
		
		return query.getResultList();
	}
	
	@Override
	public GoldenUnit getByID(String username, BigInteger goldenUnitID)
	{
		TypedQuery<GoldenUnit> query = sessionFactory.getCurrentSession()
				.createQuery("from GoldenUnit where user = :user and idGolden = :idGolden", GoldenUnit.class)
				.setParameter("user", username)
				.setParameter("idGolden", goldenUnitID);
		
		GoldenUnit foundGoldenUnit = null;
		try
		{
			foundGoldenUnit = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundGoldenUnit;
	}

	@Override
	public void add(GoldenUnit newGoldenUnit)
	{
		sessionFactory.getCurrentSession().save(newGoldenUnit);		
	}
	
	@Override
	public void deleteByID(BigInteger goldenUnitID)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 *  
		 */
		sessionFactory.getCurrentSession().createNativeQuery("delete from project_golden where id_golden = :idGolden")
				.setParameter("idGolden", goldenUnitID)
				.executeUpdate();
		
		sessionFactory.getCurrentSession()
				.createQuery("delete from GoldenUnit where idGolden = :idGolden")
				.setParameter("idGolden", goldenUnitID)
				.executeUpdate();
	}
	
	@Override
	public void saveChanges(GoldenUnit goldenUnit)
	{
		/*
		 * TODO : Check if there is a shorter way to update an existent Object
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createQuery("update GoldenUnit set name = :name, modifiedDate = :modifiedDate, "
						+ "category = :category, manufacturer = :manufacturer, model = :model, "
						+ "swVer = :swVer, hwVer = :hwVer, description = :description where idGolden = :idGolden")
				.setParameter("name", goldenUnit.getName())
				.setParameter("modifiedDate", goldenUnit.getModifiedDate())
				.setParameter("category", goldenUnit.getCategory())
				.setParameter("manufacturer", goldenUnit.getManufacturer())
				.setParameter("model", goldenUnit.getModel())
				.setParameter("swVer", goldenUnit.getSwVer())
				.setParameter("hwVer", goldenUnit.getHwVer())
				.setParameter("description", goldenUnit.getDescription())
				.setParameter("idGolden", goldenUnit.getIdGolden())
				.executeUpdate();
	}

	@Override
	public GoldenUnit getByName(String username, String goldenUnitName)
	{
		TypedQuery<GoldenUnit> query = sessionFactory.getCurrentSession()
				.createQuery("from GoldenUnit where user = :user and name = :name", GoldenUnit.class)
				.setParameter("user", username)
				.setParameter("name", goldenUnitName);
		
		GoldenUnit foundGoldenUnit = null;
		try
		{
			foundGoldenUnit = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundGoldenUnit;
	}

	@Override
	public List<GoldenUnit> listByProjectID(int projectID)
	{
		/*
		 * TODO :	Change NativeQuery to TypedQuery
		 * 			Casting BigInteger to Integer
		 * 			Add ManyToMany to Project and GoldenUnit models in order to retrieve them using one query
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<Integer> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select id_golden from project_golden where id_project = :idProject")
				.setParameter("idProject", projectID);
		
		List<Integer> goldenUnitIDs = query.getResultList();
		List<GoldenUnit> goldenUnitList = new ArrayList<GoldenUnit>();
		
		if (!goldenUnitIDs.isEmpty())
		{
			TypedQuery<GoldenUnit> guQuery = sessionFactory.getCurrentSession()
					.createQuery("from GoldenUnit where idGolden in (:ids)", GoldenUnit.class)
					.setParameterList("ids", goldenUnitIDs);
			
			goldenUnitList = guQuery.getResultList();
		}
		
		return goldenUnitList;
	}

	@Override
	public void deleteByProjectID(int projectID)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from project_golden where id_project = :idProject")
				.setParameter("idProject", projectID)
				.executeUpdate();
	}
}
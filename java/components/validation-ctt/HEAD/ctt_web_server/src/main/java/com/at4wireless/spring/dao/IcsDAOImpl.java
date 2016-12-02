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

package com.at4wireless.spring.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Ics;

@Repository
public class IcsDAOImpl implements IcsDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Ics> list()
	{
		TypedQuery<Ics> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_all_ics", Ics.class);
		
		return query.getResultList();
	}

	@Override
	@Transactional
	public List<Ics> getByService(int serviceID)
	{
		TypedQuery<Ics> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ics_by_service_group", Ics.class)
				.setParameter("serviceGroup", serviceID);
		
		return query.getResultList();
	}

	@Override
	public void add(Ics newIcs)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(newIcs);
	}

	@Override
	public Ics get(String name)
	{
		TypedQuery<Ics> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ics_by_name", Ics.class)
				.setParameter("name", name);
		
		Ics foundIcs = null;
		try
		{
			foundIcs = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundIcs;
	}
}
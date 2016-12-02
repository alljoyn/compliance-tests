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

import com.at4wireless.spring.model.Ixit;

@Repository
public class IxitDAOImpl implements IxitDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Ixit> list()
	{	
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_all_ixit", Ixit.class);
		
		return query.getResultList();
	}
	
	@Override
	public Ixit get(String name)
	{
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ixit_by_name", Ixit.class)
				.setParameter("name", name);
		
		Ixit foundIxit = null;
		try
		{
			foundIxit = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundIxit;
	}
	
	@Override
	@Transactional
	public List<Ixit> getByService(int serviceID)
	{	
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ixit_by_service_group", Ixit.class)
				.setParameter("serviceGroup", serviceID);
		
		return query.getResultList();
	}

	@Override
	public void add(Ixit ixit)
	{
		sessionFactory.getCurrentSession().save(ixit);
	}
}
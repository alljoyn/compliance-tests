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

import com.at4wireless.spring.model.Dut;

@Repository
public class DutDAOImpl implements DutDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Dut> list(String user)
	{		
		TypedQuery<Dut> query = sessionFactory.getCurrentSession()
				.createQuery("from Dut where user = :user", Dut.class)
				.setParameter("user", user);
		
		return query.getResultList();
	}
	
	@Override
	public Dut get(int dutID, String user)
	{
		TypedQuery<Dut> query = sessionFactory.getCurrentSession()
				.createQuery("from Dut where idDut = :idDut and user = :user", Dut.class)
				.setParameter("idDut", dutID)
				.setParameter("user", user);
		
		Dut foundDut = null;	
		try
		{
			foundDut = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundDut;
	}
	
	@Override
	public Dut get(String name, String user)
	{		
		TypedQuery<Dut> query = sessionFactory.getCurrentSession()
				.createQuery("from Dut where name = :name and user = :user", Dut.class)
				.setParameter("name", name)
				.setParameter("user", user);
		
		Dut foundDut = null;
		try
		{
			foundDut = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundDut;
	}

	@Override
	public void add(Dut newDut)
	{
		sessionFactory.getCurrentSession().save(newDut);		
	}
	
	@Override
	public int delete(int dutID)
	{
		return sessionFactory.getCurrentSession()
				.createQuery("delete Dut where idDut = :idDut")
				.setParameter("idDut", dutID)
				.executeUpdate();
	}
}
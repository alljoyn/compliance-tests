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

import com.at4wireless.spring.model.Sample;

@Repository
public class SampleDAOImpl implements SampleDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Sample> list(int dutID)
	{
		TypedQuery<Sample> query = sessionFactory.getCurrentSession()
				.createQuery("from Sample where associatedDut = :idDut", Sample.class)
				.setParameter("idDut", dutID);
		
		return query.getResultList();
	}
	
	@Override
	public Sample get(int sampleID)
	{
		TypedQuery<Sample> query = sessionFactory.getCurrentSession()
				.createQuery("from Sample where idSample = :idSample", Sample.class)
				.setParameter("idSample", sampleID);
		
		Sample foundSample = null;
		try
		{
			foundSample = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundSample;
	}

	@Override
	public void add(Sample newSample)
	{
		sessionFactory.getCurrentSession().save(newSample);		
	}
	
	@Override
	public int delete(int sampleID)
	{
		return sessionFactory.getCurrentSession()
				.createQuery("delete Sample where idSample = :idSample")
				.setParameter("idSample", sampleID)
				.executeUpdate();
	}
}
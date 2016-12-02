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

import java.math.BigInteger;
import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.ServiceFramework;

@Repository
public class ServiceFrameworkDAOImpl implements ServiceFrameworkDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<ServiceFramework> list()
	{
		TypedQuery<ServiceFramework> query = sessionFactory.getCurrentSession()
				.createQuery("from ServiceFramework", ServiceFramework.class);

		return query.getResultList();
	}
	
	@Override
	public List<BigInteger> getServices(int idProject)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<BigInteger> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select id_service from project_services where id_project = :idProject")
				.setParameter("idProject", idProject);
		
		return query.getResultList();
	}

	@Override
	public List<String> getServicesByName(int idProject)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<String> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select name from services where id_service in "
						+ "(select id_service from project_services where id_project = :idProject)")
				.setParameter("idProject", idProject);
		
		return query.getResultList();
	}
}
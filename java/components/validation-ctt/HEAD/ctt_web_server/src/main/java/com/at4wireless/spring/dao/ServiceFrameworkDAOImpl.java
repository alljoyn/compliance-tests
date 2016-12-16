/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
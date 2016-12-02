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

import com.at4wireless.spring.model.Category;

@Repository
public class CategoryDAOImpl implements CategoryDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Category> list()
	{
		TypedQuery<Category> query = sessionFactory.getCurrentSession()
				.createQuery("from Category", Category.class);
		
		return query.getResultList();
	}

	@Override
	@Transactional
	public Category getById(int categoryID)
	{
		TypedQuery<Category> query = sessionFactory.getCurrentSession()
				.createQuery("from Category where idCategory = :idCategory", Category.class)
				.setParameter("idCategory", categoryID);
		
		Category foundCategory = null;
		try
		{
			foundCategory = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundCategory;
	}
}
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

package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CategoryDAO;
import com.at4wireless.spring.dao.GoldenUnitDAO;
import com.at4wireless.spring.model.Category;
import com.at4wireless.spring.model.GoldenUnit;

@Service
public class GoldenUnitServiceImpl implements GoldenUnitService
{
	@Autowired
	private GoldenUnitDAO guDao;
	
	@Autowired
	private CategoryDAO categoryDao;
	
	@Override
	@Transactional
	public GoldenUnit create(GoldenUnit gu)
	{
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		
		gu.setCreatedDate(date);
		gu.setModifiedDate(date);

		if (guDao.getByName(gu.getUser(), gu.getName()) != null)
		{
			return null;
		}
		else
		{
			guDao.add(gu);
			return gu;
		}
	}

	@Override
	@Transactional
	public List<GoldenUnit> getTableData(String username)
	{
		return guDao.list(username);
	}

	@Override
	@Transactional
	public GoldenUnit getFormData(String username, BigInteger idGu)
	{
		return guDao.getByID(username,idGu);
	}

	@Override
	@Transactional
	public GoldenUnit update(GoldenUnit gu)
	{
		if (guDao.getByID(gu.getUser(), gu.getIdGolden()) != null)
		{
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
			gu.setModifiedDate(date);
			guDao.saveChanges(gu);
			return gu;
		}
		else
		{
			return null;
		}
	}

	@Override
	@Transactional
	public boolean delete(String username, BigInteger idGu)
	{
		if (guDao.getByID(username, idGu) != null)
		{
			guDao.deleteByID(idGu);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	@Transactional
	public List<Category> getCategories()
	{
		return categoryDao.list();
	}

	@Override
	@Transactional
	public List<GoldenUnit> getGuList(int idProject)
	{
		return guDao.listByProjectID(idProject);
	}
	
	@Override
	@Transactional
	public boolean exists(String username, String name, BigInteger id)
	{	
		GoldenUnit gu = guDao.getByName(username, name);
		
		if (gu == null)
		{
			return false;
		}
		else if (gu.getIdGolden().equals(id))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	@Transactional
	public Category getCategoryById(int idCategory)
	{
		return categoryDao.getById(idCategory);
	}

	@Override
	@Transactional
	public void deleteProjectGus(int idProject)
	{
		guDao.deleteByProjectID(idProject);
	}
}
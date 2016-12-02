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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.ServiceFrameworkDAO;
import com.at4wireless.spring.model.ServiceFramework;

@Service
public class ServiceFrameworkServiceImpl implements ServiceFrameworkService {
	@Autowired
	private ServiceFrameworkDAO serviceDao;
	
	@Override
	@Transactional
	public List<ServiceFramework> list() {
		return serviceDao.list();
	}
}
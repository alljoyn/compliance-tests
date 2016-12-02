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

import com.at4wireless.spring.model.ServiceFramework;

/**
 * Interface with methods that allow controllers to communicate with service framework table
 *
 */
public interface ServiceFrameworkService {
	
	/**
	 * Returns a list of service frameworks
	 * 
	 * @return	list of service frameworks
	 */
	public List<ServiceFramework> list();
}
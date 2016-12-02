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

import com.at4wireless.spring.model.ServiceFramework;

public interface ServiceFrameworkDAO
{
	/**
	 * @return all available service frameworks
	 */
	public List<ServiceFramework> list();
	
	/**
	 * Searches services associated to a project and returns their IDs
	 * 
	 * @param projectID
	 * 			ID of the project whose services have to be returned
	 * 
	 * @return services IDs list
	 */
	public List<BigInteger> getServices(int projectID);
	
	/**
	 * Searches services associated to a project and returns their names
	 * 
	 * @param projectID
	 * 			ID of the project whose services have to be returned
	 * 
	 * @return services names list
	 */
	public List<String> getServicesByName(int projectID);
}
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

import com.at4wireless.spring.model.Ixit;

public interface IxitDAO
{
	/**
	 * Retrieves the list of IXIT
	 * 
	 * @return list of IXIT
	 */
	public List<Ixit> list();
	
	/**
	 * Retrieves an IXIT using its name in query
	 * 
	 * @param name
	 * 			name of the IXIT to be retrieved
	 * 
	 * @return retrieved IXIT
	 */
	public Ixit get(String name);
	
	/**
	 * Retrieves a list of IXIT of a certain service framework
	 * 
	 * @param serviceID
	 * 			ID of the Service Framework whose IXIT are going to be retrieved
	 * 
	 * @return list of IXIT
	 */
	public List<Ixit> getByService(int serviceID);
	
	/**
	 * Creates a new IXIT
	 * 
	 * @param newIxit
	 * 			information of the new IXIT to be added to database
	 */
	public void add(Ixit newIxit);
}
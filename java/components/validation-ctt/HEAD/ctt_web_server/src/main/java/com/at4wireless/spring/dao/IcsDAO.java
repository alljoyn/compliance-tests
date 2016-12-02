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

import com.at4wireless.spring.model.Ics;

public interface IcsDAO
{
	/**
	 * Retrieves the list of ICS
	 * 
	 * @return list of ICS
	 */
	public List<Ics> list();
	
	/**
	 * Retrieves an ICS using its name in query
	 * 
	 * @param name
	 * 			name of the ICS to be retrieved
	 * 
	 * @return retrieved ICS
	 */
	public Ics get(String name);
	
	/**
	 * Retrieves a list of ICS of a certain service framework
	 * 
	 * @param serviceID
	 * 			ID of the service framework whose ICS are going to be retrieved
	 * 
	 * @return list of ICS
	 */
	public List<Ics> getByService(int serviceID);
	
	/**
	 * Stores a new ICS in database
	 * 
	 * @param newIcs
	 * 			information of the new ICS to be added to database
	 */
	public void add(Ics newIcs);
}
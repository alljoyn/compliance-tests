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

import com.at4wireless.spring.model.Dut;

public interface DutDAO
{
	/**
	 * Retrieves DUTs of a certain user
	 * 
	 * @param user
	 * 			user whose DUT are going to be retrieved
	 * 
	 * @return DUT list
	 */
	public List<Dut> list(String user);
	
	/**
	 * Retrieves information of a certain DUT using its ID in query
	 * 
	 * @param dutID
	 * 			ID of the DUT to be retrieved
	 * @param user
	 * 			user who created the DUT to be retrieved
	 * 
	 * @return DUT object
	 */
	public Dut get(int dutID, String user);
	
	/**
	 * Retrieves information of a certain DUT using its name in query
	 * 
	 * @param name
	 * 			name of the DUT to be retrieved
	 * @param user
	 * 			user who created the DUT to be retrieved
	 * 
	 * @return DUT object
	 */
	public Dut get(String name, String user);
	
	/**
	 * Creates a new DUT
	 * 
	 * @param newDut
	 * 			data of the new DUT to be stored
	 */
	public void add(Dut newDut);
	
	/**
	 * Removes a DUT using its ID in query
	 * 
	 * @param dutID
	 * 			ID of the DUT to be removed
	 * 
	 * @return the number of entities deleted
	 */
	public int delete(int dutID);
}
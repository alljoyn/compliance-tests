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

import com.at4wireless.spring.model.GoldenUnit;

public interface GoldenUnitDAO
{
	/**
	 * Retrieves the list of Golden Units of a certain user
	 * 
	 * @param username
	 * 			name of the user whose Golden Units are going to be retrieved
	 * 
	 * @return list of Golden Units
	 */
	public List<GoldenUnit> list(String username);
	
	/**
	 * Adds a new Golden Unit to the database
	 * 
	 * @param goldenUnit
	 * 			Golden Unit to be added
	 */
	public void add(GoldenUnit newGoldenUnit);
	
	/**
	 * Removes a Golden Unit using its ID in the query
	 * 
	 * @param goldenUnitID
	 * 			ID of the Golden Unit to be removed
	 */
	public void deleteByID(BigInteger goldenUnitID);
	
	/**
	 * Updates a Golden Unit
	 * 
	 * @param goldenUnit
	 * 			information of the Golden Unit to be updated
	 */
	public void saveChanges(GoldenUnit goldenUnit);
	
	/**
	 * Retrieves a certain Golden Unit using its ID in query
	 * 
	 * @param username
	 * 			name of the user that manages the target Golden Unit
	 * @param goldenUnitID
	 * 			ID of the Golden Unit to be retrieved
	 * 
	 * @return Golden Unit
	 */
	public GoldenUnit getByID(String username, BigInteger goldenUnitID);
	
	/**
	 * Retrieves a certain Golden Unit using its name in query
	 * 
	 * @param username
	 * 			name of the user that manages the target Golden Unit
	 * @param goldenUnitName
	 * 			name of the Golden Unit to be retrieved
	 * 
	 * @return Golden Unit
	 */
	public GoldenUnit getByName(String username, String goldenUnitName);
	
	/**
	 * Retrieves assigned Golden Units of a certain project
	 * 
	 * @param projectID
	 * 			ID of the project whose assigned Golden Units are going to be retrieved
	 * 
	 * @return list of project's assigned Golden Units
	 */
	public List<GoldenUnit> listByProjectID(int projectID);
	
	/**
	 * Removes assigned Golden Units of a certain project
	 * 
	 * @param projectID
	 * 			ID of the project whose assigned Golden Units are going to be removed
	 */
	public void deleteByProjectID(int projectID);
}
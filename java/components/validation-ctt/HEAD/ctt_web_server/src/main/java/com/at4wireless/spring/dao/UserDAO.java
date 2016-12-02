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

import com.at4wireless.spring.model.User;

public interface UserDAO
{
	/**
	 * Adds a new user into database
	 * 
	 * @param newUser
	 * 			user to be added
	 */
	public void add(User newUser);
	
	/**
	 * Retrieves a user
	 * 
	 * @param username
	 * 			name of the user to be retrieved
	 * 
	 * @return user if exists, null otherwise
	 */
	public User get(String username);
	
	/**
	 * Retrieves the role of a certain user
	 * 
	 * @param username
	 * 			name of the user whose role will be retrieved
	 * 
	 * @return role if exists, null otherwise
	 */
	public String getUserRole(String username);
	
	/**
	 * Updates user's information
	 * 
	 * @param username
	 * 			name of the user to be updated
	 * @param role
	 * 			role of the user to be updated
	 */
	public void update(String username, String role);
	
	/**
	 * Stores the Local Agent's API Key of a certain user
	 * 
	 * @param username
	 * 			name of the user whose Local Agent API Key is going to be stored
	 * @param laApiKey
	 * 			API key
	 */
	public void setKey(String username, String laApiKey);
	
	/**
	 * Retrieves stored key of a certain user that is used to cipher logs 
	 * 
	 * @param username
	 * 			name of the user whose key is retrieved
	 * 
	 * @return cipher key
	 */
	public String getAesCipherKey(String username);
	
	/**
	 * Stores key of a certain user that will be used to cipher logs
	 * 
	 * @param username
	 * 			name of the user whose key is going to be stored
	 * @param aesCipherKey
	 * 			key that will be used to cipher logs
	 */
	public void setAesCipherKey(String username, String aesCipherKey);
}
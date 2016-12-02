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

import javax.crypto.SecretKey;

import com.at4wireless.spring.model.User;

/**
 * Interface with methods that allow user controller to communicate with database
 *
 */
public interface UserService
{
	/**
	 * Adds new user to dabatase
	 * 
	 * @param newUser user to be added
	 * @return true if the user is successfully added, false if user already exists
	 */
	public boolean addUser(User newUser);
	
	/**
	 * Checks if a user already exists
	 * @param	username	authenticated user
	 * @param	name		name to check
	 * @return				true if exists, false otherwise
	 */
	public boolean exists(String name);
	
	/**
	 * Updates user's information
	 * @param	user	user to be updated
	 * @param 	role	user's role
	 */
	public void update(String user, String role);
	
	/**
	 * Sets Local Agent API key
	 * 
	 * @param	name	user's name
	 */
	public int setKey(String name);
	
	/**
	 * Sets Local Agent Encryption Key
	 * 
	 * @param 	user		user's name
	 * @param 	localKey	local agent's encryption key
	 */
	public String keyExchange(String user, String localKey);
	
	/**
	 * Returns cipher key converted to object
	 * 
	 * @param 	user	user whose key is returned
	 * @return			secret key
	 */
	public SecretKey getAesSecretKey(String user);
	
	public boolean hasCipherKey(String user);
	
	public void setCipherKey(String user, String aesSecretKey);
	
	public String getUserRole(String username);
}
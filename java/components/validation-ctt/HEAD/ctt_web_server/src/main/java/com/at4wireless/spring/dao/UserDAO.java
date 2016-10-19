/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
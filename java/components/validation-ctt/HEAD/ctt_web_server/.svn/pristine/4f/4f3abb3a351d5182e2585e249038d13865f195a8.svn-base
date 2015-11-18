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

/**
 * Interface that manages database user-related access
 *
 */
public interface UserDAO {
	
	/**
	 * Adds a new user
	 * @param 	user	user to be added
	 */
	public void addUser(User user);
	
	/**
	 * Retrieves a user
	 * @param 	name	name of the user to be retrieved
	 * @return			user
	 */
	public User getUser(String name);
	
	/**
	 * Updates user's information
	 * @param 	name	name of the user to be updated
	 * @param 	role	role of the user to be updated
	 */
	public void update(String name, String role);
	
	/**
	 * Stores local agent API key
	 * 
	 * @param	name	user's name
	 * @param 	key		API key
	 */
	public void setKey(String name, String key);
	
	/**
	 * Retrieves stored key that is used to cipher logs
	 * 
	 * @param 	user	user whose key is retrieved
	 * @return			cipher key
	 */
	public String getAesCipherKey(String user);
	
	public void setAesCipherKey(String user, String aesCipherKey);
}

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
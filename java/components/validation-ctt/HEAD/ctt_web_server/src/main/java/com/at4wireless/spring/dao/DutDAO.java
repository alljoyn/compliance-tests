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
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

/**
 * Interface that manages database DUT-related access
 *
 */
public interface DutDAO {
	
	/**
	 * Retrieves DUTs of a certain user
	 * @param 	user	user whose DUT are going to be retrieved
	 * @return			DUT list
	 */
	public List<Dut> list(String user);
	
	/**
	 * Creates a new DUT
	 * @param 	dut		data of the DUT that is going to be created
	 */
	public void addDut(Dut dut);
	
	/**
	 * Removes a DUT
	 * @param 	dutId	ID of the DUT to be removed
	 */
	public void delDut(int dutId);
	
	/**
	 * Updates information of a DUT
	 * @param 	dut		DUT object with the information to be updated
	 */
	public void saveChanges(Dut dut);
	
	/**
	 * Retrieves information of a certain DUT using its ID in query
	 * @param 	user	DUT user
	 * @param 	idDut	DUT ID
	 * @return			DUT object
	 */
	public Dut getDut(String user, int idDut);
	
	/**
	 * Retrieves information of a certain DUT using its name in query
	 * @param 	user	DUT user
	 * @param 	name	DUT name
	 * @return			DUT object
	 */
	public Dut getDutByName(String user, String name);
}

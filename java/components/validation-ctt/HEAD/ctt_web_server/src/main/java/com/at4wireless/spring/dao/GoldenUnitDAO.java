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

/**
 * Interface that manages database GU-related access
 *
 */
import java.util.List;

import com.at4wireless.spring.model.GoldenUnit;

public interface GoldenUnitDAO {
	
	/**
	 * Retrieves Golden Units of a certain user
	 * @param 	user	user whose GUs are going to be retrieves
	 * @return			list of GUs
	 */
	public List<GoldenUnit> list(String user);
	
	/**
	 * Adds a new GU
	 * @param 	gu	GU to be added
	 */
	public void addGu(GoldenUnit gu);
	
	/**
	 * Removes a GU
	 * @param 	idGolden	ID of the GU to be removed
	 */
	public void delGu(int idGolden);
	
	/**
	 * Updates a GU
	 * @param 	gu	information of the GU to be updated
	 */
	public void saveChanges(GoldenUnit gu);
	
	/**
	 * Retrieves a certain GU using its ID in query
	 * @param 	user		GU user
	 * @param 	idGolden	GU ID
	 * @return				GU object
	 */
	public GoldenUnit getGu(String user, int idGolden);
	
	/**
	 * Retrieves a certain GU using its name in query
	 * @param 	user		GU user
	 * @param 	name		GU name
	 * @return				GU object
	 */
	public GoldenUnit getGuByName(String user, String name);
	
	/**
	 * Retrieves assigned	GUs of a certain project
	 * @param 	idProject	project ID
	 * @return				list of GU
	 */
	public List<GoldenUnit> getGuList(int idProject);
	
	public void deleteProjectGus(int idProject);
}

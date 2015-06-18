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

import com.at4wireless.spring.model.TestCase;

/**
 * Interface that manages database TC-related access
 *
 */
public interface TcDAO {
	
	/**
	 * Retrieves all stored testcases
	 * @return	list of testcases
	 */
	public List<TestCase> list();
	
	/**
	 * Retrieves testcases of a certain type and service
	 * @param type			testcases type
	 * @param idService		testcases service framework
	 * @return				list of testcases
	 */
	public List<TestCase> getService(String type, int idService);
	
	/**
	 * Retrieves all stored testcases of a certain certification release
	 * @param 	idCertRel	certification release ID
	 * @return				list of testcases
	 */
	public List<TestCase> list(int idCertRel);
	
	/**
	 * Retrieves all stored testcases allowed by TCCL
	 * @param 	type		testcases type
	 * @param 	idService	tescases service ID
	 * @param 	intList		list of IDs of allowed testcases
	 * @return				list of allowed testcases
	 */
	public List<TestCase> getServiceWithRestriction(String type, int idService, List<Integer> intList);
}

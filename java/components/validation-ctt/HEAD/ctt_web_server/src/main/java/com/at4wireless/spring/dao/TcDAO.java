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

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.TestCase;

public interface TcDAO
{
	/**
	 * Retrieves all stored test cases
	 * 
	 * @return list of test cases
	 */
	public List<TestCase> list();
	
	/**
	 * Retrieves all stored test cases of a certain Certification Release
	 * 
	 * @param certRelID
	 * 			ID of the Certification Release whose test cases are going to be retrieved
	 * 
	 * @return list of test cases
	 */
	public List<TestCase> listByCertRel(int certRelID);
	
	/**
	 * Retrieves test cases of a certain type and service
	 * 
	 * @param type
	 * 			test cases' type
	 * @param serviceID
	 * 			test cases' Service Framework
	 * 
	 * @return list of test cases
	 */
	public List<TestCase> getByTypeAndService(String type, BigInteger serviceID);
	
	/**
	 * Retrieves all stored testcases allowed by TCCL
	 * 
	 * @param type
	 * 			testcases type
	 * @param serviceID
	 * 			tescases service ID
	 * @param testCaseIDsFromCR
	 * 			list of IDs of allowed testcases
	 * 
	 * @return list of allowed testcases
	 */
	public List<TestCase> getServiceWithRestriction(String type, BigInteger serviceID, List<BigInteger> testCaseIDsFromCR);
	
	public void assignTestCasesToCertificationRelease(int certRelID);
	
	public void add(TestCase newTestCase);
	
	public void update(TestCase testCase);
}

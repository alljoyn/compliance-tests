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
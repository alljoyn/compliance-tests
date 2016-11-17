/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCase;

/**
 * Interface with methods that allow testcases controller to communicate with database
 *
 */
public interface TestCaseService {
	
	/**
	 * Returns applicable testcases of a certain project
	 * 
	 * @param 	p			project
	 * @param 	map			map that contains ICS configuration
	 * @return				applicable testcases
	 */
	public List<TestCase> load(Project p, Map<String,String[]> map);
	
	/**
	 * Returns all testcases of a certain service framework
	 * 
	 * @param 	type		project type
	 * @param 	idService	service ID
	 * @return				list of testcases
	 */
	public List<TestCase> getService(String type, BigInteger idService);
	
	/**
	 * Returns all stored testcases
	 * 
	 * @return	list of testcases
	 */
	public List<TestCase> list();
	
	/**
	 * Returns all stored testcases of a certain certification release
	 * 
	 * @param 	idCertRel	project certification release
	 * @return				list of testcases
	 */
	public List<TestCase> list(int idCertRel);
	
	/**
	 * Returns testcases allowed by TCCL
	 * 
	 * @param 	idTccl	applied TCCL
	 * @return			list of allowed testcases
	 */
	public List<Integer> getEnabled(int idTccl);
	
	/**
	 * Returns testcases not allowed by TCCL
	 * @param 	idTccl	applied TCCL
	 * @return			list of not allowed testcases
	 */
	public List<BigInteger> getDisabled(int idTccl);
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @param	results			location of the results XML
	 * @return					data to be included in Test Report
	 */
	public List<String> pdfData(String configuration, String results);
	
	/**
	 * Returns data to be included in ZIP file
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @param 	results			location of the results XML
	 * @return					data to be included in ZIP file
	 */
	public List<String> zipData(String configuration, String results);
	
	/**
	 * Checks if all applicable testcases have been executed
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @param 	results			location of the results XML
	 * @return					true if all testcases have been executed, false otherwise
	 */
	public boolean ranAll(String configuration, String results);
	
	/**
	 * Returns the date and time of the last execution of a certain testcase
	 * 
	 * @param 	tcName		testcase name
	 * @param 	results		location of the results XML
	 * @return				last execution
	 */
	public String lastExecution(String tcName, String results);
	
	public List<Integer> getConfigured(Project p);
	
	public String add(TestCase testCase);
}
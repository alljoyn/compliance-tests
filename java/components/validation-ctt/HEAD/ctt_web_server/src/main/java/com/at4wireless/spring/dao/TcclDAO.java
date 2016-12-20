/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

public interface TcclDAO
{
	/**
	 * Retrieves all stored TCCLs
	 * 
	 * @return list of TCCL
	 */
	public List<Tccl> list();
	
	/**
	 * Returns the number of TCCLs stored of a certain certification release
	 * 
	 * @param certRel
	 * 			Certification Release whose number of TCCL is going to be returned
	 * 
	 * @return number of TCCLs
	 */
	public int getNumber(String certRel);
	
	/**
	 * Adds a new TCCL into database
	 * 
	 * @param newTccl
	 * 			TCCL to be added
	 * 
	 * @return ID of the TCCL added
	 */
	public int add(Tccl newTccl);
	
	public void storeAssociatedTestCases(int tcclID, List<String> types, List<Boolean> enables, List<BigInteger> testcaseIDs);
	
	/**
	 * Removes stored testcases of a TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL whose testcases are going to be removed
	 */
	public void deleteList(int idTccl);
	
	/**
	 * Removes a TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL to be removed
	 */
	public void deleteTccl(int idTccl);
	
	/**
	 * Retrieves stored testcases from a certain TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL
	 * 
	 * @return List of testcases
	 */
	public List<TestCaseTccl> getList(int idTccl);
	
	/**
	 * Updates a TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL
	 * @param date
	 * 			date of modification
	 */
	public void updateTccl(int idTccl, java.sql.Timestamp date);
	
	/**
	 * Retrieves all TCCL from a certain Certification Release
	 * 
	 * @param idCertRel
	 * 			ID of the Certification Release
	 * 
	 * @return List of TCCL
	 */
	public List<Tccl> listByCR(int idCertRel);
	
	/**
	 * Retrieves testcase IDs from a certain TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL
	 * 
	 * @return List of testcase IDs
	 */
	public List<Integer> getIds(int idTccl);
	
	/**
	 * Retrieves IDs of disabled testcases from a certain TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL
	 * 
	 * @return List of testcase IDs
	 */
	public List<BigInteger> getIdsDisabled(int idTccl);
	
	/**
	 * Returns the name of a TCCL
	 * 
	 * @param idTccl
	 * 			ID of the TCCL
	 * 
	 * @return name of the TCCL
	 */
	public String getTcclName(int idTccl);
}
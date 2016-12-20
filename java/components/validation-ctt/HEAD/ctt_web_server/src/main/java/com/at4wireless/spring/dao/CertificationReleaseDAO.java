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

import com.at4wireless.spring.model.CertificationRelease;

public interface CertificationReleaseDAO
{
	/**
	 * Retrieves from database the list of available certification release (DEBUG and RELEASE)
	 * 
	 * @return list of CR
	 */
	public List<CertificationRelease> list();
	
	/**
	 * Retrieves from database the list of available certification release (RELEASE)
	 * 
	 * @return list of CR
	 */
	public List<CertificationRelease> listRelease();
	
	/**
	 * Retrieves a certain Certification Release using its ID in query
	 * 
	 * @param certRelID
	 * 			ID of the Certification Release to be retrieved
	 * 
	 * @return the required Certification Release if exists, null otherwise
	 */
	public CertificationRelease getByID(int certRelID);
	
	/**
	 * Retrieves a certain Certification Release's description using its name in the query
	 * 
	 * @param certRelName
	 * 			Name of the Certification Release whose description is going to be retrieved
	 * 
	 * @return the required Certification Release's description if exists, null otherwise
	 */
	public String getDescription(String certRelName);
	
	/**
	 * Retrieves from database all available test cases in a certification release
	 * 
	 * @param certRelID
	 * 			if of the CR whose test cases are going to be returned
	 * 
	 * @return list of test cases
	 */
	public List<BigInteger> getTestCasesByID(int certRelID);
	
	/**
	 * Adds a new Certification Release to the database
	 * 
	 * @param certificationRelease
	 * 			information about the Certification Release to be added
	 * 
	 * @return ID of the newly added Certification Release
	 */
	public int add(CertificationRelease certificationRelease);
	
	/**
	 * Changes the value of a Certification Release from DEBUG to RELEASE using its name in the query
	 * 
	 * @param certRelName
	 * 			Name of the Certification Release that is going to be changed
	 */
	public void fromDebugToRelease(String certRelName);
	
	/**
	 * Updates the description field of a Certification Release using its name in the query
	 * 
	 * @param certRelName
	 * 			name of the Certification Release whose description is going to be updated
	 * @param description
	 * 			text to be set as description of the target Certification Release
	 */
	public void updateDescription(String certRelName, String description);
	
	/**
	 * Checks if a certain Certification Release exists
	 * 
	 * @param certRelName
	 * 			Name of the Certification Release whose status is going to be checked
	 * 
	 * @return true if the Certification Release exists, false otherwise
	 */
	public boolean exists(String certRelName);
	
	/**
	 * Checks if a certain Certification Release is RELEASE version
	 * 
	 * @param certRelName
	 * 			Name of the Certification Release whose status is going to be checked
	 * 
	 * @return true if the Certification Release is RELEASE, false otherwise
	 */
	public boolean isRelease(String certRelName);
}
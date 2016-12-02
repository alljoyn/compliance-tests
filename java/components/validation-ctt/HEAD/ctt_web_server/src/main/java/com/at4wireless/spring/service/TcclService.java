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

package com.at4wireless.spring.service;

import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

/**
 * Interface with methods that allow tccl controller to communicate with database
 *
 */
public interface TcclService {
	
	/**
	 * Returns list of all defined TCCL
	 * @return	TCCL list
	 */
	public List<Tccl> list();
	
	/**
	 * Creates a new TCCL
	 * @param 	map		map with all necessary information for the creation of the TCCL
	 * @return			new TCCL object
	 */
	public Tccl create(Map<String, String[]> map);
	
	/**
	 * Removes a certain TCCL
	 * 
	 * @param 	idTccl	ID of the TCCL to be removed
	 */
	public void delete(int idTccl);
	
	/**
	 * Returns the list of testcases of a certain TCCL
	 * @param 	idTccl	ID of the TCCL to be returned
	 * @return			list ot testcases
	 */
	public List<TestCaseTccl> getTccl(int idTccl);
	
	/**
	 * Updates the list of testcases of a certain TCCL
	 * @param 	map		map with the list of testcases to update database
	 * @return			modified TCCL object
	 */
	public Tccl update(Map<String, String[]> map);
	
	/**
	 * Returns all TCCL of a certain certification release
	 * @param 	idCertRel	ID of the certification release whose TCCLs are going to be listed
	 * @return				list of TCCL
	 */
	public List<Tccl> listByCR(int idCertRel);
	
	public void addCertificationReleaseIfNotExists(String certificationRelease, String packageVersion, String description) throws Exception;
	
	public String getTcclName(int idTccl);
}
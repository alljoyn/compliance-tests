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

import com.at4wireless.spring.model.CertificationRelease;

/**
 * Interface with methods that allow controllers to communicate with certification release table
 *
 */
public interface CertificationReleaseService
{
	/**
	 * Returns available Certification Releases (DEBUG and RELEASE)
	 * 
	 * @return	list of Certification Releases
	 */
	public List<CertificationRelease> list();
	
	/**
	 * Returns available Certification Releases (RELEASE)
	 * 
	 * @return	list of Certification Releases
	 */
	public List<CertificationRelease> listReleaseVersions();
	
	/**
	 * Retrieves the description of a certain Certification Release
	 * 
	 * @param certRelName
	 * 			name of the Certification Release whose description is going to be returned
	 * 
	 * @return description if exists, null otherwise
	 */
	public String getCertificationReleaseDescription(String certRelName);
	
	/**
	 * Retrieves the name of a certain Certification Release
	 * 
	 * @param certRelID
	 * 			ID of the Certification Release whose name is going to be returned
	 * 
	 * @return name if exists, null otherwise
	 */
	public String getCertificationReleaseName(int certRelID);
	
	/**
	 * Checks if a certain Certification Release is RELEASE version
	 * 
	 * @param certRelID
	 * 			ID of the Certification Release to be checked
	 * 
	 * @return true if RELEASE, false if DEBUG
	 */
	public boolean isReleaseVersion(int certRelID);
}
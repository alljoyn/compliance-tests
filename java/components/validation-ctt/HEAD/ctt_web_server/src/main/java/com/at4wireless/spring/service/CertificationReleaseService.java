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
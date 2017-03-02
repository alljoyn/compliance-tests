/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *      Project (AJOSP) Contributors and others.
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

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.Ixit;

/**
 * Interface with methods that allow ixit controller to communicate with database
 *
 */
public interface IxitService {
	
	/**
	 * Returns IXIT values of an certain project
	 * 
	 * @param 	services		project services
	 * @param 	isConfigured	project configuration condition
	 * @param 	configuration	location of the configuration XML
	 * @return					list of IXIT with default or previously configured values 
	 */
	public List<Ixit> load(List<BigInteger> services, boolean isConfigured, String configuration);
	
	/**
	 * Gets IXIT of a certain service
	 * 
	 * @param 	idService	ID of the service whose IXIT are going to be returned
	 * @return				service IXIT
	 */
	public List<Ixit> getService(int idService);
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @return					data to include in Test Report
	 */
	public List<String> pdfData(String configuration);
	
	public String add(Ixit ixit);
}
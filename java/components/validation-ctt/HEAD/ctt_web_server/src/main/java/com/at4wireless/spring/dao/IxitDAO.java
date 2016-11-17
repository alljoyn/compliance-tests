/*******************************************************************************
 *  *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Ixit;

public interface IxitDAO
{
	/**
	 * Retrieves the list of IXIT
	 * 
	 * @return list of IXIT
	 */
	public List<Ixit> list();
	
	/**
	 * Retrieves an IXIT using its name in query
	 * 
	 * @param name
	 * 			name of the IXIT to be retrieved
	 * 
	 * @return retrieved IXIT
	 */
	public Ixit get(String name);
	
	/**
	 * Retrieves a list of IXIT of a certain service framework
	 * 
	 * @param serviceID
	 * 			ID of the Service Framework whose IXIT are going to be retrieved
	 * 
	 * @return list of IXIT
	 */
	public List<Ixit> getByService(int serviceID);
	
	/**
	 * Creates a new IXIT
	 * 
	 * @param newIxit
	 * 			information of the new IXIT to be added to database
	 */
	public void add(Ixit newIxit);
}
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

import com.at4wireless.spring.model.Category;
import com.at4wireless.spring.model.GoldenUnit;

/**
 * Interface with methods that allow golden unit controller to communicate with database
 *
 */
public interface GoldenUnitService {
	/**
	 * Stores a Golden Unit
	 * 
	 * @param 	gu	Golden Unit information to be stored
	 * @return		true if GU is successfully stored, false if GU exists
	 */
	public GoldenUnit create(GoldenUnit gu);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param 	username	user whose GUs have to be loaded
	 * @return				GU list of this user
	 */
	public List<GoldenUnit> getTableData(String username);
	
	/**
	 * Returns a certain Golden Unit of a given user formatted to be displayed in form
	 * 
	 * @param 	username	user whose GUs have to be loaded
	 * @param 	idGu		id of the requested Golden Unit
	 * @return				requested Golden Unit, if exists
	 */
	public GoldenUnit getFormData(String username, BigInteger idGu);
	
	/**
	 * Updates a Golden Unit
	 * 
	 * @param 	gu	Golden Unit information to be updated
	 * @return		true if Golden Unit is successfully updated, false otherwise
	 */
	public GoldenUnit update(GoldenUnit gu);
	
	/**
	 * Deletes a Golden Unit
	 * 
	 * @param 	username	user whose Golden Unit is going to be deleted
	 * @param 	idGu		ID of the Golden Unit to be deleted
	 * @return				true if Golden Unit is successfully removed, false otherwise
	 */
	public boolean delete(String username, BigInteger idGu);
	
	/**
	 * Returns a list with defined device categories for interoperability testing
	 * 
	 * @return	list of categories
	 */
	public List<Category> getCategories();
	
	/**
	 * Returns the Golden Units assigned to a project
	 * 
	 * @param 	idProject	ID of the project whose assigned GUs are going to be returned
	 * @return				assigned GUs
	 */
	public List<GoldenUnit> getGuList(int idProject);
	
	/**
	 * Checks if a golden unit with input name already exists
	 * @param	username	authenticated user
	 * @param	name		name to check
	 * @param	id			id to check
	 * @return				true if exists, false otherwise
	 */
	public boolean exists(String username, String name, BigInteger id);
	
	public Category getCategoryById(int idCategory);
	
	public void deleteProjectGus(int idProject);
}
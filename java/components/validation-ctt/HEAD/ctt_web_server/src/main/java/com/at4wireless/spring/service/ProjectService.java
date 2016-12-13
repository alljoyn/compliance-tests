/*******************************************************************************
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright 2016 Open Connectivity Foundation and Contributors to
 *      AllSeen Alliance. All rights reserved.
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

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;

/**
 * Interface with methods that allow project controller to communicate with database
 *
 */
public interface ProjectService {
	
	/**
	 * Stores a project
	 * 
	 * @param 	p	project information to be stored
	 * @return		true if project is successfully stored, false if project exists
	 */
	public Project create(Project p);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param 	username	user whose projects have to be loaded
	 * @return				project list of this user
	 */
	public List<Project> getTableData(String username);
	
	/**
	 * Returns a certain project of a given user formatted to be displayed in form
	 * 
	 * @param 	username	user whose projects have to be loaded
	 * @param 	idProject	id of the requested project
	 * @return				requested project, if exists
	 */
	public Project getFormData(String username, int idProject);
	
	/**
	 * Updates a project
	 * 
	 * @param 	p	project information to be updated
	 * @return		true if project is successfully updated, false otherwise
	 */
	public Project update(Project p);
	
	/**
	 * Deletes a project
	 * 
	 * @param 	username	user whose project is going to be deleted
	 * @param 	idProject	id of the project to be deleted
	 * @return				true if project is successfully removed, false otherwise
	 */
	public boolean delete(String username, int idProject);

	/**
	 * Saves project configuration location
	 * 
	 * @param 	idProject	id of the project
	 * @param 	url			location of the configuration file	
	 */
	public void configProject(int idProject, String url);
	
	/**
	 * Saves project results location
	 * 
	 * @param 	idProject	id of the project
	 * @param 	url			location of the results file
	 */
	public void resultProject(int idProject, String url);
	
	/**
	 * Assigns DUT to a project
	 * 
	 * @param 	username	user whose project is going to be modified
	 * @param 	project		project object with DUT ID
	 * @return				type of project
	 */
	public String setDut(String username, Project project);
	
	/**
	* Assigns GU to a project
	 * 
	 * @param 	username	user whose project is going to be modified
	 * @param 	project		project object with GU ID
	 */
	public void setGu(String username, Project project);
	
	/**
	 * Returns the list of project formatted to be sent via REST
	 * 
	 * @param 	username	user whose project list is going to be returned
	 * @return				list of projects
	 */
	public List<RestProject> getRestData (String username);
	
	/**
	 * Retrieves all projects of a certain user
	 * 
	 * @param 	username	user
	 * @return				list of project
	 */
	public List<Project> list(String username);
	
	/**
	 * Clears configuration of the projects with a certain assigned DUT
	 * 
	 * @param 	username	projects user
	 * @param 	idDut		DUT ID
	 * @return				true if projects have been cleared, false otherwise
	 */
	public boolean clearConfigByDut(String username, int idDut);
	
	/**
	 * Returns all supported services of a certain project
	 * 
	 * @param 	idProject	project ID
	 * @return				List of service IDs
	 */
	public List<BigInteger> getServicesData(int idProject);
	
	/**
	 * Returns all project-related data to be added to the Test Report
	 * 
	 * @param 	username	project username
	 * @param 	idProject	project ID
	 * @return				data to be added to the Test Report
	 */
	public List<String> pdfData(String username, int idProject);
	
	/**
	 * Saves project Test Report location
	 * 
	 * @param 	idProject	project ID
	 * @param 	path		location of the Test Report file
	 */
	public void testReport(int idProject, String path);
	
	/**
	 * Checks if a project with input name already exists
	 * @param	username	authenticated user
	 * @param	name		name to check
	 * @param	id			id to check
	 * @return				true if exists, false otherwise
	 */
	public boolean exists(String username, String name, int id);
}
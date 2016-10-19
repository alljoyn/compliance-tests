/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Project;

/**
 * Interface that manages database project-related access
 *
 */
public interface ProjectDAO
{
	/**
	 * Retrieves projects associated to a certain user
	 * 
	 * @param username
	 * 			user whose projects have to be retrieved
	 * 
	 * @return user's stored projects
	 */
	public List<Project> list(String username);
	
	/**
	 * Retrieves a certain project by ID
	 * 
	 * @param username
	 * 			name of the user whose project is going to be retrieved
	 * @param projectID
	 * 			ID of the project to be retrieved
	 * 
	 * @return project information if exists, null otherwise
	 */
	public Project getByID(String username, int projectID);
	
	/**
	 * Retrieves a certain project by Name
	 * 
	 * @param username
	 * 			name of the user whose project is going to be retrieved
	 * @param projectName
	 * 			name of the project to be retrieved
	 * 
	 * @return project information if exists, null otherwise
	 */
	public Project getByName(String username, String projectName);
	
	/**
	 * Retrieves all projects with a certain assigned DUT
	 * 
	 * @param username
	 * 			name of the user whose project is going to be retrieved
	 * @param dutID
	 * 			ID of the DUT assigned to the project
	 * 
	 * @return the list of projects whose DUT is the one chosen
	 */
	public List<Project> getByDUT(String username, int dutID);
	
	/**
	 * Retrieves the names of the Golden Units associated to a certain project
	 * 
	 * @param projectID
	 * 			ID of the project whose Golden Units are going to be returned
	 * 
	 * @return Golden Unit names list
	 */
	public List<String> getGoldenUnitsByProjectID(int projectID);
	
	/**
	 * Adds a project entry to database
	 * 
	 * @param newProject
	 * 			project information to be stored
	 * 
	 * @return ID of the new entry
	 */
	public int add(Project newProject);
	
	/**
	 * Updates a project
	 * 
	 * @param project
	 * 			project object with the information to be updated
	 */
	public void update(Project project);
	
	/**
	 * Drops a project entry from database
	 * 
	 * @param projectID
	 * 			ID of the project to be deleted
	 */
	public void delete(int projectID);
	
	/**
	 * Assigns a DUT to a project
	 * 
	 * @param project
	 * 			project object with the DUT to be assigned
	 */
	public void setDut(Project project);
	
	public void assignGoldenUnits(Project project);
	
	public void unassignGoldenUnits(int projectId);
	
	/**
	 * Stores project configuration location
	 * 
	 * @param projectID
	 * 			ID of the project whose configuration path is going to be set
	 * @param configurationPath
	 * 			location of the configuration file
	 */
	public void setConfig(int projectID, String configurationPath);
	
	/**
	 * Stores project results location
	 * 
	 * @param projectID
	 * 			ID of the project whose results path is going to be set
	 * @param resultsPath
	 * 			location of the results file
	 */
	public void setResults(int projectID, String resultsPath);
	
	/**
	 * Stores project test report location
	 * 
	 * @param projectID
	 * 			ID of the project whose test report path is going to be set
	 * @param testReportPath
	 * 			location of the test report file
	 */
	public void setTestReport(int projectID, String testReportPath);
}
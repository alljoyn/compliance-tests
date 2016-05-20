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
public interface ProjectDAO {
	
	/**
	 * Searches user's project into database
	 * 
	 * @param 	user	user whose projects have to be loaded	
	 * @return			user's assigned project
	 */
	public List<Project> list(String user);
	
	/**
	 * Searches GU associated to a project and returns their names
	 * @param 	idProject	id of the project whose GU have to be returned
	 * @return				GU names list
	 */
	public List<String> getGoldenByName(int idProject);
	
	/**
	 * Adds a project entry to database
	 * 
	 * @param 	project	project information to be stored
	 * @return 			ID of the new entry
	 */
	public int addProject(Project project);
	
	/**
	 * Drops a project entry from database
	 * 
	 * @param 	projectId	ID of the project to be deleted
	 */
	public void delProject(int projectId);
	
	/**
	 * Stores project configuration location
	 * 
	 * @param 	idProject	project ID
	 * @param 	url			location of the configuration file
	 */
	public void configProject(String idProject, String url);
	
	/**
	 * Stores project results location
	 * 
	 * @param 	idProject	project ID
	 * @param 	url			location of the results file
	 */
	public void resultProject(int idProject, String url);
	
	/**
	 * Updates a project
	 * 
	 * @param 	project		project object with the information to be updated
	 */
	public void saveChanges(Project project);
	
	/**
	 * Assigns a DUT to a project
	 * @param 	project		project object with the DUT to be assigned
	 */
	public void setDut(Project project);
	
	public void unassignGoldenUnits(int projectId);
	
	public void assignGoldenUnits(Project project);
	
	/**
	 * Retrieves a certain project by id
	 * 
	 * @param 	user		project user
	 * @param 	idProject	project id
	 * @return				project data
	 */
	public Project getProject(String user, int idProject);
	
	/**
	 * Retrieves a certain project by name
	 * 
	 * @param 	user		project user
	 * @param 	name		project name
	 * @return				project data
	 */
	public Project getProjectByName(String user, String name);
	
	/**
	 * Retrieves all project with a certain assigned DUT
	 * 
	 * @param 	user	project user
	 * @param 	idDut	project assigned DUT
	 * @return			list of projects
	 */
	public List<Project> getProjectByDut(String user, int idDut);
	
	/**
	 * Stores project test report location
	 * 
	 * @param 	idProject	project ID
	 * @param 	path		location of the test report file
	 */
	public void setTestReport(int idProject, String path);

}

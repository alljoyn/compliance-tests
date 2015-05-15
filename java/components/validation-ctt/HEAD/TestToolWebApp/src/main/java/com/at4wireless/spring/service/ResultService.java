package com.at4wireless.spring.service;

import java.util.List;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCaseResult;

/**
 * Interface with methods that allow result controller to communicate with database
 *
 */
public interface ResultService {
	
	/**
	 * Returns available testcase results of a given project
	 * 
	 * @param 	p	project whose results are going to be returned
	 * @return		project results
	 */
	public List<TestCaseResult> getResults(Project p);
	
	/**
	 * Creates Test Report of a given project
	 * 
	 * @param 	username	project user
	 * @param 	p			project	
	 * @return				true if Test Report is successfully created, false otherwise
	 */
	public boolean createTestReport(String username, Project p);
}

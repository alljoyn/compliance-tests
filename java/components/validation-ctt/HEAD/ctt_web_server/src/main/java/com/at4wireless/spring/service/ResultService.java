package com.at4wireless.spring.service;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;

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
	public boolean createTestReport(String username, Project p, String imgPath);
	
	public boolean allTestCasesRun(String username, int idProject);
	
	public List<String> createZipFile(String username, int idProject, SecretKey aesSecretKey) throws IOException;
	
	public String uploadZipFileToCawt(String username, String cri, int idProject, String cawtUrl, String cawtSecret) throws Exception;
	
	public void downloadZipFile(HttpServletResponse response, String username, int idProject) throws IOException;
	
	public void downloadPdfFile(HttpServletResponse response, String testReportPath) throws IOException;
}

package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;

public interface ProjectService {

	public void configProject(String idProject, String url);
	public void resultProject(int idProject, String url);
	public String setDut(String username, Project project);
	public void setGu(String username, Project project);
	
	public boolean delete(String username, int idProject);
	public boolean update(Project p);
	public Project getFormData(String username, int idProject);
	public boolean create(Project p);
	public List<Project> getTableData(String username);
	public List<RestProject> getRestData (String username);
	public List<Project> list(String username);
	public boolean clearConfigByDut(String username, int idDut);
	//public boolean clearConfigByGu(String username, int idGu);
	public List<BigInteger> getServicesData(int idProject);
	public List<String> pdfData(String username, int idProject);
	public void testReport(int idProject, String path);
}

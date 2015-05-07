package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.Project;

public interface ProjectDAO {
	public List<Project> list(String user);
	public int addProject(Project project);
	public void delProject(int projectId);
	public void configProject(String idProject, String url);
	public void resultProject(int idProject, String url);
	public void saveChanges(Project project);
	public void setDut(Project project);
	public void setGu(Project project);
	public Project getProject(String user, int idProject);
	public Project getProjectByName(String user, String name);
	public List<Project> getProjectByDut(String user, int idDut);
	//public List<Project> getProjectByGu(String user, int idGu);
	public List<BigInteger> getServices(int idProject);
	public List<String> getServicesByName(int idProject);
	public void setTestReport(int idProject, String path);
	public List<String> getGoldenByName(int idProject);
}

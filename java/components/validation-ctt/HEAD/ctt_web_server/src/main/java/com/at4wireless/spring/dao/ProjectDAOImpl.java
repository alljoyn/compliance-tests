/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.Project;

@Repository
public class ProjectDAOImpl implements ProjectDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Project> list(String user)
	{
		TypedQuery<Project> query = sessionFactory.getCurrentSession()
				.createQuery("from Project where user = :user", Project.class)
				.setParameter("user", user);
		
		return query.getResultList();
	}
	
	@Override
	public Project getByID(String username, int projectID)
	{
		TypedQuery<Project> query = sessionFactory.getCurrentSession()
				.createQuery("from Project where user = :user and idProject = :idProject", Project.class)
				.setParameter("user", username)
				.setParameter("idProject", projectID);
		
		Project foundProject = null;
		try
		{
			foundProject = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundProject;
	}
	
	@Override
	public Project getByName(String username, String projectName)
	{
		TypedQuery<Project> query = sessionFactory.getCurrentSession()
				.createQuery("from Project where user = :user and name = :name", Project.class)
				.setParameter("user", username)
				.setParameter("name", projectName);
		
		Project foundProject = null;
		try
		{
			foundProject = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundProject;
	}

	@Override
	public List<Project> getByDUT(String username, int dutID)
	{
		TypedQuery<Project> query = sessionFactory.getCurrentSession()
				.createQuery("from Project where user = :user and idDut = :idDut", Project.class)
				.setParameter("user", username)
				.setParameter("idDut", dutID);
		
		return query.getResultList();
	}
	
	@Override
	public List<String> getGoldenUnitsByProjectID(int projectID)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		@SuppressWarnings("unchecked")
		NativeQuery<String> query = sessionFactory.getCurrentSession()
				.createNativeQuery("select (name) from golden where id_golden in "
						+ "(select id_golden from project_golden where id_project = :idProject)")
				.setParameter("idProject", projectID);
		
		return query.getResultList();
	}
	
	@Override
	public int add(Project newProject)
	{
		sessionFactory.getCurrentSession().save(newProject);
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		int idProject = newProject.getIdProject();
		String[] supportedServices = newProject.getSupportedServices().split("[\\.]+");
		for (int i = 0; i < supportedServices.length; i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into project_services (id_project, id_service) values (:idProject, :idService)")
					.setParameter("idProject", idProject)
					.setParameter("idService", supportedServices[i])
					.executeUpdate();
		}
		
		return idProject;
	}
	
	@Override
	public void update(Project project)
	{
		/*
		 * TODO : Look for a shorter way to update an existent Object
		 * 
		 */
		int idProject = project.getIdProject();
		sessionFactory.getCurrentSession()
				.createQuery("update Project set name = :name, modifiedDate = :modifiedDate, "
						+ "idCertrel = :idCertrel, type = :type, carId = :carId, idTccl = :idTccl where idProject = :idProject")
				.setParameter("name", project.getName())
				.setParameter("modifiedDate", project.getModifiedDate())
				.setParameter("idCertrel", project.getIdCertrel())
				.setParameter("type", project.getType())
				.setParameter("carId", project.getCarId())
				.setParameter("idTccl", project.getIdTccl())
				.setParameter("idProject", idProject)
				.executeUpdate();
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from project_services where id_project = :idProject")
				.setParameter("idProject", idProject)
				.executeUpdate();
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		String[] supportedServices = project.getSupportedServices().split("[\\.]+");
		for (int i = 0; i < supportedServices.length; i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into project_services (id_project, id_service) values (:idProject, :idService)")
					.setParameter("idProject", idProject)
					.setParameter("idService", supportedServices[i])
					.executeUpdate();
		}
	}
	
	@Override
	public void delete(int projectID)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from project_services where id_project = :idProject")
				.setParameter("idProject", projectID)
				.executeUpdate();
		
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from project_golden where id_project = :idProject")
				.setParameter("idProject", projectID)
				.executeUpdate();
		
		sessionFactory.getCurrentSession()
				.createQuery("delete from Project where idProject = :idProject")
				.setParameter("idProject", projectID)
				.executeUpdate();
	}
	
	@Override
	public void setDut(Project project)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update Project set idDut = :idDut where idProject = :idProject")
				.setParameter("idDut", project.getIdDut())
				.setParameter("idProject", project.getIdProject())
				.executeUpdate();
	}
	
	@Override
	public void assignGoldenUnits(Project project)
	{
		int idProject = project.getIdProject();
		String[] goldenUnitIDs = project.getgUnits().split("[\\.]+");
		for (int i = 0; i < goldenUnitIDs.length; i++)
		{
			sessionFactory.getCurrentSession()
					.createNativeQuery("insert into project_golden (id_project, id_golden) values (:idProject, :idGolden)")
					.setParameter("idProject", idProject)
					.setParameter("idGolden", goldenUnitIDs[i])
					.executeUpdate();
		}
	}
	
	@Override
	public void unassignGoldenUnits(int projectId)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("delete from project_golden where id_project = :idProject")
				.setParameter("idProject", projectId)
				.executeUpdate();
	}
	
	@Override
	public void setConfig(int projectID, String configurationPath)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update Project set configuration = :configuration, "
						+ "isConfigured = :isConfigured where idProject = :idProject")
				.setParameter("configuration", configurationPath)
				.setParameter("isConfigured", true)
				.setParameter("idProject", projectID)
				.executeUpdate();
	}
	
	@Override
	public void setResults(int projectID, String resultsPath)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update Project set results = :results, hasResults = :hasResults where idProject = :idProject")
				.setParameter("results", resultsPath)
				.setParameter("hasResults", true)
				.setParameter("idProject", projectID)
				.executeUpdate();
	}

	@Override
	public void setTestReport(int projectID, String testReportPath)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update Project set testReport = :testReport, hasTestReport = :hasTestReport where idProject = :idProject")
				.setParameter("testReport", testReportPath)
				.setParameter("hasTestReport", true)
				.setParameter("idProject", projectID)
				.executeUpdate();
	}
}
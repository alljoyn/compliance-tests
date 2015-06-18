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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.Project;

@Repository
public class ProjectDAOImpl implements ProjectDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Project> list(String user) {
		@SuppressWarnings("unchecked")
		List<Project> listProject = (List<Project>) sessionFactory.getCurrentSession()
				.createCriteria(Project.class)
					.add(Restrictions.like("user", user))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listProject;
	}
	
	@Override
	public Project getProject(String user, int idProject) {
		@SuppressWarnings("unchecked")
		List<Project> listProject = (List<Project>) sessionFactory.getCurrentSession()
				.createCriteria(Project.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("idProject", idProject))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		if(listProject.isEmpty()) {
			return null;
		} else {
			return listProject.get(0);
		}
	}
	
	@Override
	public Project getProjectByName(String user, String name) {
		@SuppressWarnings("unchecked")
		List<Project> listProject = (List<Project>) sessionFactory.getCurrentSession()
				.createCriteria(Project.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("name", name))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		if (listProject.isEmpty()) {
			return null;
		} else {
			return listProject.get(0);
		}
	}

	@Override
	public int addProject(Project project) {
		sessionFactory.getCurrentSession().save(project);

		String[] var = project.getSupportedServices().split("[\\.]+");
		StringBuilder str = new StringBuilder("values ");
		for (int i=0; i<var.length; i++) {
			str.append("("+Integer.toString(project.getIdProject())+","+var[i]+")");
			if(i!=(var.length-1)) str.append(",");
			else str.append(";");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into project_services (id_project,id_service) "
				+str.toString()).executeUpdate();
		
		return project.getIdProject();

	}
	
	@Override
	public void delProject(int projectId) {
		
		sessionFactory.getCurrentSession().createSQLQuery("delete from project_services where id_project="
				+projectId+";").executeUpdate();
		
		sessionFactory.getCurrentSession().createSQLQuery("delete from project_golden where id_project="
				+projectId+";").executeUpdate();
		
		sessionFactory.getCurrentSession().createQuery("delete from Project p where p.idProject ='"+projectId+"'").executeUpdate();
		

	}
	
	@Override
	public void configProject(String idProject, String url) {
		
		int configured = 0;
		if (url!=null) {
			configured = 1;
		} else {
			url="";
		}
		sessionFactory.getCurrentSession().createQuery("update Project set configuration = '"
				+url+"', isConfigured = '"+configured+
				"' where idProject = '"+idProject+"'").executeUpdate();
	}
	
	@Override
	public void resultProject(int idProject, String url) {
		sessionFactory.getCurrentSession().createQuery("update Project set results = '"
				+url+"', hasResults = '1'"+
				" where idProject = '"+idProject+"'").executeUpdate();
	}
	
	@Override
	public void saveChanges(Project project) {
		sessionFactory.getCurrentSession().createQuery("update Project set name = '"+project.getName()
				+"', modifiedDate = '"+project.getModifiedDate()+"', idCertrel = '"+project.getIdCertrel()
				+"', type = '"+project.getType()+"', carId = '"+project.getCarId()
				+"', idTccl = '"+project.getIdTccl()
				+"' where idProject = '"+project.getIdProject()+"'").executeUpdate();
		
		sessionFactory.getCurrentSession().createSQLQuery("delete from project_services where id_project="
				+project.getIdProject()+";").executeUpdate();
		
		String[] var = project.getSupportedServices().split("[\\.]+");
		StringBuilder str = new StringBuilder("values ");
		for (int i=0; i<var.length; i++) {
			str.append("("+Integer.toString(project.getIdProject())+","+var[i]+")");
			if(i!=(var.length-1)) str.append(",");
			else str.append(";");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into project_services (id_project,id_service) "
				+str.toString()).executeUpdate();
	}
	
	@Override
	public void setDut(Project project) {
		sessionFactory.getCurrentSession().createQuery("update Project set idDut = '"
				+project.getIdDut()+"' where idProject = '"+project.getIdProject()+"'").executeUpdate();
	}

	@Override
	public void setGu(Project project) {
		sessionFactory.getCurrentSession().createSQLQuery("delete from project_golden where id_project="
				+project.getIdProject()+";").executeUpdate();
		
		String[] var = project.getgUnits().split("[\\.]+");
		StringBuilder str = new StringBuilder("values ");
		for (int i=0; i<var.length; i++) {
			str.append("("+Integer.toString(project.getIdProject())+","+var[i]+")");
			if(i!=(var.length-1)) str.append(",");
			else str.append(";");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into project_golden (id_project,id_golden) "
				+str.toString()).executeUpdate();
	}

	@Override
	public List<Project> getProjectByDut(String user, int idDut) {
		@SuppressWarnings("unchecked")
		List<Project> listProject = (List<Project>) sessionFactory.getCurrentSession()
				.createCriteria(Project.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("idDut", idDut))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		if (listProject.isEmpty()) {
			return null;
		} else {
			return listProject;
		}
	}

	@Override
	public void setTestReport(int idProject, String path) {
		sessionFactory.getCurrentSession().createQuery("update Project set testReport = '"
				+path+"', hasTestReport = 1"+
				" where idProject = '"+idProject+"'").executeUpdate();
	}

	@Override
	public List<String> getGoldenByName(int idProject) {
		@SuppressWarnings("unchecked")
		List<String> stringList = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select (name) from golden where id_golden in"
						+"(select id_golden from project_golden where id_project="+idProject+");").list();
		return stringList;
	}

}

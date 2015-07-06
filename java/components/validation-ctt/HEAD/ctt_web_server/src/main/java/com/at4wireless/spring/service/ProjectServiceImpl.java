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
package com.at4wireless.spring.service;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.DutDAO;
import com.at4wireless.spring.dao.GoldenUnitDAO;
import com.at4wireless.spring.dao.ProjectDAO;
import com.at4wireless.spring.dao.ServiceFrameworkDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.RestProject;

@Service
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	private ProjectDAO projectDao;
	
	@Autowired
	private CertificationReleaseDAO crDao;
	
	@Autowired
	private DutDAO dutDao;
	
	@Autowired
	private GoldenUnitDAO guDao;
	
	@Autowired
	private TcclDAO tcclDao;
	
	@Autowired
	private ServiceFrameworkDAO serviceDao;
	
	@Override
	@Transactional
	public boolean create(Project p) {
		
		java.sql.Timestamp date = new java.sql.Timestamp(
				new java.util.Date().getTime());

		p.setCreatedDate(date);
		p.setModifiedDate(date);

		if(projectDao.getProjectByName(p.getUser(), p.getName())!=null) {
			return false;
		} else {
			projectDao.addProject(p);
			return true;
		}
	}
	
	@Override
	@Transactional
	public List<Project> getTableData(String username) {
		List<Project> projectList = projectDao.list(username);
		
		for (Project p : projectList) {
			List<String> sList = serviceDao.getServicesByName(p.getIdProject());
			StringBuilder str= new StringBuilder();
			for (int i=0; i<sList.size(); i++) {
				str.append(sList.get(i));
				if(i!=(sList.size()-1)) {
					str.append(", ");
				}
			}
			p.setSupportedServices(str.toString());
		}
		
		for (Project p : projectList) {
			List<String> sList = projectDao.getGoldenByName(p.getIdProject());
			if(!sList.isEmpty()) {
				StringBuilder str= new StringBuilder();
				
				for (int i=0; i<sList.size(); i++) {
					str.append(sList.get(i));
					if(i!=(sList.size()-1)) {
						str.append(", ");
					}
				}
				p.setgUnits(str.toString());
			} else {
				if (p.getType().equals("Conformance")) {
					p.setgUnits("N/A");
				} else {
					p.setgUnits("Not selected");
				}
			}
		}
		
		return projectList;
	}
	
	@Override
	@Transactional
	public Project getFormData(String username, int idProject) {

		Project p = projectDao.getProject(username,idProject);
		List<BigInteger> biList = serviceDao.getServices(p.getIdProject());
		
		StringBuilder str = new StringBuilder();
		for (BigInteger bi : biList)
			str.append(bi.toString() + ".");
		p.setSupportedServices(str.toString());

		return p;
	}
	
	@Override
	@Transactional
	public boolean update(Project p) {
		Project saved = projectDao.getProject(p.getUser(), p.getIdProject());
		if(saved!=null) {
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
			p.setModifiedDate(date);
			String[] var = p.getSupportedServices().split("[\\.]+");
			List<BigInteger> listServices = serviceDao.getServices(saved.getIdProject());
			boolean equals=(var.length==listServices.size());
			int i=0;
			while ((equals)&&(i<var.length)) {
				equals = (Integer.parseInt(var[i])==listServices.get(i).intValue());
				i++;
			}
			
			if((saved.getIdCertrel()!=p.getIdCertrel())
					||(!(saved.getType().equals(p.getType())))
					||(!equals)) {
				if(saved.isIsConfigured()) {	
					try {
						String cfg = saved.getConfiguration();
						if(cfg!=null) {
							File fileTemp = new File(cfg);
							if(fileTemp.exists()) {
								fileTemp.delete();
							}
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
					projectDao.configProject(Integer.toString(saved.getIdProject()), null);
				}
			}
			projectDao.saveChanges(p);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	@Transactional
	public boolean delete(String username, int idProject) {

		Project p = projectDao.getProject(username, idProject);
		if(p!=null) {
			try {
				String cfg = p.getConfiguration();
				if (cfg != null) {
					File filePath = new File(cfg.substring(0,cfg.lastIndexOf(File.separator)));
					
					if (filePath.isDirectory()) {
						File files[] = filePath.listFiles();
						for (int i=0; i<files.length; i++) {
							if (files[i].exists()) {
								files[i].delete();
							}
						}
						filePath.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			projectDao.delProject(idProject);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public void configProject(String idProject, String url) {
		projectDao.configProject(idProject, url);
		
	}

	@Override
	@Transactional
	public void resultProject(int idProject, String url) {
		projectDao.resultProject(idProject, url);
		
	}

	@Override
	@Transactional
	public String setDut(String username, Project project) {
		Project p = projectDao.getProject(username, project.getIdProject());
		
		if(project.getIdDut()!=0) {
			if((p.getIdDut()!=0)&&(p.isIsConfigured())&&(project.getIdDut()!=p.getIdDut())) {
				try {
					String cfg = p.getConfiguration();
					if(cfg!=null) {
						File fileTemp = new File(cfg);
						if(fileTemp.exists()) {
							fileTemp.delete();
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				projectDao.configProject(Integer.toString(project.getIdProject()), null);
			}
			projectDao.setDut(project);
		}
		
		return p.getType();
		
	}

	@Override
	@Transactional
	public void setGu(String username, Project project) {

		if(!project.getgUnits().isEmpty()) {
			projectDao.setGu(project);
		}
		
	}
	
	@Override
	@Transactional
	public List<RestProject> getRestData(String username) {
		List<RestProject> listRestProject = new ArrayList<RestProject>();
		for (Project p : getTableData(username)) {
			RestProject rp = new RestProject();
			
			rp.setIdProject(p.getIdProject());
			rp.setName(p.getName());
			rp.setCreatedDate(p.getCreatedDate());
			rp.setModifiedDate(p.getModifiedDate());
			rp.setType(p.getType());
			rp.setSupportedServices(p.getSupportedServices());
			
			for (CertificationRelease cr : crDao.list()) {
				if (p.getIdCertrel()==cr.getIdCertrel()) {
					rp.setCertRel(cr.getName());
				}
			}
			
			rp.setIsConfigured(p.isIsConfigured());
			rp.setHasResults(p.isHasResults());
			if(p.getIdDut()!=0) {
				rp.setDut(dutDao.getDut(username, p.getIdDut()).getName());
			}
			rp.setGolden(p.getgUnits());
			
			
			listRestProject.add(rp);
		}
		
		return listRestProject;
	}
	
	@Override
	@Transactional
	public List<Project> list(String username) {
		return projectDao.list(username);
	}

	@Override
	@Transactional
	public boolean clearConfigByDut(String username, int idDut) {
		List<Project> lp = projectDao.getProjectByDut(username, idDut);
		if(lp!=null) {
			for (Project p : projectDao.getProjectByDut(username, idDut)) {
				if(p.isIsConfigured()) {	
					try {
						String cfg = p.getConfiguration();
						if(cfg!=null) {
							File fileTemp = new File(cfg);
							if(fileTemp.exists()) {
								fileTemp.delete();
							}
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
					projectDao.configProject(Integer.toString(p.getIdProject()), null);
				}
				p.setIdDut(0);
				projectDao.setDut(p);
			}
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public List<BigInteger> getServicesData(int idProject) {
		return serviceDao.getServices(idProject);
	}
	
	@Override
	@Transactional
	public List<String> pdfData(String username, int idProject) {
		
		Project p = projectDao.getProject(username,idProject);
		List<String> sList = serviceDao.getServicesByName(p.getIdProject());
		
		StringBuilder str = new StringBuilder();
		for (int i=0; i<sList.size(); i++) {
			str.append(sList.get(i));
			if(i<(sList.size()-1)) {
				str.append(", ");
			}
		}
		p.setSupportedServices(str.toString());

		String separator = ": ";
		List<String> listString = new ArrayList<String>();
		listString.add("1. Project Details");
		listString.add("Project Name"+separator+p.getName());
		listString.add("User Name"+separator+p.getUser());
		java.util.Date date = new java.util.Date();
		listString.add("Test Report Creation Date and Time"+separator+date.toString());
		listString.add("Certification Application Request ID"+separator+p.getCarId());
		listString.add("Type of Project"+separator+p.getType());
		listString.add("Supported Services"+separator+p.getSupportedServices());
		listString.add("Certification Release Version"+separator+crDao.getName(p.getIdCertrel())
				+" ("+crDao.getCertificationReleaseDescription(crDao.getName(p.getIdCertrel()))+")");
		listString.add("TCCL Version"+separator+tcclDao.getTcclName(p.getIdTccl()));
		
		return listString;
		
	}

	@Override
	@Transactional
	public void testReport(int idProject, String path) {
		projectDao.setTestReport(idProject, path);
	}

	@Override
	@Transactional
	public boolean exists(String username, String name, int id) {
		
		Project p = projectDao.getProjectByName(username, name);
		if(p==null) {
			return false;
		} else if (p.getIdProject()==id) {
			return false;
		}
		return true;
	}
}

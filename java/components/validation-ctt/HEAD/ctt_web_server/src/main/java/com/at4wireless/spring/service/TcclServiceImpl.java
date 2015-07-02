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
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.TcDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

@Service
public class TcclServiceImpl implements TcclService {

	@Autowired
	private TcclDAO tcclDao;
	
	@Autowired
	private CertificationReleaseDAO certrelDao;
	
	@Autowired
	private TcDAO tcDao;
	
	@Override
	@Transactional
	public List<Tccl> list() {
		return tcclDao.list();
	}

	@Override
	@Transactional
	public Tccl create(Map<String, String[]> map) {
		Tccl tccl = new Tccl();
		
		java.sql.Timestamp date = new java.sql.Timestamp(
				new java.util.Date().getTime());
		int next = tcclDao.getNumber(map.get("certrel[name]")[0].substring(1))+1;
		tccl.setName("TCCL_"+map.get("certrel[name]")[0].substring(1)+"_v0."+next);
		tccl.setCreatedDate(date);
		tccl.setModifiedDate(date);
		tccl.setIdCertrel(Integer.parseInt(map.get("certrel[id]")[0]));
		tccl.setNumTc((map.size()-2)/3);
		tccl.setNameCertrel(certrelDao.getName(Integer.parseInt(map.get("certrel[id]")[0])));
		int idTccl = tcclDao.addTccl(tccl);
		
		if (idTccl!=0) {
			StringBuilder str = new StringBuilder();
			for (int j=0; j<((map.size()-2)/3); j++) {
				String s = "("+idTccl+",'"+map.get("json["+j+"][type]")[0]+"',"
						+map.get("json["+j+"][enabled]")[0]+","+map.get("json["+j+"][id]")[0]+")";
				str.append(s);
				if (j!=(((map.size()-2)/3)-1)) str.append(",");
			}

			tcclDao.saveList(str.toString());
		}
		return tccl;
	}

	@Override
	@Transactional
	public void delete(int idTccl) {
		tcclDao.deleteList(idTccl);
		tcclDao.deleteTccl(idTccl);
		
	}

	@Override
	@Transactional
	public List<TestCaseTccl> getTccl(int idTccl) {
		return tcclDao.getList(idTccl);
	}

	@Override
	@Transactional
	public Tccl update(Map<String, String[]> map) {

		java.sql.Timestamp date = new java.sql.Timestamp(
				new java.util.Date().getTime());
		int idTccl = Integer.parseInt(map.get("idTccl")[0]);
		Tccl tccl = new Tccl();
		tccl.setIdTccl(idTccl);
		tccl.setModifiedDate(date);

		tcclDao.updateTccl(idTccl,date);
		tcclDao.deleteList(idTccl);
		
		if (idTccl!=0) {
			StringBuilder str = new StringBuilder();
			for (int j=0; j<((map.size()-2)/3); j++) {
				String s = "("+idTccl+",'"+map.get("json["+j+"][type]")[0]+"',"
						+map.get("json["+j+"][enabled]")[0]+","+map.get("json["+j+"][id]")[0]+")";
				str.append(s);
				if (j!=(((map.size()-2)/3)-1)) str.append(",");
			}

			tcclDao.saveList(str.toString());
		}
		return tccl;
	}

	@Override
	@Transactional
	public List<Tccl> listByCR(int idCertRel) {
		return tcclDao.listByCR(idCertRel);
	}

	@Override
	@Transactional
	public void addCertificationReleaseIfNotExists(String certificationRelease, String packageVersion,
			String description) throws Exception {
		
		if(!certrelDao.certificationReleaseExists(certificationRelease)) {
			CertificationRelease cRelease = new CertificationRelease();
			cRelease.setName(certificationRelease);
			cRelease.setEnabled(true);
			cRelease.setRelease(isReleaseVersion(packageVersion));
			cRelease.setDescription(description);
			int crId = certrelDao.addCertificationRelease(cRelease);
			tcDao.assignTestCasesToCertificationRelease(crId);
			
			//certrelDao.assignTestCases(certificationRelease);
		} else {
			if (certrelDao.isReleaseVersion(certificationRelease)) {
				if (!isReleaseVersion(packageVersion)) {
					throw new Exception("A release version of the package already exists. Debug versions of "+certificationRelease+" are not allowed");
				}
				else
				{
					certrelDao.updateDescription(certificationRelease, description);
				}
			} else {
				if (isReleaseVersion(packageVersion)) {
					certrelDao.fromDebugToRelease(certificationRelease);
					cleanDebugPackages(certificationRelease);
				}
				certrelDao.updateDescription(certificationRelease, description);
			}
		}
	}
	
	private boolean isReleaseVersion(String packageVersion) {
		return (packageVersion.charAt(0) == 'R');
	}
	
	private void cleanDebugPackages(final String certificationRelease) {
		File folder = new File(File.separator+"Allseen"+File.separator+"Technology");
		File[] files = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(certificationRelease+"_D");
			}
		});
		
		for (final File file : files) {
			if (!file.delete()) {
				System.err.println("Can't remove "+file.getAbsolutePath());
			}
		}
	}
}

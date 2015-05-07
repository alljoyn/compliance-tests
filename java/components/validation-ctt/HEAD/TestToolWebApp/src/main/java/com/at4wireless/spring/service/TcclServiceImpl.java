package com.at4wireless.spring.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CertificationReleaseDAO;
import com.at4wireless.spring.dao.TcclDAO;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

@Service
public class TcclServiceImpl implements TcclService {

	@Autowired
	private TcclDAO tcclDao;
	
	@Autowired
	private CertificationReleaseDAO certrelDao;
	
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

}

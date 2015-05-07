package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.DutDAO;
import com.at4wireless.spring.dao.SampleDAO;
import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Sample;

@Service
public class DutServiceImpl implements DutService {

	@Autowired
	private DutDAO dutDao;
	
	@Autowired
	private SampleDAO sampleDao;
	
	@Override
	@Transactional
	public boolean create(Dut d) {
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		
		//When created, both dates get the same value
		d.setCreatedDate(date);
		d.setModifiedDate(date);

		// if dut's assigned user and context's user are equal
		if(dutDao.getDutByName(d.getUser(), d.getName())!=null) {
			return false;
		} else {
			dutDao.addDut(d);
			sampleDao.addSample(new Sample(d));
			return true;
		}	
	}

	@Override
	@Transactional
	public List<Dut> getTableData(String username) {
		return dutDao.list(username);
	}
	
	@Override
	@Transactional
	public Dut getFormData(String username, int idDut) {
		return dutDao.getDut(username,idDut);
	}
	
	@Override
	@Transactional
	public boolean update(Dut d) {
		if(dutDao.getDut(d.getUser(), d.getIdDut())!=null) {
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
			d.setModifiedDate(date);
			dutDao.saveChanges(d);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public boolean delete(String username, int idDut) {
		if (dutDao.getDut(username, idDut)!=null) {
			for(Sample s : sampleDao.list(idDut)) {
				sampleDao.delSample(s.getIdSample());
			}
			dutDao.delDut(idDut);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public List<Sample> getSampleData(int idDut) {
		return sampleDao.list(idDut);
	}

	@Override
	@Transactional
	public void createSample(Sample s) {
		sampleDao.addSample(s);	
	}
	
	@Override
	@Transactional
	public void deleteSample(int idSample) {
		sampleDao.delSample(idSample);
	}

	@Override
	@Transactional
	public Sample getSampleFormData(int idSample) {
		// TODO Auto-generated method stub
		return sampleDao.getSample(idSample);
	}

	@Override
	@Transactional
	public void updateSample(Sample s) {
		sampleDao.saveChanges(s);
	}

	@Override
	@Transactional
	public void setValues(String username, int idDut, List<Ixit> listIxit) {
		Dut d = dutDao.getDut(username, idDut);
		Sample s = sampleDao.list(idDut).get(0);
		listIxit.get(1).setValue(s.getAppId());
		listIxit.get(3).setValue(d.getName());
		listIxit.get(4).setValue(s.getDeviceId());
		listIxit.get(6).setValue(d.getManufacturer());
		listIxit.get(7).setValue(d.getModel());
		listIxit.get(8).setValue(s.getSwVer());
		listIxit.get(10).setValue(s.getHwVer());
		
	}

}

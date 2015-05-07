package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.CategoryDAO;
import com.at4wireless.spring.dao.GoldenUnitDAO;
import com.at4wireless.spring.model.Category;
import com.at4wireless.spring.model.GoldenUnit;
import com.at4wireless.spring.model.Sample;

@Service
public class GoldenUnitServiceImpl implements GoldenUnitService {

	@Autowired
	private GoldenUnitDAO guDao;
	
	@Autowired
	private CategoryDAO categoryDao;
	
	@Override
	@Transactional
	public boolean create(GoldenUnit gu) {
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		
		//When created, both dates get the same value
		gu.setCreatedDate(date);
		gu.setModifiedDate(date);

		// if dut's assigned user and context's user are equal
		if(guDao.getGuByName(gu.getUser(), gu.getName())!=null) {
			return false;
		} else {
			guDao.addGu(gu);
			return true;
		}
	}

	@Override
	@Transactional
	public List<GoldenUnit> getTableData(String username) {
		return guDao.list(username);
	}

	@Override
	@Transactional
	public GoldenUnit getFormData(String username, int idGu) {
		return guDao.getGu(username,idGu);
	}

	@Override
	@Transactional
	public boolean update(GoldenUnit gu) {
		if(guDao.getGu(gu.getUser(), gu.getIdGolden())!=null) {
			java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
			gu.setModifiedDate(date);
			guDao.saveChanges(gu);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public boolean delete(String username, int idGu) {
		if (guDao.getGu(username, idGu)!=null) {
			guDao.delGu(idGu);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	@Transactional
	public List<Category> getCategories() {
		return categoryDao.list();
	}

	@Override
	@Transactional
	public List<String> getGuList(int idProject) {
		return guDao.getGuList(idProject);
	}
}

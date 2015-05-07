package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.ServiceDAO;
import com.at4wireless.spring.model.ServiceFramework;

@Service
public class ServiceFrameworkServiceImpl implements ServiceFrameworkService {
	@Autowired
	private ServiceDAO serviceDao;
	
	@Override
	@Transactional
	public List<ServiceFramework> list() {
		return serviceDao.list();
	}
}

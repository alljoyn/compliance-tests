package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.TestCase;

public interface TcDAO {
	public List<TestCase> list();
	public List<TestCase> getService(String type, int idService);
	public List<TestCase> list(int idCertRel);
	public List<TestCase> getServiceWithRestriction(String type, int idService, List<Integer> intList);
}

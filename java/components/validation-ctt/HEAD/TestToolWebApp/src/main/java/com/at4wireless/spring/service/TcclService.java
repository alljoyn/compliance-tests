package com.at4wireless.spring.service;

import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

public interface TcclService {
	public List<Tccl> list();
	public Tccl create(Map<String, String[]> map);
	public void delete(int idTccl);
	public List<TestCaseTccl> getTccl(int idTccl);
	public Tccl update(Map<String, String[]> map);
	public List<Tccl> listByCR(int idCertRel);
}

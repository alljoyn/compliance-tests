package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.TestCase;

public interface TestCaseService {
	public List<TestCase> load(List<BigInteger> services, Map<String,String[]> map, String type, int idTccl);
	public List<TestCase> getService(String type, int idService);
	public List<TestCase> list();
	public List<TestCase> list(int idCertRel);
	public List<Integer> getEnabled(int idTccl);
	public List<Integer> getDisabled(int idTccl);
	public List<String> pdfData(String configuration, String results);
	public List<String> zipData(String configuration, String results);
	public boolean ranAll(String configuration, String results);
}

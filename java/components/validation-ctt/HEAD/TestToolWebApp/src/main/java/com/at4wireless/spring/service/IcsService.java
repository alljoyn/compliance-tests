package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Result;

public interface IcsService {
	public List<Ics> load(List<BigInteger> services, boolean isConfigured, String configuration);
	public List<Result> check(List<BigInteger> services, Map<String,String[]> map);
	public List<Ics> getService(int idService);
	public List<String> pdfData(String configuration);
}

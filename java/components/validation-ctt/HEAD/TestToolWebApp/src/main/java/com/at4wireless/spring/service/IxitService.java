package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.Ixit;

public interface IxitService {
	public List<Ixit> load(List<BigInteger> services, boolean isConfigured, String configuration);
	public List<Ixit> getService(int idService);
	public List<String> pdfData(String configuration);
}

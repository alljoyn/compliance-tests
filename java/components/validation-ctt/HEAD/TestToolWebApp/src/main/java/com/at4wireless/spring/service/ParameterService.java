package com.at4wireless.spring.service;

import java.util.List;

import com.at4wireless.spring.model.Parameter;

public interface ParameterService {
	public List<Parameter> list();
	public List<Parameter> load(boolean isConfigured, String configuration);
	public List<String> pdfData(String configuration);
}

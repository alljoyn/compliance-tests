package com.at4wireless.spring.service;

import java.util.List;

import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Sample;

public interface DutService {
	public boolean create(Dut d);
	public List<Dut> getTableData(String username);
	public Dut getFormData(String username, int idDut);
	public boolean update(Dut d);
	public boolean delete(String username, int idDut);
	public List<Sample> getSampleData(int idDut);
	public void createSample(Sample s);
	public void deleteSample(int idSample);
	public Sample getSampleFormData(int idSample);
	public void updateSample(Sample s);
	public void setValues(String username, int idDut, List<Ixit> listIxit);
}

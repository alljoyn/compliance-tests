package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Sample;

public interface SampleDAO {
	public List<Sample> list(int dut);
	public void addSample(Sample sample);
	public void delSample(int idSample);
	public void saveChanges(Sample sample);
	public Sample getSample(int idSample);
}

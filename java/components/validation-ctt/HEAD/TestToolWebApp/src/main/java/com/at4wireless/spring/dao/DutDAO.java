package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Dut;

public interface DutDAO {
	public List<Dut> list(String user);
	public void addDut(Dut dut);
	public void delDut(int dutId);
	public void saveChanges(Dut dut);
	public Dut getDut(String user, int idDut);
	public Dut getDutByName(String user, String name);
}

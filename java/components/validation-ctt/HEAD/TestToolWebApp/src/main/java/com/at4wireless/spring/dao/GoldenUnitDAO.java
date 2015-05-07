package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.GoldenUnit;

public interface GoldenUnitDAO {
	public List<GoldenUnit> list(String user);
	public void addGu(GoldenUnit gu);
	public void delGu(int idGolden);
	public void saveChanges(GoldenUnit gu);
	public GoldenUnit getGu(String user, int idGolden);
	public GoldenUnit getGuByName(String user, String name);
	public List<String> getGuList(int idProject);
}

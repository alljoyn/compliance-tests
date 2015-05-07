package com.at4wireless.spring.service;

import java.util.List;

import com.at4wireless.spring.model.Category;
import com.at4wireless.spring.model.GoldenUnit;

public interface GoldenUnitService {
	public boolean create(GoldenUnit gu);
	public List<GoldenUnit> getTableData(String username);
	public GoldenUnit getFormData(String username, int idGu);
	public boolean update(GoldenUnit gu);
	public boolean delete(String username, int idGu);
	public List<Category> getCategories();
	public List<String> getGuList(int idProject);
}

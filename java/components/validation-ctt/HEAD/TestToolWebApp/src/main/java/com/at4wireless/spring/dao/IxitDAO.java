package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Ixit;

public interface IxitDAO {
	public List<Ixit> list();
	public List<Ixit> getService(int idService);
}

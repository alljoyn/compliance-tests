package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Ics;

public interface IcsDAO {
	public List<Ics> list();
	public List<Ics> getService(int idService);
}

package com.at4wireless.spring.common;

import java.util.Collection;

public class DataTablesData
{
	public int draw;
	
	public int recordsTotal;
	public int recordsFiltered;
	
	@SuppressWarnings("rawtypes")
	public Collection data;
	
	public DataTablesData()
	{
		
	}
}
package com.at4wireless.spring.model.dto;

import com.at4wireless.spring.model.TestCase;

public class TestCaseDT
{
	public TestCaseDT(TestCase testcase, boolean selected)
	{
		this.id = testcase.getIdTC().intValue();
		this.name = testcase.getName();
		this.description = testcase.getDescription();
		this.selected = selected;
	}
	
	private int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	private boolean selected;
	public boolean isSelected() { return selected; }
	public void setSelected(boolean selected) { this.selected = selected; }
}
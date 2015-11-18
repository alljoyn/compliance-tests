package com.at4wireless.spring.model.dto;

import com.at4wireless.spring.model.TestCase;

public class TestCaseDT
{
	private int id;
	private String name;
	private String description;
	private boolean selected;
	
	public TestCaseDT(TestCase testcase, boolean selected)
	{
		this.id = testcase.getIdTC();
		this.name = testcase.getName();
		this.description = testcase.getDescription();
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
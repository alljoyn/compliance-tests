/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/

package com.at4wireless.spring.model;

public class Result
{	
	public Result(int id, int idService, boolean result)
	{
		this.id = id;
		this.idService = idService;
		this.result = result;
	}
	
	private int id;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	private int idService;
	public int getIdService() { return idService; }
	public void setIdService(int idService) { this.idService = idService; }
	
	private boolean result;
	public boolean isResult() { return result; }
	public void setResult(boolean result) { this.result = result; }
}
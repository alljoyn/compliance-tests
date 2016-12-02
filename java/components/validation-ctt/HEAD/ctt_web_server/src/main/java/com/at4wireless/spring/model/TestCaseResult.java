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

public class TestCaseResult
{
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	private String execTimestamp;
	public String getExecTimestamp() { return execTimestamp; }
	public void setExecTimestamp(String execTimestamp) { this.execTimestamp = execTimestamp; }
	
	private String version;
	public String getVersion() { return version; }
	public void setVersion(String version) { this.version = version; }
	
	private String verdict;
	public String getVerdict() { return verdict; }
	public void setVerdict(String verdict) { this.verdict = verdict; }
	
	private String log;
	public String getLog() { return log; }
	public void setLog(String log) { this.log = log; }
}
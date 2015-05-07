package com.at4wireless.spring.model;

public class TestCaseResult {
	private String name;
	private String description;
	private String execTimestamp;
	private String version;
	private String verdict;
	private String log;
	
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
	public String getExecTimestamp() {
		return execTimestamp;
	}
	public void setExecTimestamp(String execTimestamp) {
		this.execTimestamp = execTimestamp;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVerdict() {
		return verdict;
	}
	public void setVerdict(String verdict) {
		this.verdict = verdict;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	
	
}

package com.at4wireless.spring.model;

public class Result {
	private int id;
	private int idService;
	private boolean result;
	
	public Result(int id, int idService, boolean result) {
		this.id = id;
		this.idService = idService;
		this.result = result;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdService() {
		return idService;
	}
	public void setIdService(int idService) {
		this.idService = idService;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
}

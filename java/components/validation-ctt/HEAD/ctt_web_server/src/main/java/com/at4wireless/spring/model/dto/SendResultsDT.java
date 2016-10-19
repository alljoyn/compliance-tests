package com.at4wireless.spring.model.dto;

import java.util.List;

public class SendResultsDT
{
	public SendResultsDT(int resultCode, List<String> resultMessages)
	{
		this.resultCode = resultCode;
		this.resultMessages = resultMessages;
	}
	
	private int resultCode;
	public int getResultCode() { return resultCode; }
	public void setResultCode(int resultCode) { this.resultCode = resultCode; }
	
	private List<String> resultMessages;
	public List<String> getResultMessages() { return resultMessages; }
	public void setResultMessages(List<String> resultMessages) { this.resultMessages = resultMessages; }
}

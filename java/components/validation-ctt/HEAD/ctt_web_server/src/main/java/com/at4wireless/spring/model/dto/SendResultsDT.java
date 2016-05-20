package com.at4wireless.spring.model.dto;

import java.util.List;

public class SendResultsDT
{
	private int resultCode;
	private List<String> resultMessages;
	
	public SendResultsDT(int resultCode, List<String> resultMessages)
	{
		this.resultCode = resultCode;
		this.resultMessages = resultMessages;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public List<String> getResultMessages() {
		return resultMessages;
	}

	public void setResultMessages(List<String> resultMessages) {
		this.resultMessages = resultMessages;
	}
}

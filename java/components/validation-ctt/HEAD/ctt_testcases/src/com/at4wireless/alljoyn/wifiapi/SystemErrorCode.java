package com.at4wireless.alljoyn.wifiapi;

public enum SystemErrorCode 
{
	ERROR_SUCCESS(0),
	ERROR_NOT_ENOUGH_MEMORY(8),
	ERROR_INVALID_PARAMETER(87),
	ERROR_REMOTE_SESSION_LIMIT_EXCEEDED(1220);
	
	private int value;
	
	private SystemErrorCode(int value)
	{
		this.value = value;
	}
}

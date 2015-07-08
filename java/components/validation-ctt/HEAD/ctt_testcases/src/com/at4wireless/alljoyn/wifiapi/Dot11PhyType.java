package com.at4wireless.alljoyn.wifiapi;

public enum Dot11PhyType
{
	UNKNOWN,
	ANY,
	FHSS,
	DSSS,
	IRBASEBAND,
	OFDM,
	HRDSSS,
	ERP,
	HT,
	VHT,
	IHV_start, //0x80000000
	IHV_end //0xffffffff
}

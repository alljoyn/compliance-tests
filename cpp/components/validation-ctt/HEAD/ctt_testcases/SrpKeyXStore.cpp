/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

******************************************************************************/
#include "stdafx.h"
#include "SrpKeyXStore.h"

const char* SrpKeyXStore::getPincode(std::string t_PeerName)
{
	return m_PincodeStore.empty() ? nullptr : m_PincodeStore.at(t_PeerName);
}

void SrpKeyXStore::setPincode(std::string t_PeerName, const char* t_Pincode)
{
	std::map<std::string, const char*>::iterator iterator = m_PincodeStore.find(t_PeerName);
	if (iterator != m_PincodeStore.end())
	{
		iterator->second = t_Pincode;
	}
	else
	{
		m_PincodeStore.insert(std::pair<std::string, const char*>(t_PeerName, t_Pincode));
	}
}
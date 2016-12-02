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
#pragma once

#include <alljoyn\SessionListener.h>

class ServiceAvailabilityHandler : public ajn::SessionListener
{
public:
	bool waitForSessionLost(const long);
private:
	unsigned short m_countdown_latch{ 1 };
	void SessionLost(ajn::SessionId, SessionLostReason);
};
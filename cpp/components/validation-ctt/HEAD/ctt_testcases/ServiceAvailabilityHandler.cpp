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
#include "ServiceAvailabilityHandler.h"

#include <thread>

void ServiceAvailabilityHandler::SessionLost(ajn::SessionId t_SessionId, SessionLostReason t_Reason)
{
	//LOG(INFO) << "SessionLost sessionId = " << t_session_id << ", Reason = " << t_reason;
	--m_countdown_latch;
}

bool ServiceAvailabilityHandler::waitForSessionLost(const long t_Timeout)
{
	const clock_t beginTime = clock();

	while (m_countdown_latch == 1 && (clock() - beginTime < t_Timeout))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	return m_countdown_latch == 0;
}
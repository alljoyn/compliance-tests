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

#include <alljoyn\ApplicationStateListener.h>

class Claim_ApplicationStateListener : public ajn::ApplicationStateListener
{
public:
	Claim_ApplicationStateListener();
	virtual void State(const char* busName, const qcc::KeyInfoNISTP256& publicKeyInfo, ajn::PermissionConfigurator::ApplicationState state);

	bool stateChanged;
};
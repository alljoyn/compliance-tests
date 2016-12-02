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
#include "OnboardingSignalListenerImpl.h"

OnboardingSignalListenerImpl::OnboardingSignalListenerImpl()
{
}

OnboardingSignalListenerImpl::~OnboardingSignalListenerImpl()
{
}


void OnboardingSignalListenerImpl::ConnectionResultSignalReceived(short connectionResultCode, const qcc::String& connectionResultMessage)
{
	LOG(DEBUG) << "connectionResultCode=" << connectionResultCode;
	LOG(DEBUG) << "connectionResultMessage=" << connectionResultMessage.c_str();
}
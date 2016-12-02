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

#include <alljoyn\BusAttachment.h>

class BusAttachmentMgr
{
	public:
		BusAttachmentMgr();
		void create(const std::string&, const bool);
		QStatus connect();
		QStatus advertise();
		void release();
		ajn::BusAttachment* getBusAttachment();
		QStatus registerBusObject(ajn::BusObject, const bool);
		const char* getBusUniqueName();

	protected:
		ajn::BusAttachment* createBusAttachment(const std::string&, const bool);
	
	private:
		static bool m_Initialized;
		ajn::BusAttachment* m_BusAttachment{ nullptr };
		qcc::String m_DaemonName;
		qcc::String m_AdvertisedName;
};
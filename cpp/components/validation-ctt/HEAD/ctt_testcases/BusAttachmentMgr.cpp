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
#include "BusAttachmentMgr.h"

#include <alljoyn\DBusStdDefines.h>

using namespace ajn;
using namespace std;

bool BusAttachmentMgr::m_Initialized = false;

BusAttachmentMgr::BusAttachmentMgr()
{
	if (!m_Initialized)
	{
		m_Initialized = true;
	}
}

void BusAttachmentMgr::create(const string& t_BusApplicationName, const bool t_Policy)
{
	LOG(INFO) << "Creating BusAttachment";
	m_BusAttachment = createBusAttachment(t_BusApplicationName, t_Policy);
}

QStatus BusAttachmentMgr::connect()
{
	LOG(INFO) << "Starting BusAttachment";
	QStatus status = m_BusAttachment->Start();

	if (status != QStatus::ER_OK)
	{
		LOG(INFO) << "Unable to start busAttachment " << QCC_StatusText(status);
		return status;
	}

	LOG(INFO) << "Connecting BusAttachment";
	status = m_BusAttachment->Connect();

	if (status != QStatus::ER_OK)
	{
		LOG(INFO) << "Unable to connect busAttachment " << QCC_StatusText(status);
	}

	return status;
}

QStatus BusAttachmentMgr::advertise()
{
	m_DaemonName = "org.alljoyn.BusNode_" + m_BusAttachment->GetGlobalGUIDString();
	QStatus status = m_BusAttachment->RequestName(m_DaemonName.c_str(), 
		DBUS_NAME_FLAG_DO_NOT_QUEUE);

	if (status != QStatus::ER_OK)
	{
		LOG(ERROR) << "Failed to requestName '" << m_DaemonName.c_str() << "': " 
			<< QCC_StatusText(status);
		m_DaemonName.clear();
		return status;
	}

	m_AdvertisedName = "quiet@" + m_DaemonName;
	status = m_BusAttachment->AdvertiseName(m_AdvertisedName.c_str(), 
		TRANSPORT_IP);

	if (status != ER_OK)
	{
		LOG(ERROR) << "Failed to advertise name " << m_DaemonName.c_str();
		m_AdvertisedName.clear();
	}

	return status;
}

void BusAttachmentMgr::release()
{
	if (!m_AdvertisedName.empty())
	{
		QStatus status = m_BusAttachment->CancelAdvertiseName(m_AdvertisedName.c_str(),
			TRANSPORT_IP);

		if (status != ER_OK)
		{
			LOG(INFO) << "cancelAdvertiseName returned: %s" << QCC_StatusText(status);
		}

		m_AdvertisedName.clear();
	}

	if (!m_DaemonName.empty())
	{
		QStatus status = m_BusAttachment->ReleaseName(m_DaemonName.c_str());

		if (status != ER_OK)
		{
			LOG(INFO) << "releaseName returned: %s" << QCC_StatusText(status);
		}

		m_DaemonName.clear();
	}

	if (m_BusAttachment != nullptr)
	{
		LOG(INFO) << "Disconnecting BusAttachment";
		m_BusAttachment->ClearKeyStore();
		m_BusAttachment->Disconnect();
		m_BusAttachment->Stop();
		m_BusAttachment->Join();
		delete m_BusAttachment;
	}
	
}

BusAttachment* BusAttachmentMgr::createBusAttachment(const string& t_ApplicationName, 
	const bool t_Policy)
{
	return new BusAttachment(t_ApplicationName.c_str(), t_Policy);
}

BusAttachment* BusAttachmentMgr::getBusAttachment()
{
	return m_BusAttachment;
}

QStatus BusAttachmentMgr::registerBusObject(BusObject t_BusObject, const bool t_Secured)
{
	QStatus status = m_BusAttachment->RegisterBusObject(t_BusObject, t_Secured);

	if (status != QStatus::ER_OK)
	{
		LOG(ERROR) << "Failed to register object: " << QCC_StatusText(status);
	}

	return status;
}

const char* BusAttachmentMgr::getBusUniqueName()
{
	return m_BusAttachment->GetUniqueName().c_str();
}

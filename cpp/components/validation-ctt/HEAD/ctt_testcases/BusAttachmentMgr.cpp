/******************************************************************************
* Copyright AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

void BusAttachmentMgr::create(const string& t_BusApplicationName, bool t_Policy)
{
    LOG(INFO) << "Creating BusAttachment";
    m_BusAttachment = createBusAttachment(t_BusApplicationName, t_Policy);
}

QStatus BusAttachmentMgr::connect()
{
    LOG(INFO) << "Starting BusAttachment";
    QStatus status = m_BusAttachment->Start();
    if (ER_OK != status) {
        LOG(INFO) << "Unable to start busAttachment " << QCC_StatusText(status);
        return status;
    }

    LOG(INFO) << "Connecting BusAttachment";
    status = m_BusAttachment->Connect();
    if (ER_OK != status) {
        LOG(INFO) << "Unable to connect busAttachment " << QCC_StatusText(status);
    }

    return status;
}

QStatus BusAttachmentMgr::advertise()
{
    m_DaemonName = "org.alljoyn.BusNode_" + m_BusAttachment->GetGlobalGUIDString();
    QStatus status = m_BusAttachment->RequestName(m_DaemonName.c_str(), 
        DBUS_NAME_FLAG_DO_NOT_QUEUE);

    if (ER_OK != status) {
        LOG(ERROR) << "Failed to requestName '" << m_DaemonName.c_str() << "': " 
            << QCC_StatusText(status);
        m_DaemonName.clear();
        return status;
    }

    m_AdvertisedName = "quiet@" + m_DaemonName;
    status = m_BusAttachment->AdvertiseName(m_AdvertisedName.c_str(), TRANSPORT_IP);
    if (ER_OK != status) {
        LOG(ERROR) << "Failed to advertise name " << m_DaemonName.c_str();
        m_AdvertisedName.clear();
    }

    return status;
}

void BusAttachmentMgr::release()
{
    if (!m_AdvertisedName.empty())
    {
        QStatus status = m_BusAttachment->CancelAdvertiseName(m_AdvertisedName.c_str(), TRANSPORT_IP);
        if (ER_OK != status) {
            LOG(INFO) << "cancelAdvertiseName returned: %s" << QCC_StatusText(status);
        }

        m_AdvertisedName.clear();
    }

    if (!m_DaemonName.empty())
    {
        QStatus status = m_BusAttachment->ReleaseName(m_DaemonName.c_str());
        if (ER_OK != status)
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
    bool t_Policy)
{
    return new BusAttachment(t_ApplicationName.c_str(), t_Policy);
}

BusAttachment* BusAttachmentMgr::getBusAttachment()
{
    return m_BusAttachment;
}

QStatus BusAttachmentMgr::registerBusObject(BusObject t_BusObject, bool t_Secured)
{
    QStatus status = m_BusAttachment->RegisterBusObject(t_BusObject, t_Secured);
    if (ER_OK != status) {
        LOG(ERROR) << "Failed to register object: " << QCC_StatusText(status);
    }

    return status;
}

AJ_PCSTR BusAttachmentMgr::getBusUniqueName()
{
    return m_BusAttachment->GetUniqueName().c_str();
}
/******************************************************************************
 * 
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 * 
 *    SPDX-License-Identifier: Apache-2.0
 * 
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
 * 
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 * 
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

#include "stdafx.h"
#include "CdmTestSuite.h"

#include <interfaces/controller/operation/RemoteControllabilityIntfController.h>
#include <interfaces/controller/operation/RemoteControllabilityIntfControllerListener.h>

class RemoteControllabilityListener : public RemoteControllabilityIntfControllerListener
{
public:
    CdmSemaphore m_event;
    CdmSemaphore m_eventSignal;
    QStatus m_status;
    bool m_isControllable;
    bool m_isControllableSignal;
    qcc::String m_errorName;
    qcc::String m_errorMessage;



    virtual void OnResponseGetIsControllable(QStatus status, const qcc::String& objectPath, const bool isControllable, void* context) override
    {
        m_status = status;
        m_isControllable = isControllable;
        m_event.SetEvent();
    }
    virtual void OnIsControllableChanged(const qcc::String& objectPath, const bool isControllable) override
    {
        m_isControllableSignal = isControllable;
        m_eventSignal.SetEvent();
    }
};

TEST_F(CdmTestSuite, CDM_v1_RemoteControllability)
{
    WaitForControllee(REMOTE_CONTROLLABILITY_INTERFACE);
    for (size_t i = 0; i < m_interfaces.size(); i++) {
        TEST_LOG_OBJECT_PATH(m_interfaces[i].objectPath);

        auto listener = mkRef<RemoteControllabilityListener>();
        auto interface = m_controller->CreateInterface("org.alljoyn.SmartSpaces.Operation.RemoteControllability", m_interfaces[i].busName,
                                                                qcc::String(m_interfaces[i].objectPath.c_str()), m_interfaces[i].sessionId, listener);
        auto controller = std::dynamic_pointer_cast<RemoteControllabilityIntfController>(interface);
        QStatus status = ER_FAIL;

        TEST_LOG_1("Get initial values for all properties.");
        {
            TEST_LOG_2("Retrieve the IsControllable property.");
            status = controller->GetIsControllable();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
        }
    }
}

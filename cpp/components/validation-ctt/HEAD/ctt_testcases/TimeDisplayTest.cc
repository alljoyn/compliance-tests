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
#include <algorithm>

#include <interfaces/controller/userinterfacesettings/TimeDisplayIntfController.h>
#include <interfaces/controller/userinterfacesettings/TimeDisplayIntfControllerListener.h>

class TimeDisplayListener : public TimeDisplayIntfControllerListener
{
public:
    CdmSemaphore m_event;
    CdmSemaphore m_eventSignal;
    QStatus m_status;

    uint8_t m_displayTimeformat;
    uint8_t m_displayTimeformatSignal;

    qcc::String m_errorName;
    qcc::String m_errorMessage;

    std::vector<uint8_t> m_supportedTimeFormats;
    std::vector<uint8_t> m_supportedTimeFormatsSignal;

    virtual void OnResponseGetDisplayTimeFormat(QStatus status, const qcc::String& objectPath, const uint8_t timeformat, void* context) override
    {
        m_status = status;
        m_displayTimeformat = timeformat;
        m_event.SetEvent();
    }

    virtual void OnResponseSetDisplayTimeFormat(QStatus status, const qcc::String& objectPath, void* context) override
    {
        m_status = status;
        m_event.SetEvent();
    }

    virtual void OnResponseGetSupportedDisplayTimeFormats(QStatus status, const qcc::String& objectPath, const std::vector<uint8_t>& supportedTimeFormats, void* context) override
    {
        m_supportedTimeFormats = supportedTimeFormats;
        m_status = status;
        m_event.SetEvent();
    }

    virtual void OnDisplayTimeFormatChanged(const qcc::String& objectPath, const uint8_t timeformat) override
    {
        m_displayTimeformatSignal = timeformat;
        m_eventSignal.SetEvent();
    }

    virtual void OnSupportedDisplayTimeFormatsChanged(const qcc::String& objectPath, const std::vector<uint8_t>& supportedTimeFormats) override
    {
        m_supportedTimeFormatsSignal = supportedTimeFormats;
        m_eventSignal.SetEvent();
    }

    QStatus GetUnsupportedTimeFormat(uint8_t& unsupportedTimeFormat)
    {
        for(uint8_t i = 0; ; i++)
        {
            std::vector<uint8_t>::iterator it;
            it = std::find(m_supportedTimeFormats.begin(),m_supportedTimeFormats.end(), i);
            if(it == m_supportedTimeFormats.end())
            {
                unsupportedTimeFormat = i;
                return ER_OK;
            }
        }
        return ER_FAIL;
    }
};

TEST_F(CdmTestSuite, CDM_v1_TimeDisplay)
{
    WaitForControllee(TIME_DISPLAY_INTERFACE);
    for (size_t i = 0; i < m_interfaces.size(); i++) {
        TEST_LOG_OBJECT_PATH(m_interfaces[i].objectPath);

        auto listener = mkRef<TimeDisplayListener>();
        auto interface = m_controller->CreateInterface("org.alljoyn.SmartSpaces.UserInterfaceSettings.TimeDisplay", m_interfaces[i].busName, qcc::String(m_interfaces[i].objectPath.c_str()),
                                                                m_interfaces[i].sessionId, listener);
        auto controller = std::dynamic_pointer_cast<TimeDisplayIntfController>(interface);
        QStatus status = ER_FAIL;

        TEST_LOG_1("Get initial values for all properties.");
        {
            TEST_LOG_2("Retrieve the DisplayTimeFormat property.");
            status = controller->GetDisplayTimeFormat();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);

            TEST_LOG_2("Retrieve SupportedDisplayTimeFormats property.");
            status = controller->GetSupportedDisplayTimeFormats();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
        }

        const uint8_t initDisplayTimeFormat = listener->m_supportedTimeFormats[0];
        TEST_LOG_1("Initialize all read-write properties.");
        {
            TEST_LOG_2("Set the DisplayTimeFormat property to the 1st item of the m_supportedTimeFormats.");
            if (listener->m_displayTimeformat != initDisplayTimeFormat) {
                status = controller->SetDisplayTimeFormat(initDisplayTimeFormat);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);

                EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
                listener->m_eventSignal.ResetEvent();
                EXPECT_EQ(listener->m_displayTimeformatSignal, initDisplayTimeFormat);
            }
        }

        TEST_LOG_1("Set properties to invalid value.");
        {
            TEST_LOG_2("Set the DisplayTimeFormat property to value outside SupportedDisplayTimeFormats.");
            uint8_t unsupportedTimeFormat = 0;
            status = listener->GetUnsupportedTimeFormat(unsupportedTimeFormat);
            if(status == ER_OK)
            {
                status = controller->SetDisplayTimeFormat(unsupportedTimeFormat);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_NE(listener->m_status, ER_OK);

                TEST_LOG_2("Get the DisplayTimeFormat property.");
                status = controller->GetDisplayTimeFormat();
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);
                EXPECT_EQ(listener->m_displayTimeformat, initDisplayTimeFormat);
            }
        }

        TEST_LOG_1("Set properties to valid value.");
        {
            TEST_LOG_2("If SupportedDisplayTimeFormats > 1, set the DisplayTimeFormat property to the 2nd item of the SupportedDisplayTimeFormats.");
            if (listener->m_supportedTimeFormats.size() > 1) {
                const uint8_t validDisplayTimeFormat = listener->m_supportedTimeFormats[1];
                status = controller->SetDisplayTimeFormat(validDisplayTimeFormat);

                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);

                TEST_LOG_3("Wait the PropertiesChanged signal for the DisplayTimeFormat property.");
                EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
                listener->m_eventSignal.ResetEvent();
                EXPECT_EQ(listener->m_displayTimeformatSignal, validDisplayTimeFormat);

                TEST_LOG_3("Get the DisplayTimeFormat property.");
                status = controller->GetDisplayTimeFormat();
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);
                EXPECT_EQ(listener->m_displayTimeformat, validDisplayTimeFormat);
            }
        }
    }
}

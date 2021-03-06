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

#include <interfaces/controller/operation/AlertsIntfController.h>
#include <interfaces/controller/operation/AlertsIntfControllerListener.h>
using namespace std;
class AlertsListener : public AlertsIntfControllerListener
{
public:
    CdmSemaphore m_event;
    CdmSemaphore m_eventSignal;
    QStatus m_status;
    std::vector<AlertsInterface::AlertRecord> m_Alerts;
    std::vector<AlertsInterface::AlertRecord> m_AlertsSignal;
    std::vector<AlertsInterface::AlertCodesDescriptor> m_AlertCodesDescription;

    qcc::String m_errorName;
    qcc::String m_errorMessage;

    virtual void OnResponseGetAlerts(QStatus status, const qcc::String& objectPath, const std::vector<AlertsInterface::AlertRecord>& alerts, void* context) override
    {
        m_Alerts = alerts;
        m_status = status;
        m_event.SetEvent();
    }


    virtual void OnAlertsChanged(const qcc::String& objectPath, const std::vector<AlertsInterface::AlertRecord>& alerts) override
    {
        m_AlertsSignal = alerts;
        m_eventSignal.SetEvent();
    }


    virtual void OnResponseGetAlertCodesDescription(QStatus status, const qcc::String& objectPath, const std::vector<AlertsInterface::AlertCodesDescriptor>& descriptions,
                                                      void* context, const char* errorName, const char* errorMessage) override
    {
        m_AlertCodesDescription = descriptions;
        if (status != ER_OK) {
            if (errorName) {
                m_errorName = errorName;
            }
            if (errorMessage) {
                m_errorMessage = errorMessage;
            }
        }
        m_status = status;
        m_event.SetEvent();
    }


    virtual void OnResponseAcknowledgeAlert(QStatus status, const qcc::String& objectPath, void* context, const char* errorName, const char* errorMessage) override
    {
        if (status != ER_OK) {
            if (errorName) {
                m_errorName = errorName;
            }
            if (errorMessage) {
                m_errorMessage = errorMessage;
            }
        }
        m_status = status;
        m_event.SetEvent();
    }


    virtual void OnResponseAcknowledgeAllAlerts(QStatus status, const qcc::String& objectPath, void* context, const char* errorName, const char* errorMessage) override
    {
        if (status != ER_OK) {
            if (errorName) {
                m_errorName = errorName;
            }
            if (errorMessage) {
                m_errorMessage = errorMessage;
            }
        }
        m_status = status;
        m_event.SetEvent();
    }
};



uint16_t findInvalidAlertCode(const std::vector<AlertsInterface::AlertRecord>& alerts)
{
    uint16_t invalidAlertCode = 0x8000;
    size_t i =0;

    while (invalidAlertCode != (uint16_t) -1){
        for(i = 0 ; i < alerts.size(); i ++){
            if(alerts[i].alertCode == invalidAlertCode)
                break;
        }
        if (i == alerts.size()){
            return invalidAlertCode;
        }
        invalidAlertCode++;
    }
    return (uint16_t) -1;
}



TEST_F(CdmTestSuite, CDM_v1_Alerts)
{
    WaitForControllee(ALERTS_INTERFACE);
    for (size_t i = 0; i < m_interfaces.size(); i++) {
        TEST_LOG_OBJECT_PATH(m_interfaces[i].objectPath);

        auto listener = mkRef<AlertsListener>();
        auto interface = m_controller->CreateInterface("org.alljoyn.SmartSpaces.Operation.Alerts", m_interfaces[i].busName,
                                                                qcc::String(m_interfaces[i].objectPath.c_str()), m_interfaces[i].sessionId, listener);
        auto controller = std::dynamic_pointer_cast<AlertsIntfController>(interface);
        QStatus status = ER_FAIL;

        TEST_LOG_1("Get initial values for all properties.");
        {
            TEST_LOG_2("Retrieve the Alerts property.");
            status = controller->GetAlerts();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);

        }

        TEST_LOG_1("Call method with invalid param.");
        {
            TEST_LOG_2("Call GetAlertCodesDescription method with \"Invalid_lang\".");
            qcc::String invalidLang = "Invalid_lang";
            status = controller->GetAlertCodesDescription(invalidLang);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_NE(listener->m_status, ER_OK);
            EXPECT_STREQ(listener->m_errorName.c_str(), CdmInterface::GetInterfaceErrorName(LANGUAGE_NOT_SUPPORTED).c_str());

            TEST_LOG_2("Call AcknowledgeAlert method with \"OutOfAlertCodes\" value.");
            uint16_t invalidAlertCode = findInvalidAlertCode(listener->m_Alerts);
            status = controller->AcknowledgeAlert(invalidAlertCode);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);

        }

        TEST_LOG_1("Call method with valid param.");
        {
            TEST_LOG_2("Call GetAlertCodesDescription method with \"en\".");
            qcc::String validLang = "en";
            status = controller->GetAlertCodesDescription(validLang);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);

            TEST_LOG_2("If size of the Alerts > 0, Call AcknowledgeAlert method with 1st value from Alerts property.");
            if (listener->m_Alerts.size() > 0) {
                uint16_t validCode = listener->m_Alerts[0].alertCode;
                status = controller->AcknowledgeAlert(validCode);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);
            }

            TEST_LOG_2("Call AcknowledgeAllAlerts method.");
            status = controller->AcknowledgeAllAlerts();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);

        }
    }
}

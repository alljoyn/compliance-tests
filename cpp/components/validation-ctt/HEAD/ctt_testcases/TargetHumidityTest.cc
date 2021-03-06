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

#include <interfaces/controller/environment/TargetHumidityIntfController.h>
#include <interfaces/controller/environment/TargetHumidityIntfControllerListener.h>
#include <algorithm>

static const uint8_t MIN_HUMIDITY = 0;
static const uint8_t MAX_HUMIDITY = 100;

class TargetHumidityListener : public TargetHumidityIntfControllerListener
{
public:
    CdmSemaphore m_event;
    CdmSemaphore m_eventSignal;

    QStatus m_status;
    qcc::String m_errorName;
    qcc::String m_errorMessage;

    uint8_t m_targetValue;
    uint8_t m_minValue;
    uint8_t m_maxValue;
    uint8_t m_stepValue;
    std::vector<uint8_t> m_selectableHumidityLevels;
    uint8_t m_targetValueSignal;
    uint8_t m_minValueSignal;
    uint8_t m_maxValueSignal;
    uint8_t m_stepValueSignal;
    std::vector<uint8_t> m_selectableHumidityLevelsSignal;

    virtual void OnResponseSetTargetValue(QStatus status, const qcc::String& objectPath, void* context) override
    {
        m_status = status;
        m_event.SetEvent();
    }

    virtual void OnResponseGetTargetValue(QStatus status, const qcc::String& objectPath, const uint8_t value, void* context) override
    {
        m_status = status;
        m_targetValue = value;
        m_event.SetEvent();
    }

    virtual void OnResponseGetMinValue(QStatus status, const qcc::String& objectPath, const uint8_t value, void* context) override
    {
        m_status = status;
        m_minValue = value;
        m_event.SetEvent();
    }

    virtual void OnResponseGetMaxValue(QStatus status, const qcc::String& objectPath, const uint8_t value, void* context) override
    {
        m_status = status;
        m_maxValue = value;
        m_event.SetEvent();
    }

    virtual void OnResponseGetStepValue(QStatus status, const qcc::String& objectPath, const uint8_t value, void* context) override
    {
        m_status = status;
        m_stepValue = value;
        m_event.SetEvent();
    }

    virtual void OnResponseGetSelectableHumidityLevels(QStatus status, const qcc::String& objectPath, const std::vector<uint8_t>& value, void* context) override
    {
        m_status = status;
        m_selectableHumidityLevels = value;
        m_event.SetEvent();
    }

    virtual void OnTargetValueChanged(const qcc::String& objectPath, const uint8_t value) override
    {
        m_targetValueSignal = value;
        m_eventSignal.SetEvent();
    }

    virtual void OnMinValueChanged(const qcc::String& objectPath, const uint8_t value) override
    {
        m_minValueSignal = value;
        m_eventSignal.SetEvent();
    }

    virtual void OnMaxValueChanged(const qcc::String& objectPath, const uint8_t value) override
    {
        m_maxValueSignal = value;
        m_eventSignal.SetEvent();
    }

    virtual void OnStepValueChanged(const qcc::String& objectPath, const uint8_t value) override
    {
        m_stepValueSignal = value;
        m_eventSignal.SetEvent();
    }

    virtual void OnSelectableHumidityLevelsChanged(const qcc::String& objectPath, const std::vector<uint8_t>& value) override
    {
        m_selectableHumidityLevelsSignal = value;
        m_eventSignal.SetEvent();
    }
};

uint8_t getInvalidValue(std::vector<uint8_t>& levels) {
    for (int i = MIN_HUMIDITY; i < MAX_HUMIDITY; ++i) {
        if (std::find(levels.begin(), levels.end(), i) == levels.end())
            return i;
    }
    return MAX_HUMIDITY + 1;
}

TEST_F(CdmTestSuite, CDM_v1_TargetHumidity)
{
    WaitForControllee(TARGET_HUMIDITY_INTERFACE);
    for (size_t i = 0; i < m_interfaces.size(); i++) {
        TEST_LOG_OBJECT_PATH(m_interfaces[i].objectPath);

        auto listener = mkRef<TargetHumidityListener>();
        auto interface = m_controller->CreateInterface("org.alljoyn.SmartSpaces.Environment.TargetHumidity", m_interfaces[i].busName,
                                                                qcc::String(m_interfaces[i].objectPath.c_str()), m_interfaces[i].sessionId, listener);
        auto controller = std::dynamic_pointer_cast<TargetHumidityIntfController>(interface);
        QStatus status = ER_FAIL;

        TEST_LOG_1("Get initial values for all properties.");
        {
            TEST_LOG_2("Retrieve the TargetValue property.");
            status = controller->GetTargetValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_2("Retrieve the MinValue property.");
            status = controller->GetMinValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_2("Retrieve the MaxValue property.");
            status = controller->GetMaxValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_2("Retrieve the StepValue property.");
            status = controller->GetStepValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_2("Retrieve the SelectableHumidityLevels property.");
            status = controller->GetSelectableHumidityLevels();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();
        }

        TEST_LOG_1("Initialize all read-write properties.");
        if (listener->m_maxValue > listener->m_minValue) {
            TEST_LOG_2("Initialize the TargetValue property to the MinValue");
            if (listener->m_targetValue != listener->m_minValue) {
                uint8_t target = listener->m_minValue;
                status =  controller->SetTargetValue(target);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                EXPECT_EQ(listener->m_status, ER_OK);
                listener->m_event.ResetEvent();

                EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
                listener->m_eventSignal.ResetEvent();
                EXPECT_EQ(listener->m_targetValueSignal, target);
            }
        } else {
            TEST_LOG_2("If SelectableHumidityLevels > 1, initialize the TargetValue property to the 1st item of the SelectableHumidityLevels.");
            if (listener->m_selectableHumidityLevels.size() > 1 && listener->m_targetValue != listener->m_selectableHumidityLevels[0]) {
                uint8_t target = listener->m_selectableHumidityLevels[0];
                status =  controller->SetTargetValue(target);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                EXPECT_EQ(listener->m_status, ER_OK);
                listener->m_event.ResetEvent();

                EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
                listener->m_eventSignal.ResetEvent();
                EXPECT_EQ(listener->m_targetValueSignal, target);
            }
        }

        TEST_LOG_1("Set properties to valid value.");
        if (listener->m_maxValue > listener->m_minValue) {
            TEST_LOG_2("Set the TargetValue property to the MaxValue.");
            uint8_t target = listener->m_maxValue;
            status =  controller->SetTargetValue(target);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_3("Wait the PropertiesChanged signal for the TargetValue property.");
            EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
            listener->m_eventSignal.ResetEvent();
            EXPECT_EQ(listener->m_targetValueSignal, target);

            TEST_LOG_3("Get the TargetValue property.");
            status =  controller->GetTargetValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
            EXPECT_EQ(listener->m_targetValue, target);
        } else if (listener->m_selectableHumidityLevels.size() > 1) {
            TEST_LOG_2("If SelectableHumidityLevels > 1, Set the TargetValue property to the 2nd item of the SelectableHumidityLevels.");
            uint8_t target = listener->m_selectableHumidityLevels[1];
            status =  controller->SetTargetValue(target);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_3("Wait the PropertiesChanged signal for the TargetValue property.");
            EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
            listener->m_eventSignal.ResetEvent();
            EXPECT_EQ(listener->m_targetValueSignal, target);

            TEST_LOG_3("Get the TargetValue property.");
            status =  controller->GetTargetValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
            EXPECT_EQ(listener->m_targetValue, target);
        }

        TEST_LOG_1("Set properties to invalid value.");
        if (listener->m_maxValue > listener->m_minValue) {
            TEST_LOG_2("If MinValue > " << (int)MIN_HUMIDITY << ", Set the TargetValue property to the 0.");
            if (listener->m_minValue > MIN_HUMIDITY) {
                status =  controller->SetTargetValue(MIN_HUMIDITY);
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                EXPECT_EQ(listener->m_status, ER_OK);
                listener->m_event.ResetEvent();

                TEST_LOG_3("Wait the PropertiesChanged signal for the TargetValue property.");
                EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
                listener->m_eventSignal.ResetEvent();
                EXPECT_EQ(listener->m_targetValueSignal, listener->m_minValue);

                TEST_LOG_3("Get the TargetValue property.");
                status =  controller->GetTargetValue();
                EXPECT_EQ(status, ER_OK);
                EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
                listener->m_event.ResetEvent();
                EXPECT_EQ(listener->m_status, ER_OK);
                EXPECT_EQ(listener->m_targetValue, listener->m_minValue);
            }

            TEST_LOG_2("Set the TargetValue property to invalid Value(" << (int)MAX_HUMIDITY + 1 << ").");
            status =  controller->SetTargetValue(MAX_HUMIDITY + 1);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_EQ(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_3("Wait the PropertiesChanged signal for the TargetValue property.");
            EXPECT_EQ(true, listener->m_eventSignal.Wait(TIMEOUT)) << "property changed signal is missing";
            listener->m_eventSignal.ResetEvent();
            EXPECT_DOUBLE_EQ(listener->m_targetValueSignal, listener->m_maxValue);

            TEST_LOG_3("Get the TargetValue property.");
            status =  controller->GetTargetValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
            EXPECT_EQ(listener->m_targetValue, listener->m_maxValue);
        } else if (listener->m_selectableHumidityLevels.size() > 0) {
            uint8_t prevValue = listener->m_targetValue;
            uint8_t targetValue = getInvalidValue(listener->m_selectableHumidityLevels);
            TEST_LOG_2("Set the TargetValue property to invalid Value(" << (int)targetValue << ").");
            status =  controller->SetTargetValue(targetValue);
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            EXPECT_NE(listener->m_status, ER_OK);
            listener->m_event.ResetEvent();

            TEST_LOG_3("Get the TargetValue property.");
            status =  controller->GetTargetValue();
            EXPECT_EQ(status, ER_OK);
            EXPECT_EQ(true, listener->m_event.Wait(TIMEOUT));
            listener->m_event.ResetEvent();
            EXPECT_EQ(listener->m_status, ER_OK);
            EXPECT_EQ(listener->m_targetValue, prevValue);
        }
    }
}

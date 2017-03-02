/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
*    
*    SPDX-License-Identifier: Apache-2.0
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*    
*    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*    Alliance. All rights reserved.
*    
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*    
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include <set>

#include "ControllerServiceBusObject.h"
#include "ControllerServiceLampBusObject.h"
#include "ControllerServicePresetBusObject.h"
#include "ControllerServiceLampGroupBusObject.h"
#include "ControllerServiceSceneBusObject.h"
#include "ControllerServiceMasterSceneBusObject.h"
#include "LeaderElectionAndStateSyncBusObject.h"
#include "ControllerServiceTransitionEffectBusObject.h"
#include "ControllerServicePulseEffectBusObject.h"
#include "ControllerServiceSceneElementBusObject.h"
#include "ControllerServiceSceneWithSceneElementsBusObject.h"
#include "ControllerServiceDataSetBusObject.h"
#include "IOManager.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class LSFControllerTestSuite : public ::testing::Test, public IOManager
{
public:
	LSFControllerTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;
	static const char* CONTROLLER_BUS_OBJECT_PATH;

	static const char* CONTROLLERSERVICE_INTERFACE_NAME;
	static const char* LAMP_INTERFACE_NAME;
	static const char* LAMPGROUP_INTERFACE_NAME;
	static const char* PRESET_INTERFACE_NAME;
	static const char* SCENE_INTERFACE_NAME;
	static const char* MASTERSCENE_INTERFACE_NAME;
	static const char* LEADER_ELECTION_INTERFACE_NAME;
	static const char* TRANSITION_EFFECT_INTERFACE_NAME;
	static const char* PULSE_EFFECT_INTERFACE_NAME;
	static const char* SCENE_WITH_SCENE_ELEMENTS_INTERFACE_NAME;
	static const char* SCENE_ELEMENT_INTERFACE_NAME;
	static const char* DATA_SET_INTERFACE_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	ControllerServiceBusObject* m_ControllerServiceBusObject{ nullptr };
	ControllerServiceLampBusObject* m_LampBusObject{ nullptr };
	ControllerServicePresetBusObject* m_PresetBusObject{ nullptr };
	ControllerServiceLampGroupBusObject* m_LampGroupBusObject{ nullptr };
	ControllerServiceSceneBusObject* m_SceneBusObject{ nullptr };
	ControllerServiceMasterSceneBusObject* m_MasterSceneBusObject{ nullptr };
	LeaderElectionAndStateSyncBusObject* m_LeaderElectionAndStateSyncBusObject{ nullptr };
	ControllerServiceTransitionEffectBusObject* m_TransitionEffectBusObject{ nullptr };
	ControllerServicePulseEffectBusObject* m_PulseEffectBusObject{ nullptr };
	ControllerServiceSceneElementBusObject* m_SceneElementBusObject{ nullptr };
	ControllerServiceSceneWithSceneElementsBusObject* m_SceneWithElementsBusObject{ nullptr };
	ControllerServiceDataSetBusObject* m_DataSetBusObject{ nullptr };

	void initProxyBusObjects();
	//void waitForSessionToClose();
	void releaseResources();

	// LSF_Controller-v1-01
	void verifyInterfacesFromAnnouncement();
	void verifyLeaderElectionAndStateSyncInterface();

	// LSF_Controller-v1-04
	QStatus getConnectedLamp(std::string&);

	// LSF_Controller-v1-07
	QStatus getParamNameAndValue(const ajn::MsgArg&, std::string&, std::string&);

	// LSF_Controller-v1-09
	ajn::MsgArg* newLampState(const bool, const uint32_t, const uint32_t, const uint32_t, const uint32_t);

	// LSF_Controller-v1-15
	QStatus createLampGroup(const std::vector<const char*>&, uint32_t&, qcc::String&);

	// LSF_Controller-v1-16
	bool compareLampStates(const std::vector<ajn::MsgArg>&, ajn::MsgArg*);
	ajn::MsgArg getValueFromLampState(const std::vector<ajn::MsgArg>&, const char*);

	// LSF_Controller-v1-23
	QStatus createTransitionScene(qcc::String&);
	QStatus createPulseScene(qcc::String&);

	// LSF_Controller-v1-27
	QStatus createMasterScene(qcc::String&);

	// LSF_Controller-v1-36
	QStatus createTransitionEffect(qcc::String&);

	// LSF_Controller-v1-42
	QStatus createPulseEffect(qcc::String&);

	// LSF_Controller-v1-47
	QStatus createSceneElement(qcc::String&);

	// LSF_Controller-v1-51
	QStatus createSceneWithSceneElements(qcc::String&);
};
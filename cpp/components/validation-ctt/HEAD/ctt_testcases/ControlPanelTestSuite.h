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

#include "IOManager.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class ControlPanelTestSuite : public ::testing::Test, public IOManager
{
public:
	ControlPanelTestSuite();
	void SetUp();
	void TearDown();

protected:
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static AJ_PCSTR CONTROLPANEL_INTERFACE_NAME;
	static AJ_PCSTR CONTAINER_INTERFACE_NAME;
	static AJ_PCSTR SECURED_CONTAINER_INTERFACE_NAME;
	static AJ_PCSTR NOTIFICATION_ACTION_INTERFACE_NAME;
	static AJ_PCSTR PROPERTY_INTERFACE_NAME;
	static AJ_PCSTR SECURED_PROPERTY_INTERFACE_NAME;
	static AJ_PCSTR LABEL_PROPERTY_INTERFACE_NAME;
	static AJ_PCSTR ACTION_INTERFACE_NAME;
	static AJ_PCSTR SECURED_ACTION_INTERFACE_NAME;
	static AJ_PCSTR DIALOG_INTERFACE_NAME;
	static AJ_PCSTR SECURED_DIALOG_INTERFACE_NAME;
	static AJ_PCSTR HTTP_CONTROL_INTERFACE_NAME;

	static AJ_PCSTR CONTROLPANEL_PATH_PATTERN;
	static AJ_PCSTR HTTPCONTROL_PATH_PATTERN;
	static AJ_PCSTR URL_REGEX;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	XMLBasedBusIntrospector* m_BusIntrospector{ nullptr };

	// ControlPanel-v1-01
	void validateControlPanelInterfaceDetailList(std::list<InterfaceDetail>);
	bool stringMatchesPattern(const std::string&, const std::string&);
	void validateControlPanelBusObject(const std::string&);
	QStatus getVersionPropertyFromInterface(ajn::ProxyBusObject*, const std::string&, uint16_t&);
	void assertContainerObjectExists(const std::string&);

	// ControlPanel-v1-02
	void validateContainerInterfaceDetailList(const std::list<InterfaceDetail>&, bool);
	void assertValidAncestorIsPresentForContainer(const std::string&);
	void validateContainerBusObject(const std::string&, bool);
	QStatus getStatesPropertyFromInterface(ajn::ProxyBusObject*, const std::string&, uint32_t&);
	void validateContainerParameters(size_t, const ajn::MsgArg*);
	void validateOptionalParameter0(size_t, const ajn::MsgArg*);
	void validateOptionalParameter1(size_t, const ajn::MsgArg*);
	void validateContainerParameterLayoutHints(size_t, const ajn::MsgArg*);

	// ControlPanel-v1-03
	void validatePropertyInterfaceDetailList(const std::list<InterfaceDetail>&, bool);
	void assertAncestorContainerIsPresent(const std::string&);
	void validatePropertyBusObject(const std::string&, bool);
	void validatePropertyControlParameters(size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validatePropertyControlParameterLayoutHints(size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validateBasedOnLayoutHintId(size_t, const ajn::MsgArg*, uint16_t, const ajn::MsgArg);
	void assertOptionalParameter4IsPresent(size_t, const ajn::MsgArg*, uint16_t);
	void validateDateTimeHint(uint16_t, const ajn::MsgArg, uint16_t);
	void validatePropertyControlOptionalParameter3(size_t, const ajn::MsgArg*);
	void validatePropertyControlOptionalParameter4(size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validatePropertyControlOptionalParameter5(size_t, const ajn::MsgArg*, const ajn::MsgArg);

	// ControlPanel-v1-04
	void validateLabelPropertyInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateLabelPropertyBusObject(const std::string&);
	void validateLabelPropertyParameters(size_t, const ajn::MsgArg*);
	void validateParameterLayoutHints(size_t, const ajn::MsgArg*);

	// ControlPanel-v1-05
	void validateActionInterfaceDetailList(const std::list<InterfaceDetail>&, bool);
	void validateActionBusObject(const std::string&, bool);
	void validateActionParameters(size_t, const ajn::MsgArg*);

	// ControlPanel-v1-06
	void validateDialogInterfaceDetailList(const std::list<InterfaceDetail>&, bool);
	void assertValidAncestorIsPresentForDialog(const std::string&);
	void validateDialogBusObject(const std::string&, bool);
	void validateDialogParameters(size_t, const ajn::MsgArg*, uint16_t);
	void validateDialogParameterActionLabelIds(size_t, const ajn::MsgArg*, uint16_t);
	void validateDialogActions(ajn::ProxyBusObject*, const std::string&,  uint16_t);
	void validateInvokingDialogActionReturnsErrorStatus(ajn::ProxyBusObject*, const std::string&, AJ_PCSTR);

	// ControlPanel-v1-08
	void validateNotificationActionInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateNotificationActionBusObject(const std::string&);
	void assertContainerOrDialogObjectExists(const std::string&);

	// ControlPanel-v1-09
	void validateHttpControlInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateHttpControlBusObject(const std::string&);
	void validateRootUrl(ajn::ProxyBusObject*);

	// ControlPanel-v1-10
	void validateSecuredContainerInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredInterfaceInvalidPasscodeBusObject(const std::string&, AJ_PCSTR);
	void setInvalidAuthentication();
	void validateSecuredPropertyInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredActionInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredDialogInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);

	void releaseResources();
};
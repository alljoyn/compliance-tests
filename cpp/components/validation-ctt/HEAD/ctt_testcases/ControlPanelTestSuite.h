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
	static const char* BUS_APPLICATION_NAME;
	static const char* CONTROLPANEL_INTERFACE_NAME;
	static const char* CONTAINER_INTERFACE_NAME;
	static const char* SECURED_CONTAINER_INTERFACE_NAME;
	static const char* NOTIFICATION_ACTION_INTERFACE_NAME;
	static const char* LIST_PROPERTY_INTERFACE_NAME;
	static const char* SECURED_LIST_PROPERTY_INTERFACE_NAME;
	static const char* PROPERTY_INTERFACE_NAME;
	static const char* SECURED_PROPERTY_INTERFACE_NAME;
	static const char* LABEL_PROPERTY_INTERFACE_NAME;
	static const char* ACTION_INTERFACE_NAME;
	static const char* SECURED_ACTION_INTERFACE_NAME;
	static const char* DIALOG_INTERFACE_NAME;
	static const char* SECURED_DIALOG_INTERFACE_NAME;
	static const char* HTTP_CONTROL_INTERFACE_NAME;

	static const char* CONTROLPANEL_PATH_PATTERN;
	static const char* HTTPCONTROL_PATH_PATTERN;
	static const char* URL_REGEX;

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
	void validateContainerInterfaceDetailList(const std::list<InterfaceDetail>&, const bool);
	void assertValidAncestorIsPresentForContainer(const std::string&);
	void validateContainerBusObject(const std::string&, const bool);
	QStatus getStatesPropertyFromInterface(ajn::ProxyBusObject*, const std::string&, uint32_t&);
	void validateContainerParameters(const size_t, const ajn::MsgArg*);
	void validateOptionalParameter0(const size_t, const ajn::MsgArg*);
	void validateOptionalParameter1(const size_t, const ajn::MsgArg*);
	void validateContainerParameterLayoutHints(const size_t, const ajn::MsgArg*);

	// ControlPanel-v1-03
	void validatePropertyInterfaceDetailList(const std::list<InterfaceDetail>&, const bool);
	void assertAncestorContainerIsPresent(const std::string&);
	void validatePropertyBusObject(const std::string&, const bool);
	void validatePropertyControlParameters(const size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validatePropertyControlParameterLayoutHints(const size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validateBasedOnLayoutHintId(const size_t, const ajn::MsgArg*, const uint16_t, const ajn::MsgArg);
	void assertOptionalParameter4IsPresent(const size_t, const ajn::MsgArg*, const uint16_t);
	void validateDateTimeHint(const uint16_t, const ajn::MsgArg, const uint16_t);
	void validatePropertyControlOptionalParameter3(const size_t, const ajn::MsgArg*);
	void validatePropertyControlOptionalParameter4(const size_t, const ajn::MsgArg*, const ajn::MsgArg);
	void validatePropertyControlOptionalParameter5(const size_t, const ajn::MsgArg*, const ajn::MsgArg);

	// ControlPanel-v1-04
	void validateLabelPropertyInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateLabelPropertyBusObject(const std::string&);
	void validateLabelPropertyParameters(const size_t, const ajn::MsgArg*);
	void validateParameterLayoutHints(const size_t, const ajn::MsgArg*);

	// ControlPanel-v1-05
	void validateActionInterfaceDetailList(const std::list<InterfaceDetail>&, const bool);
	void validateActionBusObject(const std::string&, const bool);
	void validateActionParameters(const size_t, const ajn::MsgArg*);

	// ControlPanel-v1-06
	void validateDialogInterfaceDetailList(const std::list<InterfaceDetail>&, const bool);
	void assertValidAncestorIsPresentForDialog(const std::string&);
	void validateDialogBusObject(const std::string&, const bool);
	void validateDialogParameters(const size_t, const ajn::MsgArg*, const uint16_t);
	void validateDialogParameterActionLabelIds(const size_t, const ajn::MsgArg*, const uint16_t);
	void validateDialogActions(ajn::ProxyBusObject*, const std::string&,  const uint16_t);
	void validateInvokingDialogActionReturnsErrorStatus(ajn::ProxyBusObject*, const std::string&, const char*);

	// ControlPanel-v1-07
	void validateListPropertyInterfaceDetailList(const std::list<InterfaceDetail>&, const bool);
	void validateListPropertyBusObject(const std::string&, const bool);
	void validateListPropertyParameters(const size_t, const ajn::MsgArg*);
	void validateListPropertyValues(const size_t, const ajn::MsgArg*);

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
	void validateSecuredInterfaceInvalidPasscodeBusObject(const std::string&, const char*);
	void setInvalidAuthentication();
	void validateSecuredPropertyInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredActionInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredDialogInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);
	void validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(const std::list<InterfaceDetail>&);

	void releaseResources();
};
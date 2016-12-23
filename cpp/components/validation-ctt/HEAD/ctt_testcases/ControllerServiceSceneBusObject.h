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

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServiceSceneBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceSceneBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void ScenesNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void ScenesCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void ScenesUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void ScenesDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void ScenesAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	bool DidScenesNameChanged();
	bool DidScenesCreated();
	bool DidScenesUpdated();
	bool DidScenesDeleted();
	bool DidScenesApplied();

	QStatus GetVersion(uint32_t&);

	QStatus GetAllSceneIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetSceneName(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetSceneName(AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&);
	QStatus CreateScene(const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus UpdateScene(AJ_PCSTR, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, uint32_t&, qcc::String&);
	QStatus DeleteScene(AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus GetScene(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&);
	QStatus ApplyScene(AJ_PCSTR, uint32_t&, qcc::String&);
	
private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_ScenesNameChangedSignalReceived;
	bool m_ScenesCreatedSignalReceived;
	bool m_ScenesUpdatedSignalReceived;
	bool m_ScenesDeletedSignalReceived;
	bool m_ScenesAppliedSignalReceived;
};
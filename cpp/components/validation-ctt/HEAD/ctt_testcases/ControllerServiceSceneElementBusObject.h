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

class ControllerServiceSceneElementBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceSceneElementBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void SceneElementsAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void SceneElementsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void SceneElementsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void SceneElementsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void SceneElementsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	bool DidSceneElementsApplied();
	bool DidSceneElementsNameChanged();
	bool DidSceneElementsCreated();
	bool DidSceneElementsUpdated();
	bool DidSceneElementsDeleted();

	QStatus GetVersion(uint32_t&);

	QStatus ApplySceneElement(AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus CreateSceneElement(const std::vector<AJ_PCSTR>&, const std::vector<AJ_PCSTR>&, AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus DeleteSceneElement(AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus GetAllSceneElementIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetSceneElement(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<qcc::String>&, std::vector<qcc::String>&, qcc::String&);
	QStatus GetSceneElementName(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetSceneElementName(AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&);
	QStatus UpdateSceneElement(AJ_PCSTR, const std::vector<AJ_PCSTR>&, const std::vector<AJ_PCSTR>&, AJ_PCSTR, uint32_t&, qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_SceneElementsAppliedSignalReceived;
	bool m_SceneElementsCreatedSignalReceived;
	bool m_SceneElementsDeletedSignalReceived;
	bool m_SceneElementsNameChangedSignalReceived;
	bool m_SceneElementsUpdatedSignalReceived;
};
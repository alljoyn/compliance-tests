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

class ControllerServicePulseEffectBusObject : public ajn::MessageReceiver
{
public:
	ControllerServicePulseEffectBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void PulseEffectsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void PulseEffectsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void PulseEffectsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void PulseEffectsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	bool DidPulseEffectsNameChanged();
	bool DidPulseEffectsCreated();
	bool DidPulseEffectsUpdated();
	bool DidPulseEffectsDeleted();

	QStatus GetVersion(uint32_t&);

	QStatus ApplyPulseEffectOnLampGroups(AJ_PCSTR, const std::vector<AJ_PCSTR>&, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus ApplyPulseEffectOnLamps(AJ_PCSTR, const std::vector<AJ_PCSTR>&, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus CreatePulseEffect(ajn::MsgArg*, uint32_t, uint32_t, uint32_t, ajn::MsgArg*, AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus DeletePulseEffect(AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus GetAllPulseEffectIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetPulseEffect(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&, uint32_t&, uint32_t&, uint32_t&, std::vector<ajn::MsgArg>&, qcc::String&, qcc::String&);
	QStatus GetPulseEffectName(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetPulseEffectName(AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&);
	QStatus UpdatePulseEffect(AJ_PCSTR, ajn::MsgArg*, uint32_t, uint32_t, uint32_t, ajn::MsgArg*, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_PulseEffectsCreatedSignalReceived;
	bool m_PulseEffectsDeletedSignalReceived;
	bool m_PulseEffectsNameChangedSignalReceived;
	bool m_PulseEffectsUpdatedSignalReceived;
};
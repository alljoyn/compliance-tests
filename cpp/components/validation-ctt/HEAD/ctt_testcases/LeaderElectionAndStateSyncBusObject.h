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

class LeaderElectionAndStateSyncBusObject : ajn::MessageReceiver
{
public:
	LeaderElectionAndStateSyncBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void BlobChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	bool DidBlobChanged();

	QStatus GetVersion(uint32_t&);

	QStatus GetChecksumAndModificationTimestamp(std::vector<ajn::MsgArg>&);
	QStatus GetBlob(uint32_t, uint32_t&, qcc::String&, uint32_t&, uint64_t&);
	QStatus Overthrow(bool&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_BlobChangedSignalReceived;
};
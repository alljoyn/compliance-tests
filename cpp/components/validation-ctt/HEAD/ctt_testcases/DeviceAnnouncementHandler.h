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

#include <alljoyn\AboutListener.h>
#include "AboutAnnouncementDetails.h"
#include <deque>
#include <chrono>

class DeviceAnnouncementHandler : public ajn::AboutListener
{
public:
	DeviceAnnouncementHandler(const std::string&, const uint8_t*);
	void Announced(const char*, uint16_t, ajn::SessionPort, const ajn::MsgArg&, const ajn::MsgArg&);
	//~DeviceAnnouncementHandler();
	AboutAnnouncementDetails* waitForNextDeviceAnnouncement(long);
	//void waitForSessionToClose(const uint16_t);
	void clearQueuedDeviceAnnouncements();

private:
	std::string m_DutDeviceId;
	const uint8_t* m_DutAppId{ nullptr };
	//std::deque<qcc::String> m_LostDevices;
	std::deque<AboutAnnouncementDetails> m_ReceivedAnnouncements;

	bool deviceIdMatches(const std::string&, const std::string&);
	bool appIdMatches(const uint8_t*, const uint8_t*);
};
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

#include <alljoyn/BusAttachment.h>

class ControllerServiceBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LightingResetSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg);
	bool DidLightingReset();

	QStatus GetVersion(uint32_t&);

	QStatus LightingResetControllerService(uint32_t&);
	QStatus GetControllerServiceVersion(uint32_t&);
	
private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_LightingResetSignalReceived;
};
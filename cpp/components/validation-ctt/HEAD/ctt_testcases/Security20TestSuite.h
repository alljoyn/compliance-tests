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

#include <set>

#include <alljoyn\AboutProxy.h>

class Security20TestSuite : public ::testing::Test, public IOManager
{
public:
	Security20TestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	std::string m_DefaultLanguage = std::string{ "" };
	ajn::SecurityApplicationProxy* m_SecurityApplicationProxy{ nullptr };

	void releaseResources();
	void setManifestTemplate(ajn::BusAttachment&);
};

/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include "AboutAnnouncement.h"

class AboutAnnouncementDetails : public AboutAnnouncement
{
public:
	AboutAnnouncementDetails(const char*, const uint16_t, const uint16_t, const ajn::MsgArg&, const ajn::MsgArg&);
	char* getDeviceId() const;
	uint8_t* getAppId() const;
	char* getDeviceName() const;
	char* getAppName() const;
	char* getManufacturer() const;
	char* getModel() const;
	const char* getSupportedLanguages() const;
	char* getDescription() const;
	char* getManufactureDate() const;
	char* getSoftwareVersion() const;
	char* getAjSoftwareVersion() const;
	char* getHardwareVersion() const;
	char* getSupportUrl() const;
	bool supportsInterface(char*);
	char* getDefaultLanguage() const;
private:
	static std::map<qcc::String, qcc::String> KEY_SIGNATURE_MAP;

	bool checkForCorrectType(qcc::String key, qcc::String signature);
};
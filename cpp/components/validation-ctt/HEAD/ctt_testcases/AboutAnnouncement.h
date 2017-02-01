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

#include <alljoyn\AboutData.h>
#include <alljoyn\AboutObjectDescription.h>
#include <alljoyn\MsgArg.h>

class AboutAnnouncement
{
	public:
		AboutAnnouncement(const char*, const uint16_t, const uint16_t, const ajn::MsgArg&, const ajn::MsgArg&);
		std::string getServiceName() const;
		uint16_t getPort() const;
		ajn::AboutObjectDescription* getObjectDescriptions() const;
		ajn::AboutData* getAboutData() const;
		uint16_t getVersion() const;
	private:
		std::string m_ServiceName = std::string{ "" };
		uint16_t m_Version;
		uint16_t m_Port;
		ajn::AboutObjectDescription* m_ObjectDescription{ nullptr };
		ajn::AboutData* m_AboutData{ nullptr };
};
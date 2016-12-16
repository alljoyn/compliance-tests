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

#include <map>

#include "SrpKeyXHandlerImpl.h"
#include "SrpLogonHandlerImpl.h"
#include "ECDHENullHandlerImpl.h"
#include "ECDHEPskHandlerImpl.h"
#include "ECDHEEcdsaHandlerImpl.h"
#include "RsaKeyXHandlerImpl.h"
#include "PinKeyXHandlerImpl.h"

#include <alljoyn\AuthListener.h>

class AuthListeners : public ajn::AuthListener
{
public:
	AuthListeners(bool, SrpKeyXHandlerImpl*, const std::string&,
		bool, SrpLogonHandlerImpl*, const std::string&, const std::string&,
		bool, ECDHENullHandlerImpl*, 
		bool, ECDHEPskHandlerImpl*, const std::string&, 
		bool, ECDHEEcdsaHandlerImpl*, const std::string&, const std::string&,
		bool, RsaKeyXHandlerImpl*, const std::string&, const std::string&,
		bool, PinKeyXHandlerImpl*, const std::string&);
	bool RequestCredentials(const char*, const char*, uint16_t, const char*, uint16_t, Credentials&);
	void AuthenticationComplete(const char*, const char*, bool);
	std::vector<std::string> getAuthMechanisms();
	std::string getAuthMechanismsAsString();

private:
	std::map<std::string, ajn::AuthListener*> m_AuthListeners;
};
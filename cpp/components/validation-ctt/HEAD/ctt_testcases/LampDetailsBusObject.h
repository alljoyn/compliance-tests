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

#include <vector>

#include <alljoyn/BusAttachment.h>

class LampDetailsBusObject
{
public:
	LampDetailsBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	QStatus GetVersion(uint32_t&);
	QStatus GetMake(uint32_t&);
	QStatus GetModel(uint32_t&);
	QStatus GetType(uint32_t&);
	QStatus GetLampType(uint32_t&);
	QStatus GetLampBaseType(uint32_t&);
	QStatus GetLampBeamAngle(uint32_t&);
	QStatus GetDimmable(bool&);
	QStatus GetColor(bool&);
	QStatus GetVariableColorTemp(bool&);
	QStatus GetHasEffects(bool&);
	QStatus GetMinVoltage(uint32_t&);
	QStatus GetMaxVoltage(uint32_t&);
	QStatus GetWattage(uint32_t&);
	QStatus GetIncandescentEquivalent(uint32_t&);
	QStatus GetMaxLumens(uint32_t&);
	QStatus GetMinTemperature(uint32_t&);
	QStatus GetMaxTemperature(uint32_t&);
	QStatus GetColorRenderingIndex(uint32_t&);
	QStatus GetLampID(qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
};
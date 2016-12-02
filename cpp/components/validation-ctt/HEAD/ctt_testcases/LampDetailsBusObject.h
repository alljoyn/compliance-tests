/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

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
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
#include "stdafx.h"
#include "ArrayParser.h"

#include <algorithm>

using namespace std;

string ArrayParser::parseAppId(const uint8_t* t_appId)
{
	size_t size = 16;
	char* appIdStr = new char[size*2 + 1 + 4];
	size_t i;
	size_t offset = 0;
	for (i = 0; i < size; ++i)
	{
		sprintf_s(appIdStr + i * 2 + offset, size * 2 + 5, "%02x", t_appId[i]);

		if (i == 3 || i == 5 || i == 7 || i == 9)
		{
			sprintf_s(appIdStr + (i + 1) * 2 + offset, size * 2 + 5, "%s", "-");
			offset++;
		}
	}

	appIdStr[i * 2 + offset] = 0;

	return string(appIdStr);
}

uint8_t* ArrayParser::parseAppIdFromString(const string& t_StringAppId)
{
	size_t size = 16;
	uint8_t* appId = new uint8_t[size];

	string strCpy(t_StringAppId);
	strCpy.erase(remove(strCpy.begin(), strCpy.end(), '-'), strCpy.end());
	
	char tmp[3];
	tmp[2] = '\0';

	for (int i = 0; i < size; ++i)
	{
		tmp[0] = strCpy.at(2 * i);
		tmp[1] = strCpy.at(2 * i + 1);

		appId[i] = static_cast<uint8_t>(strtol(tmp, NULL, 16));
	}

	return appId;
}

uint8_t* ArrayParser::parseBytesFromHexString(const string& t_HexString, size_t& t_ByteArraySize)
{
	uint8_t* bytes = new uint8_t[t_HexString.length()/2];

	char tmp[3];
	tmp[2] = '\0';

	int i;
	for (i = 0; i < t_HexString.length()/2; ++i)
	{
		tmp[0] = t_HexString.at(2 * i);
		tmp[1] = t_HexString.at(2 * i + 1);

		bytes[i] = static_cast<uint8_t>(strtol(tmp, NULL, 16));
	}

	t_ByteArraySize = i;
	return bytes;
}

uint16_t ArrayParser::stringToUint16(const char* t_Str)
{
	char *end;
	errno = 0;
	long val = strtol(t_Str, &end, 10);
	if (errno || end == t_Str || *end != '\0' || val < 0 || val >= 0x10000)
	{
		return false;
	}
	return (uint16_t)val;
}
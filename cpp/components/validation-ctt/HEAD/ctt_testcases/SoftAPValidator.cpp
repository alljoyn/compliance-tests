/******************************************************************************
* Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "SoftAPValidator.h"

const char* SoftAPValidator::SOFT_AP_ASSERT_MESSAGE = "Soft AP name must start with AJ_ or end with _AJ string";
const char* SoftAPValidator::SOFT_AP_PREFIX = "AJ_";
const char* SoftAPValidator::SOFT_AP_SUFFIX = "_AJ";

bool SoftAPValidator::validateSoftAP(const std::string& t_SoftApName)
{
	bool condition = starts_with(t_SoftApName, SOFT_AP_PREFIX) || ends_with(t_SoftApName, SOFT_AP_SUFFIX);
	EXPECT_TRUE(condition) << SOFT_AP_ASSERT_MESSAGE;
	return condition;
}

inline bool SoftAPValidator::starts_with(const std::string &t_Value, const std::string &t_Starting)
{
	if (t_Starting.size() > t_Value.size()) return false;
	return t_Value.find(t_Starting) == 0;
}

inline bool SoftAPValidator::ends_with(const std::string &t_Value, const std::string &t_Ending)
{
	if (t_Ending.size() > t_Value.size()) return false;
	return std::equal(t_Ending.rbegin(), t_Ending.rend(), t_Value.rbegin());
}
/******************************************************************************
* Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright 2016 Open Connectivity Foundation and Contributors to
*    AllSeen Alliance. All rights reserved.
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

#include <set>

#include "ValidationResult.h"

template <class E>
class SetValidator
{
public:
	ValidationResult validate(std::set<E>, std::set<E>);
private:
	std::set<E> substract(std::set<E>, std::set<E>);
};

template <class E>
ValidationResult SetValidator<E>::validate(std::set<E> t_ExpectedObjectSet, std::set<E> t_FoundObjectSet)
{
	bool valid = true;
	std::string failureReason;
	std::set<E> extraObjects = substract(t_FoundObjectSet, t_ExpectedObjectSet);
	std::set<E> missingObjects = substract(t_ExpectedObjectSet, t_FoundObjectSet);

	if (!missingObjects.empty())
	{
		valid = false;

		for (auto object : missingObjects)
		{
			failureReason.append(" - Missing ");
			failureReason.append(object);
		}
	}

	if (!extraObjects.empty())
	{
		valid = false;
	}

	for (auto object : extraObjects)
	{
		failureReason.append(" - Undefined ");
		failureReason.append(object);
	}

	return ValidationResult(valid, failureReason);
}

template <class E>
std::set<E> SetValidator<E>::substract(std::set<E> t_ExpectedObjectSet, std::set<E> t_FoundObjectSet)
{
	std::set<E> missingObjects;

	for (auto object : t_ExpectedObjectSet)
	{
		if (!(t_FoundObjectSet.find(object) != t_FoundObjectSet.end()))
		{
			missingObjects.insert(object);
		}
	}

	return missingObjects;
}
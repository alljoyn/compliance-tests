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
#include "stdafx.h"
#include "IntrospectionMethod.h"

using namespace std;

list<IntrospectionArg> IntrospectionMethod::getArgs() const
{
	return m_Args;
}

set<IntrospectionAnnotation> IntrospectionMethod::getAnnotations() const
{
	return m_Annotations;
}

int IntrospectionMethod::compareTo(IntrospectionMethod t_IntrospectionMethod)
{
	return getName() == t_IntrospectionMethod.getName();
}

int IntrospectionMethod::hashCode()
{
	//COMPLETE
	return 1;
}

bool IntrospectionMethod::operator < (const IntrospectionMethod& t_IntrospectionMethod) const
{
	if (getName().compare(t_IntrospectionMethod.getName()) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}

	// Args and Annotations should be compared also.
}

IntrospectionMethod::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Method [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", args=");
	//stringToReturn.append(getArgs().);
	stringToReturn.append(", annotations=");
	//stringToReturn.append(getAnnotations());
	stringToReturn.append("]");

	return stringToReturn;
}

bool IntrospectionMethod::operator == (const IntrospectionMethod& t_IntrospectionMethod) const
{
	if (this == &t_IntrospectionMethod)
	{
		return true;
	}

	if (!((IntrospectionBaseTag) *this == (IntrospectionBaseTag)t_IntrospectionMethod))
	{
		return false;
	}

	if (getAnnotations().empty())
	{
		if (!t_IntrospectionMethod.getAnnotations().empty())
		{
			return false;
		}
	}
	else if (!(getAnnotations() == t_IntrospectionMethod.getAnnotations()))
	{
		return false;
	}

	if (getArgs().empty())
	{
		if (!t_IntrospectionMethod.getArgs().empty())
		{
			return false;
		}
	}
	else if (!(getArgs() == t_IntrospectionMethod.getArgs()))
	{
		return false;
	}

	return true;
}
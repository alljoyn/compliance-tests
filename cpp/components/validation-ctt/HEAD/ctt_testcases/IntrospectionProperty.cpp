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
#include "stdafx.h"
#include "IntrospectionProperty.h"

using namespace std;

string IntrospectionProperty::getType() const
{
	return m_Type;
}

void IntrospectionProperty::setType(string t_Type)
{
	m_Type = t_Type;
}

string IntrospectionProperty::getAccess() const
{
	return m_Access;
}

void IntrospectionProperty::setAccess(string t_Access)
{
	m_Access = t_Access;
}

int IntrospectionProperty::compareTo(IntrospectionProperty t_IntrospectionProperty)
{
	return getName() == t_IntrospectionProperty.getName();
}

int IntrospectionProperty::hashCode()
{
	//COMPLETE
	return 1;
}

bool IntrospectionProperty::operator < (const IntrospectionProperty& t_IntrospectionProperty) const
{
	if (getName().compare(t_IntrospectionProperty.getName()) < 0)
	{
		return true;
	}
	else if (getName().compare(t_IntrospectionProperty.getName()) > 0)
	{
		return false;
	}
	else if (getType().compare(t_IntrospectionProperty.getType()) < 0)
	{
		return true;
	}
	else if (getType().compare(t_IntrospectionProperty.getType()) > 0)
	{
		return false;
	}
	else if (getAccess().compare(t_IntrospectionProperty.getAccess()) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}

IntrospectionProperty::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Property [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", type=");
	stringToReturn.append(getType());
	stringToReturn.append(", Access=");
	stringToReturn.append(getAccess());
	stringToReturn.append("]");

	return stringToReturn;
}
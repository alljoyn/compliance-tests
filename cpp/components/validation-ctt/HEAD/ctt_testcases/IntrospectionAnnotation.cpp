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
#include "IntrospectionAnnotation.h"

using namespace std;

string IntrospectionAnnotation::getValue() const
{
	return m_Value;
}

void IntrospectionAnnotation::setValue(string t_Value)
{
	m_Value = t_Value;
}

int IntrospectionAnnotation::compareTo(IntrospectionAnnotation t_IntrospectionAnnotation)
{
	return getName() == t_IntrospectionAnnotation.getName();
}

bool IntrospectionAnnotation::operator <(const IntrospectionAnnotation& t_IntrospectionAnnotation) const
{
	if (getName().compare(t_IntrospectionAnnotation.getName()) < 0)
	{
		return true;
	}
	else if (getName().compare(t_IntrospectionAnnotation.getName()) > 0)
	{
		return false;
	}
	else
	{
		return (getValue().compare(t_IntrospectionAnnotation.getValue()) < 0);
	}
}

IntrospectionAnnotation::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Annotation [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", Value=");
	stringToReturn.append(getValue());
	stringToReturn.append("]");

	return stringToReturn;
}
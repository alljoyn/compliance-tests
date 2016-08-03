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
#include "IntrospectionSignal.h"

using namespace std;

list<IntrospectionArg> IntrospectionSignal::getArgs()
{
	return m_Args;
}

int IntrospectionSignal::compareTo(IntrospectionSignal t_IntrospectionSignal)
{
	return getName() == t_IntrospectionSignal.getName();
}

int IntrospectionSignal::hashCode()
{
	//COMPLETAR
	return 1;
}

bool IntrospectionSignal::operator < (const IntrospectionSignal& t_IntrospectionSignal) const
{
	if (getName().compare(t_IntrospectionSignal.getName()) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}

	// Args should be compared also.
}

IntrospectionSignal::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Signal [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", Args=");
	//stringToReturn.append(getArgs());
	stringToReturn.append("]");

	return stringToReturn;
}
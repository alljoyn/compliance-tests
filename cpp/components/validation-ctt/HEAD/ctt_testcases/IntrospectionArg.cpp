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
#include "IntrospectionArg.h"

using namespace std;

string IntrospectionArg::getType()
{
	return m_Type;
}

void IntrospectionArg::setType(string t_Type)
{
	m_Type = t_Type;
}

string IntrospectionArg::getDirection()
{
	return m_Direction;
}

void IntrospectionArg::setDirection(string t_Direction)
{
	m_Direction = t_Direction;
}

int IntrospectionArg::hashCode()
{
	//COMPLETE
	return 1;
}

//bool IntrospectionArg::equals(object t_object)
//{
//
//}

string IntrospectionArg::toString()
{
	string stringToReturn;
	stringToReturn.append("[Type=");
	stringToReturn.append(getType());
	stringToReturn.append("]");

	return stringToReturn;
}
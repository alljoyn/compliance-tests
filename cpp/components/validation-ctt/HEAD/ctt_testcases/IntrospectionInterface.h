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
#pragma once

#include <set>

#include "IntrospectionMethod.h"
#include "IntrospectionProperty.h"
#include "IntrospectionSignal.h"

class IntrospectionInterface : public IntrospectionBaseTag
{
public:
	std::set<IntrospectionMethod> getMethods();
	std::set<IntrospectionProperty> getProperties();
	std::set<IntrospectionSignal> getSignals();
	std::set<IntrospectionAnnotation> getAnnotations();
	int hashCode();
	//bool equals(object);
private:
	std::set<IntrospectionMethod> m_Methods;
	std::set<IntrospectionProperty> m_Properties;
	std::set<IntrospectionSignal> m_Signals;
	std::set<IntrospectionAnnotation> m_Annotations;
};
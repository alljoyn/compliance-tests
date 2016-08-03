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
#include "MessageSet.h"

MessageSet::MessageSet(std::string t_Text, ajn::services::NotificationMessageType t_Priority) :
	m_Text(t_Text), m_Priority(t_Priority) {}

std::string MessageSet::getText()
{
	return m_Text;
}

ajn::services::NotificationMessageType MessageSet::getPriority()
{
	return m_Priority;
}

std::string MessageSet::getPriorityAsString()
{
	switch (m_Priority)
	{
		case ajn::services::NotificationMessageType::INFO :
			return "INFO";
		case ajn::services::NotificationMessageType::WARNING :
			return "WARNING";
		case ajn::services::NotificationMessageType::EMERGENCY :
			return "EMERGENCY";
		default :
			return "";
	}
}
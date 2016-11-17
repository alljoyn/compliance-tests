/******************************************************************************
* *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#pragma once

#include <atomic>
#include <string>

#include "AboutAnnouncementDetails.h"
#include "BusIntrospector.h"
#include "NotificationHandler.h"
#include "XMLBasedBusIntrospector.h"

#include <alljoyn\notification\Notification.h>
#include <alljoyn\notification\NotificationReceiver.h>

class NotificationValidator : public ajn::services::NotificationReceiver
{
public:
	NotificationValidator(std::string, uint8_t*, bool, bool, bool, uint16_t);
	void initializeForDevice(AboutAnnouncementDetails&, XMLBasedBusIntrospector&);
	void Receive(ajn::services::Notification const&);
	void Dismiss(const int32_t, const qcc::String);
	unsigned int getNumberOfNotificationsReceived();
	

private:
	static const char* URL_REGEX;

	NotificationHandler* m_NotificationHandler{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	XMLBasedBusIntrospector* m_BusIntrospector{ nullptr };
	std::atomic<unsigned int> m_NotificationCounter{ 0 };
	uint16_t m_ExpectedNotificationVersion;
	bool m_ExpectedRichIconUrl;
	bool m_ExpectedRichAudioUrl;
	bool m_ExpectedResponseObjectPath;
	bool m_ReadingNotifications;

	void validateUrl(const std::string&);
	void readNotification();

};
/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include "IOManager.h"
#include "ServiceHelper.h"

#include <set>

#include <alljoyn\AboutProxy.h>


class AboutTestSuite : public ::testing::Test, public IOManager
{
	public:
		AboutTestSuite();
		void SetUp();
		void TearDown();

	protected:
		static const char* BUS_APPLICATION_NAME;
		static long ANNOUNCEMENT_TIMEOUT_IN_SECONDS;
		static const char* DATE_FORMAT;
		static const char* URL_REGEX;

		std::string m_DutDeviceId = std::string{ "" };
		uint8_t* m_DutAppId{ nullptr };
		ServiceHelper* m_ServiceHelper{ nullptr };
		AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
		ajn::AboutProxy* m_AboutProxy{ nullptr };
		ajn::AboutIconProxy* m_AboutIconProxy{ nullptr };
		std::string m_DefaultLanguage = std::string{ "" };

		// About-v1-01
		void validatePathIfAboutInterfacePresentInAnnouncement();
		const char* getAboutInterfacePathFromAnnouncement();
		void verifyAboutData(const ajn::AboutData&);
		void verifyFieldIsPresent(const char*, const ajn::AboutData&, std::string&);
		void compareAbout(const char*, const std::string&, const std::string&, std::string);

		// About-v1-03
		void populateMap(ajn::AboutObjectDescription*, std::map<std::string, const char**>&);
		void populateMap(ajn::MsgArg&, std::map<std::string, const char**>&);

		template <typename Map>
		bool compareMapKeys(Map const &lhs, Map const &rhs)
		{
			auto pred = [](decltype(*lhs.begin()) a, decltype(a) b)
			{ return a.first == b.first; };

			return (lhs.size() == rhs.size()
				&& std::equal(lhs.begin(), lhs.end(), rhs.begin(), pred));
		}

		// About-v1-04
		void populateAnnouncementPathInterfaceSet(std::set<std::string>&, ajn::AboutObjectDescription*, const std::string&);
		void populateBusIntrospectPathInterfaceSet(XMLBasedBusIntrospector&, std::set<std::string>&, const std::string&);

		template<typename T>
		std::vector<T>
		split(const T & str, const T & delimiters)
		{
			std::vector<T> v;
			T::size_type start = 0;
			auto pos = str.find_first_of(delimiters, start);

			while (pos != T::npos)
			{
				if (pos != start) // ignore empty tokens
					v.emplace_back(str, start, pos - start);
				start = pos + 1;
				pos = str.find_first_of(delimiters, start);
			}

			if (start < str.length()) // ignore trailing delimiter
				v.emplace_back(str, start, str.length() - start); // add what's left of the string

			return v;
		}

		// About-v1-06
		void verifyAboutData(ajn::AboutData, std::string);
		void checkForNull(ajn::AboutData, std::string);
		void checkForNull(ajn::AboutData, std::string, std::string);
		void validateSignature(ajn::AboutData, std::string);
		void validateSignature(ajn::AboutData, std::string, const char*, std::string);
		void validateSignatureForNonRequiredFields(ajn::AboutData, std::string);
		void validateDateOfManufacture(ajn::AboutData, std::string);
		void validateSupportUrl(ajn::AboutData, std::string);
		void validateUrl(const std::string&);

		// About-v1-07
		void validateSupportedLanguagesContainsDefaultLanguage(ajn::MsgArg*, size_t);
		bool isDefaultLanguagePresent(ajn::MsgArg*, size_t);
		void validateSupportedLanguagesAboutMap(ajn::AboutData, ajn::MsgArg*, size_t);
		void compareNonLocalizedFieldsInAboutMap(ajn::AboutData, ajn::AboutData, std::string);
		void compareRequiredFieldsInAboutMap(ajn::AboutData, ajn::AboutData, std::string);
		void compareAbout(std::string, const char**, size_t, const char**, size_t, std::string);
		void compareNonRequiredFieldsInAboutMap(ajn::AboutData, ajn::AboutData, std::string);
		void compareAboutNonRequired(ajn::AboutData, ajn::AboutData, std::string, std::string);
		std::string prepareAssertionFailureResponse(std::string, std::string);

		// About-v1-08
		void compareFieldsInAboutData(ajn::AboutData, ajn::AboutData, std::string);

		void releaseResources();
};
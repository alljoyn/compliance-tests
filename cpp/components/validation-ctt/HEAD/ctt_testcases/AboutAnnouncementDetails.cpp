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
#include "AboutAnnouncementDetails.h"

using namespace ajn;
using namespace qcc;

AboutAnnouncementDetails::AboutAnnouncementDetails(const char* t_ServiceName, const uint16_t t_Version, 
	const uint16_t t_Port, const MsgArg& t_ObjectDescription, const MsgArg& t_AboutData) : 
	AboutAnnouncement(t_ServiceName, t_Version, t_Port, t_ObjectDescription, t_AboutData) {}

std::map<String, String> AboutAnnouncementDetails::KEY_SIGNATURE_MAP = 
{
		{ AboutData::DEVICE_ID, "s" },
		{ AboutData::DEVICE_NAME, "s" },
		{ AboutData::DEFAULT_LANGUAGE, "s" },
		{ AboutData::APP_NAME, "s" },
		{ AboutData::MANUFACTURER, "s" },
		{ AboutData::MODEL_NUMBER, "s" },
		{ AboutData::DESCRIPTION, "s" },
		{ AboutData::DATE_OF_MANUFACTURE, "s" },
		{ AboutData::SOFTWARE_VERSION, "s" },
		{ AboutData::AJ_SOFTWARE_VERSION, "s" },
		{ AboutData::HARDWARE_VERSION, "s" },
		{ AboutData::SUPPORT_URL, "s" },
		{ AboutData::APP_ID, "ay" },
		{ AboutData::SUPPORTED_LANGUAGES, "as" }
};

bool AboutAnnouncementDetails::checkForCorrectType(String t_Key, String t_Signature)
{
	String expectedSignature = KEY_SIGNATURE_MAP.at(t_Key);

	if ((expectedSignature.c_str() != NULL) && (expectedSignature != t_Signature))
	{
		//throw
	}
	return false;
}

char* AboutAnnouncementDetails::getDeviceId() const
{
	char* deviceId;
	getAboutData()->GetDeviceId(&deviceId);
	return deviceId;
}

uint8_t* AboutAnnouncementDetails::getAppId() const
{
	uint8_t* appId;
	size_t appIdSize;
	getAboutData()->GetAppId(&appId, &appIdSize);
	uint8_t* fixedAppId = new uint8_t[appIdSize];

	for (size_t i = 0; i < appIdSize; ++i)
	{
		fixedAppId[i] = appId[i];
	}

	return fixedAppId;
}

char* AboutAnnouncementDetails::getDeviceName() const
{
	char* deviceName;
	getAboutData()->GetDeviceName(&deviceName);
	return deviceName;
}

char* AboutAnnouncementDetails::getAppName() const
{
	char* appName;
	getAboutData()->GetAppName(&appName);
	return appName;
}

char* AboutAnnouncementDetails::getManufacturer() const
{
	char* manufacturer;
	getAboutData()->GetManufacturer(&manufacturer);
	return manufacturer;
}

char* AboutAnnouncementDetails::getModel() const
{
	char* model;
	getAboutData()->GetModelNumber(&model);
	return model;
}

const char* AboutAnnouncementDetails::getSupportedLanguages() const
{
	const char* supportedLanguages;
	size_t arraySize = getAboutData()->GetSupportedLanguages(&supportedLanguages);
	return supportedLanguages;
}

char* AboutAnnouncementDetails::getDescription() const
{
	char* description;
	getAboutData()->GetDescription(&description);
	return description;
}

char* AboutAnnouncementDetails::getManufactureDate() const
{
	char* manufacturerDate;
	getAboutData()->GetDateOfManufacture(&manufacturerDate);
	return manufacturerDate;
}

char* AboutAnnouncementDetails::getSoftwareVersion() const
{
	char* softwareVersion;
	getAboutData()->GetSoftwareVersion(&softwareVersion);
	return softwareVersion;
}

char* AboutAnnouncementDetails::getAjSoftwareVersion() const
{
	char* ajSoftwareVersion;
	getAboutData()->GetAJSoftwareVersion(&ajSoftwareVersion);
	return ajSoftwareVersion;
}

char* AboutAnnouncementDetails::getHardwareVersion() const
{
	char* hardwareVersion;
	getAboutData()->GetHardwareVersion(&hardwareVersion);
	return hardwareVersion;
}

char* AboutAnnouncementDetails::getSupportUrl() const
{
	char* supportUrl;
	getAboutData()->GetSupportUrl(&supportUrl);
	return supportUrl;
}

bool AboutAnnouncementDetails::supportsInterface(char* t_InterfaceName)
{
	size_t numberOfPaths = getObjectDescriptions()->GetPaths(NULL, 0);
	const char** paths = new const char*[numberOfPaths];
	getObjectDescriptions()->GetPaths(paths, numberOfPaths);

	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		size_t numberOfInterfaces = getObjectDescriptions()->GetInterfaces(paths[i], NULL, 0);
		const char** intfs = new const char*[numberOfInterfaces];
		getObjectDescriptions()->GetInterfaces(paths[i], intfs, numberOfInterfaces);

		for (size_t j = 0; j < numberOfInterfaces; ++j)
		{
			if (strcmp(intfs[j], t_InterfaceName) == 0)
			{
				delete[] intfs;
				delete[] paths;
				return true;
			}
		}
		delete[] intfs;
	}
	delete[] paths;

	return false;
}

char* AboutAnnouncementDetails::getDefaultLanguage() const
{
	char* defaultLanguage;
	getAboutData()->GetDefaultLanguage(&defaultLanguage);
	return defaultLanguage;
}
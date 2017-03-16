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

#include <alljoyn\AboutKeys.h>

using namespace ajn;
using namespace qcc;

AboutAnnouncementDetails::AboutAnnouncementDetails(AJ_PCSTR t_ServiceName, uint16_t t_Version,
    uint16_t t_Port, const MsgArg& t_ObjectDescription, const MsgArg& t_AboutData) : 
    AboutAnnouncement(t_ServiceName, t_Version, t_Port, t_ObjectDescription, t_AboutData) {}

std::map<String, String> AboutAnnouncementDetails::KEY_SIGNATURE_MAP = 
{
        { AboutKeys::DEVICE_ID, "s" },
        { AboutKeys::DEVICE_NAME, "s" },
        { AboutKeys::DEFAULT_LANGUAGE, "s" },
        { AboutKeys::APP_NAME, "s" },
        { AboutKeys::MANUFACTURER, "s" },
        { AboutKeys::MODEL_NUMBER, "s" },
        { AboutKeys::DESCRIPTION, "s" },
        { AboutKeys::DATE_OF_MANUFACTURE, "s" },
        { AboutKeys::SOFTWARE_VERSION, "s" },
        { AboutKeys::AJ_SOFTWARE_VERSION, "s" },
        { AboutKeys::HARDWARE_VERSION, "s" },
        { AboutKeys::SUPPORT_URL, "s" },
        { AboutKeys::APP_ID, "ay" },
        { AboutKeys::SUPPORTED_LANGUAGES, "as" }
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

AJ_PSTR AboutAnnouncementDetails::getDeviceId() const
{
    AJ_PSTR deviceId;
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

AJ_PSTR AboutAnnouncementDetails::getDeviceName() const
{
    AJ_PSTR deviceName;
    getAboutData()->GetDeviceName(&deviceName);
    return deviceName;
}

AJ_PSTR AboutAnnouncementDetails::getAppName() const
{
    AJ_PSTR appName;
    getAboutData()->GetAppName(&appName);
    return appName;
}

AJ_PSTR AboutAnnouncementDetails::getManufacturer() const
{
    AJ_PSTR manufacturer;
    getAboutData()->GetManufacturer(&manufacturer);
    return manufacturer;
}

AJ_PSTR AboutAnnouncementDetails::getModel() const
{
    AJ_PSTR model;
    getAboutData()->GetModelNumber(&model);
    return model;
}

AJ_PCSTR AboutAnnouncementDetails::getSupportedLanguages() const
{
    AJ_PCSTR supportedLanguages;
    size_t arraySize = getAboutData()->GetSupportedLanguages(&supportedLanguages);
    return supportedLanguages;
}

AJ_PSTR AboutAnnouncementDetails::getDescription() const
{
    AJ_PSTR description;
    getAboutData()->GetDescription(&description);
    return description;
}

AJ_PSTR AboutAnnouncementDetails::getManufactureDate() const
{
    AJ_PSTR manufacturerDate;
    getAboutData()->GetDateOfManufacture(&manufacturerDate);
    return manufacturerDate;
}

AJ_PSTR AboutAnnouncementDetails::getSoftwareVersion() const
{
    AJ_PSTR softwareVersion;
    getAboutData()->GetSoftwareVersion(&softwareVersion);
    return softwareVersion;
}

AJ_PSTR AboutAnnouncementDetails::getAjSoftwareVersion() const
{
    AJ_PSTR ajSoftwareVersion;
    getAboutData()->GetAJSoftwareVersion(&ajSoftwareVersion);
    return ajSoftwareVersion;
}

AJ_PSTR AboutAnnouncementDetails::getHardwareVersion() const
{
    AJ_PSTR hardwareVersion;
    getAboutData()->GetHardwareVersion(&hardwareVersion);
    return hardwareVersion;
}

AJ_PSTR AboutAnnouncementDetails::getSupportUrl() const
{
    AJ_PSTR supportUrl;
    getAboutData()->GetSupportUrl(&supportUrl);
    return supportUrl;
}

bool AboutAnnouncementDetails::supportsInterface(AJ_PSTR t_InterfaceName)
{
    size_t numberOfPaths = getObjectDescriptions()->GetPaths(NULL, 0);
    AJ_PCSTR* paths = new AJ_PCSTR[numberOfPaths];
    getObjectDescriptions()->GetPaths(paths, numberOfPaths);

    for (size_t i = 0; i < numberOfPaths; ++i)
    {
        size_t numberOfInterfaces = getObjectDescriptions()->GetInterfaces(paths[i], NULL, 0);
        AJ_PCSTR* intfs = new AJ_PCSTR[numberOfInterfaces];
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

AJ_PSTR AboutAnnouncementDetails::getDefaultLanguage() const
{
    AJ_PSTR defaultLanguage;
    getAboutData()->GetDefaultLanguage(&defaultLanguage);
    return defaultLanguage;
}
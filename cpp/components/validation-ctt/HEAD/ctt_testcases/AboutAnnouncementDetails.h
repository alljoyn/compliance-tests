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

#include "AboutAnnouncement.h"

class AboutAnnouncementDetails : public AboutAnnouncement
{
public:
    AboutAnnouncementDetails(AJ_PCSTR, uint16_t, uint16_t, const ajn::MsgArg&, const ajn::MsgArg&);
    AJ_PSTR getDeviceId() const;
    uint8_t* getAppId() const;
    AJ_PSTR getDeviceName() const;
    AJ_PSTR getAppName() const;
    AJ_PSTR getManufacturer() const;
    AJ_PSTR getModel() const;
    AJ_PCSTR getSupportedLanguages() const;
    AJ_PSTR getDescription() const;
    AJ_PSTR getManufactureDate() const;
    AJ_PSTR getSoftwareVersion() const;
    AJ_PSTR getAjSoftwareVersion() const;
    AJ_PSTR getHardwareVersion() const;
    AJ_PSTR getSupportUrl() const;
    bool supportsInterface(AJ_PSTR);
    AJ_PSTR getDefaultLanguage() const;
private:
    static std::map<qcc::String, qcc::String> KEY_SIGNATURE_MAP;

    bool checkForCorrectType(qcc::String key, qcc::String signature);
};
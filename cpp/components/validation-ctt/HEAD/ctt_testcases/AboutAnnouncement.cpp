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
#include "AboutAnnouncement.h"

using namespace ajn;
using namespace std;

AboutAnnouncement::AboutAnnouncement(AJ_PCSTR t_ServiceName,
    uint16_t t_Version, uint16_t t_Port, const MsgArg& t_ObjectDescription, 
    const MsgArg& t_AboutData) : 
    m_Version(t_Version), m_ServiceName(t_ServiceName), m_Port(t_Port)
{

    m_ObjectDescription = new AboutObjectDescription();
    m_ObjectDescription->CreateFromMsgArg(t_ObjectDescription);

    m_AboutData = new AboutData(t_AboutData);
}

string AboutAnnouncement::getServiceName() const
{
    return m_ServiceName;
}

uint16_t AboutAnnouncement::getPort() const
{
    return m_Port;
}

AboutObjectDescription* AboutAnnouncement::getObjectDescriptions() const
{
    return m_ObjectDescription;
}

uint16_t AboutAnnouncement::getVersion() const
{
    return m_Version;
}

AboutData* AboutAnnouncement::getAboutData() const
{
    return m_AboutData;
}
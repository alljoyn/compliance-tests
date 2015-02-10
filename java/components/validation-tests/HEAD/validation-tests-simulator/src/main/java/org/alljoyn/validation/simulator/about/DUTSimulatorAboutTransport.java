/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator.about;

import java.util.List;
import java.util.Map;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.validation.simulator.DUTSimulator;

public class DUTSimulatorAboutTransport implements AboutTransport
{
    private DUTSimulator dutSimulator;

    public DUTSimulatorAboutTransport(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    @BusProperty(signature = "q")
    public short getVersion() throws BusException
    {
        return AboutService.PROTOCOL_VERSION;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "a{sv}")
    public Map<String, Variant> GetAboutData(String languageTag) throws BusException
    {
        Map<String, Variant> aboutMap = dutSimulator.getAbout(languageTag);
        if (aboutMap == null)
        {
            throw new ErrorReplyBusException("org.alljoyn.Error.LanguageNotSupported", "The language specified is not supported");
        }
        return aboutMap;
    }

    @Override
    @BusMethod(replySignature = "a(oas)")
    public BusObjectDescription[] GetObjectDescription() throws BusException
    {
        List<BusObjectDescription> supportedInterfaces = dutSimulator.getSupportedInterfaces();
        return supportedInterfaces.toArray(new BusObjectDescription[supportedInterfaces.size()]);
    }

    @Override
    @BusSignal(signature = "qqa(oas)a{sv}")
    public void Announce(short version, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> serviceMetadata)
    {
    }
}
/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.it;

import java.util.Map;

import junit.framework.TestCase;

import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.services.common.BusObjectDescription;

public class AboutAnnouncementMismatchTestCaseWrapper extends TestCaseWrapper
{

    public AboutAnnouncementMismatchTestCaseWrapper(Class<? extends TestCase> clazz, String methodName) throws Exception
    {
        super(clazz, methodName);
    }

    @Override
    protected void runTest() throws Throwable
    {
        try
        {
            testCase.runBare();
        }
        catch (Exception ex)
        {
            Throwable rootCause = ex.getCause();
            assertTrue(rootCause instanceof ErrorReplyBusException);
            ErrorReplyBusException erbe = (ErrorReplyBusException) rootCause;
            assertEquals("BusException has a different error name", "org.freedesktop.DBus.Error.ServiceUnknown", erbe.getErrorName());
        }
    }

    @Override
    protected void setAboutInterfaceForSimulator()
    {
        final AboutTransport existingAboutInterface = simulator.getAboutInterface();
        simulator.setAboutInterface(new AboutTransport()
        {

            @Override
            @BusProperty(signature = "q")
            public short getVersion() throws BusException
            {
                return existingAboutInterface.getVersion();
            }

            @Override
            @BusMethod(replySignature = "a(oas)")
            public BusObjectDescription[] GetObjectDescription() throws BusException
            {
                return new BusObjectDescription[]
                { getBusObjectDescription() };
            }

            @Override
            @BusMethod(signature = "s", replySignature = "a{sv}")
            public Map<String, Variant> GetAboutData(String languageTag) throws BusException
            {
                return existingAboutInterface.GetAboutData(languageTag);
            }

            @Override
            @BusSignal(signature = "qqa(oas)a{sv}")
            public void Announce(short arg0, short arg1, BusObjectDescription[] arg2, Map<String, Variant> arg3)
            {
            }
        });
    }

    private BusObjectDescription getBusObjectDescription()
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setPath("/Mismatch");
        busObjectDescription.setInterfaces(new String[]
        { "org.alljoyn.Aboute" });
        return busObjectDescription;
    }
}
/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

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
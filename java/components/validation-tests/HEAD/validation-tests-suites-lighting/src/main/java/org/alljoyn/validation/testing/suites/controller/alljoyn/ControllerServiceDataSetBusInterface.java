/*******************************************************************************
*     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*     Project (AJOSP) Contributors and others.
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.DataSet")
public interface ControllerServiceDataSetBusInterface
{
/*
    <property name="Version" type="u" access="read"/>
*/
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

/*
    <method name="GetLampDataSet">
        <arg name="lampID" type="s" direction="in"/>
        <arg name="language" type="s" direction="in"/>
        <arg name="responseCode" type="u" direction="out"/>
        <arg name="lampID" type="s" direction="out"/>
        <arg name="language" type="s" direction="out"/>
        <arg name="lampName" type="s" direction="out"/>
        <arg name="lampDetails" type="a{sv}" direction="out"/>
        <arg name="lampState" type="a{sv}" direction="out"/>
        <arg name="lampParameters" type="a{sv}" direction="out"/>
    </method>
*/
    public class GetLampDataSetValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String language;
        @Position(3)
        public String lampName;
        @Position(4)
        public Map<String, Variant> lampDetails;
        @Position(5)
        public Map<String, Variant> lampState;
        @Position(6)
        public Map<String, Variant> lampParameters;
    }

    @BusMethod(signature = "ss", replySignature = "usssa{sv}a{sv}a{sv}")
    public GetLampDataSetValues GetLampDataSet(String lampID, String language);
}
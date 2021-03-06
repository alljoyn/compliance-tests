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

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name="org.allseen.LSF.ControllerService")
public interface ControllerServiceBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    /*
    "   <method name="LightingResetControllerService">"
    "       <arg name="responseCode" type="u" direction="out" />""
    "   </method>"
    */
    @BusMethod(replySignature = "u")
    public int LightingResetControllerService() throws BusException;

    /*
    "   <method name="GetControllerServiceVersion">"
    "       <arg name="version" type="u" direction="out" />"
    "   </method>"
    */
    @BusMethod(replySignature = "u")
    public int GetControllerServiceVersion() throws BusException;

    /*
    "   <signal name="ControllerServiceLightingReset"></signal>"
    */
    @BusSignal
    public void ControllerServiceLightingReset() throws BusException;
}
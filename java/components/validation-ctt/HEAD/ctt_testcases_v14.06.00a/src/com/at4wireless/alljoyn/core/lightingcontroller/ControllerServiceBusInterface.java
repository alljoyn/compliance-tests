/*
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
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Interface ControllerServiceBusInterface.
 */
@BusInterface(name="org.allseen.LSF.ControllerService")
public interface ControllerServiceBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

    /*
    "   <property name="Version" type="u" access="read" />"
    */
    /**
     * Gets the version.
     *
     * @return the version
     * @throws BusException the bus exception
     */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    /*
    "   <method name="LightingResetControllerService">"
    "       <arg name="responseCode" type="u" direction="out" />""
    "   </method>"
    */
    /**
     * Lighting reset controller service.
     *
     * @return the int
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "u")
    public int LightingResetControllerService() throws BusException;

    /*
    "   <method name="GetControllerServiceVersion">"
    "       <arg name="version" type="u" direction="out" />"
    "   </method>"
    */
    /**
     * Gets the controller service version.
     *
     * @return the int
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "u")
    public int GetControllerServiceVersion() throws BusException;

    /*
    "   <signal name="ControllerServiceLightingReset"></signal>"
    */
    /**
     * Controller service lighting reset.
     *
     * @throws BusException the bus exception
     */
    @BusSignal
    public void ControllerServiceLightingReset() throws BusException;
}
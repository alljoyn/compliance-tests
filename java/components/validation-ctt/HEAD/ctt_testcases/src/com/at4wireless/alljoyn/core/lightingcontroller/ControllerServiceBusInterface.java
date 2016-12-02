/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

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
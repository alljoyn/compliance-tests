/******************************************************************************
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 ******************************************************************************/

package org.allseen.timeservice.ajinterfaces;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Secure;
import org.allseen.timeservice.TimeServiceConst;

// TODO: Auto-generated Javadoc
/**
 * Time Service Clock interface
 */
@BusInterface(name = Clock.IFNAME)
@Secure
public interface Clock extends BusObject {

    /**
     * The name of the AllJoyn interface
     */
    public static final String IFNAME = TimeServiceConst.IFNAME_PREFIX + ".Clock";

    /**
     * The version of this interface
     */
    public static final short VERSION = 1;

    /**
     * Returns the interface version
     * 
     * @return Interface version number
     * @throws BusException
     */
    @BusProperty(signature = "q")
    short getVersion() throws BusException;

    /**
     * Returns the DateTime object of the {@link Clock}
     * 
     * @return {@link DateTimeAJ}
     * @throws BusException
     */
    @BusProperty(signature = "((qyy)(yyyq)n)")
    DateTimeAJ getDateTime() throws BusException;

    /**
     * Set DateTime to the {@link Clock}
     * 
     * @param dateTime
     * @throws BusException
     */
    @BusProperty(signature = "((qyy)(yyyq)n)")
    void setDateTime(DateTimeAJ dateTime) throws BusException;

    /**
     * The property is set to TRUE when the {@link Clock} is set.
     * 
     * @return Returns TRUE if the {@link Clock} has been set since the last
     *         reboot
     * @throws BusException
     */
    @BusProperty(signature = "b")
    boolean getIsSet() throws BusException;
}
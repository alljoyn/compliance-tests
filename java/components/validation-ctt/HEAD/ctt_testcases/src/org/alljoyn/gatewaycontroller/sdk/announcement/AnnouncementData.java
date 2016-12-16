/*
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.gatewaycontroller.sdk.announcement;

import java.util.Map;

import org.alljoyn.about.AboutService;
import org.alljoyn.bus.Variant;
import org.alljoyn.gatewaycontroller.sdk.AnnouncedApp;
import org.alljoyn.services.common.BusObjectDescription;

// TODO: Auto-generated Javadoc
/**
 * This class contains announcement data received from a device on the network
 */
public class AnnouncementData {

    /**
     * The about port number
     */
    private final short portNumber;

    /**
     * The announced object descriptions
     */
    private final BusObjectDescription[] objDescArr;

    /**
     * The announcement data
     */
    private final Map<String, Variant> aboutData;

    /**
     * The application data from this {@link AnnouncementData}
     */
    private final AnnouncedApp app;

    /**
     * Constructor
     * 
     * @param portNumber
     * @param objectDescArr
     * @param aboutData
     * @param app
     */
    AnnouncementData(short portNumber, BusObjectDescription[] objDescArr, Map<String, Variant> aboutData,
                         AnnouncedApp app) {

        this.portNumber = portNumber;
        this.objDescArr = objDescArr;
        this.aboutData  = aboutData;
        this.app        = app;
    }

    /**
     * @return The Port number which was sent with the Announcement
     */
    public short getPortNumber() {
        return portNumber;
    }

    /**
     * @return The {@link AboutService} data which was sent with the
     *         Announcement
     */
    public Map<String, Variant> getAboutData() {
        return aboutData;
    }

    /**
     * @return The application data that is created based on the received with
     *         the Announcement AboutData
     */
    public AnnouncedApp getApplicationData() {
        return app;
    }

    /**
     * @return The array of {@link BusObjectDescription}
     */
    public BusObjectDescription[] getObjDescArr() {
        return objDescArr;
    }

}
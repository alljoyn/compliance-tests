/******************************************************************************
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
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
 ******************************************************************************/

package org.allseen.timeservice.server;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Translator;
import org.allseen.timeservice.TimeServiceConst;
import org.allseen.timeservice.TimeServiceException;
import org.allseen.timeservice.ajinterfaces.AlarmFactory;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * This class implements {@link AlarmFactory} interface and realizes AllJoyn
 * communication with this AlarmFactory
 */
class AlarmFactoryBusObj implements AlarmFactory {
    
    /** The Constant TAG. */
    private static final String TAG = "ajts" + AlarmFactoryBusObj.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * Prefix of the AlarmFactory object path
     */
    private static final String OBJ_PATH_PREFIX = "/AlarmFactory";

    /**
     * Alarm factory to be notified with
     * {@link org.allseen.timeservice.server.AlarmFactory} related messages
     */
    private org.allseen.timeservice.server.AlarmFactory alarmFactory;

    /**
     * Object path of this {@link BusObject}
     */
    private final String objectPath;

    /**
     * Events & Actions object description
     */
    private final String description;

    /**
     * Events & Actions description language
     */
    private final String language;

    /**
     * Events & Actions translator
     */
    private final Translator translator;

    /**
     * Constructor
     * 
     * @param alarmFactory
     *            {@link org.allseen.timeservice.server.AlarmFactory} handler
     * @throws TimeServiceException
     *             Is thrown if failed to create {@link AlarmFactoryBusObj}
     */
    AlarmFactoryBusObj(org.allseen.timeservice.server.AlarmFactory alarmFactory) throws TimeServiceException {

        this(alarmFactory, null, null, null);
    }

    /**
     * Constructor
     * 
     * @param alarmFactory
     * @param description
     *            Events&Actions description
     * @param language
     *            Events&Actions description language
     * @param translator
     *            Events&Actions {@link Translator}
     * @throws TimeServiceException
     *             Is thrown if failed to create {@link AlarmFactoryBusObj}
     */
    AlarmFactoryBusObj(org.allseen.timeservice.server.AlarmFactory alarmFactory, String description, String language, Translator translator) throws TimeServiceException {

        if (alarmFactory == null) {

            throw new TimeServiceException("Undefined AlarmFactory");
        }

        this.alarmFactory = alarmFactory;
        objectPath = GlobalStringSequencer.append(OBJ_PATH_PREFIX);

        this.description = description;
        this.language = language;
        this.translator = translator;

        Status status = getBus().registerBusObject(this, objectPath);
        if (status != Status.OK) {

            throw new TimeServiceException("Failed to register BusObject, objPath: '" + objectPath + "', Status: '" + status + "'");
        }

        Log.info("AlarmFactory BusObject, objectPath: '" + objectPath + "' registered successfully");
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.AlarmFactory#getVersion()
     */
    @Override
    public short getVersion() throws BusException {

        return VERSION;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.AlarmFactory#newAlarm()
     */
    @Override
    public String newAlarm() throws BusException {

        Alarm alarm;

        try {

            getBus().enableConcurrentCallbacks();

            Log.debug("NewAlarm is called, objPath: '" + objectPath + "', handling");

            alarm = alarmFactory.newAlarm();
            if (alarm == null) {

                Log.error("Undefined alarm, throwing exception, objPath: '" + objectPath + "'");
                throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, "Uninitialized Alarm");
            }

            BaseAlarmBusObj alarmBusObj = new AlarmBusObj();
            alarmBusObj.init(alarm, objectPath, false, null, description, language, translator);
            alarm.setAlarmBusObj(alarmBusObj);
        } catch (ErrorReplyBusException erbe) {

            Log.error("Failed to execute 'NewAlarm', objPath: '" + objectPath + "'", erbe);
            throw erbe;
        } catch (Exception e) {

            Log.error("Failed to execute 'NewAlarm', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }

        return alarm.getObjectPath();
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.AlarmFactory#deleteAlarm(java.lang.String)
     */
    @Override
    public void deleteAlarm(String objectPath) throws BusException {

        try {

            getBus().enableConcurrentCallbacks();

            Log.debug("DeleteAlarm is called, objPath: '" + objectPath + "', handling");

            alarmFactory.deleteAlarm(objectPath);
        } catch (ErrorReplyBusException erbe) {

            Log.error("Failed to execute 'DeleteAlarm', objPath: '" + objectPath + "'", erbe);
            throw erbe;
        } catch (Exception e) {

            Log.error("Failed to execute 'DeleteAlarm', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * {@link org.allseen.timeservice.server.AlarmFactory} object path
     * 
     * @return object path
     */
    String getObjectPath() {

        return objectPath;
    }

    /**
     * Releases object resources
     */
    void release() {

        Log.debug("Releasing Server Alarm Factory object, objPath: '" + objectPath + "'");
        alarmFactory = null;

        try {

            getBus().unregisterBusObject(this);
        } catch (TimeServiceException tse) {

            Log.error("Failed to unregister BusObject, objPath: '" + objectPath + "'", tse);
        }

    }

    /**
     * Access {@link TimeServiceServer} to get the {@link BusAttachment}. If
     * {@link BusAttachment} is undefined, {@link TimeServiceException} is
     * thrown.
     * 
     * @return {@link BusAttachment}
     * @throws TimeServiceException
     */
    BusAttachment getBus() throws TimeServiceException {

        BusAttachment bus = TimeServiceServer.getInstance().getBusAttachment();

        if (bus == null) {

            throw new TimeServiceException("TimeServiceServer is not initialized");
        }

        return bus;
    }
}
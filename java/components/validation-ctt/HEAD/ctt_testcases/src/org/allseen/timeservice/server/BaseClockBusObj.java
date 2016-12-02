/******************************************************************************
  *   * 
  *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
  *    Source Project Contributors and others.
  *    
  *    All rights reserved. This program and the accompanying materials are
  *    made available under the terms of the Apache License, Version 2.0
  *    which accompanies this distribution, and is available at
  *    http://www.apache.org/licenses/LICENSE-2.0

  ******************************************************************************/

package org.allseen.timeservice.server;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Status;
import org.allseen.timeservice.DateTime;
import org.allseen.timeservice.TimeServiceConst;
import org.allseen.timeservice.TimeServiceException;
import org.allseen.timeservice.ajinterfaces.DateTimeAJ;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * This is the base class for all the {@link org.allseen.timeservice.ajinterfaces.Clock} implementations.
 * Most of the {@link org.allseen.timeservice.ajinterfaces.Clock} related calls delegated to
 * the {@link Clock} object.
 */
//Commented out because currently JavaBinding doesn't support finding AJ interfaces in the SuperClasses
//so they must be implemented by the leaf classes
abstract class BaseClockBusObj /*implements Clock*/ {
    
    /** The Constant TAG. */
    private static final String TAG = "ajts" + BaseClockBusObj.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * The clock to be notified with the {@link Clock} related messages
     */
    private Clock clock;

    /**
     * The object path of this {@link BusObject}
     */
    private final String objectPath;

    /**
     * Constructor
     * @param clock {@link Clock} handler
     * @param objectPath Object path of this object
     * @throws TimeServiceException Is thrown if failed to created {@link ClockBusObj}
     */
    protected BaseClockBusObj(Clock clock, String objectPath) throws TimeServiceException {

        if ( clock == null ) {

            throw new TimeServiceException("Undefined clock");
        }

        if ( !(this instanceof BusObject) ) {

            throw new TimeServiceException("Class not a BusObject");
        }

        this.clock       = clock;
        this.objectPath  = objectPath;

        Status status = getBus().registerBusObject((BusObject)this, this.objectPath);

        if ( status != Status.OK ) {

            throw new TimeServiceException("Failed to register BusObject, objPath: '" + objectPath +
                                               "', Status: '" + status + "'");
        }

        Log.info("Clock BusObject, objectPath: '" + objectPath + "' registered successfully");
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.Clock#getVersion()
     */
    public short getVersion() {

        return org.allseen.timeservice.ajinterfaces.Clock.VERSION;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.Clock#getDateTime()
     */
    public DateTimeAJ getDateTime() throws BusException {

        DateTimeAJ dateTimeAJ;

        try {

            Log.debug("getDateTime is called, objPath: '" + objectPath + "', handling...");

            getBus().enableConcurrentCallbacks();

            DateTime dateTime = clock.getDateTime();

            if ( dateTime == null ) {

                throw new TimeServiceException("Undefined dateTime");
            }

            dateTimeAJ = new DateTimeAJ(dateTime);
        }
        catch (Exception e) {

            Log.error("Failed to execute 'getDateTime', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }

        Log.debug("Returning DateTime: '" + dateTimeAJ + "', objPath: '" + objectPath + "'");
        return dateTimeAJ;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.Clock#setDateTime(DateTimeAJ)
     */
    public void setDateTime(DateTimeAJ dateTimeAJ) throws BusException {

        try {

            Log.debug("setDateTime is called, objPath: '" + objectPath + "'. Setting to: '" + dateTimeAJ + "'");

            getBus().enableConcurrentCallbacks();
            clock.setDateTime(dateTimeAJ.toDateTime());
        }
        catch (IllegalArgumentException ilae) {

            Log.error("Failed to execute 'setDateTime', objPath: '" + objectPath + "'", ilae);
            throw new ErrorReplyBusException(TimeServiceConst.INVALID_VALUE_ERROR, ilae.getMessage());
        }
        catch (Exception e) {

            Log.error("Failed to execute 'setDateTime', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.Clock#getIsSet()
     */
    public boolean getIsSet() throws BusException {

        try {

            Log.debug("getIsSet is called, objPath: '" + objectPath + "', handling...");

            getBus().enableConcurrentCallbacks();
            boolean isSet = clock.getIsSet();

            Log.debug("Returning isSet status: '" + isSet + "', objPath: '" + objectPath + "'");
            return isSet;
        }
        catch (Exception e) {

            Log.error("Failed to execute 'getIsSet', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }
    }

    //================================================//

     /**
      * @return object path of this object
      */
     protected String getObjectPath() {

         return objectPath;
     }

     /**
      * Releases object resources
      */
     protected void release() {

         Log.debug("Releasing Server Clock object, objPath: '" + objectPath + "'");
         clock = null;

         try {

            getBus().unregisterBusObject((BusObject)this);
        } catch (TimeServiceException tse) {

            Log.error("Failed to unregister BusObject, objPath: '" + objectPath + "'", tse);
        }
     }

    /**
     * Access {@link TimeServiceServer} to get the {@link BusAttachment}.
     * If {@link BusAttachment} is undefined, {@link TimeServiceException} is thrown.
     * @return {@link BusAttachment}
     * @throws TimeServiceException
     */
    protected BusAttachment getBus() throws TimeServiceException {

        BusAttachment bus = TimeServiceServer.getInstance().getBusAttachment();

        if ( bus == null ) {

            throw new TimeServiceException("TimeServiceServer is not initialized");
        }

        return bus;
    }
}
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

package org.allseen.timeservice.server;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.SignalEmitter.GlobalBroadcast;
import org.allseen.timeservice.AuthorityType;
import org.allseen.timeservice.TimeServiceException;
import org.allseen.timeservice.ajinterfaces.Clock;
import org.allseen.timeservice.ajinterfaces.TimeAuthority;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * This class implements {@link Clock} and {@link TimeAuthority} interfaces and
 * realizes AllJoyn communication with this Time Authority Clock.
 */
class TimeAuthorityClockBusObj extends BaseClockBusObj implements Clock, TimeAuthority {
    
    /** The Constant TAG. */
    private static final String TAG = "ajts" + TimeAuthorityClockBusObj.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * Prefix of the AuthorityClock object path
     */
    private static final String OBJ_PATH_PREFIX = "/AuthorityClock";

    /**
     * {@link AuthorityType}
     */
    private final AuthorityType authorityType;

    /**
     * TTL of the Time Sync signal
     */
    private final static int TIME_SYNC_TTL_SECONDS = 180;

    /**
     * Constructor
     * 
     * @param type
     * @param clock
     *            Clock events delegate. This clock receives all the
     *            {@link TimeAuthorityClock} related events.
     * @param objectPath
     * @throws TimeServiceException
     */
    TimeAuthorityClockBusObj(AuthorityType authorityType, TimeAuthorityClock clock) throws TimeServiceException {

        super(clock, GlobalStringSequencer.append(OBJ_PATH_PREFIX));

        this.authorityType = authorityType;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.TimeAuthority#getAuthorityType()
     */
    @Override
    public byte getAuthorityType() throws BusException {

        byte authType = authorityType.getValue();

        Log.debug("getAuthorityType is called, returning: '" + authorityType + "', value: '" + authType + "', objPath: '" + getObjectPath() + "'");
        return authType;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.TimeAuthority#timeSync()
     */
    @Override
    public void timeSync() throws BusException {
    }

    /**
     * Send {@link TimeAuthority#timeSync()} signal
     * 
     * @throws TimeServiceException
     *             Is thrown if failed to send the signal
     */
    void sendTimeSync() throws TimeServiceException {

        SignalEmitter emitter = new SignalEmitter(this, GlobalBroadcast.Off);
        emitter.setSessionlessFlag(true);
        emitter.setTimeToLive(TIME_SYNC_TTL_SECONDS);

        try {

            Log.debug("Emitting TimeSync signal, TTL: '" + TIME_SYNC_TTL_SECONDS + "', objPath: '" + getObjectPath() + "'");
            emitter.getInterface(TimeAuthority.class).timeSync();
        } catch (BusException be) {

            throw new TimeServiceException("Failed to emit 'TymeSync' signal", be);
        }
    }

    /**
     * @see org.allseen.timeservice.server.BaseClockBusObj#release()
     */
    @Override
    protected void release() {

        super.release();

    }
}
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

package org.allseen.timeservice.client;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;
import org.allseen.timeservice.ajinterfaces.Timer;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * Singleton class that is responsible to receive {@link Timer} related signals
 */
class TimerSignalHandler {
    
    /** The Constant TAG. */
    private static final String TAG = "ajts" + TimerSignalHandler.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * Self reference for the {@link TimerSignalHandler}
     */
    private static final TimerSignalHandler SELF = new TimerSignalHandler();

    /**
     * Rule to receive signals from the {@link Timer} interface
     */
    private static final String MATCH_RULE       = "interface='" + Timer.IFNAME + "',type='signal'";

    /**
     * Session-Less-Rule
     */
    private static final String SLS_MATCH_RULE   = MATCH_RULE + ",sessionless='t'";

    /**
     * {@link BusAttachment
     */
    private BusAttachment bus;

    /**
     * Timers to be notified on arriving signal
     * This Set internally uses {@link ConcurrentHashMap} that makes it thread safe.
     * @see ConcurrentHashMap
     * @see Collections#newSetFromMap(java.util.Map)
     */
    private final Set<org.allseen.timeservice.client.Timer> timers;

    /**
     * Method handler of the {@link Timer#timerEvent()}
     */
    private Method timerEventHandler;

    /**
     * Method handler of the {@link Timer#runStateChanged(boolean)}
     */
    private Method runStateChangedHandler;

    /**
     * Constructor
     */
    private TimerSignalHandler() {

        // Creates thread safe set
        timers = Collections.newSetFromMap(new ConcurrentHashMap<org.allseen.timeservice.client.Timer, Boolean>());

        try {

            timerEventHandler      = this.getClass().getDeclaredMethod("timerEvent");
            runStateChangedHandler = this.getClass().getDeclaredMethod("runStateChanged", boolean.class);
        }
        catch (NoSuchMethodException nsme) {

            Log.error("Not found one of the signal handler methods", nsme);
        }
    }

    /**
     * @return {@link TimerSignalHandler}
     */
    static TimerSignalHandler getInstance() {

        return SELF;
    }

    /**
     * Adds {@link org.allseen.timeservice.client.Timer} events listener
     * @param bus
     * @param timer
     */
    void registerTimer(BusAttachment bus, org.allseen.timeservice.client.Timer timer) {

        if ( bus == null ) {

            Log.error("Failed to register Timer, BusAttachment is undefined");
            return;
        }

        Log.info("Registering to receive signals from the Timer: '" + timer.getObjectPath() + "'");

        if ( timers.size() == 0 ) {

            registerSignalHandler(bus);
        }

        //Add the timer listener to the timers list
        timers.add(timer);
    }

    /**
     * Remove {@link org.allseen.timeservice.client.Timer} event listener
     * @param timer
     */
    void unregisterTimer(org.allseen.timeservice.client.Timer timer) {

        Log.info("Stop receiving signals from the Timer: '" + timer.getObjectPath() + "'");

        //Remove the timer listener from the timers list
        timers.remove(timer);

        if ( timers.size() == 0 ) {

            unregisterSignalHandler();
        }
    }

    /**
     * TimerEvent signal handler
     * @see Timer#timerEvent()
     */
    public void timerEvent() {

        if ( bus == null ) {

            Log.error("Received TimerEvent signal, but BusAttachment is undefined, returning");
            return;
        }

        bus.enableConcurrentCallbacks();

        String sender   = bus.getMessageContext().sender;
        String objPath  = bus.getMessageContext().objectPath;

        org.allseen.timeservice.client.Timer timer = findTimer(objPath, sender);

        Log.debug("Received TimerEvent signal from the object: '" + objPath + "', sender: '" + sender +
                        "', searching for the Timer");

        if ( timer == null ) {

            Log.debug("Not found any signal listener for the Timer: '" + objPath + "', sender: '" + sender + "'");
            return;
        }

        timer.getTimerHandler().handleTimerEvent(timer);
    }

    /**
     * RunStateChanged signal handler
     * @param isRunning TRUE if the Timer is running
     */
    public void runStateChanged(boolean isRunning) {

        if ( bus == null ) {

            Log.error("Received runStateChanged signal, but BusAttachment is undefined, returning");
            return;
        }

        bus.enableConcurrentCallbacks();

        String sender   = bus.getMessageContext().sender;
        String objPath  = bus.getMessageContext().objectPath;

        org.allseen.timeservice.client.Timer timer = findTimer(objPath, sender);

        Log.debug("Received RunStateChanged signal, IsRunning: '" + isRunning + "' from the object: '" +
                        objPath + "', sender: '" + sender + "', searching for the Timer");

        if ( timer == null ) {

            Log.debug("Not found any signal listener for the Timer: '" + objPath + "', sender: '" + sender + "'");
            return;
        }

        timer.getTimerHandler().handleRunStateChanged(timer, isRunning);
    }

    /**
     * Register signals handler
     */
    private synchronized void registerSignalHandler(BusAttachment bus) {

        Log.debug("Starting Timer signal handler");

        this.bus = bus;

        if ( timerEventHandler == null || runStateChangedHandler == null ) {

            return;
        }

        String timerIfaceLocalName = Timer.class.getName();

        registerSignalHandler(timerIfaceLocalName, timerEventHandler, SLS_MATCH_RULE);
        registerSignalHandler(timerIfaceLocalName, runStateChangedHandler, MATCH_RULE);
    }

    /**
     * Register signal handler
     * @param timerIfaceLocalName
     * @param signalHandler
     * @param matchRule
     */
    private void registerSignalHandler(String timerIfaceLocalName, Method signalHandler, String matchRule) {


        String handlerName = signalHandler.getName();

        Status status = bus.registerSignalHandler(timerIfaceLocalName, handlerName, this, signalHandler);
        if ( status != Status.OK ) {

            Log.error("Failed to call 'bus.registerSignalHandler()' for the '" + handlerName  + "' signal,"
                           + " Status: '" + status + "'");

            return;
        }

        status = bus.addMatch(matchRule);
        if ( status != Status.OK ) {

            Log.error("Failed to call bus.addMatch(" + matchRule + "), Status: '" + status + "'");
            return;
        }
    }

    /**
     * Unregister signal handler
     */
    private synchronized void unregisterSignalHandler() {

        Log.debug("Stopping Timer signal handler");
        unregisterSignalHandler(timerEventHandler,SLS_MATCH_RULE);
        unregisterSignalHandler(runStateChangedHandler, MATCH_RULE);

        //Removing BusAttachment reference
        bus = null;
    }

    /**
     * Unregister signal handler
     * @param signalHandler
     * @param matchRule
     */
    private void unregisterSignalHandler(Method signalHandler, String matchRule) {

        bus.unregisterSignalHandler(this, signalHandler);
        Status status = bus.removeMatch(matchRule);

        if ( status == Status.OK ) {

            Log.debug("Succeeded to call bus.removeMatch(" + matchRule + "), Status: '" + status + "'");
        }
        else {

            Log.error("Failed to call bus.removeMatch(" + matchRule + "), Status: '" + status + "'");
        }
    }

    /**
     * Find Timer by the given senderObjPath and senderName
     * @param senderObjPath
     * @param senderName
     * @return {@link org.allseen.timeservice.client.Timer} if found or NULL
     */
    private org.allseen.timeservice.client.Timer findTimer(String senderObjPath, String senderName) {

        Iterator<org.allseen.timeservice.client.Timer> iterator = timers.iterator();
        while ( iterator.hasNext() ) {

            org.allseen.timeservice.client.Timer timer = iterator.next();
            if ( timer.getObjectPath().equals(senderObjPath) &&
                    timer.tsClient.getServerBusName().equals(senderName) ) {


                return timer;
            }
        }

        return null;
    }
}
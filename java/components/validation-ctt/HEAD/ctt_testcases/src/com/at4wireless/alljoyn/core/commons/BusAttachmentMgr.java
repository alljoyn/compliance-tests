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
package com.at4wireless.alljoyn.core.commons;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;

import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.Logger;



 



// TODO: Auto-generated Javadoc
/**
 * The Class BusAttachmentMgr.
 */
public class BusAttachmentMgr
{
    
    /** The bus attachment. */
    private BusAttachment busAttachment;
    
    /** The Constant TAG. */
    private static final String TAG = BusAttachmentMgr.class.getSimpleName();
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    
    /** The daemon name. */
    private String daemonName;
    
    /** The advertised name. */
    private String advertisedName;
    
    /** The initialized. */
    private static boolean initialized = false;

    /**
     * Instantiates a new bus attachment mgr.
     */
    public BusAttachmentMgr()
    {
        if (!initialized)
        {
        	
        	//We load this in the main class
          //  AllJoynLibraryLoader.loadLibrary();
            initialized = true;
            
        	
            
        }
    }

    /**
     * Creates the.
     *
     * @param busApplicationName the bus application name
     * @param policy the policy
     * @throws BusException the bus exception
     */
    public void create(String busApplicationName, BusAttachment.RemoteMessage policy) throws BusException
    {
        logger.info("Creating BusAttachment");
        busAttachment = createBusAttachment(busApplicationName, policy);
    }

    /**
     * Connect.
     *
     * @throws BusException the bus exception
     */
    public void connect() throws BusException
    {
        logger.info("Connecting BusAttachment");
        Status status = busAttachment.connect();

        if (status != Status.OK)
        {
            String message = "Unable to connect busAttachment: " + status;
            logger.error(message);
            throw new BusException(message);
        }
    }

    /**
     * Advertise.
     *
     * @throws BusException the bus exception
     */
    public void advertise() throws BusException
    {
        daemonName = "org.alljoyn.BusNode_" + busAttachment.getGlobalGUIDString();
        Status status = busAttachment.requestName(daemonName, BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);

        if (status != Status.OK)
        {
            String message = "Failed to requestName '" + daemonName + "': " + status;
            daemonName = null;
            logger.error(message);
            throw new BusException(message);
        }

        advertisedName = "quiet@" + daemonName;
        status = busAttachment.advertiseName(advertisedName, SessionOpts.TRANSPORT_WLAN);

        if (status != Status.OK)
        {
            advertisedName = null;
            String message = "Failed to advertiseName: " + daemonName;
            logger.error(message);
            throw new BusException(message);
        }
    }

    /**
     * Release.
     */
    public void release()
    {
        if (advertisedName != null)
        {
            Status status = busAttachment.cancelAdvertiseName(advertisedName, SessionOpts.TRANSPORT_WLAN);
            if (status != Status.OK)
            {
                logger.warn(String.format("cancelAdvertiseName returned: %s", status));
            }
            advertisedName = null;
        }

        if (daemonName != null)
        {
            Status status = busAttachment.releaseName(daemonName);
            if (status != Status.OK)
            {
                logger.warn(String.format("releaseName returned: %s", status));
            }
            daemonName = null;
        }

        if (busAttachment != null)
        {
            logger.info("Disconnecting busAttachment");
            busAttachment.clearKeyStore();
            busAttachment.disconnect();
            busAttachment.release();
            busAttachment = null;
        }
    }

    /**
     * Creates the bus attachment.
     *
     * @param applicationName the application name
     * @param policy the policy
     * @return the bus attachment
     */
    protected BusAttachment createBusAttachment(String applicationName, BusAttachment.RemoteMessage policy)
    {
        return new BusAttachment(applicationName, policy);
    }

    /**
     * Gets the bus attachment.
     *
     * @return the bus attachment
     */
    public BusAttachment getBusAttachment()
    {
        return busAttachment;
    }

    /**
     * Register signal handler.
     *
     * @param signalHandler the signal handler
     * @throws BusException the bus exception
     */
    public void registerSignalHandler(Object signalHandler) throws BusException
    {
        Status status = busAttachment.registerSignalHandlers(signalHandler);

        if (status != Status.OK)
        {
            String message = String.format("Signal handler %s registration failed with status %s", signalHandler.getClass().getName(), status);
            logger.error(message);
            throw new BusException(message);
        }
    }

    /**
     * Register bus object.
     *
     * @param busObject the bus object
     * @param objectPath the object path
     * @throws BusException the bus exception
     */
    public void registerBusObject(BusObject busObject, String objectPath) throws BusException
    {
        Status status = busAttachment.registerBusObject(busObject, objectPath);

        if (status != Status.OK)
        {
            String message = String.format("Failed to register object: %s", status);
            logger.error(message);
            throw new BusException(message);
        }
    }

    /**
     * Gets the bus unique name.
     *
     * @return the bus unique name
     */
    public String getBusUniqueName()
    {
        return busAttachment.getUniqueName();
    }
}
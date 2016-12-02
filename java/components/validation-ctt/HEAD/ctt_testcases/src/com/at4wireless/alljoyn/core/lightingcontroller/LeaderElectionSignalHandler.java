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

import org.alljoyn.bus.annotation.BusSignalHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class LeaderElectionSignalHandler.
 */
public class LeaderElectionSignalHandler
{
    static
    {
        System.loadLibrary("alljoyn_java");
    }

    /** The signal listener. */
    private ControllerServiceSignalListener signalListener;

    /**
     * Sets the update listener.
     *
     * @param listener the new update listener
     */
    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    /**
     * Blob changed.
     *
     * @param blobType the blob type
     * @param blob the blob
     * @param checksum the checksum
     * @param timestamp the timestamp
     */
    @BusSignalHandler(iface = "org.allseen.LeaderElectionAndStateSync", signal= "BlobChanged")
    public void BlobChanged(int blobType, String blob, int checksum, long timestamp)
    {
        if (signalListener != null)
        {
            signalListener.handleBlobChanged(blobType, blob, checksum, timestamp);
        }
    }
}
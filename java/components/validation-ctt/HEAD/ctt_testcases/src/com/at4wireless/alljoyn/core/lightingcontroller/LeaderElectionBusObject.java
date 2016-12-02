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
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class LeaderElectionBusObject.
 */
public class LeaderElectionBusObject implements BusObject, LeaderElectionBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface#GetChecksumAndModificationTimestamp()
     */
    @Override
    @BusMethod(replySignature = "a(uut)")
    public ChecksumAndTimestampValues[] GetChecksumAndModificationTimestamp() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface#GetBlob(int)
     */
    @Override
    @BusMethod(signature = "u", replySignature = "usut")
    public BlobValues GetBlob(int blobType) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface#Overthrow()
     */
    @Override
    @BusMethod(replySignature = "b")
    public boolean Overthrow() throws BusException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface#BlobChanged(int, java.lang.String, int, long)
     */
    @Override
    @BusSignal(signature = "usut")
    public void BlobChanged(int blobType, String blob, int checksum, long timestamp) throws BusException
    {
    }
}
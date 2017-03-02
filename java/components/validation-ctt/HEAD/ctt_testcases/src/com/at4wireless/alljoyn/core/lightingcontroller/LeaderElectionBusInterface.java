/*
 *    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *    Project (AJOSP) Contributors and others.
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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
*/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * The Interface LeaderElectionBusInterface.
 */
@BusInterface(name = "org.allseen.LeaderElectionAndStateSync")
public interface LeaderElectionBusInterface
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

    /**
     * The Enum BlobType.
     */
    public enum BlobType {
        
        /** The lsf preset. */
        LSF_PRESET,
        
        /** The lsf lamp group. */
        LSF_LAMP_GROUP,
        
        /** The lsf scene. */
        LSF_SCENE,
        
        /** The lsf master scene. */
        LSF_MASTER_SCENE
    }

    /*
    "   <method name="GetChecksumAndModificationTimestamp">
    "       <arg name="checksumAndTimestamp" type="a(uut)" direction="out" />
    "   </method>
    */
    /**
     * The Class ChecksumAndTimestampValues.
     */
    public class ChecksumAndTimestampValues
    {
        
        /** The blob type. */
        @Position(0)
        public int blobType;
        
        /** The checksum. */
        @Position(1)
        public int checksum;
        
        /** The timestamp. */
        @Position(2)
        public long timestamp;
    }

    /**
     * Gets the checksum and modification timestamp.
     *
     * @return the checksum and timestamp values[]
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "a(uut)")
    public ChecksumAndTimestampValues[] GetChecksumAndModificationTimestamp() throws BusException;

    /*
    "   <method name="GetBlob">
    "       <arg name="blobType" type="u" direction="in" />
    "       <arg name="blobType" type="u" direction="out" />
    "       <arg name="blob" type="s" direction="out" />
    "       <arg name="checksum" type="u" direction="out" />
    "       <arg name="timestamp" type="t" direction="out" />
    "   </method>
    */
    /**
     * The Class BlobValues.
     */
    public class BlobValues
    {
        
        /** The blob type. */
        @Position(0)
        public int blobType;
        
        /** The blob. */
        @Position(1)
        public String blob;
        
        /** The checksum. */
        @Position(2)
        public int checksum;
        
        /** The timestamp. */
        @Position(3)
        public long timestamp;
    }

    /**
     * Gets the blob.
     *
     * @param blobType the blob type
     * @return the blob values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "u", replySignature = "usut")
    public BlobValues GetBlob(int blobType) throws BusException;

    /*
    "   <method name="Overthrow">
    "       <arg name="success" type="b" direction="out" />
    "   </method>
    */
    /**
     * Overthrow.
     *
     * @return true, if successful
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "b")
    public boolean Overthrow() throws BusException;

    /*
    "   <signal name="BlobChanged">
    "       <arg name="blobType" type="u" direction="out" />
    "       <arg name="blob" type="s" direction="out" />
    "       <arg name="checksum" type="u" direction="out" />
    "       <arg name="timestamp" type="t" direction="out" />
    "   </signal>
    */
    /**
     * Blob changed.
     *
     * @param blobType the blob type
     * @param blob the blob
     * @param checksum the checksum
     * @param timestamp the timestamp
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "usut")
    public void BlobChanged(int blobType, String blob, int checksum, long timestamp) throws BusException;
}
/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
//import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LeaderElectionAndStateSync")
public interface LeaderElectionBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public enum BlobType
    {
    	LSF_PRESET,
        LSF_LAMP_GROUP,
        LSF_SCENE,
        LSF_MASTER_SCENE,
        LSF_TRANSITION_EFFECT,
        LSF_PULSE_EFFECT,
        LSF_PRESET_UPDATE,
        LSF_LAMP_GROUP_UPDATE,
        LSF_SCENE_UPDATE,
        LSF_MASTER_SCENE_UPDATE,
        LSF_TRANSITION_EFFECT_UPDATE,
        LSF_PULSE_EFFECT_UPDATE,
        LSF_SCENE_ELEMENT,
        LSF_SCENE_2,
        LSF_BLOB_TYPE_LAST_VALUE
    }

    /*
    "   <method name="GetChecksumAndModificationTimestamp">
    "       <arg name="checksumAndTimestamp" type="a(uut)" direction="out" />
    "   </method>
    */
    public class ChecksumAndTimestampValues
    {
        @Position(0)
        public int blobType;

        @Position(1)
        public int checksum;

        @Position(2)
        public long timestamp;
    }

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
    public class BlobValues
    {
        @Position(0)
        public int blobType;

        @Position(1)
        public String blob;

        @Position(2)
        public int checksum;
        
        @Position(3)
        public long timestamp;
    }

    @BusMethod(signature = "u", replySignature = "usut")
    public BlobValues GetBlob(int blobType) throws BusException;

    /*
    "   <method name="Overthrow">
    "       <arg name="success" type="b" direction="out" />
    "   </method>
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
    @BusSignal(signature = "usut")
    public void BlobChanged(int blobType, String blob, int checksum, long timestamp) throws BusException;
}
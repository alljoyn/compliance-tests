/*******************************************************************************
*  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
*
*     Permission to use, copy, modify, and/or distribute this software for any
*     purpose with or without fee is hereby granted, provided that the above
*     copyright notice and this permission notice appear in all copies.
*
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

public class LeaderElectionBusObject implements BusObject, LeaderElectionBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "a(uut)")
    public ChecksumAndTimestampValues[] GetChecksumAndModificationTimestamp() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "u", replySignature = "usut")
    public BlobValues GetBlob(int blobType) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(replySignature = "b")
    public boolean Overthrow() throws BusException
    {
        return false;
    }

    @Override
    @BusSignal(signature = "usut")
    public void BlobChanged(int blobType, String blob, int checksum, long timestamp) throws BusException
    {
    }
}
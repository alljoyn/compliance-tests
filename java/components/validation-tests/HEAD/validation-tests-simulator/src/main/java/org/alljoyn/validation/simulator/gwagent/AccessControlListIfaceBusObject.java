/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator.gwagent;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.AccessControlListAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.AccessControlListIface;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestObjectDescriptionAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.RemotedAppAJ;

public class AccessControlListIfaceBusObject implements AccessControlListIface, BusObject
{

    @Override
    public short activateAcl() throws BusException
    {
        return 0;
    }

    @Override
    public short deactivateAcl() throws BusException
    {
        return 0;
    }

    @Override
    public AccessControlListAJ getAcl() throws BusException
    {
        return null;
    }

    @Override
    public short getAclStatus() throws BusException
    {
        return 0;
    }

    @Override
    public short updateAcl(String aclName, ManifestObjectDescriptionAJ[] exposedServices, RemotedAppAJ[] remotedApps, Map<String, String> internalMetadata,
            Map<String, String> customMetadata) throws BusException
    {
        return 0;
    }

    @Override
    public short updateAclMetadata(Map<String, String> metadata) throws BusException
    {
        return 0;
    }

    @Override
    public short updateAclCustomMetadata(Map<String, String> internalMetadata) throws BusException
    {
        return 0;
    }

    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

}
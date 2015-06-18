/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.gatewaycontroller.sdk.managerinterfaces;

import java.util.Map;

import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * Access Control List data
 */
public class AccessControlListAJ {

    /**
     * ACL Name
     */
    @Position(0)
    public String aclName;

    /**
     * Exposed services
     */
    @Position(1)
    public ManifestObjectDescriptionAJ[] exposedServices;

    /**
     * Remoted apps
     */
    @Position(2)
    public RemotedAppAJ[] remotedApps;

    /**
     * Internal metadata
     */
    @Position(3)
    public Map<String, String> internalMetadata;

    /**
     * Custom Metadata
     */
    @Position(4)
    public Map<String, String> customMetadata;
}
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

package org.alljoyn.gatewaycontroller.sdk;

import org.alljoyn.gatewaycontroller.sdk.Acl.AclResponseCode;

// TODO: Auto-generated Javadoc
/**
 * The class holds the {@link AclResponseCode} returned by the invocation of the
 * createAcl or updateAcl methods. In addition it holds the {@link AclRules}
 * object with the rules that do not comply with the {@link ConnectorCapabilities}
 */
public class AclWriteResponse {

    /**
     * Acl id
     */
    private final String id;

    /**
     * Response code
     */
    private final AclResponseCode code;

    /**
     * ACL object path
     */
    private final String objectPath;

    /**
     * The rules that don't comply with the {@link ConnectorCapabilities}
     */
    private final AclRules invalidRules;

    /**
     * Constructor
     * 
     * @param id
     *            ACL id
     * @param code
     *            {@link AclResponseCode}
     * @param invalidRules
     *            {@link AclRules}
     * @param objPath
     *            Object path
     */
    AclWriteResponse(String id, AclResponseCode code, AclRules invalidRules, String objPath) {

        this.id             = id;
        this.code           = code;
        this.invalidRules   = invalidRules;
        this.objectPath     = objPath;
    }

    /**
     * @return The id of the ACL that the write operation was referred to
     */
    public String getAclId() {
        return id;
    }

    /**
     * @return {@link AclResponseCode} of the ACL write action
     */
    public AclResponseCode getResponseCode() {
        return code;
    }

    /**
     * @return {@link AclRules} with the rules that don't comply with the {@link ConnectorCapabilities}
     */
    public AclRules getInvalidRules() {
        return invalidRules;
    }

    /**
     * @return {@link Acl} object path
     */
    public String getObjectPath() {
        return objectPath;
    }
}
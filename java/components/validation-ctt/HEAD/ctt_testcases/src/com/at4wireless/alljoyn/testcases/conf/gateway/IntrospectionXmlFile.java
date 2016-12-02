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
package com.at4wireless.alljoyn.testcases.conf.gateway;

// TODO: Auto-generated Javadoc
/**
 * The Enum IntrospectionXmlFile.
 */
public enum IntrospectionXmlFile
{
    
    /** The GW agent ctrl app mgmt. */
    GWAgentCtrlAppMgmt("introspection-xml/GWAgentCtrlAppMgmt.xml"),
    
    /** The GW agent ctrl app. */
    GWAgentCtrlApp("introspection-xml/GWAgentCtrlApp.xml"),
    
    /** The GW agent ctrl acl mgmt. */
    GWAgentCtrlAclMgmt("introspection-xml/GWAgentCtrlAclMgmt.xml"),
    
    /** The GW agent ctrl acl. */
    GWAgentCtrlAcl("introspection-xml/GWAgentCtrlAcl.xml");

    /** The value. */
    private String value;

    /**
     * Instantiates a new introspection xml file.
     *
     * @param value the value
     */
    IntrospectionXmlFile(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
}
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
package com.at4wireless.alljoyn.core.onboarding;


/**
 * The Class WifiNetworkConfig.
 */
public class WifiNetworkConfig
{
    
    /** The ssid. */
    private String ssid;
    
    /** The passphrase. */
    private String passphrase = "";
    
    /** The security type. */
    private String securityType = "NONE";

    /**
     * Instantiates a new wifi network config.
     *
     * @param ssid the ssid
     */
    public WifiNetworkConfig(String ssid)
    {
        this(ssid, null, null);
    }

    /**
     * Instantiates a new wifi network config.
     *
     * @param ssid the ssid
     * @param passphrase the passphrase
     * @param securityType the security type
     */
    public WifiNetworkConfig(String ssid, String passphrase, String securityType)
    {
        this.ssid = ssid;
        this.passphrase = passphrase;
        this.securityType = securityType;
    }

    /**
     * Gets the security type.
     *
     * @return the security type
     */
    public String getSecurityType()
    {
        return securityType;
    }

    /**
     * Sets the security type.
     *
     * @param securityType the new security type
     */
    public void setSecurityType(String securityType)
    {
        this.securityType = securityType;
    }

    /**
     * Gets the passphrase.
     *
     * @return the passphrase
     */
    public String getPassphrase()
    {
        return passphrase;
    }

    /**
     * Sets the passphrase.
     *
     * @param passphrase the new passphrase
     */
    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    /**
     * Gets the ssid.
     *
     * @return the ssid
     */
    public String getSsid()
    {
        return ssid;
    }

    /**
     * Sets the ssid.
     *
     * @param ssid the new ssid
     */
    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }
}
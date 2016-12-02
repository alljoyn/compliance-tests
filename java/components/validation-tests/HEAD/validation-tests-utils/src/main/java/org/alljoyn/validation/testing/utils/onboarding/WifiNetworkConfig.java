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
package org.alljoyn.validation.testing.utils.onboarding;

public class WifiNetworkConfig
{
    private String ssid;
    private String passphrase = "";
    private String securityType = "NONE";

    public WifiNetworkConfig(String ssid)
    {
        this(ssid, null, null);
    }

    public WifiNetworkConfig(String ssid, String passphrase, String securityType)
    {
        this.ssid = ssid;
        this.passphrase = passphrase;
        this.securityType = securityType;
    }

    public String getSecurityType()
    {
        return securityType;
    }

    public void setSecurityType(String securityType)
    {
        this.securityType = securityType;
    }

    public String getPassphrase()
    {
        return passphrase;
    }

    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    public String getSsid()
    {
        return ssid;
    }

    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }
}
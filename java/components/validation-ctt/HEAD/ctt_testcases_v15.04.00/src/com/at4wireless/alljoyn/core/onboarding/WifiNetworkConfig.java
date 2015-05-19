/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.onboarding;

// TODO: Auto-generated Javadoc
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

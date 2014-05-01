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
package org.alljoyn.validation.testing.utils.onboarding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

public class SoftAPValidator extends Assert
{
    private static final String SOFT_AP_ASSERT_MESSAGE = "Soft AP must match pattern AJ_{up to first 7 characters of manufacturer name}_{first 2 characters of device description}_{last 7 digits of deviceId}";
    private static final String SOFT_AP_PATTERN = "AJ_([^_]+)_([^_]+)_([^_]+)";

    public static void validateSoftAP(String softAPName, String deviceId)
    {
        Pattern pattern = Pattern.compile(SOFT_AP_PATTERN);
        Matcher matcher = pattern.matcher(softAPName);

        assertTrue(SOFT_AP_ASSERT_MESSAGE, matcher.matches());

        assertEquals(SOFT_AP_ASSERT_MESSAGE, deviceId.substring(deviceId.length() - 7), matcher.group(3));
    }
}

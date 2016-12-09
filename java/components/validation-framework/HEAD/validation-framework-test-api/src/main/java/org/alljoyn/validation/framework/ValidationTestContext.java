/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.framework;

/**
 * This interface defines methods that provide information between the tests and
 * framework classes.
 */
public interface ValidationTestContext extends TestCaseNoteListener, UserInputHandler, InterfaceDetailsListener
{
    /**
     * @return Android test context
     */
    Object getContext();

    /**
     * @return object that contains details about the app that is being tested
     */
    AppUnderTestDetails getAppUnderTestDetails();

    /**
     * @return path of the object being tested on the Alljoyn bus, e.g.
     *         {@code /About}
     */
    String getTestObjectPath();

    /**
     * @return path of the KeyStore location
     */
    String getKeyStorePath();

    /**
     * Provides the value of the provided parameter name
     * 
     * @param parameterName
     *            name of the parameter, e.g.
     *            {@code org.alljoyn.Onboarding.PersonalApSsid}
     * @return value of parameter
     */
    Object getTestParameter(String parameterName);
}
/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.utils.log.android;

import org.alljoyn.validation.testing.utils.log.Logger;

import android.util.Log;

public class AndroidLoggerImpl implements Logger
{
    private final String tag;

    public AndroidLoggerImpl(String tag)
    {
        this.tag = tag;
        if (tag.length() > 23)
        {
            throw new RuntimeException("Tag too long");
        }
    }

    @Override
    public void error(String format, Object... args)
    {
        Log.e(tag, getMessage(format, args));
    }

    @Override
    public void warn(String format, Object... args)
    {
        Log.w(tag, getMessage(format, args));
    }

    @Override
    public void info(String format, Object... args)
    {
        Log.i(tag, getMessage(format, args));
    }

    @Override
    public void debug(String format, Object... args)
    {
        Log.d(tag, getMessage(format, args));
    }

    private String getMessage(String format, Object[] args)
    {
        String msg = format;
        if (args.length > 0)
        {
            msg = String.format(format, args);
        }
        return msg;
    }

    @Override
    public void error(String message, Throwable t)
    {
        Log.e(tag, message, t);
    }

    @Override
    public void warn(String message, Throwable t)
    {
        Log.w(tag, message, t);
    }

    @Override
    public void info(String message, Throwable t)
    {
        Log.i(tag, message, t);
    }

    @Override
    public void debug(String message, Throwable t)
    {
        Log.d(tag, message, t);
    }

}
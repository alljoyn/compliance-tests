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
package com.at4wireless.alljoyn.core.commons.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowsLoggerImpl implements Logger
{
    private final String tag;

    public WindowsLoggerImpl(String tag)
    {
    	this.tag = tag;
        if (tag.length() > 30)
        {
            throw new RuntimeException("Tag too long");
        }
    }

    @Override
    public void error(String format, Object... args)
    {
    	printLog("ERROR", tag, getLineNumber(), getMessage(format, args));
    }

    @Override
    public void warn(String format, Object... args)
    {
    	printLog("WARN", tag, getLineNumber(), getMessage(format, args));
    }

    @Override
    public void info(String format, Object... args)
    {
    	printLog("INFO", tag, getLineNumber(), getMessage(format, args));
    }

    @Override
    public void debug(String format, Object... args)
    {
    	printLog("DEBUG", tag, getLineNumber(), getMessage(format, args));
    }
    
    @Override
	public void raw(String format, Object... args)
    {
    	System.out.println(getMessage(format, args));
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
    	printLog("ERROR", tag, getLineNumber(), String.format("%s: %s", message, t.getMessage()));
    }

    @Override
    public void warn(String message, Throwable t)
    {
    	printLog("WARN", tag, getLineNumber(), String.format("%s: %s", message, t.getMessage()));
    }

    @Override
    public void info(String message, Throwable t)
    {
    	printLog("INFO", tag, getLineNumber(), String.format("%s: %s", message, t.getMessage()));
    }

    @Override
    public void debug(String message, Throwable t)
    {
    	printLog("DEBUG", tag, getLineNumber(), String.format("%s: %s", message, t.getMessage()));
    }
	
	public void noTag(String format)
	{
		printNoTag(format);
	}
	
	private void printNoTag(String format)
	{
		System.out.println(format);
	}
	
	private void printLog(String level, String tag, int lineNumber, String message)
	{
		System.out.println(String.format("%s %s %s(%d) : %s", getTimeStamp(), level, tag, lineNumber, message));
	}

	public String getTimeStamp()
	{
		Date utilDate = new java.util.Date(); 
		long lnMilisegundos = utilDate.getTime();
	  
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisegundos);
	
		String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(sqlTimestamp);
		return timeStamp;
	}
	
	public static int getLineNumber()
	{
	    return Thread.currentThread().getStackTrace()[3].getLineNumber();
	}
}
/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.core.commons.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowsLoggerImpl implements Logger
{
    private final String tag;

    public WindowsLoggerImpl(String tag)
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
    	 Print("E/"+tag+"("+getPID()+"):"+format);
        
    }

    @Override
    public void warn(String format, Object... args)
    {
    	 Print("W/"+tag+"("+getPID()+"):"+format);
    }

    @Override
    public void info(String format, Object... args)
    {
    	 Print("I/"+tag+"("+getPID()+"):"+format);
    }

    @Override
    public void debug(String format, Object... args)
    {
    	 Print("D/"+tag+"("+getPID()+"):"+format);	 
    }

    @Override
    public void error(String message, Throwable t)
    {
    	 Print("D/"+tag+"("+getPID()+"):"+message);
    }

    @Override
    public void warn(String message, Throwable t)
    {
    	 Print("W/"+tag+"("+getPID()+"):"+message);
    }

    @Override
    public void info(String message, Throwable t)
    {
    	 Print("I/"+tag+"("+getPID()+"):"+message);
    }

    @Override
    public void debug(String message, Throwable t)
    {
    	 Print("D/"+tag+"("+getPID()+"):"+message);
    }

	@Override
	public void pass(String format)
	{
		System.out.println(tag+"("+getPID()+"): PASS: "+format);
	}

	@Override
	public void fail(String format)
	{
		Print("F/"+format);
	}
	
	@Override
	public void addNote(String format)
	{
		Print("D/"+tag+"("+getPID()+"):Note added:"+format);
	}
	
	public void noTag(String format)
	{
		printNoTag(format);
	}
	
	private void printNoTag(String format)
	{
		System.out.println(format);
	}
	
	private void Print(String string)
	{
		String str = getTimeStamp()+":"+string;
		System.out.println(str);
	}

	public String getTimeStamp()
	{
		Date utilDate = new java.util.Date(); 
		long lnMilisegundos = utilDate.getTime();
	  
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisegundos);
	
		String timeStamp= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(sqlTimestamp);
		return timeStamp;
	}
	
	public static long getPID()
	{
	    String processName = 
	    		java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	}
}


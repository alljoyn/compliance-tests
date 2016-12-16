/*
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.commons.log;

import java.text.SimpleDateFormat;
import java.util.Date;







// TODO: Auto-generated Javadoc
/**
 * The Class WindowsLoggerImpl.
 */
public class WindowsLoggerImpl implements Logger
{
    
    /** The tag. */
    private final String tag;

    /**
     * Instantiates a new windows logger impl.
     *
     * @param tag the tag
     */
    public WindowsLoggerImpl(String tag)
    {
        this.tag = tag;
        if (tag.length() > 23)
        {
            throw new RuntimeException("Tag too long");
        }
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#error(java.lang.String, java.lang.Object[])
     */
    @Override
    public void error(String format, Object... args)
    {
    	// System.out.println (tag+": ERROR: "+format);
    	 Print("E/"+tag+"("+getPID()+"):"+format);
        
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(String format, Object... args)
    {
    	// System.out.println (tag+": WARN: "+format);
    	 Print("W/"+tag+"("+getPID()+"):"+format);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(String format, Object... args)
    {
    	 //System.out.println (tag+": INFO: "+format);
    	 Print("I/"+tag+"("+getPID()+"):"+format);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(String format, Object... args)
    {
    	// System.out.println (tag+": DEBryreyUG: "+format); 
    	 Print("D/"+tag+"("+getPID()+"):"+format);	 
    }

 

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(String message, Throwable t)
    {
    	 //System.out.println (tag+": ERROR: "+message+": "+t.getMessage());
    	 Print("D/"+tag+"("+getPID()+"):"+message);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(String message, Throwable t)
    {
    	// System.out.println (tag+": WARN: "+message+": "+t.getMessage());
    	 Print("W/"+tag+"("+getPID()+"):"+message);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(String message, Throwable t)
    {
    	// System.out.println (tag+": INFO: "+message+": "+t.getMessage());
    	 Print("I/"+tag+"("+getPID()+"):"+message);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(String message, Throwable t)
    {
    	 //System.out.println (tag+": DEBUG: "+message+": "+t.getMessage());  
    	 Print("D/"+tag+"("+getPID()+"):"+message);
    	 }

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.commons.log.Logger#pass(java.lang.String)
	 */
	@Override
	public void pass(String format) {
		// TODO Auto-generated method stub
		System.out.println (tag+"("+getPID()+"): PASS: "+format);
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.commons.log.Logger#fail(java.lang.String)
	 */
	@Override
	public void fail(String format) {
		// TODO Auto-generated method stub
		//System.out.println (tag+": FAIL: "+format);
		Print("F/"+format);
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.core.commons.log.Logger#addNote(java.lang.String)
	 */
	@Override
	public void addNote(String format) {
		// TODO Auto-generated method stub
		Print("D/"+tag+"("+getPID()+"):Note added:"+format);
		
		//05-19 12:33:25.500: D/InstrumentationTest(4663): Setting up test
		
	}
	
	
	
	/**
	 * Prints the.
	 *
	 * @param string the string
	 */
	private void Print(String string) {
		// TODO Auto-generated method stub
		
		
		System.out.println(getTimeStamp()+":"+string);
		
	}

	
	
	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public String getTimeStamp(){
	Date utilDate = new java.util.Date(); //fecha actual
	  long lnMilisegundos = utilDate.getTime();
	  
	  java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisegundos);
	
	 String timeStamp= new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(sqlTimestamp);
	 
	 
	  return timeStamp;
	}
	
	
	
	
	
	
	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public static long getPID() {
	    String processName =
	      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	  }
	
	
	
	
	
	
	
	
	

}
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
package org.alljoyn.ns.nativeplatform;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alljoyn.ns.commons.GenericLogger;

import com.at4wireless.alljoyn.core.commons.log.Logger;







public class WindowsLogger implements GenericLogger
{
    private final String tag;

   
   
	
	
	
	public WindowsLogger() {
		tag=null;
	}





	private void Print(String string) {
	
		
		
		System.out.println(getTimeStamp()+":"+string);
		
	}

	
	
	public String getTimeStamp(){
	Date utilDate = new java.util.Date(); //fecha actual
	  long lnMilisegundos = utilDate.getTime();
	  
	  java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisegundos);
	
	 String timeStamp= new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(sqlTimestamp);
	 
	 
	  return timeStamp;
	}
	
	
	
	
	
	
	public static long getPID() {
	    String processName =
	      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	  }

	@Override
	public void debug(String TAG, String msg) {
		 Print("D/"+TAG+"("+getPID()+"):"+msg);
		
	}

	@Override
	public void info(String TAG, String msg) {
		 Print("I/"+TAG+"("+getPID()+"):"+msg);
		
	}

	@Override
	public void warn(String TAG, String msg) {
		 Print("W/"+TAG+"("+getPID()+"):"+msg);
		
	}

	@Override
	public void error(String TAG, String msg) {
		 Print("E/"+TAG+"("+getPID()+"):"+msg);
		
	}

	@Override
	public void fatal(String TAG, String msg) {
		 Print("F/"+TAG+"("+getPID()+"):"+msg);
		
	}
	
	
	
	
	
	
	
	
	

}

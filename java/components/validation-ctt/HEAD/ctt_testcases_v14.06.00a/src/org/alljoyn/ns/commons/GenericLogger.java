/******************************************************************************
 * Copyright (c) 2013-2014, AllSeen Alliance. All rights reserved.
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
 ******************************************************************************/

package org.alljoyn.ns.commons;

/**
 * Implement this interface to provide logging functionality for the Notification service
 */
public interface GenericLogger {
	/**
	 * Debug level message
	 * @param TAG Tag to be added to the message, i.e. class that writes the message 
	 * @param msg 
	 */
	public void debug(String TAG, String msg);
	
	/**
	 * Info level message
	 * @param TAG Tag to be added to the message, i.e. class that writes the message 
	 * @param msg 
	 */
	public void info(String TAG, String msg);

	/**
	 * Warn level message
	 * @param TAG Tag to be added to the message, i.e. class that writes the message 
	 * @param msg 
	 */
	public void warn(String TAG, String msg);

	/**
	 * Error level message
	 * @param TAG Tag to be added to the message, i.e. class that writes the message 
	 * @param msg 
	 */
	public void error(String TAG, String msg);
	
	/**
	 * Fatal level message
	 * @param TAG Tag to be added to the message, i.e. class that writes the message 
	 * @param msg 
	 */
	public void fatal(String TAG, String msg);
}

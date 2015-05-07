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
package com.at4wireless.alljoyn.core.notification;


import org.alljoyn.ns.commons.GenericLogger;
import org.alljoyn.ns.commons.NativePlatform;

import com.at4wireless.alljoyn.core.commons.GenericLoggerImp;

// TODO: Auto-generated Javadoc
/**
 * The Class NativePlatformWindows.
 */
public class NativePlatformWindows implements  NativePlatform {

	/**
	 * Reference to logger
	 */
	protected GenericLogger logger; 
	
	/**
	 * Constructor
	 */
	public NativePlatformWindows() {
		createLogger();
	}

	/**
	 * Creates and set logger object
	 */
	protected  void createLogger() {
		logger=new GenericLoggerImp(); 
	}
	
	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#getNativeLogger()
	 */
	@Override
	public GenericLogger getNativeLogger() {
		return logger;
	}

	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#setNativeLogger(org.alljoyn.ns.commons.GenericLogger)
	 */
	@Override
	public void setNativeLogger(GenericLogger logger) {
		this.logger = logger;
	}
}

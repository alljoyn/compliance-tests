/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.commons;

import org.alljoyn.ns.commons.GenericLogger;


/**
 * The Class GenericLoggerImp, it doesn´t print anything to only be able to see
 * sended notifications in the Notification Receiver App.
 */
public class GenericLoggerImp implements GenericLogger {

	/* (non-Javadoc)
	 * @see org.alljoyn.ns.commons.GenericLogger#debug(java.lang.String, java.lang.String)
	 */
	@Override
	public void debug(String TAG, String msg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.alljoyn.ns.commons.GenericLogger#info(java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String TAG, String msg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.alljoyn.ns.commons.GenericLogger#warn(java.lang.String, java.lang.String)
	 */
	@Override
	public void warn(String TAG, String msg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.alljoyn.ns.commons.GenericLogger#error(java.lang.String, java.lang.String)
	 */
	@Override
	public void error(String TAG, String msg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.alljoyn.ns.commons.GenericLogger#fatal(java.lang.String, java.lang.String)
	 */
	@Override
	public void fatal(String TAG, String msg) {
		// TODO Auto-generated method stub

	}

}
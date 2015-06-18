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

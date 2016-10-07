/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.suites;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.bus.BusObject;

import android.util.*;


public class LampStateSignalHandler
{
	//Load AllJoyn Library
	static
	{
		System.loadLibrary("alljoyn_java");
	}
	
	/* Interface variables */
	private LSF_LampTestSuite updateListener;
	
	public LampStateSignalHandler()
	{
		//Empty Constructor
	}
	
	public void setUpdateListener(LSF_LampTestSuite listener)
	{
		updateListener = listener;
	}

	@BusSignalHandler(iface="org.allseen.LSF.LampState", signal="LampStateChanged")
	public void handleLampStateChanged(String LampID)
	{
		Log.d("LampStateSignalHandler", "LampStateChanged for LampID: " + LampID);
		
		if (updateListener != null)
		{
			updateListener.handleLampStateChanged(LampID);
		}
	}
}
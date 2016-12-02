/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.testing.suites;

import java.util.Map;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;

public interface AnnouncementReceivedListener
{
	public void announcementReceived(String peerName, short port, BusObjectDescription[] interfaces, Map<String, Variant> aboutMap);
}
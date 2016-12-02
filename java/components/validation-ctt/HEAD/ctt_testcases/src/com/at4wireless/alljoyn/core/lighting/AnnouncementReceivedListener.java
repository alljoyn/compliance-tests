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
package com.at4wireless.alljoyn.core.lighting;

import java.util.Map;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving announcementReceived events.
 * The class that is interested in processing a announcementReceived
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addAnnouncementReceivedListener<code> method. When
 * the announcementReceived event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AnnouncementReceivedEvent
 */
public interface AnnouncementReceivedListener
{
	
	/**
	 * Announcement received.
	 *
	 * @param peerName the peer name
	 * @param port the port
	 * @param interfaces the interfaces
	 * @param aboutMap the about map
	 */
	public void announcementReceived(String peerName, short port, BusObjectDescription[] interfaces, Map<String, Variant> aboutMap);
}
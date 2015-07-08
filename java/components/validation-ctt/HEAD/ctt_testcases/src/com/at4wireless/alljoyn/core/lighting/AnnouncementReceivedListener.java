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

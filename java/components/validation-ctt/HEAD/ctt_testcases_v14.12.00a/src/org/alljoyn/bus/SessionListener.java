/*
 * Copyright (c) 2009-2011, 2013-2014, AllSeen Alliance. All rights reserved.
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

package org.alljoyn.bus;

/**
 * A SessionListener is responsible for handling session related callbacks from
 * the AllJoyn system. It is expected that a user of the AllJoyn bus will
 * specialize this class in order to respond to AllJoyn session related events.
 *
 * Listener objects are the Java objects that handle notification events and are
 * called from AllJoyn in the context of one of its threads.  All listener
 * objects are expected to be multithread safe (MT-Safe) between construction
 * and destruction.  That is, every thread executing in a listener object's
 * methods 1) gets a unique copy of all temporary data (it is re-entrant); and
 * 2) all shared data -- the object instance's member variables or any globals
 * must contain no read-modify-write access patterns (okay to write or read,
 * just never to read-modify-write).  If such access patterns are required, it
 * is the responsibility of the client to, for example, add the synchronized
 * keyword when overriding one of the listener methods or provide some other
 * serialization mechanism in order to preserve MT-Safe operation.
 *
 * This rule extends to other objects accessed during processing of
 * notifications.  For example, it is a programming error to allow a notifiation
 * method to update a collection in another object without serializing access
 * to the collection.
 *
 * The important consideration in this case is that as soon as one sets up a
 * listener object to receive notifications from AllJoyn, one is implicitly
 * dealing with multithreaded code.
 *
 * Since listener objects generally run in the context of the AllJoyn thread
 * which manages reception of events, If a blocking AllJoyn call is made in
 * the context of a notification, the necessary and sufficient conditions for
 * deadlock are established.
 *
 * The important consideration in this case is that when one receives a
 * notification from AllJoyn, that notification is executing in the context of
 * an AllJoyn thread.  If one makes a blocking call back into AllJoyn on that
 * thread, a deadlock cycle is likely, and if this happens your bus attachment
 * receive thread will deadlock (with itself).  The deadlock is typically broken
 * after a bus timeout eventually happens.
 */
public class SessionListener {

    /**
     * Create native resources held by objects of this class.
     */
    public SessionListener() {
        create();
    }

    /**
     * Destroy native resources held by objects of this class.
     */
    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    /**
     * Create any native resources held by objects of this class.  Specifically,
     * we allocate a C++ counterpart of this listener object.
     */
    private native void create();

    /**
     * Release any native resources held by objects of this class.
     * Specifically, we may delete a C++ counterpart of this listener object.
     */
    private native void destroy();

    /**
     * Invalid SessionLost code.
     */
    public static final int ALLJOYN_SESSIONLOST_INVALID                      = 0x00;

    /**
     * Remote end called LeaveSession.
     */
    public static final int ALLJOYN_SESSIONLOST_REMOTE_END_LEFT_SESSION      = 0x01;

    /**
     * Remote end closed abruptly.
     */
    public static final int ALLJOYN_SESSIONLOST_REMOTE_END_CLOSED_ABRUPTLY   = 0x02;

    /**
     * Session binder removed this endpoint by calling RemoveSessionMember.
     */
    public static final int ALLJOYN_SESSIONLOST_REMOVED_BY_BINDER            = 0x03;

    /**
     * Link was timed-out.
     */
    public static final int ALLJOYN_SESSIONLOST_LINK_TIMEOUT                 = 0x04;

    /**
     * Unspecified reason for session loss.
     */
    public static final int ALLJOYN_SESSIONLOST_REASON_OTHER                 = 0x05;

    /**
     * Session binder removed its joiner part by calling RemoveSessionMember (selfjoin only)
     */
    public static final int ALLJOYN_SESSIONLOST_REMOVED_BY_BINDER_SELF       = 0x06;

    /**
     * Called by the bus when a session becomes disconnected.
     *
     * Any implementation of this function must be multithread safe.  See the
     * class documentation for details.
     *
     * @deprecated Use {@link #sessionLost(int,int)}.
     *
     * @param sessionId     Id of session that was lost.
     */
    @Deprecated
    public void sessionLost(int sessionId) {}

    /**
     * Called by the bus when a session becomes disconnected.
     *
     * Any implementation of this function must be multithread safe.  See the
     * class documentation for details.
     *
     * @param sessionId     Id of session that was lost.
     * @param reason        Reason for the session being lost. One of:
     *                      ALLJOYN_SESSIONLOST_REMOTE_END_LEFT_SESSION
     *                      ALLJOYN_SESSIONLOST_REMOTE_END_CLOSED_ABRUPTLY
     *                      ALLJOYN_SESSIONLOST_REMOVED_BY_BINDER
     *                      ALLJOYN_SESSIONLOST_LINK_TIMEOUT
     *                      ALLJOYN_SESSIONLOST_REASON_OTHER
     */
    public void sessionLost(int sessionId, int reason) {}

    /**
     * Called by the bus for multipoint sessions when another node joins the session.
     *
     * @param sessionId     Id of multipoint session whose members have changed.
     * @param uniqueName    Unique name of member who joined the session.
     */
    public void sessionMemberAdded(int sessionId, String uniqueName) {}

    /**
     * Called by the bus for multipoint sessions when another node leaves the session.
     *
     * @param sessionId     Id of multipoint session whose members have changed.
     * @param uniqueName    Unique name of member who left the session.
     */
    public void sessionMemberRemoved(int sessionId, String uniqueName) {}

    /**
     * The opaque pointer to the underlying C++ object which is actually tied
     * to the AllJoyn code.
     */
    private long handle = 0;
}

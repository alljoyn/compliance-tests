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
package com.at4wireless.alljoyn.core.notification;

import org.alljoyn.ns.NotificationMessageType;



// TODO: Auto-generated Javadoc
/**
 * The Class MsgSet.
 */
class MsgSet
{
    
    /**
     * Instantiates a new msg set.
     *
     * @param text the text
     * @param priority the priority
     */
    public MsgSet(String text, NotificationMessageType priority)
    {
        this.text = text;
        this.priority = priority;
    }

    /** The text. */
    public String text;
    
    /** The priority. */
    public NotificationMessageType priority;
}
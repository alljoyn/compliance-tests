package com.at4wireless.alljoyn.core.notification;

import org.alljoyn.ns.NotificationMessageType;



class MsgSet
{
    public MsgSet(String text, NotificationMessageType priority)
    {
        this.text = text;
        this.priority = priority;
    }

    public String text;
    public NotificationMessageType priority;
}
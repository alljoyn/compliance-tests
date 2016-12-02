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
package com.at4wireless.alljoyn.core.commons.log;

// TODO: Auto-generated Javadoc
/**
 * The Interface Logger.
 */
public interface Logger
{
    
    /**
     * Error.
     *
     * @param format the format
     * @param args the args
     */
    void error(String format, Object... args);

    /**
     * Warn.
     *
     * @param format the format
     * @param args the args
     */
    void warn(String format, Object... args);

    /**
     * Info.
     *
     * @param format the format
     * @param args the args
     */
    void info(String format, Object... args);

    /**
     * Debug.
     *
     * @param format the format
     * @param args the args
     */
    void debug(String format, Object... args);

    /**
     * Error.
     *
     * @param message the message
     * @param t the t
     */
    void error(String message, Throwable t);

    /**
     * Warn.
     *
     * @param message the message
     * @param t the t
     */
    void warn(String message, Throwable t);

    /**
     * Info.
     *
     * @param message the message
     * @param t the t
     */
    void info(String message, Throwable t);

    /**
     * Debug.
     *
     * @param message the message
     * @param t the t
     */
    void debug(String message, Throwable t);
    

    /**
     * Pass.
     *
     * @param format the format
     */
    void pass(String format);
    		
    /**
     * Fail.
     *
     * @param format the format
     */
    void fail(String format);
    
    /**
     * Adds the note.
     *
     * @param format the format
     */
    void addNote(String format);
}
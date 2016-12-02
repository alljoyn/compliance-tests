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
package com.at4wireless.alljoyn.core.commons.log;

public interface Logger
{
    void error(String format, Object... args);

    void warn(String format, Object... args);

    void info(String format, Object... args);

    void debug(String format, Object... args);
    
    void raw(String format, Object... args);

    void error(String message, Throwable t);

    void warn(String message, Throwable t);

    void info(String message, Throwable t);

    void debug(String message, Throwable t);
}
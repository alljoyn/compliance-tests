/******************************************************************************
  *   * 
  *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
  *    Source Project Contributors and others.
  *    
  *    All rights reserved. This program and the accompanying materials are
  *    made available under the terms of the Apache License, Version 2.0
  *    which accompanies this distribution, and is available at
  *    http://www.apache.org/licenses/LICENSE-2.0

  ******************************************************************************/

package org.allseen.timeservice.server;

import java.util.concurrent.atomic.AtomicInteger;

// TODO: Auto-generated Javadoc
/**
 * This is utility class that is used to append a unique number to received string.
 * This class is not designed to be initialized.
 */
class GlobalStringSequencer {

    /**
     * Current number
     */
    private static final AtomicInteger CURRENT = new AtomicInteger(0);

    /**
     * Private Constructor
     */
    private GlobalStringSequencer() {
    }

    /**
     * Initialize CURRENT value
     */
    public static void init() {

        CURRENT.set(0);
    }

    /**
     * Appends to the given prefix the current number.
     * @param prefix String to be appended
     * @return Appended string
     */
    public static String append(String prefix) {

        return prefix + CURRENT.incrementAndGet();
    }
}
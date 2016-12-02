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

package org.allseen.timeservice;

// TODO: Auto-generated Javadoc
/**
 * {@link TimeServiceException} is thrown when there is a problem in the Time Service functionality
 */
public class TimeServiceException extends Exception {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5319795501514525468L;

    /**
     * Instantiates a new time service exception.
     */
    public TimeServiceException() {
        super();
    }

    /**
     * Instantiates a new time service exception.
     *
     * @param detailMessage the detail message
     * @param throwable the throwable
     */
    public TimeServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Instantiates a new time service exception.
     *
     * @param detailMessage the detail message
     */
    public TimeServiceException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Instantiates a new time service exception.
     *
     * @param throwable the throwable
     */
    public TimeServiceException(Throwable throwable) {
        super(throwable);
    }
}
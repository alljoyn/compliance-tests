 /******************************************************************************
  * Copyright AllSeen Alliance. All rights reserved.
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
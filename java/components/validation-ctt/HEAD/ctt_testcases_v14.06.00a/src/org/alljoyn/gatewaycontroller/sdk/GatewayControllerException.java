/*
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
 */

package org.alljoyn.gatewaycontroller.sdk;

// TODO: Auto-generated Javadoc
/**
 * This exception is thrown when a failure occurs in the Gateway Controller SDK
 */
public class GatewayControllerException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1939865232952438606L;

    /**
     * Instantiates a new gateway controller exception.
     */
    public GatewayControllerException() {
        super();
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param detailMessage the detail message
     * @param throwable the throwable
     */
    public GatewayControllerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param detailMessage the detail message
     */
    public GatewayControllerException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param throwable the throwable
     */
    public GatewayControllerException(Throwable throwable) {
        super(throwable);
    }
}
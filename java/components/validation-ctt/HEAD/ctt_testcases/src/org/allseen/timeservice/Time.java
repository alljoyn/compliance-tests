/******************************************************************************
  * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
  *    Source Project (AJOSP) Contributors and others.
  *
  *    SPDX-License-Identifier: Apache-2.0
  *
  *    All rights reserved. This program and the accompanying materials are
  *    made available under the terms of the Apache License, Version 2.0
  *    which accompanies this distribution, and is available at
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  *    Copyright 2016 Open Connectivity Foundation and Contributors to
  *    AllSeen Alliance. All rights reserved.
  *
  *    Permission to use, copy, modify, and/or distribute this software for
  *    any purpose with or without fee is hereby granted, provided that the
  *    above copyright notice and this permission notice appear in all
  *    copies.
  *
  *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
  *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
  *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
  *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
  *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
  *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
  *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
  *     PERFORMANCE OF THIS SOFTWARE.
  ******************************************************************************/

package org.allseen.timeservice;

// TODO: Auto-generated Javadoc
/**
 * Time data type which is used by the Alarm and Clock
 */
public class Time {

    /** The hour. */
    private final byte HOUR;

    /** The minute. */
    private final byte MINUTE;

    /** The second. */
    private final byte SECOND ;

    /** The millisecond. */
    private final short MILLISECOND ;

    /**
     * Constructor
     * @param hour Expected range: 0-23
     * @param minute Expected range: 0-59
     * @param second Expected range: 0-59
     * @param millisecond Expected range: 0-999
     * @throws IllegalArgumentException Is thrown if received arguments are not in the correct range
     */
    public Time(byte hour, byte minute, byte second, short millisecond) {

        checkValidity(hour, minute, second, millisecond);

        HOUR         = hour;
        MINUTE       = minute;
        SECOND       = second;
        MILLISECOND  = millisecond;
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public byte getHour() {
        return HOUR;
    }

    /**
     * Gets the minute.
     *
     * @return the minute
     */
    public byte getMinute() {
        return MINUTE;
    }

    /**
     * Gets the seconds.
     *
     * @return the seconds
     */
    public byte getSeconds() {
        return SECOND ;
    }

    /**
     * Gets the milliseconds.
     *
     * @return the milliseconds
     */
    public short getMilliseconds() {
        return MILLISECOND ;
    }

    /**
     * Check arguments validity.
     * @throws IllegalArgumentException Is thrown if received arguments are not in the correct range
     */
    private void checkValidity(byte hour, byte minute, byte second, short millisecond) {

        if ( hour < 0 || hour > 23 ) {

            throw new IllegalArgumentException("Hour is not in the expected range");
        }

        if ( minute < 0 || minute > 59 ) {

            throw new IllegalArgumentException("Minute is not in the expected range");
        }

        if ( second < 0 || second > 59 ) {

            throw new IllegalArgumentException("Second is not in the expected range");
        }

        if ( millisecond < 0 || millisecond > 999 ) {

            throw new IllegalArgumentException("Milliseconds is not in the expected range");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("{Time %s:%s:%s.%s}", HOUR, MINUTE, SECOND , MILLISECOND );
    }
}
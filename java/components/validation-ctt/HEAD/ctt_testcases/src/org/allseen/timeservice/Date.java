/******************************************************************************
  *   *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
  *    Source Project (AJOSP) Contributors and others.
  *
  *    SPDX-License-Identifier: Apache-2.0
  *
  *    All rights reserved. This program and the accompanying materials are
  *    made available under the terms of the Apache License, Version 2.0
  *    which accompanies this distribution, and is available at
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
  *    Alliance. All rights reserved.
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

import org.allseen.timeservice.ajinterfaces.Clock;

// TODO: Auto-generated Javadoc
/**
 * Date is used by the {@link Clock} to set its {@link DateTime}
 */
public class Date {

    /** The year. */
    private final short YEAR;

    /** The month. */
    private final byte MONTH;

    /** The day. */
    private final byte DAY;

    /**
     * Constructor
     * @param year Expected: four digit format
     * @param month Expected range: 1-12
     * @param day Expected range: 1-31
     * @throws IllegalArgumentException Is thrown if received arguments are not in the correct range
     */
    public Date(short year, byte month, byte day) {

        checkValidity(year, month, day);

        YEAR   = year;
        MONTH  = month;
        DAY    = day;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public short getYear() {
        return YEAR;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public byte getMonth() {
        return MONTH;
    }

    /**
     * Gets the day.
     *
     * @return the day
     */
    public byte getDay() {
        return DAY;
    }

    /**
     * Check arguments validity.
     * @throws IllegalArgumentException Is thrown if received arguments are not in the correct range
     */
    private void checkValidity(short year, byte month, byte day) {

        if ( year <= 0 ) {

            throw new IllegalArgumentException("Year is not a positive number");
        }

        if ( month < 1 || month > 12 ) {

            throw new IllegalArgumentException("Month is not in the expected range");
        }

        if ( day < 1 || day > 31 ) {

            throw new IllegalArgumentException("Day is not in the expected range");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("{Date %s-%s-%s}", DAY, MONTH, YEAR);
    }
}
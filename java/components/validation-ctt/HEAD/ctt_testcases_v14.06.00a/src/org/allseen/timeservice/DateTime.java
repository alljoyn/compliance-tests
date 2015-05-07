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
 * Date & Time data type
 */
public class DateTime {

    /** The date. */
    private final Date date;

    /** The time. */
    private final Time time;

    /**
     * UTC offset in minutes
     */
    private final short offsetMinutes;

    /**
     * Constructor
     *
     * @param date {@link Date}
     * @param time {@link Time}
     * @param offsetMinutes UTC offset in minutes
     * @throws IllegalArgumentException Is thrown if received undefined arguments
     */
    public DateTime(Date date, Time time, short offsetMinutes) {

        if ( date == null ) {

            throw new IllegalArgumentException("Undefined date");
        }

        if ( time == null ) {

            throw new IllegalArgumentException("Undefined time");
        }

        this.date = date;
        this.time = time;
        this.offsetMinutes = offsetMinutes;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public Time getTime() {
        return time;
    }

    /**
     * UTC offset in minutes
     *
     * @return UTC offset
     */
    public short getOffsetMinutes() {
        return offsetMinutes;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "{DateTime " + date + " " + time + " offset: '" + offsetMinutes + "'}";
    }
}

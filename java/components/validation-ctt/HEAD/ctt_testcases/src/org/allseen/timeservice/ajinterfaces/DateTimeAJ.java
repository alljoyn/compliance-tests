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

package org.allseen.timeservice.ajinterfaces;

import org.alljoyn.bus.annotation.Position;
import org.allseen.timeservice.Date;
import org.allseen.timeservice.DateTime;
import org.allseen.timeservice.Time;

// TODO: Auto-generated Javadoc
/**
 * Date Time structure for communicating with {@link Clock}
 */
public class DateTimeAJ {

    /**
     * Date structure
     */
    public static class DateAJ {

        /** The year. */
        @Position(0)
        public short year;

        /** The month. */
        @Position(1)
        public byte month;

        /** The day. */
        @Position(2)
        public byte day;

        /**
         * Default Constructor
         */
        public DateAJ(){
        }

        /**
         * Constructor
         * @param date
         */
        public DateAJ(Date date) {

            year   = date.getYear();
            month  = date.getMonth();
            day    = date.getDay();
        }

        /**
         * Converts {@link DateAJ} into {@link Date}
         * @return {@link Date}
         * @throws IllegalArgumentException
         */
        public Date toDate() {

            return new Date(year, month, day);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            return String.format("{DateAJ %s-%s-%s}", day, month, year);
        }
    }

    /**
     * Time structure
     */
    public static class TimeAJ {

        //0-23
        /** The hour. */
        @Position(0)
        public byte hour;

        //0-59
        /** The minute. */
        @Position(1)
        public byte minute;

        //0-59
        /** The second. */
        @Position(2)
        public byte second;

        //0-999
        /** The millisecond. */
        @Position(3)
        public short millisecond;

        /**
         * Default Constructor
         */
        public TimeAJ() {
        }

        /**
         * Constructor
         * @param time
         */
        public TimeAJ(Time time) {

            hour          = time.getHour();
            minute        = time.getMinute();
            second        = time.getSeconds();
            millisecond   = time.getMilliseconds();
        }

        /**
         * Converts {@link TimeAJ} into {@link Time}
         * @return {@link Time}
         * @throws IllegalArgumentException
         */
        public Time toTime() {

            return new Time(hour, minute, second, millisecond);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            return String.format("{TimeAJ %s:%s:%s.%s}", hour, minute, second, millisecond);
        }
    }

    /** The date. */
    @Position(0)
    public DateAJ date;

    /** The time. */
    @Position(1)
    public TimeAJ time;

    /** The offset minutes. */
    @Position(2)
    public short offsetMinutes;

    /**
     * Default Constructor
     */
    public DateTimeAJ() {
    }

    /**
     * Constructor
     * @param dateTime
     */
    public DateTimeAJ(DateTime dateTime) {

        date          = new DateAJ(dateTime.getDate());
        time          = new TimeAJ(dateTime.getTime());
        offsetMinutes = dateTime.getOffsetMinutes();
    }

    /**
     * Converts from AllJoyn structure {@link DateTimeAJ} to {@link DateTime}
     * @return {@link DateTime}
     * @throws IllegalArgumentException Is thrown if received arguments are not in the correct range
     * @see Date
     * @see Time
     */
    public DateTime toDateTime()  {

        return new DateTime(date.toDate(), time.toTime(), offsetMinutes);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "{DateTimeAJ " + date + " " + time + " offset: '" + offsetMinutes + "'}";
    }
}
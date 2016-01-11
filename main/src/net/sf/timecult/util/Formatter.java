/*
 * Copyright (c) Rustam Vishnyakov, 2005-2010 (dyadix@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * $Id: Formatter.java,v 1.8 2010/02/02 20:59:47 dyadix Exp $
 */

package net.sf.timecult.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import net.sf.timecult.model.Duration;


/**
 * Converts date/time and time intervals to formatted strings.
 */
public class Formatter {

    
    public static String toDateTimeString(Date date, boolean isLong) {
        if (isLong) {
            return longDateTimeFormatter.format(date);
        }
        else {
            return dateTimeFormatter.format(date);
        }
    }
    
    public static Date parseDateString(String dateString) throws ParseException {
        return dateFormatter.parse(dateString);
    }
    
    public static Date parseTimeString(String timeString) throws ParseException {
        return timeFormatter.parse(timeString);
    }

    public static Date parseTimeString(String timeString, Date defaultTime) {
        try {
            return parseTimeString(timeString);
        }
        catch (ParseException e) {
            return defaultTime;
        }
    }
    
    
    /**
     * Parses a duration string and returns a corresponding duration object. The
     * duration string must conform to one of the formats hh:mm:ss or hh:mm. Returns
     * 0 duration for null or empty string.
     * 
     * @param durationString
     *            The duration string to parse
     * @return The duration object.
     * @throws ParseException
     */
    public static Duration parseDurationString(String durationString)
        throws ParseException {
        if (durationString == null || durationString.length() == 0) {
            return new Duration(0);
        }
        int hours;
        int min;
        int sec;
        String[] chunks = durationString.split(":");
        if (chunks.length < 2 || chunks.length > 3)
            throw new ParseException("Expecting hh:mm:ss or hh:mm", 0); // TODO: Localize
        String hourString = chunks[0];
        String minString = chunks[1];
        String secString = "0";
        if (chunks.length == 3) {
            secString = chunks[2];
        }
        try {
            hours = Integer.parseInt(hourString);
            min = Integer.parseInt(minString);
            sec = Integer.parseInt(secString);
        }
        catch (NumberFormatException nfe) {
            throw new ParseException("Number expected", 0);
        }
        long durationMs = (((hours * 60) + min) * 60 + sec) * 1000;
        return new Duration(durationMs);
    }


    public static String toDateString(Date date) {
        return dateFormatter.format(date);
    }


    public static String toTimeString(Date date) {
        return timeFormatter.format(date);
    }
    
    /**
     * @param timeMs
     *            The time interval in milliseconds.
     * @param showZeros
     *            If set to true, zero time will be returned as a zeros
     *            separated with :, not as an empty string.
     * @return The string in [n d ]hh:mm:ss format
     */
    public static String toDurationString(long timeMs, boolean showZeros) {
        final int SEC_PER_MIN = 60;
        final int SEC_PER_HOUR = SEC_PER_MIN * 60;
        //final int SEC_PER_WDAY = SEC_PER_HOUR * 8;

        StringBuilder buf = new StringBuilder();
        if (timeMs > 0 || showZeros) {
            int seconds = (int) (timeMs / 1000);
            int hours = seconds / SEC_PER_HOUR;
            buf.append(hours).append(":");

            seconds -= hours * SEC_PER_HOUR;
            int minutes = seconds / SEC_PER_MIN;
            buf.append(format(minutes));
            buf.append(":");

            seconds -= minutes * SEC_PER_MIN;
            buf.append(format(seconds));
        }
        return buf.toString();
    }

    private static String format(int value) {
        StringBuilder buf = new StringBuilder();
        if (value == 0) {
            buf.append("00");
        }
        else {
            if (value < 10) {
                buf.append("0");

            }
            buf.append(Integer.toString(value));
        }
        return buf.toString();
    }
    
    private static DateFormat dateTimeFormatter = DateFormat
                                                    .getDateTimeInstance(
                                                        DateFormat.MEDIUM,
                                                        DateFormat.SHORT,
                                                        Locale.getDefault());

    private static DateFormat dateFormatter     = DateFormat.getDateInstance(
                                                    DateFormat.MEDIUM,
                                                    Locale.getDefault());

    private static DateFormat timeFormatter     = DateFormat.getTimeInstance(
                                                    DateFormat.SHORT,
                                                    Locale.getDefault());
    
    private static DateFormat longDateTimeFormatter = DateFormat
                                                        .getDateTimeInstance(
                                                            DateFormat.LONG,
                                                            DateFormat.LONG,
                                                            Locale.getDefault());


}

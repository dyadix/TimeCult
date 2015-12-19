/*
 * Copyright (c) Rustam Vishnyakov, 2005-2006 (rvishnyakov@yahoo.com)
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
 * $Id: QuoteTimeFormatter.java,v 1.2 2007/06/08 19:10:25 dyadix Exp $
 */

package net.sf.timecult.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts date/time and time intervals to formatted strings.
 * 
 * @author R.Vishnyakov (dyadix@mail.ru)
 */
public class QuoteTimeFormatter {

    // Public methods
    //**************************************************************************

    /**
     * @return Time string in hh:mm:ss 24-hour format
     * @param date  The date object to be formatted.
     */
    public static String toTimeString(Date date) {
        return _timeFormatter.format(date);
    }

    /**
     * @param date The date object to be formatted.
     * @return Date string in yyyy-MM-dd format.
     */
    public static String toDateString(Date date) {
        return _dateFormatter.format(date);
    }

    /**
     * @param timeMs
     *            The time interval in milliseconds.
     * @return The string in [n d ]hh:mm:ss format
     */
    public static String toDurationString(long timeMs) {
        final int SEC_PER_MIN = 60;
        final int SEC_PER_HOUR = SEC_PER_MIN * 60;
        //final int SEC_PER_WDAY = SEC_PER_HOUR * 8;

        StringBuilder buf = new StringBuilder();
        if (timeMs > 0) {
            int seconds = (int) (timeMs / 1000);
            /*
             int days = seconds / SEC_PER_WDAY;
             if(days > 0)
             {
             buf.append(Integer.toString(days));
             buf.append(" wd ");
             }
             
             seconds -= days * SEC_PER_WDAY;
             */
            int hours = seconds / SEC_PER_HOUR;
            if (hours != 0) {
                buf.append(hours).append(":");
            }

            seconds -= hours * SEC_PER_HOUR;
            int minutes = seconds / SEC_PER_MIN;
            if (hours == 0) {
                buf.append(minutes);
            }
            else {
                buf.append(format(minutes));
            }
            buf.append("'");

            seconds -= minutes * SEC_PER_MIN;
            buf.append(format(seconds)).append("''");
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

    // Private attributes
    //**************************************************************************
    private static SimpleDateFormat _dateFormatter = new SimpleDateFormat();
    private static SimpleDateFormat _timeFormatter = new SimpleDateFormat();

    static {
        _dateFormatter.applyPattern("yyyy-MM-dd");
        _timeFormatter.applyPattern("HH:mm:ss");
    }
}

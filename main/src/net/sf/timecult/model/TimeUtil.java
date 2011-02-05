/*
 * Copyright (c) Rustam Vishnyakov, 2009 (dyadix@gmail.com)
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
 * $Id: TimeUtil.java,v 1.3 2009/05/13 12:58:55 dyadix Exp $ 
 */
package net.sf.timecult.model;

import java.util.Date;

/**
 * A collection of algorithms for miscellaneous time/date calculations.
 */
public class TimeUtil {
    
    /**
     * Forms a time record as an intersection of the original time record and a given
     * filter (interval). If no filter is given or the filter fully contains the time
     * record, the original record is returned.
     * 
     * @param filter    The filter to use (can be null).
     * @param timeRec   The original time record.
     * @return Time record with applied filter OR the original record if it
     * fits inside filter interval.
     */
    public static TimeRecord getFilteredTimeRec(TimeRecordFilter filter,
        TimeRecord timeRec) {
        if (filter == null) {
            return timeRec;
        }
        long recStartTime = timeRec.getStart().getTime();
        long recEndTime = timeRec.getEnd().getTime();
        long resultStartTime = recStartTime;
        long resultEndTime = recEndTime;
        boolean isPartial = false;
        if (filter.getSinceDate() != null && filter.getToDate() != null) {
            long filterEndTime = filter.getToDate().getTime();
            long filterStartTime = filter.getSinceDate().getTime();
            if (recEndTime > filterEndTime) {
                resultEndTime = filterEndTime;
                isPartial = true;
            }
            if (recStartTime < filterStartTime) {
                resultStartTime = filterStartTime;
                isPartial = true;
            }
        }
        if (isPartial) {
            long resultDuration = resultEndTime - resultStartTime;
            TimeRecord filtered = new TimeRecord(timeRec.getTask(), new Date(
                resultStartTime), resultDuration, timeRec.getNotes());
            return filtered;
        }
        else {
            return timeRec;
        }
    }
}

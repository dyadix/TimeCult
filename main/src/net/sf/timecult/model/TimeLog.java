/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (dyadix@gmail.com)
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
 * $Id: TimeLog.java,v 1.4 2011/01/18 02:57:53 dragulceo Exp $ 
 */
package net.sf.timecult.model;

/**
 * Defines methods to store and access time records. Particular implementations may use
 * different types of storages.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public interface TimeLog {

    /**
     * Adds a time record to the log.
     * @param timeRec The time record to add.
     */
    public void addTimeRecord(TimeRecord timeRec);

    /**
     * @param filter The filter to select time records. If the filter is null, all time 
     *               records will be returned.
     * @return Array of time records matching the given criteria specified by the filter.
     */
    public TimeRecord[] getTimeRecords(TimeRecordFilter filter);

    public void joinRecords(TimeRecord timeRecs[]);
    /**
     * Removes all the time records specified by the given filter. If the filter
     * is null, all the records are removed (the time log is cleaned up).
     * @param filter	The filter to select time records.
     */
    public void removeRecords(TimeRecordFilter filter);
    
    /**
     * Removes a given set of time records.
     * @param timeRecs The array of time records to be removed.
     */
    public void removeRecords(TimeRecord timeRecs[]);

    /**
     * Removes all the time log records.
     */
    public void clear();
        
}

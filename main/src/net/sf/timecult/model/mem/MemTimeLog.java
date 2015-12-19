/*
 * Copyright (c) Rustam Vishnyakov, 2005-2008 (dyadix@gmail.com)
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
 * $Id: MemTimeLog.java,v 1.11 2011/01/18 02:57:53 dragulceo Exp $ 
 */
package net.sf.timecult.model.mem;
import java.util.Calendar;
import java.util.TreeSet;
import java.util.Vector;

import net.sf.timecult.model.*;

/**
 * Keeps time records in memory.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class MemTimeLog implements TimeLog
{
    
    private TreeSet<TimeRecord> _timeLog = new TreeSet<TimeRecord>();    

    /**
     * Adds a time record to the log.
     * @param timeRec The time record to add.
     */
    public void addTimeRecord(TimeRecord timeRec)
    {
        _timeLog.add(timeRec);        
    }

    /**
     * @return Time records for the given filter.
     * @see dyadix.timetracker.model.TimeLog#getTimeRecords(dyadix.timetracker.model.TimeRecordFilter)
     */
    public TimeRecord[] getTimeRecords(TimeRecordFilter filter) {
        TreeSet<TimeRecord> result = new TreeSet<TimeRecord>();

        for (TimeRecord stored : _timeLog) {
            if (matches(stored, filter)) {
                if (filter == null || !filter.isGroupByDate()) {
                    result.add(stored);
                }
                else {
                    addGroupByDate(result, stored);
                }
            }
        }
        return result.toArray(new TimeRecord[result.size()]);
    }
    
    
    /*
     * Get record's date and attempt to find another one with the same date. Merge
     * the records if found or add a new record.
     * 
     * @param target    The target collection.
     * @param timeRec   The time record to add.
     */
    private void addGroupByDate(TreeSet<TimeRecord> target,
        TimeRecord timeRec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeRec.getStart().getTime());
        //
        // Reset time of day to 0
        //
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        //
        // Find another record with the same date
        //
        TimeRecord groupRec = new TimeRecord(timeRec.getTask(), c.getTime(), 0,
            "");
        if (target.contains(groupRec)) {            
            groupRec = target.tailSet(groupRec).first();
            groupRec.setDuration(groupRec.getDuration().getValue()
                + timeRec.getDuration().getValue());
        }
        else {
            groupRec.setDuration(timeRec.getDuration().getValue());
            target.add(groupRec);
        }
    }


    /**
     * Removes records specified by the filter.
     * 
     * @see dyadix.timetracker.model.TimeLog#removeRecords(dyadix.timetracker.model.TimeRecordFilter)
     */
    public synchronized void removeRecords(TimeRecordFilter filter)
    {
        Vector<TimeRecord> toRemove = new Vector<TimeRecord>();
        if(filter != null)
        {
            for(TimeRecord stored: _timeLog)
            {                
                if(matches(stored, filter))
                {
                    toRemove.add(stored);
                }
            }
            for(TimeRecord recToRemove: toRemove)
            {
                _timeLog.remove(recToRemove);
            }
        }
        else
        {
            _timeLog.clear();
        }        
    }
    
    
    /**
     * Removes all the time log records from memory.
     */
    public void clear()
    {
        _timeLog.clear();
    }
    
    private boolean matches(TimeRecord timeRec, TimeRecordFilter filter)
    {
        if (filter == null)
        {
            return true;
        }
        boolean match = true;
        //noinspection ConstantConditions
        if (match && filter.getTask() != null)
        {
            if (!filter.getTask().equals(timeRec.getTask()))
            {
                match = false;
            }
        }
        if (match && filter.getProject() != null) {
            if (!timeRec.getTask().belongsTo(filter.getProject())) {
                match = false;
            }
        }
        if (match && filter.getSinceDate() != null && filter.getToDate() != null)
        {                  
            if (filter.getSinceDate().after(timeRec.getEnd())
                || filter.getToDate().getTime() <= timeRec.getStart().getTime()) {
                match = false;
            }
        }
        return match;
    }
    
    /**
     * Joins the time records by given references.
     * 
     * @param timeRecs
     *            The array of time records (references).
     */
    public void joinRecords(TimeRecord[] timeRecs) {
        if (timeRecs != null) {
            //_timeLog.join(timeRecs[i]);
            long total = timeRecs[0].getDuration().getValue();
            StringBuilder sb = new StringBuilder(timeRecs[0].getNotes());
            for (int i = 1; i < timeRecs.length; i++) {
                if(timeRecs[i].getNotes().length() > 0) {
                    sb.append("|");
                    sb.append(timeRecs[i].getNotes());
                }
                total += timeRecs[i].getDuration().getValue();
                _timeLog.remove(timeRecs[i]);
            }
            timeRecs[0].setDuration(total);
            timeRecs[0].setNotes(sb.toString());
        }
    }
    

    /**
     * Removes the time records by given references.
     * 
     * @param timeRecs
     *            The array of time records (references).
     */
    public void removeRecords(TimeRecord[] timeRecs) {
        if (timeRecs != null) {
            for (TimeRecord timeRec : timeRecs) {
                _timeLog.remove(timeRec);
            }
        }
    }


}

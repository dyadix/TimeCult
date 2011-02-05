/*
 * Copyright (c) Rustam Vishnyakov, 2007 (dyadix@gmail.com)
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
 * $Id: TimeSheet.java,v 1.4 2011/01/03 15:36:36 dyadix Exp $
 */
package net.sf.timecult.ui.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import net.sf.timecult.model.Duration;
import net.sf.timecult.model.TimeLog;
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.Totals;

/**
 * Contains timesheet data.
 */
public class TimeSheet {
    
    private Calendar startDate;
    private Calendar endDate;
    private Vector<Totals> items = new Vector<Totals>();
    private TimeLog timeLog;
    
    /**
     * Creates a new empty timesheet for the given number of days
     * ending with end date.
     * @param endDate   The end date.
     * @param ndays     The number of days to be included.
     */
    public TimeSheet(TimeLog timeLog, Date endDate, int ndays) {        
        this.endDate = Calendar.getInstance();
        this.endDate.setTime(endDate);
        this.endDate.set(Calendar.HOUR_OF_DAY, 0);
        this.endDate.set(Calendar.MINUTE, 0);
        this.endDate.set(Calendar.SECOND, 0);
        this.endDate.set(Calendar.MILLISECOND, 0);
        this.endDate.add(Calendar.DATE, 1);
        this.startDate = (Calendar) this.endDate.clone();
        this.startDate.add(Calendar.DATE, -ndays);
        this.timeLog = timeLog;
    }
    
    /**
     * Adds an item (project or task) to the timesheet.
     * @param item  The item to add.
     */
    public void addItem(Totals item) {
        this.items.add(item);
    }
    
    /**
     * Get all the items (projects and tasks) from the timesheet.
     * @return Project and tasks added to the timesheet.
     */
    public Totals[] getItems() {
        return items.toArray(new Totals[0]);
    }
    
    /**
     * @return An array of dates from start date to end date.
     */
    public Date[] getDates() {
        int ndays = this.endDate.get(Calendar.DAY_OF_YEAR) - this.startDate.get(Calendar.DAY_OF_YEAR);
        if(this.startDate.get(Calendar.YEAR) != this.endDate.get(Calendar.YEAR)) {
            ndays += this.startDate.getMaximum(Calendar.DAY_OF_YEAR) - 1;
        }
        Date[] dates = new Date[ndays];
        Calendar currDate = (Calendar) this.startDate.clone();
        for(int i = 0; i < ndays; i ++) {
            dates[i] = (Date) currDate.getTime().clone();
            currDate.add(Calendar.DATE, 1);
        }
        return dates;
    }
    
    public Duration getTimeUsed(int itemIndex, Date date) {
        TimeRecordFilter filter = new TimeRecordFilter();
        filter.setSinceDate(date);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(date);
        endDate.add(Calendar.DATE, 1);
        filter.setToDate(endDate.getTime());
        Duration timeUsed = items.get(itemIndex).getTotalDuration(
            timeLog,
            filter);
        return timeUsed;
    }
    
    
    public Duration getTimeUsed(int itemIndex) {
        return getTimeUsed(items.get(itemIndex));
    }
    
    public Duration getTimeUsed(Totals item) {
        TimeRecordFilter filter = new TimeRecordFilter();
        filter.setSinceDate(startDate.getTime());
        filter.setToDate(endDate.getTime());
        Duration timeUsed = item.getTotalDuration(
            timeLog,
            filter);
        return timeUsed;
    }
    

}

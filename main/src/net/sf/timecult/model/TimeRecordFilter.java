/*
 * File: TimeRecordFilter.java
 * Created: 24.05.2005
 *
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
 */
package net.sf.timecult.model;

import java.util.Date;

/**
 * Defines a criteria to select time records. Certain fields can be null which means that
 * they should not be used as a selection criteria.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class TimeRecordFilter {
    
    public TimeRecordFilter(String label) {
        this.label = label;
    }
    
    public TimeRecordFilter() {        
    }

    /**
     * @return Returns the sinceDate.
     */
    public Date getSinceDate() {
        return _sinceDate;
    }

    /**
     * @param sinceDate The sinceDate to set.
     */
    public void setSinceDate(Date sinceDate) {
        _sinceDate = sinceDate;
    }

    /**
     * @return Returns the task.
     */
    public Task getTask() {
        return _task;
    }

    /**
     * @param task The task to set.
     */
    public void setTask(Task task) {
        _task = task;
    }

    /**
     * @return Returns the toDate.
     */
    public Date getToDate() {
        return _toDate;
    }

    /**
     * @param toDate The toDate to set.
     */
    public void setToDate(Date toDate) {
        _toDate = toDate;
    }

    public Object clone() {
        TimeRecordFilter theClone = new TimeRecordFilter(this.label);
        theClone.setSinceDate(_sinceDate);
        theClone.setToDate(_toDate);
        theClone.setTask(_task);
        theClone.setGroupByDate(this.groupByDate);
        return theClone;
    }    
    
    
    public String getLabel() {
        return this.label;
    }
    
    
    public void setGroupByDate(boolean groupByDate) {
        this.groupByDate = groupByDate;
    }
    
    public boolean isGroupByDate() {
        return this.groupByDate;
    }
    
    public boolean isCustom() {
        return this.isCustom;
    }
    
    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }
    
    public Project getProject() {
        return _project;
    }

    public void setProject(Project project) {
        _project = project;
    }

    public boolean isWithinDateRange(Date dateTime) {
        return dateTime != null &&
                (_sinceDate == null || _sinceDate.before(dateTime)) &&
                (_toDate == null || _toDate.after(dateTime));
    }

    private Date _sinceDate;
    private Date _toDate;
    private Task _task;
    private Project _project;


    private String label;
    private boolean groupByDate = false;
    private boolean isCustom = false;
}

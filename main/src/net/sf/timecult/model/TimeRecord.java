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
 * $Id: TimeRecord.java,v 1.6 2008/03/12 20:14:03 dyadix Exp $ 
 */
package net.sf.timecult.model;

import java.util.Date;

/**
 * Contains informaion about a task-related activity (start/end time).
 * 
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class TimeRecord implements Comparable {

    private Task   task;
    private Date   start;
    private long   duration = 0;
    private String notes;


    public TimeRecord(Task task, Date start, long duration, String notes) {
        this.task = task;
        this.start = start;
        this.duration = duration;
        this.notes = notes;
    }


    /**
     * @return Returns the start.
     */
    public Date getStart() {
        return this.start;
    }


    /**
     * @param start
     *            The start to set.
     */
    public void setStart(Date start) {
        this.start = start;
    }


    public Date getEnd() {
        return new Date(this.start.getTime() + this.duration);
    }


    /**
     * @return Returns the task.
     */
    public Task getTask() {
        return this.task;
    }


    /**
     * @param task
     *            The task to set.
     */
    public void setTask(Task task) {
        this.task = task;
    }


    public Duration getDuration() {
        return new Duration(this.duration);
    }


    public String getNotes() {
        return this.notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }


    public int compareTo(Object o) {
        if (!(o instanceof TimeRecord)) {
            throw new Error("TimeRecord can not be compared to "
                + o.getClass().getName());
        }
        
        int result;

        TimeRecord timeRec = (TimeRecord) o;
        if (timeRec.getStart().getTime() == this.start.getTime()) {
            result = 0;
        }
        else if (timeRec.getStart().getTime() > this.start.getTime()) {
            result = -1;
        }
        else {
            result = 1;
        }

        if (result == 0) {
            result = this.getTask().getId().compareTo(timeRec.getTask().getId());
        }
        return result;
    }

}

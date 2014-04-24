/*
 * File: Task.java
 * Created: 20.05.2005
 *
 * Copyright (c) Rustam Vishnyakov, 2005-2011 (rvishnyakov@yahoo.com)
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

/**
 * Task is a unit for which a spent time can be measured.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class Task extends ProjectTreeItem implements TotalsCalculator, DescriptionHolder {

    public Task(Project parent, String id, String name) {
        super(id, name, parent);
    }


    /**
     * Compares two tasks. The tasks are equal if their IDs match.
     * @param o The task object to compare with.
     */
    public boolean equals(Object o) {
        boolean equal = false;
        if (o != null && o instanceof Task) {
            Task t = (Task) o;
            if (t.getId() == getId())
                equal = true;
        }
        return equal;
    }

    /**
     * @return Totals for the task.
     */
    @Override
    public Totals getTotals(TimeLog timeLog, TimeRecordFilter filter) {
        Totals totals = new Totals();
        TimeRecordFilter localTaskFilter = new TimeRecordFilter();
        if (filter != null) {
            localTaskFilter = (TimeRecordFilter) filter.clone();
        }
        localTaskFilter.setTask(this);
        TimeRecord timeRecords[] = timeLog.getTimeRecords(localTaskFilter);
        for (TimeRecord timeRecord : timeRecords) {
            totals.addDuration(TimeUtil.getFilteredTimeRec(filter, timeRecord).getDuration());
        }
        if (isClosed()) {
            if (getCloseDateTime() != null
                    && filter != null
                    && filter.getSinceDate() != null
                    && filter.getToDate() != null
                    && filter.getSinceDate().before(getCloseDateTime())
                    && filter.getToDate().after(getCloseDateTime())) {
                totals.incClosedItems(1);
            }
        }
        else {
            totals.incOpenItems(1);
        }
        return totals;
    }

    public Project getProject() {
        return getParent();
    }

    public void setStatus(int statusId) {
        _status.setId(statusId);
        updateCloseDateTime(statusId);
    }

    public void setFlagColor(TaskStatus.FlagColor flagColor) {
        _status.setFlagColor(flagColor);
    }

    public TaskStatus.FlagColor getFlagColor() {
        return _status.getFlagColor();
    }

    public void setStatusFromString(String str) {
        _status.setFromString(str);
        updateCloseDateTime(_status.getId());
    }

    public int getStatusId() {
        return _status.getId();
    }

    public TaskStatus getStatus() {
        return _status;
    }

    public String getStatusAsString() {
        return _status.toString();
    }

    /**
     * @return True if a standalone timer is enabled for the task. 
     * False otherwise. True is returned only if the task is not closed.
     */
    public boolean isStandaloneTimerEnabled() {
        return !isClosed();
    }

    public boolean isDeletable() {
        return true;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }
    
    /**
     * @return True if the task is not measurable anymore (cancelled or
     *         finished).
     * 
     */
    public boolean isClosed() {
        switch (_status.getId()) {
        case TaskStatus.FINISHED:
        case TaskStatus.CANCELLED:
            return true;
        default:
            return false;
        }
    }

    public boolean isWaiting() {
        return _status.getId() == TaskStatus.WAITING;
    }
    
    public boolean isFlagged() {
        return _status.getId() == TaskStatus.FLAGGED;
    }
    
    
    public void setWaitReason(WaitReason waitReason) {
        this._waitReason = waitReason;
    }
    
    public WaitReason getWaitReason() {
        return this._waitReason;
    }
    
    private void updateCloseDateTime(int statusId) {
        if (!isClosed()) {
            setCloseDateTime(null);
        }
    }
    
    @Override
    public ItemType getItemType() {
        return ItemType.TASK;
    }
    
    
    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public boolean mayHaveDeadline() {
        return true;
    }


    private TaskStatus _status = new TaskStatus();
    private String _description = "";
    private WaitReason _waitReason;    

}

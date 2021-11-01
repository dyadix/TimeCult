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

    private final TaskStatus status      = new TaskStatus();
    private       String     description = "";
    private       WaitReason waitReason;

    public Task(Project parent, String id, String name) {
        super(id, name, parent);
    }


    /**
     * Compares two tasks. The tasks are equal if their IDs match.
     * @param o The task object to compare with.
     */
    public boolean equals(Object o) {
        boolean equal = false;
        if (o instanceof Task) {
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
        TimeRecord[] timeRecords = timeLog.getTimeRecords(localTaskFilter);
        for (TimeRecord timeRecord : timeRecords) {
            totals.addDuration(TimeUtil.getFilteredTimeRec(filter, timeRecord).getDuration());
        }
        if (isClosed()) {
            if (filter == null || filter.isWithinDateRange(getCloseDateTime())) {
                totals.incClosedItems(1);
            }
        }
        else {
            totals.incOpenItems(1);
        }
        if (filter == null || filter.isWithinDateRange(getCreationDateTime())) {
            totals.incNewItems(1);
        }
        return totals;
    }

    public Project getProject() {
        return getParent();
    }

    public void setStatus(int statusId) {
        status.setId(statusId);
        updateCloseDateTime(statusId);
    }

    public void setFlagColor(TaskStatus.FlagColor flagColor) {
        status.setFlagColor(flagColor);
    }

    public TaskStatus.FlagColor getFlagColor() {
        return status.getFlagColor();
    }

    public void setStatusFromString(String str) {
        status.setFromString(str);
        updateCloseDateTime(status.getId());
    }

    public int getStatusId() {
        return status.getId();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getStatusAsString() {
        return status.toString();
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return True if the task is not measurable anymore (cancelled or
     *         finished).
     * 
     */
    public boolean isClosed() {
        switch (status.getId()) {
        case TaskStatus.FINISHED:
        case TaskStatus.CANCELLED:
            return true;
        default:
            return false;
        }
    }

    public boolean isWaiting() {
        return status.getId() == TaskStatus.WAITING;
    }
    
    public boolean isFlagged() {
        return status.getId() == TaskStatus.FLAGGED;
    }
    
    
    public void setWaitReason(WaitReason waitReason) {
        this.waitReason = waitReason;
    }
    
    public WaitReason getWaitReason() {
        return this.waitReason;
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

}

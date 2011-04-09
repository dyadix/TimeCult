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
 */
package net.sf.timecult.model;

public class TaskStatus {

    public final static int NOT_STARTED = 0;
    public final static int IN_PROGRESS = 1;
    public final static int FINISHED = 2;
    public final static int ON_HOLD = 3;
    public final static int CANCELLED = 4;
    public final static int FLAGGED = 5;
    public final static int WAITING = 6;

    private final static String NOT_STARTED_STR = "notStarted";
    private final static String IN_PROGRESS_STR = "inProgress";
    private final static String FINISHED_STR = "finished";
    private final static String ON_HOLD_STR = "onHold";
    private final static String CANCELLED_STR = "cancelled";
    private final static String FLAGGED_STR = "flagged";
    private final static String WAITING_STR = "waiting";

    public enum FlagColor {
        RED,
        GREEN,
        BLUE
    }

    private int _id = NOT_STARTED;
    private FlagColor flagColor = FlagColor.RED;

    public TaskStatus(FlagColor flagColor) {
        this.flagColor = flagColor;
        this._id = FLAGGED;
    }
    
    public TaskStatus(int id) {
        this._id = id;
    }
    
    
    public TaskStatus() {
        this._id = NOT_STARTED;
    }

    public void setFlagColor(FlagColor flagColor) {
        this.flagColor = flagColor;
    }

    public FlagColor getFlagColor() {
        return this.flagColor;
    }
    
    public String toString() {
        switch (_id) {
        case NOT_STARTED:
            return NOT_STARTED_STR;
        case IN_PROGRESS:
            return IN_PROGRESS_STR;
        case FINISHED:
            return FINISHED_STR;
        case ON_HOLD:
            return ON_HOLD_STR;
        case CANCELLED:
            return CANCELLED_STR;
        case FLAGGED:
            return FLAGGED_STR;
        case WAITING:
            return WAITING_STR;
        }
        return null;
    }

    public static int parseId(String str) {
        int id = NOT_STARTED;
        if (NOT_STARTED_STR.equals(str)) {
            id = NOT_STARTED;
        }
        else if (IN_PROGRESS_STR.equals(str)) {
            id = IN_PROGRESS;
        }
        else if (FINISHED_STR.equals(str)) {
            id = FINISHED;
        }
        else if (ON_HOLD_STR.equals(str)) {
            id = ON_HOLD;
        }
        else if (CANCELLED_STR.equals(str)) {
            id = CANCELLED;
        }
        else if (FLAGGED_STR.equals(str)) {
            id = FLAGGED;
        }
        else if (WAITING_STR.equals(str)) {
            id = WAITING;
        }
        return id;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setFromString(String str) {
        _id = parseId(str);
    }

    public int getId() {
        return _id;
    }

    public boolean isFinished() {
        return _id == FINISHED;
    }

    public static FlagColor parseColorString(String str) {
        return FlagColor.valueOf(str);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TaskStatus)) return false;
        TaskStatus otherStatus = (TaskStatus)obj;
        if (_id == FLAGGED) {
            return (otherStatus.getId() == FLAGGED) && (otherStatus.getFlagColor() == flagColor);
        }
        return otherStatus.getId() == _id;
    }


}

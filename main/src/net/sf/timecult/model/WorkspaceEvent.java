/*
 * Created on Jul 21, 2005
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

/**
 * Contains information about workspace changes.
 * @author rvishnyakov
 */
public class WorkspaceEvent {

    public final static int WORKSPACE_SAVED            = 0;
    public final static int WORKSPACE_TASK_ADDED       = 1;
    public final static int WORKSPACE_PROJECT_ADDED    = 2;
    public final static int WORKSPACE_TASK_REMOVED     = 3;
    public final static int WORKSPACE_PROJECT_REMOVED  = 4;
    public final static int WORKSPACE_TIME_REC_ADDED   = 5;
    public final static int WORKSPACE_TIME_LOG_CLEARED = 6;
    public final static int WORKSPACE_TASK_CHANGED     = 7;
    public final static int WORKSPACE_PROJECT_CHANGED  = 8;
    public final static int TASK_STATUS_CHANGED        = 9;
    public final static int NOTES_UPDATED              = 10;
    public final static int TIME_REC_CHANGED           = 11;
    public final static int TIME_RECS_REMOVED          = 12;
    public final static int TIME_RECS_JOINED           = 13;
    public final static int FILTER_CHANGED             = 14;
    public final static int SETTINGS_UPDATED           = 15;

    public WorkspaceEvent(int eventId) {
        _id = eventId;
    }

    public WorkspaceEvent(int eventId, Object source) {
        _id = eventId;
        _source = source;
    }

    public Object getSource() {
        return _source;
    }

    public int getId() {
        return _id;
    }

    private int _id = WORKSPACE_SAVED;
    private Object _source = null;
}

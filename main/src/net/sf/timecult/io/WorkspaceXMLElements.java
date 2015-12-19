/*
 * Copyright (c) Rustam Vishnyakov, 2005-2010 (dyadix@gmail.com)
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
 * $Id: WorkspaceXMLElements.java,v 1.12 2010/04/02 14:31:34 dyadix Exp $
 */
package net.sf.timecult.io;

/**
 * A set of constants defining workspace XML elements.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public interface WorkspaceXMLElements {
    
    /*
     * The current file version. File versions are not backwards compatible.
     */
    int FILE_VERSION = 12;

    /*
     * Tag and attribute names
     */
    String ROOT_TAG = "timecult";
    String OLD_ROOT_TAG = "timetracker";
    String PROJECT_TAG = "project";
    String TASK_TAG = "task";
    String ID_ATTR = "id";
    String NAME_ATTR = "name";
    String LAST_ID_ATTR = "lastId";
    String TIME_REC_TAG = "timeRec";
    String TASK_ID_ATTR = "taskId";
    String START_TIME_ATTR = "startTime";
    String DURATION_ATTR = "duration";
    String TIME_LOG_TAG = "timeLog";
    String APP_VERSION_ATTR = "appVersion";
    String NOTES = "notes";
    String STATUS_ATTR = "status";
    String SETTINGS_TAG = "settings";
    String PROPERTY_TAG = "property";
    String VALUE_ATTR = "value";
    String NOTES_TAG = "notes";
    String FILE_VERSION_ATTR = "fileVersion";
    String FLAGS_ATTR = "flags";
    String CREATION_DATETIME_ATTR = "created";
    String CLOSE_DATETIME_ATTR = "closed";
    String WAIT_REASON_TAG="waitReason";
    String UUID_ATTR = "uuid";
    String PROJECT_TREE_TAG = "projectTree";
    String ACTIVITY_TAG = "activity";
    String FILTERS_TAG = "filters";
    String FILTER_TAG = "filter";
    String FILTER_NAME_ATTR = "name";
    String FILTER_START_TIME = "startTime";
    String FILTER_END_TIME = "endTime";
    String HYPERLINK_ATTR = "url";
    String DEFAULT_NOTE_ATTR = "defaultNote";
    String FLAG_COLOR_ATTR = "flagColor";
    String DEADLINE = "deadline";

    /*
     * Settings (properties)
     */
    String IDLE_TASK_ENABLED = "idleTaskEnabled";
    String LAST_ID_PROPERTY = "lastId";
    String TIME_FILTER_PROPERTY = "timeFilter";
    String ROUND_UP_INTERVAL = "roundUpInterval";
    
    /*
     * Flags
     */
    String EXPANDED_FLAG = "expanded";
    String SELECTED_FLAG = "selected";
}

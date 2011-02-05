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
    public final static int FILE_VERSION = 10;

    /*
     * Tag and attribute names
     */
    public final static String ROOT_TAG = "timecult";
    public final static String OLD_ROOT_TAG = "timetracker";
    public final static String PROJECT_TAG = "project";
    public final static String TASK_TAG = "task";
    public final static String ID_ATTR = "id";
    public final static String NAME_ATTR = "name";
    public final static String LAST_ID_ATTR = "lastId";
    public final static String TIME_REC_TAG = "timeRec";
    public final static String TASK_ID_ATTR = "taskId";
    public final static String START_TIME_ATTR = "startTime";
    public final static String DURATION_ATTR = "duration";
    public final static String TIME_LOG_TAG = "timeLog";
    public final static String APP_VERSION_ATTR = "appVersion";
    public final static String NOTES = "notes";
    public final static String STATUS_ATTR = "status";
    public final static String SETTINGS_TAG = "settings";
    public final static String PROPERTY_TAG = "property";
    public final static String VALUE_ATTR = "value";
    public final static String NOTES_TAG = "notes";
    public final static String FILE_VERSION_ATTR = "fileVersion";
    public final static String FLAGS_ATTR = "flags";
    public final static String CREATION_DATETIME_ATTR = "created";
    public final static String CLOSE_DATETIME_ATTR = "closed";
    public final static String WAIT_REASON_TAG="waitReason";
    public final static String UUID_ATTR = "uuid";
    public final static String PROJECT_TREE_TAG = "projectTree";
    public final static String ACTIVITY_TAG = "activity";
    public final static String FILTERS_TAG = "filters";
    public final static String FILTER_TAG = "filter";
    public final static String FILTER_NAME_ATTR = "name";
    public final static String FILTER_START_TIME = "startTime";
    public final static String FILTER_END_TIME = "endTime";
    public final static String HYPERLINK_ATTR = "url";
    public final static String DEFAULT_NOTE_ATTR = "defaultNote";

    /*
     * Settings (properties)
     */
    public final static String IDLE_TASK_ENABLED = "idleTaskEnabled";
    public final static String LAST_ID_PROPERTY = "lastId";
    public final static String TIME_FILTER_PROPERTY = "timeFilter";
    public final static String ROUND_UP_INTERVAL = "roundUpInterval";
    
    /*
     * Flags
     */
    public final static String EXPANDED_FLAG = "expanded";
    public final static String SELECTED_FLAG = "selected";
}

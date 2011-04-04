/*
 * Copyright (c) Rustam Vishnyakov, 2008 (dyadix@gmail.com)
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
package net.sf.timecult.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.znerd.xmlenc.XMLOutputter;

import net.sf.timecult.AppInfo;
import net.sf.timecult.model.IdleTask;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.model.TimeRecord;
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.WorkspaceSettings;
import net.sf.timecult.model.Project.SortCriteria;
import net.sf.timecult.model.ProjectTreeItem.ItemType;

/**
 * Replaces WorkspaceWriter by directly writing XML content instead of 
 * creating a DOM tree first. Uses xmlenc library.
 */
public class WorkspaceXMLWriter implements WorkspaceXMLElements {

    private Workspace workspace;
    private XMLOutputter output;
    
    public WorkspaceXMLWriter(Workspace workspace) {
        this.workspace = workspace;
    }
    
    
    public void saveWorkspace(File workspaceFile) throws Exception {
        FileOutputStream os = new FileOutputStream(workspaceFile);        
        OutputStreamWriter outWriter = new OutputStreamWriter(os, "UTF-8");
        BufferedWriter out = new BufferedWriter(outWriter);
        this.output = new XMLOutputter(out, "UTF-8");
        this.output.declaration();
        writeWorkspace();
        os.close();
    }
    
    
    private void writeWorkspace() throws Exception {
        this.output.startTag(ROOT_TAG);
        this.output.attribute(APP_VERSION_ATTR, AppInfo.getMajorVersion() + "."
            + AppInfo.getMinorVersion());
        this.output.attribute(FILE_VERSION_ATTR, Integer.toString(FILE_VERSION));
        //
        // This is a hack to make newer file version explicitely incompatible
        // with older application versions. Otherwise read errors do not get reported
        // (due to a previously existing bug in all version before 0.7.0).
        //
        this.output.attribute(LAST_ID_ATTR, "Incompatible version");
        
        this.output.attribute(NAME_ATTR, this.workspace.getName());
        this.output.attribute(UUID_ATTR, this.workspace.getUUIDString());
        addSettings();
        addFilters();
        addProjectTree();
        addTimeLog();
        this.output.endDocument();
    }
    
    private void addSettings() throws Exception {
        this.output.startTag(SETTINGS_TAG);
        addProperty(IDLE_TASK_ENABLED, Boolean.toString(this.workspace
            .isIdleEnabled()));
        addProperty(LAST_ID_PROPERTY, this.workspace.getIdGenerator()
            .getLastId());
        long creationTimeMs = this.workspace.getCreationDateTime().getTime();
        if (creationTimeMs != 0) {
            addProperty(CREATION_DATETIME_ATTR, Long.toString(creationTimeMs));
        }
        if (this.workspace.getFilter() != null) {
            addProperty(TIME_FILTER_PROPERTY, this.workspace.getFilter()
                .getLabel());
        }
        addOtherSettings();
        this.output.endTag();
    }
    
    
    private void addOtherSettings() throws Exception {
        WorkspaceSettings settings = this.workspace.getSettings();
        if (settings.getRoundUpInterval() != 0) {
            addProperty(ROUND_UP_INTERVAL, Long.toString(settings.getRoundUpInterval()));
        }
    }
    
    
    private void addFilters() throws Exception {
        this.output.startTag(FILTERS_TAG);
        for (TimeRecordFilter customFilter : this.workspace.getCustomFilters()
            .asArray()) {
            this.output.startTag(FILTER_TAG);
            this.output.attribute(FILTER_NAME_ATTR, customFilter.getLabel());
            this.output.attribute(FILTER_START_TIME, Long.toString(customFilter
                .getSinceDate().getTime()));
            this.output.attribute(FILTER_END_TIME, Long.toString(customFilter
                .getToDate().getTime()));
            this.output.endTag();
        }
        this.output.endTag();
    }
    
    private void addProperty(String name, String value) throws Exception {
        this.output.startTag(PROPERTY_TAG);
        this.output.attribute(NAME_ATTR, name);
        this.output.attribute(VALUE_ATTR, value);
        this.output.endTag();
    }
    
    
    private void addProjectTree() throws Exception {
        this.output.startTag(PROJECT_TREE_TAG);
        addSubprojects(this.workspace);
        addTasks(this.workspace);
        this.output.endTag();
    }
    
    private void addSubprojects(Project parent) throws Exception {
        Project[] subprojects = parent.getSubprojects(SortCriteria.DEFAULT);
        for (int i = 0; i < subprojects.length; i++) {
            Project subproject = subprojects[i];
            this.output.startTag(PROJECT_TAG);
            this.output.attribute(ID_ATTR, subproject.getId());
            this.output.attribute(NAME_ATTR, subproject.getName());
            StringBuffer flagsBuf = new StringBuffer();
            if (subproject.isExpanded()) {
                flagsBuf.append(EXPANDED_FLAG);
            }
            if (this.workspace.isSelected(subproject)) {
                if (flagsBuf.length() > 0) {
                    flagsBuf.append(',');
                }
                flagsBuf.append(SELECTED_FLAG);
            }
            if (flagsBuf.length() > 0) {
                this.output.attribute(FLAGS_ATTR, flagsBuf.toString());
            }
            addCommonItemAttributes(subproject);
            addNotes(subproject.getDescription());
            addSubprojects(subproject);
            addTasks(subproject);
            this.output.endTag();
        }
    }
    
    
    private void addCommonItemAttributes(ProjectTreeItem item) throws Exception {
        long creationTimeMs = item.getCreationDateTime().getTime();
        if (creationTimeMs != 0) {
            this.output.attribute(CREATION_DATETIME_ATTR, Long.toString(item
                .getCreationDateTime().getTime()));
        }        
        if (item.getCloseDateTime() != null) {
            this.output.attribute(CLOSE_DATETIME_ATTR, Long.toString(item
                .getCloseDateTime().getTime()));
        }
        String url = item.getHyperlink();
        if (url != null && !url.trim().isEmpty()) {
            this.output.attribute(HYPERLINK_ATTR, url);
        }
    }
    
    
    private void addTasks(Project parent) throws Exception {
        Task[] tasks = parent.getTasks(SortCriteria.DEFAULT);
        for (Task task : tasks) {
            switch(task.getItemType()) {
            case TASK:
            case IDLE_TASK:
                this.output.startTag(TASK_TAG);
                break;
            case ACTIVITY:
                this.output.startTag(ACTIVITY_TAG);
                break;
            }
            this.output.attribute(ID_ATTR, task.getId());
            this.output.attribute(NAME_ATTR, task.getName());
            this.output.attribute(STATUS_ATTR, task.getStatusAsString());
            if (task.isFlagged()) {
                this.output.attribute(FLAG_COLOR_ATTR, task.getFlagColor().toString());
            }
            if (task.getItemType() == ItemType.IDLE_TASK) {
                if (((IdleTask) task).getDefaultNote() != null) {
                    this.output.attribute(DEFAULT_NOTE_ATTR, ((IdleTask) task)
                        .getDefaultNote());
                }
            }
            if (this.workspace.isSelected(task)) {
                this.output.attribute(FLAGS_ATTR, SELECTED_FLAG);
            }
            addCommonItemAttributes(task);
            if (task.getStatus() == TaskStatus.WAITING
                && task.getWaitReason() != null) {
                addTextNode(WAIT_REASON_TAG, task.getWaitReason().getText());
            }
            addNotes(task.getDescription());
            this.output.endTag();
        }
    }
    
    
    private void addNotes(String notes) throws Exception {
        addTextNode(NOTES_TAG, notes);
    }
    
    
    private void addTextNode(String tag, String text) throws Exception {
        if (text != null && text.length() != 0) {
            this.output.startTag(tag);
            this.output.pcdata(text);
            this.output.endTag();
        }
    }
    
    
    private void addTimeLog() throws IOException {
        TimeRecord[] timeRecords = this.workspace.getTimeLog().getTimeRecords(
            null);
        this.output.startTag(TIME_LOG_TAG);
        for (int i = 0; i < timeRecords.length; i++) {
            TimeRecord timeRecord = timeRecords[i];
            this.output.startTag(TIME_REC_TAG);
            this.output.attribute(TASK_ID_ATTR, timeRecord.getTask().getId());
            this.output.attribute(START_TIME_ATTR, Long.toString(timeRecord
                .getStart().getTime()));
            this.output.attribute(DURATION_ATTR, Long.toString(timeRecord
                .getDuration().getValue()));
            if (timeRecord.getNotes() != null) {
                this.output.attribute(NOTES, timeRecord.getNotes());
            }
            this.output.endTag();
        }
        this.output.endTag();
    }
    
    
}

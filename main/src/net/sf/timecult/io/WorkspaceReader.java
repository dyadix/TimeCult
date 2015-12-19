/*
 * Copyright (c) Rustam Vishnyakov, 2005-2009 (dyadix@gmail.com)
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
 * $Id: WorkspaceReader.java,v 1.13 2010/04/02 14:31:34 dyadix Exp $
 */
package net.sf.timecult.io;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Reads a workspace from a datasource.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class WorkspaceReader extends DefaultHandler implements
        WorkspaceXMLElements {

    /**
     * Creates new workspace reader for the given workspace.
     * @param workspace
     */
    public WorkspaceReader(Workspace workspace) {
        _workspace = workspace;
        _currProject = _workspace.getRoot();
        _currHO = _currProject;
        _workspace.setCreationDateTime(new Date(0));
    }    

    public void readWorkspace(File workspaceFile) throws SAXException,
            ParserConfigurationException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser parser = spf.newSAXParser();
        _workspace.setFileName(workspaceFile.getName());
        parser.parse(workspaceFile, this);        
    }
    
    
    /** 
     * @return True if the file is being (or was) upgraded from the previous version of TimeCult.
     */
    public boolean isFileUpgradeMode() {
        return this.fileUpgradeMode;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        boolean hoEnd = false;
        if (PROJECT_TAG.equals(qName)) {
            if (!_projectStack.empty()) {
                _currProject = _projectStack.pop();
            }
            hoEnd = true;
        }
        else if (TASK_TAG.equals(qName)) {
            hoEnd = true;
        }
        else if (NOTES_TAG.equals(qName)) {
            if (_currHO != null && _currHO instanceof DescriptionHolder) {
                ((DescriptionHolder) _currHO).setDescription(_notesBuffer
                        .toString());
            }
            _notesBuffer = null;
        }
        else if (WAIT_REASON_TAG.equals(qName)) {
            if (_currHO != null && _currHO instanceof Task) {
                Task currTask = (Task) _currHO;
                currTask.setWaitReason(WaitReason.newInstance(_reasonBuffer.toString()));
            }
            _reasonBuffer = null;
        }
        else if (FILTERS_TAG.equals(qName)) {
            if (_workspace.getFilter() == null
                && this.selectedFilterName != null) {
                TimeRecordFilter filter = TimeRecordFilterFactory
                    .createFilter(this.selectedFilterName);
                _workspace.setFilter(filter);
            }
        }
        if (hoEnd) {
            if (!_hoStack.empty()) {
                _currHO = _hoStack.pop();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        try {
            if (PROJECT_TAG.equals(qName)) {
                _hoStack.push(_currHO);
                _projectStack.push(_currProject);
                String projId = attributes.getValue(ID_ATTR);
                String projName = attributes.getValue(NAME_ATTR);
                if (projId != null && projName != null) {
                    _currProject = _workspace.createChildProject(_currProject,
                            projId, projName);
                    _currHO = _currProject;
                    String flags = attributes.getValue(FLAGS_ATTR);
                    if(flags != null) {
                        if(flags.contains(EXPANDED_FLAG)) {
                            _currProject.setExpanded(true);
                        }
                        if(flags.contains(SELECTED_FLAG)) {
                            _workspace.setSelection(_currProject);
                        }
                    }
                    setCommonItemAttributes(attributes, _currProject);
                }
                else {
                    throw new InvalidFormatException(ResourceHelper
                            .getString("error.invalidFormat"));
                }                
            }
            else if (TASK_TAG.equals(qName) || ACTIVITY_TAG.equals(qName)) {
                boolean isActivity = ACTIVITY_TAG.equals(qName);
                _hoStack.push(_currHO);
                String taskId = attributes.getValue(ID_ATTR);
                String taskName = attributes.getValue(NAME_ATTR);
                String taskStatus = attributes.getValue(STATUS_ATTR);
                String flagColorStr = attributes.getValue(FLAG_COLOR_ATTR);
                if (taskId != null && taskName != null) {
                    Task task;
                    if (isActivity) {
                        task = _workspace.createActivity(
                            _currProject,
                            taskId,
                            taskName);
                    }
                    else {
                        task = _workspace.createTask(
                            _currProject,
                            taskId,
                            taskName);
                        if (task instanceof IdleTask) {
                            IdleTask idle = (IdleTask) task;
                            idle.setDefaultNote(attributes.getValue(DEFAULT_NOTE_ATTR));
                        }
                    }
                    if (taskStatus != null) {
                        task.setStatusFromString(taskStatus);
                    }
                    if (flagColorStr != null) {
                        task.setFlagColor(TaskStatus.parseColorString(flagColorStr));
                    }
                    setCommonItemAttributes(attributes, task);
                    String taskFlags = attributes.getValue(FLAGS_ATTR);
                    if (taskFlags != null && taskFlags.contains(SELECTED_FLAG)) {
                        _workspace.setSelection(task);
                    }
                    _currHO = task;
                }
                else {
                    throw new InvalidFormatException(ResourceHelper
                        .getString("error.invalidFormat"));
                }
            }
            else if (OLD_ROOT_TAG.equals(qName) || ROOT_TAG.equals(qName)) {
                String fileVersionStr = attributes.getValue(FILE_VERSION_ATTR);
                // Is this an old (pre-0.7.0) file? (version 0 file format)
                if (fileVersionStr == null) {
                    // 
                    // For backwards compatibility we are still using the old
                    // method
                    // of getting the ID.
                    //
                    String lastId = attributes.getValue(LAST_ID_ATTR);
                    if (lastId != null) {
                        _workspace.getIdGenerator().setCurrentId(lastId);
                    }
                    this.fileUpgradeMode = true;
                }
                else {
                    int fileVersion = Integer.parseInt(fileVersionStr);
                    if (FILE_VERSION < fileVersion) {
                        throw new InvalidFormatException(ResourceHelper
                            .getString("error.newerFormat"));
                    }
                    else if (FILE_VERSION > fileVersion) {
                        this.fileUpgradeMode = true;
                    }
                }
                String workspaceName = attributes.getValue(NAME_ATTR);
                if (workspaceName != null) {
                    _workspace.setName(workspaceName);
                }
                String uuid = attributes.getValue(UUID_ATTR);
                if (uuid != null) {
                    _workspace.setUUIDString(uuid);
                }
            }
            else if (NOTES_TAG.equals(qName)) {
                _notesBuffer = new StringBuffer();
            }
            else if (WAIT_REASON_TAG.equals(qName)) {
                _reasonBuffer = new StringBuffer();
            }
            else if (TIME_REC_TAG.equals(qName)) {
                String taskId = attributes.getValue(TASK_ID_ATTR);
                if (taskId != null) {
                    Task task = _workspace.findTask(taskId);
                    if (task != null) {
                        String startTimeStr = attributes
                                .getValue(START_TIME_ATTR);
                        String durationStr = attributes.getValue(DURATION_ATTR);
                        if (startTimeStr != null && durationStr != null) {
                            long startTimeMs = Long.parseLong(startTimeStr);
                            Date startTime = new Date(startTimeMs);
                            long durationMs = Long.parseLong(durationStr);
                            String notes = attributes.getValue(NOTES);
                            TimeRecord timeRec = _workspace.createRecord(
                                task,
                                startTime,
                                durationMs,
                                notes,
                                false);
                            _workspace.recordTime(timeRec);
                            //
                            // If the task is marked as non-started, enforce it's
                            // status to 'in progress' anyway because it has some
                            // time records.
                            //
                            if (task.getStatus().getId() == TaskStatus.NOT_STARTED) {
                                task.setStatus(TaskStatus.IN_PROGRESS);
                            }
                        }
                    }
                    else {
                        //
                        // For now just ignore the case to be backwards compatible
                        // with versions prior to 0.7.0 which still may have undeleted time
                        // records in time log [bug #1557454]
                        /*
                        throw new InvalidFormatException(ResourceHelper
                                .getString("error.invalidFormat"));
                        */
                    }
                }
                else {
                    throw new InvalidFormatException(ResourceHelper
                            .getString("error.invalidFormat"));
                }
            }
            else if (PROPERTY_TAG.equals(qName)) {
                String name = attributes.getValue(NAME_ATTR);
                String value = attributes.getValue(VALUE_ATTR);
                setProperty(name, value);
            }
            else if (FILTER_TAG.equals(qName)) {
                String name = attributes.getValue(FILTER_NAME_ATTR);
                String startTime = attributes.getValue(FILTER_START_TIME);
                String endTime = attributes.getValue(FILTER_END_TIME);
                addFilter(name, startTime, endTime);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }

    }

    public void setProperty(String name, String value) {
        if (name != null && value != null) {
            if (IDLE_TASK_ENABLED.equals(name)) {
                boolean idleEnabled = Boolean.parseBoolean(value);
                if (idleEnabled) {
                    _workspace.enableIdle();
                }
                else {
                    _workspace.disableIdle();
                }
            }
            else if(LAST_ID_PROPERTY.equals(name)) {
                _workspace.getIdGenerator().setCurrentId(value);
            }
            else if(CREATION_DATETIME_ATTR.equals(name)) {
                long creationTimeMs = Long.parseLong(value);
                _workspace.setCreationDateTime(new Date(creationTimeMs));
            }
            else if (TIME_FILTER_PROPERTY.equals(name)) {
                this.selectedFilterName = value;
            }
            else if (ROUND_UP_INTERVAL.equals(name)) {
                long interval = Long.parseLong(value);
                _workspace.getSettings().setRoundUpInterval(interval);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String appendStr = new String(ch, start, length);        
        if (_notesBuffer != null) {
            _notesBuffer.append(appendStr);
        }
        else if(_reasonBuffer != null) {
            _reasonBuffer.append(appendStr);
        }
    }
    
    
    private void setCommonItemAttributes(Attributes attributes, ProjectTreeItem item) {
        String creationTimeStr = attributes.getValue(CREATION_DATETIME_ATTR);
        if (creationTimeStr != null) {
            long creationTimeMs = Long.parseLong(creationTimeStr);
            item.setCreationDateTime(new Date(creationTimeMs));
        }
        String closeTimeStr = attributes.getValue(CLOSE_DATETIME_ATTR);
        if (closeTimeStr != null) {
            long closeTimeMs = Long.parseLong(closeTimeStr);
            item.setCloseDateTime(new Date(closeTimeMs));
        }
        String url = attributes.getValue(HYPERLINK_ATTR);
        if (url != null) {
            item.setHyperlink(url);
        }
        String deadlineStr = attributes.getValue(DEADLINE);
        if (deadlineStr != null) {
            long deadlineTimeMs = Long.parseLong(deadlineStr);
            item.setDeadline(new Date(deadlineTimeMs));
        }
    }
    
    
    private void addFilter(String name, String startTime, String endTime)
        throws InvalidFormatException {
        try {
            Date startDate = new Date(Long.parseLong(startTime));
            Date endDate = new Date(Long.parseLong(endTime));
            TimeRecordFilter customFilter = new TimeRecordFilter(name);
            customFilter.setSinceDate(startDate);
            customFilter.setToDate(endDate);
            customFilter.setCustom(true);
            this._workspace.getCustomFilters().addFilter(customFilter);
            if (this.selectedFilterName != null && this.selectedFilterName.equals(name)) {
                this._workspace.setFilter(customFilter);
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidFormatException(ResourceHelper
                .getString("error.invalidFormat"));
        }
    }

    
    private Workspace _workspace = null;
    private Stack<Project> _projectStack = new Stack<Project>();
    private Project _currProject = null;
    private Stack<Object> _hoStack = new Stack<Object>();
    private boolean fileUpgradeMode = false; 
    private String selectedFilterName;
    
    /*
     * Current hierachy object (HO)
     */
    private Object _currHO = null;
    private StringBuffer _notesBuffer = null;
    private StringBuffer _reasonBuffer;

}

/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (dyadix@gmail.com)
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

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import net.sf.timecult.AppInfo;
import net.sf.timecult.model.*;
import net.sf.timecult.model.Project.SortCriteria;

/**
 * Saves a workspace to XML file. The current implementation does that via a DOM
 * tree to use a standard built-in transformation mechanism.
 * @author rvishnyakov
 * @deprecated Use WorkspaceXMLWriter instead
 */
public class WorkspaceWriter implements WorkspaceXMLElements {

    public WorkspaceWriter(Workspace workspace) {
        _workspace = workspace;
        try {
            this.docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            this.topNode = null;
        }
        catch (ParserConfigurationException err) {
            err.printStackTrace();
        }
    }

    public void saveWorkspace(File workspaceFile) throws Exception {
        workspaceToDom();
        saveDocument(workspaceFile);
    }

    private void saveDocument(File outFile) throws Exception {

        TransformerFactory tfFac = TransformerFactory.newInstance();
        Transformer tf = tfFac.newTransformer();
        tf.setOutputProperty("encoding", "UTF-8");
        tf.setOutputProperty("indent", "yes");
        OutputStream out = new FileOutputStream(outFile);
        OutputStreamWriter wr = new OutputStreamWriter(out, "UTF-8");
        StreamResult sr = new StreamResult(wr);
        tf.transform(new DOMSource(this.document), sr);
    }

    private void workspaceToDom() throws IOException {
        this.document = docBuilder.getDOMImplementation().createDocument(null,
                ROOT_TAG, null);
        this.topNode = this.document.getDocumentElement();
        addAttribute(APP_VERSION_ATTR, AppInfo.getMajorVersion() + "."
                + AppInfo.getMinorVersion());
        addAttribute(FILE_VERSION_ATTR, Integer.toString(FILE_VERSION));
        //
        // This is a hack to make newer file version explicitely incompatible
        // with older application versions. Otherwise read errors do not get reported
        // (due to a previously existing bug in all version before 0.7.0).
        //
        addAttribute(LAST_ID_ATTR, "Incompatible version");
        
        addAttribute(NAME_ATTR, _workspace.getName());
        addAttribute(UUID_ATTR, _workspace.getUUIDString());
        
        addSettings(_workspace);
        addProjectTree(_workspace);
        addTimeLog();
    }
    
    private void addProjectTree(Workspace _workspace) throws IOException{
        createSubNode(PROJECT_TREE_TAG);
        addSubprojects(_workspace);
        addTasks(_workspace);
        toPreviousLevel();
    }

    private void addSettings(Workspace _workspace) {
        createSubNode(SETTINGS_TAG);
        addProperty(IDLE_TASK_ENABLED, Boolean.toString(_workspace
            .isIdleEnabled()));
        addProperty(LAST_ID_PROPERTY, _workspace.getIdGenerator().getLastId());
        long creationTimeMs = _workspace.getCreationDateTime().getTime();
        if (creationTimeMs != 0) {
            addProperty(CREATION_DATETIME_ATTR, Long.toString(creationTimeMs));
        }
        if (_workspace.getFilter() != null) {
            addProperty(TIME_FILTER_PROPERTY, _workspace.getFilter().getLabel());
        }
        toPreviousLevel();
    }

    private void addProperty(String name, String value) {
        createSubNode(PROPERTY_TAG);
        addAttribute(NAME_ATTR, name);
        addAttribute(VALUE_ATTR, value);
        toPreviousLevel();
    }

    private Element createSubNode(String name) {
        Element element = this.document.createElement(name);
        addSubNode(element);
        return element;
    }

    private void addSubNode(Node node) {
        this.topNode.appendChild(node);
        this.topNode = node;
    }

    private void toPreviousLevel() {
        this.topNode = this.topNode.getParentNode();
    }

    private void addAttribute(String name, String value) {
        Element e = (Element) this.topNode;
        e.setAttribute(name, value);
    }

    private void addSubprojects(Project parent) throws IOException {
        Project[] subprojects = parent.getSubprojects(SortCriteria.DEFAULT);
        for (int i = 0; i < subprojects.length; i++) {
            Project subproject = subprojects[i];
            createSubNode(PROJECT_TAG);
            addAttribute(ID_ATTR, subproject.getId());
            addAttribute(NAME_ATTR, subproject.getName());
            StringBuffer flagsBuf = new StringBuffer();
            if (subproject.isExpanded()) {
                flagsBuf.append(EXPANDED_FLAG);
            }
            if (_workspace.isSelected(subproject)) {
                if (flagsBuf.length() > 0) {
                    flagsBuf.append(',');
                }
                flagsBuf.append(SELECTED_FLAG);
            }
            if (flagsBuf.length() > 0) {
                addAttribute(FLAGS_ATTR, flagsBuf.toString());
            }
            addDateTimeAttributes(subproject);
            addNotes(subproject.getDescription());
            addSubprojects(subproject);
            addTasks(subproject);
            toPreviousLevel();
        }
    }

    private void addTasks(Project parent) throws IOException {
        Task[] tasks = parent.getTasks(SortCriteria.DEFAULT);
        for (int i = 0; i < tasks.length; i++) {
            Task task = tasks[i];
            createSubNode(TASK_TAG);
            addAttribute(ID_ATTR, task.getId());
            addAttribute(NAME_ATTR, task.getName());
            addAttribute(STATUS_ATTR, task.getStatusAsString());
            if (_workspace.isSelected(task)) {
                addAttribute(FLAGS_ATTR, SELECTED_FLAG);
            }
            addDateTimeAttributes(task);
            if (task.getStatus() == TaskStatus.WAITING
                && task.getWaitReason() != null) {
                addTextNode(WAIT_REASON_TAG, task.getWaitReason().getText());
            }
            addNotes(task.getDescription());
            toPreviousLevel();
        }
    }

    private void addTimeLog() throws IOException {
        TimeRecord[] timeRecords = _workspace.getTimeLog().getTimeRecords(null);
        createSubNode(TIME_LOG_TAG);
        for (int i = 0; i < timeRecords.length; i++) {
            TimeRecord timeRecord = timeRecords[i];
            createSubNode(TIME_REC_TAG);
            addAttribute(TASK_ID_ATTR, timeRecord.getTask().getId());
            addAttribute(START_TIME_ATTR, Long.toString(timeRecord.getStart()
                    .getTime()));
            addAttribute(DURATION_ATTR, Long.toString(timeRecord.getDuration()
                    .getValue()));
            if (timeRecord.getNotes() != null) {
                addAttribute(NOTES, timeRecord.getNotes());
            }
            toPreviousLevel();
        }
        toPreviousLevel();
    }

    private void addNotes(String notes) {
        addTextNode(NOTES_TAG, notes);
    }
    
    private void addTextNode(String tag, String text) {
        if (text != null && text.length() != 0) {
            Element notesElem = createSubNode(tag);
            Text content = this.document.createTextNode(text);
            notesElem.appendChild(content);
            toPreviousLevel();
        }
    }
    
    
    private void addDateTimeAttributes(ProjectTreeItem item) {
        long creationTimeMs = item.getCreationDateTime().getTime();
        if (creationTimeMs != 0) {
            addAttribute(CREATION_DATETIME_ATTR, Long.toString(item
                .getCreationDateTime().getTime()));
        }        
        if (item.getCloseDateTime() != null) {
            addAttribute(CLOSE_DATETIME_ATTR, Long.toString(item
                .getCloseDateTime().getTime()));
        }
    }

    private Workspace _workspace = null;
    private DocumentBuilder docBuilder = null;
    private Document document = null;
    private Node topNode = null;
}

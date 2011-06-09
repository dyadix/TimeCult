/*
 * Copyright (c) Rustam Vishnyakov, 2008-2010 (dyadix@gmail.com)
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
 * $Id: TaskListToolbar.java,v 1.8 2010/09/26 11:06:06 dyadix Exp $
 */
package net.sf.timecult.ui.swt.tasklist;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.export.pdf.PdfTaskListExporter;
import net.sf.timecult.model.Activity;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.ui.swt.IconSet;
import net.sf.timecult.ui.swt.SWTMainWindow;

import net.sf.timecult.ui.swt.ToolBarBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TaskListToolbar extends ToolBarBase {
    
    private TaskListView taskView;
    private LinkedList<ToolItem> items;

    public TaskListToolbar(TaskListView taskView, Composite contentPanel) {
        super(contentPanel.getShell(), taskView.getMainWindow().getIconSet(), 2);
        this.taskView = taskView;
    }

    protected void setup(int toolBarNumber) {
        switch (toolBarNumber) {
            case 0:
                this.items = new LinkedList<ToolItem>();
                items.add(createStatusButton(new TaskStatus(TaskStatus.FlagColor.RED), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.FlagColor.ORANGE), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.FlagColor.BLUE), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.FlagColor.GREEN), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.FlagColor.MAGENTA), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.NOT_STARTED), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.IN_PROGRESS), Task.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.IN_PROGRESS), Activity.class));
                items.add(createStatusButton(new TaskStatus(TaskStatus.WAITING), Task.class));
                this.items.get(0).setSelection(true);
                break;

            case 1:

                ToolItem exportToPdfButton = createButton("tasklist.pdf", SWT.PUSH);
                exportToPdfButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        exportToPdf();
                    }
                });
                break;
        }
    }       
        
    
    private ToolItem createStatusButton(TaskStatus status, Class subtype) {
        String tag = status.toString();
        if (subtype.equals(Activity.class)) {
            tag = "activity";
        }
        if (status.getId() == TaskStatus.FLAGGED) {
            tag = status.getFlagColor().toString().toLowerCase() + "Flag";
        }
        else {
            tag = "tasklist." + tag;
        }
        ToolItem item = createButton(tag, SWT.RADIO);
        item.setData(new TaskData(status, subtype));
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() instanceof ToolItem) {
                    ToolItem selButton = (ToolItem) e.getSource();
                    updateTable(selButton);
                }
            }
        });
        return item;
    }
    
    
    private class TaskData {
        private Class subtype;
        private TaskStatus status;
        
        public TaskData(TaskStatus status, Class subtype) {
            this.subtype = subtype;
            this.status = status;
        }
        
        public TaskStatus getStatus() {
            return this.status;
        }
        
        public Class getSubtype() {
            return this.subtype;
        }
    }
    
    /**
     * Selects the next tool button and updates the task list accordingly.
     * If the current button is the last in the row, the first button is
     * selected.
     */
    public void selectNext() {
        boolean selectionFound = false;
        for (ToolItem item : this.items) {
            if (selectionFound) {
                item.setSelection(true);
                updateTable(item);
                break;
            }
            if (item.getSelection()) {
                item.setSelection(false);
                if (item != items.getLast()) {
                    selectionFound = true;
                }
            }
        }
        if (!selectionFound && !items.isEmpty()) {
            ToolItem first = items.getFirst();
            first.setSelection(true);
            updateTable(first);
        }
    }

    
    /**
     * Selects the previous tool button and updates the task list accordingly.
     * If the current button is the first in the row, the last button is
     * selected.
     */
    public void selectPrev() {
        boolean selectionFound = false;
        for (Iterator<ToolItem> iter = items.descendingIterator(); iter
            .hasNext();) {
            ToolItem item = iter.next();
            if (selectionFound) {
                item.setSelection(true);
                updateTable(item);
                break;
            }
            if (item.getSelection()) {
                item.setSelection(false);
                if (item != items.getFirst()) {
                    selectionFound = true;
                }
            }
        }
        if (!selectionFound && !items.isEmpty()) {
            ToolItem last = items.getLast();
            last.setSelection(true);
            updateTable(last);
        }
    }    
    
    
    private void updateTable(ToolItem item) {
        TaskData taskData = (TaskData) item.getData();
        TaskListToolbar.this.taskView.listTasks(taskData.getStatus(), taskData
            .getSubtype());
    }
    

    private void exportToPdf() {
        try {
            File pdfTarget = File.createTempFile("tasklist_", ".pdf");
            if (pdfTarget != null) {
                PdfTaskListExporter.export(TimeTracker.getInstance()
                    .getWorkspace(), pdfTarget.getPath());
            }            
            Program.launch(pdfTarget.getPath());
        }
        catch (Exception e) {
            e.printStackTrace();
            this.taskView.getMainWindow().showError(
                e.getClass().getName() + ":" + e.getMessage());
        }
    }
               
}

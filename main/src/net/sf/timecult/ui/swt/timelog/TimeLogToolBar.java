/*
 * Copyright (c) Rustam Vishnyakov, 2007-2010 (dyadix@gmail.com)
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
 * $Id: TimeLogToolBar.java,v 1.10 2011/01/18 02:57:53 dragulceo Exp $
 */
package net.sf.timecult.ui.swt.timelog;

import net.sf.timecult.model.Duration;
import net.sf.timecult.ui.swt.ToolBarBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;

import net.sf.timecult.model.Task;
import net.sf.timecult.model.TimeRecord;
import net.sf.timecult.ui.swt.SWTTimeLogTableView;

public class TimeLogToolBar extends ToolBarBase implements SelectionListener {

    private SWTTimeLogTableView timeLogTable;
    private ToolItem            editButton;
    private ToolItem            addButton;
    private ToolItem            joinButton;


    public TimeLogToolBar(SWTTimeLogTableView timeLogTable) {
        super(timeLogTable.getContentArea(), timeLogTable.getMainWindow().getIconSet(), 1,
                new Color(timeLogTable.getContentArea().getDisplay(), 255, 255, 255));
        this.timeLogTable = timeLogTable;
    }


    @Override
    protected void setup(int i) {
        addButton = createAddButton();
        addButton.setEnabled(false);
        editButton = createEditButton();
        createDeleteButton();
        joinButton = createJoinButton();
    }


    private ToolItem createEditButton() {
        ToolItem item = createButton("editRecord", SWT.PUSH);
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeLogToolBar.this.timeLogTable.editSelection();
            }
        });
        return item;
    }
    
    private ToolItem createAddButton() {
        ToolItem item = createButton("addRecord", SWT.PUSH);
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeLogToolBar.this.timeLogTable.addNewEntry();
            }
        });
        return item;
    }


    private ToolItem createDeleteButton() {
        ToolItem item = createButton("deleteRecord", SWT.PUSH);
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeLogToolBar.this.timeLogTable.removeSelected();
            }
        });
        return item;
    }
    
    private ToolItem createJoinButton() {
        ToolItem item = createButton("joinRecords", SWT.PUSH);
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeLogToolBar.this.timeLogTable.joinSelected();
            }
        });
        item.setEnabled(false);
        return item;
    }

    public void updateOnTreeSelection(Object selection) {
        addButton.setEnabled(false);
        if (selection != null && selection instanceof Task) {
            Task task = (Task)selection;
            if (!task.isClosed()) {
                addButton.setEnabled(true);
            }
        }
    }


    /*
     * A listener for TimeLog table selection events
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent evt) {
        Object source = evt.getSource();
        if (source != null && source instanceof Table) {
            TableItem items[] = ((Table) source).getSelection();
            if (items.length > 0) {
                long total_duration = 0;
                for (int i = 0; i < items.length; i++) {
                    if(items[i].getData() instanceof TimeRecord) {
                        total_duration += ((TimeRecord) items[i].getData()).getDuration().getValue();
                    }
                }
                timeLogTable.getMainWindow().getStatusLine().setSelectionLabel("Duration: " + (new Duration(total_duration)).toString());
                if (items.length > 1) {
                    this.joinButton.setEnabled(true);
                } else {
                    this.joinButton.setEnabled(false);
                }
                TableItem item = items[0];
                if (item.getData() != null
                    && item.getData() instanceof TimeRecord) {
                    TimeRecord timeRec = (TimeRecord) item.getData();
                    if (timeRec.getTask().isClosed()) {
                        this.editButton.setEnabled(false);
                    }
                    else {
                        this.editButton.setEnabled(true);
                    }
                }
            }
            else {
                this.editButton.setEnabled(false);
            }
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        // Do nothing
    }

}

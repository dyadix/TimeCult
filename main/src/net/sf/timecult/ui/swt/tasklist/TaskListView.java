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
 * $Id: TaskListView.java,v 1.8 2010/12/14 12:14:22 dyadix Exp $
 */
package net.sf.timecult.ui.swt.tasklist;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;
import net.sf.timecult.util.ObjectInfoHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.Collection;
import java.util.TreeMap;

public class TaskListView extends SWTDialog {

    private static final String[] titleKeys = { "table.task" };
    private static final int[]    align     = { SWT.LEFT };
    private SWTMainWindow         mainWindow;
    private Table                 taskListTable;
    private TaskStatus            taskStatus = new TaskStatus(TaskStatus.IN_PROGRESS);
    private Class                 taskSubtype;
    private TaskListToolbar       toolbar;
    private Label                 infoText;
    private Label                 parentText;

    private static Point lastSize;


    public TaskListView(SWTMainWindow mainWindow) {
        super(mainWindow.getShell(), true, false, true);
        this.mainWindow = mainWindow;
        this.taskSubtype = Task.class;
    }


    @Override
    protected Composite createContentPanel(Shell shell) {
        toolbar = new TaskListToolbar(this, shell);

        GridLayout contentLayout = new GridLayout();
        contentLayout.numColumns = 1;
        contentLayout.makeColumnsEqualWidth = true;
        Composite contentPanel = new Composite(shell, SWT.NONE);
        contentPanel.setLayout(contentLayout);

        GridData tableLayoutData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL);
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        grid.makeColumnsEqualWidth = true;
        
        GridData infoLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        this.parentText = new Label(contentPanel, SWT.None);
        this.parentText.setLayoutData(infoLayoutData);
        
        taskListTable = new Table(contentPanel, SWT.FULL_SELECTION | SWT.BORDER);
        taskListTable.setLayoutData(tableLayoutData);
        taskListTable.setLinesVisible(true);
        taskListTable.setHeaderVisible(true);

        taskListTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.keyCode == SWT.CR) {
                    switchToSelection();
                    TaskListView.this.getShell().setVisible(false);
                }
                else if(evt.keyCode == SWT.ARROW_RIGHT) {
                    TaskListView.this.toolbar.selectNext();
                }
                else if(evt.keyCode == SWT.ARROW_LEFT) {
                    TaskListView.this.toolbar.selectPrev();
                }
            }
        });
        taskListTable.setFocus();
        taskListTable.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing                
            }

            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() != null && e.getSource() instanceof Table) {
                    TableItem items[] = ((Table)e.getSource()).getSelection();
                    if (items.length > 0) {
                        onRowSelection(items[0]);
                    }
                }                               
            }});

        for (int i = 0; i < titleKeys.length; i++) {
            TableColumn column = new TableColumn(taskListTable, SWT.NONE);
            column.setAlignment(align[i]);
            column.setText(ResourceHelper.getString(titleKeys[i]));
        }
        addTableData();

        taskListTable.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                int width = taskListTable.getClientArea().width;
                taskListTable.getColumn(0).setWidth(width);
            }
        });
        
        this.infoText = new Label(contentPanel, SWT.None);        
        this.infoText.setLayoutData(infoLayoutData);       

        return contentPanel;
    }


    @Override
    protected void createButtons(Composite buttonPanel) {
        GridData buttonData = new GridData();
        Button selectButton = new Button(buttonPanel, SWT.FLAT);
        selectButton.setLayoutData(buttonData);

        selectButton.setText(ResourceHelper.getString("button.select"));
        selectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                switchToSelection();
                TaskListView.this.getShell().setVisible(false);
            }
        });

        Button closeButton = new Button(buttonPanel, SWT.FLAT);
        closeButton.setLayoutData(buttonData);

        closeButton.setText(ResourceHelper.getString("button.close"));
        closeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                close();
            }
        });
    }


    private void addTableData() {
        Task filteredTasks[] = TimeTracker.getInstance().getWorkspace()
            .getTasksByStatus(this.taskStatus);
        TreeMap<String, Task> sortedItems = new TreeMap<String, Task>();
        for (Task filteredTask : filteredTasks) {
            sortedItems.put(filteredTask.toString(), filteredTask);
        }
        Collection<Task> tasks = sortedItems.values();
        for (Task task : tasks) {
            //
            // Add flagged items regardless of their subtype (task or activity)
            //
            if ((this.taskStatus.getId() == TaskStatus.FLAGGED && task.getStatusId() == TaskStatus.FLAGGED)
                || task.getClass().equals(this.taskSubtype)) {
                TableItem taskItem = new TableItem(taskListTable, SWT.NONE);
                taskItem.setData(task);
                taskItem.setText(task.toString());
            }
        }
    }


    private void switchToSelection() {
        TableItem[] items = taskListTable.getSelection();
        if (items.length > 0) {
            Task selectedTask = (Task) items[0].getData();
            this.mainWindow.getProjectTreeView().setCurrentSelection(selectedTask);
        }
    }


    @Override
    protected String getTitle() {
        return ResourceHelper.getString("tasklist.title");
    }


    @Override
    protected boolean handleOk() {
        return true;
    }
    
    
    public SWTMainWindow getMainWindow() {
        return this.mainWindow;
    }
    
    
    void listTasks(TaskStatus status, Class subtype) {
        this.taskStatus = status;
        this.taskListTable.removeAll();
        this.taskSubtype = subtype;
        this.addTableData();
    }
    
    
    public TaskStatus getCurrTaskStatus() {
        return this.taskStatus;
    }
    
    private void onRowSelection(TableItem item) {
        Object data = item.getData();
        if (data != null && data instanceof ProjectTreeItem) {
            ProjectTreeItem projItem = (ProjectTreeItem)data;
            this.parentText.setText(projItem.getParent().toString());
            this.infoText.setText(ObjectInfoHelper.getObjectInfo(projItem));
        }
    }

    @Override
    protected Point getPreferredSize() {
        return lastSize;
    }

    @Override
    protected void storeCurrentSize(Point size) {
        lastSize = size;
    }

    @Override
    protected void setup(Shell shell) {
        super.setup(shell);
        Rectangle displayRect = shell.getDisplay().getBounds();
        shell.setSize(displayRect.width / 2, displayRect.height / 2);
    }
}

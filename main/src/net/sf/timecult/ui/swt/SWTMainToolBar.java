/*
 * Copyright (c) TimeCult Project Team, 2005-2023 (dyadix@gmail.com)
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
 * $Id: $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.ui.report.TimeSheetHelper;
import net.sf.timecult.ui.swt.search.FindDialog;
import net.sf.timecult.ui.swt.tasklist.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Main tool bar wrapper.
 */
public class SWTMainToolBar extends ToolBarBase {

    private ToolItem _saveButton;
    private ToolItem _startButton;
    private ToolItem _timeSheetButton;
    private ToolItem _pomoButton;

    private final SWTMainWindow _mainWindow;

    public SWTMainToolBar(SWTMainWindow window) {
        super(window.getShell(), window.getIconSet(), 1);
        _mainWindow = window;
    }

    @Override
    protected void setup(int toolBarNumber) {
        if (toolBarNumber == 0) {
            createNewWorkspaceButton();
            createOpenButton();
            createSaveButton();
            createStartButton();
            createPomodoroButton();
            createShowFlaggedButton();
            createTimeSheetButton();
            createFindButton();
        }
    }


    private void createNewWorkspaceButton() {
        ToolItem newWorkspaceButton = createButton("newWorkspace", SWT.PUSH);
        newWorkspaceButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().resetWorkspace();
            }
        });
    }

    private void createOpenButton() {
        ToolItem _openButton = createButton("open", SWT.PUSH);
        _openButton.setEnabled(true);
        _openButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().openWorkspace();
            }
        });
    }


    private void createSaveButton() {
        _saveButton = createButton("save", SWT.PUSH);
        _saveButton.setEnabled(false);
        _saveButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().saveWorkspace(true);
            }
        });
    }

    private void createStartButton() {
        _startButton = createButton("start", SWT.PUSH);
        _startButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().startTimer();
            }
        });
        _startButton.setEnabled(false);

    }


    private void createShowFlaggedButton() {
        ToolItem _showFlaggedButton = createButton("tasklist", SWT.PUSH);
        _showFlaggedButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TaskListView taskListView = new TaskListView(_mainWindow);
                taskListView.open();
            }
        });
    }

    private void createTimeSheetButton() {
        _timeSheetButton = createButton("quickTimesheet", SWT.PUSH);
        _timeSheetButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ProjectTreeItem selection = TimeTracker.getInstance()
                    .getWorkspace().getSelection();
                if (selection instanceof Project) {
                    TimeSheetHelper tsh = new TimeSheetHelper(_mainWindow
                        .getShell(), (Project) selection);
                    tsh.openTimeSheet();
                }
            }
        });
    }


    private void createFindButton() {
        ToolItem findButton = createButton("find", SWT.PUSH);
        findButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FindDialog findDialog = new FindDialog(_mainWindow, TimeTracker.getInstance().getWorkspace());
                findDialog.open();
            }
        });
    }

    private void createPomodoroButton() {
        _pomoButton = createButton("tomato", SWT.PUSH);
        _pomoButton.setEnabled(true);
    }

    public void updateOnSelection(Object selection) {
        _startButton.setEnabled(false);
        _pomoButton.setEnabled(false);
        _timeSheetButton.setEnabled(false);
        if (selection instanceof Task task) {
            if (task.isStandaloneTimerEnabled()) {
                _startButton.setEnabled(true);
                _pomoButton.setEnabled(true);
            }
        } else if (selection instanceof Project) {
            _timeSheetButton.setEnabled(true);
        }
    }


    public void setSaveEnabled(boolean enabled) {
        _saveButton.setEnabled(enabled);
    }

}

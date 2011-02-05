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
 * $Id: SWTMainToolBar.java,v 1.19 2010/12/12 14:41:50 dyadix Exp $
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
 * @author rvishnyakov
 */
public class SWTMainToolBar extends ToolBarBase {

    private ToolItem _openButton;
	private ToolItem _saveButton;
	private ToolItem _newWorkspaceButton;
	private ToolItem _startButton;
    private ToolItem _showFlaggedButton;
    private ToolItem _timeSheetButton;
	private SWTMainWindow _mainWindow;
	
	protected static final Project TimeSheetDialog = null;

    public SWTMainToolBar(SWTMainWindow window) {
        super(window.getShell(), window.getIconSet(), 1);
		_mainWindow = window;
	}

    @Override
    protected void setup(int toolBarNumber) {
        switch (toolBarNumber) {
            case 0:
                createNewWorkspaceButton();
                createOpenButton();
                createSaveButton();
                createStartButton();
                createShowFlaggedButton();
                createTimeSheetButton();
                createFindButton();
        }
    }
	
	
    private ToolItem createNewWorkspaceButton() {
        _newWorkspaceButton = createButton("newWorkspace", SWT.PUSH);
        _newWorkspaceButton.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				TimeTracker.getInstance().resetWorkspace();
			}
		});
        return _newWorkspaceButton;
    }
	
	private ToolItem createOpenButton() {
        _openButton = createButton("open", SWT.PUSH);
        _openButton.setEnabled(true);
        _openButton.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				TimeTracker.getInstance().openWorkspace();
			}
		});
        return _openButton;
	}
	
	
    private ToolItem createSaveButton() {
        _saveButton = createButton("save", SWT.PUSH);
        _saveButton.setEnabled(false);
        _saveButton.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		TimeTracker.getInstance().saveWorkspace(true);
        	}
        });
        return _saveButton;
    }
    
    private ToolItem createStartButton() {
		_startButton = createButton("start", SWT.PUSH);
		_startButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TimeTracker.getInstance().startTimer();
			}
		});
		_startButton.setEnabled(false);

		return _startButton;
	}
    
    
    private ToolItem createShowFlaggedButton() {
        _showFlaggedButton = createButton("tasklist", SWT.PUSH);
        _showFlaggedButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TaskListView taskListView = new TaskListView(_mainWindow);
                taskListView.open();
            }
        });
        return _showFlaggedButton;
    }
    
    private ToolItem createTimeSheetButton() {
        _timeSheetButton = createButton("quickTimesheet", SWT.PUSH);
        _timeSheetButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ProjectTreeItem selection = TimeTracker.getInstance()
                    .getWorkspace().getSelection();
                if (selection != null && selection instanceof Project) {
                    TimeSheetHelper tsh = new TimeSheetHelper(_mainWindow
                        .getShell(), (Project) selection);
                    tsh.openTimeSheet();
                }
            }
        });
        return _timeSheetButton;
    }


    private ToolItem createFindButton() {
        ToolItem findButton = createButton("find", SWT.PUSH);
        findButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FindDialog findDialog = new FindDialog(_mainWindow, TimeTracker.getInstance().getWorkspace());
                findDialog.open();
            }
        });
        return findButton;
    }
    
    public void updateOnSelection(Object selection) {
    	_startButton.setEnabled(false);
        _timeSheetButton.setEnabled(false);
        if (selection instanceof Task) {
            Task task = (Task) selection;
            if (task.isStandaloneTimerEnabled()) {
                _startButton.setEnabled(true);
            }
        }
        else if(selection instanceof Project) {
            _timeSheetButton.setEnabled(true);
        }
    }
    
    
	
	public void setSaveEnabled(boolean enabled) {
		_saveButton.setEnabled(enabled);
	}

}

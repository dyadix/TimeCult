/*
 * Copyright (c) Rustam Vishnyakov, 2005-2011 (dyadix@gmail.com)
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
 * $Id: SWTProjectTreePopup.java,v 1.22 2009/11/21 13:56:17 dyadix Exp $
 */

package net.sf.timecult.ui.swt;

import java.util.Calendar;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.IdleTask;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.model.WaitReason;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.WorkspaceEvent;
import net.sf.timecult.model.WorkspaceListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

public class SWTProjectTreePopup implements WorkspaceListener {

    private SWTProjectTreeView _treeView;
    private Menu _popup;
    private MenuItem _deleteItem;
    private MenuItem _doneItem;
    private MenuItem _enableIdle;
    private MenuItem _disableIdle;
    private MenuItem _cancelItem;
    private MenuItem _markWithItem;
    private MenuItem _waitItem;
    private MenuItem _reopenItem;
    private ProjectTreeItem _selection;

	public SWTProjectTreePopup(SWTProjectTreeView projTreeView) {
		_treeView = projTreeView;
		setup(projTreeView.getTree());
	}
	
	private void setup(Tree tree) {
		_popup = new Menu(tree);
		tree.setMenu(_popup);
        
        MenuItem addItem = null;
		
		if (_selection != null) {
			if (_selection.isComposite()) {                
				addItem = new MenuItem(_popup, SWT.CASCADE);
				addItem.setText(ResourceHelper.getString("menu.add"));
                addItem.setImage(_treeView.getParent().getIconSet().getIcon("add", true));
                if (((Project)_selection).isClosed()) {
                    addItem.setEnabled(false);
                }
				_treeView.getParent().getMenuFactory().createAddMenu(addItem);
			}
            

			if (_selection.isDeletable()) {
                if (addItem != null) {
                    new MenuItem(_popup, SWT.SEPARATOR);
                }
                MenuItem cutItem = new MenuItem(_popup, SWT.CASCADE);
                cutItem.setText(ResourceHelper.getString("menu.cut"));
                cutItem.setImage(getIcon("cut"));
                cutItem.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        TimeTracker.getInstance().cutSelection();
                    }
                });
                cutItem.setEnabled(TimeTracker.getInstance()
                    .isLocalClipboardEmpty()
                    && _selection.isDeletable());
                
                MenuItem pasteItem = new MenuItem(_popup, SWT.CASCADE);
                pasteItem.setText(ResourceHelper.getString("menu.paste"));
                pasteItem.setImage(getIcon("paste"));
                pasteItem.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        TimeTracker.getInstance().pasteFromLocalClipboard();
                    }
                });
                pasteItem.setEnabled(!TimeTracker.getInstance()
                    .isLocalClipboardEmpty()
                    && _selection.isComposite());
                
                _deleteItem = new MenuItem(_popup, SWT.CASCADE);
				_deleteItem.setText(ResourceHelper.getString("menu.delete"));
                _deleteItem.setImage(getIcon("delete"));
				_deleteItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						TimeTracker.getInstance().removeSelection();
					}
				});
			}
			            

			if (_selection instanceof Task) {
                
                switch(_selection.getItemType()) {
                case TASK:
                    new MenuItem(_popup, SWT.SEPARATOR);
                    createTaskItems();
                    break;
                case IDLE_TASK:
                    createIdleTaskItems();
                    break;
                case ACTIVITY:
                    new MenuItem(_popup, SWT.SEPARATOR);
                    createTaskItems();
                    break;
                }
                
			}
            else if (_selection instanceof Project) {                
                createProjectLifecycleItems((Project)_selection);
            }
            
            new MenuItem(_popup, SWT.SEPARATOR);
            
			this._treeView.getParent().getMenuFactory().createPropertyItem(_popup);
		}
	}
    
    
    /**
     * Create menu items for a regular task.
     */
    private void createTaskItems() {
        Task task = (Task) _selection;
        boolean isActivity = task.getItemType().equals(ProjectTreeItem.ItemType.ACTIVITY);

        Menu markWithMenu = new Menu(_popup);
        _markWithItem = new MenuItem(_popup, SWT.CASCADE);
        _markWithItem.setMenu(markWithMenu);
        _markWithItem.setText(ResourceHelper.getString("menu.markWith"));

        createFlagItem(markWithMenu, TaskStatus.FlagColor.RED);
        createFlagItem(markWithMenu, TaskStatus.FlagColor.ORANGE);
        createFlagItem(markWithMenu, TaskStatus.FlagColor.BLUE);
        createFlagItem(markWithMenu, TaskStatus.FlagColor.GREEN);
        createFlagItem(markWithMenu, TaskStatus.FlagColor.MAGENTA);

        new MenuItem(markWithMenu, SWT.SEPARATOR);

        createFlagItem(markWithMenu, null);

        if (!isActivity) {
            createWaitMenu();
        }

        new MenuItem(_popup, SWT.SEPARATOR);

        if (!isActivity) {
            _doneItem = new MenuItem(_popup, SWT.CASCADE);
            _doneItem.setText(ResourceHelper.getString("menu.status.done"));
            _doneItem.setImage(getIcon("finished"));
            _doneItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Task) {
                        changeStatus(TaskStatus.FINISHED);
                    }
                }
            });
        }

        _cancelItem = new MenuItem(_popup, SWT.CASCADE);
        _cancelItem.setText(ResourceHelper.getString("menu.status.cancel"));
        _cancelItem.setImage(getIcon("cancel"));
        _cancelItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                if (_selection != null && _selection instanceof Task) {
                    changeStatus(TaskStatus.CANCELLED);
                }
            }
        });

        _reopenItem = new MenuItem(_popup, SWT.CASCADE);
        _reopenItem.setText(ResourceHelper.getString("menu.status.reopen"));
        _reopenItem.setImage(getIcon("reopen"));
        _reopenItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                if (_selection != null && _selection instanceof Task) {
                    changeStatus(TaskStatus.IN_PROGRESS);
                }
            }
        });

        if (task.isClosed()) {
            if (!isActivity) {
                _doneItem.setEnabled(false);
                _waitItem.setEnabled(false);
            }
            _cancelItem.setEnabled(false);
            _markWithItem.setEnabled(false);
            _reopenItem.setEnabled(!task.hasClosedParent());
        }
        else {
            if (!isActivity) {
                _doneItem.setEnabled(true);
                _waitItem.setEnabled(true);
            }
            _cancelItem.setEnabled(true);
            _markWithItem.setEnabled(true);
            _reopenItem.setEnabled(false);
        }

        if (task.isDeletable()) {
            _deleteItem.setEnabled(true);
        }
        else {
            _deleteItem.setEnabled(false);
        }
    }
    
    
    /**
     * Create menu items for IDLE task.
     */
    private void createIdleTaskItems() {
        _enableIdle = new MenuItem(_popup, SWT.CASCADE);
        _enableIdle.setText(ResourceHelper.getString("menu.idle.enable"));
        _enableIdle.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeTracker.getInstance().getWorkspace().enableIdle();
                _enableIdle.setEnabled(false);
                _disableIdle.setEnabled(true);
                _treeView.updateTreeItemStyle(_selection);
            }
        });

        _disableIdle = new MenuItem(_popup, SWT.CASCADE);
        _disableIdle.setText(ResourceHelper.getString("menu.idle.disable"));
        _disableIdle.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeTracker.getInstance().getWorkspace().disableIdle();
                _enableIdle.setEnabled(true);
                _disableIdle.setEnabled(false);
                _treeView.updateTreeItemStyle(_selection);
            }
        });

        if (((IdleTask) _selection).isEnabled()) {
            _enableIdle.setEnabled(false);
            _disableIdle.setEnabled(true);
        }
        else {
            _enableIdle.setEnabled(true);
            _disableIdle.setEnabled(false);
        }
    }
    
    
    private void createProjectLifecycleItems(Project project) {
        if (project instanceof Workspace) {
            return;
        }
        new MenuItem(_popup, SWT.SEPARATOR);
        if (project.getCloseDateTime() == null) {
            MenuItem closeProject = new MenuItem(_popup, SWT.CASCADE);
            closeProject.setText("Close"); // TODO: Localize
            closeProject.setImage(getIcon("close-project"));
            closeProject.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Project) {
                        Project p = (Project) _selection;
                        if (p.hasOpenItems()) {
                            TimeTracker
                                    .getInstance()
                                    .getUIManager()
                                    .showError(
                                            "Can't close the project: there are open items under it."); // TODO: Localize
                        } else {
                            p.setCloseDateTime(Calendar.getInstance().getTime());
                            notifyProjectStateChange(p);
                        }
                    }
                }
            });
        }
        else {
            MenuItem reopenProject = new MenuItem(_popup, SWT.CASCADE);
            reopenProject.setText(ResourceHelper.getString("menu.status.reopen"));
            reopenProject.setImage(getIcon("reopen"));
            if (project.hasClosedParent()) {
                reopenProject.setEnabled(false);
            }
            reopenProject.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Project) {
                        Project p = (Project) _selection;
                        p.setCloseDateTime(null);
                        notifyProjectStateChange(p);
                    }
                }
            });
        }
    }
    
    
    /*
     * Send notification on project state change and updated UI elements. 
     */
    private void notifyProjectStateChange(Project p) {
        TimeTracker.getInstance().getWorkspace().fireWorkspaceChanged(
            new WorkspaceEvent(WorkspaceEvent.WORKSPACE_PROJECT_CHANGED, p));
        _treeView.updateTreeItemStyle(p);
        _treeView.getParent().getStatusLine().setSelection(p);
        updateOnSelection(p);
    }
    
	
    
	public void updateOnSelection(Object selection) {
		_selection = (ProjectTreeItem)selection; // Change updateOn..()
                                                    // signature instead of
                                                    // typecast
		if (_popup != null) {
			_popup.dispose();
		}
		setup(_treeView.getTree());
	}
    
    private void changeStatus(int statusId) {
        changeStatus(new TaskStatus(statusId), null);
    }

    private void changeStatus(TaskStatus newStatus) {
        changeStatus(newStatus, null);
    }
    
    private void changeStatus(TaskStatus status, WaitReason waitReason) {
        Task task = (Task) _selection;
        boolean isActivity = task.getItemType().equals(ProjectTreeItem.ItemType.ACTIVITY);
        task.setStatus(status.getId());
        if (status.getId() == TaskStatus.FLAGGED) {
            task.setFlagColor(status.getFlagColor());
        }
        task.setWaitReason(waitReason);
        if (task.isClosed()) {
            if (!isActivity) {
                _doneItem.setEnabled(false);
                _waitItem.setEnabled(false);
            }
            _cancelItem.setEnabled(false);
            _markWithItem.setEnabled(false);
            task.setCloseDateTime(Calendar.getInstance().getTime());
        }
        //setFlagItemText(task);
        TimeTracker
                .getInstance()
                .getWorkspace()
                .fireWorkspaceChanged(
                        new WorkspaceEvent(
                                WorkspaceEvent.TASK_STATUS_CHANGED, task));
        _treeView.updateTreeItemStyle(_selection);
        //_treeView.getParent().getMainMenu().updateFlagged();
        _treeView.getParent().getStatusLine().setSelection(task);
    }

    /*
    private void setFlagItemText(Task task) {
        if (task.isFlagged()) {
            _flagItem.setText(ResourceHelper.getString("menu.status.unsetFlag"));
            _flagItem.setImage(null);
        } else {
            _flagItem
                    .setText(ResourceHelper.getString("menu.status.setFlag"));
            _flagItem.setImage(getIcon("flagged"));
        }
    }
    */
    
    private Image getIcon(String iconTag) {
        return _treeView.getParent().getIconSet().getIcon(iconTag, true);
    }
    
    
    private void createWaitMenu() {
        _waitItem = new MenuItem(_popup, SWT.CASCADE);
        _waitItem.setText(ResourceHelper.getString("menu.waitingFor"));
        _waitItem.setImage(getIcon("waiting"));

        Menu _waitMenu = new Menu(_waitItem);
        _waitItem.setMenu(_waitMenu);
        WaitReason[] reasons = WaitReason.getAllReasons();
        for (WaitReason reason : reasons) {
            MenuItem reasonItem = new MenuItem(_waitMenu, SWT.CASCADE);
            reasonItem.setText(reason.getText());
            reasonItem.setData(reason);
            reasonItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Task) {
                        Object data = ((MenuItem) evt.getSource()).getData();
                        if (data != null) {
                            setWaiting((WaitReason) data);
                        }
                    }
                }
            });
        }
        if (reasons != null && reasons.length > 0) {
            new MenuItem(_waitMenu, SWT.SEPARATOR);
        }
        MenuItem otherItem = new MenuItem(_waitMenu, SWT.CASCADE);
        otherItem.setText(ResourceHelper.getString("menu.waiting.other"));
        otherItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                ReasonDialog reasonDialog = new ReasonDialog(SWTProjectTreePopup.this);
                reasonDialog.open();
            }
        });
    }
    
    
    public Shell getShell() {
        return _treeView.getParent().getShell();
    }
    
    public void setWaiting(WaitReason reason) {
        changeStatus(new TaskStatus(TaskStatus.WAITING), reason);
    }
    
    
    /**
     * Updates the menu when selected task status has changed.
     * @param we
     */
    public void workspaceChanged(WorkspaceEvent we) {
        if (we.getId() == WorkspaceEvent.TASK_STATUS_CHANGED) {
            Object source = we.getSource();
            if (source != null && source instanceof Task && _selection == source) {
                setup(this._treeView.getTree()); 
            }
        }
    }

    private MenuItem createFlagItem(Menu markWithMenu, final TaskStatus.FlagColor flagColor) {
        MenuItem flagItem = new MenuItem(markWithMenu, SWT.CASCADE);
        if (flagColor != null) {
            final String tag = flagColor.toString().toLowerCase() + "Flag";
            flagItem.setImage(getIcon(tag));
            flagItem.setText(ResourceHelper.getString("menu.markWith." + tag));
            flagItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Task) {
                        TaskStatus newStatus = new TaskStatus(TaskStatus.FLAGGED);
                        newStatus.setFlagColor(flagColor);
                        changeStatus(newStatus);
                    }
                }
            });
        } else {
            flagItem.setText(ResourceHelper.getString("menu.markWith.clearFlag"));
            flagItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    if (_selection != null && _selection instanceof Task) {
                        Task task = (Task) _selection;
                        if (task.isFlagged()) {
                            changeStatus(TaskStatus.IN_PROGRESS);
                        }
                    }
                }
            });
        }
        return flagItem;
    }

    public Menu getMenu() {
        return _popup;
    }
}

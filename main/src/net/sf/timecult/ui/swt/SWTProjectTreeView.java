/*
 * Copyright (c) Rustam Vishnyakov, 2005-2019 (dyadix@gmail.com)
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
 * $Id: SWTProjectTreeView.java,v 1.31 2010/12/25 11:39:12 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.TimeTracker;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.conf.AppPreferencesListener;
import net.sf.timecult.model.*;
import net.sf.timecult.model.Project.SortCriteria;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import java.util.Date;
import java.util.HashMap;

public class SWTProjectTreeView implements AppPreferencesListener {

    private SortCriteria sortCriteria = SortCriteria.BY_NAME;

    private Tree _tree;
    private SWTMainWindow _mainWindow;
    private SWTProjectTreePopup _popup;
    private HashMap<Object,TreeItem> _itemHash = new HashMap<Object,TreeItem>();
    private final ItemStyleFactory _styleFactory;

	public SWTProjectTreeView(SWTMainWindow mainWindow) {
        _mainWindow = mainWindow;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _tree = new Tree(_mainWindow.getProjectTreeContainer(), SWT.SINGLE
            | SWT.BORDER | SWT.VIRTUAL);
        _tree.addSelectionListener(new ProjectTreeSelectionListener());
        _tree.addTreeListener(new ProjectTreeListener());
        _tree.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                openLink();
            }
        });
        _tree.setLayoutData(gridData);
        _styleFactory = new ItemStyleFactory(mainWindow, _tree.getBackground());
        _popup = new SWTProjectTreePopup(this);
        buildTree(null, TimeTracker.getInstance().getWorkspace());


        AppPreferences.getInstance().addListener(this);

        _tree.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                if (e.stateMask == SWT.ALT) {
                    switch (e.keyCode) {
                        case SWT.CR:
                            openItemProperties();
                            break;
                        case 't':
                        case 'T':
                            addTask();
                            break;
                    }
                }
                else if (e.stateMask == SWT.NONE) {
                    switch (e.keyCode) {
                        case SWT.CR:
                            TreeItem[] selection = _tree.getSelection();
                            if (selection.length > 0) {
                                selection[0].setExpanded(!selection[0].getExpanded());
                            }
                            break;
                        case '0':
                            setFlag(null);
                            break;
                        case '1':
                            setFlag(TaskStatus.FlagColor.RED);
                            break;
                        case '2':
                            setFlag(TaskStatus.FlagColor.ORANGE);
                            break;
                        case '3':
                            setFlag(TaskStatus.FlagColor.BLUE);
                            break;
                        case '4':
                            setFlag(TaskStatus.FlagColor.GREEN);
                            break;
                        case '5':
                            setFlag(TaskStatus.FlagColor.MAGENTA);
                            break;
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
                // Do nothing
            }
        });
    }

    private void setFlag(TaskStatus.FlagColor flagColor) {
        if (_tree.getSelectionCount() != 0) {
            Object currSelection = _tree.getSelection()[0].getData();
            if (currSelection instanceof Task) {
                Task currTask = (Task)currSelection;
                if (flagColor == null) {
                    currTask.setStatus(TaskStatus.IN_PROGRESS);
                }
                else {
                    currTask.setStatus(TaskStatus.FLAGGED);
                    currTask.setFlagColor(flagColor);
                }
                update();
            }
        }
    }

	public void update() {
		Object currSelection;
		if (_tree.getSelectionCount() != 0) {
			currSelection = _tree.getSelection()[0].getData();
		}
        else {
            currSelection = TimeTracker.getInstance().getWorkspace();
        }
        _tree.removeAll();
        _tree.setRedraw(false);
        _itemHash.clear();
        buildTree(null, TimeTracker.getInstance().getWorkspace());
		if (currSelection != null) {
			TreeItem item = this.findByData(_tree.getTopItem(), currSelection);
			if (item != null) {
				_tree.setSelection(item);
                _tree.showSelection();
			}
		}
        _popup.updateOnSelection(currSelection);
        _tree.setRedraw(true);
	}




	public void updateOnRemove(Object object) {
		TreeItem item = findByData(_tree.getTopItem(), object);
		if (item != null) {
			item.dispose();
		}
	}

	public void updateTreeItemStyle(Object object) {
		TreeItem item = findByData(_tree.getTopItem(), object);
		if (item != null) {
			setTextAndAttributes(item, (ProjectTreeItem)object);
		}
	}

    private void setTextAndAttributes(TreeItem item, ProjectTreeItem modelItem) {
	    item.setText(modelItem.toString());
        if (modelItem.getHyperlink() != null) {
            item.setText(item.getText() + " \u00b7\u00b7\u00b7");
        }
	    ItemStyle style = _styleFactory.getItemStyle(modelItem);
	    if (style.getFont() != null) {
            item.setFont(style.getFont());
        }
	    if (style.getBackground() != null) {
	        item.setBackground(style.getBackground());
        }
	    if (style.getForeground() != null) {
	        item.setForeground(style.getForeground());
        }
	    item.setImage(style.getImage());
    }

    private void buildTree(TreeItem parent, Project project) {
        if (!isVisible(project)) {
            return;
        }
		TreeItem projItem;
		if (parent == null) {
			projItem = new TreeItem(_tree, SWT.NONE);
		} else {
			projItem = new TreeItem(parent, SWT.NONE);
		}
		projItem.setData(project);
		setTextAndAttributes(projItem, project);
        _itemHash.put(project, projItem);
		Project[] subprojects = project.getSubprojects(this.sortCriteria);
        for (Project subproject : subprojects) {
            buildTree(projItem, subproject);
        }
		Task[] tasks = project.getTasks(this.sortCriteria);
        for (Task task : tasks) {
            if (isVisible(task)) {
                TreeItem treeItem = new TreeItem(projItem, SWT.NONE);
                treeItem.setData(task);
                setTextAndAttributes(treeItem, task);
                _itemHash.put(task, treeItem);
            }
        }
        if(project instanceof Workspace) {
            projItem.setExpanded(true);
        }
        else {
            projItem.setExpanded(project.isExpanded());
        }
	}

	public Tree getTree() {
		return _tree;
	}

	public SWTMainWindow getParent() {
		return _mainWindow;
	}

	private class ProjectTreeSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent evt) {
			TreeItem[] selItems = _tree.getSelection();
            if (selItems == null || selItems.length == 0) return;
			Object selection = selItems[0].getData();
			TimeTracker.getInstance().selectObject(selection);
		}
	}

	public void setCurrentSelection(Object object) {
        if (object == null) {
            this._tree.setSelection((TreeItem) null);
            return;
        }
        TreeItem matchingItem = findByData(_tree.getTopItem(), object);
        if (matchingItem != null) {
            _tree.setSelection(matchingItem);
            _tree.setFocus();
            TimeTracker.getInstance().selectObject(object);
        } else {
            TimeTracker.getInstance().selectObject(TimeTracker.getInstance().getWorkspace());
        }
    }

	private TreeItem findByData(TreeItem item, Object object) {
		return _itemHash.get(object);
	}

	public SWTProjectTreePopup getPopupMenu() {
		return _popup;
	}


    private class ProjectTreeListener implements TreeListener {

        public void treeCollapsed(TreeEvent evt) {
            TreeItem item = (TreeItem)evt.item;
            Object data = item.getData();
            if (data instanceof ProjectTreeItem) {
                ((ProjectTreeItem)data).setExpanded(false);
            }
        }

        public void treeExpanded(TreeEvent evt) {
            TreeItem item = (TreeItem)evt.item;
            Object data = item.getData();
            if (data instanceof ProjectTreeItem) {
                ((ProjectTreeItem)data).setExpanded(true);
            }
        }
    }
    
    
    /*
    public void workspaceChanged(WorkspaceEvent we) {
        if (we.getId() == WorkspaceEvent.FILTER_CHANGED) {
            this.dispose();
        }        
    } 
    */

    public SortCriteria getSortCriteria() {
        return this.sortCriteria;
    }

    public boolean isVisible(ProjectTreeItem treeItem) {
        if (!TimeTracker.getInstance().getAppPreferences().isHideClosed()) {
            return true;
        }
        if (treeItem.getCloseDateTime() == null) {
            return true;
        }
        TimeRecordFilter filter = TimeTracker.getInstance().getFilter();
        if (filter != null) {
            Date firstDate = filter.getSinceDate();
            Date closeDate = treeItem.getCloseDateTime();
            return !closeDate.before(firstDate);
        }
        return true;
    }


    public void preferenceChanged(String prefName) {
        if (AppPreferences.HIDE_CLOSED.equals(prefName)) {
            update();
        }
    }


    /*
     * Open a link stored in the currently selected element
     *
     */
    private void openLink() {
        Object currSelection;
        if (_tree.getSelectionCount() == 0) {
            return;
        }
        currSelection = _tree.getSelection()[0].getData();
        if (currSelection instanceof ProjectTreeItem) {
            String url = ((ProjectTreeItem)currSelection).getHyperlink();
            if (url != null) {
                Program.launch(url);
            }
        }
    }


    private void openItemProperties() {
        TreeItem[] selection = _tree.getSelection();
        if (selection.length > 0) {
            Object data = selection[0].getData();
            if (data instanceof ProjectTreeItem) {
                SWTDialog propertyDialog = DialogFactory.createPropertyDialog(
                    _mainWindow,
                    (ProjectTreeItem) data);
                if (propertyDialog != null) {
                    propertyDialog.open();
                }
            }
        }
    }

    private void addTask() {
        Object currSelection = _tree.getSelection()[0].getData();
        if (currSelection instanceof ProjectTreeItem) {
            ProjectTreeItem item = (ProjectTreeItem) currSelection;
            if (!(currSelection instanceof Project)) {
                Project project = item.getParent();
                setCurrentSelection(project);
            }
            SWTTaskEditDialog taskDialog = new SWTTaskEditDialog(getParent());
            taskDialog.open();
        }
    }


}

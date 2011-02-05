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
 * $Id: SWTProjectTreeView.java,v 1.31 2010/12/25 11:39:12 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import java.util.Date;
import java.util.HashMap;

//import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.conf.AppPreferencesListener;
import net.sf.timecult.model.Activity;
import net.sf.timecult.model.IdleTask;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.Project.SortCriteria;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
//import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class SWTProjectTreeView implements AppPreferencesListener {
    
    private Color normalTextColor;
    private Color disabledTextColor;
    private Color flaggedTextColor;
    private HashMap<Integer,Font> fontMap = new HashMap<Integer,Font>();
    private SortCriteria sortCriteria = SortCriteria.BY_NAME;

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
        // _tree.setHeaderVisible(true);
        // TreeColumn projectTaskColumn = new TreeColumn(_tree, SWT.NONE);
        // projectTaskColumn.setText(ResourceHelper.getString("table.object"));
        // projectTaskColumn.setWidth(400);
        _popup = new SWTProjectTreePopup(this);
        buildTree(null, TimeTracker.getInstance().getWorkspace());
        IconSet iconSet = _mainWindow.getIconSet();
        _workspaceImage = iconSet.getIcon("workspace", true);
        _projectImage = iconSet.getIcon("project", true);
        _newTaskImage = iconSet.getIcon("newTask", true);
        _inProgressImage = iconSet.getIcon("inProgress", true);
        _finishedImage = iconSet.getIcon("finished", true);
        _cancelledImage = iconSet.getIcon("cancelled", true);
        _flaggedImage = iconSet.getIcon("flagged", true);
        _idleImage = iconSet.getIcon("idle", true);
        _waitingImage = iconSet.getIcon("waiting", true);
        _activityImage = iconSet.getIcon("activity", true);
        _closedProjectImage = iconSet.getIcon("project-closed", true);
        
        this.disabledTextColor = new Color(_mainWindow.getShell().getDisplay(),
            128, 128, 192);
        this.normalTextColor = new Color(_mainWindow.getShell().getDisplay(),
            0, 0, 0);
        this.flaggedTextColor = new Color(_mainWindow.getShell().getDisplay(),
            255, 0, 0);
        
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
            }


            public void keyReleased(KeyEvent e) {
                // Do nothing
            }
        });
    }
	
	public void update() {
		Object currSelection = null;
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
			setAttributes(item, (ProjectTreeItem)object);
		}
	}
	
	private void buildTree(TreeItem parent, Project project) {
        if (!isVisible(project)) {
            return;
        }
		TreeItem projItem = null;
		if (parent == null) {
			projItem = new TreeItem(_tree, SWT.NONE);
		} else {			
			projItem = new TreeItem(parent, SWT.NONE);
		}
		projItem.setData(project);
		setAttributes(projItem, project);
        _itemHash.put(project, projItem);
		Project[] subprojects = project.getSubprojects(this.sortCriteria);
		for (int i = 0; i < subprojects.length; i++) {
			buildTree(projItem, subprojects[i]);
		}
		Task[] tasks = project.getTasks(this.sortCriteria);
		for (int i = 0; i < tasks.length; i++) {
            if (isVisible(tasks[i])) {
                TreeItem treeItem = new TreeItem(projItem, SWT.NONE);
                treeItem.setData(tasks[i]);
                setAttributes(treeItem, tasks[i]);
                _itemHash.put(tasks[i], treeItem);
            }
		}
        if(project instanceof Workspace) {
            projItem.setExpanded(true);
        }
        else {
            projItem.setExpanded(project.isExpanded());
        }
	}
	
	private void setAttributes(TreeItem item, ProjectTreeItem modelItem) {
        item.setText(modelItem.toString());
        item.setForeground(normalTextColor);
        switch (modelItem.getItemType()) {
        case WORKSPACE:
            item.setImage(_workspaceImage);
            break;
        case PROJECT:
            if (modelItem.getCloseDateTime() == null) {
                item.setImage(_projectImage);
            }
            else {
                item.setImage(_closedProjectImage);
            }
            break;
        case TASK:
            Task task = (Task) modelItem;
            switch (task.getStatus()) {
            case TaskStatus.NOT_STARTED:
                item.setImage(_newTaskImage);
                setFontStyle(item, SWT.BOLD);
                break;
            case TaskStatus.IN_PROGRESS:
                item.setImage(_inProgressImage);
                setFontStyle(item, SWT.NORMAL);
                break;
            case TaskStatus.FINISHED:
                item.setImage(_finishedImage);
                setFontStyle(item, SWT.NORMAL);
                item.setForeground(this.disabledTextColor);
                break;
            case TaskStatus.CANCELLED:
                item.setImage(_cancelledImage);
                setFontStyle(item, SWT.NORMAL);
                item.setForeground(this.disabledTextColor);
                break;
            case TaskStatus.FLAGGED:
                setFontStyle(item, SWT.NORMAL);
                item.setImage(_flaggedImage);
                item.setForeground(this.flaggedTextColor);
                break;
            case TaskStatus.WAITING:
                setFontStyle(item, SWT.NORMAL);
                item.setImage(_waitingImage);
                break;
            }
            break;
        case IDLE_TASK:
            item.setImage(_idleImage);
            IdleTask idle = (IdleTask) modelItem;
            if (!idle.isEnabled()) {
                item.setForeground(this.disabledTextColor);
            }
            else {
                item.setForeground(this.normalTextColor);
            }
            break;
        case ACTIVITY:
            Activity activity = (Activity) modelItem;
            switch (activity.getStatus()) {
            case TaskStatus.IN_PROGRESS:
                item.setImage(_activityImage);
                break;
            case TaskStatus.CANCELLED:
                item.setImage(_cancelledImage);
                item.setForeground(this.disabledTextColor);
                break;
            case TaskStatus.FLAGGED:
                item.setImage(_flaggedImage);
                item.setForeground(this.flaggedTextColor);
                break;
            }            
            break;
        }
        if (modelItem.getHyperlink() != null) {
            item.setText(item.getText() + " \u00b7\u00b7\u00b7");
        }
    }
	
	
	private void setFontStyle(TreeItem item, int style) {
        Integer fontKey = new Integer(style);
        Font fn = null;
        if (!this.fontMap.containsKey(fontKey)) {
            FontData[] f = item.getFont().getFontData();
            for (int i = 0; i < f.length; i++) {
                f[i].setStyle(style);
            }
            fn = new Font(_mainWindow.getShell().getDisplay(), f);
            this.fontMap.put(fontKey, fn);
        }
        else {
            fn = fontMap.get(fontKey);
        }
        item.setFont(fn);
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
            this.update();
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
            if (closeDate.before(firstDate)) {
                return false;
            }
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
        Object currSelection = null;
        if (_tree.getSelectionCount() == 0) {
            return;
        }
        currSelection = _tree.getSelection()[0].getData();
        if (currSelection != null && currSelection instanceof ProjectTreeItem) {
            String url = ((ProjectTreeItem)currSelection).getHyperlink();
            if (url != null) {
                Program.launch(url);
            }
        }
    }
    

    private void openItemProperties() {
        TreeItem selection[] = _tree.getSelection();
        if (selection.length > 0) {
            Object data = selection[0].getData();
            if (data != null && data instanceof ProjectTreeItem) {
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
        if (currSelection != null && currSelection instanceof ProjectTreeItem) {
            ProjectTreeItem item = (ProjectTreeItem) currSelection;
            if (!(currSelection instanceof Project)) {
                Project project = item.getParent();
                setCurrentSelection(project);
            }
            SWTTaskEditDialog taskDialog = new SWTTaskEditDialog(getParent());
            taskDialog.open();
        }
    }
    
	
	private Tree _tree = null;
	private SWTMainWindow _mainWindow = null;
	private SWTProjectTreePopup _popup;
    private HashMap<Object,TreeItem> _itemHash = new HashMap<Object,TreeItem>();
    private Image _workspaceImage;
    private Image _projectImage;
    private Image _newTaskImage;
    private Image _inProgressImage;
    private Image _finishedImage;
    private Image _cancelledImage;
    private Image _flaggedImage;
    private Image _idleImage;
    private Image _waitingImage;
    private Image _activityImage;
    private Image _closedProjectImage;



}

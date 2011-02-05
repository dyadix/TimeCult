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
 * $Id: MenuFactory.java,v 1.6 2010/04/22 18:02:33 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.ProjectTreeItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Creates common menu elements.
 */
public class MenuFactory {

    private SWTMainWindow mainWindow;
    private Image         projectImage;
    private Image         taskImage;
    private Image         activityImage;


    /**
     * Constructor.
     * 
     * @param mainWindow
     *            Main window owning the menu factory.
     */
    public MenuFactory(SWTMainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.activityImage = this.mainWindow.getIconSet().getIcon("activity", true);
        this.taskImage = this.mainWindow.getIconSet().getIcon("inProgress", true);
        this.projectImage = this.mainWindow.getIconSet().getIcon("project", true);
    }


    /**
     * Creates add menu for the specified "Add" item.
     * 
     * @param addItem
     *            The "Add" item to create the menu for.
     * @return The created "Add" menu with "Project"/"Task" etc. items.
     */
    public Menu createAddMenu(MenuItem addItem) {
        Menu addMenu = new Menu(addItem);
        addItem.setMenu(addMenu);
        MenuItem addProjectItem = new MenuItem(addMenu, SWT.CASCADE);
        addProjectItem.setText(ResourceHelper.getString("menu.add.project"));
        addProjectItem.setImage(this.projectImage);
        addProjectItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTProjectEditDialog projDialog = new SWTProjectEditDialog(
                    mainWindow);
                projDialog.open();
            }
        });

        MenuItem addTaskItem = new MenuItem(addMenu, SWT.CASCADE);
        addTaskItem.setText(ResourceHelper.getString("menu.add.task"));
        addTaskItem.setImage(this.taskImage);
        addTaskItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTaskEditDialog taskDialog = new SWTTaskEditDialog(mainWindow);
                taskDialog.open();
            }
        });

        MenuItem addActivityItem = new MenuItem(addMenu, SWT.CASCADE);
        addActivityItem.setText(ResourceHelper.getString("menu.add.activity"));
        addActivityItem.setImage(this.activityImage);
        addActivityItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                ActivityEditDialog actDialog = new ActivityEditDialog(
                    mainWindow);
                actDialog.open();
            }
        });
        return addMenu;
    }
    
    
    /**
     * Creates a property menu item.
     * 
     * @param parent  The parent menu to create the item for.
     * @return The newly created property menu item.
     */
    public MenuItem createPropertyItem(Menu parent) {
        MenuItem propsItem = new MenuItem(parent, SWT.CASCADE);
        propsItem.setText(ResourceHelper.getString("menu.properties") + "\tAlt+Enter");
        propsItem.setImage(this.mainWindow.getIconSet().getIcon("edit", true));
        propsItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                ProjectTreeItem selection = TimeTracker.getInstance().getWorkspace().getSelection();
                if (selection != null) {
                    SWTDialog propertyDialog = DialogFactory.createPropertyDialog(mainWindow, selection);
                    if (propertyDialog != null) {
                        propertyDialog.open();
                    }
                }
            }
        }); 
        return propsItem;
    }
}

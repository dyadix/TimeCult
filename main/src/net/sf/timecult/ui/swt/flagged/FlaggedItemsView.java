/*
 * Copyright (c) Rustam Vishnyakov, 2007-2008 (dyadix@gmail.com)
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
 * $Id: FlaggedItemsView.java,v 1.7 2010/12/14 12:14:22 dyadix Exp $
 */

package net.sf.timecult.ui.swt.flagged;

import java.util.Collection;
import java.util.TreeMap;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;
import net.sf.timecult.ui.swt.MissingSelectionObjectException;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FlaggedItemsView extends SWTDialog {
    
    private static final String[] titleKeys = { "table.task" };
    private static final int[] length = { 500 };
    private static final int[] align = { SWT.LEFT };
    private SWTMainWindow _mainWindow;
    private Table _flaggedTable;    
    
    public FlaggedItemsView(SWTMainWindow mainWindow) {
        super(mainWindow.getShell());
        _mainWindow = mainWindow;
    }
    
    
    @Override
    protected Composite createContentPanel(Shell shell) {
        GridLayout contentLayout = new GridLayout();
        contentLayout.numColumns = 1;
        contentLayout.makeColumnsEqualWidth = true;        
        Composite contentPanel = new Composite(shell, SWT.None);
        contentPanel.setLayout(contentLayout);
        
        GridData tableLayoutData = new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_FILL);
        tableLayoutData.heightHint = 200;
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        grid.makeColumnsEqualWidth = true;
        _flaggedTable = new Table(contentPanel, SWT.FULL_SELECTION | SWT.BORDER);
        _flaggedTable.setLayoutData(tableLayoutData);
        _flaggedTable.setLinesVisible(true);
        _flaggedTable.setHeaderVisible(true);
        
        _flaggedTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.keyCode == SWT.CR) {
                    switchToSelection();
                    FlaggedItemsView.this.getShell().setVisible(false);
                }
            }
        });
        _flaggedTable.setFocus();
        
        for (int i = 0; i < titleKeys.length; i++) {
            TableColumn column = new TableColumn(_flaggedTable, SWT.NONE);
            column.setAlignment(align[i]);
            column.setText(ResourceHelper.getString(titleKeys[i]));
            column.setWidth(length[i]);
        }        
        addTableData();
        
        return contentPanel;
    }
    
    

    @Override
    protected void createButtons(Composite buttonPanel) {
        GridData buttonData = new GridData();
        buttonData.widthHint = 60;

        Button selectButton = new Button(buttonPanel, SWT.FLAT);
        selectButton.setLayoutData(buttonData);

        selectButton.setText(ResourceHelper.getString("button.select"));        
        selectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                switchToSelection();                
                FlaggedItemsView.this.getShell().setVisible(false);
            }
        });
        
        
        Button closeButton = new Button(buttonPanel, SWT.FLAT);
        closeButton.setLayoutData(buttonData);

        closeButton.setText(ResourceHelper.getString("button.close"));
        closeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                FlaggedItemsView.this.getShell().setVisible(false);
            }
        });
    }    
    
    
    private void addTableData() {
        Task flaggedTasks[] = TimeTracker.getInstance().getWorkspace()
                .getTasksByStatus(TaskStatus.FLAGGED);
        TreeMap<String,Task> sortedItems = new TreeMap<String,Task>();        
        for (int i = 0; i < flaggedTasks.length; i++) {
            sortedItems.put(flaggedTasks[i].getName(), flaggedTasks[i]);
        }
        Collection<Task> tasks = sortedItems.values(); 
        for (Task task : tasks) {
            TableItem taskItem = new TableItem(_flaggedTable, SWT.NONE);
            taskItem.setData(task);
            taskItem.setText(task.toString());
        }
    }

    
    private void switchToSelection() {
        TableItem[] items = _flaggedTable.getSelection();
        if (items.length > 0) {
            Task selectedTask = (Task) items[0].getData();
            _mainWindow.getProjectTreeView().setCurrentSelection(selectedTask);
        }
    }


    @Override
    protected String getTitle() {
        return ResourceHelper.getString("flagged.title");
    }


    @Override
    protected boolean handleOk() {
        return true;
    }
    
}

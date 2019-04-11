/*
 * Copyright (c) Rustam Vishnyakov, 2010 (dyadix@gmail.com)
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
 * $Id: FindDialog.java,v 1.6 2011/01/01 16:38:42 dyadix Exp $
 */

package net.sf.timecult.ui.swt.search;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;
import net.sf.timecult.ui.swt.SWTProjectTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 * @author Rustam Vishnyakov <dyadix@gmail.com>
 */
public class FindDialog extends SWTDialog {

    private Text textToFind;
    private final Workspace workspace;
    private final SWTMainWindow mainWindow;
    private SWTProjectTreeView projectTreeView;
    private Table taskListTable;

    public FindDialog(SWTMainWindow mainWindow, Workspace workspace) {
        super(mainWindow.getShell(), true, true, true);
        this.workspace = workspace;
        this.mainWindow = mainWindow;
        this.projectTreeView = mainWindow.getProjectTreeView();
    }

    private void setInitialSize(Shell parentShell, Shell thisShell) {
        Point parentSize = parentShell.getSize();
        float ratio = 0.7f;
        thisShell.setSize(Math.round(ratio * parentSize.x), Math.round(ratio * parentSize.y));
    }

    @Override
    protected void setup(Shell shell) {
        super.setup(shell);
        setInitialSize(mainWindow.getShell(), shell);
        SWTMainWindow.centerShellRelatively(getParent(), shell);
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    protected void createButtons(Composite buttonPanel) {
        GridData buttonLayout = new GridData(GridData.FILL);
        buttonLayout.widthHint = 80;
        Button findButton = new Button(buttonPanel, SWT.FLAT );
        findButton.setLayoutData(buttonLayout);
        findButton.setText(ResourceHelper.getString("button.find"));
        findButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                doFind();
            }
        });

        Button cancelButton = new Button(buttonPanel, SWT.FLAT);
        cancelButton.setLayoutData(buttonLayout);
        cancelButton.setText(ResourceHelper.getString("button.close"));
        cancelButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent evt) {
                close();
            }
        });
    }

    private void doFind() {
        taskListTable.setRedraw(false);
        taskListTable.removeAll();
        String currSearchKey = textToFind.getText();
        ProjectTreeItem[] allFoundItems = workspace.findItems(currSearchKey, false, projectTreeView.getSortCriteria());
        for (ProjectTreeItem item : allFoundItems) {
            if (this.projectTreeView.isVisible(item)) {
                TableItem taskItem = new TableItem(taskListTable, SWT.NONE);
                taskItem.setData(item);
                taskItem.setText(item.getName());
            }
        }
        taskListTable.setRedraw(true);
    }


    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite textPanel = new Composite(shell, SWT.BORDER);
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        textPanel.setLayout(grid);
        //
        // Text sample
        //
        Label textLabel = new Label(textPanel, SWT.None);
        textLabel.setText(ResourceHelper.getString("find.searchString"));
        this.textToFind = new Text(textPanel, SWT.BORDER);
        GridData searchTextLayoutData  = new GridData(GridData.FILL_HORIZONTAL);
        textToFind.setLayoutData(searchTextLayoutData);
        this.textToFind.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.CR:
                        doFind();
                        FindDialog.this.textToFind.setFocus();
                        break;
                    case SWT.ESC:
                        close();
                        break;
                }
            }
        });
        this.textToFind.setFocus();
        //
        // Results table
        //
        GridData tableLayoutData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL);
        tableLayoutData.grabExcessHorizontalSpace = true;
        taskListTable = new Table(textPanel, SWT.FULL_SELECTION | SWT.BORDER);
        taskListTable.setLayoutData(tableLayoutData);
        taskListTable.setLinesVisible(true);
        taskListTable.setHeaderVisible(false);
        taskListTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.keyCode == SWT.CR) {
                    switchToSelection();
                }
            }
        });
        return textPanel;
    }

    @Override
    protected boolean handleOk() {
        return true;
    }

    @Override
    protected String getTitle() {
        return ResourceHelper.getString("find.title");
    }

    private void switchToSelection() {
        TableItem[] items = taskListTable.getSelection();
        if (items.length > 0) {
            ProjectTreeItem selectedItem = (ProjectTreeItem) items[0].getData();
            this.mainWindow.getProjectTreeView().setCurrentSelection(selectedItem);
            close();
        }
    }
}

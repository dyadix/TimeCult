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
import net.sf.timecult.model.Workspace;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;
import net.sf.timecult.ui.swt.SWTProjectTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;


/**
 * @author Rustam Vishnyakov <dyadix@gmail.com>
 */
public class FindDialog extends SWTDialog {

    private Text textToFind;
    private ArrayList<ProjectTreeItem> foundItems;
    private final Workspace workspace;
    private int currItem = 0;
    private Label statusLabel;
    private final SWTMainWindow mainWindow;
    private Button findNextButton;
    private SWTProjectTreeView projectTreeView;
    private String currSearchKey;

    public FindDialog(SWTMainWindow mainWindow, Workspace workspace) {
        super(mainWindow.getShell());
        this.workspace = workspace;
        this.mainWindow = mainWindow;
        this.projectTreeView = mainWindow.getProjectTreeView();
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

        findNextButton = new Button(buttonPanel, SWT.FLAT );
        findNextButton.setLayoutData(buttonLayout);
        findNextButton.setText(ResourceHelper.getString("button.find.next"));
        findNextButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                switchToNext();
            }
        });
        findNextButton.setEnabled(false);

        Button cancelButton = new Button(buttonPanel, SWT.FLAT);
        cancelButton.setLayoutData(buttonLayout);
        cancelButton.setText(ResourceHelper.getString("button.close"));
        cancelButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent evt) {
                close();
            }
        });
    }

    private void selectItem(int itemNo) {
        statusLabel.setText((itemNo + 1) + " of " + foundItems.size());
        this.mainWindow.getProjectTreeView().setCurrentSelection(foundItems.get(itemNo));
    }

    private void doFind() {
        currSearchKey =  textToFind.getText();
        ProjectTreeItem[] allFoundItems = workspace.findItems(currSearchKey, false, projectTreeView.getSortCriteria());
        foundItems = new ArrayList<ProjectTreeItem>();
        for (ProjectTreeItem item : allFoundItems) {
            if (this.projectTreeView.isVisible(item)) {
                foundItems.add(item);
            }
        }
        currItem = 0;
        if (foundItems == null || foundItems.size() == 0) {
            statusLabel.setText("Nothing found.");
        } else {
            findNextButton.setEnabled(foundItems.size() > 1);
            selectItem(currItem);
        }
    }

    private boolean switchToNext() {
        if (currSearchKey != null && !currSearchKey.equals(textToFind.getText())) return false;
        if (foundItems != null && foundItems.size() > 0) {
            currItem++;
            if (currItem >= foundItems.size()) {
                return false;
            }
            selectItem(currItem);
            return true;
        }
        return false;
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite textPanel = new Composite(shell, SWT.BORDER);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        textPanel.setLayout(grid);
        //
        // Text sample
        //
        Label textLabel = new Label(textPanel, SWT.None);
        textLabel.setText(ResourceHelper.getString("find.searchString"));
        this.textToFind = createTextField(textPanel, "", 400);
        this.textToFind.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.CR:
                        if (!switchToNext()) {
                            doFind();
                        }
                        break;
                    case SWT.ESC:
                        close();
                        break;
                }
            }
        });
        this.textToFind.setFocus();
        //
        // Status string
        //
        Label itemsLabel = new Label(textPanel, SWT.None);
        itemsLabel.setText(ResourceHelper.getString("find.item"));
        statusLabel = new Label(textPanel, SWT.BORDER);
        GridData gl = new GridData();
        gl.widthHint = 410;
        statusLabel.setLayoutData(gl);
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

}

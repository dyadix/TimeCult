/*
 * Copyright (c) Rustam Vishnyakov, 2009-2010 (dyadix@gmail.com)
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
 * $Id: ProjectItemEditDialog.java,v 1.10 2010/04/02 16:03:53 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.ui.GenericUIManager;
import net.sf.timecult.util.Formatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A generic dialog for all types of project items: tasks, project, activities etc.
 */
public abstract class ProjectItemEditDialog extends SWTDialog {
    
    private Text nameText;
    private Text hyperlinkText;
    private Text idText;
    private Text createdText;
    private Text closedText;
    private Text deadlineText;
    private SWTMainWindow mainWindow;
    private ProjectTreeItem item;
    private ProjectTreeItem.ItemType itemType;
    private TimeTracker appInstance;
    
    
    /**
     * Constructor for the new item.
     * @param mainWindow    The main application window.
     * @param itemType      The item type as specified in <code>ProjectTreeItem</code>.
     */
    public ProjectItemEditDialog(SWTMainWindow mainWindow, ProjectTreeItem.ItemType itemType) {
        super(mainWindow.getShell(), true);
        this.mainWindow = mainWindow;
        this.item = null;
        this.itemType = itemType;
        this.appInstance = TimeTracker.getInstance();
    }
    
    
    /**
     * Constructor for the existing item to be edited.
     * @param mainWindow    The main application window.
     * @param item          The item to be edited (must not be null!)
     */
    public ProjectItemEditDialog(SWTMainWindow mainWindow, ProjectTreeItem item) {
        super(mainWindow.getShell(), true);
        this.mainWindow = mainWindow;
        this.item = item;
        this.itemType = item.getItemType();
        this.appInstance = TimeTracker.getInstance();
    }
    
    
    public String getName() {
        return this.nameText.getText();
    }

    public SWTMainWindow getMainWindow() {
        return this.mainWindow;
    }
    
    @Override
    protected Composite createContentPanel(Shell shell) {     
        Composite textPanel = new Composite(shell, SWT.BORDER);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        textPanel.setLayout(grid);
        //
        // ID field (non-editable)
        //
        Label idLabel = new Label(textPanel, SWT.None);
        idLabel.setText("ID:"); //TODO: LOCALIZE
        this.idText = createTextField(textPanel, "", getIdFieldLength());
        this.idText.setEnabled(false);
        //
        // Name field
        //
        Label nameLabel = new Label(textPanel, SWT.None);
        nameLabel.setText(ResourceHelper.getString("message.itemName") + ":");
        this.nameText = createTextField(textPanel, "", 500);
        this.nameText.setFocus();
        this.nameText.addKeyListener(getDefaultKeyListener());
        //
        // Hyperlink field
        //
        if (isHyperlinkAvailable()) {
            Label hyperlinkLabel = new Label(textPanel, SWT.None);
            hyperlinkLabel.setText(ResourceHelper.getString("dialog.hyperlink"));
            this.hyperlinkText = createTextField(textPanel, "", 500);
            this.hyperlinkText.addKeyListener(getDefaultKeyListener());
        }
        if (this.item != null) {
            // 
            // Creation date/time
            //
            if (isCreationDateAvailable()) {
                Label createdLabel = new Label(textPanel, SWT.None);
                createdLabel.setText(ResourceHelper.getString("status.created")
                    + ":");
                this.createdText = createTextField(textPanel, "", 180);
                this.createdText.setEditable(false);
                this.createdText.addKeyListener(getDefaultKeyListener());
            }
            // 
            // Close date/time
            //
            if (isCloseDateAvailable()) {
                Label closedLabel = new Label(textPanel, SWT.None);
                closedLabel
                    .setText(ResourceHelper.getString("status.closed") + ":");
                this.closedText = createTextField(textPanel, "", 180);
                this.closedText.setEditable(false);
                this.closedText.addKeyListener(getDefaultKeyListener());
            }
        }
        Label deadlineLabel = new Label(textPanel, SWT.None);
        deadlineLabel.setText("Deadline:"); //TODO: LOCALIZE!
        this.deadlineText = SWTUIManager.addDateField(this, textPanel);
        return textPanel;
    }
    
    
    @Override
    protected void initFields() {
        if (item != null) {
            this.nameText.setText(item.getName());
            if (item.getHyperlink() != null) {
                this.hyperlinkText.setText(item.getHyperlink());
            }
            this.idText.setText(getItemId());
            if (item.getCreationDateTime() != null && item.getCreationDateTime().getTime() != 0) {
                this.createdText.setText(Formatter.toDateTimeString(item.getCreationDateTime(),true));
            }
            if (item.getCloseDateTime() != null) {
                this.closedText.setText(Formatter.toDateTimeString(item.getCloseDateTime(),true));
            }
            if (item.getDeadline() != null) {
                deadlineText.setText(Formatter.toDateString(item.getDeadline()));
            }
        }
        else {
            String newId = TimeTracker.getInstance().getWorkspace().getIdGenerator().getNewId();
            this.idText.setText(newId);
        }
    }
    
    
    @Override
    protected final boolean handleOk() {
        String hyperlink = null;
        if (this.hyperlinkText != null) {
            hyperlink = this.hyperlinkText.getText();
            if (hyperlink != null && !hyperlink.isEmpty()) {
                String hluc = hyperlink.toUpperCase();
                if (!hluc.startsWith("HTTP:") && !hluc.startsWith("FTP:")) {
                    errorMessage(ResourceHelper.getString("message.invalidHyperlink"));                
                    return false;
                }
                try {
                    new URL(hyperlink);
                }
                catch (MalformedURLException e) {
                    errorMessage(ResourceHelper.getString("message.invalidURL"));
                    return false;
                }
            }
        }
        Date deadline;
        try {
            String deadlineStr = deadlineText.getText();
            if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
                deadline = null;
            } else {
                deadline = Formatter.parseDateString(deadlineText.getText());
            }
        } catch (ParseException e) {
            errorMessage("Invalid date format!"); //TODO: LOCALIZE
            return false;
        }
        if (item == null) {
            item = createItemAtSelection();
            setItemData(item, hyperlink, deadline);
        }
        else {            
            item.setName(this.nameText.getText());
            setItemData(item, hyperlink, deadline);
            boolean extendedResult = afterUpdate();
            appInstance.getUIManager().setCurrentSelection(item);
            return extendedResult;
            
        }
        return true;
    }


    private void setItemData(ProjectTreeItem item, String hyperlink, Date deadline) {
        item.setDeadline(deadline);
        item.setHyperlink(hyperlink);
    }
    
    
    protected boolean afterUpdate() {
        //
        // Do nothing by default
        //
        return true;
    }
    
    
    private ProjectTreeItem createItemAtSelection() {
        String id = this.idText.getText();
        String name = getName();
        GenericUIManager uiManager = appInstance.getUIManager();
        ProjectTreeItem treeItem = null;
        ProjectTreeItem selectedItem = this.appInstance.getWorkspace().getSelection();
        if (selectedItem != null && selectedItem instanceof Project) {
            treeItem = this.appInstance.getWorkspace().createItem((Project)selectedItem, this.itemType, id, name);
            if (treeItem != null) {
                TimeTracker.getInstance().getUIManager().updateProjectTree();
                treeItem.setCreationDateTime(Calendar.getInstance().getTime());
                appInstance.selectObject(treeItem);            
                uiManager.setCurrentSelection(treeItem);
            }
        }
        return treeItem;
    }
    
    
    protected boolean isHyperlinkAvailable() {
        return true;
    }
    
    protected boolean isCloseDateAvailable() {
        return true;
    }
    
    protected boolean isCreationDateAvailable() {
        return true;
    }
    
    protected String getItemId() {
        if (this.item != null) {
            return this.item.getId();
        }
        return null;
    }
    
    protected int getIdFieldLength() {
        return 40;
    }
}

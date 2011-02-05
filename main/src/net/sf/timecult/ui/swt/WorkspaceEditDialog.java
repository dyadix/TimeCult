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
 * $Id: WorkspaceEditDialog.java,v 1.4 2010/03/26 12:39:25 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.Workspace;

public class WorkspaceEditDialog extends SWTProjectEditDialog {
    
    private Workspace workspace;
    private Combo roundUpCombo;
    
    public WorkspaceEditDialog(SWTMainWindow mainWindow, Workspace selection) {
        super(mainWindow, selection);
        this.workspace = selection;
    }

    @Override
    protected boolean isHyperlinkAvailable() {
        return false;
    }
    
    @Override
    protected boolean isCloseDateAvailable() {
        return false;
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite textPanel = super.createContentPanel(shell);
        Label roundupLabel = new Label(textPanel, SWT.None);
        roundupLabel.setText(ResourceHelper.getString("dialog.adjustTime"));
        this.roundUpCombo = new Combo(textPanel, SWT.None);
        this.roundUpCombo.add("");        
        this.roundUpCombo.add("1");
        this.roundUpCombo.add("5");
        this.roundUpCombo.add("10");
        this.roundUpCombo.add("30");
        if (this.workspace != null) {
            int roundUpMin = this.workspace.getSettings().getRoundUpIntervalInMinutes();
            if (roundUpMin != 0) {
                this.roundUpCombo.setText(Integer.toString(roundUpMin));
            }
        }
        return textPanel;
    }
    
    @Override
    protected String getItemId() {
        return this.workspace.getUUIDString();
    }
    
    @Override
    protected int getIdFieldLength() {
        return 220;
    }

    @Override
    protected boolean afterUpdate() {
        if (this.workspace != null) {
            String roundUpStr = this.roundUpCombo.getText();
            if (roundUpStr != null) {
                try {
                    int roundUpMin = 0;
                    if (!roundUpStr.isEmpty()) {
                        roundUpMin = Integer.parseInt(roundUpStr);
                    }
                    this.workspace.getSettings().setRoundUpIntervalInMinutes(roundUpMin);
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    @Override
    protected String getTitle() {
        return ResourceHelper.getString("dialog.properties.workspace");
    }
    
    

}

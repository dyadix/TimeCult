/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (dyadix@gmail.com)
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
 * $Id: ReasonDialog.java,v 1.1 2007/12/07 21:28:53 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.WaitReason;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReasonDialog extends SWTDialog {
    
    private Text reasonText;
    private SWTProjectTreePopup popup;
    
    public ReasonDialog(SWTProjectTreePopup popup) {        
        super(popup.getShell(), false);
        this.popup = popup;
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite textPanel = new Composite(shell, SWT.None);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        textPanel.setLayout(grid);
        Label label = new Label(textPanel, SWT.None);
        label.setText(ResourceHelper.getString("message.reasonText") + ":");
        GridData projectNameLayout = new GridData(GridData.FILL_HORIZONTAL);
        projectNameLayout.widthHint = 500;
        this.reasonText = new Text(textPanel, SWT.LEFT | SWT.BORDER );
        this.reasonText.setLayoutData(projectNameLayout);
        this.reasonText.addKeyListener(getDefaultKeyListener());
        this.reasonText.setFocus();
        return textPanel;
    }

    @Override
    protected String getTitle() {
        return ResourceHelper.getString("dialog.waitReason");
    }

    @Override
    protected boolean handleOk() {
        WaitReason waitReason = WaitReason.newInstance(this.reasonText.getText());
        this.popup.setWaiting(waitReason);
        return true;
    }
    
}

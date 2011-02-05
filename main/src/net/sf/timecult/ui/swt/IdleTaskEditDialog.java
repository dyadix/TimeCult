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
 * $Id: IdleTaskEditDialog.java,v 1.2 2010/04/02 14:31:34 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.IdleTask;

public class IdleTaskEditDialog extends SWTTaskEditDialog {
    
    private Text noteText;
    private IdleTask idleTask;
    
    public IdleTaskEditDialog(SWTMainWindow parent) {
        super(parent);
    }

    public IdleTaskEditDialog(SWTMainWindow parent, IdleTask task) {
        super(parent, task);
        this.idleTask = task;
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
    protected boolean isCreationDateAvailable() {
        return false;
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite panel = super.createContentPanel(shell);
        Label noteLabel = new Label(panel, SWT.None);
        noteLabel.setText(ResourceHelper.getString("dialog.defaultNote") + ":");
        this.noteText = createTextField(panel, "", 500);
        this.noteText.addKeyListener(getDefaultKeyListener());
        if (this.idleTask != null && this.idleTask.getDefaultNote() != null) {
            this.noteText.setText(this.idleTask.getDefaultNote());
        }
        return panel;
    }
    
    @Override
    protected boolean afterUpdate() {
        if (this.idleTask != null) {
            this.idleTask.setDefaultNote(noteText.getText());
        }
        return true;
    }
    
        
}

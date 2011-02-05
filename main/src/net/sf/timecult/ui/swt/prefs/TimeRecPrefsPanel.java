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
 * $Id: TimeRecPrefsPanel.java,v 1.2 2010/06/16 16:48:43 dyadix Exp $
 */
package net.sf.timecult.ui.swt.prefs;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

public class TimeRecPrefsPanel {
    private PreferencesDialog prefsDialog;
    private Composite contentArea;
    private Button showRecEditDialogBox;
    private Button dontSaveEmptyTimeRecBox;
    
    public TimeRecPrefsPanel(PreferencesDialog prefsDialog) {
        this.prefsDialog = prefsDialog;
        setup();
    }
    
    private void setup() {
        TabItem generalPrefsTab = new TabItem(this.prefsDialog.getTabFolder(), SWT.BORDER);
        generalPrefsTab.setText(ResourceHelper.getString("dialog.options.timeRec"));
        this.contentArea = new Composite(this.prefsDialog.getTabFolder(), SWT.NONE);
        GridLayout areaLayout = new GridLayout();
        areaLayout.numColumns = 2;        
        this.contentArea.setLayout(areaLayout);
        generalPrefsTab.setControl(this.contentArea);
        
        addShowRecEditDialog();
        addDontSaveEmptyTimeRec();
    }
    
    
    private void addShowRecEditDialog() {        
        this.showRecEditDialogBox = new Button(this.contentArea, SWT.CHECK);
        this.showRecEditDialogBox.setSelection(prefsDialog.getAppPreferences().isShowRecEditDialog());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.showRecEditDialog"));
    }
    
    private void addDontSaveEmptyTimeRec() {        
        this.dontSaveEmptyTimeRecBox = new Button(this.contentArea, SWT.CHECK);
        this.dontSaveEmptyTimeRecBox.setSelection(prefsDialog.getAppPreferences().isDontSaveEmptyTimeRec());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.dontSaveEmptyTimeRec"));
    }
    
    public boolean apply() {
        prefsDialog.getAppPreferences().setShowRecEditDialog(this.showRecEditDialogBox.getSelection());
        prefsDialog.getAppPreferences().setDontSaveEmptyTimeRec(this.dontSaveEmptyTimeRecBox.getSelection());
        return true;
    }
}

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
 * $Id: PreferencesDialog.java,v 1.5 2010/06/04 15:02:09 dyadix Exp $
 */
package net.sf.timecult.ui.swt.prefs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.ui.swt.SWTDialog;

public class PreferencesDialog extends SWTDialog {
    
    private TabFolder tabs;
    private GeneralPrefsPanel generalPrefs;
    private TimeRecPrefsPanel timeRecPrefs;
    private NotificationPrefsPanel notificationPrefsPanel;

    public PreferencesDialog(Shell parent) {
        super(parent, false);
    }


    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite mainPanel = new Composite(shell, SWT.None);
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        mainPanel.setLayout(grid);
        //
        // Tabs
        //
        SashForm tabsForm = new SashForm(shell, SWT.NONE);
        tabsForm.setOrientation(SWT.HORIZONTAL);
        GridData tabsData = new GridData(GridData.FILL_HORIZONTAL
            | GridData.FILL_VERTICAL);
        tabsForm.setLayoutData(tabsData);
        this.tabs = new TabFolder(tabsForm, SWT.NONE);
        //
        // General prefs
        //
        this.generalPrefs = new GeneralPrefsPanel(this);
        this.timeRecPrefs = new TimeRecPrefsPanel(this);
        this.notificationPrefsPanel = new NotificationPrefsPanel(this);
        
        return mainPanel;
    }


    @Override
    protected String getTitle() {
        return ResourceHelper.getString("dialog.options");
    }


    @Override
    protected boolean handleOk() {
        boolean success = true;
        if (success) {
            this.generalPrefs.apply();
            this.timeRecPrefs.apply();
            this.notificationPrefsPanel.apply();
        }
        return success;
    }
    
    
    public TabFolder getTabFolder() {
        return this.tabs;
    }
    
    public AppPreferences getAppPreferences() {
        return AppPreferences.getInstance();
    }

}

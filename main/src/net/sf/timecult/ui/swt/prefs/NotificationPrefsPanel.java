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
 * $Id: NotificationPrefsPanel.java,v 1.2 2010/06/16 16:03:46 dyadix Exp $
 */package net.sf.timecult.ui.swt.prefs;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

public class NotificationPrefsPanel {
    private PreferencesDialog prefsDialog;
    private Composite contentArea;
    private Button runningTimerNotificationButton;
    private Button idleTimeNotificationButton;
    
    public NotificationPrefsPanel(PreferencesDialog prefsDialog) {
        this.prefsDialog = prefsDialog;
        setup();
    }
    
    private void setup() {
        TabItem notificationPrefsTab = new TabItem(this.prefsDialog.getTabFolder(), SWT.BORDER);
        notificationPrefsTab.setText(ResourceHelper.getString("settings.notifications"));
        this.contentArea = new Composite(this.prefsDialog.getTabFolder(), SWT.NONE);
        GridLayout areaLayout = new GridLayout();
        areaLayout.numColumns = 2;        
        this.contentArea.setLayout(areaLayout);
        notificationPrefsTab.setControl(this.contentArea);
        
        addRunningTimerNotificationSetting();
        addIdleTimeNotificationSetting();
    }
    
    
    private void addRunningTimerNotificationSetting() {        
        this.runningTimerNotificationButton = new Button(this.contentArea, SWT.CHECK);
        this.runningTimerNotificationButton.setSelection(prefsDialog.getAppPreferences().isRunningTimerNotification());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("settings.notifications.runningTimers"));
    }
    
    private void addIdleTimeNotificationSetting() {        
        this.idleTimeNotificationButton = new Button(this.contentArea, SWT.CHECK);
        this.idleTimeNotificationButton.setSelection(prefsDialog.getAppPreferences().isIdleTimeNotification());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("settings.notifications.idleTime"));
    }
    
    
    public boolean apply() {
        prefsDialog.getAppPreferences().setRunningTimerNotification(this.runningTimerNotificationButton.getSelection());
        prefsDialog.getAppPreferences().setIdleTimeNotification(this.idleTimeNotificationButton.getSelection());
        return true;
    }
}

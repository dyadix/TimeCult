/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (rvishnyakov@gmail.com)
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
 * $Id: SWTMainTabFolder.java,v 1.6 2008/09/13 06:35:08 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.conf.AppPreferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Main tab folder
 * @author rvishnyakov
 *
 */
public class SWTMainTabFolder {

	public SWTMainTabFolder(SWTMainWindow mainWindow) {
		_mainWindow = mainWindow;
        this.appPrefs = AppPreferences.getInstance();
		setup();
	}
	
	
	private void setup() {
		tabs = new TabFolder(_mainWindow.getMainTabFolderSash(), SWT.NONE);
        this.tabs.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                TabItem[] selected = tabs.getSelection();
                if (selected != null && selected.length > 0) {
                    int index = getTabIndex(selected[0].getText());
                    appPrefs.setSelectedTab(index);
                }
            }});
	}
	
	public TabFolder getTabs() {
		return tabs;
	}
	
    
    public void selectTab(int index) {
        if (index >= 0 && index < this.tabs.getItemCount()) {
            this.tabs.setSelection(index);
        }
    }
    
    
    private int getTabIndex(String tabText) {
        if (tabText == null)
            return -1;
        for (int i = 0; i < this.tabs.getItemCount(); i++) {
            if (tabText.equals(this.tabs.getItem(i).getText())) {
                return i;
            }
        }
        return -1;
    }
	
	private TabFolder tabs;
	private SWTMainWindow _mainWindow = null;
    private AppPreferences appPrefs;
}

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
 * $Id: AppPreferences.java,v 1.12 2011/01/16 05:22:53 dragulceo Exp $
 */
package net.sf.timecult.conf;

import net.sf.timecult.PlatformUtil;

import java.util.Vector;

/**
 * Encapsulates the application preferences.
 */
public class AppPreferences {
    
    public static final String HIDE_WHEN_MINIMIZED = "hideWhenMinimized";
    public static final String KEEP_TIMER_POS = "keepTimerPos";
    public static final String AUTO_OPEN_RECENT_FILE = "autoOpenRecentFile";
    public static final String HIDE_CLOSED = "hideClosed";
    public static final String AUTOSAVE = "autosave";
    public static final String SHOW_REC_EDIT_DIALOG = "showRecEditDialog";
    public static final String DONT_SAVE_EMPTY_TIME_REC = "dontSaveEmptyTimeRec";
    
    public static final String RUNNING_TIMER_NOTIFICATION = "runningTimerNotification";
    public static final String IDLE_TIME_NOTIFICATION = "idleTimeNotification";
    
    private final static int[] DEFAULT_TIME_LOG_COL_WIDTH = {250, 250, 100, 80, 100, 200 };
    private final static int[] DEFAULT_TREE_TAB_SASH_WEIGHTS = {1,1}; // Equal size
    private final static int[] DEFAULT_TOTALS_COL_WIDTH = { 0, 300, 50, 50, 50, 50 };

    private boolean hideWhenMinimized;
    private boolean keepTimerPos;
    private boolean autoOpenRecentFile;
    private static AppPreferences instance;
    private int timeLogColWidth[];
    private int treeTabSashWeights[];
    private int selectedTab;
    private int totalsColWidths[];
    private boolean hideClosed;
    private boolean autoSave;
    private boolean showRecEditDialog;
    private boolean dontSaveEmptyTimeRec;
    
    private boolean runningTimerNotification;
    private boolean idleTimeNotification;

    public boolean isIdleTimeNotification() {
        return idleTimeNotification;
    }

    public void setIdleTimeNotification(boolean idleTimeNotification) {
        this.idleTimeNotification = idleTimeNotification;
    }

    private Vector<AppPreferencesListener> listeners;
    
    private AppPreferences() {
        listeners = new Vector<AppPreferencesListener>();
        this.hideWhenMinimized = false;
        this.keepTimerPos = false;
        this.timeLogColWidth = new int[DEFAULT_TIME_LOG_COL_WIDTH.length];
        for (int i = 0; i < this.timeLogColWidth.length; i++) {
            this.timeLogColWidth[i] = DEFAULT_TIME_LOG_COL_WIDTH[i];
        }
        this.treeTabSashWeights = DEFAULT_TREE_TAB_SASH_WEIGHTS;
        this.selectedTab = 0;
        this.totalsColWidths = new int[DEFAULT_TOTALS_COL_WIDTH.length];
        for (int i = 0; i < this.totalsColWidths.length; i++) {
            this.totalsColWidths[i] = DEFAULT_TOTALS_COL_WIDTH[i];
        }
        this.hideClosed = false;
    }
    
    public void setHideWhenMinimized(boolean hide) {
        this.hideWhenMinimized = hide;
    }
    
    public boolean isHideWhenMinimized() {
        if (PlatformUtil.isOSLinux()) {
            return false; // Doesn't work reliably on Linux
        }
        return this.hideWhenMinimized;
    }
    
    
    public boolean isShowRecEditDialog() {
        return showRecEditDialog;
    }

    public void setShowRecEditDialog(boolean showRecEditDialog) {
        this.showRecEditDialog = showRecEditDialog;
    }

    public void setKeepTimerPos(boolean keepTimerPos) {
        this.keepTimerPos = keepTimerPos;        
    }
    
    
    public boolean isKeepTimerPos() {
        return this.keepTimerPos;
    }
        
    public void setAutoOpenRecentFile(boolean autoOpen) {
        this.autoOpenRecentFile = autoOpen;
    }
    
    public boolean isAutoOpenRecentFile() {
        return this.autoOpenRecentFile;
    }
    
    
    public int getTimeLogColWidth(int col) {
        return this.timeLogColWidth[col];
    }
    
    
    public int[] getTimeLogColWidths() {
        return this.timeLogColWidth;
    }
    
    
    public void setTimeLogColWidth(int col, int width) {
        this.timeLogColWidth[col] = width;
    }
    
    
    public int getTotalsColWidth(int col) {
        return this.totalsColWidths[col];
    }
    
    
    public int[] getTotalsColWidths() {
        return this.totalsColWidths;
    }
    
    
    public void setTotalsColWidth(int col, int width) {
        if (col < this.totalsColWidths.length) this.totalsColWidths[col] = width;
    }
    
    
    public void setTreeTabSashWeights(int[] weights) {
        this.treeTabSashWeights = weights;
    }
    
    public int[] getTreeTabSashWeights() {
        return this.treeTabSashWeights;
    }
    
    
    public void setSelectedTab(int index) {
        this.selectedTab = index;
    }
    
    
    public int getSelectedTab() {
        return this.selectedTab;
    }
    
    
    public void setHideClosed(boolean hideClosed) {
        this.hideClosed = hideClosed;
        notifyListeners(HIDE_CLOSED);
    }
    
    
    public boolean isHideClosed() {
        return this.hideClosed;
    }
    
    
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        notifyListeners(AUTOSAVE);
    }
    
    public boolean isAutoSave() {
        return this.autoSave;
    }
    
    
    public static synchronized AppPreferences getInstance() {
        if(instance == null) {
            instance = new AppPreferences();
        }
        return instance;
    }
    
    public void addListener(AppPreferencesListener l) {
        listeners.add(l);
    }
    
    public void notifyListeners(String prefName) {
        for(AppPreferencesListener l : listeners) {
            l.preferenceChanged(prefName);
        }
    }

    public void setDontSaveEmptyTimeRec(boolean dontSaveEmptyTimeRec) {
        this.dontSaveEmptyTimeRec = dontSaveEmptyTimeRec;
    }

    public boolean isDontSaveEmptyTimeRec() {
        return dontSaveEmptyTimeRec;
    }

    public boolean isRunningTimerNotification() {
        return runningTimerNotification;
    }

    public void setRunningTimerNotification(boolean runningTimerNotification) {
        this.runningTimerNotification = runningTimerNotification;
    }
}

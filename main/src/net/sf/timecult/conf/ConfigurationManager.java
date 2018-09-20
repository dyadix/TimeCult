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
 * $Id: ConfigurationManager.java,v 1.15 2010/06/16 16:03:46 dyadix Exp $
 */
package net.sf.timecult.conf;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;
import java.util.Properties;

import net.sf.timecult.TimeTracker;

/**
 * Provides simple services to save/and retrieve user preferences.
 * Uses "Application Data" directory on Windows and 
 * user home directory on other (non-Windows) systems
 * @author rvishnyakov
 */
public class ConfigurationManager {
    
    public static final String TIMER_POS_X    = "timer.x";
    public static final String TIMER_POS_Y    = "timer.y";
    public static final String APP_WIN_TOP    = "window.y";
    public static final String APP_WIN_LEFT   = "window.x";
    public static final String APP_WIN_WIDTH  = "window.width";
    public static final String APP_WIN_HEIGHT = "window.height";
    public static final String LOOK_AND_FEEL  = "lookAndFeel";
    public static final String CONF_FILE_NAME = "timecult.xml";
    public static final String TIME_LOG_COL_WIDTHS = "timeLogColWidths";
    public static final String TREE_TAB_SASH_WEIGHTS = "treeTabSashWeights";
    public static final String TOTALS_COL_WIDTHS = "totalsColWidths";
    public static final String SELECTED_TAB = "selectedTab";

    public ConfigurationManager(TimeTracker timeTracker) {
		_timeTracker = timeTracker;
		File confDir = new File(getConfigDir());
		confDir.mkdirs();
		_confFile = new File(confDir.getAbsolutePath()
				+ System.getProperty("file.separator") + CONF_FILE_NAME);
        this.props = new Properties();
	}

    public void load() throws FileNotFoundException, IOException {
        if (_confFile.exists()) {
            this.props.loadFromXML(new FileInputStream(_confFile));
            int i = 0;
            String fileStr = null;
            while ((fileStr = props.getProperty("files." + i)) != null) {
                File file = new File(fileStr);
                if (file.exists()) {
                    _timeTracker.addRecentlyOpenFile(file);
                }
                i ++;
            }
            int top = Integer.parseInt(props.getProperty(APP_WIN_TOP));
            int left = Integer.parseInt(props.getProperty(APP_WIN_LEFT));
            int width = Integer.parseInt(props.getProperty(APP_WIN_WIDTH));
            int height = Integer.parseInt(props.getProperty(APP_WIN_HEIGHT));
            _timeTracker.getUIManager().setBounds(left, top, width, height);
            String lfClassName = props.getProperty(LOOK_AND_FEEL);
            if (lfClassName != null) {
                _timeTracker.getUIManager().setLookAndFeel(lfClassName);
            }
            readDefaultTimerPos(props);
            readTimeLogColWidths();
            readTotalsColWidths();
            readTreeTabSashWeights();            
            readSelectedTab();
            readAppPreferences(props);            
        }
    }

    public void save() throws IOException {
        this.props.clear();
        //
        // Recently open files
        //
        File[] recentlyOpenFiles = _timeTracker.getRecentlyOpenFiles();
        for (int i = 0; i < recentlyOpenFiles.length; i++) {
            this.props.setProperty("files." + i, recentlyOpenFiles[i].getAbsolutePath());
        }
        //
        // Window coordinates
        //
        Rectangle winBounds = _timeTracker.getUIManager().getBounds();
        this.props.setProperty(APP_WIN_TOP, Integer.toString(winBounds.y));
        this.props.setProperty(APP_WIN_LEFT, Integer.toString(winBounds.x));
        this.props.setProperty(APP_WIN_HEIGHT, Integer.toString(winBounds.height));
        this.props.setProperty(APP_WIN_WIDTH, Integer.toString(winBounds.width));
        this.props.setProperty(LOOK_AND_FEEL, _timeTracker.getUIManager()
            .getLookAndFeel());
        saveDefaultTimerPos(props);
        saveAppPreferences(props);
        saveTimeLogColWidths();
        saveTotalsColWidths();
        saveTreeTabSashWeights();
        saveSelectedTab();
        
        this.props.storeToXML(new FileOutputStream(_confFile), "TimeCult Configuration");
    }
    
    private String getConfigDir() {
    	String separator = System.getProperty("file.separator");
    	StringBuffer confDir = new StringBuffer();
    	String osName = System.getProperty("os.name");
    	if (osName.startsWith("Windows")) {
    		confDir.append(System.getenv("APPDATA"));
    		confDir.append(separator);
    		confDir.append("TimeCult");
    	}    	
    	else { // On UNIX-based systems
    		confDir.append(System.getProperty("user.home"));
    		confDir.append(separator);
    		confDir.append(".timecult");
    	}
    	return confDir.toString();
    }

    public String getLogDir() {
        String logDirPath = getConfigDir() + File.separator + "logs";
        File logDir = new File(logDirPath);
        //noinspection ResultOfMethodCallIgnored
        logDir.mkdirs();
        return logDirPath;
    }
    
    public void setDefaultTimerPos(Point pos) {
        this.timerPos = pos;
    }
    
    
    public void setDefaultTimerPos(int x, int y) {
        this.timerPos = new Point(x, y);
    }
    
    public Point getDefaultTimerPos() {
        return this.timerPos;
    }
    
    
    private void readDefaultTimerPos(Properties props) {
        String posXStr = props.getProperty(TIMER_POS_X);
        String posYStr = props.getProperty(TIMER_POS_Y);
        if (posXStr != null && posYStr != null) {
            int posX = Integer.parseInt(posXStr);
            int posY = Integer.parseInt(posYStr);
            timerPos = new  Point(posX, posY);
            AppPreferences.getInstance().setKeepTimerPos(true);
        }
    }
    
    
    private void saveDefaultTimerPos(Properties props) throws IOException {
        if (timerPos != null && AppPreferences.getInstance().isKeepTimerPos()) {
            this.props.setProperty(TIMER_POS_X, Integer.toString(timerPos.x));
            this.props.setProperty(TIMER_POS_Y, Integer.toString(timerPos.y));
        }
        else {
            this.props.remove(TIMER_POS_X);
            this.props.remove(TIMER_POS_Y);
        }
    }
    
    
    private void saveTimeLogColWidths() {
        StringBuffer buf = new StringBuffer();
        int colWidths[] = AppPreferences.getInstance().getTimeLogColWidths();
        for (int i = 0; i < colWidths.length; i++) {
            buf.append(colWidths[i]);
            if (i < colWidths.length - 1) {
                buf.append(',');
            }
        }
        this.props.setProperty(TIME_LOG_COL_WIDTHS, buf.toString());
    }
    
    
    private void readTimeLogColWidths() {
        String widthsStr = this.props.getProperty(TIME_LOG_COL_WIDTHS);
        AppPreferences appPrefs = AppPreferences.getInstance();
        if (widthsStr != null) {
            String colWidths[] = widthsStr.split(",");
            for (int i = 0; i < colWidths.length; i ++) {
                appPrefs.setTimeLogColWidth(i, Integer.parseInt(colWidths[i]));
            }
        }
    }
    
    
    private void saveTotalsColWidths() {
        StringBuffer buf = new StringBuffer();
        int colWidths[] = AppPreferences.getInstance().getTotalsColWidths();
        for (int i = 0; i < colWidths.length; i++) {
            buf.append(colWidths[i]);
            if (i < colWidths.length - 1) {
                buf.append(',');
            }
        }
        this.props.setProperty(TOTALS_COL_WIDTHS, buf.toString());
    }
    
    
    private void readTotalsColWidths() {
        String widthsStr = this.props.getProperty(TOTALS_COL_WIDTHS);
        AppPreferences appPrefs = AppPreferences.getInstance();
        if (widthsStr != null) {
            String colWidths[] = widthsStr.split(",");
            for (int i = 0; i < colWidths.length; i++) {
                appPrefs.setTotalsColWidth(i, Integer.parseInt(colWidths[i]));
            }
        }
    }
    
    
    private void saveTreeTabSashWeights() {
        StringBuffer buf = new StringBuffer();
        int weights[] = AppPreferences.getInstance().getTreeTabSashWeights();
        for (int i = 0; i < weights.length; i++) {
            buf.append(weights[i]);
            if (i < weights.length - 1) {
                buf.append(',');
            }
        }
        this.props.setProperty(TREE_TAB_SASH_WEIGHTS, buf.toString());
    }
    
    
    private void readTreeTabSashWeights() {
        String weightsStr = this.props.getProperty(TREE_TAB_SASH_WEIGHTS);
        int weights[] = null;
        if (weightsStr != null) {
            String weightStr[] = weightsStr.split(",");
            weights = new int[AppPreferences.getInstance().getTreeTabSashWeights().length];
            for (int i = 0; i < weights.length; i ++) {
                weights[i] = Integer.parseInt(weightStr[i]);
            }
            AppPreferences.getInstance().setTreeTabSashWeights(weights);
        }
    }
    
    
    private void readSelectedTab() {
        String selectedTabStr = this.props.getProperty(SELECTED_TAB);
        if (selectedTabStr != null) {
            AppPreferences.getInstance().setSelectedTab(Integer.parseInt(selectedTabStr));
        }
    }
    
    
    private void saveSelectedTab() {
        this.props.setProperty(SELECTED_TAB, Integer.toString(AppPreferences
            .getInstance().getSelectedTab()));
    }
    
    
    private void saveAppPreferences(Properties props) {
        AppPreferences appPrefs = AppPreferences.getInstance();
        props.setProperty(AppPreferences.HIDE_WHEN_MINIMIZED, Boolean
            .toString(appPrefs.isHideWhenMinimized()));
        props.setProperty(AppPreferences.AUTO_OPEN_RECENT_FILE, Boolean
            .toString(appPrefs.isAutoOpenRecentFile()));
        props.setProperty(AppPreferences.HIDE_CLOSED, Boolean.toString(appPrefs.isHideClosed()));
        props.setProperty(AppPreferences.AUTOSAVE, Boolean.toString(appPrefs.isAutoSave()));
        props.setProperty(AppPreferences.SHOW_REC_EDIT_DIALOG, Boolean.toString(appPrefs.isShowRecEditDialog()));
        props.setProperty(AppPreferences.DONT_SAVE_EMPTY_TIME_REC, Boolean.toString(appPrefs.isDontSaveEmptyTimeRec()));
        props.setProperty(AppPreferences.RUNNING_TIMER_NOTIFICATION, Boolean.toString(appPrefs.isRunningTimerNotification()));
        props.setProperty(AppPreferences.IDLE_TIME_NOTIFICATION, Boolean.toString(appPrefs.isIdleTimeNotification()));
    }


    private void readAppPreferences(Properties props) {
        AppPreferences appPrefs = AppPreferences.getInstance();
        if (props.getProperty(AppPreferences.HIDE_WHEN_MINIMIZED) != null) {
            appPrefs.setHideWhenMinimized(Boolean.parseBoolean(props
                .getProperty(AppPreferences.HIDE_WHEN_MINIMIZED)));
        }
        if (props.getProperty(AppPreferences.AUTO_OPEN_RECENT_FILE) != null) {
            appPrefs.setAutoOpenRecentFile(Boolean.parseBoolean(props
                .getProperty(AppPreferences.AUTO_OPEN_RECENT_FILE)));
        }
        if (props.getProperty(AppPreferences.HIDE_CLOSED) != null) {
            appPrefs.setHideClosed(Boolean.parseBoolean(props
                .getProperty(AppPreferences.HIDE_CLOSED)));
        }
        if (props.getProperty(AppPreferences.AUTOSAVE) != null) {
            appPrefs.setAutoSave(Boolean.parseBoolean(props
                .getProperty(AppPreferences.AUTOSAVE)));
        }
        if (props.getProperty(AppPreferences.SHOW_REC_EDIT_DIALOG) != null) {
            appPrefs.setShowRecEditDialog(Boolean.parseBoolean(props
                .getProperty(AppPreferences.SHOW_REC_EDIT_DIALOG)));
        }
        if (props.getProperty(AppPreferences.DONT_SAVE_EMPTY_TIME_REC) != null) {
            appPrefs.setDontSaveEmptyTimeRec(Boolean.parseBoolean(props
                .getProperty(AppPreferences.DONT_SAVE_EMPTY_TIME_REC)));
        }
        if (props.getProperty(AppPreferences.RUNNING_TIMER_NOTIFICATION) != null) {
            appPrefs.setRunningTimerNotification(Boolean.parseBoolean(props
                .getProperty(AppPreferences.RUNNING_TIMER_NOTIFICATION)));
        }
        if (props.getProperty(AppPreferences.IDLE_TIME_NOTIFICATION) != null) {
            appPrefs.setIdleTimeNotification(Boolean.parseBoolean(props
                .getProperty(AppPreferences.IDLE_TIME_NOTIFICATION)));
        }
    }


    private File _confFile = null;
    private TimeTracker _timeTracker = null;
    private Point timerPos;
    private Properties props;

}
